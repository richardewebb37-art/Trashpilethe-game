package com.trashapp.database.dao

import androidx.room.*
import com.trashapp.database.entity.ChallengeEntity
import com.trashapp.database.entity.ChallengeProgressEntity
import com.trashapp.database.entity.LevelChallengesEntity
import com.trashapp.database.entity.LevelProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Challenge database operations
 */
@Dao
interface ChallengeDao {
    
    // ==================== Challenge Entity Operations ====================
    
    /**
     * Insert or replace a challenge
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)
    
    /**
     * Insert or replace multiple challenges
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)
    
    /**
     * Get all challenges for a player
     */
    @Query("SELECT * FROM challenges WHERE playerId = :playerId ORDER BY level, type")
    fun getChallengesForPlayer(playerId: String): Flow<List<ChallengeEntity>>
    
    /**
     * Get challenges for a specific level
     */
    @Query("SELECT * FROM challenges WHERE playerId = :playerId AND level = :level ORDER BY type")
    fun getChallengesForLevel(playerId: String, level: Int): Flow<List<ChallengeEntity>>
    
    /**
     * Get challenges by type for a player
     */
    @Query("SELECT * FROM challenges WHERE playerId = :playerId AND type = :type ORDER BY level")
    fun getChallengesByType(playerId: String, type: String): Flow<List<ChallengeEntity>>
    
    /**
     * Get milestone challenges
     */
    @Query("SELECT * FROM challenges WHERE playerId = :playerId AND isMilestone = 1 ORDER BY level")
    fun getMilestoneChallenges(playerId: String): Flow<List<ChallengeEntity>>
    
