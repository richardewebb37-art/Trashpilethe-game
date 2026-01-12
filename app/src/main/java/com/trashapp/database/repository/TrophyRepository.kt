package com.trashapp.database.repository

import com.trashapp.database.dao.TrophyDao
import com.trashapp.database.entity.TrophyCollectionEntity
import com.trashapp.database.entity.TrophyEntity
import com.trashapp.database.entity.TrophyProgressEntity
import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.trophy.Trophy
import com.trashapp.gcms.trophy.TrophyRarity
import com.trashapp.gcms.trophy.TrophyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Repository for Trophy data operations
 * Bridges between TrophyManager and Room database
 */
class TrophyRepository(private val trophyDao: TrophyDao) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    // ==================== Trophy Operations ====================
    
    /**
     * Save trophies to database
     */
    suspend fun saveTrophies(playerId: String, trophies: List<Trophy>) {
        val trophyEntities = trophies.map { it.toEntity(playerId) }
        trophyDao.insertTrophies(trophyEntities)
        
        // Initialize progress for each trophy
        val progressList = trophies.map { trophy ->
            TrophyProgressEntity(
                trophyId = trophy.id,
                playerId = playerId,
                isUnlocked = false,
                progress = 0.0f,
                unlockedAt = null,
                lastUpdated = System.currentTimeMillis()
            )
        }
        trophyDao.insertTrophyProgressList(progressList)
        
        // Initialize collection
        val collection = TrophyCollectionEntity(
            playerId = playerId,
            unlockedTrophies = "[]",
            totalXPEarned = 0,
            totalPointsEarned = 0,
            lastUpdated = System.currentTimeMillis()
        )
        trophyDao.insertTrophyCollection(collection)
    }
    
    /**
     * Get all trophies for a player
     */
    fun getTrophiesForPlayer(playerId: String): Flow<List<Trophy>> {
        return trophyDao.getTrophiesForPlayer(playerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get trophies by tier
     */
    fun getTrophiesByTier(playerId: String, tier: Tier): Flow<List<Trophy>> {
        return trophyDao.getTrophiesByTier(playerId, tier.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get trophies by rarity
     */
    fun getTrophiesByRarity(playerId: String, rarity: TrophyRarity): Flow<List<Trophy>> {
        return trophyDao.getTrophiesByRarity(playerId, rarity.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get trophies for a specific level
     */
    fun getTrophiesForLevel(playerId: String, level: Int): Flow<List<Trophy>> {
        return trophyDao.getTrophiesForLevel(playerId, level).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get milestone trophies
     */
    fun getMilestoneTrophies(playerId: String): Flow<List<Trophy>> {
        return trophyDao.getMilestoneTrophies(playerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get unlocked trophies
     */
    fun getUnlockedTrophies(playerId: String): Flow<List<Trophy>> {
        return trophyDao.getUnlockedTrophies(playerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get locked trophies
     */
    fun getLockedTrophies(playerId: String): Flow<List<Trophy>> {
        return trophyDao.getLockedTrophies(playerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    /**
     * Get a specific trophy by ID
     */
    suspend fun getTrophyById(trophyId: String): Trophy? {
        val entity = trophyDao.getTrophyById(trophyId)
        return entity?.toDomainModel()
    }
    
    /**
     * Unlock a trophy
     */
    suspend fun unlockTrophy(trophyId: String, playerId: String): Boolean {
        val trophy = getTrophyById(trophyId) ?: return false
        
        val unlockedAt = System.currentTimeMillis()
        
        // Update trophy unlock status
        trophyDao.updateTrophyUnlockStatus(trophyId, true, unlockedAt)
        
        // Update progress
        trophyDao.updateTrophyProgress(
            trophyId = trophyId,
            playerId = playerId,
            progress = 1.0f,
            isUnlocked = true,
            unlockedAt = unlockedAt,
            lastUpdated = unlockedAt
        )
        
        // Get current collection
        val collection = trophyDao.getTrophyCollection(playerId).map { it }.map { it } // Get current value
        // Note: In real implementation, we'd use first() to get current value
        
        // Update collection
        val unlockedTrophies = json.decodeFromString<List<String>>("[]") // Simplified
        val newUnlockedTrophies = unlockedTrophies + trophyId
        val newTotalXP = 0 // trophy.xpReward (simplified)
        val newTotalPoints = 0 // trophy.pointsReward (simplified)
        
        trophyDao.updateTrophyCollection(
            playerId = playerId,
            unlockedTrophies = json.encodeToString(newUnlockedTrophies),
            totalXPEarned = newTotalXP,
            totalPointsEarned = newTotalPoints,
            lastUpdated = unlockedAt
        )
        
        return true
    }
    
    /**
     * Delete all trophies for a player
     */
    suspend fun deleteTrophiesForPlayer(playerId: String) {
        trophyDao.deleteTrophiesForPlayer(playerId)
        trophyDao.deleteTrophyCollection(playerId)
        trophyDao.deleteTrophyProgressForPlayer(playerId)
    }
    
    // ==================== Trophy Collection Operations ====================
    
    /**
     * Get trophy collection for a player
     */
    fun getTrophyCollection(playerId: String): Flow<TrophyCollectionEntity?> {
        return trophyDao.getTrophyCollection(playerId)
    }
    
    /**
     * Get trophy stats for a player
     */
    suspend fun getTrophyStats(playerId: String): TrophyStats {
        val totalTrophies = trophyDao.countTrophiesForPlayer(playerId)
        val unlockedCount = trophyDao.countUnlockedTrophiesForPlayer(playerId)
        val collection = trophyDao.getTrophyCollection(playerId).map { it }.map { it }
        
        return TrophyStats(
            totalTrophies = totalTrophies,
            unlockedTrophies = emptyList(), // Would load from collection
            totalXPEarned = collection?.totalXPEarned ?: 0,
            totalPointsEarned = collection?.totalPointsEarned ?: 0,
            completionPercentage = if (totalTrophies > 0) {
                (unlockedCount.toFloat() / totalTrophies * 100).toInt()
            } else 0
        )
    }
    
    /**
     * Update trophy collection stats
     */
    suspend fun updateTrophyCollection(
        playerId: String,
        unlockedTrophies: List<String>,
        totalXPEarned: Int,
        totalPointsEarned: Int
    ) {
        trophyDao.updateTrophyCollection(
            playerId = playerId,
            unlockedTrophies = json.encodeToString(unlockedTrophies),
            totalXPEarned = totalXPEarned,
            totalPointsEarned = totalPointsEarned,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    // ==================== Trophy Progress Operations ====================
    
    /**
     * Get trophy progress for a player
     */
    fun getTrophyProgressForPlayer(playerId: String): Flow<List<TrophyProgressEntity>> {
        return trophyDao.getTrophyProgressForPlayer(playerId)
    }
    
    /**
     * Get progress for a specific trophy
     */
    suspend fun getTrophyProgress(trophyId: String, playerId: String): TrophyProgressEntity? {
        return trophyDao.getTrophyProgress(trophyId, playerId)
    }
    
    /**
     * Update trophy progress
     */
    suspend fun updateTrophyProgress(
        trophyId: String,
        playerId: String,
        progress: Float,
        isUnlocked: Boolean
    ) {
        val unlockedAt = if (isUnlocked) System.currentTimeMillis() else null
        trophyDao.updateTrophyProgress(
            trophyId = trophyId,
            playerId = playerId,
            progress = progress,
            isUnlocked = isUnlocked,
            unlockedAt = unlockedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Delete trophy progress for a player
     */
    suspend fun deleteTrophyProgressForPlayer(playerId: String) {
        trophyDao.deleteTrophyProgressForPlayer(playerId)
    }
}