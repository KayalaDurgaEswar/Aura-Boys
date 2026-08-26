package com.example.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.NaturalSandPill
import com.example.ui.theme.NaturalSurface
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextMuted
import com.example.ui.theme.NaturalTextSecondary
import com.example.ui.theme.OchreGold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptDialog(
    donor: DonorCandidate,
    committeeName: String,
    presidentName: String,
    festivalYear: String,
    onDismiss: () -> Unit,
    onCopied: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateStr = if (donor.paymentDate != null) {
        SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date(donor.paymentDate))
    } else {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
    }
    val receiptNo = "GU-$festivalYear-${String.format("%04d", donor.id)}"

    val receiptText = buildString {
        appendLine("🚩 *श्री गणेशाय नमः* 🚩")
        appendLine("🏛️ *$committeeName* ($festivalYear)")
        appendLine("📜 *DIGITAL DONATION RECEIPT*")
        appendLine("─────────────────────────")
        appendLine("🎫 *Receipt No:* $receiptNo")
        appendLine("📅 *Date:* $dateStr")
        appendLine("👤 *Devotee Name:* ${donor.name}")
        if (donor.houseOrFlat.isNotBlank()) appendLine("🏠 *Address/Flat:* ${donor.houseOrFlat}")
        if (donor.phone.isNotBlank()) appendLine("📞 *Phone:* ${donor.phone}")
        appendLine("💰 *Amount Paid:* ${format.format(donor.paidAmount.ifZero(donor.targetAmount))}")
        appendLine("💳 *Payment Mode:* ${donor.paymentMode}")
        if (donor.transactionRef.isNotBlank()) appendLine("🔖 *Ref / Txn ID:* ${donor.transactionRef}")
        if (donor.notes.isNotBlank()) appendLine("📝 *Purpose / Seva:* ${donor.notes}")
        appendLine("─────────────────────────")
        appendLine("✅ *Status:* PAID & VERIFIED")
        appendLine("✍️ *Issued by:* $presidentName")
        appendLine("🙏 *May Lord Ganesha shower prosperity and happiness on your family!*")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("receipt_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NaturalSurface),
            border = BorderStroke(1.dp, NaturalBorderLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Close Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Chanda Receipt",
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown,
                        fontSize = 15.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_receipt_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NaturalTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Traditional Border Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.5.dp, OchreGold.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .background(NaturalSandPill, shape = RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "॥ श्री गणेशाय नमः ॥",
                            color = NaturalTerracotta,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = committeeName,
                            color = EarthBrown,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Ganesh Chaturthi Mahotsav $festivalYear",
                            color = NaturalTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = NaturalBorderSand, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Receipt Details Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "RECEIPT NO", fontSize = 9.sp, color = NaturalTextMuted, fontWeight = FontWeight.Bold)
                                Text(text = receiptNo, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EarthBrown)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "DATE", fontSize = 9.sp, color = NaturalTextMuted, fontWeight = FontWeight.Bold)
                                Text(text = dateStr, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = NaturalTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Devotee Name & Details Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NaturalSurface,
                            border = BorderStroke(1.dp, NaturalBorderSand),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "RECEIVED WITH THANKS FROM", fontSize = 9.sp, color = NaturalTextMuted, fontWeight = FontWeight.Bold)
                                Text(
                                    text = donor.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EarthBrown
                                )
                                if (donor.houseOrFlat.isNotBlank()) {
                                    Text(
                                        text = "Address: ${donor.houseOrFlat}",
                                        fontSize = 11.sp,
                                        color = NaturalTextSecondary
                                    )
                                }
                                if (donor.phone.isNotBlank()) {
                                    Text(
                                        text = "Phone: ${donor.phone}",
                                        fontSize = 10.sp,
                                        color = NaturalTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Amount Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NaturalGreenBg,
                            border = BorderStroke(1.dp, NaturalGreenBorder),
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
                                    Text(text = "AMOUNT RECEIVED", fontSize = 9.sp, color = NaturalGreen, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = format.format(donor.paidAmount.ifZero(donor.targetAmount)),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalGreen
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NaturalGreen
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = NaturalCreamBg,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "PAID (${donor.paymentMode})",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NaturalCreamBg
                                        )
                                    }
                                }
                            }
                        }

                        if (donor.transactionRef.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Txn Ref: ${donor.transactionRef}",
                                fontSize = 10.sp,
                                color = NaturalTextMuted
                            )
                        }

                        if (donor.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Purpose: ${donor.notes}",
                                fontSize = 11.sp,
                                color = NaturalTextSecondary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = NaturalBorderSand, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Signature & Seal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "🕉️ Ganpati Bappa Morya",
                                    fontSize = 10.sp,
                                    color = EarthBrown,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Valid Digital Receipt",
                                    fontSize = 9.sp,
                                    color = NaturalTextMuted
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Certified by",
                                    fontSize = 9.sp,
                                    color = NaturalTextMuted
                                )
                                Text(
                                    text = presidentName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EarthBrown
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions: Share on WhatsApp & Copy Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(receiptText))
                            onCopied()
                        },
                        modifier = Modifier.weight(1f).testTag("copy_receipt_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NaturalBorderSand)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = EarthBrown,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", color = EarthBrown, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, receiptText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Chanda Receipt")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.weight(1.3f).testTag("share_whatsapp_receipt_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun Double.ifZero(other: Double): Double = if (this > 0) this else other
