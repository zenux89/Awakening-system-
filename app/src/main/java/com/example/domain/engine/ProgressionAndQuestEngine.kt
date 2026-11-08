package com.example.domain.engine

import com.example.data.local.QuestEntity
import com.example.data.local.WorkoutLogEntity
import com.example.domain.model.Achievement
import com.example.domain.model.AttributeProgress
import com.example.domain.model.AttributeType
import com.example.domain.model.ExerciseDefinition
import com.example.domain.model.HunterRank
import com.example.domain.model.ProgressionChain
import com.example.domain.model.QuestCategory
import com.example.domain.model.SkillNode
import com.example.domain.model.UserProfile
import java.util.Calendar

object ProgressionAndQuestEngine {

    val exerciseLibrary: List<ExerciseDefinition> = listOf(
        // PUSH CHAIN (1..5)
        ExerciseDefinition(
            id = "push_1_wall",
            name = "Wall Push-Ups",
            chain = ProgressionChain.PUSH,
            tier = 1,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.DISCIPLINE,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 8,
            defaultRestSeconds = 45,
            baseXp = 45,
            summary = "Joint-safe upper body pressing against a wall to build scapular and elbow stability.",
            instructions = listOf(
                "Stand roughly arm's length + 15 cm away from a sturdy wall.",
                "Place palms flat at shoulder height and slightly wider than shoulder-width.",
                "Brace your core so your body forms a straight diagonal line from head to heels.",
                "Lower your chest smoothly toward the wall over 2 seconds, keeping elbows at ~45°.",
                "Press firmly back to the starting position without shrugging your shoulders."
            ),
            warmUpTip = "10 gentle shoulder rolls and 8 wrist circles before starting.",
            regressionsTip = "Step slightly closer to the wall to reduce shoulder and wrist load.",
            cameraSetupHint = "Place phone 1.8m–2.2m to your side at waist-to-chest height so shoulder, elbow, and hip alignment are visible."
        ),
        ExerciseDefinition(
            id = "push_2_incline",
            name = "Incline Push-Ups",
            chain = ProgressionChain.PUSH,
            tier = 2,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.AGILITY,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 8,
            defaultRestSeconds = 60,
            baseXp = 55,
            summary = "Elevated surface push-up bridging wall pressing and floor bodyweight loading.",
            instructions = listOf(
                "Place hands on a sturdy elevated surface (desk, countertop, or bench).",
                "Step feet back into a rigid plank line with glutes and abs engaged.",
                "Lower your sternum toward the edge of the surface with controlled tempo.",
                "Pause briefly at the bottom, then drive through your palms to full extension."
            ),
            warmUpTip = "10 scapular push-ups on the incline surface + 10 arm swings.",
            regressionsTip = "Use a higher surface or return to Wall Push-Ups.",
            cameraSetupHint = "Position phone 2m away at a 45–90° side angle showing full upper-body and hip line."
        ),
        ExerciseDefinition(
            id = "push_3_knee",
            name = "Controlled Knee Push-Ups",
            chain = ProgressionChain.PUSH,
            tier = 3,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.ENDURANCE,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 8,
            defaultRestSeconds = 60,
            baseXp = 65,
            summary = "Floor pressing from padded knees with strict plank alignment from knees to head.",
            instructions = listOf(
                "Kneel on a mat or folded towel and place hands slightly wider than shoulders.",
                "Maintain a straight line from knees through hips to shoulders.",
                "Lower chest to within a fist's distance of the floor over 2 seconds, then press up."
            ),
            warmUpTip = "8 cat-cow spinal waves and 10 wrist flexor stretches.",
            regressionsTip = "Switch to Incline Push-Ups if you feel wrist pressure.",
            cameraSetupHint = "Set phone near floor/table level (~1.8m side view) so torso descent and lockout are visible."
        ),
        ExerciseDefinition(
            id = "push_4_standard",
            name = "Standard Push-Ups",
            chain = ProgressionChain.PUSH,
            tier = 4,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.DISCIPLINE,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 10,
            defaultRestSeconds = 75,
            baseXp = 80,
            summary = "Full horizontal bodyweight press requiring total-body tension and scapular control.",
            instructions = listOf(
                "Assume a high plank with hands beneath shoulders and feet hip-width apart.",
                "Keep elbows at ~45° to your ribs as you lower your body as a single rigid unit.",
                "Drive forcefully back to lockout without hips sagging."
            ),
            warmUpTip = "10 scapular push-ups + 15 seconds high plank hold.",
            regressionsTip = "Switch to Knee or Incline Push-Ups for remaining reps if form breaks down.",
            cameraSetupHint = "Place phone 1.8m–2.4m away at low side angle showing head-to-ankle alignment."
        ),
        ExerciseDefinition(
            id = "push_5_archer",
            name = "Tempo & Archer Push-Up Prep",
            chain = ProgressionChain.PUSH,
            tier = 5,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.AGILITY,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 4,
            defaultRepsOrSeconds = 8,
            defaultRestSeconds = 90,
            baseXp = 100,
            summary = "Asymmetrical wide-stance pressing loading one arm predominantly per repetition.",
            instructions = listOf(
                "Adopt a wide hand stance with fingertips turned slightly outward.",
                "Shift weight toward your working arm while keeping the opposite arm straighter for guidance.",
                "Control the 3-second descent, then drive back to center and alternate sides."
            ),
            warmUpTip = "12 shoulder circles + 6 easy standard push-ups.",
            regressionsTip = "Use standard push-ups with a 3-second lowering tempo.",
            cameraSetupHint = "Front-diagonal (45°) camera placement 2m away to capture lateral shift and depth."
        ),

        // SQUAT CHAIN (1..5)
        ExerciseDefinition(
            id = "squat_1_sit_to_stand",
            name = "Supported Sit-to-Stand",
            chain = ProgressionChain.SQUAT,
            tier = 1,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.AGILITY,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 8,
            defaultRestSeconds = 45,
            baseXp = 45,
            summary = "Chair-assisted squat pattern teaching hip hinge, knee tracking, and balanced ascent.",
            instructions = listOf(
                "Sit on the front edge of a sturdy chair with feet shoulder-width apart.",
                "Lean torso slightly forward with chest proud and arms extended ahead.",
                "Press evenly through mid-foot and heels to stand tall.",
                "Lower hips back down under control over 2–3 seconds until lightly seated."
            ),
            warmUpTip = "8 gentle standing hip circles and 6 ankle rocks per side.",
            regressionsTip = "Use a slightly higher seat or lightly rest fingertips on a table for balance.",
            cameraSetupHint = "Place phone 2m away at waist height (45° or side angle) so hip-to-knee depth is visible."
        ),
        ExerciseDefinition(
            id = "squat_2_bodyweight",
            name = "Bodyweight Squats",
            chain = ProgressionChain.SQUAT,
            tier = 2,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.ENDURANCE,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 10,
            defaultRestSeconds = 60,
            baseXp = 60,
            summary = "Unassisted full-range lower-body compound movement building quad, glute, and core strength.",
            instructions = listOf(
                "Stand with feet shoulder-width apart and toes turned out 10–20°.",
                "Brace your core and sit hips back and down as if lowering into an invisible chair.",
                "Keep knees tracking in line with mid-toes and heels flat on the floor.",
                "Descend to a comfortable pain-free depth, then drive up to standing."
            ),
            warmUpTip = "10 leg swings per leg + 5 slow half-depth warm-up squats.",
            regressionsTip = "Place a chair behind you as a safe depth target (box squat).",
            cameraSetupHint = "Position phone 2m–2.5m away at a 45° angle so both knee tracking and hip depth are in frame."
        ),
        ExerciseDefinition(
            id = "squat_3_tempo",
            name = "3-1-1 Tempo Squats",
            chain = ProgressionChain.SQUAT,
            tier = 3,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.DISCIPLINE,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 10,
            defaultRestSeconds = 60,
            baseXp = 75,
            summary = "Controlled 3-second lowering with a 1-second bottom pause to eliminate momentum.",
            instructions = listOf(
                "Count a steady 3 seconds as you descend into your squat.",
                "Hold motionless at the bottom for 1 full second with active hip tension.",
                "Drive smoothly back to the top without bouncing."
            ),
            warmUpTip = "8 bodyweight squats + 15-second glute bridge hold.",
            regressionsTip = "Reduce depth slightly or omit the bottom pause if knees feel fatigued.",
            cameraSetupHint = "2m side/diagonal view with full head-to-foot framing."
        ),
        ExerciseDefinition(
            id = "squat_4_split",
            name = "Controlled Split Squats / Lunges",
            chain = ProgressionChain.SQUAT,
            tier = 4,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.AGILITY,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 8,
            defaultRestSeconds = 75,
            baseXp = 85,
            summary = "Unilateral lower-body strength and pelvic stability training.",
            instructions = listOf(
                "Take a comfortable split stance with feet hip-width apart laterally.",
                "Lower your back knee vertically toward the floor while keeping your front foot flat.",
                "Drive through your front heel to return to the top; complete reps on both legs."
            ),
            warmUpTip = "10 hip flexor pulses per side + 6 bodyweight squats.",
            regressionsTip = "Keep a hand near a wall or chair back for balance assistance.",
            cameraSetupHint = "Side-angle camera placement 2.2m away to track front and rear knee flexion."
        ),
        ExerciseDefinition(
            id = "squat_5_pistol_prep",
            name = "Single-Leg Box Squat (Pistol Prep)",
            chain = ProgressionChain.SQUAT,
            tier = 5,
            primaryAttribute = AttributeType.AGILITY,
            secondaryAttribute = AttributeType.STRENGTH,
            supportsCameraVerification = true,
            isDurationSeconds = false,
            defaultSets = 4,
            defaultRepsOrSeconds = 6,
            defaultRestSeconds = 90,
            baseXp = 105,
            summary = "High-skill single-leg control descending to a chair/box and standing on one leg.",
            instructions = listOf(
                "Stand in front of a chair on one leg with the non-working foot hovering forward.",
                "Reach arms forward and hinge hips back to touch the seat softly under control.",
                "Drive up through your single planted foot while keeping your knee aligned."
            ),
            warmUpTip = "10 ankle mobility rocks + 8 split squats per leg.",
            regressionsTip = "Use your non-working heel lightly on the floor as a kickstand.",
            cameraSetupHint = "Front-45° angle 2m away to monitor knee stability and hip control."
        ),

        // ENDURANCE CHAIN (1..5)
        ExerciseDefinition(
            id = "end_1_walk",
            name = "Zone-1 Awakening Walk",
            chain = ProgressionChain.ENDURANCE,
            tier = 1,
            primaryAttribute = AttributeType.ENDURANCE,
            secondaryAttribute = AttributeType.DISCIPLINE,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 1,
            defaultRepsOrSeconds = 600,
            defaultRestSeconds = 0,
            baseXp = 50,
            summary = "Comfortable conversational-pace walking to build aerobic base and daily consistency.",
            instructions = listOf(
                "Walk indoors or outdoors at a relaxed, rhythmic pace for 10 minutes.",
                "Maintain tall posture with relaxed shoulders and natural arm swing."
            ),
            warmUpTip = "1 minute of easy ankle circles and slow marching in place.",
            regressionsTip = "Split into two 5-minute bouts if 10 continuous minutes feels taxing.",
            cameraSetupHint = "Timer-guided endurance quest."
        ),
        ExerciseDefinition(
            id = "end_2_extended_walk",
            name = "Extended Aerobic Patrol",
            chain = ProgressionChain.ENDURANCE,
            tier = 2,
            primaryAttribute = AttributeType.ENDURANCE,
            secondaryAttribute = AttributeType.DISCIPLINE,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 1,
            defaultRepsOrSeconds = 900,
            defaultRestSeconds = 0,
            baseXp = 65,
            summary = "Sustained 15-minute steady-state walk improving aerobic efficiency.",
            instructions = listOf(
                "Maintain a steady cadence for 15 minutes with calm rhythmic breathing.",
                "Focus on smooth heel-to-toe roll and upright ribcage alignment."
            ),
            warmUpTip = "Start the first 2 minutes at an easy stroll.",
            regressionsTip = "Slow your pace at any time to keep breathing comfortable.",
            cameraSetupHint = "Timer-guided endurance quest."
        ),
        ExerciseDefinition(
            id = "end_3_brisk_walk",
            name = "Brisk Tempo Walk / March",
            chain = ProgressionChain.ENDURANCE,
            tier = 3,
            primaryAttribute = AttributeType.ENDURANCE,
            secondaryAttribute = AttributeType.AGILITY,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 1,
            defaultRepsOrSeconds = 1200,
            defaultRestSeconds = 0,
            baseXp = 80,
            summary = "Purposeful 20-minute brisk-cadence walk or indoor low-impact cardio.",
            instructions = listOf(
                "Walk at a brisk pace where breathing is deeper but still controlled.",
                "Drive elbows gently back to naturally quicken your stride cadence."
            ),
            warmUpTip = "3 minutes easy walking + dynamic calf stretches.",
            regressionsTip = "Alternate 3 minutes brisk with 2 minutes easy recovery pace.",
            cameraSetupHint = "Timer-guided endurance quest."
        ),
        ExerciseDefinition(
            id = "end_4_intervals",
            name = "Walk / Jog Adaptation Intervals",
            chain = ProgressionChain.ENDURANCE,
            tier = 4,
            primaryAttribute = AttributeType.ENDURANCE,
            secondaryAttribute = AttributeType.STRENGTH,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 5,
            defaultRepsOrSeconds = 120,
            defaultRestSeconds = 60,
            baseXp = 95,
            summary = "Gradual tendon and cardiovascular conditioning alternating light jogging with walking recovery.",
            instructions = listOf(
                "Alternate 60–90 seconds of relaxed, soft-landing jogging with 60–90 seconds of brisk walking.",
                "Land softly beneath your center of mass with short, quick steps."
            ),
            warmUpTip = "5 minutes brisk walk + ankle hops and leg swings.",
            regressionsTip = "Substitute fast marching if impact bothers shins or knees.",
            cameraSetupHint = "Timer-guided interval quest."
        ),
        ExerciseDefinition(
            id = "end_5_run",
            name = "Continuous Stamina Run / Conditioning",
            chain = ProgressionChain.ENDURANCE,
            tier = 5,
            primaryAttribute = AttributeType.ENDURANCE,
            secondaryAttribute = AttributeType.DISCIPLINE,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 1,
            defaultRepsOrSeconds = 1500,
            defaultRestSeconds = 0,
            baseXp = 115,
            summary = "Sustained 25-minute aerobic run or low-impact high-output conditioning session.",
            instructions = listOf(
                "Hold a sustainable, rhythmic pace for 20–25 minutes.",
                "Keep shoulders relaxed, jaw unclenched, and cadence light."
            ),
            warmUpTip = "5 minutes progressive walk-to-jog warm-up.",
            regressionsTip = "Insert 60-second walk breaks every 5 minutes as needed.",
            cameraSetupHint = "Timer-guided endurance quest."
        ),

        // CORE & AGILITY CHAIN (1..4)
        ExerciseDefinition(
            id = "core_1_deadbug",
            name = "Controlled Dead Bug Bracing",
            chain = ProgressionChain.CORE,
            tier = 1,
            primaryAttribute = AttributeType.AGILITY,
            secondaryAttribute = AttributeType.STRENGTH,
            supportsCameraVerification = false,
            isDurationSeconds = false,
            defaultSets = 3,
            defaultRepsOrSeconds = 8,
            defaultRestSeconds = 40,
            baseXp = 45,
            summary = "Spine-safe core stabilization coordinating contralateral limb movement.",
            instructions = listOf(
                "Lie on your back with knees bent at 90° above hips and arms reaching up.",
                "Gently press your lower back into the floor so there is no gap beneath your lumbar spine.",
                "Slowly extend one heel toward the floor while reaching the opposite arm overhead, then alternate."
            ),
            warmUpTip = "5 deep diaphragmatic breaths with pelvic tilts.",
            regressionsTip = "Move only the legs over a smaller range of motion.",
            cameraSetupHint = "Floor core stability drill."
        ),
        ExerciseDefinition(
            id = "core_2_plank",
            name = "Forearm Pillar Plank",
            chain = ProgressionChain.CORE,
            tier = 2,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.DISCIPLINE,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 3,
            defaultRepsOrSeconds = 25,
            defaultRestSeconds = 45,
            baseXp = 55,
            summary = "Isometric anterior core and shoulder girdle stabilization.",
            instructions = listOf(
                "Rest on forearms with elbows directly beneath shoulders and feet hip-width apart.",
                "Squeeze glutes and brace abs while breathing steadily without lower-back sagging."
            ),
            warmUpTip = "6 cat-cow stretches and 10-second glute squeeze.",
            regressionsTip = "Perform from knees or elevate forearms on a bench/couch.",
            cameraSetupHint = "Timer-guided isometric hold."
        ),
        ExerciseDefinition(
            id = "core_3_balance",
            name = "Single-Leg Stork Balance & Reach",
            chain = ProgressionChain.CORE,
            tier = 3,
            primaryAttribute = AttributeType.AGILITY,
            secondaryAttribute = AttributeType.FLEXIBILITY,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 3,
            defaultRepsOrSeconds = 35,
            defaultRestSeconds = 30,
            baseXp = 65,
            summary = "Ankle-knee-hip proprioception and neuromuscular coordination drill.",
            instructions = listOf(
                "Stand tall on one foot with a slight micro-bend in the standing knee.",
                "Fix your gaze on a point ahead and slowly reach your free foot forward, sideways, and back."
            ),
            warmUpTip = "10 ankle circles each direction.",
            regressionsTip = "Stand near a wall to lightly tap a fingertip if balance wavers.",
            cameraSetupHint = "Timer-guided balance quest."
        ),
        ExerciseDefinition(
            id = "core_4_hollow",
            name = "Hollow Body Core Lock",
            chain = ProgressionChain.CORE,
            tier = 4,
            primaryAttribute = AttributeType.STRENGTH,
            secondaryAttribute = AttributeType.AGILITY,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 3,
            defaultRepsOrSeconds = 30,
            defaultRestSeconds = 45,
            baseXp = 85,
            summary = "Gymnastic total-core tension hold building elite trunk stiffness.",
            instructions = listOf(
                "Lie on your back, press lower spine firmly into the mat, and lift head/shoulders slightly.",
                "Tuck knees or extend legs at an angle where your lower back never loses floor contact."
            ),
            warmUpTip = "8 dead bugs + 10 pelvic tilts.",
            regressionsTip = "Keep knees bent at 90° and arms reaching toward your hips.",
            cameraSetupHint = "Timer-guided core hold."
        ),

        // MOBILITY & FLEXIBILITY CHAIN (1..3)
        ExerciseDefinition(
            id = "mob_1_thoracic",
            name = "Cervical & Thoracic Decompression Flow",
            chain = ProgressionChain.MOBILITY,
            tier = 1,
            primaryAttribute = AttributeType.FLEXIBILITY,
            secondaryAttribute = AttributeType.DISCIPLINE,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 2,
            defaultRepsOrSeconds = 120,
            defaultRestSeconds = 20,
            baseXp = 40,
            summary = "Gentle upper-back, shoulder, and neck articular mobility restoring posture.",
            instructions = listOf(
                "Perform slow neck half-circles, shoulder blade retractions, and thoracic open-book rotations.",
                "Move strictly within a pain-free, soothing range of motion synchronized with slow breaths."
            ),
            warmUpTip = "3 slow 4-second inhales and 6-second exhales.",
            regressionsTip = "Perform seated in a supportive chair.",
            cameraSetupHint = "Guided recovery & mobility flow."
        ),
        ExerciseDefinition(
            id = "mob_2_hip_ankle",
            name = "Hip 90/90 & Ankle Mobility Protocol",
            chain = ProgressionChain.MOBILITY,
            tier = 2,
            primaryAttribute = AttributeType.FLEXIBILITY,
            secondaryAttribute = AttributeType.AGILITY,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 2,
            defaultRepsOrSeconds = 180,
            defaultRestSeconds = 20,
            baseXp = 55,
            summary = "Unlocks hip internal/external rotation and ankle dorsiflexion for deeper, safer squats.",
            instructions = listOf(
                "Sit on the floor with knees bent in a gentle windshield-wiper or 90/90 position.",
                "Rotate knees side to side slowly, then perform 8 kneeling ankle rocks per side."
            ),
            warmUpTip = "Sit elevated on a pillow if hips feel tight.",
            regressionsTip = "Perform supine knee-to-chest hugs and gentle twists instead.",
            cameraSetupHint = "Guided mobility protocol."
        ),
        ExerciseDefinition(
            id = "mob_3_full_flow",
            name = "Full-System Kinetic Restoration Flow",
            chain = ProgressionChain.MOBILITY,
            tier = 3,
            primaryAttribute = AttributeType.FLEXIBILITY,
            secondaryAttribute = AttributeType.ENDURANCE,
            supportsCameraVerification = false,
            isDurationSeconds = true,
            defaultSets = 2,
            defaultRepsOrSeconds = 240,
            defaultRestSeconds = 30,
            baseXp = 70,
            summary = "Comprehensive full-body dynamic stretch, low lunge thoracic reach, and spinal wave flow.",
            instructions = listOf(
                "Transition smoothly through cat-cow, downward dog calf pedals, low lunge reach, and child's pose.",
                "Spend 5 slow breaths in each position to down-regulate nervous system tension."
            ),
            warmUpTip = "Begin on hands and knees with gentle breathing.",
            regressionsTip = "Skip any position that creates joint pinching.",
            cameraSetupHint = "Guided full-body mobility flow."
        )
    )

