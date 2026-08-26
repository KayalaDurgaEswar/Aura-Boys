package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.GaneshDao
import com.example.data.entity.AdminRequest
import com.example.data.entity.AppSetting
import com.example.data.entity.DonorCandidate
import com.example.data.entity.ExpenseItem
import com.example.data.entity.FestivalEvent
import com.example.data.entity.SamagriItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DonorCandidate::class,
        ExpenseItem::class,
        FestivalEvent::class,
        SamagriItem::class,
        AppSetting::class,
        AdminRequest::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ganeshDao(): GaneshDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ganesh_utsav_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.ganeshDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: GaneshDao) {
            // Initial Settings
            val initialSettings = listOf(
                AppSetting("committee_name", "Aura Boys"),
                AppSetting("festival_year", "2026"),
                AppSetting("target_goal", "125000"),
                AppSetting("admin_pin", "nani5002"),
                AppSetting("admin_username", "nani"),
                AppSetting("upi_id", "auraboys@upi"),
                AppSetting("upi_name", "Aura Boys Utsav"),
                AppSetting("venue", "Asivanipalem"),
                AppSetting("president_name", "Nani (Main Admin)"),
                AppSetting("secretary_name", "Aura Committee")
            )
            dao.setSettings(initialSettings)

            val now = System.currentTimeMillis()
            val oneDay = 86400000L

            // Initial Donors (Paid and Unpaid candidates)
            val initialDonors = listOf(
                DonorCandidate(
                    name = "Rajesh Sharma & Family",
                    houseOrFlat = "Flat A-402, Shivam Apts",
                    phone = "9823011223",
                    targetAmount = 5100.0,
                    paidAmount = 5100.0,
                    isPaid = true,
                    paymentMode = "UPI",
                    paymentDate = now - (oneDay * 3),
                    transactionRef = "UPI/260824/88921",
                    notes = "Maha Puja Sponsor for Day 1"
                ),
                DonorCandidate(
                    name = "Sunil Verma",
                    houseOrFlat = "B-104, Green Meadows",
                    phone = "9876543210",
                    targetAmount = 2500.0,
                    paidAmount = 2500.0,
                    isPaid = true,
                    paymentMode = "Cash",
                    paymentDate = now - (oneDay * 2),
                    transactionRef = "CASH-REC-02",
                    notes = "Cash given to Committee"
                ),
                DonorCandidate(
                    name = "Priya & Deepak Kulkarni",
                    houseOrFlat = "House 12, Sector 5",
                    phone = "9422004455",
                    targetAmount = 3100.0,
                    paidAmount = 3100.0,
                    isPaid = true,
                    paymentMode = "UPI",
                    paymentDate = now - (oneDay * 1),
                    transactionRef = "UPI/260825/99120",
                    notes = "Modak Prasad sponsor"
                ),
                DonorCandidate(
                    name = "Vikas Malhotra",
                    houseOrFlat = "Flat C-301, Royal Palms",
                    phone = "9988776655",
                    targetAmount = 2100.0,
                    paidAmount = 0.0,
                    isPaid = false,
                    paymentMode = "UPI",
                    notes = "Will pay on weekend"
                ),
                DonorCandidate(
                    name = "Anand Joshi",
                    houseOrFlat = "Flat 102, Shanti Niwas",
                    phone = "9890123456",
                    targetAmount = 1500.0,
                    paidAmount = 0.0,
                    isPaid = false,
                    paymentMode = "Cash",
                    notes = "Follow up on Thursday"
                ),
                DonorCandidate(
                    name = "Kishore Bhai & Sons",
                    houseOrFlat = "Shop No. 4, Market Road",
                    phone = "9820019283",
                    targetAmount = 11000.0,
                    paidAmount = 11000.0,
                    isPaid = true,
                    paymentMode = "Bank Transfer",
                    paymentDate = now - (oneDay * 4),
                    transactionRef = "NEFT/HDFC/00912",
                    notes = "Pandal Flower Decoration Gold Sponsor"
                ),
                DonorCandidate(
                    name = "Dr. Shrikant Shinde",
                    houseOrFlat = "Bungalow 7, Sai Enclave",
                    phone = "9765432190",
                    targetAmount = 5000.0,
                    paidAmount = 0.0,
                    isPaid = false,
                    paymentMode = "UPI",
                    notes = "Pledged during general body meet"
                ),
                DonorCandidate(
                    name = "Meera & Sanjay Nair",
                    houseOrFlat = "Flat D-502, Gokul Heights",
                    phone = "9123456780",
                    targetAmount = 2100.0,
                    paidAmount = 2100.0,
                    isPaid = true,
                    paymentMode = "UPI",
                    paymentDate = now - (oneDay * 1),
                    transactionRef = "UPI/260825/11029",
                    notes = "Rangoli competition contributor"
                ),
                DonorCandidate(
                    name = "Mahesh Chordia",
                    houseOrFlat = "Flat A-101, Shivam Apts",
                    phone = "9821098765",
                    targetAmount = 1100.0,
                    paidAmount = 0.0,
                    isPaid = false,
                    paymentMode = "Cash",
                    notes = "Asked for evening visit"
                ),
                DonorCandidate(
                    name = "Sai Krupa Builders",
                    houseOrFlat = "Commercial Complex 2nd Floor",
                    phone = "9900112233",
                    targetAmount = 21000.0,
                    paidAmount = 21000.0,
                    isPaid = true,
                    paymentMode = "Cheque",
                    paymentDate = now - (oneDay * 5),
                    transactionRef = "CHQ#449102",
                    notes = "Main Idol & Sthapana Grand Sponsor"
                )
            )
            dao.insertAllDonors(initialDonors)

            // Initial Expenses
            val initialExpenses = listOf(
                ExpenseItem(
                    title = "Eco-Friendly Ganesh Idol (Advance)",
                    category = "Idol & Installation",
                    amount = 15000.0,
                    vendor = "Shree Murti Art Kendra",
                    paidBy = "Rameshwar Patil",
                    date = now - (oneDay * 6),
                    notes = "6ft Clay (Shadu Mati) Ganesh Idol"
                ),
                ExpenseItem(
                    title = "Pandal Structure & Water-Proof Shed",
                    category = "Pandal & Lighting",
                    amount = 28000.0,
                    vendor = "Gauri Mandap Decorators",
                    paidBy = "Amit Deshmukh",
                    date = now - (oneDay * 4),
                    notes = "Pandal setup with decorative fabric drape"
                ),
                ExpenseItem(
                    title = "Decorative LED Serial Lights & Focus",
                    category = "Pandal & Lighting",
                    amount = 8500.0,
                    vendor = "Om Sai Electricals",
                    paidBy = "Sunil Verma",
                    date = now - (oneDay * 3),
                    notes = "Pandal entrance gate lighting"
                ),
                ExpenseItem(
                    title = "Sound System & Mic Setup (10 Days)",
                    category = "Sound & Dhol",
                    amount = 12000.0,
                    vendor = "Sur Sangeet Sounds",
                    paidBy = "Rajesh Sharma",
                    date = now - (oneDay * 2),
                    notes = "Daily Puja sound & speaker set"
                ),
                ExpenseItem(
                    title = "Puja Items, Camphor, Incense & Hawan Kit",
                    category = "Puja Items",
                    amount = 4500.0,
                    vendor = "Ganpati Pooja Bhandar",
                    paidBy = "Priya Kulkarni",
                    date = now - (oneDay * 1),
                    notes = "All 10 days puja items"
                ),
                ExpenseItem(
                    title = "Prasad & Modak Ingredients (Sugar, Mawa, Coconut)",
                    category = "Prasad & Food",
                    amount = 6800.0,
                    vendor = "Mahalaxmi Sweets & Grocery",
                    paidBy = "Amit Deshmukh",
                    date = now,
                    notes = "1001 Modak Mahabhog preparation"
                )
            )
            dao.insertAllExpenses(initialExpenses)

            // Initial Festival Schedule
            val initialEvents = listOf(
                FestivalEvent(
                    dayNumber = 1,
                    title = "Ganesh Sthapana & Puja",
                    dateStr = "Aug 27, 2026",
                    timeStr = "09:30 AM - 12:30 PM",
                    location = "Asivanipalem Pandal",
                    description = "Welcoming Ganesha with prayer and inauguration.",
                    isCompleted = false
                ),
                FestivalEvent(
                    dayNumber = 1,
                    title = "Evening Maha Puja & Modak Distribution",
                    dateStr = "Aug 27, 2026",
                    timeStr = "07:30 PM - 09:00 PM",
                    location = "Asivanipalem Pandal",
                    description = "Community prayer with 108 oil deepaks.",
                    isCompleted = false
                ),
                FestivalEvent(
                    dayNumber = 3,
                    title = "Morning Chanting & Prayer Session",
                    dateStr = "Aug 29, 2026",
                    timeStr = "08:00 AM - 11:30 AM",
                    location = "Asivanipalem Stage",
                    description = "Group chanting session by community members.",
                    isCompleted = false
                ),
                FestivalEvent(
                    dayNumber = 5,
                    title = "Special Puja & Community Food Offering",
                    dateStr = "Aug 31, 2026",
                    timeStr = "05:00 PM - 08:30 PM",
                    location = "Asivanipalem Hall",
                    description = "Special food offerings followed by community Prasad.",
                    isCompleted = false
                ),
                FestivalEvent(
                    dayNumber = 7,
                    title = "Children's Drawing & Craft Contest",
                    dateStr = "Sep 02, 2026",
                    timeStr = "04:30 PM - 07:00 PM",
                    location = "Asivanipalem Community Hall",
                    description = "Prizes for best eco-friendly Ganesha craft & artwork.",
                    isCompleted = false
                ),
                FestivalEvent(
                    dayNumber = 8,
                    title = "Cultural Evening & Dhol Performance",
                    dateStr = "Sep 03, 2026",
                    timeStr = "07:00 PM - 10:30 PM",
                    location = "Asivanipalem Ground",
                    description = "Live traditional lezim & dhol tasha performance.",
                    isCompleted = false
                ),
                FestivalEvent(
                    dayNumber = 10,
                    title = "Grand Farewell Procession & Visarjan",
                    dateStr = "Sep 05, 2026",
                    timeStr = "02:00 PM Onwards",
                    location = "Asivanipalem to Lake",
                    description = "Grand farewell procession with music, flowers, and community celebration.",
                    isCompleted = false
                )
            )
            dao.insertAllEvents(initialEvents)

            // Initial Samagri Items
            val initialSamagri = listOf(
                SamagriItem(name = "Clay Eco-Ganesh Idol", quantity = "1 Unit", category = "Sthapana", isArranged = true, assignedTo = "Rameshwar"),
                SamagriItem(name = "Fresh Durva Grass Bundles", quantity = "50 Bundles", category = "Daily Puja", isArranged = true, assignedTo = "Sunil"),
                SamagriItem(name = "Red Hibiscus Flowers", quantity = "101 Flowers daily", category = "Daily Puja", isArranged = false, assignedTo = "Priya"),
                SamagriItem(name = "Pure Cow Ghee for Diya", quantity = "5 Litres", category = "Daily Puja", isArranged = true, assignedTo = "Deepak"),
                SamagriItem(name = "Modak Prasad Boxes (Printed)", quantity = "500 Boxes", category = "Maha Prasad", isArranged = false, assignedTo = "Amit"),
                SamagriItem(name = "Camphor & Incense Dhoop Cones", quantity = "2 Large Packs", category = "Daily Puja", isArranged = true, assignedTo = "Rajesh"),
                SamagriItem(name = "Sandalwood Paste & Turmeric", quantity = "1 Set", category = "Daily Puja", isArranged = true, assignedTo = "Kishore"),
                SamagriItem(name = "Flower Petals for Visarjan", quantity = "20 Kgs", category = "Visarjan", isArranged = false, assignedTo = "Volunteers Team")
            )
            dao.insertAllSamagri(initialSamagri)

            // Initial Authorized Committee Members (Pre-approved)
            val initialAdminRequests = listOf(
                AdminRequest(
                    requesterName = "Rameshwar Patil",
                    phone = "9823011223",
                    reason = "Committee Joint Secretary",
                    status = "APPROVED",
                    requestedAt = now - (oneDay * 5),
                    reviewedAt = now - (oneDay * 5)
                ),
                AdminRequest(
                    requesterName = "Sunil Verma",
                    phone = "9876543210",
                    reason = "Puja & Samagri Incharge",
                    status = "APPROVED",
                    requestedAt = now - (oneDay * 4),
                    reviewedAt = now - (oneDay * 4)
                ),
                AdminRequest(
                    requesterName = "Amit Deshmukh",
                    phone = "9988776655",
                    reason = "Prasad & Volunteer Lead",
                    status = "APPROVED",
                    requestedAt = now - (oneDay * 3),
                    reviewedAt = now - (oneDay * 3)
                ),
                AdminRequest(
                    requesterName = "Priya Kulkarni",
                    phone = "9422004455",
                    reason = "Accounts & Mahila Mandal Lead",
                    status = "APPROVED",
                    requestedAt = now - (oneDay * 2),
                    reviewedAt = now - (oneDay * 2)
                ),
                AdminRequest(
                    requesterName = "Rajesh Sharma",
                    phone = "9820019283",
                    reason = "Sound & Stage Coordinator",
                    status = "APPROVED",
                    requestedAt = now - (oneDay * 1),
                    reviewedAt = now - (oneDay * 1)
                )
            )
            dao.insertAllAdminRequests(initialAdminRequests)
        }
    }
}
