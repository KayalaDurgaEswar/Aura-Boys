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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

import com.example.data.entity.AdminRequest
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable

@Composable
fun AdminAuthDialog(
    isAdminMode: Boolean,
    isMainAdmin: Boolean,
    currentAdminName: String,
    approvedMembers: List<AdminRequest> = emptyList(),
    onLoginMainAdmin: (String, String) -> Boolean,
    onLoginAuthorizedAdmin: (String) -> Boolean,
    onRequestAdminClick: () -> Unit,
    onLogoutAdmin: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var mainAdminUsername by remember { mutableStateOf("") }
    var mainAdminPassword by remember { mutableStateOf("") }
    var authorizedMemberName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("admin_auth_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NaturalSurface),
            border = BorderStroke(1.dp, NaturalBorderLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = OchreGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Access Portal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_admin_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NaturalTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isAdminMode) {
                    // Currently logged in as Admin
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NaturalSandPill, RoundedCornerShape(12.dp))
                            .border(1.dp, NaturalBorderSand, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isMainAdmin) "👑 Main Admin: $currentAdminName" else "🛡️ Authorized Admin: $currentAdminName",
                                fontWeight = FontWeight.Bold,
                                color = EarthBrown,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You currently have full administrative management access.",
                                fontSize = 12.sp,
                                color = NaturalTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onLogoutAdmin()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("exit_admin_mode_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Switch to Member (Devotee) View", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    // Tabs: Authorized Member first, Main Admin second
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
                            text = { Text("Authorized Member", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Main Admin", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTab == 0) {
                        // Authorized Member Login (First)
                        Text(
                            text = "Approved Committee Member Login",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NaturalTextSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = authorizedMemberName,
                            onValueChange = { authorizedMemberName = it },
                            label = { Text("Your Approved Name") },
                            placeholder = { Text("e.g. Committee Member") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = OchreGold) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_member_name_field"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NaturalTerracotta,
                                unfocusedBorderColor = NaturalBorderSand,
                                focusedTextColor = EarthBrown,
                                unfocusedTextColor = EarthBrown
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (onLoginAuthorizedAdmin(authorizedMemberName)) {
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("login_auth_member_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Login with Authorized Name", fontWeight = FontWeight.Bold, color = NaturalCreamBg)
                        }
                    } else {
                        // Main Admin Login (Second)
                        Text(
                            text = "Main Admin Login",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NaturalTextSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = mainAdminUsername,
                            onValueChange = { mainAdminUsername = it },
                            label = { Text("Username") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = OchreGold) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("main_admin_username_field"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NaturalTerracotta,
                                unfocusedBorderColor = NaturalBorderSand,
                                focusedTextColor = EarthBrown,
                                unfocusedTextColor = EarthBrown
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = mainAdminPassword,
                            onValueChange = { mainAdminPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = OchreGold) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = NaturalTextMuted
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (onLoginMainAdmin(mainAdminUsername, mainAdminPassword)) {
                                        onDismiss()
                                    }
                                }
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("main_admin_password_field"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NaturalTerracotta,
                                unfocusedBorderColor = NaturalBorderSand,
                                focusedTextColor = EarthBrown,
                                unfocusedTextColor = EarthBrown
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (onLoginMainAdmin(mainAdminUsername, mainAdminPassword)) {
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("login_main_admin_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EarthBrown),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Login as Main Admin", fontWeight = FontWeight.Bold, color = NaturalCreamBg)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Request Admin Access Option for members
                    OutlinedButton(
                        onClick = onRequestAdminClick,
                        modifier = Modifier.fillMaxWidth().testTag("open_request_admin_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NaturalBorderSand)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = NaturalTerracotta, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Request Admin Access from Main Admin", color = NaturalTerracotta, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
