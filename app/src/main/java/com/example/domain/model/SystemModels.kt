package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AttrAgilityColor
import com.example.ui.theme.AttrDisciplineColor
import com.example.ui.theme.AttrEnduranceColor
import com.example.ui.theme.AttrFlexibilityColor
import com.example.ui.theme.AttrStrengthColor
import com.example.ui.theme.RankAColor
import com.example.ui.theme.RankBColor
import com.example.ui.theme.RankCColor
import com.example.ui.theme.RankDColor
import com.example.ui.theme.RankEColor
import kotlin.math.max

enum class HunterRank(
    val code: String,
    val title: String,
    val fullLabel: String,
    val motto: String,
    val color: Color,
    val minLevel: Int,
    val minMinAttributeLevel: Int,
    val minCompletedWorkouts: Int,
    val minStreakDays: Int,
    val requiredBossEvaluations: Int
) {
    E_AWAKENING(
        code = "E-RANK",
        title = "Awakening",
        fullLabel = "E-Rank: Awakening",
        motto = "The System has recognized your Dormant Vessel. Begin baseline conditioning.",
        color = RankEColor,
        minLevel = 1,
        minMinAttributeLevel = 1,
        minCompletedWorkouts = 0,
        minStreakDays = 0,
        requiredBossEvaluations = 0
    ),
    D_FOUNDATION(
        code = "D-RANK",
        title = "Foundation",
        fullLabel = "D-Rank: Foundation",
        motto = "Structural integrity established. Joint stability and foundational movement patterns online.",
        color = RankDColor,
        minLevel = 4,
        minMinAttributeLevel = 2,
        minCompletedWorkouts = 4,
        minStreakDays = 2,
        requiredBossEvaluations = 1
    ),
    C_WARRIOR(
        code = "C-RANK",
        title = "Warrior",
        fullLabel = "C-Rank: Warrior",
        motto = "Full bodyweight mastery achieved. Consistent neuromuscular output under sustained load.",
        color = RankCColor,
        minLevel = 9,
        minMinAttributeLevel = 4,
        minCompletedWorkouts = 12,
        minStreakDays = 5,
        requiredBossEvaluations = 2
    ),
    B_ELITE(
        code = "B-RANK",
        title = "Elite",
        fullLabel = "B-Rank: Elite",
        motto = "High-output physical specimen. Unilateral control, explosive stamina, and iron discipline.",
        color = RankBColor,
        minLevel = 16,
        minMinAttributeLevel = 7,
        minCompletedWorkouts = 25,
        minStreakDays = 10,
        requiredBossEvaluations = 4
    ),
    A_TRANSCENDENT(
        code = "A-RANK",
        title = "Transcendent",
        fullLabel = "A-Rank: Transcendent",
        motto = "Peak human vessel. Harmonized strength, endurance, mobility, agility, and unwavering discipline.",
        color = RankAColor,
        minLevel = 25,
        minMinAttributeLevel = 10,
        minCompletedWorkouts = 50,
        minStreakDays = 21,
        requiredBossEvaluations = 6
    )
}

enum class AttributeType(
    val displayName: String,
    val shortCode: String,
    val description: String,
    val trainingFocus: String,
    val color: Color
) {
    STRENGTH(
        displayName = "Strength",
        shortCode = "STR",
        description = "Muscular force production, upper/lower push-pull mechanics, and core bracing.",
        trainingFocus = "Push-up progressions, squat variations, and isometric core holds.",
        color = AttrStrengthColor
    ),
    ENDURANCE(
        displayName = "Endurance",
        shortCode = "END",
        description = "Cardiovascular efficiency, aerobic work capacity, and fatigue resistance.",
        trainingFocus = "Walking sessions, brisk pace intervals, and sustained movement quests.",
        color = AttrEnduranceColor
    ),
    AGILITY(
        displayName = "Agility",
        shortCode = "AGI",
        description = "Neuromuscular coordination, single-leg balance, proprioception, and tempo control.",
        trainingFocus = "Single-leg balance drills, tempo squats, and split-stance stability.",
        color = AttrAgilityColor
    ),
    FLEXIBILITY(
        displayName = "Flexibility",
        shortCode = "FLX",
        description = "Joint range of motion, thoracic/hip mobility, and tissue decompression.",
        trainingFocus = "Controlled articular rotations, dynamic warm-ups, and decompression flows.",
        color = AttrFlexibilityColor
    ),
    DISCIPLINE(
        displayName = "Discipline",
        shortCode = "DSC",
        description = "Schedule adherence, honest self-assessment, recovery logging, and consistency.",
        trainingFocus = "Daily quest completion, recovery check-ins, and streak maintenance.",
        color = AttrDisciplineColor
    )
}

enum class QuestCategory(val label: String, val badgeText: String) {
    MAIN("Main Quest", "MAIN DIRECTIVE"),
    SIDE("Side Quest", "OPTIONAL MODULE"),
    RECOVERY("Recovery Quest", "RESTORATION PROTOCOL"),
    CHALLENGE("Boss Challenge", "EVALUATION TRIAL")
}

