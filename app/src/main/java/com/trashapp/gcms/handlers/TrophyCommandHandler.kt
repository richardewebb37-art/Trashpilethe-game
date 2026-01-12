package com.trashapp.gcms.handlers

import com.trashapp.gcms.GCMSController
import com.trashapp.gcms.GCMSState
import com.trashapp.gcms.commands.*
import com.trashapp.gcms.events.*
import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.trophy.Trophy
import com.trashapp.gcms.trophy.TrophyGenerator
import com.trashapp.gcms.trophy.TrophyManager
import com.trashapp.gcms.trophy.TrophyRarity
import kotlinx.coroutines.flow.first

/**
 * Handles all trophy-related commands
 * 
 * This handler manages:
 * - Trophy awarding on level up
 * - Trophy unlocking
 * - Trophy eligibility checking
 * - Trophy collection management
 */
class TrophyCommandHandler(
    private val controller: GCMSController,
    private val state: GCMSState
) {
    
    private val trophyManager = TrophyManager()
    private var isInitialized = false
    
    /**
     * Handle trophy commands
     */
    suspend fun handle(command: GCMSCommand) {
        when (command) {
            is AwardTrophiesCommand -> handleAwardTrophies(command)
            is UnlockTrophyCommand -> handleUnlockTrophy(command)
            is CheckTrophyEligibilityCommand -> handleCheckEligibility(command)
            is GetTrophiesByTierCommand -> handleGetTrophiesByTier(command)
            is GetTrophiesByRarityCommand -> handleGetTrophiesByRarity(command)
            is ResetTrophiesCommand -> handleResetTrophies()
            is GenerateTrophiesCommand -> handleGenerateTrophies()
            is ClaimTrophyRewardsCommand -> handleClaimRewards(command)
        }
    }
    
    /**
     * Initialize trophy system
     */
    suspend fun initialize() {
        if (!isInitialized) {
            val generator = TrophyGenerator()
            val trophies = generator.generateAllTrophies()
            trophyManager.initialize(trophies)
            isInitialized = true
        }
    }
    
    /**
     * Award trophies on level up
     */
    private suspend fun handleAwardTrophies(command: AwardTrophiesCommand) {
        initialize()
        
        val player = state.players.values.firstOrNull() ?: return
        val tier = try {
            Tier.valueOf(command.tier.uppercase())
        } catch (e: IllegalArgumentException) {
            return
        }
        
        val reward = trophyManager.awardTrophiesOnLevelUp(
            level = command.level,
            tier = tier,
            pointSystem = player.pointSystem,
            playerAbilities = command.playerAbilities,
            playerSkills = command.playerSkills
        )
        
        // Emit trophy awarded event
        controller.emitEvent(TrophiesAwardedEvent(
            trophies = reward.trophies,
            level = command.level,
            totalXP = reward.totalXP,
            totalPoints = reward.totalPoints
        ))
        
        // Emit individual unlock events
        reward.trophies.forEach { trophy ->
            controller.emitEvent(TrophyUnlockedEvent(trophy))
        }
        
        // Check for milestone completion
        checkMilestones(reward.totalAwarded)
        
        // Emit progress update
        emitProgressUpdate()
        
        // Add XP and points from trophies to player
        if (reward.totalXP > 0) {
            controller.submitCommand(AddXPCommand(reward.totalXP))
        }
        if (reward.totalPoints > 0) {
            controller.submitCommand(AddPointsCommand(reward.totalPoints))
        }
    }
    
    /**
     * Unlock a specific trophy
     */
    private suspend fun handleUnlockTrophy(command: UnlockTrophyCommand) {
        initialize()
        
        val unlocked = trophyManager.unlockTrophy(command.trophyId)
        
        if (unlocked) {
            val trophies = trophyManager.unlockedTrophies.value
            val trophy = trophies[command.trophyId]
            
            trophy?.let {
                controller.emitEvent(TrophyUnlockedEvent(it))
                emitProgressUpdate()
            }
        }
    }
    
    /**
     * Check trophy eligibility
     */
    private suspend fun handleCheckEligibility(command: CheckTrophyEligibilityCommand) {
        initialize()
        
        val eligible = trophyManager.getEligibleTrophies(
            playerLevel = command.playerLevel,
            playerPoints = command.playerPoints,
            playerAbilities = command.playerAbilities,
            playerSkills = command.playerSkills
        )
        
        eligible.forEach { trophy ->
            controller.emitEvent(TrophyEligibleEvent(
                trophy = trophy,
                playerLevel = command.playerLevel,
                playerPoints = command.playerPoints
            ))
        }
    }
    
    /**
     * Get trophies by tier
     */
    private suspend fun handleGetTrophiesByTier(command: GetTrophiesByTierCommand) {
        initialize()
        
        val tier = try {
            Tier.valueOf(command.tier.uppercase())
        } catch (e: IllegalArgumentException) {
            return
        }
        
        val trophies = trophyManager.getTrophiesByTier(tier)
        // Results can be accessed via TrophyManager's StateFlow
    }
    
    /**
     * Get trophies by rarity
     */
    private suspend fun handleGetTrophiesByRarity(command: GetTrophiesByRarityCommand) {
        initialize()
        
        val rarity = try {
            TrophyRarity.valueOf(command.rarity.uppercase())
        } catch (e: IllegalArgumentException) {
            return
        }
        
        val trophies = trophyManager.getTrophiesByRarity(rarity)
        // Results can be accessed via TrophyManager's StateFlow
    }
    
    /**
     * Reset trophy collection
     */
    private suspend fun handleResetTrophies() {
        trophyManager.reset()
        isInitialized = false
        emitProgressUpdate()
    }
    
    /**
     * Generate all trophies
     */
    private suspend fun handleGenerateTrophies() {
        val generator = TrophyGenerator()
        val trophies = generator.generateAllTrophies()
        trophyManager.initialize(trophies)
        isInitialized = true
    }
    
    /**
     * Claim trophy rewards
     */
    private suspend fun handleClaimRewards(command: ClaimTrophyRewardsCommand) {
        val trophies = trophyManager.unlockedTrophies.value
        
        command.trophyIds.forEach { trophyId ->
            val trophy = trophies[trophyId]
            if (trophy?.isUnlocked == true) {
                // Trophy rewards are automatically added when unlocked
                // This command can be used for manual claiming if needed
            }
        }
    }
    
    /**
     * Check for trophy milestones
     */
    private suspend fun checkMilestones(trophyCount: Int) {
        val milestones = listOf(10, 25, 50, 75, 100, 150, 200)
        
        milestones.forEach { milestone ->
            if (trophyCount == milestone) {
                controller.emitEvent(TrophyMilestoneEvent(
                    milestone = "Unlocked $milestone trophies",
                    trophyCount = trophyCount,
                    bonusReward = milestone * 10
                ))
            }
        }
    }
    
    /**
     * Emit progress update event
     */
    private suspend fun emitProgressUpdate() {
        val collection = trophyManager.trophyCollection.first()
        
        controller.emitEvent(TrophyProgressUpdatedEvent(
            totalTrophies = collection.totalTrophies,
            unlockedTrophies = collection.unlockedTrophies,
            completionPercentage = collection.completionPercentage,
            rarityBreakdown = collection.rarityBreakdown
        ))
        
        // Check for tier completion
        checkTierCompletion()
    }
    
    /**
     * Check if all trophies in a tier are unlocked
     */
    private suspend fun checkTierCompletion() {
        Tier.entries.forEach { tier ->
            val trophies = trophyManager.getTrophiesByTier(tier)
            val unlocked = trophies.count { it.isUnlocked }
            
            if (unlocked > 0 && unlocked == trophies.size) {
                controller.emitEvent(TierTrophiesCompleteEvent(
                    tier = tier.displayName,
                    trophiesUnlocked = unlocked
                ))
            }
        }
    }
}