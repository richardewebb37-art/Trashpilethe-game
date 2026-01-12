package com.trashapp.gcms.challenge

import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.trophy.TrophyRarity
import kotlin.random.Random

/**
 * Challenge System - Manages challenge generation and progression gating
 * 
 * This system:
 * - Generates challenges for each level
 * - Checks challenge completion status
 * - Gates level progression based on challenge completion
 * - Handles dynamic difficulty scaling
 */
class ChallengeSystem {
    
    companion object {
        // Challenge count ranges per tier
        private val TIER_CHALLENGE_COUNT = mapOf(
            Tier.LIFE to ChallengeCountRange(1, 2),
            Tier.BEGINNER to ChallengeCountRange(2, 3),
            Tier.NOVICE to ChallengeCountRange(3, 4),
            Tier.HARD to ChallengeCountRange(4, 5),
            Tier.EXPERT to ChallengeCountRange(5, 6),
            Tier.MASTER to ChallengeCountRange(6, 8)
        )
        
        // Difficulty multiplier per tier
        private val TIER_DIFFICULTY_MULTIPLIER = mapOf(
            Tier.LIFE to 1.0,
            Tier.BEGINNER to 1.2,
            Tier.NOVICE to 1.5,
            Tier.HARD to 1.8,
            Tier.EXPERT to 2.2,
            Tier.MASTER to 2.5
        )
        
        // Challenge type distribution per tier
        private val TIER_CHALLENGE_TYPES = mapOf(
            Tier.LIFE to listOf(
                ChallengeType.SCORE,
                ChallengeType.POINT_ACCUMULATION
            ),
            Tier.BEGINNER to listOf(
                ChallengeType.SCORE,
                ChallengeType.POINT_ACCUMULATION,
                ChallengeType.ABILITY_USE,
                ChallengeType.CARD_PLAYED
            ),
            Tier.NOVICE to listOf(
                ChallengeType.SCORE,
                ChallengeType.ABILITY_USE,
                ChallengeType.SKILL_UNLOCK,
                ChallengeType.ROUND_WIN,
                ChallengeType.COMBO
            ),
            Tier.HARD to listOf(
                ChallengeType.SCORE,
                ChallengeType.ABILITY_USE,
                ChallengeType.SKILL_UNLOCK,
                ChallengeType.WIN_STREAK,
                ChallengeType.COMBO,
                ChallengeType.MATCH_WIN
            ),
            Tier.EXPERT to listOf(
                ChallengeType.SCORE,
                ChallengeType.ABILITY_USE,
                ChallengeType.WIN_STREAK,
                ChallengeType.COMBO,
                ChallengeType.MATCH_WIN,
                ChallengeType.TIME_LIMIT
            ),
            Tier.MASTER to listOf(
                ChallengeType.SCORE,
                ChallengeType.ABILITY_USE,
                ChallengeType.WIN_STREAK,
                ChallengeType.COMBO,
                ChallengeType.MATCH_WIN,
                ChallengeType.TIME_LIMIT,
                ChallengeType.SCORE // Score challenges are harder at master level
            )
        )
        
        // Base challenge values by level
        private val BASE_SCORE_TARGET = mapOf(
            1 to 100, 2 to 150, 3 to 200, 4 to 250, 5 to 300,
            10 to 500, 20 to 1000, 30 to 1500, 50 to 2500,
            75 to 4000, 100 to 6000, 150 to 10000, 200 to 15000
        )
    }
    
    /**
     * Calculate number of challenges for a level
     */
    fun calculateChallengeCount(level: Int, tier: Tier): Int {
        val range = TIER_CHALLENGE_COUNT[tier] ?: ChallengeCountRange(1, 2)
        
        // Add slight variation based on level within tier
        val levelBonus = when (tier) {
            Tier.LIFE -> 0
            Tier.BEGINNER -> (level - 5) / 10
            Tier.NOVICE -> (level - 20) / 15
            Tier.HARD -> (level - 50) / 15
            Tier.EXPERT -> (level - 80) / 20
            Tier.MASTER -> (level - 140) / 20
        }
        
        val baseCount = Random.nextInt(range.min, range.max + 1)
        return (baseCount + levelBonus).coerceIn(range.min, range.max + 2)
    }
    
    /**
     * Determine challenge types for a level
     */
    fun determineChallengeTypes(level: Int, tier: Tier, count: Int): List<ChallengeType> {
        val availableTypes = TIER_CHALLENGE_TYPES[tier] ?: listOf(ChallengeType.SCORE)
        val selectedTypes = mutableListOf<ChallengeType>()
        
        // Always include at least one score challenge
        if (ChallengeType.SCORE in availableTypes && !selectedTypes.contains(ChallengeType.SCORE)) {
            selectedTypes.add(ChallengeType.SCORE)
        }
        
        // Randomly select remaining types
        val remainingTypes = availableTypes.filter { it !in selectedTypes }.shuffled()
        while (selectedTypes.size < count && remainingTypes.isNotEmpty()) {
            selectedTypes.add(remainingTypes.removeAt(0))
        }
        
        return selectedTypes
    }
    
