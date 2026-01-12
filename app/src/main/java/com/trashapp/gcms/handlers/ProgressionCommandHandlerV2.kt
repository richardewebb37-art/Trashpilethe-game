package com.trashapp.gcms.handlers

import com.trashapp.audio.AudioAssetManager
import com.trashapp.gcms.commands.*
import com.trashapp.gcms.events.*
import com.trashapp.gcms.models.GCMSState
import com.trashapp.gcms.progression.*

/**
 * Progression Command Handler V2
 * Updated for tier-aware operations with XPSystem integration
 */
class ProgressionCommandHandlerV2(
    private val audioManager: AudioAssetManager? = null
) : CommandHandler {
    
    private val xpSystem = XPSystem()
    
    override fun canHandle(command: GCMSCommand): Boolean {
        return command is BuyAbilityCommand ||
               command is BuySkillCommand ||
               command is UpgradeAbilityCommand ||
               command is LevelUpSkillCommand ||
               command is RefundAbilityCommand ||
               command is RefundSkillCommand ||
               command is AddPointsCommand ||
               command is AddXPCommand ||
               command is LoseXPCommand
    }
    
    override suspend fun handle(command: GCMSCommand, state: GCMSState): Result<List<GCMSEvent>> {
        return when (command) {
            is BuyAbilityCommand -> handleBuyAbility(command, state)
            is BuySkillCommand -> handleBuySkill(command, state)
            is UpgradeAbilityCommand -> handleUpgradeAbility(command, state)
            is LevelUpSkillCommand -> handleLevelUpSkill(command, state)
            is RefundAbilityCommand -> handleRefundAbility(command, state)
            is RefundSkillCommand -> handleRefundSkill(command, state)
            is AddPointsCommand -> handleAddPoints(command, state)
            is AddXPCommand -> handleAddXP(command, state)
            is LoseXPCommand -> handleLoseXP(command, state)
            else -> Result.failure(IllegalArgumentException("Unknown progression command"))
        }
    }
    
    /**
     * Handle buying an ability with tier-aware validation
     */
    private fun handleBuyAbility(cmd: BuyAbilityCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val ability = progressionTree.getAbility(cmd.abilityId)
            ?: return Result.failure(IllegalStateException("Ability not found"))
        
        // Check if already purchased
        if (ability.currentRank > 0) {
            return Result.failure(IllegalStateException("Ability already purchased"))
        }
        
        // Check prerequisites
        if (!progressionTree.canUnlockAbility(ability)) {
            return Result.failure(IllegalStateException("Prerequisites not met"))
        }
        
        // Check tier requirements
        val currentLevel = pointSystem.getCurrentLevel()
        if (currentLevel < ability.tier.minLevel) {
            return Result.failure(IllegalStateException(
                "Level ${ability.tier.minLevel} required (current: $currentLevel)"
            ))
        }
        
        // Check points
        val cost = ability.baseCost
        if (pointSystem.availablePoints < cost) {
            return Result.failure(IllegalStateException("Insufficient points"))
        }
        
        // Purchase ability
        val success = progressionTree.purchaseAbility(cmd.abilityId)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to purchase ability"))
        }
        
        // Spend points
        pointSystem.spendPoints(cost)
        
        // Add XP using XPSystem
        val xpGained = xpSystem.calculateAbilityXP(ability.tier, ability.rarity)
        pointSystem.addXP(xpGained)
        
        // Play sound
        audioManager?.playSound("coin")
        
        val events = mutableListOf<GCMSEvent>(
            AbilityPurchasedEvent(
                playerId = cmd.playerId,
                abilityId = cmd.abilityId,
                tier = ability.tier,
                cost = cost,
                xpGained = xpGained
            )
        )
        
        // Check for level up
        val newLevel = pointSystem.getCurrentLevel()
        if (newLevel > currentLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = currentLevel,
                newLevel = newLevel,
                tier = Tier.fromLevel(newLevel),
                xpRequired = pointSystem.xpToNextLevel
            ))
        }
        
        // Check for ability unlock events
        progressionTree.getUnlockedAbilitiesForAbility(cmd.abilityId).forEach { unlockedId ->
            events.add(AbilityUnlockedEvent(
                playerId = cmd.playerId,
                abilityId = unlockedId
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Handle buying a skill with automatic ability unlock
     */
    private fun handleBuySkill(cmd: BuySkillCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val skill = progressionTree.getSkill(cmd.skillId)
            ?: return Result.failure(IllegalStateException("Skill not found"))
        
        // Check if already purchased
        if (skill.currentLevel > 0) {
            return Result.failure(IllegalStateException("Skill already purchased"))
        }
        
        // Check prerequisites
        if (!progressionTree.canUnlockSkill(skill)) {
            return Result.failure(IllegalStateException("Prerequisites not met"))
        }
        
        // Check tier requirements
        val currentLevel = pointSystem.getCurrentLevel()
        if (currentLevel < skill.tier.minLevel) {
            return Result.failure(IllegalStateException(
                "Level ${skill.tier.minLevel} required (current: $currentLevel)"
            ))
        }
        
        // Check points
        val cost = skill.baseCost
        if (pointSystem.availablePoints < cost) {
            return Result.failure(IllegalStateException("Insufficient points"))
        }
        
        // Purchase skill
        val success = progressionTree.purchaseSkill(cmd.skillId)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to purchase skill"))
        }
        
        // Spend points
        pointSystem.spendPoints(cost)
        
        // Add XP using XPSystem
        val xpGained = xpSystem.calculateSkillXP(skill.tier, skill.rarity)
        pointSystem.addXP(xpGained)
        
        // Play sound
        audioManager?.playSound("coin")
        
        val events = mutableListOf<GCMSEvent>(
            SkillPurchasedEvent(
                playerId = cmd.playerId,
                skillId = cmd.skillId,
                tier = skill.tier,
                cost = cost,
                xpGained = xpGained,
                abilitiesUnlocked = skill.unlockedAbilities.size
            )
        )
        
        // Check for level up
        val newLevel = pointSystem.getCurrentLevel()
        if (newLevel > currentLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = currentLevel,
                newLevel = newLevel,
                tier = Tier.fromLevel(newLevel),
                xpRequired = pointSystem.xpToNextLevel
            ))
        }
        
        // Unlock all abilities for this skill
        skill.unlockedAbilities.forEach { abilityId ->
            events.add(AbilityUnlockedEvent(
                playerId = cmd.playerId,
                abilityId = abilityId
            ))
        }
        
        // Check for skill unlock events
        progressionTree.getUnlockedSkillsForSkill(cmd.skillId).forEach { unlockedId ->
            events.add(SkillUnlockedEvent(
                playerId = cmd.playerId,
                skillId = unlockedId
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Handle upgrading an ability with tier-aware XP calculation
     */
    private fun handleUpgradeAbility(cmd: UpgradeAbilityCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val ability = progressionTree.getAbility(cmd.abilityId)
            ?: return Result.failure(IllegalStateException("Ability not found"))
        
        // Check max rank
        if (ability.currentRank >= ability.maxRank) {
            return Result.failure(IllegalStateException("Ability at max rank"))
        }
        
        // Calculate cost with rank multiplier
        val costMultiplier = 1.0 + (ability.currentRank * 0.2)
        val cost = (ability.baseCost * costMultiplier).toInt()
        
        // Check points
        if (pointSystem.availablePoints < cost) {
            return Result.failure(IllegalStateException("Insufficient points"))
        }
        
        // Upgrade ability
        val success = progressionTree.upgradeAbility(cmd.abilityId)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to upgrade ability"))
        }
        
        // Spend points
        pointSystem.spendPoints(cost)
        
        // Add XP using XPSystem with rank multiplier
        val xpMultiplier = 1.0 + (ability.currentRank * 0.3)
        val baseXP = xpSystem.calculateAbilityXP(ability.tier, ability.rarity)
        val xpGained = (baseXP * xpMultiplier).toInt()
        pointSystem.addXP(xpGained)
        
        // Play sound
        audioManager?.playSound("coin")
        
        val events = mutableListOf<GCMSEvent>(
            AbilityUpgradedEvent(
                playerId = cmd.playerId,
                abilityId = cmd.abilityId,
                tier = ability.tier,
                oldRank = ability.currentRank - 1,
                newRank = ability.currentRank,
                cost = cost,
                xpGained = xpGained
            )
        )
        
        // Check for level up
        val currentLevel = pointSystem.getCurrentLevel() - 1 // Before XP
        val newLevel = pointSystem.getCurrentLevel()
        if (newLevel > currentLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = currentLevel,
                newLevel = newLevel,
                tier = Tier.fromLevel(newLevel),
                xpRequired = pointSystem.xpToNextLevel
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Handle leveling up a skill with tier-aware XP calculation
     */
    private fun handleLevelUpSkill(cmd: LevelUpSkillCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val skill = progressionTree.getSkill(cmd.skillId)
            ?: return Result.failure(IllegalStateException("Skill not found"))
        
        // Check max level
        if (skill.currentLevel >= skill.maxLevel) {
            return Result.failure(IllegalStateException("Skill at max level"))
        }
        
        // Calculate cost with level multiplier
        val costMultiplier = 1.0 + (skill.currentLevel * 0.15)
        val cost = (skill.baseCost * costMultiplier).toInt()
        
        // Check points
        if (pointSystem.availablePoints < cost) {
            return Result.failure(IllegalStateException("Insufficient points"))
        }
        
        // Level up skill
        val success = progressionTree.levelUpSkill(cmd.skillId)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to level up skill"))
        }
        
        // Spend points
        pointSystem.spendPoints(cost)
        
        // Add XP using XPSystem with level multiplier
        val xpMultiplier = 1.0 + (skill.currentLevel * 0.25)
        val baseXP = xpSystem.calculateSkillXP(skill.tier, skill.rarity)
        val xpGained = (baseXP * xpMultiplier).toInt()
        pointSystem.addXP(xpGained)
        
        // Play sound
        audioManager?.playSound("coin")
        
        val events = mutableListOf<GCMSEvent>(
            SkillLeveledUpEvent(
                playerId = cmd.playerId,
                skillId = cmd.skillId,
                tier = skill.tier,
                oldLevel = skill.currentLevel - 1,
                newLevel = skill.currentLevel,
                cost = cost,
                xpGained = xpGained
            )
        )
        
        // Check for level up
        val currentLevel = pointSystem.getCurrentLevel() - 1 // Before XP
        val newLevel = pointSystem.getCurrentLevel()
        if (newLevel > currentLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = currentLevel,
                newLevel = newLevel,
                tier = Tier.fromLevel(newLevel),
                xpRequired = pointSystem.xpToNextLevel
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Handle refunding an ability with XP penalty
     */
    private fun handleRefundAbility(cmd: RefundAbilityCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val ability = progressionTree.getAbility(cmd.abilityId)
            ?: return Result.failure(IllegalStateException("Ability not found"))
        
        // Check if purchased
        if (ability.currentRank == 0) {
            return Result.failure(IllegalStateException("Ability not purchased"))
        }
        
        // Calculate refund (50% of spent points)
        val refundAmount = (ability.baseCost * ability.currentRank * 0.5).toInt()
        
        // Refund ability
        val success = progressionTree.refundAbility(cmd.abilityId)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to refund ability"))
        }
        
        // Refund points
        pointSystem.addPoints(refundAmount)
        
        // Remove XP with penalty using XPSystem
        val baseXP = xpSystem.calculateAbilityXP(ability.tier, ability.rarity)
        val xpLost = xpSystem.applyXPPenalty(baseXP)
        pointSystem.loseXP(xpLost)
        
        // Play sound
        audioManager?.playSound("lose")
        
        val events = mutableListOf<GCMSEvent>(
            AbilityRefundedEvent(
                playerId = cmd.playerId,
                abilityId = cmd.abilityId,
                tier = ability.tier,
                refundAmount = refundAmount,
                xpLost = xpLost
            )
        )
        
        // Check for level down
        val currentLevel = pointSystem.getCurrentLevel() + 1 // Before XP loss
        val newLevel = pointSystem.getCurrentLevel()
        if (newLevel < currentLevel) {
            events.add(LevelDownEvent(
                playerId = cmd.playerId,
                oldLevel = currentLevel,
                newLevel = newLevel,
                tier = Tier.fromLevel(newLevel),
                xpLost = xpLost
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Handle refunding a skill with XP penalty
     */
    private fun handleRefundSkill(cmd: RefundSkillCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val skill = progressionTree.getSkill(cmd.skillId)
            ?: return Result.failure(IllegalStateException("Skill not found"))
        
        // Check if purchased
        if (skill.currentLevel == 0) {
            return Result.failure(IllegalStateException("Skill not purchased"))
        }
        
        // Calculate refund (50% of spent points)
        val refundAmount = (skill.baseCost * skill.currentLevel * 0.5).toInt()
        
        // Refund skill
        val success = progressionTree.refundSkill(cmd.skillId)
        if (!success) {
            return Result.failure(IllegalStateException("Failed to refund skill"))
        }
        
        // Refund points
        pointSystem.addPoints(refundAmount)
        
        // Remove XP with penalty using XPSystem
        val baseXP = xpSystem.calculateSkillXP(skill.tier, skill.rarity)
        val xpLost = xpSystem.applyXPPenalty(baseXP)
        pointSystem.loseXP(xpLost)
        
        // Play sound
        audioManager?.playSound("lose")
        
        val events = mutableListOf<GCMSEvent>(
            SkillRefundedEvent(
                playerId = cmd.playerId,
                skillId = cmd.skillId,
                tier = skill.tier,
                refundAmount = refundAmount,
                xpLost = xpLost
            )
        )
        
        // Check for level down
        val currentLevel = pointSystem.getCurrentLevel() + 1 // Before XP loss
        val newLevel = pointSystem.getCurrentLevel()
        if (newLevel < currentLevel) {
            events.add(LevelDownEvent(
                playerId = cmd.playerId,
                oldLevel = currentLevel,
                newLevel = newLevel,
                tier = Tier.fromLevel(newLevel),
                xpLost = xpLost
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Handle adding points
     */
    private fun handleAddPoints(cmd: AddPointsCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        pointSystem.addPoints(cmd.amount)
        
        // Play sound
        audioManager?.playSound("coin")
        
        return Result.success(listOf(
            PointsAddedEvent(
                playerId = cmd.playerId,
                amount = cmd.amount,
                newTotal = pointSystem.availablePoints
            )
        ))
    }
    
    /**
     * Handle adding XP with tier-aware level calculation
     */
    private fun handleAddXP(cmd: AddXPCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val currentLevel = pointSystem.getCurrentLevel()
        
        // Add XP using PointSystem
        pointSystem.addXP(cmd.amount)
        
        val events = mutableListOf<GCMSEvent>(
            XPAddedEvent(
                playerId = cmd.playerId,
                amount = cmd.amount,
                newTotal = pointSystem.totalXP
            )
        )
        
        // Check for level up using XPSystem
        val newLevel = pointSystem.getCurrentLevel()
        if (newLevel > currentLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = currentLevel,
                newLevel = newLevel,
                tier = Tier.fromLevel(newLevel),
                xpRequired = pointSystem.xpToNextLevel
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Handle losing XP with tier-aware level calculation
     */
    private fun handleLoseXP(cmd: LoseXPCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val currentLevel = pointSystem.getCurrentLevel()
        
        // Apply XP penalty using XPSystem
        val actualXPLost = xpSystem.applyXPPenalty(cmd.amount)
        pointSystem.loseXP(actualXPLost)
        
        // Play sound
        audioManager?.playSound("lose")
        
        val events = mutableListOf<GCMSEvent>(
            XPLostEvent(
                playerId = cmd.playerId,
                amount = actualXPLost,
                newTotal = pointSystem.totalXP
            )
        )
        
        // Check for level down using XPSystem
        val newLevel = pointSystem.getCurrentLevel()
        if (newLevel < currentLevel) {
            events.add(LevelDownEvent(
                playerId = cmd.playerId,
                oldLevel = currentLevel,
                newLevel = newLevel,
                tier = Tier.fromLevel(newLevel),
                xpLost = actualXPLost
            ))
        }
        
        return Result.success(events)
    }
}