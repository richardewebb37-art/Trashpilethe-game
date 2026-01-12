package com.trashapp.gcms.handlers

import com.trashapp.audio.AudioAssetManager
import com.trashapp.gcms.commands.*
import com.trashapp.gcms.events.*
import com.trashapp.gcms.models.GCMSState
import com.trashapp.gcms.progression.*

/**
 * Progression Command Handler
 * Handles all progression-related commands (buying abilities/skills, leveling, etc.)
 */
class ProgressionCommandHandler(
    private val audioManager: AudioAssetManager? = null
) : CommandHandler {
    
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
    
    private fun handleBuyAbility(cmd: BuyAbilityCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val ability = progressionTree.getAbility(cmd.abilityId)
            ?: return Result.failure(IllegalArgumentException("Ability not found"))
        
        // Check if can purchase
        if (!ability.canPurchase(pointSystem.availablePoints)) {
            return Result.failure(IllegalStateException("Cannot purchase ability"))
        }
        
        // Purchase ability
        val (newTree, xpGranted) = progressionTree.purchaseAbility(cmd.abilityId)
        val newPointSystem = pointSystem.spendPoints(ability.cost, xpGranted)
        val oldLevel = pointSystem.currentLevel
        val newLevel = newPointSystem.currentLevel
        
        val events = mutableListOf<GCMSEvent>()
        
        // Add ability purchased event
        events.add(AbilityPurchasedEvent(
            playerId = cmd.playerId,
            abilityId = cmd.abilityId,
            abilityName = ability.name,
            cost = ability.cost,
            xpGranted = xpGranted,
            newLevel = newLevel
        ))
        
        // Add level up event if level increased
        if (newLevel > oldLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = oldLevel,
                newLevel = newLevel,
                totalXP = newPointSystem.totalXP
            ))
            // Play level up sound
            audioManager?.playSound(AudioAssetManager.SOUND_COIN)
        }
        
        // Add ability unlocked events for newly unlocked abilities
        val unlockedAbilities = newTree.getPurchasableAbilities().filter {
            !progressionTree.getAbility(it.id)!!.isUnlocked
        }
        unlockedAbilities.forEach { unlocked ->
            events.add(AbilityUnlockedEvent(
                playerId = cmd.playerId,
                abilityId = unlocked.id,
                abilityName = unlocked.name
            ))
        }
        
        events.add(StateChangedEvent(state.toString()))
        
        return Result.success(events)
    }
    
    private fun handleBuySkill(cmd: BuySkillCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val skill = progressionTree.getSkill(cmd.skillId)
            ?: return Result.failure(IllegalArgumentException("Skill not found"))
        
        // Check if can purchase
        if (!skill.canPurchase(pointSystem.availablePoints)) {
            return Result.failure(IllegalStateException("Cannot purchase skill"))
        }
        
        // Purchase skill
        val (newTree, xpGranted) = progressionTree.purchaseSkill(cmd.skillId)
        val newPointSystem = pointSystem.spendPoints(skill.cost, xpGranted)
        val oldLevel = pointSystem.currentLevel
        val newLevel = newPointSystem.currentLevel
        
        val events = mutableListOf<GCMSEvent>()
        
        // Add skill purchased event
        events.add(SkillPurchasedEvent(
            playerId = cmd.playerId,
            skillId = cmd.skillId,
            skillName = skill.name,
            cost = skill.cost,
            xpGranted = xpGranted,
            newLevel = newLevel
        ))
        
        // Add level up event if level increased
        if (newLevel > oldLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = oldLevel,
                newLevel = newLevel,
                totalXP = newPointSystem.totalXP
            ))
            audioManager?.playSound(AudioAssetManager.SOUND_COIN)
        }
        
        // Add skill unlocked events for newly unlocked skills
        val unlockedSkills = newTree.getPurchasableSkills().filter {
            !progressionTree.getSkill(it.id)!!.isUnlocked
        }
        unlockedSkills.forEach { unlocked ->
            events.add(SkillUnlockedEvent(
                playerId = cmd.playerId,
                skillId = unlocked.id,
                skillName = unlocked.name
            ))
        }
        
        events.add(StateChangedEvent(state.toString()))
        
        return Result.success(events)
    }
    
    private fun handleUpgradeAbility(cmd: UpgradeAbilityCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val ability = progressionTree.getAbility(cmd.abilityId)
            ?: return Result.failure(IllegalArgumentException("Ability not found"))
        
        if (!ability.canUpgrade()) {
            return Result.failure(IllegalStateException("Cannot upgrade ability"))
        }
        
        val cost = ability.upgradeCost()
        val xp = ability.upgradeXP()
        
        if (!pointSystem.canAfford(cost)) {
            return Result.failure(IllegalStateException("Not enough points"))
        }
        
        val newPointSystem = pointSystem.spendPoints(cost, xp)
        val updatedAbility = ability.upgrade()
        val updatedTree = progressionTree.copy(
            abilities = progressionTree.abilities.toMutableMap().apply {
                this[cmd.abilityId] = updatedAbility
            }
        )
        
        val events = mutableListOf<GCMSEvent>(
            AbilityUpgradedEvent(
                playerId = cmd.playerId,
                abilityId = cmd.abilityId,
                abilityName = ability.name,
                newRank = updatedAbility.currentRank,
                cost = cost,
                xpGranted = xp
            ),
            StateChangedEvent(state.toString())
        )
        
        if (newPointSystem.currentLevel > pointSystem.currentLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = pointSystem.currentLevel,
                newLevel = newPointSystem.currentLevel,
                totalXP = newPointSystem.totalXP
            ))
            audioManager?.playSound(AudioAssetManager.SOUND_COIN)
        }
        
        return Result.success(events)
    }
    
    private fun handleLevelUpSkill(cmd: LevelUpSkillCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val skill = progressionTree.getSkill(cmd.skillId)
            ?: return Result.failure(IllegalArgumentException("Skill not found"))
        
        if (!skill.canLevelUp()) {
            return Result.failure(IllegalStateException("Cannot level up skill"))
        }
        
        val cost = skill.levelUpCost()
        val xp = skill.levelUpXP()
        
        if (!pointSystem.canAfford(cost)) {
            return Result.failure(IllegalStateException("Not enough points"))
        }
        
        val newPointSystem = pointSystem.spendPoints(cost, xp)
        val updatedSkill = skill.levelUp()
        val updatedTree = progressionTree.copy(
            skills = progressionTree.skills.toMutableMap().apply {
                this[cmd.skillId] = updatedSkill
            }
        )
        
        val events = mutableListOf<GCMSEvent>(
            SkillLeveledUpEvent(
                playerId = cmd.playerId,
                skillId = cmd.skillId,
                skillName = skill.name,
                newLevel = updatedSkill.currentLevel,
                cost = cost,
                xpGranted = xp
            ),
            StateChangedEvent(state.toString())
        )
        
        if (newPointSystem.currentLevel > pointSystem.currentLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = pointSystem.currentLevel,
                newLevel = newPointSystem.currentLevel,
                totalXP = newPointSystem.totalXP
            ))
            audioManager?.playSound(AudioAssetManager.SOUND_COIN)
        }
        
        return Result.success(events)
    }
    
    private fun handleRefundAbility(cmd: RefundAbilityCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val ability = progressionTree.getAbility(cmd.abilityId)
            ?: return Result.failure(IllegalArgumentException("Ability not found"))
        
        val (newTree, xpLost) = progressionTree.refundAbility(cmd.abilityId)
        val (newPointSystem, xpToRegain) = pointSystem.refundPoints(ability.cost, xpLost)
        
        val events = mutableListOf<GCMSEvent>(
            AbilityRefundedEvent(
                playerId = cmd.playerId,
                abilityId = cmd.abilityId,
                abilityName = ability.name,
                refundAmount = ability.cost,
                xpLost = xpLost,
                newLevel = newPointSystem.currentLevel,
                xpToRegain = xpToRegain
            ),
            StateChangedEvent(state.toString())
        )
        
        if (newPointSystem.currentLevel < pointSystem.currentLevel) {
            events.add(LevelDownEvent(
                playerId = cmd.playerId,
                oldLevel = pointSystem.currentLevel,
                newLevel = newPointSystem.currentLevel,
                totalXP = newPointSystem.totalXP,
                penaltyMultiplier = pointSystem.penaltyMultiplier
            ))
            audioManager?.playSound(AudioAssetManager.SOUND_LOSE)
        }
        
        return Result.success(events)
    }
    
    private fun handleRefundSkill(cmd: RefundSkillCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val pointSystem = player.pointSystem
        val progressionTree = player.progressionTree
        
        val skill = progressionTree.getSkill(cmd.skillId)
            ?: return Result.failure(IllegalArgumentException("Skill not found"))
        
        val (newTree, xpLost) = progressionTree.refundSkill(cmd.skillId)
        val (newPointSystem, xpToRegain) = pointSystem.refundPoints(skill.cost, xpLost)
        
        val events = mutableListOf<GCMSEvent>(
            SkillRefundedEvent(
                playerId = cmd.playerId,
                skillId = cmd.skillId,
                skillName = skill.name,
                refundAmount = skill.cost,
                xpLost = xpLost,
                newLevel = newPointSystem.currentLevel,
                xpToRegain = xpToRegain
            ),
            StateChangedEvent(state.toString())
        )
        
        if (newPointSystem.currentLevel < pointSystem.currentLevel) {
            events.add(LevelDownEvent(
                playerId = cmd.playerId,
                oldLevel = pointSystem.currentLevel,
                newLevel = newPointSystem.currentLevel,
                totalXP = newPointSystem.totalXP,
                penaltyMultiplier = pointSystem.penaltyMultiplier
            ))
            audioManager?.playSound(AudioAssetManager.SOUND_LOSE)
        }
        
        return Result.success(events)
    }
    
    private fun handleAddPoints(cmd: AddPointsCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val newPointSystem = player.pointSystem.addPoints(cmd.amount)
        
        val events = listOf(
            PointsAddedEvent(
                playerId = cmd.playerId,
                amount = cmd.amount,
                reason = "Score/Action",
                newBalance = newPointSystem.availablePoints
            ),
            StateChangedEvent(state.toString())
        )
        
        return Result.success(events)
    }
    
    private fun handleAddXP(cmd: AddXPCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val newPointSystem = player.pointSystem.addXP(cmd.amount)
        val oldLevel = player.pointSystem.currentLevel
        val newLevel = newPointSystem.currentLevel
        
        val events = mutableListOf<GCMSEvent>(
            XPAddedEvent(
                playerId = cmd.playerId,
                amount = cmd.amount,
                reason = "Achievement/Bonus",
                newTotalXP = newPointSystem.totalXP,
                newLevel = newLevel
            ),
            StateChangedEvent(state.toString())
        )
        
        if (newLevel > oldLevel) {
            events.add(LevelUpEvent(
                playerId = cmd.playerId,
                oldLevel = oldLevel,
                newLevel = newLevel,
                totalXP = newPointSystem.totalXP
            ))
            audioManager?.playSound(AudioAssetManager.SOUND_WIN)
        }
        
        return Result.success(events)
    }
    
    private fun handleLoseXP(cmd: LoseXPCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val (newPointSystem, xpToRegain) = player.pointSystem.loseXP(cmd.amount, true)
        val oldLevel = player.pointSystem.currentLevel
        val newLevel = newPointSystem.currentLevel
        
        val events = mutableListOf<GCMSEvent>(
            XPLostEvent(
                playerId = cmd.playerId,
                amount = cmd.amount,
                reason = "Penalty",
                newTotalXP = newPointSystem.totalXP,
                newLevel = newLevel,
                xpToRegain = xpToRegain
            ),
            StateChangedEvent(state.toString())
        )
        
        if (newLevel < oldLevel) {
            events.add(LevelDownEvent(
                playerId = cmd.playerId,
                oldLevel = oldLevel,
                newLevel = newLevel,
                totalXP = newPointSystem.totalXP,
                penaltyMultiplier = player.pointSystem.penaltyMultiplier
            ))
            audioManager?.playSound(AudioAssetManager.SOUND_LOSE)
        }
        
        return Result.success(events)
    }
}