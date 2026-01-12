package com.trashapp.gcms.events

import com.trashapp.gcms.challenge.Challenge
import com.trashapp.gcms.challenge.ChallengeProgress

/**
 * Challenge-related events for the GCMS system
 */

/**
 * Emitted when challenges are generated for a level
 */
data class ChallengesGeneratedEvent(
    val level: Int,
    val challengeCount: Int
) : GCMSEvent()

/**
 * Emitted when challenge progress is updated
 */
data class ChallengeProgressUpdatedEvent(
    val challengeId: String,
    val challengeName: String,
    val progress: Int,
    val maxProgress: Int,
    val percentage: Float
) : GCMSEvent()

/**
 * Emitted when a challenge is completed
 */
data class ChallengeCompletedEvent(
    val challenge: Challenge,
    val xpEarned: Int,
    val pointsEarned: Int,
    val achievement: String
) : GCMSEvent()

/**
 * Emitted when all challenges for a level are completed
 */
data class LevelChallengesCompletedEvent(
    val level: Int,
    val totalChallenges: Int,
    val totalXPEarned: Int,
    val totalPointsEarned: Int
) : GCMSEvent()

/**
 * Emitted when player can advance to next level
 */
data class CanAdvanceToNextLevelEvent(
    val currentLevel: Int,
    val nextLevel: Int,
    val requirementsMet: Boolean
) : GCMSEvent()

/**
 * Emitted when player advances to next level
 */
data class AdvancedToNextLevelEvent(
    val previousLevel: Int,
    val newLevel: Int,
    val challengesCompleted: Int
) : GCMSEvent()

/**
 * Emitted when level advancement is blocked
 */
data class LevelAdvancementBlockedEvent(
    val currentLevel: Int,
    val missingRequirements: List<String>
) : GCMSEvent()

/**
 * Emitted when challenge progress summary is updated
 */
data class ChallengeProgressSummaryEvent(
    val level: Int,
    val progress: ChallengeProgress
) : GCMSEvent()

/**
 * Emitted when a game action affects challenge progress
 */
data class GameActionTrackedEvent(
    val actionType: String,
    val affectedChallenges: List<String>,
    val progressUpdates: Map<String, Int>
) : GCMSEvent()