package com.trashapp.gcms.progression

/**
 * Skill
 * Represents a purchasable skill in the progression tree
 * Skills are passive bonuses that grant XP when purchased
 * Enhanced to support tiered progression with detailed mechanics
 */
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val tier: Tier = Tier.NEWBIE,          // Progression tier
    val levelRequired: Int = 1,             // Level required to unlock
    val cost: Int,                          // SP cost to purchase
    val xpGranted: Int,                     // XP granted when purchased
    val icon: String? = null,               // Icon resource name
    val category: SkillCategory = SkillCategory.GENERAL,
    val rarity: SkillRarity = SkillRarity.COMMON,
    val maxLevel: Int = 1,                  // Maximum skill level (most skills are single-level)
    val currentLevel: Int = 0,              // Current skill level
    val prerequisites: List<String> = emptyList(),  // Required skill IDs
    val unlocks: List<String> = emptyList(),        // Skills this unlocks
    val abilities: List<String> = emptyList(),      // Ability IDs contained in this skill
    val isUnlocked: Boolean = false,  // Is this skill available for purchase?
    val isPurchased: Boolean = false  // Has this skill been purchased?
) {
    /**
     * Check if skill can be leveled up
     */
    fun canLevelUp(): Boolean {
        return isPurchased && currentLevel < maxLevel
    }
    
    /**
     * Check if skill can be purchased
     */
    fun canPurchase(availablePoints: Int): Boolean {
        return isUnlocked && !isPurchased && availablePoints >= cost
    }
    
    /**
     * Calculate cost for next level
     */
    fun levelUpCost(): Int {
        if (currentLevel == 0) return cost
        return cost * (currentLevel + 1)
    }
    
    /**
     * Calculate XP granted for level up
     */
    fun levelUpXP(): Int {
        if (currentLevel == 0) return xpGranted
        return xpGranted * (currentLevel + 1)
    }
    
    /**
     * Purchase this skill
     */
    fun purchase(): Skill {
        return copy(
            currentLevel = 1,
            isPurchased = true
        )
    }
    
    /**
     * Level up this skill
     */
    fun levelUp(): Skill {
        return copy(
            currentLevel = currentLevel + 1
        )
    }
    
    /**
     * Refund (sell) this skill
     */
    fun refund(): Skill {
        return copy(
            currentLevel = 0,
            isPurchased = false
        )
    }
    
    /**
     * Get progress to next level (0.0 to 1.0)
     */
    fun levelProgress(): Float {
        return if (maxLevel > 0) currentLevel.toFloat() / maxLevel.toFloat() else 1.0f
    }
    
    /**
     * Get number of abilities in this skill
     */
    fun getAbilityCount(): Int = abilities.size
    
    /**
     * Calculate XP multiplier based on rarity
     */
    fun getXPMultiplier(): Float {
        return when (rarity) {
            SkillRarity.COMMON -> 1.0f
            SkillRarity.UNCOMMON -> 1.25f
            SkillRarity.RARE -> 1.5f
            SkillRarity.EPIC -> 2.0f
            SkillRarity.LEGENDARY -> 3.0f
        }
    }
    
    /**
     * Check if this skill is a master tier skill
     */
    fun isMasterTier(): Boolean = tier == Tier.MASTER
    
    /**
     * Check if this skill has prerequisites
     */
    fun hasPrerequisites(): Boolean = prerequisites.isNotEmpty()
}

/**
 * Skill Categories - Updated to match new specification
 */
enum class SkillCategory {
    GENERAL,      // General utility/progression skills
    COMBAT,       // Combat/Offensive skills
    DEFENSE,      // Defense/Survival skills
    SUPPORT,      // Support/Tactical skills
    MAGIC,        // Magic/Arcane skills
    MOVEMENT,     // Movement/Evasion skills
    PRECISION,    // Precision/Technique skills
    POWER,        // Power/Strength skills
    MENTAL,       // Mental/Special skills
    SPECIAL       // Special/Master skills
}

/**
 * Skill Rarity
 */
enum class SkillRarity {
    COMMON,       // Common skills
    UNCOMMON,     // Uncommon skills
    RARE,         // Rare skills
    EPIC,         // Epic skills
    LEGENDARY     // Legendary skills
}