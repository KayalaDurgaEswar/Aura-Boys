package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.sync.CloudSyncState
import com.example.data.sync.CloudSyncStatus
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.NaturalAvatarBg
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalBorderSand
import com.example.ui.theme.NaturalCreamBg
import com.example.ui.theme.NaturalGreen
import com.example.ui.theme.NaturalSandPill
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold

@Composable
fun FestiveTopAppBar(
    committeeName: String,
    festivalYear: String,
    isAdminMode: Boolean,
    isMainAdmin: Boolean,
    currentAdminName: String,
    pendingAdminRequestsCount: Int,
    cloudSyncState: CloudSyncState,
    onSyncClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminAuthClick: () -> Unit,
    onShareReportClick: () -> Unit
) {
    val isSyncing = cloudSyncState.status == CloudSyncStatus.SYNCING
    val rotation by if (isSyncing) {
        val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spin"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0f) }
    }

    Surface(
        color = NaturalCreamBg,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .border(width = (0.5).dp, color = NaturalBorderLight)
            .testTag("festive_top_app_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NaturalCreamBg)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Branding
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NaturalAvatarBg)
                            .border(1.dp, NaturalBorderSand, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_ganesh_icon),
                            contentDescription = "Lord Ganesha Icon",
                            modifier = Modifier.size(34.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = committeeName.ifBlank { "Aura Boys" },
                            color = EarthBrown,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "Ganesh Festival $festivalYear",
                            color = OchreGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                // Action buttons: Cloud Sync, Notifications, and Share
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Cloud Sync Button
                    IconButton(
                        onClick = onSyncClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NaturalSandPill)
                            .testTag("cloud_sync_button")
                    ) {
                        Icon(
                            imageVector = when (cloudSyncState.status) {
                                CloudSyncStatus.SYNCING -> Icons.Default.Sync
                                CloudSyncStatus.OFFLINE_ERROR -> Icons.Default.CloudOff
                                else -> Icons.Default.CloudDone
                            },
                            contentDescription = "Cloud Sync",
                            tint = when (cloudSyncState.status) {
                                CloudSyncStatus.SYNCING -> NaturalTerracotta
                                CloudSyncStatus.OFFLINE_ERROR -> EarthBrown.copy(alpha = 0.5f)
                                else -> NaturalGreen
                            },
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(if (isSyncing) rotation else 0f)
                        )
                    }

                    // Notification Icon with Badge for Admin Requests (ONLY Visible to Main Admin)
                    if (isMainAdmin) {
                        IconButton(
                            onClick = onNotificationsClick,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NaturalSandPill)
                                .testTag("admin_notifications_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (pendingAdminRequestsCount > 0) {
                                        Badge(
                                            containerColor = NaturalTerracotta,
                                            contentColor = Color.White
                                        ) {
                                            Text("$pendingAdminRequestsCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Admin Requests Notifications",
                                    tint = if (pendingAdminRequestsCount > 0) NaturalTerracotta else EarthBrown,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onShareReportClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NaturalSandPill)
                            .testTag("share_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Collection Report",
                            tint = EarthBrown,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-bar showing Location and Admin Portal link with Live Cloud indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "📍 Asivanipalem",
                        color = NaturalTextSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "•",
                        color = NaturalBorderSand,
                        fontSize = 11.sp
                    )
                    Text(
                        text = when (cloudSyncState.status) {
                            CloudSyncStatus.SYNCING -> "Syncing..."
                            CloudSyncStatus.OFFLINE_ERROR -> "Offline"
                            else -> "🟢 Live"
                        },
                        color = when (cloudSyncState.status) {
                            CloudSyncStatus.SYNCING -> NaturalTerracotta
                            CloudSyncStatus.OFFLINE_ERROR -> EarthBrown.copy(alpha = 0.6f)
                            else -> NaturalGreen
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Admin Status / Auth Link
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NaturalSandPill,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, NaturalBorderSand, RoundedCornerShape(20.dp))
                        .clickable { onAdminAuthClick() }
                        .testTag("role_toggle_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isAdminMode) NaturalGreen else NaturalBorderSand)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAdminMode) {
                                if (isMainAdmin) "MAIN ADMIN (nani)" else "ADMIN: $currentAdminName"
                            } else {
                                "MEMBER (DEVOTEE)"
                            },
                            color = EarthBrown,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }
        }
    }
}

