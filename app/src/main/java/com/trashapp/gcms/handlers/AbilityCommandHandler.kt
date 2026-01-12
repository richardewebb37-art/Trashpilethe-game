package com.trashapp.gcms.handlers

import com.trashapp.gcms.commands.AbilityCommand
import com.trashapp.gcms.events.AbilityEvent
import com.trashapp.gcms.progression.Ability
import com.trashapp.gcms.progression.ProgressionTree

/**
 * Ability Command Handler
 * Handles all ability-related commands in the GCMS system
 */
class AbilityCommandHandler(
    private val eventEmitter: (AbilityEvent) -> Unit,
    private val audioNotifier: (String) -> Unit = {}
) {
    
    /**
     * Handle an ability command
     */
    fun handleCommand(
        command: AbilityCommand,
        progressionTree: ProgressionTree,
        currentAP: Int,
        playerLevel: Int
    ): Pair<ProgressionTree, Int>? {
        return when (command) {
            is AbilityCommand.UseAbilityCommand -> 
                handleUseAbility(command, progressionTree, currentAP, playerLevel)
            is AbilityCommand.ActivateSkillEffectCommand -> 
                handleActivateSkillEffect(command, progressionTree, playerLevel)
            is AbilityCommand.TrackAbilityUsageCommand -> 
                handleTrackAbilityUsage(command, progressionTree)
            is AbilityCommand.ResetAbilityUsageCommand -> 
                handleResetAbilityUsage(command, progressionTree)
            is AbilityCommand.CheckAbilityAvailableCommand -> 
                handleCheckAbilityAvailable(command, progressionTree, currentAP)
            is AbilityCommand.GetAbilityRemainingUsesCommand -> 
                handleGetAbilityRemainingUses(command, progressionTree)
            is AbilityCommand.PurchaseAbilityCommand -> 
                handlePurchaseAbility(command, progressionTree, currentAP, playerLevel)
            is AbilityCommand.RefundAbilityCommand -> 
                handleRefundAbility(command, progressionTree, currentAP)
        }
    }
    
    /**
     * Handle UseAbilityCommand
     */
    private fun handleUseAbility(
        command: AbilityCommand.UseAbilityCommand,
        progressionTree: ProgressionTree,
        currentAP: Int,
        playerLevel: Int
    ): Pair<ProgressionTree, Int>? {
        val ability = progressionTree.getAbility(command.abilityId) ?: return null
        
        // Check if ability is purchased
        if (!ability.isPurchased) {
            eventEmitter(AbilityEvent.AbilityUseFailedEvent(
                abilityId = command.abilityId,
                abilityName = ability.name,
                reason = "Ability not purchased"
            ))
            return null
        }
        
        // Check if ability can be used
        if (!ability.canUse()) {
            val reason = when {
                ability.usesThisRound >= ability.usageLimit -> "Round usage limit reached"
                ability.usesThisMatch >= ability.usageLimit -> "Match usage limit reached"
                else -> "Cannot use ability"
            }
            
            eventEmitter(AbilityEvent.AbilityUseFailedEvent(
                abilityId = command.abilityId,
                abilityName = ability.name,
                reason = reason
            ))
            return null
        }
        
        // Check if player has enough AP
        if (currentAP < ability.cost) {
            eventEmitter(AbilityEvent.AbilityUseFailedEvent(
                abilityId = command.abilityId,
                abilityName = ability.name,
                reason = "Insufficient AP"
            ))
            return null
        }
        
        // Use the ability
        val result = progressionTree.useAbility(command.abilityId, currentAP)
        if (result == null) {
            eventEmitter(AbilityEvent.AbilityUseFailedEvent(
                abilityId = command.abilityId,
                abilityName = ability.name,
                reason = "Failed to use ability"
            ))
            return null
        }
        
        val (newTree, newAP) = result
        val updatedAbility = newTree.getAbility(command.abilityId)!!
        
        // Play sound effect
        audioNotifier("ability_use")
        
        // Emit event
        eventEmitter(AbilityEvent.AbilityUsedEvent(
            abilityId = command.abilityId,
            abilityName = ability.name,
            costPaid = ability.cost,
            remainingUses = updatedAbility.getRemainingUses()
        ))
        
        // Check if ability is now unavailable
        if (!updatedAbility.canUse()) {
            eventEmitter(AbilityEvent.AbilityUnavailableEvent(
                abilityId = command.abilityId,
                abilityName = ability.name,
                reason = "Usage limit reached"
            ))
        }
        
        return result
    }
    
    /**
     * Handle ActivateSkillEffectCommand
     */
    private fun handleActivateSkillEffect(
        command: AbilityCommand.ActivateSkillEffectCommand,
        progressionTree: ProgressionTree,
        playerLevel: Int
    ): Pair<ProgressionTree, Int>? {
        val skill = progressionTree.getSkill(command.skillId) ?: return null
        
        // Check if skill is purchased
        if (!skill.isPurchased) {
            return null
        }
        
        // Emit event about skill effect
        eventEmitter(AbilityEvent.SkillEffectTriggeredEvent(
            skillId = command.skillId,
            skillName = skill.name,
            effectDescription = skill.description
        ))
        
        return Pair(progressionTree, 0)
    }
    
    /**
     * Handle TrackAbilityUsageCommand
     */
    private fun handleTrackAbilityUsage(
        command: AbilityCommand.TrackAbilityUsageCommand,
        progressionTree: ProgressionTree
    ): Pair<ProgressionTree, Int>? {
        val ability = progressionTree.getAbility(command.abilityId) ?: return null
        
        val updatedAbility = when (command.usageType) {
            AbilityCommand.UsageType.ROUND -> ability.copy(
                usesThisRound = ability.usesThisRound + 1,
                usesThisMatch = ability.usesThisMatch + 1
            )
            AbilityCommand.UsageType.MATCH -> ability.copy(
                usesThisMatch = ability.usesThisMatch + 1
            )
        }
        
        val newTree = progressionTree.copy(
            abilities = progressionTree.abilities + (command.abilityId to updatedAbility)
        )
        
        eventEmitter(AbilityEvent.AbilityUsageUpdatedEvent(
            abilityId = command.abilityId,
            usesThisRound = updatedAbility.usesThisRound,
            usesThisMatch = updatedAbility.usesThisMatch,
            remainingUses = updatedAbility.getRemainingUses()
        ))
        
        return Pair(newTree, 0)
    }
    
    /**
     * Handle ResetAbilityUsageCommand
     */
    private fun handleResetAbilityUsage(
        command: AbilityCommand.ResetAbilityUsage,
        progressionTree: ProgressionTree
    ): Pair<ProgressionTree, Int>? {
        val newTree = when (command.resetType) {
            AbilityCommand.ResetType.ROUND -> progressionTree.resetRoundUsage()
            AbilityCommand.ResetType.MATCH -> progressionTree.resetMatchUsage()
        }
        
        // Count abilities that were reset
        val abilitiesReset = newTree.abilities.values.count { ability ->
            val oldAbility = progressionTree.getAbility(ability.id)
            oldAbility != null && (
                oldAbility.usesThisRound > 0 || oldAbility.usesThisMatch > 0
            )
        }
        
        eventEmitter(AbilityEvent.AbilityUsageResetEvent(
            resetType = when (command.resetType) {
                AbilityCommand.ResetType.ROUND -> AbilityCommand.UsageType.ROUND
                AbilityCommand.ResetType.MATCH -> AbilityCommand.UsageType.MATCH
            },
            abilitiesReset = abilitiesReset
        ))
        
        return Pair(newTree, 0)
    }
    
    /**
     * Handle CheckAbilityAvailableCommand
     */
    private fun handleCheckAbilityAvailable(
        command: AbilityCommand.CheckAbilityAvailableCommand,
        progressionTree: ProgressionTree,
        currentAP: Int
    ): Pair<ProgressionTree, Int>? {
        val ability = progressionTree.getAbility(command.abilityId) ?: return null
        
        val isAvailable = ability.isPurchased && 
                          ability.canUse() && 
                          currentAP >= ability.cost
        
        if (isAvailable) {
            eventEmitter(AbilityEvent.AbilityAvailableEvent(
                abilityId = command.abilityId,
                abilityName = ability.name,
                reason = "Ability is ready to use"
            ))
        } else {
            val reason = when {
                !ability.isPurchased -> "Ability not purchased"
                !ability.canUse() -> "Usage limit reached"
                currentAP < ability.cost -> "Insufficient AP"
                else -> "Ability unavailable"
            }
            
            eventEmitter(AbilityEvent.AbilityUnavailableEvent(
                abilityId = command.abilityId,
                abilityName = ability.name,
                reason = reason
            ))
        }
        
        return Pair(progressionTree, currentAP)
    }
    
    /**
     * Handle GetAbilityRemainingUsesCommand
     */
    private fun handleGetAbilityRemainingUses(
        command: AbilityCommand.GetAbilityRemainingUsesCommand,
        progressionTree: ProgressionTree
    ): Pair<ProgressionTree, Int>? {
        val ability = progressionTree.getAbility(command.abilityId) ?: return null
        
        eventEmitter(AbilityEvent.AbilityUsageUpdatedEvent(
            abilityId = command.abilityId,
            usesThisRound = ability.usesThisRound,
            usesThisMatch = ability.usesThisMatch,
            remainingUses = ability.getRemainingUses()
        ))
        
        return Pair(progressionTree, 0)
    }
    
    /**
     * Handle PurchaseAbilityCommand
     */
    private fun handlePurchaseAbility(
        command: AbilityCommand.PurchaseAbilityCommand,
        progressionTree: ProgressionTree,
        currentAP: Int,
        playerLevel: Int
    ): Pair<ProgressionTree, Int>? {
        val ability = progressionTree.getAbility(command.abilityId) ?: return null
        
        // Check if ability is unlocked
        if (!ability.isUnlocked) {
            return null
        }
        
        // Check if already purchased
        if (ability.isPurchased) {
            return null
        }
        
        // Check if prerequisites are met
        val prerequisitesMet = ability.prerequisites.all { prerequisiteId ->
            val skill = progressionTree.getSkill(prerequisiteId)
            val prereqAbility = progressionTree.getAbility(prerequisiteId)
            
            (skill?.isPurchased == true) || (prereqAbility?.isPurchased == true)
        }
        
        if (!prerequisitesMet) {
            return null
        }
        
        // Check if player has enough points
        if (currentAP < command.cost) {
            return null
        }
        
        // Purchase the ability
        val result = progressionTree.purchaseAbility(command.abilityId, currentAP)
        val (newTree, newAP) = result
        
        val updatedAbility = newTree.getAbility(command.abilityId)!!
        
        // Play sound effect
        audioNotifier("ability_purchase")
        
        // Emit event
        eventEmitter(AbilityEvent.AbilityPurchasedEvent(
            abilityId = command.abilityId,
            abilityName = ability.name,
            costPaid = command.cost,
            xpGranted = updatedAbility.getTotalXPGranted()
        ))
        
        return result
    }
    
    /**
     * Handle RefundAbilityCommand
     */
    private fun handleRefundAbility(
        command: AbilityCommand.RefundAbilityCommand,
        progressionTree: ProgressionTree,
        currentAP: Int
    ): Pair<ProgressionTree, Int>? {
        val ability = progressionTree.getAbility(command.abilityId) ?: return null
        
        // Check if ability is purchased
        if (!ability.isPurchased) {
            return null
        }
        
        // Refund the ability
        val updatedAbility = ability.refund()
        val newTree = progressionTree.copy(
            abilities = progressionTree.abilities + (command.abilityId to updatedAbility)
        )
        
        val pointsRefunded = ability.cost
        val xpLost = updatedAbility.getTotalXPGranted()
        val newAP = currentAP + pointsRefunded
        
        // Play sound effect
        audioNotifier("ability_refund")
        
        // Emit event
        eventEmitter(AbilityEvent.AbilityRefundedEvent(
            abilityId = command.abilityId,
            abilityName = ability.name,
            pointsRefunded = pointsRefunded,
            xpLost = xpLost
        ))
        
        return Pair(newTree, newAP)
    }
}