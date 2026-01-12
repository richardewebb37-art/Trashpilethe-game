package com.trashapp.database.repository

import com.trashapp.database.dao.ChallengeDao
import com.trashapp.database.entity.ChallengeEntity
import com.trashapp.database.entity.ChallengeProgressEntity
import com.trashapp.database.entity.LevelChallengesEntity
import com.trashapp.database.entity.LevelProgressEntity
import com.trashapp.gcms.challenge.Challenge
import com.trashapp.gcms.challenge.ChallengeType
import com.trashapp.gcms.challenge.LevelProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for Challenge data operations
 * Bridges between ChallengeManager and Room database
 */
class ChallengeRepository(private val challengeDao: ChallengeDao) {
    
    // ==================== Challenge Operations ====================
    
    /**
     * Save challenges to database
     */
    suspend fun saveChallenges(playerId: String, challenges: List<Challenge>) {
        val challengeEntities = challenges.map { it.toEntity(playerId) }
        challengeDao.insertChallenges(challengeEntities)
        
        // Initialize progress for each challenge
        val progressList = challenges.map { challenge ->
            ChallengeProgressEntity(
                challengeId = challenge.id,
                playerId = playerId,
                currentScore = 0,
                currentAbilities = 0,
                currentSkills = 0,
                currentPoints = 0,
                currentCombos = 0,
                currentStreaks = 0,
                currentCardsPlayed = 0,
                currentRoundWins = 0,
                currentMatchWins = 0,
                isCompleted = false,
                progress = 0.0f,
                completedAt = null,
                lastUpdated = System.currentTimeMillis()
            )
        }
        challengeDao.insertChallengeProgressList(progressList)
    }
    
    /**
     * Save challenges for a specific level
     */
    suspend fun saveChallengesForLevel(playerId: String, level: Int, challenges: List<Challenge>) {
        // Save challenges
        val challengeEntities = challenges.map { it.toEntity(playerId) }
        challengeDao.insertChallenges(challengeEntities)
        
        // Initialize progress
        val progressList = challenges.map { challenge ->
            ChallengeProgressEntity(
                challengeId = challenge.id,
                playerId = playerId,
                currentScore = 0,
                currentAbilities = 0,
                currentSkills = 0,
                currentPoints = 0,
                currentCombos = 0,
                currentStreaks = 0,
                currentCardsPlayed = 0,
                currentRoundWins = 0,
                currentMatchWins = 0,
                isCompleted = false,
                progress = 0.0f,
                completedAt = null,
                lastUpdated = System.currentTimeMillis()
            )
        }
        challengeDao.insertChallengeProgressList(progressList)
        
        // Save level challenges
        val challengeIds = challenges.map { it.id }
        val levelChallenges = LevelChallengesEntity(
            level = level,
            playerId = playerId,
            challengeIds = challengeIds.joinToString(","),
            completedCount = 0,
            totalCount = challenges.size,
            canAdvance = false,
            lastUpdated = System.currentTimeMillis()
        )
        challengeDao.insertLevelChallenges(levelChallenges)
        
        // Save level progress
        val levelProgress = LevelProgressEntity(
            level = level,
            playerId = playerId,
            totalXP = 0,
            requiredXP = level * 100, // Simplified
            points = 0,
            requiredPoints = level * 10, // Simplified
            abilitiesCount = 0,
            requiredAbilities = 1, // Simplified
            skillsCount = 0,
            requiredSkills = 1, // Simplified
            completedChallenges = "",
            inProgressChallenges = "",
            notStartedChallenges = challengeIds.joinToString(","),
            totalChallenges = challenges.size,
            canAdvanceToNextLevel = false,
            lastUpdated = System.currentTimeMillis()
        )
        challengeDao.insertLevelProgress(levelProgress)
    }
    
