package com.trashapp.gcms.events

import com.trashapp.gcms.trophy.Trophy
import com.trashapp.gcms.trophy.TrophyRarity

/**
 * Trophy-related events for the GCMS system
 */

/**
 * Emitted when trophies are awarded on level up
 */
data class TrophiesAwardedEvent(
    val trophies: List<Trophy>,
    val level: Int,
    val totalXP: Int,
    val totalPoints: Int
) : GCMSEvent()

/**
 * Emitted when a specific trophy is unlocked
 */
data class TrophyUnlockedEvent(
    val trophy: Trophy
) : GCMSEvent()

/**
 * Emitted when player becomes eligible for a trophy
 */
data class TrophyEligibleEvent(
    val trophy: Trophy,
    val playerLevel: Int,
    val playerPoints: Int
) : GCMSEvent()

/**
 * Emitted when trophy collection progress is updated
 */
data class TrophyProgressUpdatedEvent(
    val totalTrophies: Int,
    val unlockedTrophies: Int,
    val completionPercentage: Float,
    val rarityBreakdown: Map<TrophyRarity, Int>
) : GCMSEvent()

/**
 * Emitted when player completes a trophy milestone
 */
data class TrophyMilestoneEvent(
    val milestone: String,
    val trophyCount: Int,
    val bonusReward: Int
) : GCMSEvent()

/**
 * Emitted when all trophies in a tier are unlocked
 */
data class TierTrophiesCompleteEvent(
    val tier: String,
    val trophiesUnlocked: Int
) : GCMSEvent()