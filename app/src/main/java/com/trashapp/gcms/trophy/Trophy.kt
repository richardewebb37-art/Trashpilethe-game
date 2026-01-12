package com.trashapp.gcms.trophy

import com.trashapp.gcms.progression.Tier

/**
 * Represents a trophy that can be awarded to players
 * 
 * Trophy rewards are dynamic and based on:
 * - Player level and tier
 * - Points, abilities, and skills unlocked
 * - Randomized allocation within tier ranges
 */
data class Trophy(
    val id: String,
    val name: String,
    val description: String,
    val tier: Tier,
    val requiredLevel: Int,
    val requiredAbilities: List<String> = emptyList(),
    val requiredSkills: List<String> = emptyList(),
    val requiredPoints: Int = 0,
    val rarity: TrophyRarity,
    val icon: String? = null, // Resource identifier for trophy icon
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null // Timestamp when unlocked
) {
    /**
     * Check if player meets all prerequisites to earn this trophy
     */
    fun canUnlock(
        playerLevel: Int,
        playerPoints: Int,
        playerAbilities: Set<String>,
        playerSkills: Set<String>
    ): Boolean {
        // Must be at required level
        if (playerLevel < requiredLevel) return false
        
        // Must have required points
        if (playerPoints < requiredPoints) return false
        
        // Must have all required abilities
        val hasAbilities = requiredAbilities.all { it in playerAbilities }
        if (!hasAbilities) return false
        
        // Must have all required skills
        val hasSkills = requiredSkills.all { it in playerSkills }
        if (!hasSkills) return false
        
        return true
    }
    
    /**
     * Get trophy XP value based on rarity
     */
    fun getXPValue(): Int {
        return rarity.xpValue
    }
    
    /**
     * Get trophy point bonus
     */
    fun getPointBonus(): Int {
        return rarity.pointBonus
    }
}

/**
 * Trophy rarity levels with associated rewards
 */
enum class TrophyRarity(val displayName: String, val xpValue: Int, val pointBonus: Int) {
    COMMON("Common", 25, 5),
    UNCOMMON("Uncommon", 50, 10),
    RARE("Rare", 100, 25),
    EPIC("Epic", 250, 50),
    LEGENDARY("Legendary", 500, 100),
    MYTHIC("Mythic", 1000, 200)
}