package com.example.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AwakenDatabase
import com.example.data.local.QuestEntity
import com.example.data.local.RecoveryLogEntity
import com.example.data.local.StoredSystemState
import com.example.data.local.SystemPreferencesDataStore
import com.example.data.local.WorkoutLogEntity
import com.example.domain.engine.ProgressionAndQuestEngine
import com.example.domain.model.Achievement
import com.example.domain.model.AttributeType
import com.example.domain.model.CharacterStatusState
import com.example.domain.model.ExerciseDefinition
import com.example.domain.model.HunterRank
import com.example.domain.model.LevelMath
import com.example.domain.model.ProgressionChain
import com.example.domain.model.SkillNode
import com.example.domain.model.UserProfile
import com.example.domain.model.VerificationMethod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class SystemBannerEvent(
    val title: String,
    val subtitle: String,
    val isRankUp: Boolean = false,
    val isLevelUp: Boolean = false
)

data class WorkoutCompletionSummary(
    val questTitle: String,
    val exerciseName: String,
    val completedSets: Int,
    val completedRepsOrSeconds: Int,
    val isDurationSeconds: Boolean,
    val verificationMethod: VerificationMethod,
    val xpEarned: Int,
    val primaryAttribute: AttributeType,
    val secondaryAttribute: AttributeType,
    val previousLevel: Int,
    val newLevel: Int,
    val previousRank: HunterRank,
    val newRank: HunterRank,
    val progressionNote: String
)

data class AwakenUiState(
    val isInitialized: Boolean = false,
    val todayDateKey: String = "",
    val profile: UserProfile = UserProfile(),
    val characterStatus: CharacterStatusState = CharacterStatusState(
        codename = "PLAYER-01",
        totalXp = 45,
        level = 1,
        currentLevelXp = 45,
        nextLevelXp = 145,
        rank = HunterRank.E_AWAKENING,
        nextRank = HunterRank.D_FOUNDATION,
        attributes = AttributeType.entries.associateWith { LevelMath.calculateAttributeProgress(it, 20) },
        streakDays = 1,
        completedWorkoutsCount = 0,
        completedBossEvaluations = 0
    ),
    val todayQuests: List<QuestEntity> = emptyList(),
    val workoutHistory: List<WorkoutLogEntity> = emptyList(),
    val recoveryLogs: List<RecoveryLogEntity> = emptyList(),
    val skillTree: List<SkillNode> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val activeQuest: QuestEntity? = null,
    val activeExercise: ExerciseDefinition? = null,
    val lastCompletionSummary: WorkoutCompletionSummary? = null,
    val bannerEvent: SystemBannerEvent? = null,
    val backupJsonText: String = ""
)

class AwakenViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AwakenDatabase.getInstance(application)
    private val dao = database.awakenDao()
    private val prefs = SystemPreferencesDataStore(application)

    private val todayKey: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private val _activeQuestId = MutableStateFlow<String?>(null)
    private val _lastSummary = MutableStateFlow<WorkoutCompletionSummary?>(null)
    private val _bannerEvent = MutableStateFlow<SystemBannerEvent?>(null)
    private val _backupJsonText = MutableStateFlow("")

    private val coreDataFlow = combine(
        prefs.systemStateFlow,
        dao.observeQuestsForDate(todayKey),
        dao.observeWorkoutLogs(),
        dao.observeRecoveryLogs()
    ) { storedState, quests, workoutLogs, recoveryLogs ->
        Quadruple(storedState, quests, workoutLogs, recoveryLogs)
    }

    val uiState: StateFlow<AwakenUiState> = combine(
        coreDataFlow,
        _activeQuestId,
        _lastSummary,
        _bannerEvent,
        _backupJsonText
    ) { core, activeQuestId, lastSummary, banner, backupJson ->
        val (stored, quests, workoutLogs, recoveryLogs) = core
        val (level, curLvXp, nextLvXp) = LevelMath.calculateLevelFromTotalXp(stored.totalXp)
        val attributesMap = AttributeType.entries.associateWith { attr ->
            LevelMath.calculateAttributeProgress(attr, stored.attributeXp[attr] ?: 15)
        }
        val (rank, nextRank) = ProgressionAndQuestEngine.evaluateHunterRank(
            level = level,
            attributes = attributesMap,
            completedWorkoutsCount = workoutLogs.size,
            streakDays = stored.streakDays,
            bossEvaluationsCleared = stored.bossEvaluationsCleared
        )
        val characterStatus = CharacterStatusState(
            codename = stored.profile.codename,
            totalXp = stored.totalXp,
            level = level,
            currentLevelXp = curLvXp,
            nextLevelXp = nextLvXp,
            rank = rank,
            nextRank = nextRank,
            attributes = attributesMap,
            streakDays = stored.streakDays,
            completedWorkoutsCount = workoutLogs.size,
            completedBossEvaluations = stored.bossEvaluationsCleared
        )
        val skillTree = ProgressionAndQuestEngine.buildSkillTree(stored.profile, attributesMap)
        val achievements = ProgressionAndQuestEngine.buildAchievements(
            level = level,
            streakDays = stored.streakDays,
            logs = workoutLogs,
            recoveryLogsCount = recoveryLogs.size,
            bossCleared = stored.bossEvaluationsCleared
        )
        val activeQuest = quests.find { it.id == activeQuestId }
        val activeExercise = activeQuest?.let { ProgressionAndQuestEngine.getExerciseById(it.exerciseId) }

        AwakenUiState(
            isInitialized = true,
            todayDateKey = todayKey,
            profile = stored.profile,
            characterStatus = characterStatus,
            todayQuests = quests,
            workoutHistory = workoutLogs,
            recoveryLogs = recoveryLogs,
            skillTree = skillTree,
            achievements = achievements,
            activeQuest = activeQuest,
            activeExercise = activeExercise,
            lastCompletionSummary = lastSummary,
            bannerEvent = banner,
            backupJsonText = backupJson
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AwakenUiState(todayDateKey = todayKey)
    )

    init {
        ensureTodayQuestsGenerated()
    }

    private fun ensureTodayQuestsGenerated() {
        viewModelScope.launch {
            val existing = dao.getQuestsForDateOnce(todayKey)
            if (existing.isEmpty()) {
                val snapshot = prefs.getSnapshot()
                val logs = dao.getAllWorkoutLogsOnce()
                val generated = ProgressionAndQuestEngine.generateDailyQuests(
                    dateKey = todayKey,
                    profile = snapshot.profile,
                    recentLogs = logs
                )
                dao.insertQuests(generated)
            }
        }
    }

    fun completeOnboardingAndAssessment(updatedProfile: UserProfile) {
        viewModelScope.launch {
            val finalProfile = updatedProfile.copy(onboardingCompleted = true)
            prefs.saveProfile(finalProfile)
            // Regenerate today's uncompleted quests using the newly calibrated baseline tiers
            dao.deleteUncompletedQuestsForDate(todayKey)
            val logs = dao.getAllWorkoutLogsOnce()
            val generated = ProgressionAndQuestEngine.generateDailyQuests(
                dateKey = todayKey,
                profile = finalProfile,
                recentLogs = logs
            )
            dao.insertQuests(generated)
            _bannerEvent.value = SystemBannerEvent(
                title = "SYSTEM AWAKENING COMPLETE",
                subtitle = "Vessel [${finalProfile.codename}] calibrated. Daily directives generated."
            )
        }
    }

    fun selectActiveQuest(questId: String?) {
        _activeQuestId.value = questId
    }

    fun clearCompletionSummary() {
        _lastSummary.value = null
    }

    fun dismissBanner() {
        _bannerEvent.value = null
    }

    fun toggleReducedAnimations(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setReducedAnimations(enabled)
        }
    }

    fun triggerSafetyDeloadRecalibration(reason: String) {
        viewModelScope.launch {
            val snapshot = prefs.getSnapshot()
            val logs = dao.getAllWorkoutLogsOnce()
            dao.deleteUncompletedQuestsForDate(todayKey)
            val deloadQuests = ProgressionAndQuestEngine.generateDailyQuests(
                dateKey = todayKey,
                profile = snapshot.profile,
                recentLogs = logs,
                forceSafetyDeload = true
            )
            dao.insertQuests(deloadQuests)
            _bannerEvent.value = SystemBannerEvent(
                title = "SAFETY RECALIBRATION ACTIVE",
                subtitle = "Directives adjusted for joint protection ($reason). Zero XP penalty applied."
            )
        }
    }

    fun updateChainTierManually(chain: ProgressionChain, newTier: Int) {
        viewModelScope.launch {
            when (chain) {
                ProgressionChain.PUSH -> prefs.adjustChainTier(pushTier = newTier)
                ProgressionChain.SQUAT -> prefs.adjustChainTier(squatTier = newTier)
                ProgressionChain.ENDURANCE -> prefs.adjustChainTier(enduranceTier = newTier)
                ProgressionChain.CORE -> prefs.adjustChainTier(coreTier = newTier)
                ProgressionChain.MOBILITY -> prefs.adjustChainTier(mobilityTier = newTier)
            }
            val snapshot = prefs.getSnapshot()
            dao.deleteUncompletedQuestsForDate(todayKey)
            val regenerated = ProgressionAndQuestEngine.generateDailyQuests(
                dateKey = todayKey,
                profile = snapshot.profile,
                recentLogs = dao.getAllWorkoutLogsOnce()
            )
            dao.insertQuests(regenerated)
            _bannerEvent.value = SystemBannerEvent(
                title = "SKILL VARIATION ACTIVATED",
                subtitle = "${chain.title} calibrated to Tier $newTier."
            )
        }
    }

    fun recordQuestWorkoutCompletion(
        quest: QuestEntity,
        completedSets: Int,
        completedRepsOrSeconds: Int,
        verificationMethod: VerificationMethod,
        perceivedEffortRpe: Int,
        painOrFatigueFlag: Boolean,
        safetyNotes: String
    ) {
        viewModelScope.launch {
            val currentState = uiState.value
            val prevLevel = currentState.characterStatus.level
            val prevRank = currentState.characterStatus.rank
            val exercise = ProgressionAndQuestEngine.getExerciseById(quest.exerciseId)

            val cameraBonus = if (verificationMethod == VerificationMethod.CAMERA_VERIFIED) 15 else 0
            val totalEarnedXp = quest.xpReward + cameraBonus

            val primaryAttr = runCatching { AttributeType.valueOf(quest.primaryAttribute) }
                .getOrDefault(AttributeType.STRENGTH)
            val secondaryAttr = runCatching { AttributeType.valueOf(quest.secondaryAttribute) }
                .getOrDefault(AttributeType.DISCIPLINE)

            val updatedQuest = quest.copy(
                isCompleted = true,
                verificationMethod = verificationMethod.name,
                completedSets = completedSets,
                completedRepsOrSeconds = completedRepsOrSeconds,
                perceivedEffortRpe = perceivedEffortRpe,
                reportedPainOrFatigue = painOrFatigueFlag,
                completedTimestamp = System.currentTimeMillis()
            )
            dao.updateQuest(updatedQuest)

            dao.insertWorkoutLog(
                WorkoutLogEntity(
                    questId = quest.id,
                    questTitle = quest.title,
                    category = quest.category,
                    exerciseId = quest.exerciseId,
                    exerciseName = exercise.name,
                    completedSets = completedSets,
                    completedRepsOrSeconds = completedRepsOrSeconds,
                    isDurationSeconds = quest.isDurationSeconds,
                    verificationMethod = verificationMethod.name,
                    perceivedEffortRpe = perceivedEffortRpe,
                    painOrFatigueFlag = painOrFatigueFlag,
                    safetyNotes = safetyNotes,
                    xpEarned = totalEarnedXp,
                    primaryAttribute = primaryAttr.name
                )
            )

            val isBoss = quest.category == "CHALLENGE" && !painOrFatigueFlag
            prefs.awardXpAndUpdateStreak(
                xpEarned = totalEarnedXp,
                primaryAttribute = primaryAttr,
                secondaryAttribute = secondaryAttr,
                dateKey = todayKey,
                isBossEvaluation = isBoss
            )

            // Intelligent Progression Check
            var progressionMessage = "Consistent execution recorded. Attribute experience increased."
            if (painOrFatigueFlag) {
                progressionMessage = "Discomfort noted. Next session will prioritize joint decompression and reduced load."
            } else if (perceivedEffortRpe <= 7 && completedSets >= quest.targetSets && completedRepsOrSeconds >= quest.targetRepsOrSeconds) {
                val allLogsForEx = dao.getAllWorkoutLogsOnce().filter {
                    it.exerciseId == exercise.id && !it.painOrFatigueFlag && it.perceivedEffortRpe <= 7
                }
                if (allLogsForEx.size >= 2 || isBoss) {
                    val nextTier = exercise.tier + 1
                    when (exercise.chain) {
                        ProgressionChain.PUSH -> if (nextTier <= 5) prefs.adjustChainTier(pushTier = nextTier)
                        ProgressionChain.SQUAT -> if (nextTier <= 5) prefs.adjustChainTier(squatTier = nextTier)
                        ProgressionChain.ENDURANCE -> if (nextTier <= 5) prefs.adjustChainTier(enduranceTier = nextTier)
                        ProgressionChain.CORE -> if (nextTier <= 4) prefs.adjustChainTier(coreTier = nextTier)
                        ProgressionChain.MOBILITY -> if (nextTier <= 3) prefs.adjustChainTier(mobilityTier = nextTier)
                    }
                    progressionMessage = "MASTERY THRESHOLD MET: ${exercise.chain.title} unlocked Tier $nextTier!"
                }
            }

            val newSnapshot = prefs.getSnapshot()
            val (newLevel, _, _) = LevelMath.calculateLevelFromTotalXp(newSnapshot.totalXp)
            val newAttrs = AttributeType.entries.associateWith {
                LevelMath.calculateAttributeProgress(it, newSnapshot.attributeXp[it] ?: 20)
            }
            val allLogsAfter = dao.getAllWorkoutLogsOnce()
            val (newRank, _) = ProgressionAndQuestEngine.evaluateHunterRank(
                level = newLevel,
                attributes = newAttrs,
                completedWorkoutsCount = allLogsAfter.size,
                streakDays = newSnapshot.streakDays,
                bossEvaluationsCleared = newSnapshot.bossEvaluationsCleared
            )

            _lastSummary.value = WorkoutCompletionSummary(
                questTitle = quest.title,
                exerciseName = exercise.name,
                completedSets = completedSets,
                completedRepsOrSeconds = completedRepsOrSeconds,
                isDurationSeconds = quest.isDurationSeconds,
                verificationMethod = verificationMethod,
                xpEarned = totalEarnedXp,
                primaryAttribute = primaryAttr,
                secondaryAttribute = secondaryAttr,
                previousLevel = prevLevel,
                newLevel = newLevel,
                previousRank = prevRank,
                newRank = newRank,
                progressionNote = progressionMessage
            )

            if (newRank != prevRank) {
                _bannerEvent.value = SystemBannerEvent(
                    title = "RANK ASCENSION: ${newRank.fullLabel.uppercase()}",
                    subtitle = newRank.motto,
                    isRankUp = true
                )
            } else if (newLevel > prevLevel) {
                _bannerEvent.value = SystemBannerEvent(
                    title = "LEVEL UP! LV.$prevLevel → LV.$newLevel",
                    subtitle = "Physical capacity expanding. +$totalEarnedXp XP integrated.",
                    isLevelUp = true
                )
            }
        }
    }

    fun logRecoveryCheckIn(
        sleepHours: Float,
        hydrationGlasses: Int,
        sorenessLevel: Int,
        energyLevel: Int,
        painReported: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            val xp = 35
            dao.insertRecoveryLog(
                RecoveryLogEntity(
                    dateKey = todayKey,
                    sleepHours = sleepHours,
                    hydrationGlasses = hydrationGlasses,
                    sorenessLevel = sorenessLevel,
                    energyLevel = energyLevel,
                    painReported = painReported,
                    notes = notes,
                    xpEarned = xp
                )
            )
            prefs.awardXpAndUpdateStreak(
                xpEarned = xp,
                primaryAttribute = AttributeType.DISCIPLINE,
                secondaryAttribute = AttributeType.FLEXIBILITY,
                dateKey = todayKey,
                isBossEvaluation = false
            )
            if (painReported || sorenessLevel >= 4) {
                triggerSafetyDeloadRecalibration("Recovery check-in reported high soreness or pain")
            } else {
                _bannerEvent.value = SystemBannerEvent(
                    title = "RECOVERY TELEMETRY LOGGED",
                    subtitle = "+$xp XP awarded to Discipline & Flexibility. Responsible recovery fuels growth."
                )
            }
        }
    }

    fun generateBackupJson() {
        viewModelScope.launch {
            val snapshot = prefs.getSnapshot()
            val logs = dao.getAllWorkoutLogsOnce()
            val recovery = dao.getAllRecoveryLogsOnce()

            val root = JSONObject().apply {
                put("schemaVersion", 1)
                put("exportedAt", System.currentTimeMillis())
                put("codename", snapshot.profile.codename)
                put("age", snapshot.profile.age)
                put("heightCm", snapshot.profile.heightCm)
                put("weightKg", snapshot.profile.weightKg ?: JSONObject.NULL)
                put("fitnessExperience", snapshot.profile.fitnessExperience)
                put("availableMinutesPerDay", snapshot.profile.availableMinutesPerDay)
                put("trainingDaysPerWeek", snapshot.profile.trainingDaysPerWeek)
                put("pushChainTier", snapshot.profile.pushChainTier)
                put("squatChainTier", snapshot.profile.squatChainTier)
                put("enduranceChainTier", snapshot.profile.enduranceChainTier)
                put("coreChainTier", snapshot.profile.coreChainTier)
                put("mobilityChainTier", snapshot.profile.mobilityChainTier)
                put("totalXp", snapshot.totalXp)
                put("streakDays", snapshot.streakDays)
                put("bossEvaluationsCleared", snapshot.bossEvaluationsCleared)
                val attrsObj = JSONObject()
                snapshot.attributeXp.forEach { (k, v) -> attrsObj.put(k.name, v) }
                put("attributesXp", attrsObj)
                put("workoutLogsCount", logs.size)
                put("recoveryLogsCount", recovery.size)

                val logsArr = JSONArray()
                logs.take(50).forEach { log ->
                    logsArr.put(
                        JSONObject().apply {
                            put("questId", log.questId)
                            put("questTitle", log.questTitle)
                            put("category", log.category)
                            put("exerciseId", log.exerciseId)
                            put("exerciseName", log.exerciseName)
                            put("completedSets", log.completedSets)
                            put("completedRepsOrSeconds", log.completedRepsOrSeconds)
                            put("isDurationSeconds", log.isDurationSeconds)
                            put("verificationMethod", log.verificationMethod)
                            put("perceivedEffortRpe", log.perceivedEffortRpe)
                            put("painOrFatigueFlag", log.painOrFatigueFlag)
                            put("xpEarned", log.xpEarned)
                            put("primaryAttribute", log.primaryAttribute)
                            put("timestamp", log.timestamp)
                        }
                    )
                }
                put("workoutLogs", logsArr)
            }
            _backupJsonText.value = root.toString(2)
        }
    }

    fun importBackupJson(jsonString: String): Boolean {
        return try {
            val obj = JSONObject(jsonString)
            val currentProfile = uiState.value.profile
            val weightVal = if (obj.isNull("weightKg")) null else obj.optDouble("weightKg", 70.0).toFloat()
            val restoredProfile = currentProfile.copy(
                codename = obj.optString("codename", currentProfile.codename),
                age = obj.optInt("age", currentProfile.age),
                heightCm = obj.optInt("heightCm", currentProfile.heightCm),
                weightKg = weightVal,
                pushChainTier = obj.optInt("pushChainTier", 1),
                squatChainTier = obj.optInt("squatChainTier", 1),
                enduranceChainTier = obj.optInt("enduranceChainTier", 1),
                coreChainTier = obj.optInt("coreChainTier", 1),
                mobilityChainTier = obj.optInt("mobilityChainTier", 1),
                onboardingCompleted = true
            )
            val attrsObj = obj.optJSONObject("attributesXp")
            val attrMap = AttributeType.entries.associateWith { attr ->
                attrsObj?.optInt(attr.name, 25) ?: 25
            }
            val restoredState = StoredSystemState(
                profile = restoredProfile,
                totalXp = obj.optInt("totalXp", 100),
                attributeXp = attrMap,
                streakDays = obj.optInt("streakDays", 1),
                lastCompletedDateKey = todayKey,
                bossEvaluationsCleared = obj.optInt("bossEvaluationsCleared", 0)
            )
            viewModelScope.launch {
                prefs.restoreFromBackup(restoredState)
                _bannerEvent.value = SystemBannerEvent(
                    title = "SYSTEM DATA RESTORED",
                    subtitle = "Vessel [${restoredProfile.codename}] state imported from local JSON."
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
