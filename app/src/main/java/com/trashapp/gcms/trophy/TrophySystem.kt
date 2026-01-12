package com.trashapp.gcms.trophy

import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.trophy.TrophyRarity.*
import kotlin.random.Random

/**
 * Trophy System - Dynamic reward allocation based on level, tier, and prerequisites
 * 
 * This system manages:
 * - Trophy count calculation per level-up
 * - Trophy eligibility checking
 * - Trophy rarity distribution
 * - Randomization within tier ranges
 */
class TrophySystem {
    
    companion object {
        // Trophy ranges per tier
        private val TIER_RANGES = mapOf(
            Tier.LIFE to TrophyRange(1, 3),
            Tier.BEGINNER to TrophyRange(2, 5),
            Tier.NOVICE to TrophyRange(3, 7),
            Tier.HARD to TrophyRange(5, 10),
            Tier.EXPERT to TrophyRange(8, 15),
            Tier.MASTER to TrophyRange(10, 20)
        )
        
        // Rarity distribution per tier (weight-based)
        private val TIER_RARITY_DISTRIBUTION = mapOf(
            Tier.LIFE to mapOf(
                COMMON to 70f,
                UNCOMMON to 25f,
                RARE to 5f
            ),
            Tier.BEGINNER to mapOf(
                COMMON to 60f,
                UNCOMMON to 30f,
                RARE to 8f,
                EPIC to 2f
            ),
            Tier.NOVICE to mapOf(
                COMMON to 50f,
                UNCOMMON to 35f,
                RARE to 12f,
                EPIC to 3f
            ),
            Tier.HARD to mapOf(
                COMMON to 35f,
                UNCOMMON to 40f,
                RARE to 18f,
                EPIC to 6f,
                LEGENDARY to 1f
            ),
            Tier.EXPERT to mapOf(
                COMMON to 20f,
                UNCOMMON to 35f,
                RARE to 30f,
                EPIC to 12f,
                LEGENDARY to 3f
            ),
            Tier.MASTER to mapOf(
                COMMON to 10f,
                UNCOMMON to 25f,
                RARE to 35f,
                EPIC to 20f,
                LEGENDARY to 8f,
                MYTHIC to 2f
            )
        )
    }
    
    /**
     * Calculate number of trophies to award for level up
     */
    fun calculateTrophyCount(level: Int, tier: Tier): Int {
        val range = TIER_RANGES[tier] ?: TrophyRange(1, 3)
        
        // Add slight variation based on level within tier
        val levelBonus = when (tier) {
            Tier.LIFE -> 0
            Tier.BEGINNER -> (level - 5) / 5
            Tier.NOVICE -> (level - 20) / 10
            Tier.HARD -> (level - 50) / 10
            Tier.EXPERT -> (level - 80) / 15
            Tier.MASTER -> (level - 140) / 15
        }
        
        val baseCount = Random.nextInt(range.min, range.max + 1)
        return (baseCount + levelBonus).coerceAtMost(range.max + 3)
    }
    
    /**
     * Determine trophy rarity based on tier and randomness
     */
    fun determineRarity(tier: Tier): TrophyRarity {
        val distribution = TIER_RARITY_DISTRIBUTION[tier] ?: 
            mapOf(COMMON to 70f, UNCOMMON to 25f, RARE to 5f)
        
        val random = Random.nextFloat() * 100f
        var cumulative = 0f
        
        for ((rarity, weight) in distribution) {
            cumulative += weight
            if (random <= cumulative) {
                return rarity
            }
        }
        
        return COMMON // Fallback
    }
    
    /**
     * Check which trophies the player is eligible for
     */
    fun getEligibleTrophies(
        availableTrophies: List<Trophy>,
        playerLevel: Int,
        playerPoints: Int,
        playerAbilities: Set<String>,
        playerSkills: Set<String>
    ): List<Trophy> {
        return availableTrophies.filter { trophy ->
            !trophy.isUnlocked && trophy.canUnlock(
                playerLevel,
                playerPoints,
                playerAbilities,
                playerSkills
            )
        }
    }
    
    /**
     * Award trophies for level up
     */
    fun awardTrophiesForLevelUp(
        level: Int,
        tier: Tier,
        availableTrophies: List<Trophy>,
        playerPoints: Int,
        playerAbilities: Set<String>,
        playerSkills: Set<String>
    ): TrophyReward {
        val trophyCount = calculateTrophyCount(level, tier)
        val eligibleTrophies = getEligibleTrophies(
            availableTrophies,
            level,
            playerPoints,
            playerAbilities,
            playerSkills
        )
        
        // Shuffle eligible trophies for randomness
        val shuffledEligible = eligibleTrophies.shuffled()
        
        // Award trophies based on count
        val awardedTrophies = mutableListOf<Trophy>()
        val randomTrophies = mutableListOf<Trophy>()
        
        for (i in 0 until trophyCount) {
            if (i < shuffledEligible.size) {
                // Award eligible trophy
                val trophy = shuffledEligible[i].copy(
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
                awardedTrophies.add(trophy)
            } else {
                // Generate random trophy if no eligible ones left
                val rarity = determineRarity(tier)
                val randomTrophy = generateRandomTrophy(level, tier, rarity)
                randomTrophies.add(randomTrophy)
                awardedTrophies.add(randomTrophy.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
            }
        }
        
        return TrophyReward(
            awardedTrophies,
            trophyCount,
            eligibleTrophies.size
        )
    }
    
    /**
     * Generate a random trophy with no prerequisites
     */
    private fun generateRandomTrophy(level: Int, tier: Tier, rarity: TrophyRarity): Trophy {
        val prefixes = listOf("Mighty", "Swift", "Legendary", "Brave", "Noble", "Fearless", "Honorable")
        val nouns = listOf("Beginner", "Adventurer", "Warrior", "Champion", "Hero", "Legend", "Master")
        
        val name = "${prefixes.random()} ${nouns.random()}"
        val id = "trophy_${System.currentTimeMillis()}_${Random.nextInt(1000)}"
        
        return Trophy(
            id = id,
            name = name,
            description = "Awarded for reaching level $level in the ${tier.displayName} tier",
            tier = tier,
            requiredLevel = level,
            rarity = rarity
        )
    }
    
    /**
     * Calculate total XP from awarded trophies
     */
    fun calculateTotalXP(trophies: List<Trophy>): Int {
        return trophies.sumOf { it.getXPValue() }
    }
    
    /**
     * Calculate total point bonus from awarded trophies
     */
    fun calculateTotalPointBonus(trophies: List<Trophy>): Int {
        return trophies.sumOf { it.getPointBonus() }
    }
}

/**
 * Trophy range for a tier
 */
data class TrophyRange(val min: Int, val max: Int)

/**
 * Result of trophy awarding
 */
data class TrophyReward(
    val trophies: List<Trophy>,
    val totalAwarded: Int,
    val eligibleCount: Int
) {
    val totalXP: Int get() = trophies.sumOf { it.getXPValue() }
    val totalPoints: Int get() = trophies.sumOf { it.getPointBonus() }
}