package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.DonorCandidate
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.NaturalAvatarBg
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalBorderSand
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalGreen
import com.example.ui.theme.NaturalGreenBg
import com.example.ui.theme.NaturalGreenBorder
import com.example.ui.theme.NaturalRed
import com.example.ui.theme.NaturalRedBg
import com.example.ui.theme.NaturalRedBorder
import com.example.ui.theme.NaturalSandPill
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold
import com.example.ui.viewmodel.ChandaFilter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChandaScreen(
    donors: List<DonorCandidate>,
    filter: ChandaFilter,
    searchQuery: String,
    isAdminMode: Boolean,
    onFilterChange: (ChandaFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onAddDonorClick: () -> Unit,
    onEditDonorClick: (DonorCandidate) -> Unit,
    onDeleteDonorClick: (DonorCandidate) -> Unit,
    onTogglePaidStatus: (DonorCandidate) -> Unit,
    onOpenReceipt: (DonorCandidate) -> Unit,
    onGenerateReminder: (DonorCandidate) -> String
) {
    val context = LocalContext.current
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    // Filter and search logic
    val filteredDonors = donors.filter { donor ->
        val matchesFilter = when (filter) {
            ChandaFilter.ALL -> true
            ChandaFilter.PAID -> donor.isPaid
            ChandaFilter.UNPAID -> !donor.isPaid
        }
        val matchesSearch = searchQuery.isBlank() ||
                donor.name.contains(searchQuery, ignoreCase = true) ||
                donor.houseOrFlat.contains(searchQuery, ignoreCase = true) ||
                donor.phone.contains(searchQuery, ignoreCase = true) ||
                donor.notes.contains(searchQuery, ignoreCase = true)

        matchesFilter && matchesSearch
    }

    val totalPaidAmount = donors.filter { it.isPaid }.sumOf { it.paidAmount }
    val totalPendingAmount = donors.filter { !it.isPaid }.sumOf { it.targetAmount }
    val paidCount = donors.count { it.isPaid }
    val unpaidCount = donors.count { !it.isPaid }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NaturalCreamBg)
            .testTag("chanda_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NaturalSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search devotee, flat, phone...", fontSize = 13.sp, color = NaturalTextMuted) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = OchreGold)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = NaturalTextMuted)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("chanda_search_bar"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Segmented Natural Pills: All, Paid, Unpaid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All Chip
                    val isAll = filter == ChandaFilter.ALL
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isAll) NaturalTerracotta else NaturalSandPill)
                            .then(if (!isAll) Modifier.border(1.dp, NaturalBorderSand, RoundedCornerShape(20.dp)) else Modifier)
                            .clickable { onFilterChange(ChandaFilter.ALL) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("tab_all_donors"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All (${donors.size})",
                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isAll) Color.White else EarthBrown
                        )
                    }

                    // Paid Chip
                    val isPaid = filter == ChandaFilter.PAID
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isPaid) NaturalTerracotta else NaturalSandPill)
                            .then(if (!isPaid) Modifier.border(1.dp, NaturalBorderSand, RoundedCornerShape(20.dp)) else Modifier)
                            .clickable { onFilterChange(ChandaFilter.PAID) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("tab_paid_donors"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Paid ($paidCount)",
                            fontWeight = if (isPaid) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isPaid) Color.White else EarthBrown
                        )
                    }

                    // Unpaid Chip
                    val isUnpaid = filter == ChandaFilter.UNPAID
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isUnpaid) NaturalTerracotta else NaturalSandPill)
                            .then(if (!isUnpaid) Modifier.border(1.dp, NaturalBorderSand, RoundedCornerShape(20.dp)) else Modifier)
                            .clickable { onFilterChange(ChandaFilter.UNPAID) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("tab_unpaid_donors"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Unpaid ($unpaidCount)",
                            fontWeight = if (isUnpaid) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isUnpaid) Color.White else EarthBrown
                        )
                    }
                }

                // Subtotal bar
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (filter) {
                            ChandaFilter.ALL -> "Total Devotees: ${donors.size}"
                            ChandaFilter.PAID -> "Total Collected: ${format.format(totalPaidAmount)}"
                            ChandaFilter.UNPAID -> "Pending Recovery: ${format.format(totalPendingAmount)}"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (filter) {
                            ChandaFilter.ALL -> EarthBrown
                            ChandaFilter.PAID -> NaturalGreen
                            ChandaFilter.UNPAID -> NaturalRed
                        }
                    )

                    if (isAdminMode) {
                        Text(
                            text = "Admin Mode Active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTerracotta
                        )
                    }
                }
            }

            // Donors List
            if (filteredDonors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Pending,
                            contentDescription = null,
                            tint = NaturalTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No devotees matching \"$searchQuery\"" else "No devotees in this list yet",
                            color = NaturalTextMuted,
                            fontSize = 14.sp
                        )
                        if (isAdminMode) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onAddDonorClick,
                                colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+ Add Devotee", color = NaturalCreamBg)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 10.dp, bottom = 90.dp)
                ) {
                    items(filteredDonors, key = { it.id }) { donor ->
                        DonorItemCard(
                            donor = donor,
                            isAdminMode = isAdminMode,
                            onTogglePaid = { onTogglePaidStatus(donor) },
                            onEdit = { onEditDonorClick(donor) },
                            onDelete = { onDeleteDonorClick(donor) },
                            onOpenReceipt = { onOpenReceipt(donor) },
                            onSendReminder = {
                                val reminderText = onGenerateReminder(donor)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reminderText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Send WhatsApp Reminder to ${donor.name}")
                                context.startActivity(shareIntent)
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button (Admin Only)
        if (isAdminMode) {
            FloatingActionButton(
                onClick = onAddDonorClick,
                containerColor = EarthBrown,
                contentColor = NaturalCreamBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 20.dp)
                    .testTag("add_donor_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Devotee", modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
fun DonorItemCard(
    donor: DonorCandidate,
    isAdminMode: Boolean,
    onTogglePaid: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onOpenReceipt: () -> Unit,
    onSendReminder: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateStr = if (donor.paymentDate != null) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(donor.paymentDate))
    } else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("donor_card_${donor.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalSurface),
        border = BorderStroke(1.dp, NaturalBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Main Top Row: Avatar, Name, Address, and Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar + Name & Address
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Devotee Avatar Initial
                    val initial = donor.name.take(1).uppercase(Locale.getDefault())
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(NaturalAvatarBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (initial.isNotBlank()) initial else "D",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTerracotta
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = donor.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (donor.houseOrFlat.isNotBlank()) {
                            Text(
                                text = donor.houseOrFlat,
                                fontSize = 11.sp,
                                color = NaturalTextMuted
                            )
                        } else if (donor.phone.isNotBlank()) {
                            Text(
                                text = donor.phone,
                                fontSize = 11.sp,
                                color = NaturalTextMuted
                            )
                        }
                    }
                }

                // Amount & Status Badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = format.format(if (donor.isPaid) donor.paidAmount else donor.targetAmount),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTerracotta
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (donor.isPaid) NaturalGreenBg else NaturalRedBg,
                        border = BorderStroke(1.dp, if (donor.isPaid) NaturalGreenBorder else NaturalRedBorder),
                        modifier = Modifier.testTag("status_badge_${donor.id}")
                    ) {
                        Text(
                            text = if (donor.isPaid) "PAID" else "UNPAID",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (donor.isPaid) NaturalGreen else NaturalRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (donor.notes.isNotBlank() || dateStr != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (donor.notes.isNotBlank()) {
                        Text(
                            text = "✨ ${donor.notes}",
                            fontSize = 11.sp,
                            color = NaturalTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (dateStr != null) {
                        Text(
                            text = dateStr,
                            fontSize = 10.sp,
                            color = NaturalTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = NaturalBorderLight, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Devotee Actions
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (donor.isPaid) {
                        // Digital Receipt
                        OutlinedButton(
                            onClick = onOpenReceipt,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp).testTag("receipt_button_${donor.id}"),
                            border = BorderStroke(1.dp, NaturalBorderSand),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EarthBrown)
                        ) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = EarthBrown, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Receipt", fontSize = 11.sp, color = EarthBrown, fontWeight = FontWeight.Medium)
                        }
                    } else {
                        // WhatsApp Reminder
                        OutlinedButton(
                            onClick = onSendReminder,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp).testTag("remind_button_${donor.id}"),
                            border = BorderStroke(1.dp, NaturalGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NaturalGreen)
                        ) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = NaturalGreen, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Remind", fontSize = 11.sp, color = NaturalGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Admin Controls (Quick toggle payment, Edit, Delete)
                if (isAdminMode) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Admin Quick Toggle Paid Status button
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (donor.isPaid) NaturalSandPill else NaturalGreenBg,
                            border = BorderStroke(1.dp, if (donor.isPaid) NaturalBorderSand else NaturalGreenBorder),
                            modifier = Modifier
                                .clickable { onTogglePaid() }
                                .testTag("admin_toggle_paid_${donor.id}")
                        ) {
                            Text(
                                text = if (donor.isPaid) "Mark Unpaid" else "Mark Paid",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (donor.isPaid) EarthBrown else NaturalGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(28.dp).testTag("edit_donor_${donor.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = NaturalTextMuted, modifier = Modifier.size(15.dp))
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp).testTag("delete_donor_${donor.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = NaturalTextMuted, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }
}
