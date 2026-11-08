package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.presentation.AwakenViewModel
import com.example.presentation.components.SystemNotificationBanner
import com.example.presentation.screens.CharacterStatusAndRankScreen
import com.example.presentation.screens.CodexAndSettingsScreen
import com.example.presentation.screens.DashboardScreen
import com.example.presentation.screens.DailyQuestsScreen
import com.example.presentation.screens.OnboardingAndAssessmentScreen
import com.example.presentation.screens.SkillTreeAndTrialsScreen
import com.example.presentation.screens.SplashAndSystemInitScreen
import com.example.ui.theme.AwakenSystemTheme
import com.example.ui.theme.SystemCharcoal
import com.example.ui.theme.SystemCyan
import com.example.ui.theme.SystemObsidian
import com.example.ui.theme.SystemSurfaceElevated
import com.example.ui.theme.SystemTextMuted

enum class SystemNavTab(val route: String, val label: String, val icon: ImageVector) {
    SYSTEM("system", "System", Icons.Default.Dashboard),
    QUESTS("quests", "Quests", Icons.Default.Assignment),
    STATUS("status", "Status", Icons.Default.Shield),
    SKILLS("skills", "Skills", Icons.Default.AccountTree),
    CODEX("codex", "Codex", Icons.Default.MenuBook)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val awakenViewModel: AwakenViewModel = viewModel()
            AwakenAppRoot(viewModel = awakenViewModel)
        }
    }
}

@Composable
fun AwakenAppRoot(viewModel: AwakenViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showSplashInit by rememberSaveable { mutableStateOf(true) }
    var showOnboardingModal by rememberSaveable { mutableStateOf(false) }
    var currentTab by rememberSaveable { mutableStateOf(SystemNavTab.SYSTEM) }
    var codexInitialSubTab by rememberSaveable { mutableStateOf("HISTORY") }

    AwakenSystemTheme(reducedAnimations = uiState.profile.reducedAnimations) {
        if (showSplashInit) {
            SplashAndSystemInitScreen(
                onInitializationComplete = { showSplashInit = false },
                onEnterOnboarding = {
                    showSplashInit = false
                    showOnboardingModal = true
                },
                isOnboardingCompleted = uiState.profile.onboardingCompleted
            )
            return@AwakenSystemTheme
        }

        if (showOnboardingModal) {
            BackHandler { showOnboardingModal = false }
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = SystemObsidian
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    OnboardingAndAssessmentScreen(
                        initialProfile = uiState.profile,
                        onComplete = { updatedProfile ->
                            viewModel.completeOnboardingAndAssessment(updatedProfile)
                            showOnboardingModal = false
                        },
                        onCancel = { showOnboardingModal = false }
                    )
                }
            }
            return@AwakenSystemTheme
        }

        // Handle back button on secondary tabs to return to central System Dashboard
        if (currentTab != SystemNavTab.SYSTEM) {
            BackHandler {
                viewModel.selectActiveQuest(null)
                currentTab = SystemNavTab.SYSTEM
            }
        }

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = SystemObsidian,
            bottomBar = {
                NavigationBar(
                    containerColor = SystemCharcoal,
                    tonalElevation = 8.dp
                ) {
                    SystemNavTab.entries.forEach { tab ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (tab != SystemNavTab.QUESTS) {
                                    viewModel.selectActiveQuest(null)
                                }
                                currentTab = tab
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SystemCyan,
                                selectedTextColor = SystemCyan,
                                indicatorColor = SystemSurfaceElevated,
                                unselectedIconColor = SystemTextMuted,
                                unselectedTextColor = SystemTextMuted
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SystemNotificationBanner(
                    event = uiState.bannerEvent,
                    onDismiss = viewModel::dismissBanner
                )

                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        SystemNavTab.SYSTEM -> DashboardScreen(
                            uiState = uiState,
                            onOpenQuest = { quest ->
                                viewModel.selectActiveQuest(quest.id)
                                currentTab = SystemNavTab.QUESTS
                            },
                            onNavigateToQuests = { currentTab = SystemNavTab.QUESTS },
                            onNavigateToStatus = { currentTab = SystemNavTab.STATUS },
                            onNavigateToSkills = { currentTab = SystemNavTab.SKILLS },
                            onNavigateToRecovery = {
                                codexInitialSubTab = "RECOVERY"
                                currentTab = SystemNavTab.CODEX
                            },
                            onOpenOnboardingCalibration = { showOnboardingModal = true },
                            onTriggerSafetyDeload = {
                                viewModel.triggerSafetyDeloadRecalibration("User initiated dashboard safety deload")
                            }
                        )

                        SystemNavTab.QUESTS -> DailyQuestsScreen(
                            uiState = uiState,
                            onSelectQuest = { quest -> viewModel.selectActiveQuest(quest.id) },
                            onCompleteWorkout = { quest, sets, repsOrSec, method, rpe, painFlag, notes ->
                                viewModel.recordQuestWorkoutCompletion(
                                    quest = quest,
                                    completedSets = sets,
                                    completedRepsOrSeconds = repsOrSec,
                                    verificationMethod = method,
                                    perceivedEffortRpe = rpe,
                                    painOrFatigueFlag = painFlag,
                                    safetyNotes = notes
                                )
                            },
                            onClearActiveQuest = { viewModel.selectActiveQuest(null) },
                            onDismissSummary = viewModel::clearCompletionSummary
                        )

                        SystemNavTab.STATUS -> CharacterStatusAndRankScreen(
                            uiState = uiState,
                            onOpenCalibration = { showOnboardingModal = true }
                        )

                        SystemNavTab.SKILLS -> SkillTreeAndTrialsScreen(
                            uiState = uiState,
                            onSelectChainTier = viewModel::updateChainTierManually
                        )

                        SystemNavTab.CODEX -> CodexAndSettingsScreen(
                            uiState = uiState,
                            initialTab = codexInitialSubTab,
                            onLogRecovery = viewModel::logRecoveryCheckIn,
                            onToggleReducedAnimations = viewModel::toggleReducedAnimations,
                            onOpenProfileCalibration = { showOnboardingModal = true },
                            onGenerateBackupJson = viewModel::generateBackupJson,
                            onImportBackupJson = viewModel::importBackupJson
                        )
                    }
                }
            }
        }
    }
}
