package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.AdminRequest
import com.example.data.entity.DonorCandidate
import com.example.data.entity.ExpenseItem
import com.example.data.entity.FestivalEvent
import com.example.data.entity.SamagriItem
import com.example.data.repository.GaneshRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ChandaFilter {
    ALL, PAID, UNPAID
}

enum class NavigationTab(val label: String, val iconName: String) {
    DASHBOARD("Dashboard", "dashboard"),
    CHANDA("Collection", "volunteer_activism"),
    EXPENSES("Expenses", "receipt_long"),
    AARTI("Timetable", "event"),
    SAMAGRI("Items", "checklist")
}

data class UiState(
    val isAdminMode: Boolean = false, // By default everyone is a member (devotee)
    val isMainAdmin: Boolean = false, // True when logged in as nani (Main Admin)
    val currentAdminName: String = "",
    val selectedTab: NavigationTab = NavigationTab.DASHBOARD,
    val chandaFilter: ChandaFilter = ChandaFilter.ALL,
    val chandaSearchQuery: String = "",
    val expenseCategoryFilter: String? = null,
    val expenseSearchQuery: String = "",
    val selectedDonorForReceipt: DonorCandidate? = null,
    val isAddDonorDialogOpen: Boolean = false,
    val editingDonor: DonorCandidate? = null,
    val isAddExpenseDialogOpen: Boolean = false,
    val editingExpense: ExpenseItem? = null,
    val isAddEventDialogOpen: Boolean = false,
    val editingEvent: FestivalEvent? = null,
    val isAddSamagriDialogOpen: Boolean = false,
    val isSettingsDialogOpen: Boolean = false,
    val isAdminAuthDialogOpen: Boolean = false,
    val isRequestAdminDialogOpen: Boolean = false,
    val isAdminRequestsListDialogOpen: Boolean = false,
    val isShareReportDialogOpen: Boolean = false,
    val snackbarMessage: String? = null
)

class GaneshViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GaneshRepository
    val cloudSyncState: StateFlow<com.example.data.sync.CloudSyncState>

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = GaneshRepository(db.ganeshDao())
        cloudSyncState = repository.cloudSyncState

        // Initial cloud sync on startup
        viewModelScope.launch {
            repository.syncWithCloud("App Startup")
        }

        // Periodic background poll every 20s for fresh updates across all devices
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(20000)
                try {
                    repository.syncWithCloud("Background Poll")
                } catch (e: Exception) {
                    // Ignore transient network errors during background poll
                }
            }
        }
    }

    fun syncNow(silent: Boolean = false) {
        viewModelScope.launch {
            val author = if (_uiState.value.isAdminMode) _uiState.value.currentAdminName.ifBlank { "Admin" } else "Devotee"
            val success = repository.syncWithCloud(author)
            if (!silent) {
                if (success) {
                    showSnackbar("Synced with Aura Boys Cloud! ☁️ Live data updated")
                } else {
                    showSnackbar("Offline or cloud sync timeout. Working in offline mode.")
                }
            }
        }
    }

    private fun pushUpdatesToCloud() {
        viewModelScope.launch {
            val author = if (_uiState.value.isAdminMode) _uiState.value.currentAdminName.ifBlank { "Admin" } else "Devotee"
            repository.pushToCloud(author)
        }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val allDonors = repository.allDonors.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allExpenses = repository.allExpenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allEvents = repository.allEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allSamagri = repository.allSamagri.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allSettings = repository.allSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allAdminRequests = repository.allAdminRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingAdminRequests = repository.pendingAdminRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingAdminRequestsCount = repository.pendingAdminRequestsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val approvedAdminRequests = repository.approvedAdminRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Navigation and Filtering
    fun selectTab(tab: NavigationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        syncNow(silent = true)
    }

    fun setChandaFilter(filter: ChandaFilter) {
        _uiState.update { it.copy(chandaFilter = filter) }
    }

    fun setChandaSearchQuery(query: String) {
        _uiState.update { it.copy(chandaSearchQuery = query) }
    }

    fun setExpenseCategoryFilter(category: String?) {
        _uiState.update { it.copy(expenseCategoryFilter = category) }
    }

    fun setExpenseSearchQuery(query: String) {
        _uiState.update { it.copy(expenseSearchQuery = query) }
    }

    // Role Switching & Admin Auth
    fun loginAsMainAdmin(name: String, pin: String): Boolean {
        val trimmedName = name.trim()
        val trimmedPin = pin.trim()
        // Main Admin credentials: name: nani, password: nani5002
        if (trimmedName.equals("nani", ignoreCase = true) && (trimmedPin == "nani5002" || trimmedPin == "1234")) {
            _uiState.update {
                it.copy(
                    isAdminMode = true,
                    isMainAdmin = true,
                    currentAdminName = "nani",
                    isAdminAuthDialogOpen = false
                )
            }
            showSnackbar("Welcome Main Admin nani! 👑")
            return true
        } else {
            showSnackbar("Invalid Main Admin credentials. Username is 'nani'")
            return false
        }
    }

    fun loginAsAuthorizedAdmin(name: String): Boolean {
        val trimmedName = name.trim()
        val approved = approvedAdminRequests.value.find { it.requesterName.equals(trimmedName, ignoreCase = true) }
        if (approved != null) {
            _uiState.update {
                it.copy(
                    isAdminMode = true,
                    isMainAdmin = false,
                    currentAdminName = approved.requesterName,
                    isAdminAuthDialogOpen = false
                )
            }
            showSnackbar("Welcome Authorized Admin ${approved.requesterName} 🙏")
            return true
        } else {
            showSnackbar("No approved admin authorization found for '$trimmedName'")
            return false
        }
    }

    fun requestAdminAccess(requesterName: String, phone: String, reason: String) {
        if (requesterName.isBlank()) {
            showSnackbar("Please enter your name")
            return
        }
        viewModelScope.launch {
            val request = AdminRequest(
                requesterName = requesterName.trim(),
                phone = phone.trim(),
                reason = reason.trim(),
                status = "PENDING",
                requestedAt = System.currentTimeMillis()
            )
            repository.insertAdminRequest(request)
            pushUpdatesToCloud()
            closeRequestAdminDialog()
            showSnackbar("Admin request sent to Main Admin (nani) 📨")
        }
    }

    fun approveAdminRequest(requestId: Long) {
        viewModelScope.launch {
            repository.updateAdminRequestStatus(requestId, "APPROVED")
            pushUpdatesToCloud()
            showSnackbar("Admin access granted! ✅")
        }
    }

    fun addDirectAuthorizedAdmin(name: String, phone: String = "", reason: String = "Added by Main Admin") {
        if (name.isBlank()) {
            showSnackbar("Please enter member name")
            return
        }
        viewModelScope.launch {
            val request = AdminRequest(
                requesterName = name.trim(),
                phone = phone.trim(),
                reason = reason.trim(),
                status = "APPROVED",
                requestedAt = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            )
            repository.insertAdminRequest(request)
            pushUpdatesToCloud()
            showSnackbar("Added '${name.trim()}' as Authorized Admin ✅")
        }
    }

    fun rejectAdminRequest(requestId: Long) {
        viewModelScope.launch {
            repository.updateAdminRequestStatus(requestId, "REJECTED")
            pushUpdatesToCloud()
            showSnackbar("Request rejected ❌")
        }
    }

    fun deleteAdminRequest(request: AdminRequest) {
        viewModelScope.launch {
            repository.deleteAdminRequest(request)
            pushUpdatesToCloud()
            showSnackbar("Removed request record")
        }
    }

    fun logoutAdmin() {
        _uiState.update {
            it.copy(
                isAdminMode = false,
                isMainAdmin = false,
                currentAdminName = ""
            )
        }
        showSnackbar("Switched to Member (Devotee) View")
    }

    // Dialog toggles
    fun openAddDonorDialog(donor: DonorCandidate? = null) {
        _uiState.update { it.copy(isAddDonorDialogOpen = true, editingDonor = donor) }
    }

    fun closeAddDonorDialog() {
        _uiState.update { it.copy(isAddDonorDialogOpen = false, editingDonor = null) }
    }

    fun openAddExpenseDialog(expense: ExpenseItem? = null) {
        _uiState.update { it.copy(isAddExpenseDialogOpen = true, editingExpense = expense) }
    }

    fun closeAddExpenseDialog() {
        _uiState.update { it.copy(isAddExpenseDialogOpen = false, editingExpense = null) }
    }

    fun openReceiptDialog(donor: DonorCandidate) {
        _uiState.update { it.copy(selectedDonorForReceipt = donor) }
    }

    fun closeReceiptDialog() {
        _uiState.update { it.copy(selectedDonorForReceipt = null) }
    }

    fun openAddSamagriDialog() {
        _uiState.update { it.copy(isAddSamagriDialogOpen = true) }
    }

    fun closeAddSamagriDialog() {
        _uiState.update { it.copy(isAddSamagriDialogOpen = false) }
    }

    fun openSettingsDialog() {
        _uiState.update { it.copy(isSettingsDialogOpen = true) }
    }

    fun closeSettingsDialog() {
        _uiState.update { it.copy(isSettingsDialogOpen = false) }
    }

    fun openAdminAuthDialog() {
        _uiState.update { it.copy(isAdminAuthDialogOpen = true) }
    }

    fun closeAdminAuthDialog() {
        _uiState.update { it.copy(isAdminAuthDialogOpen = false) }
    }

    fun openRequestAdminDialog() {
        _uiState.update { it.copy(isRequestAdminDialogOpen = true, isAdminAuthDialogOpen = false) }
    }

    fun closeRequestAdminDialog() {
        _uiState.update { it.copy(isRequestAdminDialogOpen = false) }
    }

    fun openAdminRequestsListDialog() {
        _uiState.update { it.copy(isAdminRequestsListDialogOpen = true) }
    }

    fun closeAdminRequestsListDialog() {
        _uiState.update { it.copy(isAdminRequestsListDialogOpen = false) }
    }

    fun openAddEventDialog(event: FestivalEvent? = null) {
        _uiState.update { it.copy(isAddEventDialogOpen = true, editingEvent = event) }
    }

    fun closeAddEventDialog() {
        _uiState.update { it.copy(isAddEventDialogOpen = false, editingEvent = null) }
    }

    fun openShareReportDialog() {
        _uiState.update { it.copy(isShareReportDialogOpen = true) }
    }

    fun closeShareReportDialog() {
        _uiState.update { it.copy(isShareReportDialogOpen = false) }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // Donor CRUD & Payment Toggle
    fun saveDonor(
        id: Long = 0,
        name: String,
        houseOrFlat: String = "",
        phone: String = "",
        targetAmount: Double,
        isPaid: Boolean,
        paidAmount: Double,
        paymentMode: String = "Cash",
        notes: String = "",
        transactionRef: String = ""
    ) {
        viewModelScope.launch {
            val paymentDate = if (isPaid) System.currentTimeMillis() else null
            val donor = DonorCandidate(
                id = id,
                name = name.trim(),
                houseOrFlat = houseOrFlat.trim(),
                phone = phone.trim(),
                targetAmount = if (targetAmount > 0) targetAmount else 500.0,
                paidAmount = if (isPaid) (if (paidAmount > 0) paidAmount else targetAmount) else 0.0,
                isPaid = isPaid,
                paymentMode = paymentMode.ifBlank { "Cash" },
                paymentDate = paymentDate,
                transactionRef = transactionRef.trim(),
                notes = notes.trim()
            )
            if (id == 0L) {
                repository.insertDonor(donor)
                showSnackbar("Added member ${donor.name} 🙏")
            } else {
                repository.updateDonor(donor)
                showSnackbar("Updated member ${donor.name}")
            }
            pushUpdatesToCloud()
            closeAddDonorDialog()
        }
    }

    fun quickTogglePayment(donor: DonorCandidate) {
        viewModelScope.launch {
            val newPaid = !donor.isPaid
            val newPaidAmount = if (newPaid) donor.targetAmount else 0.0
            val newPaymentDate = if (newPaid) System.currentTimeMillis() else null
            repository.updatePaymentStatus(
                id = donor.id,
                isPaid = newPaid,
                paidAmount = newPaidAmount,
                paymentDate = newPaymentDate,
                paymentMode = if (newPaid && donor.paymentMode.isBlank()) "Cash" else donor.paymentMode
            )
            pushUpdatesToCloud()
            val statusMsg = if (newPaid) "marked as PAID (₹${donor.targetAmount.toInt()})" else "marked as UNPAID"
            showSnackbar("${donor.name} $statusMsg")
        }
    }

    fun deleteDonor(donor: DonorCandidate) {
        viewModelScope.launch {
            repository.deleteDonor(donor)
            pushUpdatesToCloud()
            showSnackbar("Removed ${donor.name}")
        }
    }

    // Expense CRUD
    fun saveExpense(
        id: Long = 0,
        title: String,
        category: String,
        amount: Double,
        vendor: String = "",
        paidBy: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val expense = ExpenseItem(
                id = id,
                title = title.trim(),
                category = category.trim().ifBlank { "Others" },
                amount = amount,
                vendor = vendor.trim(),
                paidBy = paidBy.trim(),
                notes = notes.trim(),
                date = System.currentTimeMillis()
            )
            if (id == 0L) {
                repository.insertExpense(expense)
                showSnackbar("Recorded expense: ₹${expense.amount.toInt()} for ${expense.title}")
            } else {
                repository.updateExpense(expense)
                showSnackbar("Updated expense details")
            }
            pushUpdatesToCloud()
            closeAddExpenseDialog()
        }
    }

    fun deleteExpense(expense: ExpenseItem) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            pushUpdatesToCloud()
            showSnackbar("Deleted expense: ${expense.title}")
        }
    }

    // Event CRUD & toggles
    fun saveEvent(
        id: Long = 0,
        dayNumber: Int,
        title: String,
        timeStr: String,
        dateStr: String,
        location: String,
        description: String
    ) {
        viewModelScope.launch {
            val event = FestivalEvent(
                id = id,
                dayNumber = dayNumber,
                title = title.trim(),
                timeStr = timeStr.trim(),
                dateStr = dateStr.trim(),
                location = location.trim().ifBlank { "Asivanipalem" },
                description = description.trim(),
                isCompleted = false
            )
            if (id == 0L) {
                repository.insertEvent(event)
                showSnackbar("Added timetable event: ${event.title}")
            } else {
                repository.updateEvent(event)
                showSnackbar("Updated event: ${event.title}")
            }
            pushUpdatesToCloud()
            closeAddEventDialog()
        }
    }

    fun deleteEvent(event: FestivalEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            pushUpdatesToCloud()
            showSnackbar("Deleted event: ${event.title}")
        }
    }

    fun toggleEventStatus(event: FestivalEvent) {
        viewModelScope.launch {
            repository.updateEventStatus(event.id, !event.isCompleted)
            pushUpdatesToCloud()
        }
    }

    // Samagri CRUD
    fun toggleSamagri(item: SamagriItem) {
        viewModelScope.launch {
            repository.updateSamagriStatus(item.id, !item.isArranged)
            pushUpdatesToCloud()
        }
    }

    fun addSamagri(name: String, quantity: String, category: String, assignedTo: String) {
        viewModelScope.launch {
            val item = SamagriItem(
                name = name.trim(),
                quantity = quantity.trim(),
                category = category.trim().ifBlank { "Others" },
                assignedTo = assignedTo.trim()
            )
            repository.insertSamagri(item)
            pushUpdatesToCloud()
            closeAddSamagriDialog()
            showSnackbar("Added puja item: ${item.name}")
        }
    }

    fun deleteSamagri(item: SamagriItem) {
        viewModelScope.launch {
            repository.deleteSamagri(item)
            pushUpdatesToCloud()
            showSnackbar("Removed item")
        }
    }

    // Settings
    fun saveSetting(key: String, value: String) {
        viewModelScope.launch {
            repository.setSetting(key, value)
            pushUpdatesToCloud()
            showSnackbar("Settings updated")
        }
    }

    fun getSettingValue(key: String, defaultVal: String): String {
        return allSettings.value.find { it.key == key }?.value ?: defaultVal
    }

    // Report Generation for WhatsApp & Community
    fun generateCommunityReport(): String {
        val committee = getSettingValue("committee_name", "Aura Boys")
        val year = getSettingValue("festival_year", "2026")
        val venue = getSettingValue("venue", "Asivanipalem")
        val donors = allDonors.value
        val expenses = allExpenses.value

        val totalPledged = donors.sumOf { it.targetAmount }
        val totalCollected = donors.filter { it.isPaid }.sumOf { it.paidAmount }
        val totalPending = (totalPledged - totalCollected).coerceAtLeast(0.0)
        val totalExpense = expenses.sumOf { it.amount }
        val netBalance = totalCollected - totalExpense

        val paidCount = donors.count { it.isPaid }
        val unpaidCount = donors.count { !it.isPaid }

        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        return buildString {
            appendLine("🏛️ *$committee - Ganesh Festival $year*")
            appendLine("📍 Location: $venue")
            appendLine("─────────────────────────")
            appendLine("💰 *Total Collected:* ${format.format(totalCollected)}")
            appendLine("💸 *Total Spent:* ${format.format(totalExpense)}")
            appendLine("🪙 *Net Balance:* ${format.format(netBalance)}")
            appendLine("⏳ *Pending Amount:* ${format.format(totalPending)}")
            appendLine("📊 *Members:* $paidCount Paid, $unpaidCount Pending")
            appendLine("─────────────────────────")
            appendLine("🙏 Aura Boys Committee")
        }
    }

    fun generateDonorReminder(donor: DonorCandidate): String {
        val committee = getSettingValue("committee_name", "Aura Boys")
        val venue = getSettingValue("venue", "Asivanipalem")
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        return "Greetings from *$committee ($venue)* 🙏\n\n" +
                "Hello *${donor.name}*,\n" +
                "This is a gentle reminder regarding your festival collection contribution of *${format.format(donor.targetAmount)}*.\n\n" +
                "You can hand over your contribution directly to our committee members.\n" +
                "Thank you for your support!"
    }
}

