package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.AdminRequest
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalBorderSand
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalGreen
import com.example.ui.theme.NaturalRed
import com.example.ui.theme.NaturalSandPill
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.mutableStateOf

@Composable
fun AdminRequestsDialog(
    allRequests: List<AdminRequest>,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onDelete: (AdminRequest) -> Unit,
    onAddDirectAdmin: (String, String, String) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDirectField by remember { mutableStateOf(false) }
    var newMemberName by remember { mutableStateOf("") }
    var newMemberPhone by remember { mutableStateOf("") }
    var newMemberRole by remember { mutableStateOf("") }

    val pendingRequests = allRequests.filter { it.status == "PENDING" }
    val approvedRequests = allRequests.filter { it.status == "APPROVED" }
    val rejectedRequests = allRequests.filter { it.status == "REJECTED" }

    val displayedRequests = when (selectedTab) {
        0 -> pendingRequests
        1 -> approvedRequests
        else -> rejectedRequests
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("admin_requests_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NaturalSurface),
            border = BorderStroke(1.dp, NaturalBorderLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NaturalSandPill)
                                .border(1.dp, NaturalBorderSand, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = OchreGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Authorizations",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EarthBrown
                            )
                            Text(
                                text = "${pendingRequests.size} Pending Requests",
                                fontSize = 11.sp,
                                color = if (pendingRequests.isNotEmpty()) NaturalTerracotta else NaturalTextSecondary
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_requests_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NaturalTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = NaturalSandPill,
                    contentColor = EarthBrown,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NaturalTerracotta
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Pending (${pendingRequests.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Approved (${approvedRequests.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Rejected (${rejectedRequests.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Direct Add Authorized Member in Approved Tab
                if (selectedTab == 1) {
                    if (!showAddDirectField) {
                        OutlinedButton(
                            onClick = { showAddDirectField = true },
                            modifier = Modifier.fillMaxWidth().testTag("show_add_direct_admin_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, NaturalBorderSand)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = EarthBrown)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Add Authorized Admin Member", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EarthBrown)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NaturalSandPill),
                            border = BorderStroke(1.dp, NaturalBorderSand)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Add Member as Authorized Admin", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EarthBrown)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = newMemberName,
                                    onValueChange = { newMemberName = it },
                                    label = { Text("Member Name *", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. Anand Joshi", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NaturalTerracotta,
                                        unfocusedBorderColor = NaturalBorderSand
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = newMemberPhone,
                                    onValueChange = { newMemberPhone = it },
                                    label = { Text("Phone Number", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. 9820019283", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NaturalTerracotta,
                                        unfocusedBorderColor = NaturalBorderSand
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = newMemberRole,
                                    onValueChange = { newMemberRole = it },
                                    label = { Text("Role / Committee Designation", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. Stage & Dhol Coordinator", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NaturalTerracotta,
                                        unfocusedBorderColor = NaturalBorderSand
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (newMemberName.isNotBlank()) {
                                                onAddDirectAdmin(
                                                    newMemberName.trim(),
                                                    newMemberPhone.trim(),
                                                    newMemberRole.trim().ifBlank { "Authorized Admin" }
                                                )
                                                newMemberName = ""
                                                newMemberPhone = ""
                                                newMemberRole = ""
                                                showAddDirectField = false
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Save Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NaturalCreamBg)
                                    }
                                    OutlinedButton(
                                        onClick = { showAddDirectField = false },
                                        modifier = Modifier.height(36.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Cancel", fontSize = 11.sp, color = NaturalTextMuted)
                                    }
                                }
                            }
                        }
                    }
                }

                if (displayedRequests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (selectedTab) {
                                0 -> "No pending admin requests."
                                1 -> "No approved member admins."
                                else -> "No rejected requests."
                            },
                            color = NaturalTextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayedRequests, key = { it.id }) { request ->
                            RequestItemCard(
                                request = request,
                                onApprove = { onApprove(request.id) },
                                onReject = { onReject(request.id) },
                                onDelete = { onDelete(request) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestItemCard(
    request: AdminRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(request.requestedAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("request_item_${request.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalCreamBg),
        border = BorderStroke(1.dp, NaturalBorderSand),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = EarthBrown,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = request.requesterName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EarthBrown
                    )
                }

                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = NaturalTextMuted
                )
            }

            if (request.phone.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = OchreGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = request.phone,
                        fontSize = 11.sp,
                        color = NaturalTextSecondary
                    )
                }
            }

            if (request.reason.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: ${request.reason}",
                    fontSize = 11.sp,
                    color = NaturalTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions depending on status
            if (request.status == "PENDING") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("approve_request_${request.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("reject_request_${request.id}"),
                        border = BorderStroke(1.dp, NaturalRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = NaturalRed)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NaturalRed)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (request.status == "APPROVED") "Status: Approved Admin" else "Status: Rejected",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (request.status == "APPROVED") NaturalGreen else NaturalRed
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(26.dp).testTag("delete_request_${request.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Record",
                            tint = NaturalTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
