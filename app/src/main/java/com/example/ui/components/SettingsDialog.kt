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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalBorderSand
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.OchreGold

@Composable
fun SettingsDialog(
    committeeName: String,
    festivalYear: String,
    targetGoal: String,
    upiId: String,
    upiName: String,
    presidentName: String,
    adminPin: String,
    onDismiss: () -> Unit,
    onSave: (
        committeeName: String,
        festivalYear: String,
        targetGoal: String,
        upiId: String,
        upiName: String,
        presidentName: String,
        adminPin: String
    ) -> Unit
) {
    var cName by remember { mutableStateOf(committeeName) }
    var year by remember { mutableStateOf(festivalYear) }
    var goal by remember { mutableStateOf(targetGoal) }
    var uId by remember { mutableStateOf(upiId) }
    var uName by remember { mutableStateOf(upiName) }
    var pName by remember { mutableStateOf(presidentName) }
    var pin by remember { mutableStateOf(adminPin) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("settings_dialog"),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mandal & Festival Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EarthBrown
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).testTag("close_settings_dialog")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NaturalTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cName,
                    onValueChange = { cName = it },
                    label = { Text("Mandal / Committee Name") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("setting_committee_name"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Total Festival Fund Target (₹)") },
                    leadingIcon = { Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("setting_target_goal"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uId,
                    onValueChange = { uId = it },
                    label = { Text("UPI ID for Chanda Collection") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("setting_upi_id"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uName,
                    onValueChange = { uName = it },
                    label = { Text("UPI Payee Name") },
                    modifier = Modifier.fillMaxWidth().testTag("setting_upi_name"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pName,
                    onValueChange = { pName = it },
                    label = { Text("Authorized Signature / President") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("setting_president_name"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("Admin PIN") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("setting_admin_pin"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSave(cName, year, goal, uId, uName, pName, pin)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Mandal Configuration", fontWeight = FontWeight.Bold, color = NaturalCreamBg)
                }
            }
        }
    }
}
