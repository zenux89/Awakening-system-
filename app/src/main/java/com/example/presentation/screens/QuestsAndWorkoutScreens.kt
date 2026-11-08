package com.example.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.camera.CameraTelemetryState
import com.example.camera.PoseMovementAnalyzer
import com.example.data.local.QuestEntity
import com.example.domain.engine.ProgressionAndQuestEngine
import com.example.domain.model.ExerciseDefinition
import com.example.domain.model.ProgressionChain
import com.example.domain.model.QuestCategory
import com.example.domain.model.VerificationMethod
import com.example.presentation.AwakenUiState
import com.example.presentation.WorkoutCompletionSummary
import com.example.presentation.components.HolographicCard
import com.example.presentation.components.RankBadgeChip
import com.example.presentation.components.SystemProgressBar
import com.example.ui.theme.SystemAmber
import com.example.ui.theme.SystemBorderDim
import com.example.ui.theme.SystemCrimson
import com.example.ui.theme.SystemCyan
import com.example.ui.theme.SystemEmerald
import com.example.ui.theme.SystemGold
import com.example.ui.theme.SystemObsidian
import com.example.ui.theme.SystemSurface
import com.example.ui.theme.SystemSurfaceElevated
import com.example.ui.theme.SystemTextPrimary
import com.example.ui.theme.SystemTextSecondary
import com.example.ui.theme.SystemViolet
import java.util.concurrent.Executors
import kotlinx.coroutines.delay

