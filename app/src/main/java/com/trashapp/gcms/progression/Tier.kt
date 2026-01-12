package com.trashapp.gcms.progression

/**
 * Tier System for Progression
 * Groups levels into thematic tiers with different difficulty scaling
 * Updated to 7-tier system for complete 200-level progression
 */
enum class Tier(
    val displayName: String,
    val levelRange: IntRange,
    val description: String
) {
    NEWBIE("Newbie", 1..20, "Introductory abilities/skills; foundational mechanics"),
    BEGINNER("Beginner", 21..50, "Basic abilities; start unlocking XP-driven progression"),
    NOVICE("Novice", 51..80, "Intermediate abilities/skills; some combinations required"),
    INTERMEDIATE("Intermediate", 81..110, "Stronger abilities; skill synergy required"),
    HARD("Hard", 111..140, "Advanced abilities/skills; significant XP required"),
    EXPERT("Expert", 141..170, "High-level abilities; challenging unlocks"),
    MASTER("Master", 171..200, "Ultimate abilities; god-tier power");

    companion object {
        fun getTierForLevel(level: Int): Tier {
            return values().first { level in it.levelRange }
        }
        
        /**
         * Get the base XP multiplier for this tier
         * Higher tiers require exponentially more XP
         */
        fun getBaseXPForTier(tier: Tier): Int {
            return when (tier) {
                NEWBIE -> 50
                BEGINNER -> 100
                NOVICE -> 300
                INTERMEDIATE -> 600
                HARD -> 1200
                EXPERT -> 2500
                MASTER -> 5000
            }
        }
        
        /**
         * Get cost multiplier for this tier
         * Higher tier skills/abilities cost more
         */
        fun getCostMultiplier(tier: Tier): Double {
            return when (tier) {
                NEWBIE -> 1.0
                BEGINNER -> 1.5
                NOVICE -> 2.0
                INTERMEDIATE -> 3.0
                HARD -> 4.0
                EXPERT -> 6.0
                MASTER -> 10.0
            }
        }
    }
}