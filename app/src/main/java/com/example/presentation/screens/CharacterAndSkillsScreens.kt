package com.example.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AttributeType
import com.example.domain.model.HunterRank
import com.example.domain.model.ProgressionChain
import com.example.domain.model.SkillNode
import com.example.presentation.AwakenUiState
import com.example.presentation.components.HolographicCard
import com.example.presentation.components.PentagonAttributeRadar
import com.example.presentation.components.RankBadgeChip
import com.example.presentation.components.SystemProgressBar
import com.example.ui.theme.SystemBorderDim
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
fun CharacterStatusAndRankScreen(
    uiState: AwakenUiState,
    onOpenCalibration: () -> Unit
) {
    val status = uiState.characterStatus
    var subTab by remember { mutableStateOf("ATTRIBUTES") } // ATTRIBUTES, RANKS, ACHIEVEMENTS

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SystemObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HolographicCard(accentColor = status.rank.color) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VESSEL STATUS // ${status.codename}",
                            style = MaterialTheme.typography.labelLarge,
                            color = SystemCyan
                        )
                        Text(
                            text = "LEVEL ${status.level} (${status.totalXp} TOTAL XP)",
                            style = MaterialTheme.typography.headlineMedium,
                            color = SystemTextPrimary
                        )
                    }
                    RankBadgeChip(rank = status.rank)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status.rank.motto,
                    style = MaterialTheme.typography.bodySmall,
                    color = SystemTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                PentagonAttributeRadar(attributes = status.attributes)
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ATTRIBUTES" to "5 ATTRIBUTES",
                    "RANKS" to "RANK ASCENSION",
                    "ACHIEVEMENTS" to "ACHIEVEMENTS"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = subTab == key,
                        onClick = { subTab = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        when (subTab) {
            "ATTRIBUTES" -> {
                items(AttributeType.entries) { attr ->
                    val prog = status.attributes[attr]
                    HolographicCard(accentColor = attr.color) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${attr.displayName.uppercase()} [${attr.shortCode}]",
                                style = MaterialTheme.typography.titleLarge,
                                color = attr.color
                            )
                            Text(
                                text = "LV.${prog?.level ?: 1} • ${prog?.currentLevelXp ?: 0}/${prog?.nextLevelRequirementXp ?: 90} XP",
                                style = MaterialTheme.typography.labelLarge,
                                color = SystemTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        SystemProgressBar(
                            progress = prog?.progressFraction ?: 0.2f,
                            color = attr.color,
                            height = 7.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = attr.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Trained via: ${attr.trainingFocus}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SystemTextSecondary
                        )
                    }
                }
            }

            "RANKS" -> {
                item {
                    Text(
                        text = "Rank ascension requires actual physical milestones, balanced attribute development, and consistent training — never mere XP grinding.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SystemTextSecondary
                    )
                }
                items(HunterRank.entries) { rank ->
                    val isUnlocked = status.rank.ordinal >= rank.ordinal
                    val minAttrLv = status.attributes.values.minOfOrNull { it.level } ?: 1
                    HolographicCard(accentColor = if (isUnlocked) rank.color else SystemBorderDim) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RankBadgeChip(rank = rank)
                            Text(
                                text = if (status.rank == rank) "ACTIVE RANK" else if (isUnlocked) "UNLOCKED" else "LOCKED",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isUnlocked) SystemEmerald else SystemTextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rank.motto,
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            MilestoneCheckRow(
                                label = "System Level ≥ ${rank.minLevel} (Current: Lv.${status.level})",
                                met = status.level >= rank.minLevel
                            )
                            MilestoneCheckRow(
                                label = "All 5 Attributes ≥ Lv.${rank.minMinAttributeLevel} (Lowest: Lv.$minAttrLv)",
                                met = minAttrLv >= rank.minMinAttributeLevel
                            )
                            MilestoneCheckRow(
                                label = "Completed Workouts ≥ ${rank.minCompletedWorkouts} (Current: ${status.completedWorkoutsCount})",
                                met = status.completedWorkoutsCount >= rank.minCompletedWorkouts
                            )
                            MilestoneCheckRow(
                                label = "Consistency Streak ≥ ${rank.minStreakDays} Days (Current: ${status.streakDays}d)",
                                met = status.streakDays >= rank.minStreakDays
                            )
                            MilestoneCheckRow(
                                label = "Boss Evaluations Cleared ≥ ${rank.requiredBossEvaluations} (Current: ${status.completedBossEvaluations})",
                                met = status.completedBossEvaluations >= rank.requiredBossEvaluations
                            )
                        }
                    }
                }
            }

            "ACHIEVEMENTS" -> {
                items(uiState.achievements, key = { it.id }) { ach ->
                    HolographicCard(accentColor = if (ach.isUnlocked) SystemGold else SystemBorderDim) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (ach.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (ach.isUnlocked) SystemGold else SystemTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = ach.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (ach.isUnlocked) SystemTextPrimary else SystemTextSecondary
                                    )
                                    Text(
                                        text = ach.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SystemCyan
                                    )
                                }
                            }
                            Text(
                                text = ach.progressText,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (ach.isUnlocked) SystemEmerald else SystemGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = ach.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextSecondary
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun MilestoneCheckRow(label: String, met: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.Star,
            contentDescription = null,
            tint = if (met) SystemEmerald else SystemTextMuted,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (met) SystemEmerald else SystemTextSecondary
        )
    }
}

