package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ExpenseItem
import com.example.ui.theme.EarthBrown
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
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpenseScreen(
    expenses: List<ExpenseItem>,
    totalChandaCollected: Double,
    selectedCategory: String?,
    searchQuery: String,
    isAdminMode: Boolean,
    onCategorySelected: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onAddExpenseClick: () -> Unit,
    onEditExpenseClick: (ExpenseItem) -> Unit,
    onDeleteExpenseClick: (ExpenseItem) -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val totalExpense = expenses.sumOf { it.amount }
    val netBalance = totalChandaCollected - totalExpense

    val categories = listOf(
        "All",
        "Idol & Installation",
        "Pandal & Lighting",
        "Sound & Music",
        "Prasad & Food",
        "Puja Items",
        "Visarjan Procession",
        "Permissions & Security",
        "Others"
    )

    val filteredExpenses = expenses.filter { item ->
        val matchesCategory = selectedCategory == null || selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true) ||
                item.vendor.contains(searchQuery, ignoreCase = true) ||
                item.paidBy.contains(searchQuery, ignoreCase = true) ||
                item.notes.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NaturalCreamBg)
            .testTag("expense_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search & Category Filters (Pinned at top of screen)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NaturalSurface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search expense, vendor, paid by...", fontSize = 13.sp, color = NaturalTextMuted) },
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
                        .height(48.dp)
                        .testTag("expense_search_bar"),
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

                // Horizontal Scrollable Category Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth().testTag("expense_categories_row"),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = (selectedCategory == null && cat == "All") || selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (cat == "All") onCategorySelected(null) else onCategorySelected(cat)
                            },
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
                            ),
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }

            // Scrollable Content: Summary Header Card followed by Expense Items
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
            ) {
                // Summary Header Card (Scrolls up with the list)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 6.dp)
                            .testTag("expense_summary_card"),
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
                                            imageVector = Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            tint = OchreGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = "FESTIVAL EXPENDITURE", fontSize = 10.sp, color = NaturalTextMuted, fontWeight = FontWeight.Bold)
                                        Text(text = "Expense Ledger", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EarthBrown)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = NaturalSandPill,
                                    border = BorderStroke(1.dp, NaturalBorderSand)
                                ) {
                                    Text(
                                        text = "${expenses.size} Bills",
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Total Spent", fontSize = 11.sp, color = NaturalTextMuted)
                                    Text(
                                        text = format.format(totalExpense),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalTerracotta
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Net Remaining Balance", fontSize = 11.sp, color = NaturalTextMuted)
                                    Text(
                                        text = format.format(netBalance),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (netBalance >= 0) NaturalGreen else NaturalRed
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (filteredExpenses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = NaturalTextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No expenses recorded under this filter",
                                    color = NaturalTextMuted,
                                    fontSize = 14.sp
                                )
                                if (isAdminMode) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = onAddExpenseClick,
                                        colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("+ Record Bill", color = NaturalCreamBg)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            isAdminMode = isAdminMode,
                            onEdit = { onEditExpenseClick(expense) },
                            onDelete = { onDeleteExpenseClick(expense) }
                        )
                    }
                }
            }
        }

        // FAB to Add Expense (Admin Only)
        if (isAdminMode) {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = EarthBrown,
                contentColor = NaturalCreamBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 20.dp)
                    .testTag("add_expense_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense", modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
fun ExpenseItemCard(
    expense: ExpenseItem,
    isAdminMode: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("expense_card_${expense.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalSurface),
        border = BorderStroke(1.dp, NaturalBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NaturalSandPill,
                        border = BorderStroke(1.dp, NaturalBorderSand)
                    ) {
                        Text(
                            text = expense.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = EarthBrown,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = format.format(expense.amount),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTerracotta
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vendor & Paid By Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (expense.vendor.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = NaturalTextMuted, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = expense.vendor, fontSize = 11.sp, color = NaturalTextSecondary)
                    }
                }
                if (expense.paidBy.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NaturalTextMuted, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Paid by ${expense.paidBy}", fontSize = 11.sp, color = NaturalTextSecondary)
                    }
                }
            }

            if (expense.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 ${expense.notes}",
                    fontSize = 11.sp,
                    color = NaturalTextMuted,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = NaturalBorderLight, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = NaturalTextMuted
                )

                if (isAdminMode) {
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(26.dp).testTag("edit_expense_${expense.id}")) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = NaturalTextMuted, modifier = Modifier.size(14.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(26.dp).testTag("delete_expense_${expense.id}")) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = NaturalTextMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
