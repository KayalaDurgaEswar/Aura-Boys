package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.DonorCandidate
import com.example.ui.theme.EarthBrown
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
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddDonorDialog(
    donorToEdit: DonorCandidate? = null,
    isAdminMode: Boolean = true,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        houseOrFlat: String,
        phone: String,
        targetAmount: Double,
        isPaid: Boolean,
        paidAmount: Double,
        paymentMode: String,
        notes: String,
        transactionRef: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(donorToEdit?.name ?: "") }
    var houseOrFlat by remember { mutableStateOf(donorToEdit?.houseOrFlat ?: "") }
    var phone by remember { mutableStateOf(donorToEdit?.phone ?: "") }
    var targetAmountStr by remember {
        mutableStateOf(
            if (donorToEdit != null) donorToEdit.targetAmount.toInt().toString() else "1100"
        )
    }
    var isPaid by remember { mutableStateOf(donorToEdit?.isPaid ?: false) }
    var paymentMode by remember { mutableStateOf(donorToEdit?.paymentMode ?: "UPI") }
    var transactionRef by remember { mutableStateOf(donorToEdit?.transactionRef ?: "") }
    var notes by remember { mutableStateOf(donorToEdit?.notes ?: "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val paymentModes = listOf("UPI", "Cash", "Bank Transfer", "Cheque")
    val presetAmounts = listOf("501", "1100", "2100", "5100", "11000")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("add_donor_dialog"),
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
                    Column {
                        Text(
                            text = if (donorToEdit == null) "Add Devotee / Candidate" else "Edit Devotee Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = EarthBrown
                        )
                        Text(
                            text = if (isAdminMode) "Set target chanda & payment status" else "Pledge your festive contribution",
                            fontSize = 11.sp,
                            color = NaturalTextMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).testTag("close_add_donor_button")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NaturalTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMsg = null },
                    label = { Text("Devotee / Candidate Name *") },
                    placeholder = { Text("e.g. Ramesh Patil & Family", color = NaturalTextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("donor_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // House / Flat
                OutlinedTextField(
                    value = houseOrFlat,
                    onValueChange = { houseOrFlat = it },
                    label = { Text("Flat / House No / Society") },
                    placeholder = { Text("e.g. Flat A-203, Ganesh Heights", color = NaturalTextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("donor_flat_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (for WhatsApp Receipt)") },
                    placeholder = { Text("e.g. 9876543210", color = NaturalTextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = OchreGold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("donor_phone_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Target Amount
                Text(
                    text = "Chanda Amount (₹) *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EarthBrown
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = targetAmountStr,
                    onValueChange = { targetAmountStr = it; errorMsg = null },
                    leadingIcon = { Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, tint = OchreGold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("donor_amount_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Preset amount chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetAmounts.forEach { preset ->
                        val isSelected = targetAmountStr == preset
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) EarthBrown else NaturalSandPill,
                            border = BorderStroke(1.dp, if (isSelected) EarthBrown else NaturalBorderSand),
                            modifier = Modifier
                                .clickable { targetAmountStr = preset }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "₹$preset",
                                color = if (isSelected) NaturalCreamBg else EarthBrown,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Paid toggle (Available in Admin mode or self-reporting)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPaid) NaturalGreenBg else NaturalSandPill,
                    border = BorderStroke(1.dp, if (isPaid) NaturalGreenBorder else NaturalBorderSand),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isPaid) "Status: PAID ✅" else "Status: UNPAID / PENDING ⏳",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isPaid) NaturalGreen else EarthBrown
                            )
                            Text(
                                text = if (isPaid) "Amount received & verified" else "Candidate has not paid yet",
                                fontSize = 11.sp,
                                color = NaturalTextMuted
                            )
                        }
                        Switch(
                            checked = isPaid,
                            onCheckedChange = { isPaid = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NaturalGreen,
                                checkedTrackColor = NaturalGreenBg
                            ),
                            modifier = Modifier.testTag("donor_paid_switch")
                        )
                    }
                }

                if (isPaid) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Payment Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EarthBrown)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        paymentModes.forEach { mode ->
                            val isSelected = paymentMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { paymentMode = mode },
                                label = { Text(mode, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EarthBrown,
                                    selectedLabelColor = NaturalCreamBg,
                                    containerColor = NaturalSandPill,
                                    labelColor = EarthBrown
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) EarthBrown else NaturalBorderSand
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = transactionRef,
                        onValueChange = { transactionRef = it },
                        label = { Text("Txn ID / Ref (Optional)") },
                        placeholder = { Text("e.g. UPI/260826/78219", color = NaturalTextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("donor_txn_ref_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NaturalTerracotta,
                            unfocusedBorderColor = NaturalBorderSand,
                            focusedTextColor = EarthBrown,
                            unfocusedTextColor = EarthBrown
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes / Seva
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special Seva / Notes (Optional)") },
                    placeholder = { Text("e.g. Prasad Sponsor, Aarti Seva", color = NaturalTextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Note, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("donor_notes_input"),
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
                    Text(text = errorMsg!!, color = NaturalRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMsg = "Please enter devotee name"
                            return@Button
                        }
                        val amount = targetAmountStr.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            errorMsg = "Please enter a valid amount"
                            return@Button
                        }

                        onSave(
                            name,
                            houseOrFlat,
                            phone,
                            amount,
                            isPaid,
                            if (isPaid) amount else 0.0,
                            paymentMode,
                            notes,
                            transactionRef
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_donor_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (donorToEdit == null) "Add Devotee & Chanda" else "Update Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NaturalCreamBg
                    )
                }
            }
        }
    }
}
