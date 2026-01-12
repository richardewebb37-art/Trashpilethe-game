package com.trashapp.gcms.commands

/**
 * Challenge-related commands for the GCMS system
 */

/**
 * Generate challenges for a specific level
 */
data class GenerateChallengesCommand(
    val level: Int
) : GCMSCommand()

/**
 * Update challenge progress
 */
data class UpdateChallengeProgressCommand(
    val challengeId: String,
    val progress: Int,
    val playerId: String
) : GCMSCommand()

/**
 * Complete a challenge
 */
data class CompleteChallengeCommand(
    val challengeId: String,
    val playerId: String
) : GCMSCommand()

/**
 * Check if player can advance to next level
 */
data class CheckLevelAdvancementCommand(
    val currentLevel: Int,
    val hasRequiredXP: Boolean,
    val hasRequiredPoints: Boolean,
    val hasRequiredAbilities: Boolean,
    val hasRequiredSkills: Boolean
) : GCMSCommand()

/**
 * Set current level challenges
 */
data class SetCurrentLevelChallengesCommand(
    val level: Int
) : GCMSCommand()

/**
 * Get challenges for a specific level
 */
data class GetChallengesForLevelCommand(
    val level: Int
) : GCMSCommand()

/**
 * Get challenge progress for a level
 */
data class GetChallengeProgressCommand(
    val level: Int
) : GCMSCommand()

/**
 * Reset challenge system (for testing)
 */
data class ResetChallengesCommand : GCMSCommand()

/**
 * Track game action for challenge progress
 */
data class TrackGameActionCommand(
    val actionType: String, // "score", "ability_use", "card_played", etc.
    val actionData: Map<String, Any>,
    val playerId: String
) : GCMSCommand()