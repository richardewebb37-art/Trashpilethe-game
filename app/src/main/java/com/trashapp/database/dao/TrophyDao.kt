package com.trashapp.database.dao

import androidx.room.*
import com.trashapp.database.entity.TrophyCollectionEntity
import com.trashapp.database.entity.TrophyEntity
import com.trashapp.database.entity.TrophyProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Trophy database operations
 */
@Dao
interface TrophyDao {
    
    // ==================== Trophy Entity Operations ====================
    
    /**
     * Insert or replace a trophy
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrophy(trophy: TrophyEntity)
    
    /**
     * Insert or replace multiple trophies
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrophies(trophies: List<TrophyEntity>)
    
    /**
     * Get all trophies for a player
     */
    @Query("SELECT * FROM trophies WHERE playerId = :playerId ORDER BY tier, rarity")
    fun getTrophiesForPlayer(playerId: String): Flow<List<TrophyEntity>>
    
    /**
     * Get trophies by tier for a player
     */
    @Query("SELECT * FROM trophies WHERE playerId = :playerId AND tier = :tier ORDER BY rarity")
    fun getTrophiesByTier(playerId: String, tier: String): Flow<List<TrophyEntity>>
    
    /**
     * Get trophies by rarity for a player
     */
    @Query("SELECT * FROM trophies WHERE playerId = :playerId AND rarity = :rarity ORDER BY tier")
    fun getTrophiesByRarity(playerId: String, rarity: String): Flow<List<TrophyEntity>>
    
    /**
     * Get trophies for a specific level
     */
    @Query("SELECT * FROM trophies WHERE playerId = :playerId AND requiredLevel = :level")
    fun getTrophiesForLevel(playerId: String, level: Int): Flow<List<TrophyEntity>>
    
    /**
     * Get milestone trophies
     */
    @Query("SELECT * FROM trophies WHERE playerId = :playerId AND isMilestone = 1 ORDER BY requiredLevel")
    fun getMilestoneTrophies(playerId: String): Flow<List<TrophyEntity>>
    
    /**
     * Get unlocked trophies for a player
     */
    @Query("SELECT * FROM trophies WHERE playerId = :playerId AND isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedTrophies(playerId: String): Flow<List<TrophyEntity>>
    
    /**
     * Get locked trophies for a player
     */
    @Query("SELECT * FROM trophies WHERE playerId = :playerId AND isUnlocked = 0 ORDER BY tier, rarity")
    fun getLockedTrophies(playerId: String): Flow<List<TrophyEntity>>
    
    /**
     * Update trophy unlock status
     */
    @Query("UPDATE trophies SET isUnlocked = :isUnlocked, unlockedAt = :unlockedAt WHERE id = :trophyId")
    suspend fun updateTrophyUnlockStatus(trophyId: String, isUnlocked: Boolean, unlockedAt: Long?)
    
    /**
     * Get a single trophy by ID
     */
    @Query("SELECT * FROM trophies WHERE id = :trophyId LIMIT 1")
    suspend fun getTrophyById(trophyId: String): TrophyEntity?
    
    /**
     * Delete all trophies for a player
     */
    @Query("DELETE FROM trophies WHERE playerId = :playerId")
    suspend fun deleteTrophiesForPlayer(playerId: String)
    
    /**
     * Count trophies for a player
     */
    @Query("SELECT COUNT(*) FROM trophies WHERE playerId = :playerId")
    suspend fun countTrophiesForPlayer(playerId: String): Int
    
    /**
     * Count unlocked trophies for a player
     */
    @Query("SELECT COUNT(*) FROM trophies WHERE playerId = :playerId AND isUnlocked = 1")
    suspend fun countUnlockedTrophiesForPlayer(playerId: String): Int
    
    // ==================== Trophy Collection Operations ====================
    
    /**
     * Insert or replace trophy collection
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrophyCollection(collection: TrophyCollectionEntity)
    
    /**
     * Get trophy collection for a player
     */
    @Query("SELECT * FROM trophy_collections WHERE playerId = :playerId LIMIT 1")
    fun getTrophyCollection(playerId: String): Flow<TrophyCollectionEntity?>
    
    /**
     * Update trophy collection stats
     */
    @Query("""
        UPDATE trophy_collections 
        SET unlockedTrophies = :unlockedTrophies,
            totalXPEarned = :totalXPEarned,
            totalPointsEarned = :totalPointsEarned,
            lastUpdated = :lastUpdated
        WHERE playerId = :playerId
    """)
    suspend fun updateTrophyCollection(
        playerId: String,
        unlockedTrophies: String,
        totalXPEarned: Int,
        totalPointsEarned: Int,
        lastUpdated: Long
    )
    
    /**
     * Delete trophy collection
     */
    @Query("DELETE FROM trophy_collections WHERE playerId = :playerId")
    suspend fun deleteTrophyCollection(playerId: String)
    
    // ==================== Trophy Progress Operations ====================
    
    /**
     * Insert or replace trophy progress
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrophyProgress(progress: TrophyProgressEntity)
    
    /**
     * Insert or replace multiple trophy progress entries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrophyProgressList(progressList: List<TrophyProgressEntity>)
    
    /**
     * Get trophy progress for a player
     */
    @Query("SELECT * FROM trophy_progress WHERE playerId = :playerId")
    fun getTrophyProgressForPlayer(playerId: String): Flow<List<TrophyProgressEntity>>
    
    /**
     * Get progress for a specific trophy
     */
    @Query("SELECT * FROM trophy_progress WHERE trophyId = :trophyId AND playerId = :playerId LIMIT 1")
    suspend fun getTrophyProgress(trophyId: String, playerId: String): TrophyProgressEntity?
    
    /**
     * Update trophy progress
     */
    @Query("""
        UPDATE trophy_progress 
        SET progress = :progress,
            isUnlocked = :isUnlocked,
            unlockedAt = :unlockedAt,
            lastUpdated = :lastUpdated
        WHERE trophyId = :trophyId AND playerId = :playerId
    """)
    suspend fun updateTrophyProgress(
        trophyId: String,
        playerId: String,
        progress: Float,
        isUnlocked: Boolean,
        unlockedAt: Long?,
        lastUpdated: Long
    )
    
    /**
     * Delete trophy progress for a player
     */
    @Query("DELETE FROM trophy_progress WHERE playerId = :playerId")
    suspend fun deleteTrophyProgressForPlayer(playerId: String)
    
    /**
     * Delete trophy progress for a specific trophy
     */
    @Query("DELETE FROM trophy_progress WHERE trophyId = :trophyId AND playerId = :playerId")
    suspend fun deleteTrophyProgress(trophyId: String, playerId: String)
    
    // ==================== Batch Operations ====================
    
    /**
     * Transaction: Insert trophies and their progress entries
     */
    @Transaction
    suspend fun insertTrophiesWithProgress(
        trophies: List<TrophyEntity>,
        progressList: List<TrophyProgressEntity>
    ) {
        insertTrophies(trophies)
        insertTrophyProgressList(progressList)
    }
    
    /**
     * Transaction: Update trophy unlock status and collection stats
     */
    @Transaction
    suspend fun unlockTrophyAndUpdateCollection(
        trophyId: String,
        playerId: String,
        unlockedAt: Long,
        newUnlockedTrophies: String,
        newTotalXP: Int,
        newTotalPoints: Int
    ) {
        updateTrophyUnlockStatus(trophyId, true, unlockedAt)
        updateTrophyCollection(
            playerId,
            newUnlockedTrophies,
            newTotalXP,
            newTotalPoints,
            unlockedAt
        )
    }
}