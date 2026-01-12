package com.trashapp.gcms.events

import com.trashapp.gcms.commands.AbilityCommand

/**
 * Ability Events
 * Events related to ability usage and status changes
 */
sealed class AbilityEvent : GCMSEvent() {
    
    /**
     * Ability was used successfully
     */
    data class AbilityUsedEvent(
        val abilityId: String,
        val abilityName: String,
        val costPaid: Int,
        val remainingUses: Int
    ) : AbilityEvent()
    
    /**
     * Passive skill effect was triggered
     */
    data class SkillEffectTriggeredEvent(
        val skillId: String,
        val skillName: String,
        val effectDescription: String
    ) : AbilityEvent()
    
    /**
     * Ability usage count was updated
     */
    data class AbilityUsageUpdatedEvent(
        val abilityId: String,
        val usesThisRound: Int,
        val usesThisMatch: Int,
        val remainingUses: Int
    ) : AbilityEvent()
    
    /**
     * Ability usage was reset
     */
    data class AbilityUsageResetEvent(
        val resetType: AbilityCommand.UsageType,
        val abilitiesReset: Int
    ) : AbilityEvent()
    
    /**
     * Ability became available to use
     */
    data class AbilityAvailableEvent(
        val abilityId: String,
        val abilityName: String,
        val reason: String
    ) : AbilityEvent()
    
    /**
     * Ability became unavailable (usage limit reached)
     */
    data class AbilityUnavailableEvent(
        val abilityId: String,
        val abilityName: String,
        val reason: String
    ) : AbilityEvent()
    
    /**
     * Ability was purchased
     */
    data class AbilityPurchasedEvent(
        val abilityId: String,
        val abilityName: String,
        val costPaid: Int,
        val xpGranted: Int
    ) : AbilityEvent()
    
    /**
     * Ability was refunded
     */
    data class AbilityRefundedEvent(
        val abilityId: String,
        val abilityName: String,
        val pointsRefunded: Int,
        val xpLost: Int
    ) : AbilityEvent()
    
    /**
     * Ability failed to use (insufficient AP, limit reached, etc.)
     */
    data class AbilityUseFailedEvent(
        val abilityId: String,
        val abilityName: String,
        val reason: String
    ) : AbilityEvent()
}