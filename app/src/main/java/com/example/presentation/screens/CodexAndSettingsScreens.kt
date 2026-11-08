package com.example.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.WorkoutLogEntity
import com.example.domain.model.VerificationMethod
import com.example.presentation.AwakenUiState
import com.example.presentation.components.HolographicCard
import com.example.ui.theme.SystemAmber
import com.example.ui.theme.SystemBorderDim
import com.example.ui.theme.SystemCrimson
import com.example.ui.theme.SystemCyan
import com.example.ui.theme.SystemEmerald
import com.example.ui.theme.SystemGold
import com.example.ui.theme.SystemObsidian
import com.example.ui.theme.SystemSurfaceElevated
import com.example.ui.theme.SystemTextPrimary
import com.example.ui.theme.SystemTextSecondary
import com.example.ui.theme.SystemViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CodexAndSettingsScreen(
    uiState: AwakenUiState,
    initialTab: String = "HISTORY",
    onLogRecovery: (
        sleepHours: Float,
        hydrationGlasses: Int,
        soreness: Int,
        energy: Int,
        painFlag: Boolean,
        notes: String
    ) -> Unit,
    onToggleReducedAnimations: (Boolean) -> Unit,
    onOpenProfileCalibration: () -> Unit,
    onGenerateBackupJson: () -> Unit,
    onImportBackupJson: (String) -> Boolean
) {
    var activeSubTab by remember(initialTab) { mutableStateOf(initialTab) }

    // Recovery Journal State
    var sleepHours by remember { mutableFloatStateOf(7.5f) }
    var hydrationGlasses by remember { mutableFloatStateOf(8f) }
    var sorenessLevel by remember { mutableFloatStateOf(2f) }
    var energyLevel by remember { mutableFloatStateOf(4f) }
    var painFlag by remember { mutableStateOf(false) }
    var recoveryNotes by remember { mutableStateOf("") }

    // Backup JSON Import state
    var importJsonInput by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SystemObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SYSTEM CODEX, RECOVERY & OFFLINE BACKUP",
                style = MaterialTheme.typography.headlineSmall,
                color = SystemCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "HISTORY" to "LOGS & STATS",
                    "RECOVERY" to "RECOVERY JOURNAL",
                    "BACKUP" to "SETTINGS & JSON"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = activeSubTab == key,
                        onClick = { activeSubTab = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        when (activeSubTab) {
            "HISTORY" -> {
                item {
                    val camCount = uiState.workoutHistory.count {
                        it.verificationMethod == VerificationMethod.CAMERA_VERIFIED.name ||
                            it.verificationMethod == VerificationMethod.MANUALLY_CORRECTED.name
                    }
                    HolographicCard(accentColor = SystemCyan) {
                        Text(
                            text = "PERFORMANCE & VERIFICATION ANALYTICS",
                            style = MaterialTheme.typography.titleSmall,
                            color = SystemCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatMetricColumn("TOTAL SESSIONS", "${uiState.workoutHistory.size}", SystemCyan)
                            StatMetricColumn("CAMERA VERIFIED", "$camCount", SystemEmerald)
                            StatMetricColumn("BOSS CLEARED", "${uiState.characterStatus.completedBossEvaluations}", SystemGold)
                            StatMetricColumn("RECOVERY LOGS", "${uiState.recoveryLogs.size}", SystemViolet)
                        }

                        if (uiState.workoutHistory.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "RECENT SESSION XP OUTPUT",
                                style = MaterialTheme.typography.labelSmall,
                                color = SystemTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val recent = uiState.workoutHistory.take(7).reversed()
                            val maxXp = (recent.maxOfOrNull { it.xpEarned } ?: 100).coerceAtLeast(50)
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                            ) {
                                val barWidth = (size.width / (recent.size * 2f)).coerceAtMost(48f)
                                val spacing = size.width / recent.size.coerceAtLeast(1)
                                recent.forEachIndexed { idx, log ->
                                    val frac = (log.xpEarned.toFloat() / maxXp.toFloat()).coerceIn(0.15f, 1f)
                                    val barH = size.height * frac
                                    val x = idx * spacing + (spacing - barWidth) / 2f
                                    drawRoundRect(
                                        color = if (log.verificationMethod == VerificationMethod.CAMERA_VERIFIED.name) SystemEmerald else SystemCyan,
                                        topLeft = Offset(x, size.height - barH),
                                        size = Size(barWidth, barH),
                                        cornerRadius = CornerRadius(6f, 6f)
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.workoutHistory.isEmpty()) {
                    item {
                        HolographicCard(accentColor = SystemBorderDim) {
                            Text(
                                text = "No workouts logged yet. Complete any daily directive or recovery protocol to populate your permanent offline log.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SystemTextSecondary
                            )
                        }
                    }
                } else {
                    items(uiState.workoutHistory, key = { it.id }) { log ->
                        WorkoutHistoryLogCard(log = log)
                    }
                }
            }

            "RECOVERY" -> {
                item {
                    HolographicCard(accentColor = SystemEmerald) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = SystemEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DAILY RECOVERY & BIOMETRIC CHECK-IN",
                                style = MaterialTheme.typography.titleMedium,
                                color = SystemEmerald
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Responsible recovery awards +35 XP to Discipline & Flexibility. Reporting high soreness or pain automatically adapts your quests without penalty.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sleep Duration: ${String.format(Locale.US, "%.1f", sleepHours)} hours",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SystemTextPrimary
                        )
                        Slider(
                            value = sleepHours,
                            onValueChange = { sleepHours = it },
                            valueRange = 3f..11f,
                            steps = 15,
                            colors = SliderDefaults.colors(thumbColor = SystemCyan, activeTrackColor = SystemCyan)
                        )

                        Text(
                            text = "Hydration: ${hydrationGlasses.toInt()} glasses (250ml)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SystemTextPrimary
                        )
                        Slider(
                            value = hydrationGlasses,
                            onValueChange = { hydrationGlasses = it },
                            valueRange = 1f..16f,
                            steps = 14,
                            colors = SliderDefaults.colors(thumbColor = SystemCyan, activeTrackColor = SystemCyan)
                        )

                        Text(
                            text = "Muscular Soreness: ${sorenessLevel.toInt()} / 5 (1 = Fresh, 5 = Very Sore)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SystemTextPrimary
                        )
                        Slider(
                            value = sorenessLevel,
                            onValueChange = { sorenessLevel = it },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(thumbColor = SystemGold, activeTrackColor = SystemGold)
                        )

                        Text(
                            text = "Readiness & Energy: ${energyLevel.toInt()} / 5",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SystemTextPrimary
                        )
                        Slider(
                            value = energyLevel,
                            onValueChange = { energyLevel = it },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(thumbColor = SystemEmerald, activeTrackColor = SystemEmerald)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = painFlag,
                                onCheckedChange = { painFlag = it }
                            )
                            Text(
                                text = "Flag joint discomfort / request gentle deload quests today",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (painFlag) SystemCrimson else SystemTextPrimary
                            )
                        }

                        OutlinedTextField(
                            value = recoveryNotes,
                            onValueChange = { recoveryNotes = it },
                            label = { Text("Recovery Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onLogRecovery(
                                    sleepHours,
                                    hydrationGlasses.toInt(),
                                    sorenessLevel.toInt(),
                                    energyLevel.toInt(),
                                    painFlag,
                                    recoveryNotes
                                )
                                recoveryNotes = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_recovery_log_button"),
                            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SystemEmerald, contentColor = SystemObsidian)
                        ) {
                            Text("LOG RECOVERY CHECK-IN (+35 XP)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            "BACKUP" -> {
                item {
                    HolographicCard(accentColor = SystemViolet) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = SystemViolet)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SYSTEM PERFORMANCE & PROFILE SETTINGS",
                                style = MaterialTheme.typography.titleMedium,
                                color = SystemViolet
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reduced-Animation Mode (6GB RAM Optimization)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SystemTextPrimary
                                )
                                Text(
                                    text = "Disables continuous bar transitions for ultra-low battery & memory usage.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SystemTextSecondary
                                )
                            }
                            Switch(
                                checked = uiState.profile.reducedAnimations,
                                onCheckedChange = onToggleReducedAnimations
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onOpenProfileCalibration,
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, SystemCyan)
                        ) {
                            Text("RE-OPEN ONBOARDING & CAPABILITY ASSESSMENT", color = SystemCyan)
                        }
                    }
                }

                item {
                    HolographicCard(accentColor = SystemCyan) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Backup, contentDescription = null, tint = SystemCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OFFLINE LOCAL JSON BACKUP & RESTORE",
                                style = MaterialTheme.typography.titleMedium,
                                color = SystemCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Export your complete Hunter Profile, Attribute XP, Skill Tiers, and Workout History as portable JSON — zero cloud accounts required.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onGenerateBackupJson,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_backup_json_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SystemCyan, contentColor = SystemObsidian)
                        ) {
                            Text("GENERATE LOCAL JSON BACKUP SNAPSHOT", fontWeight = FontWeight.Bold)
                        }

                        if (uiState.backupJsonText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.backupJsonText,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Exported System JSON (Copy to save offline)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = importJsonInput,
                            onValueChange = { importJsonInput = it },
                            label = { Text("Paste Backup JSON Here to Restore") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                val ok = onImportBackupJson(importJsonInput)
                                importStatusMessage = if (ok) {
                                    "Backup JSON successfully restored!"
                                } else {
                                    "Invalid JSON format. Please paste a valid Awaken backup snapshot."
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, SystemEmerald)
                        ) {
                            Text("RESTORE FROM JSON SNAPSHOT", color = SystemEmerald)
                        }

                        importStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (msg.startsWith("Backup")) SystemEmerald else SystemAmber
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun StatMetricColumn(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = SystemTextSecondary)
    }
}

@Composable
private fun WorkoutHistoryLogCard(log: WorkoutLogEntity) {
    val method = runCatching { VerificationMethod.valueOf(log.verificationMethod) }
        .getOrDefault(VerificationMethod.SELF_REPORTED)
    val badgeColor = when (method) {
        VerificationMethod.CAMERA_VERIFIED -> SystemEmerald
        VerificationMethod.MANUALLY_CORRECTED -> SystemCyan
        VerificationMethod.SELF_REPORTED -> SystemGold
    }
    val dateStr = remember(log.timestamp) {
        SimpleDateFormat("MMM dd, HH:mm", Locale.US).format(Date(log.timestamp))
    }

    HolographicCard(accentColor = badgeColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.questTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = SystemTextPrimary
                )
                Text(
                    text = "${log.exerciseName} • $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = SystemTextSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeColor.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, badgeColor)
            ) {
                Text(
                    text = method.shortTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val volUnit = if (log.isDurationSeconds) "sec" else "reps"
            Text(
                text = "Volume: ${log.completedSets} sets × ${log.completedRepsOrSeconds} $volUnit • RPE ${log.perceivedEffortRpe}/10",
                style = MaterialTheme.typography.bodySmall,
                color = SystemTextSecondary
            )
            Text(
                text = "+${log.xpEarned} XP (${log.primaryAttribute})",
                style = MaterialTheme.typography.labelMedium,
                color = SystemCyan
            )
        }
    }
}
