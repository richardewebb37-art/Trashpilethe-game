package com.trashapp.gcms.commands

import com.trashapp.gcms.trophy.Trophy

/**
 * Trophy-related commands for the GCMS system
 */

/**
 * Award trophies to player on level up
 */
data class AwardTrophiesCommand(
    val playerId: String,
    val level: Int,
    val tier: String
) : GCMSCommand()

/**
 * Manually unlock a specific trophy
 */
data class UnlockTrophyCommand(
    val trophyId: String
) : GCMSCommand()

/**
 * Check trophy eligibility
 */
data class CheckTrophyEligibilityCommand(
    val playerId: String,
    val playerLevel: Int,
    val playerPoints: Int
) : GCMSCommand()

/**
 * Get trophies by tier
 */
data class GetTrophiesByTierCommand(
    val tier: String
) : GCMSCommand()

/**
 * Get trophies by rarity
 */
data class GetTrophiesByRarityCommand(
    val rarity: String
) : GCMSCommand()

/**
 * Reset trophy collection (for testing)
 */
data class ResetTrophiesCommand : GCMSCommand()

/**
 * Generate all trophies (initialization)
 */
data class GenerateTrophiesCommand : GCMSCommand()

/**
 * Claim trophy rewards
 */
data class ClaimTrophyRewardsCommand(
    val trophyIds: List<String>
) : GCMSCommand()