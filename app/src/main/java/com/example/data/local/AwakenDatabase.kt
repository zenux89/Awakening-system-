package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        QuestEntity::class,
        WorkoutLogEntity::class,
        RecoveryLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AwakenDatabase : RoomDatabase() {
    abstract fun awakenDao(): AwakenDao

    companion object {
        @Volatile
        private var INSTANCE: AwakenDatabase? = null

        fun getInstance(context: Context): AwakenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AwakenDatabase::class.java,
                    "awaken_system_offline.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
