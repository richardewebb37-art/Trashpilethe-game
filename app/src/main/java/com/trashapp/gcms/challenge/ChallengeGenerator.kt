package com.trashapp.gcms.challenge

import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.trophy.TrophyRarity

/**
 * Generates challenge definitions for all levels 1-200
 * 
 * This creates a comprehensive challenge database with:
 * - Dynamic challenge types per level
 * - Tier-based difficulty scaling
 * - Special milestone challenges
 * - Randomized challenge requirements
 */
class ChallengeGenerator {
    
    companion object {
        // Special milestone levels with unique challenges
        private val MILESTONE_LEVELS = listOf(5, 10, 20, 30, 50, 75, 100, 125, 150, 175, 200)
        
        // Challenge type themes by tier
        private val TIER_THEMES = mapOf(
            Tier.LIFE to listOf("Basic", "Starter", "First Steps"),
            Tier.BEGINNER to listOf("Explorer", "Adventurer", "Initiate"),
            Tier.NOVICE to listOf("Warrior", "Strategist", "Tactician"),
            Tier.HARD to listOf("Elite", "Veteran", "Expert"),
            Tier.EXPERT to listOf("Master", "Legend", "Champion"),
            Tier.MASTER to listOf("Ultimate", "Divine", "Ascended")
        )
        
        // Predefined milestone challenges
        private val MILESTONE_CHALLENGES = mapOf(
            5 to listOf(
                MilestoneChallengeTemplate("First Steps", ChallengeType.SCORE, 300),
                MilestoneChallengeTemplate("Card Beginner", ChallengeType.CARD_PLAYED, 20)
            ),
            10 to listOf(
                MilestoneChallengeTemplate("Double Digits", ChallengeType.SCORE, 500),
                MilestoneChallengeTemplate("Round Winner", ChallengeType.ROUND_WIN, 5),
                MilestoneChallengeTemplate("Ability User", ChallengeType.ABILITY_USE, 5)
            ),
            20 to listOf(
                MilestoneChallengeTemplate("Novice Achievement", ChallengeType.SCORE, 1000),
                MatchWinnerTemplate("Match Winner", 1),
                MilestoneChallengeTemplate("Point Collector", ChallengeType.POINT_ACCUMULATION, 500)
            ),
            30 to listOf(
                MilestoneChallengeTemplate("Veteran Player", ChallengeType.SCORE, 1500),
                StreakTemplate("Win Streak", 3),
                MilestoneChallengeTemplate("Card Master", ChallengeType.CARD_PLAYED, 100)
            ),
            50 to listOf(
                MilestoneChallengeTemplate("Half Century", ChallengeType.SCORE, 2500),
                MatchWinnerTemplate("Multiple Wins", 3),
                ComboTemplate("Combo Master", listOf(listOf("Quick Draw", "Sheriff's Badge")))
            ),
            75 to listOf(
                MilestoneChallengeTemplate("Dedicated Gamer", ChallengeType.SCORE, 4000),
                StreakTemplate("Hot Streak", 5),
                TimeLimitTemplate("Speed Run", 3000, 60)
            ),
            100 to listOf(
                MilestoneChallengeTemplate("Century Club", ChallengeType.SCORE, 6000),
                MatchWinnerTemplate("Century Winner", 5),
                MilestoneChallengeTemplate("Ability Legend", ChallengeType.ABILITY_USE, 20)
            ),
            125 to listOf(
                MilestoneChallengeTemplate("Elite Ascendant", ChallengeType.SCORE, 8000),
                ComboTemplate("Elite Combo", listOf(
                    listOf("Quick Draw", "Sheriff's Badge", "Lucky Horseshoe")
                )),
                StreakTemplate("Elite Streak", 7)
            ),
            150 to listOf(
                MilestoneChallengeTemplate("Expert Legend", ChallengeType.SCORE, 10000),
                MatchWinnerTemplate("Expert Champion", 8),
                TimeLimitTemplate("Expert Speed", 5000, 45)
            ),
            175 to listOf(
                MilestoneChallengeTemplate("Master Craftsman", ChallengeType.SCORE, 12000),
                ComboTemplate("Master Combo", listOf(
                    listOf("Quick Draw", "Sheriff's Badge"),
                    listOf("Gold Rush", "Wild West Legend")
                )),
                StreakTemplate("Master Streak", 10)
            ),
            200 to listOf(
                MilestoneChallengeTemplate("Ultimate Champion", ChallengeType.SCORE, 15000),
                MatchWinnerTemplate("Ultimate Winner", 10),
                ComboTemplate("Ultimate Combo", listOf(
                    listOf("Quick Draw", "Sheriff's Badge", "Lucky Horseshoe"),
                    listOf("Gold Rush", "Wild West Legend"),
                    listOf("Quick Draw", "Gold Rush", "Wild West Legend")
                )),
                TimeLimitTemplate("Ultimate Speed", 8000, 30)
            )
        )
    }
    
