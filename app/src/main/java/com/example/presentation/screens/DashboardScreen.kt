package com.example.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditAttributes
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.QuestEntity
import com.example.domain.model.AttributeType
import com.example.domain.model.QuestCategory
import com.example.presentation.AwakenUiState
import com.example.presentation.components.HolographicCard
import com.example.presentation.components.PentagonAttributeRadar
import com.example.presentation.components.RankBadgeChip
import com.example.presentation.components.SystemProgressBar
import com.example.ui.theme.SystemAmber
import com.example.ui.theme.SystemBorderDim
import com.example.ui.theme.SystemCrimson
import com.example.ui.theme.SystemCyan
import com.example.ui.theme.SystemEmerald
import com.example.ui.theme.SystemGold
import com.example.ui.theme.SystemObsidian
import com.example.ui.theme.SystemSurfaceElevated
import com.example.ui.theme.SystemTextMuted
import com.example.ui.theme.SystemTextPrimary
import com.example.ui.theme.SystemTextSecondary
import com.example.ui.theme.SystemViolet

@Composable
fun DashboardScreen(
    uiState: AwakenUiState,
    onOpenQuest: (QuestEntity) -> Unit,
    onNavigateToQuests: () -> Unit,
    onNavigateToStatus: () -> Unit,
    onNavigateToSkills: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onOpenOnboardingCalibration: () -> Unit,
    onTriggerSafetyDeload: () -> Unit
) {
    val status = uiState.characterStatus
    val completedToday = uiState.todayQuests.count { it.isCompleted }
    val totalToday = uiState.todayQuests.size.coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SystemObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Hero Holographic System Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_system_hud_header),
                    contentDescription = "System Holographic HUD Header",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(196.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(196.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SystemObsidian.copy(alpha = 0.55f),
                                    SystemObsidian.copy(alpha = 0.85f),
                                    SystemObsidian.copy(alpha = 0.96f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "SYSTEM VESSEL // ${status.codename}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SystemCyan
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "LEVEL ${status.level}",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = SystemTextPrimary
                                )
                            }
                            RankBadgeChip(rank = status.rank)
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "XP PROGRESS TO LV.${status.level + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SystemTextSecondary
                                )
                                Text(
                                    text = "${status.currentLevelXp} / ${status.nextLevelXp} XP (TOTAL: ${status.totalXp})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SystemCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            SystemProgressBar(
                                progress = status.levelProgressFraction,
                                color = SystemCyan,
                                height = 8.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = SystemGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${status.streakDays}-DAY STREAK",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = SystemGold
                                    )
                                }
                                Text(
                                    text = "TODAY: $completedToday / ${uiState.todayQuests.size} DIRECTIVES CLEARED",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SystemEmerald
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Quest Directive Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAILY QUEST DIRECTIVES",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SystemTextPrimary
                    )
                    Text(
                        text = "Adaptive schedule • ${uiState.profile.availableMinutesPerDay} min window • ${uiState.todayDateKey}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SystemTextSecondary
                    )
                }
                OutlinedButton(
                    onClick = onNavigateToQuests,
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    border = BorderStroke(1.dp, SystemCyan.copy(alpha = 0.6f))
                ) {
                    Text("ALL QUESTS", style = MaterialTheme.typography.labelMedium, color = SystemCyan)
                }
            }
        }

        items(uiState.todayQuests, key = { it.id }) { quest ->
            DashboardQuestItemCard(
                quest = quest,
                onClick = { onOpenQuest(quest) }
            )
        }

        // 5-Attribute Telemetry Overview
        item {
            HolographicCard(
                accentColor = SystemViolet,
                onClick = onNavigateToStatus
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "5-ATTRIBUTE MATRIX",
                            style = MaterialTheme.typography.titleLarge,
                            color = SystemTextPrimary
                        )
                        Text(
                            text = "Real-world physical capabilities & discipline telemetry",
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Status",
                        tint = SystemViolet
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                PentagonAttributeRadar(attributes = status.attributes)
            }
        }

        // Safety, Recovery & Profile Calibration Actions
        item {
            HolographicCard(accentColor = SystemEmerald) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = SystemEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "RESPONSIBLE PROGRESSION & RECOVERY",
                            style = MaterialTheme.typography.titleMedium,
                            color = SystemEmerald
                        )
                        Text(
                            text = "Feeling joint pain, dizziness, or unusual fatigue? Recalibrate today's quests immediately with zero XP penalty.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onTriggerSafetyDeload,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("safety_deload_button"),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        border = BorderStroke(1.dp, SystemAmber)
                    ) {
                        Text("REPORT FATIGUE / DELOAD", style = MaterialTheme.typography.labelSmall, color = SystemAmber)
                    }
                    OutlinedButton(
                        onClick = onNavigateToRecovery,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_recovery_journal_button"),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        border = BorderStroke(1.dp, SystemEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = SystemEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LOG RECOVERY", style = MaterialTheme.typography.labelSmall, color = SystemEmerald)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToSkills,
                        modifier = Modifier.weight(1f),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        border = BorderStroke(1.dp, SystemCyan.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = SystemCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SKILL TREE & TRIALS", style = MaterialTheme.typography.labelSmall, color = SystemCyan)
                    }
                    OutlinedButton(
                        onClick = onOpenOnboardingCalibration,
                        modifier = Modifier.weight(1f),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        border = BorderStroke(1.dp, SystemBorderDim)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditAttributes,
                            contentDescription = null,
                            tint = SystemTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RECALIBRATE PROFILE", style = MaterialTheme.typography.labelSmall, color = SystemTextSecondary)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun DashboardQuestItemCard(
    quest: QuestEntity,
    onClick: () -> Unit
) {
    val category = runCatching { QuestCategory.valueOf(quest.category) }.getOrDefault(QuestCategory.MAIN)
    val accent = when (category) {
        QuestCategory.MAIN -> SystemCyan
        QuestCategory.SIDE -> SystemViolet
        QuestCategory.RECOVERY -> SystemEmerald
        QuestCategory.CHALLENGE -> SystemGold
    }
    val primaryAttr = runCatching { AttributeType.valueOf(quest.primaryAttribute) }.getOrDefault(AttributeType.STRENGTH)
    val targetVolumeText = if (quest.isDurationSeconds) {
        val mins = quest.targetRepsOrSeconds / 60
        val secs = quest.targetRepsOrSeconds % 60
        if (mins > 0) "${quest.targetSets}x ${mins}m ${if (secs > 0) "${secs}s" else ""}".trim()
        else "${quest.targetSets} sets × ${quest.targetRepsOrSeconds} sec"
    } else {
        "${quest.targetSets} sets × ${quest.targetRepsOrSeconds} reps"
    }

    HolographicCard(
        accentColor = if (quest.isCompleted) SystemEmerald else accent,
        onClick = onClick,
        modifier = Modifier.testTag("quest_card_${quest.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = accent.copy(alpha = 0.18f),
                    border = BorderStroke(0.8.dp, accent)
                ) {
                    Text(
                        text = category.badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = quest.difficultyLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = SystemTextSecondary
                )
            }

            Text(
                text = "+${quest.xpReward} XP • ${primaryAttr.shortCode}",
                style = MaterialTheme.typography.labelLarge,
                color = if (quest.isCompleted) SystemEmerald else SystemGold,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = SystemTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = quest.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SystemTextSecondary,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (quest.isCompleted) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SystemEmerald.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, SystemEmerald)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = SystemEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CLEARED",
                            style = MaterialTheme.typography.labelMedium,
                            color = SystemEmerald
                        )
                    }
                }
            } else {
                Surface(
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    color = accent.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, accent)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Quest",
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ENGAGE",
                            style = MaterialTheme.typography.labelMedium,
                            color = accent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TARGET: $targetVolumeText • REST: ${quest.restSeconds}s",
                style = MaterialTheme.typography.labelMedium,
                color = SystemCyan
            )
            if (quest.supportsCamera) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera AI Available",
                        tint = SystemCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CAMERA AI READY (+15 XP)",
                        style = MaterialTheme.typography.labelSmall,
                        color = SystemCyan
                    )
                }
            }
        }
    }
}
