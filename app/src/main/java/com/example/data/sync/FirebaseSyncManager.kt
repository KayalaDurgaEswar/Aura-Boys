package com.example.data.sync

import android.util.Log
import com.example.data.dao.GaneshDao
import com.example.data.entity.AdminRequest
import com.example.data.entity.AppSetting
import com.example.data.entity.DonorCandidate
import com.example.data.entity.ExpenseItem
import com.example.data.entity.FestivalEvent
import com.example.data.entity.SamagriItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class CloudSyncStatus {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE_ERROR
}

data class CloudSyncState(
    val status: CloudSyncStatus = CloudSyncStatus.IDLE,
    val lastSyncTime: Long = 0L,
    val lastSyncedBy: String = "",
    val message: String = "Ready to sync"
)

object FirebaseSyncManager {
    private const val TAG = "FirebaseSync"
    private const val FIREBASE_DATABASE_URL = "https://aura-boys-default-rtdb.firebaseio.com/aura_utsav.json"
    private const val FIREBASE_ROOT_URL = "https://aura-boys-default-rtdb.firebaseio.com/.json"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .build()

    private val _syncState = MutableStateFlow(CloudSyncState())
    val syncState: StateFlow<CloudSyncState> = _syncState.asStateFlow()

    /**
     * Pulls the latest cloud snapshot from Firebase Realtime DB and replaces local Room database.
     * If cloud is completely empty or null, seeds cloud with local Room data.
     */
    suspend fun syncWithCloud(dao: GaneshDao, currentAdminName: String = "App User"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                _syncState.value = _syncState.value.copy(
                    status = CloudSyncStatus.SYNCING,
                    message = "Connecting to Aura Boys Cloud..."
                )

                // First try fetching from child node aura_utsav.json
                var request = Request.Builder()
                    .url(FIREBASE_DATABASE_URL)
                    .get()
                    .build()

                var response = httpClient.newCall(request).execute()
                var responseBody = response.body?.string()

                var targetJson: JSONObject? = null

                if (response.isSuccessful && responseBody != null && responseBody.trim() != "null" && responseBody.trim() != "{}") {
                    try {
                        targetJson = JSONObject(responseBody)
                    } catch (e: Exception) {
                        Log.w(TAG, "Parsing aura_utsav node failed: ${e.message}")
                    }
                }

                // If not found in aura_utsav.json, check root .json
                if (targetJson == null || targetJson.length() == 0) {
                    val rootRequest = Request.Builder()
                        .url(FIREBASE_ROOT_URL)
                        .get()
                        .build()
                    val rootResponse = httpClient.newCall(rootRequest).execute()
                    val rootBody = rootResponse.body?.string()

                    if (rootResponse.isSuccessful && rootBody != null && rootBody.trim() != "null" && rootBody.trim() != "{}") {
                        try {
                            val rootObj = JSONObject(rootBody)
                            if (rootObj.has("aura_utsav")) {
                                targetJson = rootObj.optJSONObject("aura_utsav")
                            } else if (rootObj.has("donors") || rootObj.has("expenses") || rootObj.has("samagri") || rootObj.has("events")) {
                                targetJson = rootObj
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Parsing root .json failed: ${e.message}")
                        }
                    }
                }

                if (targetJson == null || targetJson.length() == 0) {
                    Log.d(TAG, "Cloud DB empty or uninitialized. Seeding cloud with local data.")
                    val pushSuccess = pushToCloudInternal(dao, currentAdminName)
                    if (pushSuccess) {
                        _syncState.value = CloudSyncState(
                            status = CloudSyncStatus.SYNCED,
                            lastSyncTime = System.currentTimeMillis(),
                            lastSyncedBy = currentAdminName,
                            message = "Cloud DB initialized & synced ✅"
                        )
                        return@withContext true
                    } else {
                        _syncState.value = CloudSyncState(
                            status = CloudSyncStatus.OFFLINE_ERROR,
                            message = "Offline or Cloud timeout"
                        )
                        return@withContext false
                    }
                }

                // Cloud data exists, parse JSON snapshot and update Room
                val cloudTimestamp = targetJson.optLong("lastUpdated", 0L)
                val cloudUpdatedBy = targetJson.optString("updatedBy", "Admin")

                // Parse Donors (handles Array or Object map)
                val donorsList = mutableListOf<DonorCandidate>()
                val donorsArray = targetJson.optJSONArray("donors")
                if (donorsArray != null) {
                    for (i in 0 until donorsArray.length()) {
                        val d = donorsArray.optJSONObject(i) ?: continue
                        donorsList.add(parseDonorObject(d, i + 1L))
                    }
                } else {
                    val donorsObj = targetJson.optJSONObject("donors")
                    if (donorsObj != null) {
                        val keys = donorsObj.keys()
                        var idx = 1L
                        while (keys.hasNext()) {
                            val k = keys.next()
                            val d = donorsObj.optJSONObject(k) ?: continue
                            donorsList.add(parseDonorObject(d, idx++))
                        }
                    }
                }

                // Parse Expenses (handles Array or Object map)
                val expensesList = mutableListOf<ExpenseItem>()
                val expensesArray = targetJson.optJSONArray("expenses")
                if (expensesArray != null) {
                    for (i in 0 until expensesArray.length()) {
                        val e = expensesArray.optJSONObject(i) ?: continue
                        expensesList.add(parseExpenseObject(e, i + 1L))
                    }
                } else {
                    val expensesObj = targetJson.optJSONObject("expenses")
                    if (expensesObj != null) {
                        val keys = expensesObj.keys()
                        var idx = 1L
                        while (keys.hasNext()) {
                            val k = keys.next()
                            val e = expensesObj.optJSONObject(k) ?: continue
                            expensesList.add(parseExpenseObject(e, idx++))
                        }
                    }
                }

                // Parse Events (handles Array or Object map)
                val eventsList = mutableListOf<FestivalEvent>()
                val eventsArray = targetJson.optJSONArray("events")
                if (eventsArray != null) {
                    for (i in 0 until eventsArray.length()) {
                        val ev = eventsArray.optJSONObject(i) ?: continue
                        eventsList.add(parseEventObject(ev, i + 1L))
                    }
                } else {
                    val eventsObj = targetJson.optJSONObject("events")
                    if (eventsObj != null) {
                        val keys = eventsObj.keys()
                        var idx = 1L
                        while (keys.hasNext()) {
                            val k = keys.next()
                            val ev = eventsObj.optJSONObject(k) ?: continue
                            eventsList.add(parseEventObject(ev, idx++))
                        }
                    }
                }

                // Parse Samagri (handles Array or Object map)
                val samagriList = mutableListOf<SamagriItem>()
                val samagriArray = targetJson.optJSONArray("samagri")
                if (samagriArray != null) {
                    for (i in 0 until samagriArray.length()) {
                        val s = samagriArray.optJSONObject(i) ?: continue
                        samagriList.add(parseSamagriObject(s, i + 1L))
                    }
                } else {
                    val samagriObj = targetJson.optJSONObject("samagri")
                    if (samagriObj != null) {
                        val keys = samagriObj.keys()
                        var idx = 1L
                        while (keys.hasNext()) {
                            val k = keys.next()
                            val s = samagriObj.optJSONObject(k) ?: continue
                            samagriList.add(parseSamagriObject(s, idx++))
                        }
                    }
                }

                // Parse Admin Requests (handles Array or Object map)
                val adminRequestsList = mutableListOf<AdminRequest>()
                val adminRequestsArray = targetJson.optJSONArray("adminRequests")
                if (adminRequestsArray != null) {
                    for (i in 0 until adminRequestsArray.length()) {
                        val a = adminRequestsArray.optJSONObject(i) ?: continue
                        adminRequestsList.add(parseAdminRequestObject(a, i + 1L))
                    }
                } else {
                    val adminRequestsObj = targetJson.optJSONObject("adminRequests")
                    if (adminRequestsObj != null) {
                        val keys = adminRequestsObj.keys()
                        var idx = 1L
                        while (keys.hasNext()) {
                            val k = keys.next()
                            val a = adminRequestsObj.optJSONObject(k) ?: continue
                            adminRequestsList.add(parseAdminRequestObject(a, idx++))
                        }
                    }
                }

                // Parse Settings (handles Array or Object map)
                val settingsList = mutableListOf<AppSetting>()
                val settingsArray = targetJson.optJSONArray("settings")
                if (settingsArray != null) {
                    for (i in 0 until settingsArray.length()) {
                        val st = settingsArray.optJSONObject(i) ?: continue
                        val key = st.optString("key", "")
                        val value = st.optString("value", "")
                        if (key.isNotBlank()) {
                            settingsList.add(AppSetting(key, value))
                        }
                    }
                } else {
                    val settingsObj = targetJson.optJSONObject("settings")
                    if (settingsObj != null) {
                        val keys = settingsObj.keys()
                        while (keys.hasNext()) {
                            val k = keys.next()
                            val v = settingsObj.opt(k)?.toString() ?: ""
                            if (k.isNotBlank()) {
                                settingsList.add(AppSetting(k, v))
                            }
                        }
                    }
                }

                // Replace local Room database with fresh cloud snapshot
                dao.replaceAllWithCloudSnapshot(
                    donors = donorsList,
                    expenses = expensesList,
                    events = eventsList,
                    samagri = samagriList,
                    adminRequests = adminRequestsList,
                    settings = settingsList
                )

                _syncState.value = CloudSyncState(
                    status = CloudSyncStatus.SYNCED,
                    lastSyncTime = if (cloudTimestamp > 0) cloudTimestamp else System.currentTimeMillis(),
                    lastSyncedBy = cloudUpdatedBy,
                    message = "Synced live DB (${donorsList.size} collection, ${expensesList.size} expenses) ☁️"
                )
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing with cloud", e)
                _syncState.value = CloudSyncState(
                    status = CloudSyncStatus.OFFLINE_ERROR,
                    message = "Offline or sync failed: ${e.localizedMessage ?: "Unknown error"}"
                )
                false
            }
        }
    }

    private fun parseDonorObject(d: JSONObject, defaultId: Long): DonorCandidate {
        val idVal = d.optLong("id", defaultId)
        val name = d.optString("name", "Donor")
        val houseOrFlat = if (d.has("houseOrFlat")) d.optString("houseOrFlat") else d.optString("address", "")
        val phone = d.optString("phone", "")
        val targetAmount = when {
            d.has("targetAmount") -> d.optDouble("targetAmount", 500.0)
            d.has("amount") -> d.optDouble("amount", 500.0)
            else -> 500.0
        }
        val isPaid = d.optBoolean("isPaid", false)
        val paidAmount = when {
            d.has("paidAmount") -> d.optDouble("paidAmount", if (isPaid) targetAmount else 0.0)
            isPaid -> targetAmount
            else -> 0.0
        }
        val paymentMode = d.optString("paymentMode", "Cash")
        val paymentDate = if (d.has("paymentDate") && !d.isNull("paymentDate")) d.optLong("paymentDate") else null
        val transactionRef = if (d.has("transactionRef")) d.optString("transactionRef") else d.optString("receiptNumber", "")
        val notes = d.optString("notes", "")
        val isApproved = d.optBoolean("isApprovedByAdmin", true)
        val createdAt = d.optLong("createdAt", System.currentTimeMillis())

        return DonorCandidate(
            id = idVal,
            name = name,
            houseOrFlat = houseOrFlat,
            phone = phone,
            targetAmount = targetAmount,
            paidAmount = paidAmount,
            isPaid = isPaid,
            paymentMode = paymentMode,
            paymentDate = paymentDate,
            transactionRef = transactionRef,
            notes = notes,
            isApprovedByAdmin = isApproved,
            createdAt = createdAt
        )
    }

    private fun parseExpenseObject(e: JSONObject, defaultId: Long): ExpenseItem {
        val idVal = e.optLong("id", defaultId)
        val title = e.optString("title", "Expense")
        val category = e.optString("category", "Miscellaneous")
        val amount = e.optDouble("amount", 0.0)
        val vendor = if (e.has("vendor")) e.optString("vendor") else e.optString("paidTo", "")
        val paidBy = if (e.has("paidBy")) e.optString("paidBy") else e.optString("verifiedBy", "")
        val date = e.optLong("date", System.currentTimeMillis())
        val notes = e.optString("notes", "")
        val receiptRef = if (e.has("receiptRef")) e.optString("receiptRef") else e.optString("receiptBillNo", "")

        return ExpenseItem(
            id = idVal,
            title = title,
            category = category,
            amount = amount,
            vendor = vendor,
            paidBy = paidBy,
            date = date,
            notes = notes,
            receiptRef = receiptRef
        )
    }

    private fun parseEventObject(ev: JSONObject, defaultId: Long): FestivalEvent {
        val idVal = ev.optLong("id", defaultId)
        val dayNumber = ev.optInt("dayNumber", 1)
        val title = ev.optString("title", "Event")
        val dateStr = if (ev.has("dateStr")) ev.optString("dateStr") else ev.optString("date", "Day 1")
        val timeStr = if (ev.has("timeStr")) ev.optString("timeStr") else ev.optString("time", "08:00 AM")
        val location = ev.optString("location", "Main Pandal")
        val description = ev.optString("description", "")
        val isCompleted = ev.optBoolean("isCompleted", false)

        return FestivalEvent(
            id = idVal,
            dayNumber = dayNumber,
            title = title,
            dateStr = dateStr,
            timeStr = timeStr,
            location = location,
            description = description,
            isCompleted = isCompleted
        )
    }

    private fun parseSamagriObject(s: JSONObject, defaultId: Long): SamagriItem {
        val idVal = s.optLong("id", defaultId)
        val name = s.optString("name", "")
        val quantity = s.optString("quantity", "1")
        val category = s.optString("category", "Daily Puja")
        val isArranged = if (s.has("isArranged")) s.optBoolean("isArranged") else s.optBoolean("isPurchased", false)
        val assignedTo = s.optString("assignedTo", "")

        return SamagriItem(
            id = idVal,
            name = name,
            quantity = quantity,
            category = category,
            isArranged = isArranged,
            assignedTo = assignedTo
        )
    }

    private fun parseAdminRequestObject(a: JSONObject, defaultId: Long): AdminRequest {
        val idVal = a.optLong("id", defaultId)
        val requesterName = if (a.has("requesterName")) a.optString("requesterName") else a.optString("applicantName", "")
        val phone = a.optString("phone", "")
        val reason = if (a.has("reason")) a.optString("reason") else a.optString("roleReason", "")
        val status = a.optString("status", "PENDING")
        val requestedAt = if (a.has("requestedAt")) a.optLong("requestedAt") else a.optLong("requestedDate", System.currentTimeMillis())
        val reviewedAt = if (a.has("reviewedAt") && !a.isNull("reviewedAt")) a.optLong("reviewedAt") else null

        return AdminRequest(
            id = idVal,
            requesterName = requesterName,
            phone = phone,
            reason = reason,
            status = status,
            requestedAt = requestedAt,
            reviewedAt = reviewedAt
        )
    }

    /**
     * Pushes current local Room database state to Firebase Cloud Realtime DB.
     */
    suspend fun pushToCloud(dao: GaneshDao, adminName: String): Boolean {
        return withContext(Dispatchers.IO) {
            _syncState.value = _syncState.value.copy(
                status = CloudSyncStatus.SYNCING,
                message = "Uploading changes to Cloud DB..."
            )
            val success = pushToCloudInternal(dao, adminName)
            if (success) {
                _syncState.value = CloudSyncState(
                    status = CloudSyncStatus.SYNCED,
                    lastSyncTime = System.currentTimeMillis(),
                    lastSyncedBy = adminName,
                    message = "Cloud database updated successfully ✅"
                )
            } else {
                _syncState.value = CloudSyncState(
                    status = CloudSyncStatus.OFFLINE_ERROR,
                    message = "Cloud push failed"
                )
            }
            success
        }
    }

    private suspend fun pushToCloudInternal(dao: GaneshDao, adminName: String): Boolean {
        return try {
            val rootJson = JSONObject()
            val now = System.currentTimeMillis()

            rootJson.put("lastUpdated", now)
            rootJson.put("updatedBy", adminName)

            // Donors
            val donors = dao.getAllDonorsList()
            val donorsArray = JSONArray()
            donors.forEach { d ->
                val obj = JSONObject().apply {
                    put("id", d.id)
                    put("name", d.name)
                    put("houseOrFlat", d.houseOrFlat)
                    put("phone", d.phone)
                    put("targetAmount", d.targetAmount)
                    put("paidAmount", d.paidAmount)
                    put("isPaid", d.isPaid)
                    put("paymentMode", d.paymentMode)
                    put("paymentDate", d.paymentDate ?: JSONObject.NULL)
                    put("transactionRef", d.transactionRef)
                    put("notes", d.notes)
                    put("isApprovedByAdmin", d.isApprovedByAdmin)
                    put("createdAt", d.createdAt)
                }
                donorsArray.put(obj)
            }
            rootJson.put("donors", donorsArray)

            // Expenses
            val expenses = dao.getAllExpensesList()
            val expensesArray = JSONArray()
            expenses.forEach { e ->
                val obj = JSONObject().apply {
                    put("id", e.id)
                    put("title", e.title)
                    put("category", e.category)
                    put("amount", e.amount)
                    put("vendor", e.vendor)
                    put("paidBy", e.paidBy)
                    put("date", e.date)
                    put("notes", e.notes)
                    put("receiptRef", e.receiptRef)
                }
                expensesArray.put(obj)
            }
            rootJson.put("expenses", expensesArray)

            // Events
            val events = dao.getAllEventsList()
            val eventsArray = JSONArray()
            events.forEach { ev ->
                val obj = JSONObject().apply {
                    put("id", ev.id)
                    put("dayNumber", ev.dayNumber)
                    put("title", ev.title)
                    put("dateStr", ev.dateStr)
                    put("timeStr", ev.timeStr)
                    put("location", ev.location)
                    put("description", ev.description)
                    put("isCompleted", ev.isCompleted)
                }
                eventsArray.put(obj)
            }
            rootJson.put("events", eventsArray)

            // Samagri
            val samagri = dao.getAllSamagriList()
            val samagriArray = JSONArray()
            samagri.forEach { s ->
                val obj = JSONObject().apply {
                    put("id", s.id)
                    put("name", s.name)
                    put("quantity", s.quantity)
                    put("category", s.category)
                    put("isArranged", s.isArranged)
                    put("assignedTo", s.assignedTo)
                }
                samagriArray.put(obj)
            }
            rootJson.put("samagri", samagriArray)

            // Admin Requests
            val adminRequests = dao.getAllAdminRequestsList()
            val adminRequestsArray = JSONArray()
            adminRequests.forEach { a ->
                val obj = JSONObject().apply {
                    put("id", a.id)
                    put("requesterName", a.requesterName)
                    put("phone", a.phone)
                    put("reason", a.reason)
                    put("status", a.status)
                    put("requestedAt", a.requestedAt)
                    put("reviewedAt", a.reviewedAt ?: JSONObject.NULL)
                }
                adminRequestsArray.put(obj)
            }
            rootJson.put("adminRequests", adminRequestsArray)

            // Settings
            val settings = dao.getAllSettingsList()
            val settingsArray = JSONArray()
            settings.forEach { st ->
                val obj = JSONObject().apply {
                    put("key", st.key)
                    put("value", st.value)
                }
                settingsArray.put(obj)
            }
            rootJson.put("settings", settingsArray)

            val jsonBody = rootJson.toString()
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toRequestBody(mediaType)

            // Push to aura_utsav.json
            val request = Request.Builder()
                .url(FIREBASE_DATABASE_URL)
                .put(body)
                .build()

            val response = httpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push to cloud", e)
            false
        }
    }
}
