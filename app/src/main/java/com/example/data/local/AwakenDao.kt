package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AwakenDao {
    @Query("SELECT * FROM quests WHERE dateKey = :dateKey ORDER BY CASE category WHEN 'MAIN' THEN 1 WHEN 'SIDE' THEN 2 WHEN 'RECOVERY' THEN 3 WHEN 'CHALLENGE' THEN 4 ELSE 5 END")
    fun observeQuestsForDate(dateKey: String): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests ORDER BY dateKey DESC")
    fun observeAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE dateKey = :dateKey")
    suspend fun getQuestsForDateOnce(dateKey: String): List<QuestEntity>

    @Query("SELECT * FROM quests")
    suspend fun getAllQuestsOnce(): List<QuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<QuestEntity>)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    @Query("DELETE FROM quests WHERE dateKey = :dateKey AND isCompleted = 0")
    suspend fun deleteUncompletedQuestsForDate(dateKey: String)

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun observeWorkoutLogs(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    suspend fun getAllWorkoutLogsOnce(): List<WorkoutLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLogs(logs: List<WorkoutLogEntity>)

    @Query("SELECT * FROM recovery_logs ORDER BY timestamp DESC")
    fun observeRecoveryLogs(): Flow<List<RecoveryLogEntity>>

    @Query("SELECT * FROM recovery_logs ORDER BY timestamp DESC")
    suspend fun getAllRecoveryLogsOnce(): List<RecoveryLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecoveryLog(log: RecoveryLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecoveryLogs(logs: List<RecoveryLogEntity>)

    @Query("DELETE FROM quests")
    suspend fun clearAllQuests()

    @Query("DELETE FROM workout_logs")
    suspend fun clearAllWorkoutLogs()

    @Query("DELETE FROM recovery_logs")
    suspend fun clearAllRecoveryLogs()
}