    /**
     * Get all challenges for a player
     */
    fun getChallengesForPlayer(playerId: String): Flow<List<Challenge>> {
        return challengeDao.getChallengesForPlayer(playerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get challenges for a specific level
     */
    fun getChallengesForLevel(playerId: String, level: Int): Flow<List<Challenge>> {
        return challengeDao.getChallengesForLevel(playerId, level).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get challenges by type
     */
    fun getChallengesByType(playerId: String, type: ChallengeType): Flow<List<Challenge>> {
        return challengeDao.getChallengesByType(playerId, type.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get milestone challenges
     */
    fun getMilestoneChallenges(playerId: String): Flow<List<Challenge>> {
        return challengeDao.getMilestoneChallenges(playerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get completed challenges
     */
    fun getCompletedChallenges(playerId: String): Flow<List<Challenge>> {
        return challengeDao.getCompletedChallenges(playerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get incomplete challenges
     */
    fun getIncompleteChallenges(playerId: String): Flow<List<Challenge>> {
        return challengeDao.getIncompleteChallenges(playerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get a specific challenge by ID
     */
    suspend fun getChallengeById(challengeId: String): Challenge? {
        val entity = challengeDao.getChallengeById(challengeId)
        return entity?.toDomainModel()
    }
    
    /**
     * Complete a challenge
     */
    suspend fun completeChallenge(challengeId: String, playerId: String, level: Int): Boolean {
        val completedAt = System.currentTimeMillis()
        
        // Update challenge completion status
        challengeDao.updateChallengeCompletion(challengeId, true, completedAt)
        
        // Get current level challenges
        val levelChallenges = challengeDao.getLevelChallenges(level, playerId)
        if (levelChallenges != null) {
            val newCompletedCount = levelChallenges.completedCount + 1
            val canAdvance = newCompletedCount >= levelChallenges.totalCount
            
            // Update level challenges
            challengeDao.updateLevelChallengesCompletion(
                level = level,
                playerId = playerId,
                completedCount = newCompletedCount,
                canAdvance = canAdvance,
                lastUpdated = completedAt
            )
        }
        
        return true
    }
    
    /**
     * Delete all challenges for a player
     */
    suspend fun deleteChallengesForPlayer(playerId: String) {
        challengeDao.deleteChallengesForPlayer(playerId)
        challengeDao.deleteChallengeProgressForPlayer(playerId)
        challengeDao.deleteLevelChallengesForPlayer(playerId)
        challengeDao.deleteLevelProgressForPlayer(playerId)
    }
    
    // ==================== Challenge Progress Operations ====================
    
    /**
     * Get challenge progress for a player
     */
    fun getChallengeProgressForPlayer(playerId: String): Flow<List<ChallengeProgressEntity>> {
        return challengeDao.getChallengeProgressForPlayer(playerId)
    }
    
    /**
     * Get progress for a specific challenge
     */
    suspend fun getChallengeProgress(challengeId: String, playerId: String): ChallengeProgressEntity? {
        return challengeDao.getChallengeProgress(challengeId, playerId)
    }
    
    /**
     * Update challenge score progress
     */
    suspend fun updateChallengeScoreProgress(
        challengeId: String,
        playerId: String,
        currentScore: Int,
        requiredScore: Int
    ) {
        val progress = if (requiredScore > 0) currentScore.toFloat() / requiredScore else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengeScoreProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentScore = currentScore,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update challenge abilities progress
     */
    suspend fun updateChallengeAbilitiesProgress(
        challengeId: String,
        playerId: String,
        currentAbilities: Int,
        requiredAbilities: Int
    ) {
        val progress = if (requiredAbilities > 0) currentAbilities.toFloat() / requiredAbilities else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengeAbilitiesProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentAbilities = currentAbilities,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update challenge skills progress
     */
    suspend fun updateChallengeSkillsProgress(
        challengeId: String,
        playerId: String,
        currentSkills: Int,
        requiredSkills: Int
    ) {
        val progress = if (requiredSkills > 0) currentSkills.toFloat() / requiredSkills else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengeSkillsProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentSkills = currentSkills,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update challenge points progress
     */
    suspend fun updateChallengePointsProgress(
        challengeId: String,
        playerId: String,
        currentPoints: Int,
        requiredPoints: Int
    ) {
        val progress = if (requiredPoints > 0) currentPoints.toFloat() / requiredPoints else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengePointsProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentPoints = currentPoints,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update challenge combos progress
     */
    suspend fun updateChallengeCombosProgress(
        challengeId: String,
        playerId: String,
        currentCombos: Int,
        requiredCombos: Int
    ) {
        val progress = if (requiredCombos > 0) currentCombos.toFloat() / requiredCombos else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengeCombosProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentCombos = currentCombos,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update challenge streaks progress
     */
    suspend fun updateChallengeStreaksProgress(
        challengeId: String,
        playerId: String,
        currentStreaks: Int,
        requiredStreaks: Int
    ) {
        val progress = if (requiredStreaks > 0) currentStreaks.toFloat() / requiredStreaks else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengeStreaksProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentStreaks = currentStreaks,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update challenge cards played progress
     */
    suspend fun updateChallengeCardsPlayedProgress(
        challengeId: String,
        playerId: String,
        currentCardsPlayed: Int,
        requiredCardsPlayed: Int
    ) {
        val progress = if (requiredCardsPlayed > 0) currentCardsPlayed.toFloat() / requiredCardsPlayed else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengeCardsPlayedProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentCardsPlayed = currentCardsPlayed,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update challenge round wins progress
     */
    suspend fun updateChallengeRoundWinsProgress(
        challengeId: String,
        playerId: String,
        currentRoundWins: Int,
        requiredRoundWins: Int
    ) {
        val progress = if (requiredRoundWins > 0) currentRoundWins.toFloat() / requiredRoundWins else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengeRoundWinsProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentRoundWins = currentRoundWins,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Update challenge match wins progress
     */
    suspend fun updateChallengeMatchWinsProgress(
        challengeId: String,
        playerId: String,
        currentMatchWins: Int,
        requiredMatchWins: Int
    ) {
        val progress = if (requiredMatchWins > 0) currentMatchWins.toFloat() / requiredMatchWins else 0f
        val isCompleted = progress >= 1.0f
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        
        challengeDao.updateChallengeMatchWinsProgress(
            challengeId = challengeId,
            playerId = playerId,
            currentMatchWins = currentMatchWins,
            progress = progress.coerceIn(0f, 1f),
            isCompleted = isCompleted,
            completedAt = completedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Delete challenge progress for a player
     */
    suspend fun deleteChallengeProgressForPlayer(playerId: String) {
        challengeDao.deleteChallengeProgressForPlayer(playerId)
    }
    
    // ==================== Level Challenges Operations ====================
    
    /**
     * Get level challenges for a player
     */
    fun getLevelChallengesForPlayer(playerId: String): Flow<List<LevelChallengesEntity>> {
        return challengeDao.getLevelChallengesForPlayer(playerId)
    }
    
    /**
     * Get level challenges for a specific level
     */
    suspend fun getLevelChallenges(level: Int, playerId: String): LevelChallengesEntity? {
        return challengeDao.getLevelChallenges(level, playerId)
    }
    
    /**
     * Update level challenges completion
     */
    suspend fun updateLevelChallengesCompletion(
        level: Int,
        playerId: String,
        completedCount: Int,
        canAdvance: Boolean
    ) {
        challengeDao.updateLevelChallengesCompletion(
            level = level,
            playerId = playerId,
            completedCount = completedCount,
            canAdvance = canAdvance,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Delete level challenges for a player
     */
    suspend fun deleteLevelChallengesForPlayer(playerId: String) {
        challengeDao.deleteLevelChallengesForPlayer(playerId)
    }
    
    // ==================== Level Progress Operations ====================
    
    /**
     * Get level progress for a player
     */
    fun getLevelProgressForPlayer(playerId: String): Flow<List<LevelProgressEntity>> {
        return challengeDao.getLevelProgressForPlayer(playerId)
    }
    
    /**
     * Get level progress for a specific level
     */
    suspend fun getLevelProgress(level: Int, playerId: String): LevelProgressEntity? {
        return challengeDao.getLevelProgress(level, playerId)
    }
    
    /**
     * Update level progress
     */
    suspend fun updateLevelProgress(
        level: Int,
        playerId: String,
        levelProgress: LevelProgress
    ) {
        challengeDao.updateLevelProgress(
            level = level,
            playerId = playerId,
            totalXP = levelProgress.totalXP,
            points = levelProgress.points,
            abilitiesCount = levelProgress.abilitiesCount,
            skillsCount = levelProgress.skillsCount,
            completedChallenges = levelProgress.completedChallenges.joinToString(","),
            inProgressChallenges = levelProgress.inProgressChallenges.joinToString(","),
            notStartedChallenges = levelProgress.notStartedChallenges.joinToString(","),
            canAdvanceToNextLevel = levelProgress.canAdvanceToNextLevel,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Delete level progress for a player
     */
    suspend fun deleteLevelProgressForPlayer(playerId: String) {
        challengeDao.deleteLevelProgressForPlayer(playerId)
    }
}