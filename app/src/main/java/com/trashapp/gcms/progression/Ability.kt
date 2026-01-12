package com.trashapp.gcms.progression

/**
 * Ability
 * Represents a purchasable ability in the progression tree
 * Abilities provide gameplay advantages and grant XP when purchased
 * Enhanced to support usage limits, costs, and detailed mechanics
 */
data class Ability(
    val id: String,
    val name: String,
    val description: String,
    val tier: Tier = Tier.NEWBIE,           // Progression tier
    val levelRequired: Int = 1,              // Level required to unlock
    val cost: Int,                           // AP cost to use
    val xpGranted: Int,                      // XP granted when purchased
    val icon: String? = null,                // Icon resource name
    val category: AbilityCategory = AbilityCategory.GENERAL,
    val rarity: AbilityRarity = AbilityRarity.COMMON,
    val maxRank: Int = 1,                    // Maximum upgrade rank
    val currentRank: Int = 0,                // Current upgrade rank
    val skillId: String? = null,             // Parent skill ID (if part of a skill)
    val prerequisites: List<String> = emptyList(),  // Required ability/skill IDs
    
    // Usage limits
    val usageLimitType: UsageLimitType = UsageLimitType.UNLIMITED,  // Per round, per match, or unlimited
    val usageLimit: Int = 0,                 // Number of uses allowed (0 = unlimited)
    val usesThisRound: Int = 0,              // Current uses this round
    val usesThisMatch: Int = 0,              // Current uses this match
    
    val unlocks: List<String> = emptyList(),          // Abilities this unlocks
    val isUnlocked: Boolean = false,          // Is this ability available for purchase?
    val isPurchased: Boolean = false,         // Has this ability been purchased?
    val isActive: Boolean = false,            // Is this ability currently active?
    
    // Effect details
    val effectType: EffectType = EffectType.PASSIVE,  // Effect type
    val effectValue: Int = 0,               // Effect magnitude (if applicable)
    val effectTarget: EffectTarget = EffectTarget.SELF  // Effect target
) {
    /**
     * Check if ability can be upgraded
     */
    fun canUpgrade(): Boolean {
        return isPurchased && currentRank < maxRank
    }
    
    /**
     * Check if ability can be purchased
     */
    fun canPurchase(availablePoints: Int): Boolean {
        return isUnlocked && !isPurchased && availablePoints >= cost
    }
    
    /**
     * Check if ability can be used
     */
    fun canUse(): Boolean {
        if (!isPurchased) return false
        if (!isActive) return false
        
        return when (usageLimitType) {
            UsageLimitType.UNLIMITED -> true
            UsageLimitType.PER_ROUND -> usesThisRound < usageLimit
            UsageLimitType.PER_MATCH -> usesThisMatch < usageLimit
        }
    }
    
    /**
     * Use this ability
     */
    fun use(): Ability {
        return when (usageLimitType) {
            UsageLimitType.UNLIMITED -> this
            UsageLimitType.PER_ROUND -> copy(usesThisRound = usesThisRound + 1, usesThisMatch = usesThisMatch + 1)
            UsageLimitType.PER_MATCH -> copy(usesThisMatch = usesThisMatch + 1)
        }
    }
    
    /**
     * Reset round usage (called at start of each round)
     */
    fun resetRoundUsage(): Ability {
        return copy(usesThisRound = 0)
    }
    
    /**
     * Reset match usage (called at start of each match)
     */
    fun resetMatchUsage(): Ability {
        return copy(usesThisRound = 0, usesThisMatch = 0)
    }
    
    /**
     * Get remaining uses
     */
    fun getRemainingUses(): Int {
        return when (usageLimitType) {
            UsageLimitType.UNLIMITED -> -1  // -1 indicates unlimited
            UsageLimitType.PER_ROUND -> usageLimit - usesThisRound
            UsageLimitType.PER_MATCH -> usageLimit - usesThisMatch
        }
    }
    
    /**
     * Get usage description
     */
    fun getUsageDescription(): String {
        return when (usageLimitType) {
            UsageLimitType.UNLIMITED -> "Unlimited uses"
            UsageLimitType.PER_ROUND -> "$usageLimit use(s) per round"
            UsageLimitType.PER_MATCH -> "$usageLimit use(s) per match"
        }
    }
    
    /**
     * Calculate cost for next upgrade
     */
    fun upgradeCost(): Int {
        return cost * (currentRank + 1)
    }
    
    /**
     * Calculate XP granted for upgrade
     */
    fun upgradeXP(): Int {
        return xpGranted * (currentRank + 1)
    }
    
    /**
     * Purchase this ability
     */
    fun purchase(): Ability {
        return copy(
            currentRank = 1,
            isPurchased = true,
            isActive = true
        )
    }
    
    /**
     * Upgrade this ability
     */
    fun upgrade(): Ability {
        return copy(
            currentRank = currentRank + 1
        )
    }
    
    /**
     * Refund (sell) this ability
     */
    fun refund(): Ability {
        return copy(
            currentRank = 0,
            isPurchased = false,
            isActive = false,
            usesThisRound = 0,
            usesThisMatch = 0
        )
    }
    
    /**
     * Calculate XP multiplier based on rarity
     */
    fun getXPMultiplier(): Float {
        return when (rarity) {
            AbilityRarity.COMMON -> 1.0f
            AbilityRarity.UNCOMMON -> 1.25f
            AbilityRarity.RARE -> 1.5f
            AbilityRarity.EPIC -> 2.0f
            AbilityRarity.LEGENDARY -> 3.0f
        }
    }
    
    /**
     * Get total XP granted including rank multipliers
     */
    fun getTotalXPGranted(): Int {
        return (xpGranted * currentRank * getXPMultiplier()).toInt()
    }
}

/**
 * Ability Categories - Updated to match new specification
 */
enum class AbilityCategory {
    GENERAL,      // General utility abilities
    COMBAT,       // Combat/Offensive abilities
    DEFENSE,      // Defensive abilities
    SUPPORT,      // Support/Tactical abilities
    MAGIC,        // Magic/Arcane abilities
    MOVEMENT,     // Movement/Evasion abilities
    PRECISION,    // Precision/Technique abilities
    POWER,        // Power/Strength abilities
    MENTAL,       // Mental/Special abilities
    SPECIAL       // Special/Master abilities
}

/**
 * Ability Rarity
 */
enum class AbilityRarity {
    COMMON,       // Common abilities
    UNCOMMON,     // Uncommon abilities
    RARE,         // Rare abilities
    EPIC,         // Epic abilities
    LEGENDARY     // Legendary abilities
}

/**
 * Usage Limit Type
 */
enum class UsageLimitType {
    UNLIMITED,    // Can be used unlimited times
    PER_ROUND,    // Limited uses per round (resets each round)
    PER_MATCH     // Limited uses per match (resets each match)
}

/**
 * Effect Type
 */
enum class EffectType {
    PASSIVE,      // Always active passive effect
    ACTIVE,       // Must be activated
    TRIGGERED,    // Triggered by game events
    TOGGLE        // Can be toggled on/off
}

/**
 * Effect Target
 */
enum class EffectTarget {
    SELF,         // Affects the player
    OPPONENT,     // Affects the opponent
    BOTH,         // Affects both players
    DECK,         // Affects the deck
    DISCARD,      // Affects the discard pile
    BOARD         // Affects the game board
}