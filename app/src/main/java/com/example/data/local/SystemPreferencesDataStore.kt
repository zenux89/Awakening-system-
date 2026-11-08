package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AttributeType
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.awakenDataStore: DataStore<Preferences> by preferencesDataStore(name = "awaken_system_prefs")

data class StoredSystemState(
    val profile: UserProfile,
    val totalXp: Int,
    val attributeXp: Map<AttributeType, Int>,
    val streakDays: Int,
    val lastCompletedDateKey: String,
    val bossEvaluationsCleared: Int
)

class SystemPreferencesDataStore(private val context: Context) {

    private object Keys {
        val CODENAME = stringPreferencesKey("codename")
        val AGE = intPreferencesKey("age")
        val HEIGHT_CM = intPreferencesKey("height_cm")
        val WEIGHT_KG = floatPreferencesKey("weight_kg")
        val HAS_WEIGHT = booleanPreferencesKey("has_weight")
        val EXPERIENCE = stringPreferencesKey("experience")
        val GOALS = stringSetPreferencesKey("goals")
        val AVAILABLE_MINUTES = intPreferencesKey("available_minutes")
        val EQUIPMENT = stringSetPreferencesKey("equipment")
        val ENVIRONMENT = stringPreferencesKey("environment")
        val DAYS_PER_WEEK = intPreferencesKey("days_per_week")
        val LIMITATIONS = stringSetPreferencesKey("limitations")
        val PUSH_TIER = intPreferencesKey("push_tier")
        val SQUAT_TIER = intPreferencesKey("squat_tier")
        val ENDURANCE_TIER = intPreferencesKey("endurance_tier")
        val CORE_TIER = intPreferencesKey("core_tier")
        val MOBILITY_TIER = intPreferencesKey("mobility_tier")
        val REDUCED_ANIMATIONS = booleanPreferencesKey("reduced_animations")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

        val TOTAL_XP = intPreferencesKey("total_xp")
        val STR_XP = intPreferencesKey("str_xp")
        val END_XP = intPreferencesKey("end_xp")
        val AGI_XP = intPreferencesKey("agi_xp")
        val FLX_XP = intPreferencesKey("flx_xp")
        val DSC_XP = intPreferencesKey("dsc_xp")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val LAST_DATE_KEY = stringPreferencesKey("last_date_key")
        val BOSS_CLEARED = intPreferencesKey("boss_cleared")
    }

    val systemStateFlow: Flow<StoredSystemState> = context.awakenDataStore.data.map { prefs ->
        val hasWeight = prefs[Keys.HAS_WEIGHT] ?: true
        val profile = UserProfile(
            codename = prefs[Keys.CODENAME] ?: "PLAYER-01",
            age = prefs[Keys.AGE] ?: 25,
            heightCm = prefs[Keys.HEIGHT_CM] ?: 172,
            weightKg = if (hasWeight) (prefs[Keys.WEIGHT_KG] ?: 70f) else null,
            fitnessExperience = prefs[Keys.EXPERIENCE] ?: "Beginner (Building Foundation)",
            primaryGoals = prefs[Keys.GOALS] ?: setOf("Full-Body Strength", "Endurance", "Consistency & Discipline"),
            availableMinutesPerDay = prefs[Keys.AVAILABLE_MINUTES] ?: 25,
            equipment = prefs[Keys.EQUIPMENT] ?: setOf("None (Bodyweight Only)", "Wall / Elevated Surface"),
            environmentPreference = prefs[Keys.ENVIRONMENT] ?: "Indoor & Outdoor Hybrid",
            trainingDaysPerWeek = prefs[Keys.DAYS_PER_WEEK] ?: 4,
            limitationsOrPainAreas = prefs[Keys.LIMITATIONS] ?: emptySet(),
            pushChainTier = (prefs[Keys.PUSH_TIER] ?: 1).coerceIn(1, 5),
            squatChainTier = (prefs[Keys.SQUAT_TIER] ?: 1).coerceIn(1, 5),
            enduranceChainTier = (prefs[Keys.ENDURANCE_TIER] ?: 1).coerceIn(1, 5),
            coreChainTier = (prefs[Keys.CORE_TIER] ?: 1).coerceIn(1, 4),
            mobilityChainTier = (prefs[Keys.MOBILITY_TIER] ?: 1).coerceIn(1, 3),
            reducedAnimations = prefs[Keys.REDUCED_ANIMATIONS] ?: false,
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false
        )
        val attrMap = mapOf(
            AttributeType.STRENGTH to (prefs[Keys.STR_XP] ?: 20),
            AttributeType.ENDURANCE to (prefs[Keys.END_XP] ?: 20),
            AttributeType.AGILITY to (prefs[Keys.AGI_XP] ?: 15),
            AttributeType.FLEXIBILITY to (prefs[Keys.FLX_XP] ?: 15),
            AttributeType.DISCIPLINE to (prefs[Keys.DSC_XP] ?: 25)
        )
        StoredSystemState(
            profile = profile,
            totalXp = prefs[Keys.TOTAL_XP] ?: 45,
            attributeXp = attrMap,
            streakDays = prefs[Keys.STREAK_DAYS] ?: 1,
            lastCompletedDateKey = prefs[Keys.LAST_DATE_KEY] ?: "",
            bossEvaluationsCleared = prefs[Keys.BOSS_CLEARED] ?: 0
        )
    }

    suspend fun getSnapshot(): StoredSystemState = systemStateFlow.first()

