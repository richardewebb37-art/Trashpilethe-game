package com.trashapp.gcms.commands

/**
 * Ability Commands
 * Commands related to using abilities in the game
 */
sealed class AbilityCommand : GCMSCommand() {
    
    /**
     * Use an ability
     */
    data class UseAbilityCommand(
        val abilityId: String,
        val abilityCost: Int
    ) : AbilityCommand()
    
    /**
     * Activate a passive skill effect
     */
    data class ActivateSkillEffectCommand(
        val skillId: String
    ) : AbilityCommand()
    
    /**
     * Track ability usage (for limits)
     */
    data class TrackAbilityUsageCommand(
        val abilityId: String,
        val usageType: UsageType
    ) : AbilityCommand()
    
    /**
     * Reset ability usage (round or match)
     */
    data class ResetAbilityUsageCommand(
        val resetType: ResetType
    ) : AbilityCommand()
    
    /**
     * Check if ability can be used
     */
    data class CheckAbilityAvailableCommand(
        val abilityId: String,
        val availableAP: Int
    ) : AbilityCommand()
    
    /**
     * Get remaining uses for an ability
     */
    data class GetAbilityRemainingUsesCommand(
        val abilityId: String
    ) : AbilityCommand()
    
    /**
     * Purchase ability with points
     */
    data class PurchaseAbilityCommand(
        val abilityId: String,
        val cost: Int
    ) : AbilityCommand()
    
    /**
     * Refund ability
     */
    data class RefundAbilityCommand(
        val abilityId: String
    ) : AbilityCommand()
    
    /**
     * Usage type for tracking
     */
    enum class UsageType {
        ROUND,
        MATCH
    }
    
    /**
     * Reset type for ability usage
     */
    enum class ResetType {
        ROUND,
        MATCH
    }
}