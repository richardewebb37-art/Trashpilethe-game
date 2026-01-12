package com.trashapp.gcms.progression

import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.progression.XPSystem

/**
 * Point System for progression
 * Manages points, XP, and level progression with dynamic mechanics
 * Integrated with tiered progression system (levels 1-200)
 * 
 * Key Features:
 * - Points earned from score and in-game actions
 * - XP only accumulates after purchasing abilities/skills
 * - Levels determined entirely by current XP (1-200)
 * - Dynamic XP scaling per level
 * - XP can increase or decrease, causing level fluctuation
 * - Penalty multiplier for regaining lost levels
 * - Tier-based progression
 */
data class PointSystem(
    val availablePoints: Int = 0,           // Points available to spend
    val totalXP: Int = 0,                   // Current total XP
    val currentLevel: Int = 1,              // Current level based on XP (1-200)
    val maxUnlockedLevel: Int = 1,          // Highest level ever reached
    val penaltyMultiplier: Float = 1.1f,    // XP penalty multiplier (10%)
    val isProgressionActive: Boolean = false // Becomes true after first purchase
) {
    private val xpSystem = XPSystem()

    /**
     * Get current tier based on level
     */
    val currentTier: Tier
        get() = Tier.getTierForLevel(currentLevel)

    /**
     * Calculate level from total XP
     * Uses dynamic XP calculation with non-linear scaling
     */
    fun calculateLevel(xp: Int): Int {
        return xpSystem.calculateLevelFromXP(xp)
    }
    
    /**
     * Calculate XP required for a specific level (1-200)
     * Uses cumulative XP calculation
     */
    fun calculateXPForLevel(level: Int): Int {
        return xpSystem.calculateTotalXPForLevel(level)
    }
    
    /**
     * Calculate XP needed to reach next level
     */
    fun xpToNextLevel(): Int {
        return xpSystem.calculateXPForNextLevel(currentLevel)
    }
    
    /**
     * Calculate progress to next level (0.0 to 1.0)
     */
    fun progressToNextLevel(): Float {
        return xpSystem.calculateProgressToNextLevel(totalXP, currentLevel)
    }
    
    /**
     * Add points (from score or actions)
     * Points can always be earned, regardless of progression state
     */
    fun addPoints(amount: Int): PointSystem {
        return copy(availablePoints = availablePoints + amount)
    }
    
    /**
     * Spend points to purchase ability/skill
     * This activates progression and grants XP
     */
    fun spendPoints(amount: Int, xpGranted: Int): PointSystem {
        if (availablePoints < amount) {
            throw IllegalStateException("Not enough points")
        }
        
        val newPoints = availablePoints - amount
        val newTotalXP = totalXP + xpGranted
        val newLevel = calculateLevel(newTotalXP)
        val newMaxLevel = maxOf(maxUnlockedLevel, newLevel)
        
        return copy(
            availablePoints = newPoints,
            totalXP = newTotalXP,
            currentLevel = newLevel,
            maxUnlockedLevel = newMaxLevel,
            isProgressionActive = true
        )
    }
    
    /**
     * Add XP directly (e.g., from achievements, bonuses)
     * Can be positive or negative
     */
    fun addXP(amount: Int): PointSystem {
        if (!isProgressionActive) {
            // XP doesn't accumulate until progression is active
            return this
        }
        
        val newTotalXP = (totalXP + amount).coerceAtLeast(0)
        val newLevel = calculateLevel(newTotalXP)
        val newMaxLevel = maxOf(maxUnlockedLevel, newLevel)
        
        return copy(
            totalXP = newTotalXP,
            currentLevel = newLevel,
            maxUnlockedLevel = newMaxLevel
        )
    }
    
    /**
     * Lose XP with penalty
     * Used when losing abilities/skills or receiving penalties
     * Returns the XP needed to regain the lost level
     */
    fun loseXP(amount: Int, wasLevelLost: Boolean): Pair<PointSystem, Int> {
        if (!isProgressionActive) {
            return Pair(this, 0)
        }
        
        val newTotalXP = (totalXP - amount).coerceAtLeast(0)
        val newLevel = calculateLevel(newTotalXP)
        val levelLost = newLevel < currentLevel
        
        val newSystem = copy(
            totalXP = newTotalXP,
            currentLevel = newLevel
        )
        
        // Calculate XP needed to regain lost level with penalty
        val xpToRegain = if (levelLost) {
            xpSystem.calculateXPToRegain(amount)
        } else {
            0
        }
        
        return Pair(newSystem, xpToRegain)
    }
    
    /**
     * Refund points (e.g., when selling ability/skill)
     * Also removes XP and may lower level
     */
    fun refundPoints(amount: Int, xpToRemove: Int): Pair<PointSystem, Int> {
        val newPoints = availablePoints + amount
        val newTotalXP = (totalXP - xpToRemove).coerceAtLeast(0)
        val newLevel = calculateLevel(newTotalXP)
        
        val newSystem = copy(
            availablePoints = newPoints,
            totalXP = newTotalXP,
            currentLevel = newLevel
        )
        
        // Calculate XP needed to regain lost level with penalty
        val xpToRegain = if (newLevel < currentLevel) {
            xpSystem.calculateXPToRegain(xpToRemove)
        } else {
            0
        }
        
        return Pair(newSystem, xpToRegain)
    }
    
    /**
     * Check if player can afford ability/skill
     */
    fun canAfford(cost: Int): Boolean {
        return availablePoints >= cost
    }
    
    /**
     * Get dynamic level ceiling based on available abilities/skills
     * This prevents hard level caps
     */
    fun calculateDynamicLevelCeiling(totalAvailableXP: Int): Int {
        return calculateLevel(totalAvailableXP)
    }

    /**
     * Calculate ability cost based on tier
     */
    fun calculateAbilityCost(tier: Tier): Int {
        return when (tier) {
            Tier.LIFE -> (10..20).random()
            Tier.BEGINNER -> (15..30).random()
            Tier.NOVICE -> (20..40).random()
            Tier.HARD -> (25..50).random()
            Tier.EXPERT -> (30..75).random()
            Tier.MASTER -> (40..100).random()
        }
    }

    /**
     * Calculate skill cost based on tier
     */
    fun calculateSkillCost(tier: Tier): Int {
        return when (tier) {
            Tier.LIFE -> (10..20).random()
            Tier.BEGINNER -> (15..30).random()
            Tier.NOVICE -> (20..40).random()
            Tier.HARD -> (25..50).random()
            Tier.EXPERT -> (30..75).random()
            Tier.MASTER -> (40..100).random()
        }
    }
    
    companion object {
        /**
         * Create initial point system
         */
        fun createInitial(): PointSystem {
            return PointSystem(
                availablePoints = 100,  // Starting points
                totalXP = 0,
                currentLevel = 1,
                maxUnlockedLevel = 1,
                penaltyMultiplier = 1.1f,
                isProgressionActive = false
            )
        }
    }
}