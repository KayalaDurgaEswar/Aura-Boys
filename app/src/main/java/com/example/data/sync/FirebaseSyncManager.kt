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

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val _syncState = MutableStateFlow(CloudSyncState())
    val syncState: StateFlow<CloudSyncState> = _syncState.asStateFlow()

    /**
     * Pulls the latest cloud snapshot from Firebase and merges/replaces into local Room database.
     * If cloud is empty or null (first time), automatically seeds cloud with local Room data.
     */
    suspend fun syncWithCloud(dao: GaneshDao, currentAdminName: String = "App User"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                _syncState.value = _syncState.value.copy(
                    status = CloudSyncStatus.SYNCING,
                    message = "Connecting to Aura Boys Cloud..."
                )

                val request = Request.Builder()
                    .url(FIREBASE_DATABASE_URL)
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null || responseBody.trim() == "null" || responseBody.trim() == "{}") {
                    Log.d(TAG, "Cloud DB empty or uninitialized. Pushing local data to cloud.")
                    // Cloud has no data yet, push local data to seed cloud!
                    val pushSuccess = pushToCloudInternal(dao, currentAdminName)
                    if (pushSuccess) {
                        _syncState.value = CloudSyncState(
                            status = CloudSyncStatus.SYNCED,
                            lastSyncTime = System.currentTimeMillis(),
                            lastSyncedBy = currentAdminName,
                            message = "Cloud initialized & synced ✅"
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
                val rootJson = JSONObject(responseBody)
                val cloudTimestamp = rootJson.optLong("lastUpdated", 0L)
                val cloudUpdatedBy = rootJson.optString("updatedBy", "Admin")

                // Parse Donors
                val donorsList = mutableListOf<DonorCandidate>()
                val donorsArray = rootJson.optJSONArray("donors")
                if (donorsArray != null) {
                    for (i in 0 until donorsArray.length()) {
                        val d = donorsArray.optJSONObject(i) ?: continue
                        donorsList.add(
                            DonorCandidate(
                                id = d.optLong("id", 0L),
                                name = d.optString("name", "Unknown"),
                                houseOrFlat = d.optString("houseOrFlat", ""),
                                phone = d.optString("phone", ""),
                                targetAmount = d.optDouble("targetAmount", 0.0),
                                paidAmount = d.optDouble("paidAmount", 0.0),
                                isPaid = d.optBoolean("isPaid", false),
                                paymentMode = d.optString("paymentMode", "Cash"),
                                paymentDate = if (d.has("paymentDate") && !d.isNull("paymentDate")) d.optLong("paymentDate") else null,
                                transactionRef = d.optString("transactionRef", ""),
                                notes = d.optString("notes", ""),
                                isApprovedByAdmin = d.optBoolean("isApprovedByAdmin", true),
                                createdAt = d.optLong("createdAt", System.currentTimeMillis())
                            )
                        )
                    }
                }

                // Parse Expenses
                val expensesList = mutableListOf<ExpenseItem>()
                val expensesArray = rootJson.optJSONArray("expenses")
                if (expensesArray != null) {
                    for (i in 0 until expensesArray.length()) {
                        val e = expensesArray.optJSONObject(i) ?: continue
                        expensesList.add(
                            ExpenseItem(
                                id = e.optLong("id", 0L),
                                title = e.optString("title", "Expense"),
                                category = e.optString("category", "Miscellaneous"),
                                amount = e.optDouble("amount", 0.0),
                                vendor = e.optString("vendor", ""),
                                paidBy = e.optString("paidBy", ""),
                                date = e.optLong("date", System.currentTimeMillis()),
                                notes = e.optString("notes", ""),
                                receiptRef = e.optString("receiptRef", "")
                            )
                        )
                    }
                }

                // Parse Events (Timetable)
                val eventsList = mutableListOf<FestivalEvent>()
                val eventsArray = rootJson.optJSONArray("events")
                if (eventsArray != null) {
                    for (i in 0 until eventsArray.length()) {
                        val ev = eventsArray.optJSONObject(i) ?: continue
                        eventsList.add(
                            FestivalEvent(
                                id = ev.optLong("id", 0L),
                                dayNumber = ev.optInt("dayNumber", 1),
                                title = ev.optString("title", ""),
                                dateStr = ev.optString("dateStr", ""),
                                timeStr = ev.optString("timeStr", ""),
                                location = ev.optString("location", "Main Pandal"),
                                description = ev.optString("description", ""),
                                isCompleted = ev.optBoolean("isCompleted", false)
                            )
                        )
                    }
                }

                // Parse Samagri
                val samagriList = mutableListOf<SamagriItem>()
                val samagriArray = rootJson.optJSONArray("samagri")
                if (samagriArray != null) {
                    for (i in 0 until samagriArray.length()) {
                        val s = samagriArray.optJSONObject(i) ?: continue
                        samagriList.add(
                            SamagriItem(
                                id = s.optLong("id", 0L),
                                name = s.optString("name", ""),
                                quantity = s.optString("quantity", ""),
                                category = s.optString("category", "Daily Puja"),
                                isArranged = s.optBoolean("isArranged", false),
                                assignedTo = s.optString("assignedTo", "")
                            )
                        )
                    }
                }

                // Parse Admin Requests
                val adminRequestsList = mutableListOf<AdminRequest>()
                val adminRequestsArray = rootJson.optJSONArray("adminRequests")
                if (adminRequestsArray != null) {
                    for (i in 0 until adminRequestsArray.length()) {
                        val a = adminRequestsArray.optJSONObject(i) ?: continue
                        adminRequestsList.add(
                            AdminRequest(
                                id = a.optLong("id", 0L),
                                requesterName = a.optString("requesterName", ""),
                                phone = a.optString("phone", ""),
                                reason = a.optString("reason", ""),
                                status = a.optString("status", "PENDING"),
                                requestedAt = a.optLong("requestedAt", System.currentTimeMillis()),
                                reviewedAt = if (a.has("reviewedAt") && !a.isNull("reviewedAt")) a.optLong("reviewedAt") else null
                            )
                        )
                    }
                }

                // Parse Settings
                val settingsList = mutableListOf<AppSetting>()
                val settingsArray = rootJson.optJSONArray("settings")
                if (settingsArray != null) {
                    for (i in 0 until settingsArray.length()) {
                        val st = settingsArray.optJSONObject(i) ?: continue
                        val key = st.optString("key", "")
                        val value = st.optString("value", "")
                        if (key.isNotBlank()) {
                            settingsList.add(AppSetting(key, value))
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
                    message = "Synced with Cloud (${donorsList.size} donors, ${expensesList.size} expenses)"
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

    /**
     * Pushes current local Room database state to Firebase Cloud Realtime DB.
     */
    suspend fun pushToCloud(dao: GaneshDao, adminName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                _syncState.value = _syncState.value.copy(
                    status = CloudSyncStatus.SYNCING,
                    message = "Publishing updates to Cloud..."
                )
                val success = pushToCloudInternal(dao, adminName)
                if (success) {
                    _syncState.value = CloudSyncState(
                        status = CloudSyncStatus.SYNCED,
                        lastSyncTime = System.currentTimeMillis(),
                        lastSyncedBy = adminName,
                        message = "Live on Cloud ✅"
                    )
                } else {
                    _syncState.value = CloudSyncState(
                        status = CloudSyncStatus.OFFLINE_ERROR,
                        message = "Failed to upload to Cloud"
                    )
                }
                success
            } catch (e: Exception) {
                Log.e(TAG, "Error pushing to cloud", e)
                _syncState.value = CloudSyncState(
                    status = CloudSyncStatus.OFFLINE_ERROR,
                    message = "Cloud upload error: ${e.localizedMessage ?: "Network error"}"
                )
                false
            }
        }
    }

    private suspend fun pushToCloudInternal(dao: GaneshDao, adminName: String): Boolean {
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

        val request = Request.Builder()
            .url(FIREBASE_DATABASE_URL)
            .put(body)
            .build()

        val response = httpClient.newCall(request).execute()
        return response.isSuccessful
    }
}