    /**
     * Generate all challenges for levels 1-200
     */
    fun generateAllChallenges(): Map<Int, List<Challenge>> {
        val allChallenges = mutableMapOf<Int, List<Challenge>>()
        
        for (level in 1..200) {
            val challenges = generateChallengesForLevel(level)
            allChallenges[level] = challenges
        }
        
        return allChallenges
    }
    
    /**
     * Generate challenges for a specific level
     */
    fun generateChallengesForLevel(level: Int): List<Challenge> {
        val tier = Tier.getTierForLevel(level)
        
        // Check if this is a milestone level
        if (level in MILESTONE_LEVELS) {
            return generateMilestoneChallenges(level, tier)
        }
        
        // Generate regular challenges
        return generateRegularChallenges(level, tier)
    }
    
    /**
     * Generate milestone challenges
     */
    private fun generateMilestoneChallenges(level: Int, tier: Tier): List<Challenge> {
        val templates = MILESTONE_CHALLENGES[level] ?: return generateRegularChallenges(level, tier)
        
        return templates.mapIndexed { index, template ->
            createChallengeFromTemplate(level, tier, template, index)
        }
    }
    
    /**
     * Generate regular challenges
     */
    private fun generateRegularChallenges(level: Int, tier: Tier): List<Challenge> {
        val challengeSystem = ChallengeSystem()
        val challengeCount = challengeSystem.calculateChallengeCount(level, tier)
        val challengeTypes = challengeSystem.determineChallengeTypes(level, tier, challengeCount)
        
        return challengeTypes.mapIndexed { index, type ->
            generateRegularChallenge(level, tier, type, index)
        }
    }
    
    /**
     * Generate a regular challenge
     */
    private fun generateRegularChallenge(
        level: Int,
        tier: Tier,
        type: ChallengeType,
        index: Int
    ): Challenge {
        val challengeSystem = ChallengeSystem()
        val difficulty = 1.0 + (index * 0.15) // Progressive difficulty
        val rarity = challengeSystem.determineRarity(level, tier)
        val requirements = challengeSystem.calculateRequirements(type, level, tier, difficulty)
        val reward = challengeSystem.calculateRewards(level, tier, rarity)
        
        val maxProgress = calculateMaxProgress(type, requirements)
        val id = "challenge_${level}_${type.name}_${System.currentTimeMillis()}_${index}"
        val name = generateRegularChallengeName(type, level, tier, index)
        val description = generateRegularChallengeDescription(type, requirements)
        
        return Challenge(
            id = id,
            level = level,
            name = name,
            description = description,
            type = type,
            requirements = requirements,
            reward = reward,
            rarity = rarity,
            isCompleted = false,
            progress = 0,
            maxProgress = maxProgress
        )
    }
    
    /**
     * Create challenge from milestone template
     */
    private fun createChallengeFromTemplate(
        level: Int,
        tier: Tier,
        template: MilestoneChallengeTemplate,
        index: Int
    ): Challenge {
        val challengeSystem = ChallengeSystem()
        val difficulty = 1.5 // Milestone challenges are harder
        val rarity = TrophyRarity.EPIC // Milestones are epic by default
        val requirements = challengeSystem.calculateRequirements(
            template.type,
            level,
            tier,
            difficulty
        )
        val reward = challengeSystem.calculateRewards(level, tier, rarity)
        
        val maxProgress = template.target
        val id = "milestone_challenge_${level}_${index}"
        val name = template.name
        val description = generateChallengeDescriptionFromTemplate(template)
        
        return Challenge(
            id = id,
            level = level,
            name = name,
            description = description,
            type = template.type,
            requirements = requirements,
            reward = reward,
            rarity = rarity,
            isCompleted = false,
            progress = 0,
            maxProgress = maxProgress
        )
    }
    
    /**
     * Calculate max progress for a challenge type
     */
    private fun calculateMaxProgress(type: ChallengeType, requirements: ChallengeRequirements): Int {
        return when (type) {
            ChallengeType.SCORE -> requirements.score
            ChallengeType.ABILITY_USE -> requirements.abilitiesUsed.values.sum()
            ChallengeType.SKILL_UNLOCK -> requirements.skillsUnlocked.size
            ChallengeType.POINT_ACCUMULATION -> requirements.pointsEarned
            ChallengeType.COMBO -> requirements.comboRequired?.abilityCombos?.size ?: 1
            ChallengeType.WIN_STREAK -> requirements.winStreak
            ChallengeType.CARD_PLAYED -> requirements.cardsPlayed
            ChallengeType.ROUND_WIN -> requirements.roundsWon
            ChallengeType.MATCH_WIN -> requirements.matchesWon
            ChallengeType.TIME_LIMIT -> requirements.score
        }
    }
    
