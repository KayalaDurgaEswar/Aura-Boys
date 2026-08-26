package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
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
import com.example.data.entity.ExpenseItem
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalBorderSand
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalRed
import com.example.ui.theme.NaturalSandPill
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    expenseToEdit: ExpenseItem? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        category: String,
        amount: Double,
        vendor: String,
        paidBy: String,
        notes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf(expenseToEdit?.title ?: "") }
    var selectedCategory by remember { mutableStateOf(expenseToEdit?.category ?: "Murti & Sthapana") }
    var amountStr by remember { mutableStateOf(if (expenseToEdit != null) expenseToEdit.amount.toInt().toString() else "") }
    var vendor by remember { mutableStateOf(expenseToEdit?.vendor ?: "") }
    var paidBy by remember { mutableStateOf(expenseToEdit?.paidBy ?: "") }
    var notes by remember { mutableStateOf(expenseToEdit?.notes ?: "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Murti & Sthapana",
        "Pandal & Lighting",
        "Sound & Dhol",
        "Prasad & Bhog",
        "Puja Samagri",
        "Visarjan Procession",
        "Permissions & Security",
        "Others"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("add_expense_dialog"),
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
                            text = if (expenseToEdit == null) "Record Festival Expense" else "Edit Expense",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = EarthBrown
                        )
                        Text(
                            text = "Track utsav funds transparency",
                            fontSize = 11.sp,
                            color = NaturalTextMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).testTag("close_expense_dialog")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NaturalTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMsg = null },
                    label = { Text("Expense Item / Purpose *") },
                    placeholder = { Text("e.g. Flower garlands & floral arch", color = NaturalTextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("expense_title_input"),
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

                // Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it; errorMsg = null },
                    label = { Text("Amount Spent (₹) *") },
                    placeholder = { Text("e.g. 4500", color = NaturalTextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null, tint = OchreGold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("expense_amount_input"),
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

                // Category Chips Scrollable
                Text(
                    text = "Category *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EarthBrown
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth().testTag("add_expense_categories_row"),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

                Spacer(modifier = Modifier.height(10.dp))

                // Vendor / Shop
                OutlinedTextField(
                    value = vendor,
                    onValueChange = { vendor = it },
                    label = { Text("Vendor / Shop Name") },
                    placeholder = { Text("e.g. Sai Pooja Stores", color = NaturalTextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("expense_vendor_input"),
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

                // Paid By (Volunteer/Committee member)
                OutlinedTextField(
                    value = paidBy,
                    onValueChange = { paidBy = it },
                    label = { Text("Paid By (Committee Member)") },
                    placeholder = { Text("e.g. Amit Deshmukh", color = NaturalTextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = OchreGold) },
                    modifier = Modifier.fillMaxWidth().testTag("expense_paid_by_input"),
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

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Bill / Receipt Details (Optional)") },
                    placeholder = { Text("e.g. Bill #4092, Paid via Google Pay", color = NaturalTextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("expense_notes_input"),
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
                        if (title.isBlank()) {
                            errorMsg = "Please enter expense purpose"
                            return@Button
                        }
                        val amount = amountStr.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            errorMsg = "Please enter a valid expense amount"
                            return@Button
                        }

                        onSave(title, selectedCategory, amount, vendor, paidBy, notes)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_expense_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (expenseToEdit == null) "Record Expense" else "Update Expense",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NaturalCreamBg
                    )
                }
            }
        }
    }
}
