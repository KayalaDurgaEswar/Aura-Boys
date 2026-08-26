package com.example.data.repository

import com.example.data.dao.GaneshDao
import com.example.data.entity.AdminRequest
import com.example.data.entity.AppSetting
import com.example.data.entity.DonorCandidate
import com.example.data.entity.ExpenseItem
import com.example.data.entity.FestivalEvent
import com.example.data.entity.SamagriItem
import kotlinx.coroutines.flow.Flow

class GaneshRepository(private val ganeshDao: GaneshDao) {

    // Admin Requests
    val allAdminRequests: Flow<List<AdminRequest>> = ganeshDao.getAllAdminRequests()
    val pendingAdminRequests: Flow<List<AdminRequest>> = ganeshDao.getPendingAdminRequests()
    val approvedAdminRequests: Flow<List<AdminRequest>> = ganeshDao.getApprovedAdminRequests()
    val pendingAdminRequestsCount: Flow<Int> = ganeshDao.getPendingAdminRequestsCount()

    suspend fun insertAdminRequest(request: AdminRequest): Long = ganeshDao.insertAdminRequest(request)
    suspend fun updateAdminRequestStatus(id: Long, status: String, reviewedAt: Long = System.currentTimeMillis()) =
        ganeshDao.updateAdminRequestStatus(id, status, reviewedAt)
    suspend fun deleteAdminRequest(request: AdminRequest) = ganeshDao.deleteAdminRequest(request)
    suspend fun getApprovedRequestByName(name: String): AdminRequest? = ganeshDao.getApprovedRequestByName(name)

    // Donors
    val allDonors: Flow<List<DonorCandidate>> = ganeshDao.getAllDonors()
    val paidDonors: Flow<List<DonorCandidate>> = ganeshDao.getPaidDonors()
    val unpaidDonors: Flow<List<DonorCandidate>> = ganeshDao.getUnpaidDonors()

    suspend fun insertDonor(donor: DonorCandidate): Long = ganeshDao.insertDonor(donor)
    suspend fun updateDonor(donor: DonorCandidate) = ganeshDao.updateDonor(donor)
    suspend fun deleteDonor(donor: DonorCandidate) = ganeshDao.deleteDonor(donor)
    suspend fun deleteDonorById(id: Long) = ganeshDao.deleteDonorById(id)
    suspend fun updatePaymentStatus(
        id: Long,
        isPaid: Boolean,
        paidAmount: Double,
        paymentDate: Long?,
        paymentMode: String
    ) = ganeshDao.updatePaymentStatus(id, isPaid, paidAmount, paymentDate, paymentMode)

    // Expenses
    val allExpenses: Flow<List<ExpenseItem>> = ganeshDao.getAllExpenses()

    suspend fun insertExpense(expense: ExpenseItem): Long = ganeshDao.insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseItem) = ganeshDao.updateExpense(expense)
    suspend fun deleteExpense(expense: ExpenseItem) = ganeshDao.deleteExpense(expense)
    suspend fun deleteExpenseById(id: Long) = ganeshDao.deleteExpenseById(id)

    // Events
    val allEvents: Flow<List<FestivalEvent>> = ganeshDao.getAllEvents()
    suspend fun insertEvent(event: FestivalEvent): Long = ganeshDao.insertEvent(event)
    suspend fun updateEventStatus(id: Long, isCompleted: Boolean) = ganeshDao.updateEventStatus(id, isCompleted)
    suspend fun updateEvent(event: FestivalEvent) = ganeshDao.updateEvent(event)
    suspend fun deleteEvent(event: FestivalEvent) = ganeshDao.deleteEvent(event)

    // Samagri
    val allSamagri: Flow<List<SamagriItem>> = ganeshDao.getAllSamagri()
    suspend fun insertSamagri(item: SamagriItem): Long = ganeshDao.insertSamagri(item)
    suspend fun updateSamagri(item: SamagriItem) = ganeshDao.updateSamagri(item)
    suspend fun updateSamagriStatus(id: Long, isArranged: Boolean) = ganeshDao.updateSamagriStatus(id, isArranged)
    suspend fun deleteSamagri(item: SamagriItem) = ganeshDao.deleteSamagri(item)

    // Settings
    val allSettings: Flow<List<AppSetting>> = ganeshDao.getAllSettings()
    suspend fun setSetting(key: String, value: String) = ganeshDao.setSetting(AppSetting(key, value))
    suspend fun getSettingValue(key: String): String? = ganeshDao.getSettingValue(key)

    // Cloud Realtime Sync
    val cloudSyncState = com.example.data.sync.FirebaseSyncManager.syncState

    suspend fun syncWithCloud(author: String = "App User"): Boolean =
        com.example.data.sync.FirebaseSyncManager.syncWithCloud(ganeshDao, author)

    suspend fun pushToCloud(author: String = "Admin"): Boolean =
        com.example.data.sync.FirebaseSyncManager.pushToCloud(ganeshDao, author)
}
