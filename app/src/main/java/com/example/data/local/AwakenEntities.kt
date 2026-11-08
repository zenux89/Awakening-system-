package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: String,
    val dateKey: String,
    val title: String,
    val description: String,
    val category: String,
    val exerciseId: String,
    val difficultyLabel: String,
    val targetSets: Int,
    val targetRepsOrSeconds: Int,
    val isDurationSeconds: Boolean,
    val restSeconds: Int,
    val xpReward: Int,
    val primaryAttribute: String,
    val secondaryAttribute: String,
    val supportsCamera: Boolean,
    val isCompleted: Boolean = false,
    val verificationMethod: String? = null,
    val completedSets: Int = 0,
    val completedRepsOrSeconds: Int = 0,
    val perceivedEffortRpe: Int? = null,
    val reportedPainOrFatigue: Boolean = false,
    val completedTimestamp: Long? = null
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questId: String,
    val questTitle: String,
    val category: String,
    val exerciseId: String,
    val exerciseName: String,
    val completedSets: Int,
    val completedRepsOrSeconds: Int,
    val isDurationSeconds: Boolean,
    val verificationMethod: String,
    val perceivedEffortRpe: Int,
    val painOrFatigueFlag: Boolean,
    val safetyNotes: String,
    val xpEarned: Int,
    val primaryAttribute: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recovery_logs")
data class RecoveryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateKey: String,
    val sleepHours: Float,
    val hydrationGlasses: Int,
    val sorenessLevel: Int,
    val energyLevel: Int,
    val painReported: Boolean,
    val notes: String,
    val xpEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
