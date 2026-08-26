package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddDonorDialog
import com.example.ui.components.AddEventDialog
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.AdminAuthDialog
import com.example.ui.components.AdminRequestsDialog
import com.example.ui.components.FestiveTopAppBar
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.RequestAdminDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.ShareReportDialog
import com.example.ui.screens.AddSamagriDialog
import com.example.ui.screens.ChandaScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpenseScreen
import com.example.ui.screens.SamagriScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.GaneshUtsavTheme
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalSandPill
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.viewmodel.GaneshViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    private val viewModel: GaneshViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GaneshUtsavTheme {
                GaneshApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GaneshApp(viewModel: GaneshViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val donors by viewModel.allDonors.collectAsStateWithLifecycle()
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val samagriList by viewModel.allSamagri.collectAsStateWithLifecycle()
    val adminRequests by viewModel.allAdminRequests.collectAsStateWithLifecycle()
    val approvedAdminRequests by viewModel.approvedAdminRequests.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingAdminRequestsCount.collectAsStateWithLifecycle()
    val cloudSyncState by viewModel.cloudSyncState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val committeeName = viewModel.getSettingValue("committee_name", "Aura Boys")
    val festivalYear = viewModel.getSettingValue("festival_year", "2026")
    val targetGoal = viewModel.getSettingValue("target_goal", "125000").toDoubleOrNull() ?: 125000.0
    val presidentName = viewModel.getSettingValue("president_name", "Nani (Main Admin)")
    val venue = viewModel.getSettingValue("venue", "Asivanipalem")

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        containerColor = NaturalCreamBg,
        topBar = {
            FestiveTopAppBar(
                committeeName = committeeName,
                festivalYear = festivalYear,
                isAdminMode = uiState.isAdminMode,
                isMainAdmin = uiState.isMainAdmin,
                currentAdminName = uiState.currentAdminName,
                pendingAdminRequestsCount = pendingCount,
                cloudSyncState = cloudSyncState,
                onSyncClick = { viewModel.syncNow() },
                onNotificationsClick = { viewModel.openAdminRequestsListDialog() },
                onAdminAuthClick = { viewModel.openAdminAuthDialog() },
                onShareReportClick = { viewModel.openShareReportDialog() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NaturalSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(width = (0.5).dp, color = NaturalBorderLight)
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = uiState.selectedTab == NavigationTab.DASHBOARD,
                    onClick = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                    icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NaturalTerracotta,
                        selectedTextColor = NaturalTerracotta,
                        indicatorColor = NaturalSandPill,
                        unselectedIconColor = EarthBrown.copy(alpha = 0.5f),
                        unselectedTextColor = EarthBrown.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == NavigationTab.CHANDA,
                    onClick = { viewModel.selectTab(NavigationTab.CHANDA) },
                    icon = { Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = "Collection") },
                    label = { Text("Collection", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NaturalTerracotta,
                        selectedTextColor = NaturalTerracotta,
                        indicatorColor = NaturalSandPill,
                        unselectedIconColor = EarthBrown.copy(alpha = 0.5f),
                        unselectedTextColor = EarthBrown.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_chanda")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == NavigationTab.EXPENSES,
                    onClick = { viewModel.selectTab(NavigationTab.EXPENSES) },
                    icon = { Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Expenses") },
                    label = { Text("Expenses", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NaturalTerracotta,
                        selectedTextColor = NaturalTerracotta,
                        indicatorColor = NaturalSandPill,
                        unselectedIconColor = EarthBrown.copy(alpha = 0.5f),
                        unselectedTextColor = EarthBrown.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_expenses")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == NavigationTab.AARTI,
                    onClick = { viewModel.selectTab(NavigationTab.AARTI) },
                    icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Timetable") },
                    label = { Text("Timetable", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NaturalTerracotta,
                        selectedTextColor = NaturalTerracotta,
                        indicatorColor = NaturalSandPill,
                        unselectedIconColor = EarthBrown.copy(alpha = 0.5f),
                        unselectedTextColor = EarthBrown.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_aarti")
                )

                NavigationBarItem(
                    selected = uiState.selectedTab == NavigationTab.SAMAGRI,
                    onClick = { viewModel.selectTab(NavigationTab.SAMAGRI) },
                    icon = { Icon(imageVector = Icons.Default.Checklist, contentDescription = "Items") },
                    label = { Text("Items", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NaturalTerracotta,
                        selectedTextColor = NaturalTerracotta,
                        indicatorColor = NaturalSandPill,
                        unselectedIconColor = EarthBrown.copy(alpha = 0.5f),
                        unselectedTextColor = EarthBrown.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_samagri")
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                NavigationTab.DASHBOARD -> {
                    DashboardScreen(
                        donors = donors,
                        expenses = expenses,
                        events = events,
                        targetGoal = targetGoal,
                        committeeName = committeeName,
                        festivalYear = festivalYear,
                        venue = venue,
                        isAdminMode = uiState.isAdminMode,
                        onNavigateToTab = { viewModel.selectTab(it) },
                        onAddDonorClick = { viewModel.openAddDonorDialog() },
                        onAddExpenseClick = { viewModel.openAddExpenseDialog() },
                        onOpenReceipt = { viewModel.openReceiptDialog(it) },
                        onOpenAartiLyrics = { viewModel.selectTab(NavigationTab.AARTI) },
                        onShareReport = { viewModel.openShareReportDialog() },
                        onAdminAuthClick = { viewModel.openAdminAuthDialog() }
                    )
                }
                NavigationTab.CHANDA -> {
                    ChandaScreen(
                        donors = donors,
                        filter = uiState.chandaFilter,
                        searchQuery = uiState.chandaSearchQuery,
                        isAdminMode = uiState.isAdminMode,
                        onFilterChange = { viewModel.setChandaFilter(it) },
                        onSearchChange = { viewModel.setChandaSearchQuery(it) },
                        onAddDonorClick = { viewModel.openAddDonorDialog() },
                        onEditDonorClick = { viewModel.openAddDonorDialog(it) },
                        onDeleteDonorClick = { viewModel.deleteDonor(it) },
                        onTogglePaidStatus = { viewModel.quickTogglePayment(it) },
                        onOpenReceipt = { viewModel.openReceiptDialog(it) },
                        onGenerateReminder = { viewModel.generateDonorReminder(it) }
                    )
                }
                NavigationTab.EXPENSES -> {
                    ExpenseScreen(
                        expenses = expenses,
                        totalChandaCollected = donors.filter { it.isPaid }.sumOf { it.paidAmount },
                        selectedCategory = uiState.expenseCategoryFilter,
                        searchQuery = uiState.expenseSearchQuery,
                        isAdminMode = uiState.isAdminMode,
                        onCategorySelected = { viewModel.setExpenseCategoryFilter(it) },
                        onSearchChange = { viewModel.setExpenseSearchQuery(it) },
                        onAddExpenseClick = { viewModel.openAddExpenseDialog() },
                        onEditExpenseClick = { viewModel.openAddExpenseDialog(it) },
                        onDeleteExpenseClick = { viewModel.deleteExpense(it) }
                    )
                }
                NavigationTab.AARTI -> {
                    ScheduleScreen(
                        events = events,
                        isAdminMode = uiState.isAdminMode,
                        onToggleEventStatus = { viewModel.toggleEventStatus(it) },
                        onAddEventClick = { viewModel.openAddEventDialog() },
                        onEditEventClick = { viewModel.openAddEventDialog(it) },
                        onDeleteEventClick = { viewModel.deleteEvent(it) }
                    )
                }
                NavigationTab.SAMAGRI -> {
                    SamagriScreen(
                        samagriList = samagriList,
                        isAdminMode = uiState.isAdminMode,
                        onToggleStatus = { viewModel.toggleSamagri(it) },
                        onDelete = { viewModel.deleteSamagri(it) },
                        onAddSamagriClick = { viewModel.openAddSamagriDialog() }
                    )
                }
            }
        }
    }

    // Dialogs
    if (uiState.selectedDonorForReceipt != null) {
        ReceiptDialog(
            donor = uiState.selectedDonorForReceipt!!,
            committeeName = committeeName,
            presidentName = presidentName,
            festivalYear = festivalYear,
            onDismiss = { viewModel.closeReceiptDialog() },
            onCopied = { viewModel.showSnackbar("Receipt text copied to clipboard 📋") }
        )
    }

    if (uiState.isAddDonorDialogOpen) {
        AddDonorDialog(
            donorToEdit = uiState.editingDonor,
            isAdminMode = uiState.isAdminMode,
            onDismiss = { viewModel.closeAddDonorDialog() },
            onSave = { name, flat, phone, targetAmount, isPaid, paidAmount, paymentMode, notes, txnRef ->
                viewModel.saveDonor(
                    id = uiState.editingDonor?.id ?: 0L,
                    name = name,
                    houseOrFlat = flat,
                    phone = phone,
                    targetAmount = targetAmount,
                    isPaid = isPaid,
                    paidAmount = paidAmount,
                    paymentMode = paymentMode,
                    notes = notes,
                    transactionRef = txnRef
                )
            }
        )
    }

    if (uiState.isAddExpenseDialogOpen) {
        AddExpenseDialog(
            expenseToEdit = uiState.editingExpense,
            onDismiss = { viewModel.closeAddExpenseDialog() },
            onSave = { title, category, amount, vendor, paidBy, notes ->
                viewModel.saveExpense(
                    id = uiState.editingExpense?.id ?: 0L,
                    title = title,
                    category = category,
                    amount = amount,
                    vendor = vendor,
                    paidBy = paidBy,
                    notes = notes
                )
            }
        )
    }

    if (uiState.isAddEventDialogOpen) {
        AddEventDialog(
            eventToEdit = uiState.editingEvent,
            onDismiss = { viewModel.closeAddEventDialog() },
            onSave = { day, title, time, date, loc, desc ->
                viewModel.saveEvent(
                    id = uiState.editingEvent?.id ?: 0L,
                    dayNumber = day,
                    title = title,
                    timeStr = time,
                    dateStr = date,
                    location = loc,
                    description = desc
                )
            }
        )
    }

    if (uiState.isAddSamagriDialogOpen) {
        AddSamagriDialog(
            onDismiss = { viewModel.closeAddSamagriDialog() },
            onSave = { name, quantity, category, assignedTo ->
                viewModel.addSamagri(name, quantity, category, assignedTo)
            }
        )
    }

    // Admin Auth Dialog (Main Admin & Authorized Member Admin login)
    if (uiState.isAdminAuthDialogOpen) {
        AdminAuthDialog(
            isAdminMode = uiState.isAdminMode,
            isMainAdmin = uiState.isMainAdmin,
            currentAdminName = uiState.currentAdminName,
            approvedMembers = approvedAdminRequests,
            onLoginMainAdmin = { user, pass -> viewModel.loginAsMainAdmin(user, pass) },
            onLoginAuthorizedAdmin = { name -> viewModel.loginAsAuthorizedAdmin(name) },
            onRequestAdminClick = { viewModel.openRequestAdminDialog() },
            onLogoutAdmin = { viewModel.logoutAdmin() },
            onDismiss = { viewModel.closeAdminAuthDialog() }
        )
    }

    // Request Admin Access Dialog (Submitted by Member)
    if (uiState.isRequestAdminDialogOpen) {
        RequestAdminDialog(
            onRequestSubmitted = { name, phone, reason ->
                viewModel.requestAdminAccess(name, phone, reason)
            },
            onDismiss = { viewModel.closeRequestAdminDialog() }
        )
    }

    // Admin Requests Management Dialog (Opened from Notification Bell Icon)
    if (uiState.isAdminRequestsListDialogOpen) {
        AdminRequestsDialog(
            allRequests = adminRequests,
            onApprove = { id -> viewModel.approveAdminRequest(id) },
            onReject = { id -> viewModel.rejectAdminRequest(id) },
            onDelete = { req -> viewModel.deleteAdminRequest(req) },
            onAddDirectAdmin = { name, phone, role -> viewModel.addDirectAuthorizedAdmin(name, phone, role) },
            onDismiss = { viewModel.closeAdminRequestsListDialog() }
        )
    }

    if (uiState.isSettingsDialogOpen) {
        SettingsDialog(
            committeeName = committeeName,
            festivalYear = festivalYear,
            targetGoal = targetGoal.toInt().toString(),
            upiId = "",
            upiName = "",
            presidentName = presidentName,
            adminPin = "nani5002",
            onDismiss = { viewModel.closeSettingsDialog() },
            onSave = { cName, year, goal, uId, uName, pName, _ ->
                viewModel.saveSetting("committee_name", cName)
                viewModel.saveSetting("festival_year", year)
                viewModel.saveSetting("target_goal", goal)
                viewModel.saveSetting("upi_id", uId)
                viewModel.saveSetting("upi_name", uName)
                viewModel.saveSetting("president_name", pName)
            }
        )
    }

    if (uiState.isShareReportDialogOpen) {
        ShareReportDialog(
            reportText = viewModel.generateCommunityReport(),
            onDismiss = { viewModel.closeShareReportDialog() },
            onCopied = { viewModel.showSnackbar("Festival report copied to clipboard 📋") }
        )
    }
}