    /**
     * Calculate challenge difficulty multiplier
     */
    fun getDifficultyMultiplier(tier: Tier): Double {
        return TIER_DIFFICULTY_MULTIPLIER[tier] ?: 1.0
    }
    
    /**
     * Get base score target for a level
     */
    fun getBaseScoreTarget(level: Int): Int {
        // Find closest level in base values
        val sortedLevels = BASE_SCORE_TARGET.keys.sorted()
        var target = 100
        
        for (lvl in sortedLevels) {
            if (level >= lvl) {
                target = BASE_SCORE_TARGET[lvl] ?: 100
            } else {
                break
            }
        }
        
        // Interpolate between levels
        val nextLevel = sortedLevels.firstOrNull { it > level }
        if (nextLevel != null && level < nextLevel) {
            val prevLevel = sortedLevels.lastOrNull { it <= level } ?: 1
            val prevTarget = BASE_SCORE_TARGET[prevLevel] ?: 100
            val nextTarget = BASE_SCORE_TARGET[nextLevel] ?: 100
            
            val ratio = (level - prevLevel).toFloat() / (nextLevel - prevLevel)
            target = (prevTarget + (nextTarget - prevTarget) * ratio).toInt()
        }
        
        return target
    }
    
    /**
     * Calculate challenge requirements based on type and level
     */
    fun calculateRequirements(
        type: ChallengeType,
        level: Int,
        tier: Tier,
        difficulty: Double
    ): ChallengeRequirements {
        val baseScore = getBaseScoreTarget(level)
        val multiplier = getDifficultyMultiplier(tier) * difficulty
        
        return when (type) {
            ChallengeType.SCORE -> ChallengeRequirements(
                score = (baseScore * multiplier).toInt()
            )
            ChallengeType.ABILITY_USE -> {
                val abilityIds = listOf("Quick Draw", "Sheriff's Badge", "Lucky Horseshoe")
                val abilitiesMap = abilityIds.associateWith { 
                    Random.nextInt(3, 8) 
                }
                ChallengeRequirements(
                    abilitiesUsed = abilitiesMap
                )
            }
            ChallengeType.SKILL_UNLOCK -> {
                val skillIds = listOf("Card Shark", "Iron Will", "Strategic Mind")
                ChallengeRequirements(
                    skillsUnlocked = skillIds.shuffled().take(Random.nextInt(1, 3))
                )
            }
            ChallengeType.POINT_ACCUMULATION -> ChallengeRequirements(
                pointsEarned = (baseScore * multiplier * 0.5).toInt()
            )
            ChallengeType.COMBO -> ChallengeRequirements(
                comboRequired = ComboRequirement(
                    abilityCombos = listOf(listOf("Quick Draw", "Sheriff's Badge")),
                    description = "Use Quick Draw and Sheriff's Badge in sequence"
                )
            )
            ChallengeType.WIN_STREAK -> ChallengeRequirements(
                winStreak = (2 + level / 20).coerceAtMost(10)
            )
            ChallengeType.CARD_PLAYED -> ChallengeRequirements(
                cardsPlayed = (10 + level * 2).coerceAtMost(100)
            )
            ChallengeType.ROUND_WIN -> ChallengeRequirements(
                roundsWon = (3 + level / 10).coerceAtMost(20)
            )
            ChallengeType.MATCH_WIN -> ChallengeRequirements(
                matchesWon = (1 + level / 25).coerceAtMost(5)
            )
            ChallengeType.TIME_LIMIT -> ChallengeRequirements(
                score = (baseScore * multiplier * 0.8).toInt(),
                timeLimit = (120 - level / 2).coerceAtLeast(30) // seconds
            )
        }
    }
    
    /**
     * Calculate challenge rewards
     */
    fun calculateRewards(level: Int, tier: Tier, rarity: TrophyRarity): ChallengeReward {
        val baseXP = when (tier) {
            Tier.LIFE -> 50
            Tier.BEGINNER -> 100
            Tier.NOVICE -> 200
            Tier.HARD -> 400
            Tier.EXPERT -> 800
            Tier.MASTER -> 1500
        }
        
        val basePoints = when (tier) {
            Tier.LIFE -> 10
            Tier.BEGINNER -> 25
            Tier.NOVICE -> 50
            Tier.HARD -> 100
            Tier.EXPERT -> 200
            Tier.MASTER -> 400
        }
        
        val rarityMultiplier = when (rarity) {
            TrophyRarity.COMMON -> 1.0
            TrophyRarity.UNCOMMON -> 1.5
            TrophyRarity.RARE -> 2.0
            TrophyRarity.EPIC -> 3.0
            TrophyRarity.LEGENDARY -> 5.0
            TrophyRarity.MYTHIC -> 10.0
        }
        
        return ChallengeReward(
            achievement = "Challenge Master Level $level",
            achievementDescription = "Complete all challenges for level $level",
            xp = (baseXP * rarityMultiplier).toInt(),
            points = (basePoints * rarityMultiplier).toInt(),
            unlocksLevel = level + 1,
            bonusRewards = mapOf(
                "coins" to (level * 10 * rarityMultiplier).toInt()
            )
        )
    }
    
