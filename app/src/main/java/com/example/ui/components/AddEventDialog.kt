package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.FestivalEvent
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalBorderSand
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalRed
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold

@Composable
fun AddEventDialog(
    eventToEdit: FestivalEvent? = null,
    onDismiss: () -> Unit,
    onSave: (dayNumber: Int, title: String, timeStr: String, dateStr: String, location: String, description: String) -> Unit
) {
    var dayNumberStr by remember { mutableStateOf(eventToEdit?.dayNumber?.toString() ?: "1") }
    var title by remember { mutableStateOf(eventToEdit?.title ?: "") }
    var timeStr by remember { mutableStateOf(eventToEdit?.timeStr ?: "07:00 PM") }
    var dateStr by remember { mutableStateOf(eventToEdit?.dateStr ?: "Day 1") }
    var location by remember { mutableStateOf(eventToEdit?.location ?: "Asivanipalem Pandal") }
    var description by remember { mutableStateOf(eventToEdit?.description ?: "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("add_event_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NaturalSurface),
            border = BorderStroke(1.dp, NaturalBorderLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = OchreGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (eventToEdit == null) "Add Timetable Event" else "Edit Timetable Event",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_add_event_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NaturalTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Day Number
                OutlinedTextField(
                    value = dayNumberStr,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) dayNumberStr = it
                    },
                    label = { Text("Day Number (e.g. 1, 2, 3...)") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = OchreGold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_day_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Event Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (errorMsg != null) errorMsg = null
                    },
                    label = { Text("Event Title *") },
                    placeholder = { Text("e.g. Maha Ganapati Puja & Laddu Auction") },
                    leadingIcon = { Icon(Icons.Default.Event, contentDescription = null, tint = OchreGold) },
                    singleLine = true,
                    isError = errorMsg != null && title.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("event_title_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Time String & Date String Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = timeStr,
                        onValueChange = { timeStr = it },
                        label = { Text("Time") },
                        placeholder = { Text("07:30 PM") },
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = OchreGold) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("event_time_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NaturalTerracotta,
                            unfocusedBorderColor = NaturalBorderSand,
                            focusedTextColor = EarthBrown,
                            unfocusedTextColor = EarthBrown
                        )
                    )

                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("Date / Label") },
                        placeholder = { Text("Sep 17") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("event_date_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NaturalTerracotta,
                            unfocusedBorderColor = NaturalBorderSand,
                            focusedTextColor = EarthBrown,
                            unfocusedTextColor = EarthBrown
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    placeholder = { Text("Asivanipalem Pandal") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = OchreGold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_location_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Details (Optional)") },
                    placeholder = { Text("Program schedule, chief guests, special items...") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = OchreGold) },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("event_description_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    )
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = NaturalRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMsg = "Please enter an event title"
                            return@Button
                        }
                        val dayNum = dayNumberStr.toIntOrNull() ?: 1
                        onSave(
                            dayNum,
                            title.trim(),
                            timeStr.trim().ifBlank { "07:00 PM" },
                            dateStr.trim().ifBlank { "Day $dayNum" },
                            location.trim().ifBlank { "Asivanipalem Pandal" },
                            description.trim()
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_event_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (eventToEdit == null) "Save Event" else "Update Event",
                        fontWeight = FontWeight.Bold,
                        color = NaturalCreamBg
                    )
                }
            }
        }
    }
}