    fun getExerciseById(id: String): ExerciseDefinition {
        return exerciseLibrary.find { it.id == id } ?: exerciseLibrary.first()
    }

    fun getExerciseForChainAndTier(chain: ProgressionChain, tier: Int): ExerciseDefinition {
        val chainExercises = exerciseLibrary.filter { it.chain == chain }.sortedBy { it.tier }
        val clamped = tier.coerceIn(1, chainExercises.maxOf { it.tier })
        return chainExercises.find { it.tier == clamped } ?: chainExercises.first()
    }

    fun evaluateHunterRank(
        level: Int,
        attributes: Map<AttributeType, AttributeProgress>,
        completedWorkoutsCount: Int,
        streakDays: Int,
        bossEvaluationsCleared: Int
    ): Pair<HunterRank, HunterRank?> {
        val minAttrLevel = attributes.values.minOfOrNull { it.level } ?: 1
        val ranks = HunterRank.entries
        var currentRank = HunterRank.E_AWAKENING
        for (rank in ranks) {
            if (level >= rank.minLevel &&
                minAttrLevel >= rank.minMinAttributeLevel &&
                completedWorkoutsCount >= rank.minCompletedWorkouts &&
                streakDays >= rank.minStreakDays &&
                bossEvaluationsCleared >= rank.requiredBossEvaluations
            ) {
                currentRank = rank
            }
        }
        val nextRank = ranks.getOrNull(ranks.indexOf(currentRank) + 1)
        return currentRank to nextRank
    }

