package com.trashapp.gcms.challenge

import com.trashapp.gcms.progression.Tier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages the player's challenges and tracks progress
 * 
 * This class handles:
 * - Challenge generation for each level
 * - Challenge progress tracking
 * - Level unlocking based on challenge completion
 * - Challenge statistics
 */
class ChallengeManager {
    
    private val _challenges = MutableStateFlow<Map<Int, List<Challenge>>>(emptyMap())
    val challenges: StateFlow<Map<Int, List<Challenge>>> = _challenges
    
    private val _currentLevelChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val currentLevelChallenges: StateFlow<List<Challenge>> = _currentLevelChallenges
    
    private val _challengeProgress = MutableStateFlow<Map<Int, ChallengeProgress>>(emptyMap())
    val challengeProgress: StateFlow<Map<Int, ChallengeProgress>> = _challengeProgress
    
    private val _playerLevel = MutableStateFlow(1)
    val playerLevel: StateFlow<Int> = _playerLevel
    
    private val challengeSystem = ChallengeSystem()
    private var isInitialized = false
    
    /**
     * Initialize challenge manager
     */
    suspend fun initialize(startingLevel: Int = 1) {
        _playerLevel.value = startingLevel
        isInitialized = true
    }
    
    /**
     * Generate challenges for a specific level
     */
    suspend fun generateChallengesForLevel(level: Int): List<Challenge> {
        val tier = Tier.getTierForLevel(level)
        val challengeCount = challengeSystem.calculateChallengeCount(level, tier)
        val challengeTypes = challengeSystem.determineChallengeTypes(level, tier, challengeCount)
        
        val challenges = challengeTypes.mapIndexed { index, type ->
            generateChallenge(level, tier, type, index)
        }
        
        // Store challenges for the level
        val currentMap = _challenges.value.toMutableMap()
        currentMap[level] = challenges
        _challenges.value = currentMap
        
        // Update progress for this level
        updateChallengeProgress(level, challenges)
        
        return challenges
    }
    
