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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.data.entity.SamagriItem
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SamagriScreen(
    samagriList: List<SamagriItem>,
    isAdminMode: Boolean,
    onToggleStatus: (SamagriItem) -> Unit,
    onDelete: (SamagriItem) -> Unit,
    onAddSamagriClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Daily Puja", "Installation", "Food & Prasad", "Visarjan Procession", "Others")

    val arrangedCount = samagriList.count { it.isArranged }
    val pendingCount = samagriList.count { !it.isArranged }

    val filteredList = samagriList.filter {
        selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NaturalCreamBg)
            .testTag("samagri_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                                Icon(imageVector = Icons.Default.Checklist, contentDescription = null, tint = OchreGold, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("PUJA SAMAGRI CHECKLIST", fontSize = 10.sp, color = NaturalTextMuted, fontWeight = FontWeight.Bold)
                                Text("Items & Volunteer Readiness", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EarthBrown)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (pendingCount == 0) NaturalGreenBg else NaturalSandPill,
                            border = BorderStroke(1.dp, if (pendingCount == 0) NaturalGreenBorder else NaturalBorderSand)
                        ) {
                            Text(
                                text = "$arrangedCount / ${samagriList.size} Ready",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pendingCount == 0) NaturalGreen else EarthBrown,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Categories Scrollable Filter Chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().testTag("samagri_categories_row"),
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
                }
            }

            // Samagri Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    SamagriItemCard(
                        item = item,
                        isAdminMode = isAdminMode,
                        onToggle = { if (isAdminMode) onToggleStatus(item) },
                        onDelete = { onDelete(item) }
                    )
                }
            }
        }

        // FAB (Admin Only)
        if (isAdminMode) {
            FloatingActionButton(
                onClick = onAddSamagriClick,
                containerColor = EarthBrown,
                contentColor = NaturalCreamBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 20.dp)
                    .testTag("add_samagri_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Samagri", modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
fun SamagriItemCard(
    item: SamagriItem,
    isAdminMode: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("samagri_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NaturalSurface),
        border = BorderStroke(
            1.dp,
            if (item.isArranged) NaturalGreenBorder else NaturalBorderLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.isArranged,
                    onCheckedChange = { if (isAdminMode) onToggle() },
                    enabled = isAdminMode,
                    colors = CheckboxDefaults.colors(checkedColor = NaturalGreen),
                    modifier = Modifier.testTag("samagri_check_${item.id}")
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isArranged) NaturalTextMuted else EarthBrown
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Qty: ${item.quantity} • ${item.category}",
                            fontSize = 11.sp,
                            color = NaturalTextSecondary
                        )
                        if (item.assignedTo.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "👤 ${item.assignedTo}",
                                fontSize = 10.sp,
                                color = NaturalTerracotta,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (isAdminMode) {
                IconButton(onClick = onDelete, modifier = Modifier.size(26.dp).testTag("delete_samagri_${item.id}")) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = NaturalTextMuted, modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddSamagriDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: String, category: String, assignedTo: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Daily Puja") }
    var assignedTo by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Daily Puja", "Installation", "Food & Prasad", "Visarjan Procession", "Others")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("add_samagri_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NaturalSurface)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Puja Item / Samagri", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EarthBrown)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NaturalTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMsg = null },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. Red Flowers, Durva Grass", color = NaturalTextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("samagri_name_input"),
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
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    placeholder = { Text("e.g. 50 Bundles / 2 Kgs", color = NaturalTextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("samagri_quantity_input"),
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

                Text("Category", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NaturalTextMuted)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = category == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { category = cat },
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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = { assignedTo = it },
                    label = { Text("Volunteer In-charge") },
                    placeholder = { Text("e.g. Sunil, Priya", color = NaturalTextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("samagri_assigned_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalTerracotta,
                        unfocusedBorderColor = NaturalBorderSand,
                        focusedTextColor = EarthBrown,
                        unfocusedTextColor = EarthBrown
                    ),
                    singleLine = true
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMsg!!, color = Color(0xFFEF4444), fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMsg = "Please enter item name"
                            return@Button
                        }
                        onSave(name, quantity, category, assignedTo)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_samagri_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Add Samagri Item", fontWeight = FontWeight.Bold, color = NaturalCreamBg)
                }
            }
        }
    }
}