@Composable
fun SkillTreeAndTrialsScreen(
    uiState: AwakenUiState,
    onSelectChainTier: (ProgressionChain, Int) -> Unit
) {
    var selectedChain by remember { mutableStateOf(ProgressionChain.PUSH) }
    val chainSkills = remember(uiState.skillTree, selectedChain) {
        uiState.skillTree.filter { it.chain == selectedChain }.sortedBy { it.tier }
    }
    val currentActiveTier = when (selectedChain) {
        ProgressionChain.PUSH -> uiState.profile.pushChainTier
        ProgressionChain.SQUAT -> uiState.profile.squatChainTier
        ProgressionChain.ENDURANCE -> uiState.profile.enduranceChainTier
        ProgressionChain.CORE -> uiState.profile.coreChainTier
        ProgressionChain.MOBILITY -> uiState.profile.mobilityChainTier
    }

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
                text = "PROGRESSIVE CALISTHENICS & STAMINA SKILL TREE",
                style = MaterialTheme.typography.headlineSmall,
                color = SystemCyan
            )
            Text(
                text = "Skills unlock automatically as you master earlier variations, or you can manually set any variation to match your daily recovery.",
                style = MaterialTheme.typography.bodySmall,
                color = SystemTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ProgressionChain.entries.forEach { chain ->
                    val shortLabel = when (chain) {
                        ProgressionChain.PUSH -> "PUSH"
                        ProgressionChain.SQUAT -> "SQUAT"
                        ProgressionChain.ENDURANCE -> "ENDURE"
                        ProgressionChain.CORE -> "CORE"
                        ProgressionChain.MOBILITY -> "MOBILITY"
                    }
                    FilterChip(
                        selected = selectedChain == chain,
                        onClick = { selectedChain = chain },
                        label = { Text(shortLabel, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        items(chainSkills, key = { it.id }) { node ->
            SkillNodeCard(
                node = node,
                isActiveVariation = node.tier == currentActiveTier,
                onActivateTier = { onSelectChainTier(node.chain, node.tier) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun SkillNodeCard(
    node: SkillNode,
    isActiveVariation: Boolean,
    onActivateTier: () -> Unit
) {
    val accent = when {
        isActiveVariation -> SystemCyan
        node.isMastered -> SystemEmerald
        node.isUnlocked -> SystemViolet
        else -> SystemBorderDim
    }

    HolographicCard(accentColor = accent) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (node.isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "TIER ${node.tier} // ${node.title.uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = SystemTextPrimary
                    )
                    Text(
                        text = node.milestoneCondition,
                        style = MaterialTheme.typography.labelSmall,
                        color = SystemTextSecondary
                    )
                }
            }

            if (isActiveVariation) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SystemCyan.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, SystemCyan)
                ) {
                    Text(
                        text = "ACTIVE IN QUESTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = SystemCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onActivateTier,
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    border = BorderStroke(1.dp, accent)
                ) {
                    Text(
                        text = if (node.isMastered) "REVISIT TIER" else "SET AS ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = node.description,
            style = MaterialTheme.typography.bodySmall,
            color = SystemTextSecondary
        )
    }
}