@Composable
fun DailyQuestsScreen(
    uiState: AwakenUiState,
    onSelectQuest: (QuestEntity) -> Unit,
    onCompleteWorkout: (
        quest: QuestEntity,
        sets: Int,
        repsOrSec: Int,
        method: VerificationMethod,
        rpe: Int,
        painFlag: Boolean,
        notes: String
    ) -> Unit,
    onClearActiveQuest: () -> Unit,
    onDismissSummary: () -> Unit
) {
    val summary = uiState.lastCompletionSummary
    val activeQuest = uiState.activeQuest
    val activeExercise = uiState.activeExercise

    if (summary != null) {
        WorkoutResultsSummaryScreen(
            summary = summary,
            onContinue = {
                onDismissSummary()
                onClearActiveQuest()
            }
        )
        return
    }

    if (activeQuest != null && activeExercise != null) {
        QuestExecutionFlowScreen(
            quest = activeQuest,
            exercise = activeExercise,
            onBack = onClearActiveQuest,
            onFinishWorkout = { sets, repsOrSec, method, rpe, pain, notes ->
                onCompleteWorkout(activeQuest, sets, repsOrSec, method, rpe, pain, notes)
            }
        )
        return
    }

    var selectedFilter by remember { mutableStateOf("ALL") }
    val filteredQuests = remember(uiState.todayQuests, selectedFilter) {
        if (selectedFilter == "ALL") uiState.todayQuests
        else uiState.todayQuests.filter { it.category == selectedFilter }
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
                text = "DAILY & CHALLENGE QUEST BOARD",
                style = MaterialTheme.typography.headlineMedium,
                color = SystemCyan
            )
            Text(
                text = "Select any directive to view biomechanical instructions, warm-up protocol, active timer, or Camera AI verification.",
                style = MaterialTheme.typography.bodySmall,
                color = SystemTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "ALL",
                    QuestCategory.MAIN.name to "MAIN",
                    QuestCategory.SIDE.name to "SIDE",
                    QuestCategory.RECOVERY.name to "RECOVERY",
                    QuestCategory.CHALLENGE.name to "BOSS"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        items(filteredQuests, key = { it.id }) { quest ->
            DashboardQuestItemCard(
                quest = quest,
                onClick = { onSelectQuest(quest) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun QuestExecutionFlowScreen(
    quest: QuestEntity,
    exercise: ExerciseDefinition,
    onBack: () -> Unit,
    onFinishWorkout: (
        sets: Int,
        repsOrSec: Int,
        method: VerificationMethod,
        rpe: Int,
        painFlag: Boolean,
        notes: String
    ) -> Unit
) {
    BackHandler { onBack() }

    // Sub-modes: 0 = Quest Details & Warm-up, 1 = Active Workout Timer/Sets, 2 = Camera Setup & Live Verification
    var subMode by remember { mutableIntStateOf(0) }
    var completedSets by remember { mutableIntStateOf(quest.targetSets) }
    var completedRepsOrSeconds by remember { mutableIntStateOf(quest.targetRepsOrSeconds) }
    var rpeSlider by remember { mutableFloatStateOf(6f) }
    var painOrFatigueReported by remember { mutableStateOf(false) }
    var verificationMethod by remember { mutableStateOf(VerificationMethod.SELF_REPORTED) }

    // Active Workout Set & Rest Timer state
    var currentSetIndex by remember { mutableIntStateOf(1) }
    var restSecondsRemaining by remember { mutableIntStateOf(0) }
    var durationSecondsRemaining by remember {
        mutableIntStateOf(if (quest.isDurationSeconds) quest.targetRepsOrSeconds else 0)
    }
    var isDurationTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(restSecondsRemaining) {
        if (restSecondsRemaining > 0) {
            delay(1000L)
            restSecondsRemaining--
        }
    }

    LaunchedEffect(isDurationTimerRunning, durationSecondsRemaining) {
        if (isDurationTimerRunning && durationSecondsRemaining > 0) {
            delay(1000L)
            durationSecondsRemaining--
        } else if (durationSecondsRemaining == 0) {
            isDurationTimerRunning = false
        }
    }

    if (subMode == 2) {
        CameraMovementVerificationScreen(
            quest = quest,
            exercise = exercise,
            onBackToWorkout = { subMode = 1 },
            onCompleteWithCameraReps = { detectedReps, manualDelta ->
                val finalReps = (detectedReps + manualDelta).coerceAtLeast(1)
                completedRepsOrSeconds = finalReps
                verificationMethod = if (manualDelta == 0) {
                    VerificationMethod.CAMERA_VERIFIED
                } else {
                    VerificationMethod.MANUALLY_CORRECTED
                }
                subMode = 1
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SystemObsidian)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Quests",
                    tint = SystemCyan
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.difficultyLabel.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = SystemCyan
                )
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = SystemTextPrimary
                )
            }
        }

        // Step 1: Biomechanical Briefing & Warm-up
        HolographicCard(accentColor = SystemCyan) {
            Text(
                text = "EXERCISE BRIEFING // ${exercise.name.uppercase()}",
                style = MaterialTheme.typography.titleSmall,
                color = SystemCyan
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = exercise.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = SystemTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            exercise.instructions.forEachIndexed { idx, line ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "0${idx + 1}.",
                        style = MaterialTheme.typography.labelMedium,
                        color = SystemCyan,
                        modifier = Modifier.width(26.dp)
                    )
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        color = SystemTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SystemSurfaceElevated,
                border = BorderStroke(1.dp, SystemBorderDim)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "WARM-UP PROTOCOL: ${exercise.warmUpTip}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SystemEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "EASIER ALTERNATIVE: ${exercise.regressionsTip}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SystemGold
                    )
                }
            }
        }

        // Step 2: Active Set & Timer Console + Camera AI Trigger
        HolographicCard(accentColor = SystemViolet) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE WORKOUT CONSOLE",
                    style = MaterialTheme.typography.titleSmall,
                    color = SystemViolet
                )
                Text(
                    text = "SET $currentSetIndex OF $completedSets",
                    style = MaterialTheme.typography.labelLarge,
                    color = SystemCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (quest.isDurationSeconds) {
                // Duration Timer UI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ACTIVE DURATION TIMER",
                            style = MaterialTheme.typography.labelSmall,
                            color = SystemTextSecondary
                        )
                        val mins = durationSecondsRemaining / 60
                        val secs = durationSecondsRemaining % 60
                        Text(
                            text = String.format("%02d:%02d", mins, secs),
                            style = MaterialTheme.typography.displayMedium,
                            color = SystemCyan
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { isDurationTimerRunning = !isDurationTimerRunning },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDurationTimerRunning) SystemAmber else SystemCyan,
                                contentColor = SystemObsidian
                            )
                        ) {
                            Text(if (isDurationTimerRunning) "PAUSE" else "START TIMER")
                        }
                        OutlinedButton(
                            onClick = {
                                isDurationTimerRunning = false
                                durationSecondsRemaining = quest.targetRepsOrSeconds
                            }
                        ) {
                            Text("RESET", color = SystemTextSecondary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Set & Rep Counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("COMPLETED SETS", style = MaterialTheme.typography.labelSmall, color = SystemTextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { completedSets = (completedSets - 1).coerceAtLeast(1) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease sets", tint = SystemCyan)
                        }
                        Text(
                            text = "$completedSets",
                            style = MaterialTheme.typography.headlineMedium,
                            color = SystemTextPrimary
                        )
                        IconButton(onClick = { completedSets = (completedSets + 1).coerceAtMost(10) }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase sets", tint = SystemCyan)
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (quest.isDurationSeconds) "SECONDS PER SET" else "REPS PER SET",
                        style = MaterialTheme.typography.labelSmall,
                        color = SystemTextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            val stepVal = if (quest.isDurationSeconds) 15 else 1
                            completedRepsOrSeconds = (completedRepsOrSeconds - stepVal).coerceAtLeast(1)
                        }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = SystemCyan)
                        }
                        Text(
                            text = "$completedRepsOrSeconds",
                            style = MaterialTheme.typography.headlineMedium,
                            color = SystemTextPrimary
                        )
                        IconButton(onClick = {
                            val stepVal = if (quest.isDurationSeconds) 15 else 1
                            completedRepsOrSeconds += stepVal
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = SystemCyan)
                        }
                    }
                }
            }

            // Rest Timer Trigger
            if (quest.restSeconds > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = SystemGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (restSecondsRemaining > 0) "RESTING: ${restSecondsRemaining}s REMAINING"
                            else "RECOMMENDED REST: ${quest.restSeconds}s BETWEEN SETS",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (restSecondsRemaining > 0) SystemGold else SystemTextSecondary
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            restSecondsRemaining = quest.restSeconds
                            if (currentSetIndex < completedSets) currentSetIndex++
                        },
                        border = BorderStroke(1.dp, SystemGold)
                    ) {
                        Text("LOG SET & REST", style = MaterialTheme.typography.labelSmall, color = SystemGold)
                    }
                }
            }

            // Camera AI Movement Verification Button
            if (quest.supportsCamera) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { subMode = 2 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("launch_camera_ai_button"),
                    shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SystemCyan.copy(alpha = 0.2f),
                        contentColor = SystemCyan
                    ),
                    border = BorderStroke(1.5.dp, SystemCyan)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (verificationMethod == VerificationMethod.SELF_REPORTED) {
                            "VERIFY WITH ON-DEVICE CAMERA AI (+15 BONUS XP)"
                        } else {
                            "CAMERA AI VERIFIED (${verificationMethod.shortTag}) — RE-OPEN CAMERA"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Step 3: Perceived Effort (RPE) & Pain/Safety Check
        HolographicCard(accentColor = SystemEmerald) {
            Text(
                text = "POST-SET TELEMETRY & SAFETY FEEDBACK",
                style = MaterialTheme.typography.titleSmall,
                color = SystemEmerald
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Perceived Effort (RPE): ${rpeSlider.toInt()} / 10 — " + when (rpeSlider.toInt()) {
                    in 1..4 -> "Light & Restorative"
                    in 5..7 -> "Optimal Progressive Load (2–3 reps in reserve)"
                    8 -> "Challenging (1 rep in reserve)"
                    else -> "Near Maximum / High Fatigue"
                },
                style = MaterialTheme.typography.bodySmall,
                color = SystemTextPrimary
            )
            Slider(
                value = rpeSlider,
                onValueChange = { rpeSlider = it },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(thumbColor = SystemEmerald, activeTrackColor = SystemEmerald)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = painOrFatigueReported,
                    onCheckedChange = { painOrFatigueReported = it },
                    colors = CheckboxDefaults.colors(checkedColor = SystemCrimson)
                )
                Column {
                    Text(
                        text = "Report joint pain, sharp discomfort, or dizziness",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (painOrFatigueReported) SystemCrimson else SystemTextPrimary
                    )
                    Text(
                        text = "System will safely adapt future difficulty without penalizing XP.",
                        style = MaterialTheme.typography.labelSmall,
                        color = SystemTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onFinishWorkout(
                        completedSets,
                        completedRepsOrSeconds,
                        verificationMethod,
                        rpeSlider.toInt(),
                        painOrFatigueReported,
                        if (painOrFatigueReported) "User flagged discomfort; adaptive deload scheduled." else "Clean session."
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("complete_quest_workout_button"),
                shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SystemEmerald,
                    contentColor = SystemObsidian
                )
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COMPLETE DIRECTIVE & CLAIM +${quest.xpReward + if (verificationMethod == VerificationMethod.CAMERA_VERIFIED) 15 else 0} XP",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CameraMovementVerificationScreen(
    quest: QuestEntity,
    exercise: ExerciseDefinition,
    onBackToWorkout: () -> Unit,
    onCompleteWithCameraReps: (detectedReps: Int, manualDelta: Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showSetupChecklist by remember { mutableStateOf(true) }
    var useFrontCamera by remember { mutableStateOf(true) }
    var telemetry by remember {
        mutableStateOf(
            CameraTelemetryState(
                detectedReps = 0,
                feedbackMessage = "Align full body inside the HUD frame (1.8m–2.2m away)."
            )
        )
    }

    val isPush = exercise.chain == ProgressionChain.PUSH
    val analyzer = remember(isPush) {
        PoseMovementAnalyzer(
            isPushExercise = isPush,
            onTelemetryUpdated = { newState ->
                telemetry = newState
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SystemObsidian)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Active Camera Privacy & Status Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToWorkout) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SystemCyan)
                }
                Column {
                    Text(
                        text = "CAMERA AI MOVEMENT VERIFICATION",
                        style = MaterialTheme.typography.titleSmall,
                        color = SystemCyan
                    )
                    Text(
                        text = "${exercise.name} • 100% Local In-Memory Processing",
                        style = MaterialTheme.typography.labelSmall,
                        color = SystemEmerald
                    )
                }
            }
            IconButton(onClick = { useFrontCamera = !useFrontCamera }) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Flip Camera",
                    tint = SystemCyan
                )
            }
        }

        if (showSetupChecklist) {
            HolographicCard(accentColor = SystemCyan) {
                Text(
                    text = "OPTICAL CALIBRATION & PHONE PLACEMENT GUIDE",
                    style = MaterialTheme.typography.titleSmall,
                    color = SystemCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = exercise.cameraSetupHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SystemTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Distance: 1.8m to 2.4m (6–8 ft) so shoulders, hips, knees, and ankles remain in frame.\n" +
                        "• Lighting: Ensure room light is in front or above you (avoid strong window backlight).\n" +
                        "• Privacy: Camera frames are analyzed strictly in RAM and immediately discarded. Zero recording or upload.\n" +
                        "• Honest Uncertainty: Pose analysis evaluates visual movement excursion and tempo; manual rep correction is always available below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SystemTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        showSetupChecklist = false
                        if (!hasCameraPermission) {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SystemCyan, contentColor = SystemObsidian)
                ) {
                    Text("START ON-DEVICE CAMERA VERIFICATION", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Camera Preview + Real-Time Kinematic Skeleton Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
                .background(SystemSurface)
        ) {
            if (hasCameraPermission && !showSetupChecklist) {
                val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
                DisposableEffect(Unit) {
                    onDispose {
                        cameraExecutor.shutdown()
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { previewView ->
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener({
                            runCatching {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.surfaceProvider = previewView.surfaceProvider
                                }
                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also {
                                        it.setAnalyzer(cameraExecutor, analyzer)
                                    }
                                val selector = if (useFrontCamera) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    imageAnalysis
                                )
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                )
            } else if (!hasCameraPermission && !showSetupChecklist) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = SystemGold,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Camera Permission Not Granted — Simulation & Manual Override Active",
                        style = MaterialTheme.typography.titleMedium,
                        color = SystemGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You can grant Camera permission for live optical analysis or simulate/adjust repetitions below.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SystemTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        border = BorderStroke(1.dp, SystemCyan)
                    ) {
                        Text("GRANT CAMERA PERMISSION", color = SystemCyan)
                    }
                }
            }

            // Real-time Pose Landmark & Joint Angle Overlay Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Target framing box
                val padX = w * 0.12f
                val padY = h * 0.10f
                drawLine(SystemCyan, Offset(padX, padY), Offset(padX + 40f, padY), 4f)
                drawLine(SystemCyan, Offset(padX, padY), Offset(padX, padY + 40f), 4f)
                drawLine(SystemCyan, Offset(w - padX, padY), Offset(w - padX - 40f, padY), 4f)
                drawLine(SystemCyan, Offset(w - padX, padY), Offset(w - padX, padY + 40f), 4f)
                drawLine(SystemCyan, Offset(padX, h - padY), Offset(padX + 40f, h - padY), 4f)
                drawLine(SystemCyan, Offset(padX, h - padY), Offset(padX, h - padY - 40f), 4f)
                drawLine(SystemCyan, Offset(w - padX, h - padY), Offset(w - padX - 40f, h - padY), 4f)
                drawLine(SystemCyan, Offset(w - padX, h - padY), Offset(w - padX, h - padY - 40f), 4f)

                val pts = telemetry.landmarks
                if (pts.size >= 6) {
                    for (i in 0 until pts.size - 1) {
                        val p1 = Offset(pts[i].normalizedX * w, pts[i].normalizedY * h)
                        val p2 = Offset(pts[i + 1].normalizedX * w, pts[i + 1].normalizedY * h)
                        drawLine(
                            color = SystemCyan.copy(alpha = 0.85f),
                            start = p1,
                            end = p2,
                            strokeWidth = 6f,
                            cap = StrokeCap.Round
                        )
                    }
                    pts.forEach { pt ->
                        val c = Offset(pt.normalizedX * w, pt.normalizedY * h)
                        drawCircle(color = SystemViolet, radius = 11f, center = c)
                        drawCircle(color = SystemCyan, radius = 6f, center = c)
                    }
                }
            }

            // Top HUD Telemetry Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SystemObsidian.copy(alpha = 0.82f),
                    border = BorderStroke(1.dp, SystemCyan)
                ) {
                    Text(
                        text = "STATE: ${telemetry.repPhase.badge} • ANGLE: ${telemetry.primaryJointAngleDeg}°",
                        style = MaterialTheme.typography.labelMedium,
                        color = SystemCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SystemObsidian.copy(alpha = 0.82f),
                    border = BorderStroke(1.dp, if (telemetry.lightingAdequate) SystemEmerald else SystemAmber)
                ) {
                    Text(
                        text = "CONF: ${(telemetry.visibilityConfidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (telemetry.lightingAdequate) SystemEmerald else SystemAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Bottom Rep Counter & Manual Correction Bar
        HolographicCard(accentColor = SystemEmerald) {
            Text(
                text = telemetry.feedbackMessage,
                style = MaterialTheme.typography.bodySmall,
                color = SystemCyan
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VERIFIED REPS (TARGET: ${quest.targetRepsOrSeconds})",
                        style = MaterialTheme.typography.labelSmall,
                        color = SystemTextSecondary
                    )
                    Text(
                        text = "${telemetry.totalEffectiveReps} REPS",
                        style = MaterialTheme.typography.displaySmall,
                        color = SystemEmerald
                    )
                }

                // Manual Correction Controls (Required by specification when detection under/over counts)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MANUAL ADJUST:",
                        style = MaterialTheme.typography.labelSmall,
                        color = SystemTextSecondary
                    )
                    IconButton(onClick = {
                        analyzer.adjustRepsManually(-1)
                        telemetry = telemetry.copy(manualAdjustmentDelta = telemetry.manualAdjustmentDelta - 1)
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Subtract Rep", tint = SystemGold)
                    }
                    IconButton(onClick = {
                        analyzer.adjustRepsManually(1)
                        telemetry = telemetry.copy(manualAdjustmentDelta = telemetry.manualAdjustmentDelta + 1)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Rep", tint = SystemGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val effective = telemetry.totalEffectiveReps.coerceAtLeast(quest.targetRepsOrSeconds)
                    onCompleteWithCameraReps(effective, telemetry.manualAdjustmentDelta)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_camera_reps_button"),
                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SystemEmerald, contentColor = SystemObsidian)
            ) {
                Icon(Icons.Default.Verified, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CONFIRM CAMERA VERIFICATION & APPLY TO WORKOUT",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WorkoutResultsSummaryScreen(
    summary: WorkoutCompletionSummary,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SystemObsidian)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        HolographicCard(accentColor = SystemGold) {
            Text(
                text = "[DIRECTIVE CLEARED]",
                style = MaterialTheme.typography.labelLarge,
                color = SystemGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = summary.questTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = SystemTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SystemSurfaceElevated,
                border = BorderStroke(1.dp, SystemCyan)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "REWARD INTEGRATED: +${summary.xpEarned} XP",
                        style = MaterialTheme.typography.titleLarge,
                        color = SystemCyan
                    )
                    Text(
                        text = "Primary Attribute: ${summary.primaryAttribute.displayName} (+${(summary.xpEarned * 0.7f).toInt()} XP)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = summary.primaryAttribute.color
                    )
                    Text(
                        text = "Secondary Attribute: ${summary.secondaryAttribute.displayName} (+${(summary.xpEarned * 0.3f).toInt()} XP)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = summary.secondaryAttribute.color
                    )
                    Text(
                        text = "Verification Mode: ${summary.verificationMethod.label}",
                        style = MaterialTheme.typography.labelMedium,
                        color = SystemEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (summary.newLevel > summary.previousLevel) {
                Text(
                    text = "LEVEL UP ACHIEVED: LV.${summary.previousLevel} → LV.${summary.newLevel}!",
                    style = MaterialTheme.typography.titleMedium,
                    color = SystemGold
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            if (summary.newRank != summary.previousRank) {
                RankBadgeChip(rank = summary.newRank)
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = summary.progressionNote,
                style = MaterialTheme.typography.bodySmall,
                color = SystemTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("workout_summary_continue_button"),
                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SystemCyan, contentColor = SystemObsidian)
            ) {
                Text("RETURN TO SYSTEM", fontWeight = FontWeight.Bold)
            }
        }
    }
}