    fun generateDailyQuests(
        dateKey: String,
        profile: UserProfile,
        recentLogs: List<WorkoutLogEntity>,
        forceSafetyDeload: Boolean = false
    ): List<QuestEntity> {
        val recentPainReported = forceSafetyDeload || recentLogs.take(2).any { it.painOrFatigueFlag || it.perceivedEffortRpe >= 9 }
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val timeMultiplier = when {
            profile.availableMinutesPerDay <= 15 -> 0.8f
            profile.availableMinutesPerDay >= 45 -> 1.2f
            else -> 1.0f
        }

        val pushEx = getExerciseForChainAndTier(
            ProgressionChain.PUSH,
            if (recentPainReported) (profile.pushChainTier - 1).coerceAtLeast(1) else profile.pushChainTier
        )
        val squatEx = getExerciseForChainAndTier(
            ProgressionChain.SQUAT,
            if (recentPainReported) (profile.squatChainTier - 1).coerceAtLeast(1) else profile.squatChainTier
        )
        val endEx = getExerciseForChainAndTier(
            ProgressionChain.ENDURANCE,
            if (recentPainReported) 1 else profile.enduranceChainTier
        )
        val coreEx = getExerciseForChainAndTier(ProgressionChain.CORE, profile.coreChainTier)
        val mobEx = getExerciseForChainAndTier(ProgressionChain.MOBILITY, profile.mobilityChainTier)

        val quests = mutableListOf<QuestEntity>()
        val isStrengthDay = dayOfWeek in listOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY, Calendar.SATURDAY)
        val isLowerPrimary = dayOfWeek in listOf(Calendar.WEDNESDAY, Calendar.SATURDAY)

