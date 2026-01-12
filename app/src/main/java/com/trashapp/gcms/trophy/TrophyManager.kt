package com.trashapp.gcms.trophy

import com.trashapp.gcms.progression.PointSystem
import com.trashapp.gcms.progression.Tier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages the player's trophy collection and tracking
 * 
 * This class handles:
 * - Trophy collection storage
 * - Trophy unlock status tracking
 * - Trophy statistics
 * - Progress notification
 */
class TrophyManager {
    
    private val _unlockedTrophies = MutableStateFlow<Map<String, Trophy>>(emptyMap())
    val unlockedTrophies: StateFlow<Map<String, Trophy>> = _unlockedTrophies
    
    private val _trophyCollection = MutableStateFlow<TrophyCollection>(TrophyCollection())
    val trophyCollection: StateFlow<TrophyCollection> = _trophyCollection
    
    private val trophySystem = TrophySystem()
    
    /**
     * Initialize trophy manager with predefined trophies
     */
    suspend fun initialize(trophies: List<Trophy>) {
        val trophyMap = trophies.associateBy { it.id }
        _unlockedTrophies.value = trophyMap
        updateCollectionStats()
    }
    
    /**
     * Award trophies on level up
     */
    suspend fun awardTrophiesOnLevelUp(
        level: Int,
        tier: Tier,
        pointSystem: PointSystem,
        playerAbilities: Set<String>,
        playerSkills: Set<String>
    ): TrophyReward {
        val availableTrophies = _unlockedTrophies.value.values.toList()
        
        val reward = trophySystem.awardTrophiesForLevelUp(
            level = level,
            tier = tier,
            availableTrophies = availableTrophies,
            playerPoints = pointSystem.availablePoints,
            playerAbilities = playerAbilities,
            playerSkills = playerSkills
        )
        
        // Update unlocked status
        val currentTrophies = _unlockedTrophies.value.toMutableMap()
        reward.trophies.forEach { awarded ->
            currentTrophies[awarded.id] = awarded
        }
        _unlockedTrophies.value = currentTrophies
        
        updateCollectionStats()
        
        return reward
    }
    
    /**
     * Unlock a specific trophy
     */
    suspend fun unlockTrophy(trophyId: String): Boolean {
        val currentTrophies = _unlockedTrophies.value.toMutableMap()
        val trophy = currentTrophies[trophyId] ?: return false
        
        if (!trophy.isUnlocked) {
            currentTrophies[trophyId] = trophy.copy(
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis()
            )
            _unlockedTrophies.value = currentTrophies
            updateCollectionStats()
            return true
        }
        
        return false
    }
    
    /**
     * Get trophies by tier
     */
    fun getTrophiesByTier(tier: Tier): List<Trophy> {
        return _unlockedTrophies.value.values
            .filter { it.tier == tier }
            .sortedBy { it.requiredLevel }
    }
    
    /**
     * Get trophies by rarity
     */
    fun getTrophiesByRarity(rarity: TrophyRarity): List<Trophy> {
        return _unlockedTrophies.value.values
            .filter { it.rarity == rarity }
            .sortedBy { it.requiredLevel }
    }
    
    /**
     * Get unlocked trophies
     */
    fun getUnlockedTrophies(): List<Trophy> {
        return _unlockedTrophies.value.values
            .filter { it.isUnlocked }
            .sortedByDescending { it.unlockedAt }
    }
    
    /**
     * Get locked trophies that are eligible for unlock
     */
    fun getEligibleTrophies(
        playerLevel: Int,
        playerPoints: Int,
        playerAbilities: Set<String>,
        playerSkills: Set<String>
    ): List<Trophy> {
        return _unlockedTrophies.value.values
            .filter { !it.isUnlocked }
            .filter { trophy ->
                trophy.canUnlock(
                    playerLevel,
                    playerPoints,
                    playerAbilities,
                    playerSkills
                )
            }
            .sortedBy { it.requiredLevel }
    }
    
    /**
     * Get trophy completion percentage
     */
    fun getCompletionPercentage(): Float {
        val total = _unlockedTrophies.value.size
        val unlocked = _unlockedTrophies.value.values.count { it.isUnlocked }
        return if (total > 0) (unlocked.toFloat() / total) * 100f else 0f
    }
    
    /**
     * Update collection statistics
     */
    private fun updateCollectionStats() {
        val trophies = _unlockedTrophies.value.values
        val unlocked = trophies.filter { it.isUnlocked }
        
        val totalXP = unlocked.sumOf { it.getXPValue() }
        val totalPoints = unlocked.sumOf { it.getPointBonus() }
        
        val rarityCount = unlocked.groupBy { it.rarity }
            .mapValues { it.value.size }
        
        _trophyCollection.value = TrophyCollection(
            totalTrophies = trophies.size,
            unlockedTrophies = unlocked.size,
            completionPercentage = getCompletionPercentage(),
            totalXPFromTrophies = totalXP,
            totalPointsFromTrophies = totalPoints,
            rarityBreakdown = rarityCount,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Reset trophy manager (for testing)
     */
    suspend fun reset() {
        _unlockedTrophies.value = emptyMap()
        _trophyCollection.value = TrophyCollection()
    }
}

/**
 * Trophy collection statistics
 */
data class TrophyCollection(
    val totalTrophies: Int = 0,
    val unlockedTrophies: Int = 0,
    val completionPercentage: Float = 0f,
    val totalXPFromTrophies: Int = 0,
    val totalPointsFromTrophies: Int = 0,
    val rarityBreakdown: Map<TrophyRarity, Int> = emptyMap(),
    val lastUpdated: Long = 0
) {
    /**
     * Get count of trophies by rarity
     */
    fun getCountByRarity(rarity: TrophyRarity): Int {
        return rarityBreakdown[rarity] ?: 0
    }
}