    /**
     * Generate a single challenge
     */
    private fun generateChallenge(
        level: Int,
        tier: Tier,
        type: ChallengeType,
        index: Int
    ): Challenge {
        val difficulty = 1.0 + (index * 0.1) // Slightly harder for each challenge
        val rarity = challengeSystem.determineRarity(level, tier)
        val requirements = challengeSystem.calculateRequirements(type, level, tier, difficulty)
        val reward = challengeSystem.calculateRewards(level, tier, rarity)
        
        val maxProgress = when (type) {
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
        
        val id = "challenge_${level}_${type.name}_${System.currentTimeMillis()}"
        val name = generateChallengeName(type, level, index)
        val description = generateChallengeDescription(type, requirements)
        
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
     * Generate challenge name
     */
    private fun generateChallengeName(type: ChallengeType, level: Int, index: Int): String {
        val prefixes = listOf("Master", "Expert", "Challenge", "Quest", "Trial")
        val suffixes = listOf("Hunter", "Seeker", "Conqueror", "Champion", "Legend")
        
        return when (type) {
            ChallengeType.SCORE -> "Score Hunter $level"
            ChallengeType.ABILITY_USE -> "Ability Master $level"
            ChallengeType.SKILL_UNLOCK -> "Skill Seeker $level"
            ChallengeType.POINT_ACCUMULATION -> "Point Collector $level"
            ChallengeType.COMBO -> "Combo Champion $level"
            ChallengeType.WIN_STREAK -> "Streak Conqueror $level"
            ChallengeType.CARD_PLAYED -> "Card Wizard $level"
            ChallengeType.ROUND_WIN -> "Round Victor $level"
            ChallengeType.MATCH_WIN -> "Match Legend $level"
            ChallengeType.TIME_LIMIT -> "Speed Demon $level"
        }
    }
    
    /**
     * Generate challenge description
     */
    private fun generateChallengeDescription(type: ChallengeType, requirements: ChallengeRequirements): String {
        return when (type) {
            ChallengeType.SCORE -> "Reach ${requirements.score} points"
            ChallengeType.ABILITY_USE -> "Use abilities ${requirements.abilitiesUsed.size} times"
            ChallengeType.SKILL_UNLOCK -> "Unlock ${requirements.skillsUnlocked.size} skills"
            ChallengeType.POINT_ACCUMULATION -> "Earn ${requirements.pointsEarned} points"
            ChallengeType.COMBO -> "Complete ${requirements.comboRequired?.abilityCombos?.size ?: 1} combo actions"
            ChallengeType.WIN_STREAK -> "Achieve a ${requirements.winStreak} win streak"
            ChallengeType.CARD_PLAYED -> "Play ${requirements.cardsPlayed} cards"
            ChallengeType.ROUND_WIN -> "Win ${requirements.roundsWon} rounds"
            ChallengeType.MATCH_WIN -> "Win ${requirements.matchesWon} matches"
            ChallengeType.TIME_LIMIT -> "Reach ${requirements.score} points within ${requirements.timeLimit} seconds"
        }
    }
    
    /**
     * Set challenges for current level
     */
    suspend fun setCurrentLevelChallenges(level: Int) {
        val challenges = _challenges.value[level] ?: generateChallengesForLevel(level)
        _currentLevelChallenges.value = challenges
    }
    
    /**
     * Update challenge progress
     */
    suspend fun updateChallengeProgress(
        challengeId: String,
        progress: Int
    ): Boolean {
        val currentMap = _challenges.value.toMutableMap()
        var challengeFound = false
        
        // Find and update the challenge
        for ((level, challenges) in currentMap) {
            val updatedChallenges = challenges.map { challenge ->
                if (challenge.id == challengeId && !challenge.isCompleted) {
                    challengeFound = true
                    val updated = challenge.updateProgress(progress)
                    if (updated.canComplete()) {
                        updated.complete()
                    } else {
                        updated
                    }
                } else {
                    challenge
                }
            }
            currentMap[level] = updatedChallenges
        }
        
        if (challengeFound) {
            _challenges.value = currentMap
            
            // Update current level challenges
            val currentLevel = _playerLevel.value
            if (currentMap.containsKey(currentLevel)) {
                _currentLevelChallenges.value = currentMap[currentLevel]!!
            }
            
            // Update progress for affected level
            for ((level, challenges) in currentMap) {
                updateChallengeProgress(level, challenges)
            }
        }
        
        return challengeFound
    }
    
    /**
     * Update progress for a specific level
     */
    private fun updateChallengeProgress(level: Int, challenges: List<Challenge>) {
        val progressMap = _challengeProgress.value.toMutableMap()
        progressMap[level] = challengeSystem.getProgressSummary(challenges)
        _challengeProgress.value = progressMap
    }
    
    /**
     * Complete a specific challenge
     */
    suspend fun completeChallenge(challengeId: String): Boolean {
        val currentMap = _challenges.value.toMutableMap()
        var challengeFound = false
        
        for ((level, challenges) in currentMap) {
            val updatedChallenges = challenges.map { challenge ->
                if (challenge.id == challengeId && !challenge.isCompleted) {
                    challengeFound = true
                    challenge.complete()
                } else {
                    challenge
                }
            }
            currentMap[level] = updatedChallenges
        }
        
        if (challengeFound) {
            _challenges.value = currentMap
            
            // Update current level challenges
            val currentLevel = _playerLevel.value
            if (currentMap.containsKey(currentLevel)) {
                _currentLevelChallenges.value = currentMap[currentLevel]!!
            }
            
            // Update progress
            for ((level, challenges) in currentMap) {
                updateChallengeProgress(level, challenges)
            }
        }
        
        return challengeFound
    }
    
    /**
     * Check if player can advance to next level
     */
    suspend fun canAdvanceToNextLevel(
        currentLevel: Int,
        hasRequiredXP: Boolean,
        hasRequiredPoints: Boolean,
        hasRequiredAbilities: Boolean,
        hasRequiredSkills: Boolean
    ): Boolean {
        val challenges = _challenges.value[currentLevel] ?: return false
        
        return challengeSystem.canAdvanceToNextLevel(
            currentLevel = currentLevel,
            challenges = challenges,
            hasRequiredXP = hasRequiredXP,
            hasRequiredPoints = hasRequiredPoints,
            hasRequiredAbilities = hasRequiredAbilities,
            hasRequiredSkills = hasRequiredSkills
        )
    }
    
    /**
     * Get challenges for a specific level
     */
    fun getChallengesForLevel(level: Int): List<Challenge> {
        return _challenges.value[level] ?: emptyList()
    }
    
    /**
     * Get progress for a specific level
     */
    fun getProgressForLevel(level: Int): ChallengeProgress? {
        return _challengeProgress.value[level]
    }
    
    /**
     * Get all completed challenges
     */
    fun getCompletedChallenges(): List<Challenge> {
        return _challenges.value.values.flatten().filter { it.isCompleted }
    }
    
    /**
     * Get in-progress challenges
     */
    fun getInProgressChallenges(): List<Challenge> {
        return _challenges.value.values.flatten().filter { 
            !it.isCompleted && it.progress > 0 
        }
    }
    
    /**
     * Get not started challenges
     */
    fun getNotStartedChallenges(): List<Challenge> {
        return _challenges.value.values.flatten().filter { 
            !it.isCompleted && it.progress == 0 
        }
    }
    
    /**
     * Get challenges by type
     */
    fun getChallengesByType(type: ChallengeType): List<Challenge> {
        return _challenges.value.values.flatten().filter { it.type == type }
    }
    
    /**
     * Reset challenge manager (for testing)
     */
    suspend fun reset() {
        _challenges.value = emptyMap()
        _currentLevelChallenges.value = emptyList()
        _challengeProgress.value = emptyMap()
        _playerLevel.value = 1
        isInitialized = false
    }
}