    suspend fun saveProfile(profile: UserProfile) {
        context.awakenDataStore.edit { prefs ->
            prefs[Keys.CODENAME] = profile.codename.ifBlank { "PLAYER-01" }
            prefs[Keys.AGE] = profile.age.coerceIn(13, 99)
            prefs[Keys.HEIGHT_CM] = profile.heightCm.coerceIn(100, 240)
            if (profile.weightKg != null) {
                prefs[Keys.HAS_WEIGHT] = true
                prefs[Keys.WEIGHT_KG] = profile.weightKg
            } else {
                prefs[Keys.HAS_WEIGHT] = false
            }
            prefs[Keys.EXPERIENCE] = profile.fitnessExperience
            prefs[Keys.GOALS] = profile.primaryGoals
            prefs[Keys.AVAILABLE_MINUTES] = profile.availableMinutesPerDay
            prefs[Keys.EQUIPMENT] = profile.equipment
            prefs[Keys.ENVIRONMENT] = profile.environmentPreference
            prefs[Keys.DAYS_PER_WEEK] = profile.trainingDaysPerWeek
            prefs[Keys.LIMITATIONS] = profile.limitationsOrPainAreas
            prefs[Keys.PUSH_TIER] = profile.pushChainTier.coerceIn(1, 5)
            prefs[Keys.SQUAT_TIER] = profile.squatChainTier.coerceIn(1, 5)
            prefs[Keys.ENDURANCE_TIER] = profile.enduranceChainTier.coerceIn(1, 5)
            prefs[Keys.CORE_TIER] = profile.coreChainTier.coerceIn(1, 4)
            prefs[Keys.MOBILITY_TIER] = profile.mobilityChainTier.coerceIn(1, 3)
            prefs[Keys.REDUCED_ANIMATIONS] = profile.reducedAnimations
            prefs[Keys.ONBOARDING_COMPLETED] = profile.onboardingCompleted
        }
    }

    suspend fun setReducedAnimations(enabled: Boolean) {
        context.awakenDataStore.edit { prefs ->
            prefs[Keys.REDUCED_ANIMATIONS] = enabled
        }
    }

    suspend fun awardXpAndUpdateStreak(
        xpEarned: Int,
        primaryAttribute: AttributeType,
        secondaryAttribute: AttributeType,
        dateKey: String,
        isBossEvaluation: Boolean
    ) {
        context.awakenDataStore.edit { prefs ->
            prefs[Keys.TOTAL_XP] = (prefs[Keys.TOTAL_XP] ?: 0) + xpEarned
            val primaryGain = (xpEarned * 0.7f).toInt().coerceAtLeast(5)
            val secondaryGain = (xpEarned * 0.3f).toInt().coerceAtLeast(3)

            fun addAttrXp(attr: AttributeType, amount: Int) {
                val key = when (attr) {
                    AttributeType.STRENGTH -> Keys.STR_XP
                    AttributeType.ENDURANCE -> Keys.END_XP
                    AttributeType.AGILITY -> Keys.AGI_XP
                    AttributeType.FLEXIBILITY -> Keys.FLX_XP
                    AttributeType.DISCIPLINE -> Keys.DSC_XP
                }
                prefs[key] = (prefs[key] ?: 0) + amount
            }

            addAttrXp(primaryAttribute, primaryGain)
            addAttrXp(secondaryAttribute, secondaryGain)
            addAttrXp(AttributeType.DISCIPLINE, 6)

            val lastDate = prefs[Keys.LAST_DATE_KEY] ?: ""
            if (lastDate != dateKey) {
                prefs[Keys.STREAK_DAYS] = (prefs[Keys.STREAK_DAYS] ?: 0) + 1
                prefs[Keys.LAST_DATE_KEY] = dateKey
            }
            if (isBossEvaluation) {
                prefs[Keys.BOSS_CLEARED] = (prefs[Keys.BOSS_CLEARED] ?: 0) + 1
            }
        }
    }

    suspend fun adjustChainTier(
        pushTier: Int? = null,
        squatTier: Int? = null,
        enduranceTier: Int? = null,
        coreTier: Int? = null,
        mobilityTier: Int? = null
    ) {
        context.awakenDataStore.edit { prefs ->
            pushTier?.let { prefs[Keys.PUSH_TIER] = it.coerceIn(1, 5) }
            squatTier?.let { prefs[Keys.SQUAT_TIER] = it.coerceIn(1, 5) }
            enduranceTier?.let { prefs[Keys.ENDURANCE_TIER] = it.coerceIn(1, 5) }
            coreTier?.let { prefs[Keys.CORE_TIER] = it.coerceIn(1, 4) }
            mobilityTier?.let { prefs[Keys.MOBILITY_TIER] = it.coerceIn(1, 3) }
        }
    }

    suspend fun restoreFromBackup(state: StoredSystemState) {
        saveProfile(state.profile)
        context.awakenDataStore.edit { prefs ->
            prefs[Keys.TOTAL_XP] = state.totalXp
            prefs[Keys.STR_XP] = state.attributeXp[AttributeType.STRENGTH] ?: 20
            prefs[Keys.END_XP] = state.attributeXp[AttributeType.ENDURANCE] ?: 20
            prefs[Keys.AGI_XP] = state.attributeXp[AttributeType.AGILITY] ?: 15
            prefs[Keys.FLX_XP] = state.attributeXp[AttributeType.FLEXIBILITY] ?: 15
            prefs[Keys.DSC_XP] = state.attributeXp[AttributeType.DISCIPLINE] ?: 25
            prefs[Keys.STREAK_DAYS] = state.streakDays
            prefs[Keys.LAST_DATE_KEY] = state.lastCompletedDateKey
            prefs[Keys.BOSS_CLEARED] = state.bossEvaluationsCleared
        }
    }
}
