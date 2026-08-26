package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.DonorCandidate
import com.example.data.entity.ExpenseItem
import com.example.data.entity.FestivalEvent
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.EarthBrownDark
import com.example.ui.theme.NaturalAvatarBg
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalBorderSand
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalGreen
import com.example.ui.theme.NaturalGreenBg
import com.example.ui.theme.NaturalRed
import com.example.ui.theme.NaturalRedBg
import com.example.ui.theme.NaturalSandPill
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTerracottaDark
import com.example.ui.theme.NaturalTerracottaLight
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold
import com.example.ui.viewmodel.NavigationTab
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    donors: List<DonorCandidate>,
    expenses: List<ExpenseItem>,
    events: List<FestivalEvent>,
    targetGoal: Double,
    committeeName: String,
    festivalYear: String,
    venue: String,
    isAdminMode: Boolean,
    onNavigateToTab: (NavigationTab) -> Unit,
    onAddDonorClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onOpenReceipt: (DonorCandidate) -> Unit,
    onOpenAartiLyrics: () -> Unit,
    onShareReport: () -> Unit,
    onAdminAuthClick: () -> Unit = {}
) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    val totalCollected = donors.filter { it.isPaid }.sumOf { it.paidAmount }
    val totalPledged = donors.sumOf { it.targetAmount }
    val totalPending = (totalPledged - totalCollected).coerceAtLeast(0.0)
    val totalExpense = expenses.sumOf { it.amount }
    val netBalance = totalCollected - totalExpense

    val paidDonorsCount = donors.count { it.isPaid }
    val unpaidDonorsCount = donors.count { !it.isPaid }
    val totalDonorsCount = donors.size

    val progressPercent = if (targetGoal > 0) ((totalCollected / targetGoal) * 100).toInt() else 0
    val progressFraction = if (targetGoal > 0) (totalCollected / targetGoal).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progressFraction, label = "progress")

    // Payment mode breakdown
    val upiCollected = donors.filter { it.isPaid && it.paymentMode.equals("UPI", ignoreCase = true) }.sumOf { it.paidAmount }
    val cashCollected = donors.filter { it.isPaid && it.paymentMode.equals("Cash", ignoreCase = true) }.sumOf { it.paidAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NaturalCreamBg)
            .testTag("dashboard_screen"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp)
    ) {
        // Hero Festive Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_ganesh_banner),
                    contentDescription = "Ganesh Celebration Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    EarthBrownDark.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Ganesh Utsav $festivalYear",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "🚩 $committeeName",
                        color = NaturalSandPill,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "📍 $venue",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Global Fund Hero Card (Natural Tones Theme)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("global_fund_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NaturalTerracotta),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GLOBAL FUND COLLECTED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${progressPercent}% Target",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Big Collected Amount Display
                    Text(
                        text = format.format(totalCollected),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sub-metrics: Expenses & Balance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "TOTAL EXPENSES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.75f),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = format.format(totalExpense),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "NET BALANCE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.75f),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = format.format(netBalance),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Translucent Progress Bar
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )
                }
            }
        }

        // Quick Action Shortcuts Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isAdminMode) {
                    // Add Collection Button
                    Button(
                        onClick = onAddDonorClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("quick_add_chanda_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = NaturalCreamBg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Add Devotee",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalCreamBg
                        )
                    }

                    // Record Expense Button
                    OutlinedButton(
                        onClick = onAddExpenseClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("quick_add_expense_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NaturalBorderSand),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = NaturalSurface)
                    ) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = OchreGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Record Bill",
                            fontSize = 12.sp,
                            color = EarthBrown,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Devotee Read-Only Navigation Buttons
                    Button(
                        onClick = { onNavigateToTab(NavigationTab.CHANDA) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("quick_view_collection_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp), tint = NaturalCreamBg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View Collection",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalCreamBg
                        )
                    }

                    OutlinedButton(
                        onClick = { onNavigateToTab(NavigationTab.EXPENSES) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("quick_view_expenses_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NaturalBorderSand),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = NaturalSurface)
                    ) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = OchreGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View Expenses",
                            fontSize = 12.sp,
                            color = EarthBrown,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Donor Statistics & Breakdown Cards
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Collection Summary & Channels",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EarthBrown,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Paid vs Pending Donors
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToTab(NavigationTab.CHANDA) },
                        colors = CardDefaults.cardColors(containerColor = NaturalSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NaturalBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Devotees Status", fontSize = 10.sp, color = NaturalTextMuted, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "$paidDonorsCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NaturalGreen)
                                    Text(text = "Paid", fontSize = 10.sp, color = NaturalGreen, fontWeight = FontWeight.Medium)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "$unpaidDonorsCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NaturalRed)
                                    Text(text = "Pending", fontSize = 10.sp, color = NaturalRed, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    // Collection Channel Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = NaturalSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NaturalBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Collection", fontSize = 10.sp, color = NaturalTextMuted, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = format.format(upiCollected),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTerracotta
                            )
                            Text(text = "Cash: ${format.format(cashCollected)}", fontSize = 10.sp, color = NaturalTextMuted)
                        }
                    }
                }
            }
        }

        // Top Devotees / Hall of Devotion
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = OchreGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Top Devotee Contributors",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EarthBrown
                        )
                    }

                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTerracotta,
                        modifier = Modifier
                            .clickable { onNavigateToTab(NavigationTab.CHANDA) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val topDonors = donors.filter { it.isPaid }.sortedByDescending { it.paidAmount }.take(4)
                if (topDonors.isEmpty()) {
                    Text(
                        text = "No paid donations recorded yet.",
                        fontSize = 12.sp,
                        color = NaturalTextMuted,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    topDonors.forEachIndexed { index, donor ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { onOpenReceipt(donor) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NaturalSurface),
                            border = BorderStroke(1.dp, NaturalBorderLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                if (index == 0) NaturalSandPill else NaturalAvatarBg,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "#${index + 1}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (index == 0) OchreGold else EarthBrown
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = donor.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EarthBrown
                                        )
                                        if (donor.houseOrFlat.isNotBlank()) {
                                            Text(
                                                text = donor.houseOrFlat,
                                                fontSize = 11.sp,
                                                color = NaturalTextMuted
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = format.format(donor.paidAmount),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalTerracotta
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = NaturalGreenBg
                                    ) {
                                        Text(
                                            text = "Receipt",
                                            fontSize = 9.sp,
                                            color = NaturalGreen,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Festival Schedule & Aarti Preview
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Festival Highlights",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EarthBrown
                    )
                    Text(
                        text = "Full Timetable",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTerracotta,
                        modifier = Modifier
                            .clickable { onNavigateToTab(NavigationTab.AARTI) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                events.take(2).forEach { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NaturalSurface),
                        border = BorderStroke(1.dp, NaturalBorderLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(NaturalSandPill, RoundedCornerShape(10.dp))
                                    .border(1.dp, NaturalBorderSand, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "DAY",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OchreGold
                                    )
                                    Text(
                                        text = "${event.dayNumber}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EarthBrown
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EarthBrown
                                )
                                Text(
                                    text = "⏰ ${event.timeStr} • 📍 ${event.location}",
                                    fontSize = 11.sp,
                                    color = NaturalTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Discreet Committee Admin Access Link (Separate from standard member view)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NaturalSandPill,
                    border = BorderStroke(1.dp, NaturalBorderSand),
                    modifier = Modifier
                        .clickable { onAdminAuthClick() }
                        .testTag("admin_portal_discreet_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAdminMode) "🛡️ Admin Access Active (Tap to Manage)" else "🔒 Aura Boys Committee Admin Portal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EarthBrown
                        )
                    }
                }
            }
        }
    }
}

