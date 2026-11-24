package com.habitflow.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFFF7F6F1)
private val Green = Color(0xFF315C48)
private val Dark = Color(0xFF252B27)
private val Muted = Color(0xFF858B85)
private val Red = Color(0xFFD32F2F)

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean = false,
    onDarkThemeChange: (Boolean) -> Unit = {},
    notificationsEnabled: Boolean = true,
    onNotificationsChange: (Boolean) -> Unit = {},
    onExportBackup: () -> Unit = {},
    onImportBackup: (String) -> Unit = {},
    onResetData: () -> Unit = {},
    bottomBar: @Composable () -> Unit
) {
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Background,
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(top = 25.dp, bottom = 20.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Preferences & Data Management",
                fontSize = 14.sp,
                color = Muted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Preferences Section Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Preferences",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notifications Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Notifications", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Dark)
                            Text(text = "Daily habit reminders", fontSize = 12.sp, color = Muted)
                        }

                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = onNotificationsChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Green)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F1ED))

                    // Dark Theme Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Dark Mode", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Dark)
                            Text(text = "Use dark theme", fontSize = 12.sp, color = Muted)
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = onDarkThemeChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Green)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Data Management Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Data Management",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onExportBackup,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green)
                    ) {
                        Text(text = "Export Backup (JSON)", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Import Backup (JSON)", color = Dark, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red)
                    ) {
                        Text(text = "Reset All Data", color = Red, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // About App Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "About HabitFlow",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Version", fontSize = 14.sp, color = Muted)
                        Text(text = "1.0.0 (Offline First)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Dark)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Storage", fontSize = 14.sp, color = Muted)
                        Text(text = "Local Room Database", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Dark)
                    }
                }
            }
        }
    }

    // Import Backup Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(text = "Import Backup JSON", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = importJsonInput,
                    onValueChange = { importJsonInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    placeholder = { Text("Paste JSON backup string here...") },
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImportDialog = false
                        if (importJsonInput.isNotBlank()) {
                            onImportBackup(importJsonInput.trim())
                            importJsonInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Data Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "Reset All Data?", fontWeight = FontWeight.Bold, color = Dark) },
            text = { Text(text = "This will permanently delete all habits and progress records.", color = Muted) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red)
                ) {
                    Text("Reset All", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
