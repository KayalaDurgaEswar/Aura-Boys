package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FestivalEvent
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.NaturalAvatarBg
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalBorderSand
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalGreen
import com.example.ui.theme.NaturalGreenBg
import com.example.ui.theme.NaturalGreenBorder
import com.example.ui.theme.NaturalSandPill
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold

@Composable
fun ScheduleScreen(
    events: List<FestivalEvent>,
    isAdminMode: Boolean,
    onToggleEventStatus: (FestivalEvent) -> Unit,
    onAddEventClick: () -> Unit = {},
    onEditEventClick: (FestivalEvent) -> Unit = {},
    onDeleteEventClick: (FestivalEvent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NaturalCreamBg)
            .testTag("schedule_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 12.dp, bottom = 140.dp)
        ) {
            // Daily Routine Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("daily_schedule_banner_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NaturalSurface),
                    border = BorderStroke(1.dp, NaturalBorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(NaturalSandPill, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = OchreGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "DAILY FESTIVAL TIMINGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaturalTextMuted)
                                    Text(text = "Daily Routine & Prayers", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EarthBrown)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NaturalSandPill,
                                border = BorderStroke(1.dp, NaturalBorderSand)
                            ) {
                                Text(
                                    text = "${events.count { it.isCompleted }} / ${events.size} Completed",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EarthBrown,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = NaturalSandPill,
                                border = BorderStroke(1.dp, NaturalBorderSand)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("🌅 Morning Prayers", fontSize = 10.sp, color = EarthBrown, fontWeight = FontWeight.Bold)
                                    Text("08:00 AM Daily", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NaturalTerracotta)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = NaturalSandPill,
                                border = BorderStroke(1.dp, NaturalBorderSand)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("🪔 Evening Prayers", fontSize = 10.sp, color = EarthBrown, fontWeight = FontWeight.Bold)
                                    Text("07:30 PM Daily", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NaturalTerracotta)
                                }
                            }
                        }
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Festival Timetable & Events",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )
                    if (isAdminMode) {
                        Text(
                            text = "+ Add Event",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTerracotta,
                            modifier = Modifier
                                .clickable { onAddEventClick() }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Events List
            if (events.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No events scheduled yet.",
                            fontSize = 13.sp,
                            color = NaturalTextMuted
                        )
                    }
                }
            } else {
                items(events, key = { it.id }) { event ->
                    EventItemCard(
                        event = event,
                        isAdminMode = isAdminMode,
                        onToggleStatus = { onToggleEventStatus(event) },
                        onEdit = { onEditEventClick(event) },
                        onDelete = { onDeleteEventClick(event) }
                    )
                }
            }
        }

        // FAB to Add Event (Admin Only)
        if (isAdminMode) {
            FloatingActionButton(
                onClick = onAddEventClick,
                containerColor = EarthBrown,
                contentColor = NaturalCreamBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 20.dp)
                    .testTag("add_event_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event", modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
fun EventItemCard(
    event: FestivalEvent,
    isAdminMode: Boolean,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalSurface),
        border = BorderStroke(
            1.dp,
            if (event.isCompleted) NaturalGreenBorder else NaturalBorderLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Day Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (event.isCompleted) NaturalGreenBg else NaturalSandPill,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DAY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (event.isCompleted) NaturalGreen else NaturalTextMuted
                    )
                    Text(
                        text = "${event.dayNumber}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (event.isCompleted) NaturalGreen else NaturalTerracotta
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = NaturalTextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${event.dateStr} • ${event.timeStr}", fontSize = 11.sp, color = NaturalTextSecondary)
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 1.dp)) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = OchreGold, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = event.location, fontSize = 11.sp, color = NaturalTextMuted)
                }

                if (event.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        fontSize = 11.sp,
                        color = NaturalTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            // Admin Controls & Completion
            if (isAdminMode) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp).testTag("edit_event_${event.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = NaturalTextMuted, modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).testTag("delete_event_${event.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = NaturalTextMuted, modifier = Modifier.size(14.dp))
                    }

                    Checkbox(
                        checked = event.isCompleted,
                        onCheckedChange = { onToggleStatus() },
                        colors = CheckboxDefaults.colors(checkedColor = NaturalGreen),
                        modifier = Modifier.testTag("event_checkbox_${event.id}")
                    )
                }
            } else {
                if (event.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = NaturalGreen,
                        modifier = Modifier.size(20.dp).padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