        if (recentPainReported) {
            quests.add(
                QuestEntity(
                    id = "${dateKey}_main_deload",
                    dateKey = dateKey,
                    title = "System Recalibration: Gentle Movement Protocol",
                    description = "Pain or high fatigue detected in recent telemetry. Volume scaled down to protect joints and restore readiness.",
                    category = QuestCategory.MAIN.name,
                    exerciseId = mobEx.id,
                    difficultyLabel = "Adaptive Deload",
                    targetSets = 2,
                    targetRepsOrSeconds = 120,
                    isDurationSeconds = true,
                    restSeconds = 30,
                    xpReward = 65,
                    primaryAttribute = AttributeType.FLEXIBILITY.name,
                    secondaryAttribute = AttributeType.DISCIPLINE.name,
                    supportsCamera = false
                )
            )
        } else if (isStrengthDay) {
            val mainEx = if (isLowerPrimary) squatEx else pushEx
            val secondaryEx = if (isLowerPrimary) pushEx else squatEx
            val scaledReps = (mainEx.defaultRepsOrSeconds * timeMultiplier).toInt().coerceAtLeast(5)

            quests.add(
                QuestEntity(
                    id = "${dateKey}_main_1",
                    dateKey = dateKey,
                    title = "Main Directive: ${mainEx.name}",
                    description = "${mainEx.summary} Focus on clean range of motion and controlled tempo.",
                    category = QuestCategory.MAIN.name,
                    exerciseId = mainEx.id,
                    difficultyLabel = "Tier ${mainEx.tier} • ${mainEx.chain.title}",
                    targetSets = mainEx.defaultSets,
                    targetRepsOrSeconds = scaledReps,
                    isDurationSeconds = mainEx.isDurationSeconds,
                    restSeconds = mainEx.defaultRestSeconds,
                    xpReward = mainEx.baseXp + 15,
                    primaryAttribute = mainEx.primaryAttribute.name,
                    secondaryAttribute = mainEx.secondaryAttribute.name,
                    supportsCamera = mainEx.supportsCameraVerification
                )
            )
            quests.add(
                QuestEntity(
                    id = "${dateKey}_main_2",
                    dateKey = dateKey,
                    title = "Structural Pairing: ${secondaryEx.name}",
                    description = "Balanced structural pairing to develop symmetrical upper and lower body strength.",
                    category = QuestCategory.MAIN.name,
                    exerciseId = secondaryEx.id,
                    difficultyLabel = "Tier ${secondaryEx.tier} • ${secondaryEx.chain.title}",
                    targetSets = (secondaryEx.defaultSets - 1).coerceAtLeast(2),
                    targetRepsOrSeconds = secondaryEx.defaultRepsOrSeconds,
                    isDurationSeconds = secondaryEx.isDurationSeconds,
                    restSeconds = secondaryEx.defaultRestSeconds,
                    xpReward = secondaryEx.baseXp,
                    primaryAttribute = secondaryEx.primaryAttribute.name,
                    secondaryAttribute = secondaryEx.secondaryAttribute.name,
                    supportsCamera = secondaryEx.supportsCameraVerification
                )
            )
        } else {
            val scaledDuration = (endEx.defaultRepsOrSeconds * timeMultiplier).toInt().coerceAtLeast(300)
            quests.add(
                QuestEntity(
                    id = "${dateKey}_main_endurance",
                    dateKey = dateKey,
                    title = "Main Directive: ${endEx.name}",
                    description = endEx.summary,
                    category = QuestCategory.MAIN.name,
                    exerciseId = endEx.id,
                    difficultyLabel = "Tier ${endEx.tier} • Aerobic Conditioning",
                    targetSets = endEx.defaultSets,
                    targetRepsOrSeconds = scaledDuration,
                    isDurationSeconds = endEx.isDurationSeconds,
                    restSeconds = endEx.defaultRestSeconds,
                    xpReward = endEx.baseXp + 15,
                    primaryAttribute = AttributeType.ENDURANCE.name,
                    secondaryAttribute = AttributeType.DISCIPLINE.name,
                    supportsCamera = false
                )
            )
            quests.add(
                QuestEntity(
                    id = "${dateKey}_main_core",
                    dateKey = dateKey,
                    title = "Stabilizer Protocol: ${coreEx.name}",
                    description = coreEx.summary,
                    category = QuestCategory.MAIN.name,
                    exerciseId = coreEx.id,
                    difficultyLabel = "Tier ${coreEx.tier} • Core & Balance",
                    targetSets = coreEx.defaultSets,
                    targetRepsOrSeconds = coreEx.defaultRepsOrSeconds,
                    isDurationSeconds = coreEx.isDurationSeconds,
                    restSeconds = coreEx.defaultRestSeconds,
                    xpReward = coreEx.baseXp,
                    primaryAttribute = coreEx.primaryAttribute.name,
                    secondaryAttribute = coreEx.secondaryAttribute.name,
                    supportsCamera = false
                )
            )
        }

