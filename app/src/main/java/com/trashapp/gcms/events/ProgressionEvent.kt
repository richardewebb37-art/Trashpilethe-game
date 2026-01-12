package com.trashapp.gcms.events

/**
 * Progression Events
 * Events related to progression system (levels, abilities, skills)
 */

/**
 * Level Up Event
 * Triggered when player gains a level
 */
data class LevelUpEvent(
    val playerId: String,
    val oldLevel: Int,
    val newLevel: Int,
    val totalXP: Int,
    val oldTier: Tier,
    val newTier: Tier
) : GCMSEvent()

/**
 * Level Down Event
 * Triggered when player loses a level
 */
data class LevelDownEvent(
    val playerId: String,
    val oldLevel: Int,
    val newLevel: Int,
    val totalXP: Int,
    val penaltyMultiplier: Float,
    val oldTier: Tier,
    val newTier: Tier
) : GCMSEvent()

/**
 * Ability Purchased Event
 * Triggered when player buys an ability
 */
data class AbilityPurchasedEvent(
    val playerId: String,
    val abilityId: String,
    val abilityName: String,
    val cost: Int,
    val xpGranted: Int,
    val newLevel: Int,
    val tier: Tier
) : GCMSEvent()

/**
 * Ability Upgraded Event
 * Triggered when player upgrades an ability
 */
data class AbilityUpgradedEvent(
    val playerId: String,
    val abilityId: String,
    val abilityName: String,
    val newRank: Int,
    val cost: Int,
    val xpGranted: Int
) : GCMSEvent()

/**
 * Ability Refunded Event
 * Triggered when player sells/refunds an ability
 */
data class AbilityRefundedEvent(
    val playerId: String,
    val abilityId: String,
    val abilityName: String,
    val refundAmount: Int,
    val xpLost: Int,
    val newLevel: Int,
    val xpToRegain: Int
) : GCMSEvent()

/**
 * Skill Purchased Event
 * Triggered when player buys a skill
 */
data class SkillPurchasedEvent(
    val playerId: String,
    val skillId: String,
    val skillName: String,
    val cost: Int,
    val xpGranted: Int,
    val newLevel: Int,
    val tier: Tier,
    val unlockedAbilities: List<String> = emptyList()
) : GCMSEvent()

/**
 * Skill Leveled Up Event
 * Triggered when player levels up a skill
 */
data class SkillLeveledUpEvent(
    val playerId: String,
    val skillId: String,
    val skillName: String,
    val newLevel: Int,
    val cost: Int,
    val xpGranted: Int
) : GCMSEvent()

/**
 * Skill Refunded Event
 * Triggered when player sells/refunds a skill
 */
data class SkillRefundedEvent(
    val playerId: String,
    val skillId: String,
    val skillName: String,
    val refundAmount: Int,
    val xpLost: Int,
    val newLevel: Int,
    val xpToRegain: Int
) : GCMSEvent()

/**
 * Points Added Event
 * Triggered when player earns points
 */
data class PointsAddedEvent(
    val playerId: String,
    val amount: Int,
    val reason: String,
    val newBalance: Int
) : GCMSEvent()

/**
 * XP Added Event
 * Triggered when player gains XP
 */
data class XPAddedEvent(
    val playerId: String,
    val amount: Int,
    val reason: String,
    val newTotalXP: Int,
    val newLevel: Int
) : GCMSEvent()

/**
 * XP Lost Event
 * Triggered when player loses XP
 */
data class XPLostEvent(
    val playerId: String,
    val amount: Int,
    val reason: String,
    val newTotalXP: Int,
    val newLevel: Int,
    val xpToRegain: Int
) : GCMSEvent()

/**
 * Ability Unlocked Event
 * Triggered when an ability becomes available for purchase
 */
data class AbilityUnlockedEvent(
    val playerId: String,
    val abilityId: String,
    val abilityName: String
) : GCMSEvent()

/**
 * Skill Unlocked Event
 * Triggered when a skill becomes available for purchase
 */
data class SkillUnlockedEvent(
    val playerId: String,
    val skillId: String,
    val skillName: String
) : GCMSEvent()