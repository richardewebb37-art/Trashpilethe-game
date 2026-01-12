package com.trashapp.gcms.handlers

import com.trashapp.audio.AudioAssetManager
import com.trashapp.gcms.GCMSController
import com.trashapp.gcms.commands.*
import com.trashapp.gcms.events.*
import com.trashapp.gcms.models.GCMSState
import com.trashapp.gcms.models.Player
import com.trashapp.gcms.progression.Tier
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
class TrophyCommandHandlerV2(
    private val audioManager: AudioAssetManager? = null
) : CommandHandler {
    
    private val trophyManager = TrophyManager()
    private var isInitialized = false
    
    override fun canHandle(command: GCMSCommand): Boolean {
        return command is AwardTrophiesCommand ||
               command is UnlockTrophyCommand ||
               command is CheckTrophyEligibilityCommand ||
               command is GetTrophiesByTierCommand ||
               command is GetTrophiesByRarityCommand ||
               command is ResetTrophiesCommand ||
               command is GenerateTrophiesCommand ||
               command is ClaimTrophyRewardsCommand
    }
    
    override suspend fun handle(command: GCMSCommand, state: GCMSState): Result<List<GCMSEvent>> {
        initialize()
        
        return when (command) {
            is AwardTrophiesCommand -> handleAwardTrophies(command, state)
            is UnlockTrophyCommand -> handleUnlockTrophy(command, state)
            is CheckTrophyEligibilityCommand -> handleCheckEligibility(command, state)
            is GetTrophiesByTierCommand -> handleGetTrophiesByTier(command, state)
            is GetTrophiesByRarityCommand -> handleGetTrophiesByRarity(command, state)
            is ResetTrophiesCommand -> handleResetTrophies(state)
            is GenerateTrophiesCommand -> handleGenerateTrophies(state)
            is ClaimTrophyRewardsCommand -> handleClaimRewards(command, state)
            else -> Result.failure(IllegalArgumentException("Unknown trophy command"))
        }
    }
    
    /**
     * Initialize trophy system
     */
    private suspend fun initialize() {
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
    private suspend fun handleAwardTrophies(command: AwardTrophiesCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == command.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val tier = try {
            Tier.valueOf(command.tier.uppercase())
        } catch (e: IllegalArgumentException) {
            return Result.failure(IllegalArgumentException("Invalid tier"))
        }
        
        val reward = trophyManager.awardTrophiesOnLevelUp(
            level = command.level,
            tier = tier,
            pointSystem = player.pointSystem,
            playerAbilities = player.progressionTree.abilities.keys,
            playerSkills = player.progressionTree.skills.keys
        )
        
        val events = mutableListOf<GCMSEvent>()
        
        // Emit trophy awarded event
        events.add(TrophiesAwardedEvent(
            trophies = reward.trophies,
            level = command.level,
            totalXP = reward.totalXP,
            totalPoints = reward.totalPoints
        ))
        
        // Emit individual unlock events
        reward.trophies.forEach { trophy ->
            events.add(TrophyUnlockedEvent(trophy))
        }
        
        // Check for milestone completion
        val milestoneEvents = checkMilestones(reward.totalAwarded)
        events.addAll(milestoneEvents)
        
        // Emit progress update
        val progressEvent = emitProgressUpdate()
        events.add(progressEvent)
        
        // Play sound
        audioManager?.playSound(AudioAssetManager.SOUND_COIN)
        
        return Result.success(events)
    }
    
    /**
     * Unlock a specific trophy
     */
    private suspend fun handleUnlockTrophy(command: UnlockTrophyCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val unlocked = trophyManager.unlockTrophy(command.trophyId)
        
        if (!unlocked) {
            return Result.failure(IllegalStateException("Could not unlock trophy"))
        }
        
        val trophies = trophyManager.unlockedTrophies.value
        val trophy = trophies[command.trophyId]
        
        return if (trophy != null) {
            audioManager?.playSound(AudioAssetManager.SOUND_WIN)
            Result.success(listOf(
                TrophyUnlockedEvent(trophy),
                emitProgressUpdate()
            ))
        } else {
            Result.failure(IllegalStateException("Trophy not found"))
        }
    }
    
    /**
     * Check trophy eligibility
     */
    private suspend fun handleCheckEligibility(command: CheckTrophyEligibilityCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == command.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val eligible = trophyManager.getEligibleTrophies(
            playerLevel = command.playerLevel,
            playerPoints = command.playerPoints,
            playerAbilities = player.progressionTree.abilities.keys,
            playerSkills = player.progressionTree.skills.keys
        )
        
        val events = eligible.map { trophy ->
            TrophyEligibleEvent(
                trophy = trophy,
                playerLevel = command.playerLevel,
                playerPoints = command.playerPoints
            )
        }
        
        return Result.success(events)
    }
    
    /**
     * Get trophies by tier
     */
    private suspend fun handleGetTrophiesByTier(command: GetTrophiesByTierCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val tier = try {
            Tier.valueOf(command.tier.uppercase())
        } catch (e: IllegalArgumentException) {
            return Result.failure(IllegalArgumentException("Invalid tier"))
        }
        
        val trophies = trophyManager.getTrophiesByTier(tier)
        // Results can be accessed via TrophyManager's StateFlow
        
        return Result.success(emptyList())
    }
    
    /**
     * Get trophies by rarity
     */
    private suspend fun handleGetTrophiesByRarity(command: GetTrophiesByRarityCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val rarity = try {
            TrophyRarity.valueOf(command.rarity.uppercase())
        } catch (e: IllegalArgumentException) {
            return Result.failure(IllegalArgumentException("Invalid rarity"))
        }
        
        val trophies = trophyManager.getTrophiesByRarity(rarity)
        // Results can be accessed via TrophyManager's StateFlow
        
        return Result.success(emptyList())
    }
    
    /**
     * Reset trophy collection
     */
    private suspend fun handleResetTrophies(state: GCMSState): Result<List<GCMSEvent>> {
        trophyManager.reset()
        isInitialized = false
        
        return Result.success(listOf(emitProgressUpdate()))
    }
    
    /**
     * Generate all trophies
     */
    private suspend fun handleGenerateTrophies(state: GCMSState): Result<List<GCMSEvent>> {
        val generator = TrophyGenerator()
        val trophies = generator.generateAllTrophies()
        trophyManager.initialize(trophies)
        isInitialized = true
        
        return Result.success(emptyList())
    }
    
    /**
     * Claim trophy rewards
     */
    private suspend fun handleClaimRewards(command: ClaimTrophyRewardsCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val trophies = trophyManager.unlockedTrophies.value
        
        command.trophyIds.forEach { trophyId ->
            val trophy = trophies[trophyId]
            if (trophy?.isUnlocked == true) {
                // Trophy rewards are automatically added when unlocked
                // This command can be used for manual claiming if needed
            }
        }
        
        return Result.success(emptyList())
    }
    
    /**
     * Check for trophy milestones
     */
    private fun checkMilestones(trophyCount: Int): List<GCMSEvent> {
        val events = mutableListOf<GCMSEvent>()
        val milestones = listOf(10, 25, 50, 75, 100, 150, 200)
        
        milestones.forEach { milestone ->
            if (trophyCount == milestone) {
                events.add(TrophyMilestoneEvent(
                    milestone = "Unlocked $milestone trophies",
                    trophyCount = trophyCount,
                    bonusReward = milestone * 10
                ))
            }
        }
        
        return events
    }
    
    /**
     * Emit progress update event
     */
    private suspend fun emitProgressUpdate(): GCMSEvent {
        val collection = trophyManager.trophyCollection.first()
        
        return TrophyProgressUpdatedEvent(
            totalTrophies = collection.totalTrophies,
            unlockedTrophies = collection.unlockedTrophies,
            completionPercentage = collection.completionPercentage,
            rarityBreakdown = collection.rarityBreakdown
        )
    }
    
    /**
     * Check if all trophies in a tier are unlocked
     */
    private suspend fun checkTierCompletion(): List<GCMSEvent> {
        val events = mutableListOf<GCMSEvent>()
        
        Tier.entries.forEach { tier ->
            val trophies = trophyManager.getTrophiesByTier(tier)
            val unlocked = trophies.count { it.isUnlocked }
            
            if (unlocked > 0 && unlocked == trophies.size) {
                events.add(TierTrophiesCompleteEvent(
                    tier = tier.displayName,
                    trophiesUnlocked = unlocked
                ))
            }
        }
        
        return events
    }
}