    /**
     * Generate regular challenge name
     */
    private fun generateRegularChallengeName(
        type: ChallengeType,
        level: Int,
        tier: Tier,
        index: Int
    ): String {
        val themes = TIER_THEMES[tier] ?: listOf("Challenge")
        val theme = themes.random()
        val ordinal = listOf("First", "Second", "Third", "Fourth", "Fifth", "Sixth").getOrElse(index) { "${index + 1}th" }
        
        return when (type) {
            ChallengeType.SCORE -> "$ordinal Score Challenge"
            ChallengeType.ABILITY_USE -> "$ordinal Ability Challenge"
            ChallengeType.SKILL_UNLOCK -> "$ordinal Skill Challenge"
            ChallengeType.POINT_ACCUMULATION -> "$ordinal Point Challenge"
            ChallengeType.COMBO -> "$ordinal Combo Challenge"
            ChallengeType.WIN_STREAK -> "$ordinal Streak Challenge"
            ChallengeType.CARD_PLAYED -> "$ordinal Card Challenge"
            ChallengeType.ROUND_WIN -> "$ordinal Round Challenge"
            ChallengeType.MATCH_WIN -> "$ordinal Match Challenge"
            ChallengeType.TIME_LIMIT -> "$ordinal Speed Challenge"
        }
    }
    
    /**
     * Generate regular challenge description
     */
    private fun generateRegularChallengeDescription(
        type: ChallengeType,
        requirements: ChallengeRequirements
    ): String {
        return when (type) {
            ChallengeType.SCORE -> "Reach ${requirements.score} points in a single session"
            ChallengeType.ABILITY_USE -> "Use your abilities effectively ${requirements.abilitiesUsed.size} times"
            ChallengeType.SKILL_UNLOCK -> "Demonstrate mastery by unlocking ${requirements.skillsUnlocked.size} skills"
            ChallengeType.POINT_ACCUMULATION -> "Accumulate ${requirements.pointsEarned} points through gameplay"
            ChallengeType.COMBO -> "Execute skillful combos: ${requirements.comboRequired?.description ?: "Complete combo actions"}"
            ChallengeType.WIN_STREAK -> "Maintain a ${requirements.winStreak} win streak to prove your skill"
            ChallengeType.CARD_PLAYED -> "Play ${requirements.cardsPlayed} cards strategically"
            ChallengeType.ROUND_WIN -> "Win ${requirements.roundsWon} rounds against opponents"
            ChallengeType.MATCH_WIN -> "Win ${requirements.matchesWon} complete matches"
            ChallengeType.TIME_LIMIT -> "Achieve ${requirements.score} points within ${requirements.timeLimit} seconds"
        }
    }
    
    /**
     * Generate challenge description from milestone template
     */
    private fun generateChallengeDescriptionFromTemplate(template: MilestoneChallengeTemplate): String {
        return when (template) {
            is MilestoneChallengeTemplate -> "Achieve ${template.target} ${template.type.displayName.lowercase()}"
            is MatchWinnerTemplate -> "Win ${template.matchCount} matches to prove your mastery"
            is StreakTemplate -> "Achieve a ${template.streakCount} win streak"
            is ComboTemplate -> "Complete ${template.combos.size} advanced combo sequences"
            is TimeLimitTemplate -> "Reach ${template.targetScore} points within ${template.timeLimit} seconds"
        }
    }
}

/**
 * Milestone challenge templates
 */
sealed class MilestoneChallengeTemplate(
    val name: String,
    val type: ChallengeType,
    val target: Int
)

data class MatchWinnerTemplate(
    val matchCount: Int
) : MilestoneChallengeTemplate("Match Winner", ChallengeType.MATCH_WIN, matchCount)

data class StreakTemplate(
    val streakCount: Int
) : MilestoneChallengeTemplate("Win Streak", ChallengeType.WIN_STREAK, streakCount)

data class ComboTemplate(
    val combos: List<List<String>>
) : MilestoneChallengeTemplate("Combo Master", ChallengeType.COMBO, combos.size)

data class TimeLimitTemplate(
    val targetScore: Int,
    val timeLimit: Int
) : MilestoneChallengeTemplate("Time Attack", ChallengeType.TIME_LIMIT, targetScore)