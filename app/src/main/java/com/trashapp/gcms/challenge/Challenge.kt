package com.trashapp.gcms.challenge

import com.trashapp.gcms.trophy.TrophyRarity

/**
 * Represents a challenge that must be completed to unlock the next level
 * 
 * Challenges act as achievements that gate level progression.
 * Players must complete all challenges for a level before they can advance,
 * even if they have enough XP, points, or abilities/skills.
 */
data class Challenge(
    val id: String,
    val level: Int,
    val name: String,
    val description: String,
    val type: ChallengeType,
    val requirements: ChallengeRequirements,
    val reward: ChallengeReward,
    val rarity: TrophyRarity,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val progress: Int = 0,
    val maxProgress: Int
) {
    /**
     * Check if challenge can be completed with current progress
     */
    fun canComplete(): Boolean {
        return progress >= maxProgress && !isCompleted
    }
    
    /**
     * Get progress percentage (0-100)
     */
    fun getProgressPercentage(): Float {
        return if (maxProgress > 0) {
            (progress.toFloat() / maxProgress) * 100f
        } else {
            0f
        }
    }
    
    /**
     * Update challenge progress
     */
    fun updateProgress(amount: Int): Challenge {
        val newProgress = (progress + amount).coerceAtMost(maxProgress)
        return this.copy(progress = newProgress)
    }
    
    /**
     * Complete the challenge
     */
    fun complete(): Challenge {
        return this.copy(
            isCompleted = true,
            completedAt = System.currentTimeMillis(),
            progress = maxProgress
        )
    }
}

/**
 * Challenge types with different requirement structures
 */
enum class ChallengeType(val displayName: String) {
    SCORE("Score Challenge"),
    ABILITY_USE("Ability Challenge"),
    SKILL_UNLOCK("Skill Challenge"),
    POINT_ACCUMULATION("Point Challenge"),
    COMBO("Combo Challenge"),
    WIN_STREAK("Win Streak Challenge"),
    CARD_PLAYED("Card Played Challenge"),
    ROUND_WIN("Round Win Challenge"),
    MATCH_WIN("Match Win Challenge"),
    TIME_LIMIT("Time Challenge")
}

/**
 * Challenge requirements
 */
data class ChallengeRequirements(
    val score: Int = 0,
    val abilitiesUsed: Map<String, Int> = emptyMap(), // Ability ID -> count
    val skillsUnlocked: List<String> = emptyList(),
    val pointsEarned: Int = 0,
    val comboRequired: ComboRequirement? = null,
    val winStreak: Int = 0,
    val cardsPlayed: Int = 0,
    val roundsWon: Int = 0,
    val matchesWon: Int = 0,
    val timeLimit: Int = 0, // in seconds
    val customRequirements: Map<String, Any> = emptyMap()
) {
    /**
     * Check if requirements are met with current progress
     */
    fun isMet(progress: Int, maxProgress: Int): Boolean {
        return progress >= maxProgress
    }
}

/**
 * Combo requirement for combo challenges
 */
data class ComboRequirement(
    val abilityCombos: List<List<String>> = emptyList(), // Lists of ability IDs to use together
    val skillAbilityCombos: Map<String, List<String>> = emptyMap(), // Skill ID -> Ability IDs
    val pointAbilityCombos: Map<Int, List<String>> = emptyMap(), // Points -> Ability IDs
    val description: String = ""
)

/**
 * Challenge rewards
 */
data class ChallengeReward(
    val achievement: String,
    val achievementDescription: String,
    val xp: Int = 0,
    val points: Int = 0,
    val unlocksLevel: Int = 0,
    val bonusRewards: Map<String, Int> = emptyMap() // e.g., {"coins": 100, "gems": 50}
) {
    /**
     * Get total XP reward
     */
    fun getTotalXP(): Int {
        return xp + bonusRewards.getOrDefault("xp", 0)
    }
    
    /**
     * Get total point reward
     */
    fun getTotalPoints(): Int {
        return points + bonusRewards.getOrDefault("points", 0)
    }
}