        val sideExercise = if (isStrengthDay) coreEx else squatEx
        quests.add(
            QuestEntity(
                id = "${dateKey}_side_1",
                dateKey = dateKey,
                title = "Side Quest: ${sideExercise.name}",
                description = "Optional supplemental conditioning to accelerate ${sideExercise.primaryAttribute.displayName} growth.",
                category = QuestCategory.SIDE.name,
                exerciseId = sideExercise.id,
                difficultyLabel = "Tier ${sideExercise.tier} • Supplemental",
                targetSets = 2,
                targetRepsOrSeconds = sideExercise.defaultRepsOrSeconds,
                isDurationSeconds = sideExercise.isDurationSeconds,
                restSeconds = 40,
                xpReward = 40,
                primaryAttribute = sideExercise.primaryAttribute.name,
                secondaryAttribute = AttributeType.AGILITY.name,
                supportsCamera = sideExercise.supportsCameraVerification
            )
        )

        quests.add(
            QuestEntity(
                id = "${dateKey}_recovery_1",
                dateKey = dateKey,
                title = "Restoration Protocol: ${mobEx.name}",
                description = "${mobEx.summary} Completing recovery protocols builds Flexibility and Discipline.",
                category = QuestCategory.RECOVERY.name,
                exerciseId = mobEx.id,
                difficultyLabel = "Recovery & Mobility",
                targetSets = mobEx.defaultSets,
                targetRepsOrSeconds = mobEx.defaultRepsOrSeconds,
                isDurationSeconds = true,
                restSeconds = 20,
                xpReward = 45,
                primaryAttribute = AttributeType.FLEXIBILITY.name,
                secondaryAttribute = AttributeType.DISCIPLINE.name,
                supportsCamera = false
            )
        )

