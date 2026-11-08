package com.example.presentation.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.model.UserProfile
import com.example.presentation.components.HolographicCard
import com.example.presentation.components.SystemProgressBar
import com.example.ui.theme.SystemBorderDim
import com.example.ui.theme.SystemCyan
import com.example.ui.theme.SystemEmerald
import com.example.ui.theme.SystemGold
import com.example.ui.theme.SystemObsidian
import com.example.ui.theme.SystemSurface
import com.example.ui.theme.SystemTextMuted
import com.example.ui.theme.SystemTextPrimary
import com.example.ui.theme.SystemTextSecondary
import com.example.ui.theme.SystemViolet
import kotlinx.coroutines.delay

@Composable
fun SplashAndSystemInitScreen(
    onInitializationComplete: () -> Unit,
    onEnterOnboarding: () -> Unit,
    isOnboardingCompleted: Boolean
) {
    var bootProgress by remember { mutableFloatStateOf(0.15f) }
    var statusLine by remember { mutableStateOf("BOOTING LOCAL DETERMINISTIC ENGINE...") }

    LaunchedEffect(isOnboardingCompleted) {
        delay(220)
        bootProgress = 0.45f
        statusLine = "LOADING OFFLINE BIOMECHANICAL RULESETS..."
        delay(240)
        bootProgress = 0.80f
        statusLine = "VERIFYING ROOM SQLITE & CAMERAX PIPELINE..."
        delay(240)
        bootProgress = 1.0f
        statusLine = "SYSTEM AWAKENING READY."
        if (isOnboardingCompleted) {
            delay(200)
            onInitializationComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SystemObsidian, SystemSurface, SystemObsidian)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        HolographicCard(accentColor = SystemCyan) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "System Emblem",
                    tint = SystemCyan,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AWAKEN — THE SYSTEM",
                        style = MaterialTheme.typography.headlineLarge,
                        color = SystemCyan
                    )
                    Text(
                        text = "OFFLINE REAL-WORLD FITNESS PROGRESSION RPG",
                        style = MaterialTheme.typography.labelMedium,
                        color = SystemTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "[SYSTEM NOTIFICATION]\nYou have met the requirements to begin physical vessel calibration. All data, camera movement verification, and quest generation execute 100% locally on your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = SystemTextPrimary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = statusLine,
                style = MaterialTheme.typography.labelMedium,
                color = SystemCyan
            )
            Spacer(modifier = Modifier.height(6.dp))
            SystemProgressBar(progress = bootProgress, color = SystemCyan, height = 8.dp)

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isOnboardingCompleted) {
                    Button(
                        onClick = onEnterOnboarding,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("accept_awakening_button"),
                        shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SystemCyan,
                            contentColor = SystemObsidian
                        )
                    ) {
                        Text(
                            text = "CALIBRATE VESSEL",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
                OutlinedButton(
                    onClick = onInitializationComplete,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("enter_dashboard_button"),
                    shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                    border = BorderStroke(1.dp, SystemCyan.copy(alpha = 0.7f))
                ) {
                    Text(
                        text = if (isOnboardingCompleted) "ENTER SYSTEM" else "QUICK START (DEFAULT)",
                        style = MaterialTheme.typography.labelLarge,
                        color = SystemCyan
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingAndAssessmentScreen(
    initialProfile: UserProfile,
    onComplete: (UserProfile) -> Unit,
    onCancel: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1 = Profile, 2 = Schedule & Safety, 3 = Conservative Assessment

    var codename by remember { mutableStateOf(initialProfile.codename) }
    var ageText by remember { mutableStateOf(initialProfile.age.toString()) }
    var heightText by remember { mutableStateOf(initialProfile.heightCm.toString()) }
    var weightText by remember { mutableStateOf(initialProfile.weightKg?.toInt()?.toString() ?: "") }
    var experience by remember { mutableStateOf(initialProfile.fitnessExperience) }
    var selectedGoals by remember { mutableStateOf(initialProfile.primaryGoals) }
    var minutesPerDay by remember { mutableFloatStateOf(initialProfile.availableMinutesPerDay.toFloat()) }
    var daysPerWeek by remember { mutableFloatStateOf(initialProfile.trainingDaysPerWeek.toFloat()) }
    var selectedEquipment by remember { mutableStateOf(initialProfile.equipment) }
    var environmentPref by remember { mutableStateOf(initialProfile.environmentPreference) }
    var selectedLimitations by remember { mutableStateOf(initialProfile.limitationsOrPainAreas) }

    // Conservative Assessment Baselines
    var pushTier by remember { mutableIntStateOf(initialProfile.pushChainTier) }
    var squatTier by remember { mutableIntStateOf(initialProfile.squatChainTier) }
    var enduranceTier by remember { mutableIntStateOf(initialProfile.enduranceChainTier) }
    var coreTier by remember { mutableIntStateOf(initialProfile.coreChainTier) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SystemObsidian)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VESSEL CALIBRATION • STEP $step OF 3",
                    style = MaterialTheme.typography.labelLarge,
                    color = SystemCyan
                )
                Text(
                    text = when (step) {
                        1 -> "Biometrics & Primary Objectives"
                        2 -> "Schedule, Equipment & Safety Guardrails"
                        else -> "Conservative Baseline Assessment"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = SystemTextPrimary
                )
            }
            OutlinedButton(
                onClick = onCancel,
                border = BorderStroke(1.dp, SystemBorderDim)
            ) {
                Text("SKIP", style = MaterialTheme.typography.labelMedium, color = SystemTextSecondary)
            }
        }

        SystemProgressBar(progress = step / 3f, color = SystemCyan, height = 6.dp)

        if (step == 1) {
            HolographicCard(accentColor = SystemCyan) {
                Text(
                    text = "1. IDENTITY & BIOMETRICS (STORED LOCALLY)",
                    style = MaterialTheme.typography.titleSmall,
                    color = SystemCyan
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = codename,
                    onValueChange = { codename = it },
                    label = { Text("Hunter Codename / Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_codename_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SystemCyan,
                        unfocusedBorderColor = SystemBorderDim
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { heightText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Height (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Weight kg (Opt)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Current Fitness Experience",
                    style = MaterialTheme.typography.titleSmall,
                    color = SystemTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                val expOptions = listOf(
                    "Complete Beginner (Starting from zero)",
                    "Beginner (Rebuilding consistency)",
                    "Intermediate (Regular exercise)",
                    "Advanced (Structured calisthenics/running)"
                )
                expOptions.forEach { option ->
                    val selected = experience == option
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { experience = option },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) SystemCyan.copy(alpha = 0.16f) else SystemObsidian,
                        border = BorderStroke(1.dp, if (selected) SystemCyan else SystemBorderDim)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (selected) SystemCyan else SystemTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = option, style = MaterialTheme.typography.bodyMedium, color = SystemTextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Primary Training Objectives",
                    style = MaterialTheme.typography.titleSmall,
                    color = SystemTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                val goalOptions = listOf(
                    "Full-Body Strength",
                    "Cardio Endurance",
                    "Flexibility & Mobility",
                    "Core & Balance",
                    "Consistency & Discipline"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    goalOptions.forEach { goal ->
                        val isSelected = goal in selectedGoals
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedGoals = if (isSelected && selectedGoals.size > 1) {
                                    selectedGoals - goal
                                } else {
                                    selectedGoals + goal
                                }
                            },
                            label = { Text(goal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SystemCyan.copy(alpha = 0.2f),
                                selectedLabelColor = SystemCyan
                            )
                        )
                    }
                }
            }
        } else if (step == 2) {
            HolographicCard(accentColor = SystemViolet) {
                Text(
                    text = "2. AVAILABILITY, ENVIRONMENT & JOINT GUARDRAILS",
                    style = MaterialTheme.typography.titleSmall,
                    color = SystemViolet
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Available Daily Workout Time: ${minutesPerDay.toInt()} minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SystemTextPrimary
                )
                Slider(
                    value = minutesPerDay,
                    onValueChange = { minutesPerDay = it },
                    valueRange = 10f..60f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = SystemCyan, activeTrackColor = SystemCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Target Training Days per Week: ${daysPerWeek.toInt()} days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SystemTextPrimary
                )
                Slider(
                    value = daysPerWeek,
                    onValueChange = { daysPerWeek = it },
                    valueRange = 2f..6f,
                    steps = 3,
                    colors = SliderDefaults.colors(thumbColor = SystemViolet, activeTrackColor = SystemViolet)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Available Equipment & Space",
                    style = MaterialTheme.typography.titleSmall,
                    color = SystemTextSecondary
                )
                val equipList = listOf(
                    "None (Bodyweight Only)",
                    "Wall / Elevated Surface",
                    "Sturdy Chair / Box",
                    "Exercise Mat",
                    "Pull-Up Bar",
                    "Dumbbells / Bands"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    equipList.forEach { item ->
                        val selected = item in selectedEquipment
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedEquipment = if (selected) selectedEquipment - item else selectedEquipment + item
                            },
                            label = { Text(item) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Training Environment Preference",
                    style = MaterialTheme.typography.titleSmall,
                    color = SystemTextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Indoor Only", "Indoor & Outdoor Hybrid", "Outdoor Focus").forEach { env ->
                        FilterChip(
                            selected = environmentPref == env,
                            onClick = { environmentPref = env },
                            label = { Text(env) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = SystemGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sensitive Joints / Movements to Protect (Optional)",
                        style = MaterialTheme.typography.titleSmall,
                        color = SystemGold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                val limitationOptions = listOf(
                    "Wrists (Prefer Wall/Incline over Floor)",
                    "Knees (Prefer Supported/Shallow Squats)",
                    "Lower Back (Avoid Spinal Flexion Load)",
                    "Shoulders (Conservative Pressing Range)",
                    "Ankles / Shins (Low-Impact Walking Only)"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    limitationOptions.forEach { lim ->
                        val isSel = lim in selectedLimitations
                        FilterChip(
                            selected = isSel,
                            onClick = {
                                selectedLimitations = if (isSel) selectedLimitations - lim else selectedLimitations + lim
                            },
                            label = { Text(lim) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SystemGold.copy(alpha = 0.2f),
                                selectedLabelColor = SystemGold
                            )
                        )
                    }
                }
            }
        } else {
            HolographicCard(accentColor = SystemEmerald) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SystemEmerald
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3. CONSERVATIVE CAPABILITY CALIBRATION",
                        style = MaterialTheme.typography.titleSmall,
                        color = SystemEmerald
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Never perform a maximum-effort test. Choose the variation you can currently perform comfortably for 6–8 controlled reps with zero pain.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SystemTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))
                CapabilitySelectorGroup(
                    title = "Upper-Body Push Starting Variation",
                    options = listOf(
                        1 to "Tier 1: Wall Push-Ups (Conservative Joint-Safe Start)",
                        2 to "Tier 2: Incline Push-Ups (Desk / Countertop height)",
                        3 to "Tier 3: Controlled Knee Push-Ups",
                        4 to "Tier 4: Standard Floor Push-Ups (8+ clean reps)"
                    ),
                    selectedTier = pushTier,
                    onSelect = { pushTier = it }
                )

                Spacer(modifier = Modifier.height(14.dp))
                CapabilitySelectorGroup(
                    title = "Lower-Body Squat Starting Variation",
                    options = listOf(
                        1 to "Tier 1: Supported Sit-to-Stand (Chair assisted)",
                        2 to "Tier 2: Bodyweight Squats (Comfortable depth)",
                        3 to "Tier 3: 3-1-1 Tempo Squats (Paused control)",
                        4 to "Tier 4: Split Squats / Lunges"
                    ),
                    selectedTier = squatTier,
                    onSelect = { squatTier = it }
                )

                Spacer(modifier = Modifier.height(14.dp))
                CapabilitySelectorGroup(
                    title = "Cardiovascular Endurance Starting Variation",
                    options = listOf(
                        1 to "Tier 1: 10-Min Comfortable Awakening Walk",
                        2 to "Tier 2: 15-Min Extended Steady Walk",
                        3 to "Tier 3: 20-Min Brisk Tempo Walk",
                        4 to "Tier 4: Walk / Jog Adaptation Intervals"
                    ),
                    selectedTier = enduranceTier,
                    onSelect = { enduranceTier = it }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (step > 1) {
                OutlinedButton(
                    onClick = { step-- },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
                ) {
                    Text("BACK", color = SystemTextPrimary)
                }
            }
            Button(
                onClick = {
                    if (step < 3) {
                        step++
                    } else {
                        val finalPush = if ("Wrists (Prefer Wall/Incline over Floor)" in selectedLimitations) {
                            pushTier.coerceAtMost(2)
                        } else pushTier
                        val finalSquat = if ("Knees (Prefer Supported/Shallow Squats)" in selectedLimitations) {
                            squatTier.coerceAtMost(2)
                        } else squatTier

                        onComplete(
                            initialProfile.copy(
                                codename = codename.ifBlank { "PLAYER-01" },
                                age = ageText.toIntOrNull()?.coerceIn(13, 99) ?: 25,
                                heightCm = heightText.toIntOrNull()?.coerceIn(100, 240) ?: 172,
                                weightKg = weightText.toFloatOrNull(),
                                fitnessExperience = experience,
                                primaryGoals = selectedGoals,
                                availableMinutesPerDay = minutesPerDay.toInt(),
                                equipment = selectedEquipment,
                                environmentPreference = environmentPref,
                                trainingDaysPerWeek = daysPerWeek.toInt(),
                                limitationsOrPainAreas = selectedLimitations,
                                pushChainTier = finalPush,
                                squatChainTier = finalSquat,
                                enduranceChainTier = enduranceTier,
                                coreChainTier = coreTier,
                                onboardingCompleted = true
                            )
                        )
                    }
                },
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .testTag("onboarding_next_button"),
                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SystemCyan,
                    contentColor = SystemObsidian
                )
            ) {
                Text(
                    text = if (step < 3) "CONTINUE CALIBRATION" else "AWAKEN SYSTEM PROFILE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CapabilitySelectorGroup(
    title: String,
    options: List<Pair<Int, String>>,
    selectedTier: Int,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = SystemCyan
        )
        options.forEach { (tier, label) ->
            val selected = selectedTier == tier
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(tier) },
                shape = RoundedCornerShape(8.dp),
                color = if (selected) SystemEmerald.copy(alpha = 0.18f) else SystemObsidian,
                border = BorderStroke(1.dp, if (selected) SystemEmerald else SystemBorderDim)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (selected) SystemEmerald else SystemTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SystemTextPrimary
                    )
                }
            }
        }
    }
}