    /**
     * Determine challenge rarity based on level and tier
     */
    fun determineRarity(level: Int, tier: Tier): TrophyRarity {
        val random = Random.nextFloat() * 100f
        
        return when (tier) {
            Tier.LIFE -> {
                when {
                    random < 80f -> TrophyRarity.COMMON
                    else -> TrophyRarity.UNCOMMON
                }
            }
            Tier.BEGINNER -> {
                when {
                    random < 60f -> TrophyRarity.COMMON
                    random < 85f -> TrophyRarity.UNCOMMON
                    else -> TrophyRarity.RARE
                }
            }
            Tier.NOVICE -> {
                when {
                    random < 40f -> TrophyRarity.COMMON
                    random < 70f -> TrophyRarity.UNCOMMON
                    random < 90f -> TrophyRarity.RARE
                    else -> TrophyRarity.EPIC
                }
            }
            Tier.HARD -> {
                when {
                    random < 30f -> TrophyRarity.COMMON
                    random < 55f -> TrophyRarity.UNCOMMON
                    random < 80f -> TrophyRarity.RARE
                    random < 95f -> TrophyRarity.EPIC
                    else -> TrophyRarity.LEGENDARY
                }
            }
            Tier.EXPERT -> {
                when {
                    random < 15f -> TrophyRarity.UNCOMMON
                    random < 40f -> TrophyRarity.RARE
                    random < 70f -> TrophyRarity.EPIC
                    random < 90f -> TrophyRarity.LEGENDARY
                    else -> TrophyRarity.MYTHIC
                }
            }
            Tier.MASTER -> {
                when {
                    random < 20f -> TrophyRarity.RARE
                    random < 50f -> TrophyRarity.EPIC
                    random < 75f -> TrophyRarity.LEGENDARY
                    else -> TrophyRarity.MYTHIC
                }
            }
        }
    }
    
    /**
     * Check if all challenges for a level are completed
     */
    fun areAllChallengesCompleted(challenges: List<Challenge>): Boolean {
        return challenges.all { it.isCompleted }
    }
    
    /**
     * Check if player can advance to next level
     */
    fun canAdvanceToNextLevel(
        currentLevel: Int,
        challenges: List<Challenge>,
        hasRequiredXP: Boolean,
        hasRequiredPoints: Boolean,
        hasRequiredAbilities: Boolean,
        hasRequiredSkills: Boolean
    ): Boolean {
        // All conditions must be met
        return hasRequiredXP &&
               hasRequiredPoints &&
               hasRequiredAbilities &&
               hasRequiredSkills &&
               areAllChallengesCompleted(challenges)
    }
    
    /**
     * Get challenge progress summary
     */
    fun getProgressSummary(challenges: List<Challenge>): ChallengeProgress {
        val total = challenges.size
        val completed = challenges.count { it.isCompleted }
        val inProgress = challenges.count { !it.isCompleted && it.progress > 0 }
        val notStarted = challenges.count { !it.isCompleted && it.progress == 0 }
        
        val totalXP = challenges.filter { it.isCompleted }.sumOf { it.reward.getTotalXP() }
        val totalPoints = challenges.filter { it.isCompleted }.sumOf { it.reward.getTotalPoints() }
        
        return ChallengeProgress(
            totalChallenges = total,
            completedChallenges = completed,
            inProgressChallenges = inProgress,
            notStartedChallenges = notStarted,
            completionPercentage = if (total > 0) (completed.toFloat() / total) * 100f else 0f,
            totalXPEarned = totalXP,
            totalPointsEarned = totalPoints
        )
    }
}

/**
 * Challenge count range for a tier
 */
data class ChallengeCountRange(val min: Int, val max: Int)

/**
 * Challenge progress summary
 */
data class ChallengeProgress(
    val totalChallenges: Int,
    val completedChallenges: Int,
    val inProgressChallenges: Int,
    val notStartedChallenges: Int,
    val completionPercentage: Float,
    val totalXPEarned: Int,
    val totalPointsEarned: Int
) {
    val isComplete: Boolean get() = totalChallenges > 0 && completedChallenges == totalChallenges
}