enum class VerificationMethod(val label: String, val shortTag: String) {
    CAMERA_VERIFIED("Camera AI Verified", "CAM-AI"),
    SELF_REPORTED("Self-Reported", "MANUAL"),
    MANUALLY_CORRECTED("Camera + Manual Adjust", "CAM-ADJ")
}

enum class ProgressionChain(val title: String, val primaryAttribute: AttributeType) {
    PUSH("Upper-Body Push Chain", AttributeType.STRENGTH),
    SQUAT("Lower-Body Squat Chain", AttributeType.STRENGTH),
    ENDURANCE("Aerobic Endurance Chain", AttributeType.ENDURANCE),
    CORE("Core & Balance Chain", AttributeType.AGILITY),
    MOBILITY("Joint Mobility & Flexibility Chain", AttributeType.FLEXIBILITY)
}

data class ExerciseDefinition(
    val id: String,
    val name: String,
    val chain: ProgressionChain,
    val tier: Int,
    val primaryAttribute: AttributeType,
    val secondaryAttribute: AttributeType,
    val supportsCameraVerification: Boolean,
    val isDurationSeconds: Boolean,
    val defaultSets: Int,
    val defaultRepsOrSeconds: Int,
    val defaultRestSeconds: Int,
    val baseXp: Int,
    val summary: String,
    val instructions: List<String>,
    val warmUpTip: String,
    val regressionsTip: String,
    val cameraSetupHint: String
)

data class AttributeProgress(
    val type: AttributeType,
    val totalXp: Int,
    val level: Int,
    val currentLevelXp: Int,
    val nextLevelRequirementXp: Int
) {
    val progressFraction: Float
        get() = if (nextLevelRequirementXp <= 0) 1f
        else (currentLevelXp.toFloat() / nextLevelRequirementXp.toFloat()).coerceIn(0f, 1f)
}

data class CharacterStatusState(
    val codename: String,
    val totalXp: Int,
    val level: Int,
    val currentLevelXp: Int,
    val nextLevelXp: Int,
    val rank: HunterRank,
    val nextRank: HunterRank?,
    val attributes: Map<AttributeType, AttributeProgress>,
    val streakDays: Int,
    val completedWorkoutsCount: Int,
    val completedBossEvaluations: Int
) {
    val levelProgressFraction: Float
        get() = if (nextLevelXp <= 0) 1f
        else (currentLevelXp.toFloat() / nextLevelXp.toFloat()).coerceIn(0f, 1f)
}

data class UserProfile(
    val codename: String = "PLAYER-01",
    val age: Int = 25,
    val heightCm: Int = 172,
    val weightKg: Float? = 70f,
    val fitnessExperience: String = "Beginner (Rebuilding consistency)",
    val primaryGoals: Set<String> = setOf("Full-Body Strength", "Endurance", "Consistency & Discipline"),
    val availableMinutesPerDay: Int = 25,
    val equipment: Set<String> = setOf("None (Bodyweight Only)", "Wall / Elevated Surface"),
    val environmentPreference: String = "Indoor & Outdoor Hybrid",
    val trainingDaysPerWeek: Int = 4,
    val limitationsOrPainAreas: Set<String> = emptySet(),
    val pushChainTier: Int = 1,
    val squatChainTier: Int = 1,
    val enduranceChainTier: Int = 1,
    val coreChainTier: Int = 1,
    val mobilityChainTier: Int = 1,
    val reducedAnimations: Boolean = false,
    val onboardingCompleted: Boolean = false
)

data class SkillNode(
    val id: String,
    val title: String,
    val chain: ProgressionChain,
    val tier: Int,
    val exerciseId: String,
    val requiredAttribute: AttributeType,
    val requiredAttributeLevel: Int,
    val description: String,
    val milestoneCondition: String,
    val isUnlocked: Boolean,
    val isMastered: Boolean
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val xpBonus: Int,
    val isUnlocked: Boolean,
    val progressText: String
)

object LevelMath {
    fun calculateLevelFromTotalXp(totalXp: Int): Triple<Int, Int, Int> {
        var remaining = max(0, totalXp)
        var level = 1
        var required = xpForNextLevel(level)
        while (remaining >= required) {
            remaining -= required
            level++
            required = xpForNextLevel(level)
        }
        return Triple(level, remaining, required)
    }

    fun xpForNextLevel(currentLevel: Int): Int {
        return 100 + (currentLevel * 45) + ((currentLevel - 1) * (currentLevel - 1) * 8)
    }

    fun calculateAttributeProgress(type: AttributeType, totalAttrXp: Int): AttributeProgress {
        var remaining = max(0, totalAttrXp)
        var level = 1
        var required = 60 + (level * 30)
        while (remaining >= required) {
            remaining -= required
            level++
            required = 60 + (level * 30)
        }
        return AttributeProgress(
            type = type,
            totalXp = totalAttrXp,
            level = level,
            currentLevelXp = remaining,
            nextLevelRequirementXp = required
        )
    }
}
