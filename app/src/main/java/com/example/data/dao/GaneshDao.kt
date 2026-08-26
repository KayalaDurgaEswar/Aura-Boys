package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.entity.AdminRequest
import com.example.data.entity.AppSetting
import com.example.data.entity.DonorCandidate
import com.example.data.entity.ExpenseItem
import com.example.data.entity.FestivalEvent
import com.example.data.entity.SamagriItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GaneshDao {

    // --- Admin Requests ---
    @Query("SELECT * FROM admin_requests ORDER BY requestedAt DESC")
    fun getAllAdminRequests(): Flow<List<AdminRequest>>

    @Query("SELECT * FROM admin_requests")
    suspend fun getAllAdminRequestsList(): List<AdminRequest>

    @Query("SELECT * FROM admin_requests WHERE status = 'PENDING' ORDER BY requestedAt DESC")
    fun getPendingAdminRequests(): Flow<List<AdminRequest>>

    @Query("SELECT * FROM admin_requests WHERE status = 'APPROVED'")
    fun getApprovedAdminRequests(): Flow<List<AdminRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminRequest(request: AdminRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAdminRequests(requests: List<AdminRequest>)

    @Update
    suspend fun updateAdminRequest(request: AdminRequest)

    @Query("UPDATE admin_requests SET status = :status, reviewedAt = :reviewedAt WHERE id = :id")
    suspend fun updateAdminRequestStatus(id: Long, status: String, reviewedAt: Long)

    @Delete
    suspend fun deleteAdminRequest(request: AdminRequest)

    @Query("DELETE FROM admin_requests")
    suspend fun deleteAllAdminRequests()

    @Query("SELECT COUNT(*) FROM admin_requests WHERE status = 'PENDING'")
    fun getPendingAdminRequestsCount(): Flow<Int>

    @Query("SELECT * FROM admin_requests WHERE requesterName = :name AND status = 'APPROVED' LIMIT 1")
    suspend fun getApprovedRequestByName(name: String): AdminRequest?

    // --- Donors (Chanda) ---
    @Query("SELECT * FROM donors ORDER BY createdAt DESC")
    fun getAllDonors(): Flow<List<DonorCandidate>>

    @Query("SELECT * FROM donors")
    suspend fun getAllDonorsList(): List<DonorCandidate>

    @Query("SELECT * FROM donors WHERE isPaid = 1 ORDER BY paymentDate DESC")
    fun getPaidDonors(): Flow<List<DonorCandidate>>

    @Query("SELECT * FROM donors WHERE isPaid = 0 ORDER BY createdAt DESC")
    fun getUnpaidDonors(): Flow<List<DonorCandidate>>

    @Query("SELECT * FROM donors WHERE id = :id")
    suspend fun getDonorById(id: Long): DonorCandidate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: DonorCandidate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDonors(donors: List<DonorCandidate>)

    @Update
    suspend fun updateDonor(donor: DonorCandidate)

    @Delete
    suspend fun deleteDonor(donor: DonorCandidate)

    @Query("DELETE FROM donors WHERE id = :id")
    suspend fun deleteDonorById(id: Long)

    @Query("DELETE FROM donors")
    suspend fun deleteAllDonors()

    @Query("UPDATE donors SET isPaid = :isPaid, paidAmount = :paidAmount, paymentDate = :paymentDate, paymentMode = :paymentMode WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, isPaid: Boolean, paidAmount: Double, paymentDate: Long?, paymentMode: String)

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseItem>>

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpensesList(): List<ExpenseItem>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExpenses(expenses: List<ExpenseItem>)

    @Update
    suspend fun updateExpense(expense: ExpenseItem)

    @Delete
    suspend fun deleteExpense(expense: ExpenseItem)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    // --- Events / Schedule ---
    @Query("SELECT * FROM events ORDER BY dayNumber ASC, id ASC")
    fun getAllEvents(): Flow<List<FestivalEvent>>

    @Query("SELECT * FROM events")
    suspend fun getAllEventsList(): List<FestivalEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: FestivalEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEvents(events: List<FestivalEvent>)

    @Update
    suspend fun updateEvent(event: FestivalEvent)

    @Delete
    suspend fun deleteEvent(event: FestivalEvent)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()

    @Query("UPDATE events SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateEventStatus(id: Long, isCompleted: Boolean)

    // --- Samagri Items ---
    @Query("SELECT * FROM samagri ORDER BY id ASC")
    fun getAllSamagri(): Flow<List<SamagriItem>>

    @Query("SELECT * FROM samagri")
    suspend fun getAllSamagriList(): List<SamagriItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSamagri(items: List<SamagriItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSamagri(item: SamagriItem): Long

    @Update
    suspend fun updateSamagri(item: SamagriItem)

    @Query("UPDATE samagri SET isArranged = :isArranged WHERE id = :id")
    suspend fun updateSamagriStatus(id: Long, isArranged: Boolean)

    @Delete
    suspend fun deleteSamagri(item: SamagriItem)

    @Query("DELETE FROM samagri")
    suspend fun deleteAllSamagri()

    // --- Settings ---
    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<AppSetting>>

    @Query("SELECT * FROM settings")
    suspend fun getAllSettingsList(): List<AppSetting>

    @Query("SELECT value FROM settings WHERE `key` = :key")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSetting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSettings(settings: List<AppSetting>)

    @Transaction
    suspend fun replaceAllWithCloudSnapshot(
        donors: List<DonorCandidate>,
        expenses: List<ExpenseItem>,
        events: List<FestivalEvent>,
        samagri: List<SamagriItem>,
        adminRequests: List<AdminRequest>,
        settings: List<AppSetting>
    ) {
        deleteAllDonors()
        if (donors.isNotEmpty()) {
            insertAllDonors(donors)
        }

        deleteAllExpenses()
        if (expenses.isNotEmpty()) {
            insertAllExpenses(expenses)
        }

        deleteAllEvents()
        if (events.isNotEmpty()) {
            insertAllEvents(events)
        }

        deleteAllSamagri()
        if (samagri.isNotEmpty()) {
            insertAllSamagri(samagri)
        }

        deleteAllAdminRequests()
        if (adminRequests.isNotEmpty()) {
            insertAllAdminRequests(adminRequests)
        }

        if (settings.isNotEmpty()) {
            setSettings(settings)
        }
    }
}