    /**
     * Get completed challenges for a player
     */
    @Query("SELECT * FROM challenges WHERE playerId = :playerId AND isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedChallenges(playerId: String): Flow<List<ChallengeEntity>>
    
    /**
     * Get incomplete challenges for a player
     */
    @Query("SELECT * FROM challenges WHERE playerId = :playerId AND isCompleted = 0 ORDER BY level, type")
    fun getIncompleteChallenges(playerId: String): Flow<List<ChallengeEntity>>
    
    /**
     * Get a single challenge by ID
     */
    @Query("SELECT * FROM challenges WHERE id = :challengeId LIMIT 1")
    suspend fun getChallengeById(challengeId: String): ChallengeEntity?
    
    /**
     * Update challenge completion status
     */
    @Query("UPDATE challenges SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :challengeId")
    suspend fun updateChallengeCompletion(challengeId: String, isCompleted: Boolean, completedAt: Long?)
    
    /**
     * Delete all challenges for a player
     */
    @Query("DELETE FROM challenges WHERE playerId = :playerId")
    suspend fun deleteChallengesForPlayer(playerId: String)
    
    /**
     * Count challenges for a player
     */
    @Query("SELECT COUNT(*) FROM challenges WHERE playerId = :playerId")
    suspend fun countChallengesForPlayer(playerId: String): Int
    
    /**
     * Count completed challenges for a player
     */
    @Query("SELECT COUNT(*) FROM challenges WHERE playerId = :playerId AND isCompleted = 1")
    suspend fun countCompletedChallengesForPlayer(playerId: String): Int
    
    /**
     * Count challenges for a specific level
     */
    @Query("SELECT COUNT(*) FROM challenges WHERE playerId = :playerId AND level = :level")
    suspend fun countChallengesForLevel(playerId: String, level: Int): Int
    
    /**
     * Count completed challenges for a specific level
     */
    @Query("SELECT COUNT(*) FROM challenges WHERE playerId = :playerId AND level = :level AND isCompleted = 1")
    suspend fun countCompletedChallengesForLevel(playerId: String, level: Int): Int
    
    // ==================== Challenge Progress Operations ====================
    
    /**
     * Insert or replace challenge progress
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallengeProgress(progress: ChallengeProgressEntity)
    
    /**
     * Insert or replace multiple challenge progress entries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallengeProgressList(progressList: List<ChallengeProgressEntity>)
    
    /**
     * Get challenge progress for a player
     */
    @Query("SELECT * FROM challenge_progress WHERE playerId = :playerId")
    fun getChallengeProgressForPlayer(playerId: String): Flow<List<ChallengeProgressEntity>>
    
    /**
     * Get progress for a specific challenge
     */
    @Query("SELECT * FROM challenge_progress WHERE challengeId = :challengeId AND playerId = :playerId LIMIT 1")
    suspend fun getChallengeProgress(challengeId: String, playerId: String): ChallengeProgressEntity?
    
    /**
     * Get progress for challenges at a specific level
     */
    @Query("""
        SELECT cp.* FROM challenge_progress cp
        INNER JOIN challenges c ON cp.challengeId = c.id
        WHERE c.playerId = :playerId AND c.level = :level
    """)
    fun getChallengeProgressForLevel(playerId: String, level: Int): Flow<List<ChallengeProgressEntity>>
    
    /**
     * Update challenge score progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentScore = :currentScore,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengeScoreProgress(
        challengeId: String,
        playerId: String,
        currentScore: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Update challenge abilities progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentAbilities = :currentAbilities,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengeAbilitiesProgress(
        challengeId: String,
        playerId: String,
        currentAbilities: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Update challenge skills progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentSkills = :currentSkills,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengeSkillsProgress(
        challengeId: String,
        playerId: String,
        currentSkills: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Update challenge points progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentPoints = :currentPoints,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengePointsProgress(
        challengeId: String,
        playerId: String,
        currentPoints: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Update challenge combos progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentCombos = :currentCombos,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengeCombosProgress(
        challengeId: String,
        playerId: String,
        currentCombos: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Update challenge streaks progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentStreaks = :currentStreaks,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengeStreaksProgress(
        challengeId: String,
        playerId: String,
        currentStreaks: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Update challenge cards played progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentCardsPlayed = :currentCardsPlayed,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengeCardsPlayedProgress(
        challengeId: String,
        playerId: String,
        currentCardsPlayed: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Update challenge round wins progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentRoundWins = :currentRoundWins,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengeRoundWinsProgress(
        challengeId: String,
        playerId: String,
        currentRoundWins: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Update challenge match wins progress
     */
    @Query("""
        UPDATE challenge_progress 
        SET currentMatchWins = :currentMatchWins,
            progress = :progress,
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            lastUpdated = :lastUpdated
        WHERE challengeId = :challengeId AND playerId = :playerId
    """)
    suspend fun updateChallengeMatchWinsProgress(
        challengeId: String,
        playerId: String,
        currentMatchWins: Int,
        progress: Float,
        isCompleted: Boolean,
        completedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Delete challenge progress for a player
     */
    @Query("DELETE FROM challenge_progress WHERE playerId = :playerId")
    suspend fun deleteChallengeProgressForPlayer(playerId: String)
    
    /**
     * Delete challenge progress for a specific challenge
     */
    @Query("DELETE FROM challenge_progress WHERE challengeId = :challengeId AND playerId = :playerId")
    suspend fun deleteChallengeProgress(challengeId: String, playerId: String)
    
    // ==================== Level Challenges Operations ====================
    
    /**
     * Insert or replace level challenges
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelChallenges(levelChallenges: LevelChallengesEntity)
    
    /**
     * Get level challenges for a player
     */
    @Query("SELECT * FROM level_challenges WHERE playerId = :playerId ORDER BY level")
    fun getLevelChallengesForPlayer(playerId: String): Flow<List<LevelChallengesEntity>>
    
    /**
     * Get level challenges for a specific level
     */
    @Query("SELECT * FROM level_challenges WHERE level = :level AND playerId = :playerId LIMIT 1")
    suspend fun getLevelChallenges(level: Int, playerId: String): LevelChallengesEntity?
    
    /**
     * Update level challenges completion count
     */
    @Query("""
        UPDATE level_challenges 
        SET completedCount = :completedCount,
            canAdvance = :canAdvance,
            lastUpdated = :lastUpdated
        WHERE level = :level AND playerId = :playerId
    """)
    suspend fun updateLevelChallengesCompletion(
        level: Int,
        playerId: String,
        completedCount: Int,
        canAdvance: Boolean,
        lastUpdated: Long
    )
    
    /**
     * Delete level challenges for a player
     */
    @Query("DELETE FROM level_challenges WHERE playerId = :playerId")
    suspend fun deleteLevelChallengesForPlayer(playerId: String)
    
    // ==================== Level Progress Operations ====================
    
    /**
     * Insert or replace level progress
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelProgress(levelProgress: LevelProgressEntity)
    
    /**
     * Get level progress for a player
     */
    @Query("SELECT * FROM level_progress WHERE playerId = :playerId ORDER BY level")
    fun getLevelProgressForPlayer(playerId: String): Flow<List<LevelProgressEntity>>
    
    /**
     * Get level progress for a specific level
     */
    @Query("SELECT * FROM level_progress WHERE level = :level AND playerId = :playerId LIMIT 1")
    suspend fun getLevelProgress(level: Int, playerId: String): LevelProgressEntity?
    
    /**
     * Update level progress
     */
    @Query("""
        UPDATE level_progress 
        SET totalXP = :totalXP,
            points = :points,
            abilitiesCount = :abilitiesCount,
            skillsCount = :skillsCount,
            completedChallenges = :completedChallenges,
            inProgressChallenges = :inProgressChallenges,
            notStartedChallenges = :notStartedChallenges,
            canAdvanceToNextLevel = :canAdvanceToNextLevel,
            lastUpdated = :lastUpdated
        WHERE level = :level AND playerId = :playerId
    """)
    suspend fun updateLevelProgress(
        level: Int,
        playerId: String,
        totalXP: Int,
        points: Int,
        abilitiesCount: Int,
        skillsCount: Int,
        completedChallenges: String,
        inProgressChallenges: String,
        notStartedChallenges: String,
        canAdvanceToNextLevel: Boolean,
        lastUpdated: Long
    )
    
    /**
     * Delete level progress for a player
     */
    @Query("DELETE FROM level_progress WHERE playerId = :playerId")
    suspend fun deleteLevelProgressForPlayer(playerId: String)
    
    // ==================== Batch Operations ====================
    
    /**
     * Transaction: Insert challenges and their progress entries
     */
    @Transaction
    suspend fun insertChallengesWithProgress(
        challenges: List<ChallengeEntity>,
        progressList: List<ChallengeProgressEntity>
    ) {
        insertChallenges(challenges)
        insertChallengeProgressList(progressList)
    }
    
    /**
     * Transaction: Complete challenge and update level progress
     */
    @Transaction
    suspend fun completeChallengeAndUpdateLevel(
        challengeId: String,
        playerId: String,
        level: Int,
        completedAt: Long,
        newCompletedCount: Int,
        canAdvance: Boolean
    ) {
        updateChallengeCompletion(challengeId, true, completedAt)
        updateLevelChallengesCompletion(level, playerId, newCompletedCount, canAdvance, completedAt)
    }
}