        val bossEx = if (profile.squatChainTier <= profile.pushChainTier) squatEx else pushEx
        quests.add(
            QuestEntity(
                id = "${dateKey}_challenge_boss",
                dateKey = dateKey,
                title = "Boss Evaluation: ${bossEx.name} Mastery Trial",
                description = "Demonstrate controlled technique across ${bossEx.defaultSets} sets (RPE ≤ 7, zero pain) to prove readiness for the next progression tier and Rank advancement.",
                category = QuestCategory.CHALLENGE.name,
                exerciseId = bossEx.id,
                difficultyLabel = "Boss Trial • Tier ${bossEx.tier}",
                targetSets = bossEx.defaultSets,
                targetRepsOrSeconds = bossEx.defaultRepsOrSeconds + 2,
                isDurationSeconds = bossEx.isDurationSeconds,
                restSeconds = bossEx.defaultRestSeconds,
                xpReward = 95,
                primaryAttribute = bossEx.primaryAttribute.name,
                secondaryAttribute = AttributeType.DISCIPLINE.name,
                supportsCamera = bossEx.supportsCameraVerification
            )
        )

        return quests
    }

    fun buildSkillTree(
        profile: UserProfile,
        attributes: Map<AttributeType, AttributeProgress>
    ): List<SkillNode> {
        return exerciseLibrary.map { ex ->
            val currentChainTier = when (ex.chain) {
                ProgressionChain.PUSH -> profile.pushChainTier
                ProgressionChain.SQUAT -> profile.squatChainTier
                ProgressionChain.ENDURANCE -> profile.enduranceChainTier
                ProgressionChain.CORE -> profile.coreChainTier
                ProgressionChain.MOBILITY -> profile.mobilityChainTier
            }
            val attrLevel = attributes[ex.primaryAttribute]?.level ?: 1
            val reqAttrLevel = (ex.tier - 1) * 2 + 1
            SkillNode(
                id = "skill_${ex.id}",
                title = ex.name,
                chain = ex.chain,
                tier = ex.tier,
                exerciseId = ex.id,
                requiredAttribute = ex.primaryAttribute,
                requiredAttributeLevel = reqAttrLevel,
                description = ex.summary,
                milestoneCondition = "Requires ${ex.primaryAttribute.shortCode} Lv.$reqAttrLevel or Tier ${ex.tier - 1} mastery.",
                isUnlocked = ex.tier <= currentChainTier || attrLevel >= reqAttrLevel,
                isMastered = ex.tier < currentChainTier
            )
        }
    }

    fun buildAchievements(
        level: Int,
        streakDays: Int,
        logs: List<WorkoutLogEntity>,
        recoveryLogsCount: Int,
        bossCleared: Int
    ): List<Achievement> {
        val cameraVerifiedCount = logs.count {
            it.verificationMethod == "CAMERA_VERIFIED" || it.verificationMethod == "MANUALLY_CORRECTED"
        }
        return listOf(
            Achievement(
                id = "ach_awaken",
                title = "System Initialization",
                description = "Complete onboarding and calibrate your starting vessel parameters.",
                category = "Milestone",
                xpBonus = 50,
                isUnlocked = true,
                progressText = "CALIBRATED"
            ),
            Achievement(
                id = "ach_first_quest",
                title = "First Step out of Dormancy",
                description = "Complete your first daily training or recovery quest.",
                category = "Consistency",
                xpBonus = 75,
                isUnlocked = logs.isNotEmpty(),
                progressText = "${logs.size.coerceAtMost(1)} / 1 Quests"
            ),
            Achievement(
                id = "ach_camera_verify",
                title = "Optical Telemetry Online",
                description = "Verify an exercise session using the on-device Camera AI movement analyzer.",
                category = "Camera AI",
                xpBonus = 100,
                isUnlocked = cameraVerifiedCount >= 1,
                progressText = "$cameraVerifiedCount / 1 Camera Sessions"
            ),
            Achievement(
                id = "ach_recovery_guardian",
                title = "Responsible Vessel Maintenance",
                description = "Log 3 sleep, hydration, and soreness recovery check-ins in the Recovery Journal.",
                category = "Recovery",
                xpBonus = 80,
                isUnlocked = recoveryLogsCount >= 3,
                progressText = "$recoveryLogsCount / 3 Check-ins"
            ),
            Achievement(
                id = "ach_streak_5",
                title = "Unbroken Discipline",
                description = "Reach a 5-day training or recovery streak.",
                category = "Discipline",
                xpBonus = 150,
                isUnlocked = streakDays >= 5,
                progressText = "$streakDays / 5 Days"
            ),
            Achievement(
                id = "ach_boss_slayer",
                title = "Trial Challenger",
                description = "Clear a Personal Boss Evaluation to prove progressive mastery.",
                category = "Boss Trials",
                xpBonus = 150,
                isUnlocked = bossCleared >= 1,
                progressText = "$bossCleared / 1 Boss Trials"
            ),
            Achievement(
                id = "ach_level_10",
                title = "Double-Digit Vessel",
                description = "Reach System Level 10 through consistent physical training.",
                category = "Progression",
                xpBonus = 250,
                isUnlocked = level >= 10,
                progressText = "Lv. $level / 10"
            )
        )
    }
}
