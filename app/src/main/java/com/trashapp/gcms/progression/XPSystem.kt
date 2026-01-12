package com.trashapp.gcms.progression

/**
 * Dynamic XP System
 * XP requirements scale non-linearly with level
 * XP is only gained through purchasing abilities/skills
 * Losing abilities/skills can reduce XP and potentially levels
 */
class XPSystem {
    private val penaltyMultiplier = 1.1f // 10% penalty to regain lost XP

    /**
     * Calculate XP required to reach the next level
     * Formula: BaseXP * (Level^1.1) + Random(-5% to +5%)
     */
    fun calculateXPForNextLevel(currentLevel: Int): Int {
        val baseXP = getBaseXPForLevel(currentLevel)
        val randomVariance = (Math.random() - 0.5) * 0.1 // ±5% variance
        val multiplier = Math.pow(currentLevel.toDouble(), 1.1) * (1 + randomVariance)
        return (baseXP * multiplier).toInt().coerceAtLeast(50)
    }

    /**
     * Calculate total XP required to reach a specific level from level 1
     */
    fun calculateTotalXPForLevel(targetLevel: Int): Int {
        var totalXP = 0
        for (level in 1 until targetLevel) {
            totalXP += calculateXPForNextLevel(level)
        }
        return totalXP
    }

    /**
     * Get base XP for a level tier
     * Early levels: 50-200 XP
     * Mid-levels: 300-1000 XP
     * Late levels: 1200-5000+ XP
     */
    private fun getBaseXPForLevel(level: Int): Int {
        val tier = Tier.getTierForLevel(level)
        return when (tier) {
            Tier.LIFE -> 50
            Tier.BEGINNER -> 100
            Tier.NOVICE -> 300
            Tier.HARD -> 600
            Tier.EXPERT -> 1200
            Tier.MASTER -> 2500
        }
    }

    /**
     * Calculate current level based on total XP
     */
    fun calculateLevelFromXP(totalXP: Int): Int {
        if (totalXP <= 0) return 1
        
        var level = 1
        var accumulatedXP = 0
        
        while (level < 200) {
            val xpNeeded = calculateXPForNextLevel(level)
            if (accumulatedXP + xpNeeded > totalXP) break
            accumulatedXP += xpNeeded
            level++
        }
        
        return level.coerceIn(1, 200)
    }

    /**
     * Calculate progress to next level (0.0 to 1.0)
     */
    fun calculateProgressToNextLevel(currentXP: Int, currentLevel: Int): Float {
        val totalXPForCurrentLevel = calculateTotalXPForLevel(currentLevel)
        val xpNeededForNext = calculateXPForNextLevel(currentLevel)
        val currentProgress = currentXP - totalXPForCurrentLevel
        
        return (currentProgress.toFloat() / xpNeededForNext).coerceIn(0f, 1f)
    }

    /**
     * Calculate XP to regain after losing abilities/skills
     * Includes 10% penalty
     */
    fun calculateXPToRegain(lostXP: Int): Int {
        return (lostXP * penaltyMultiplier).toInt()
    }

    /**
     * Calculate XP loss when refunding abilities/skills
     */
    fun calculateXPLoss(originalXPGained: Int): Int {
        return originalXPGained
    }

    /**
     * Generate pre-computed XP table for all levels (1-200)
     */
    fun generateXPTable(): List<XPTableEntry> {
        val table = mutableListOf<XPTableEntry>()
        var accumulatedXP = 0
        
        for (level in 1..200) {
            val xpToNext = if (level < 200) calculateXPForNextLevel(level) else 0
            table.add(XPTableEntry(level, accumulatedXP, xpToNext, Tier.getTierForLevel(level)))
            accumulatedXP += xpToNext
        }
        
        return table
    }
}

/**
 * Data class representing a single level in the XP table
 */
data class XPTableEntry(
    val level: Int,
    val accumulatedXP: Int,
    val xpToNextLevel: Int,
    val tier: Tier
)