package com.trashapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trashapp.gcms.progression.Rarity
import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.trophy.TrophyPrerequisites

/**
 * Room entity for Trophy data persistence
 */
@Entity(tableName = "trophies")
data class TrophyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val tier: String, // Store as string, convert to Tier enum
    val rarity: String, // Store as string, convert to TrophyRarity enum
    val xpReward: Int,
    val pointsReward: Int,
    val requiredLevel: Int,
    val requiredAbilities: Int,
    val requiredSkills: Int,
    val requiredPoints: Int,
    val isMilestone: Boolean,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null, // Timestamp when unlocked
    val playerId: String
)

/**
 * Room entity for Trophy Collection (player's trophy progress)
 */
@Entity(tableName = "trophy_collections")
data class TrophyCollectionEntity(
    @PrimaryKey
    val playerId: String,
    val unlockedTrophies: String, // JSON string of trophy IDs
    val totalXPEarned: Int,
    val totalPointsEarned: Int,
    val lastUpdated: Long
)

/**
 * Room entity for Trophy Progress (individual trophy progress)
 */
@Entity(tableName = "trophy_progress")
data class TrophyProgressEntity(
    @PrimaryKey
    val trophyId: String,
    val playerId: String,
    val isUnlocked: Boolean = false,
    val progress: Float = 0.0f, // 0.0 to 1.0
    val unlockedAt: Long? = null,
    val lastUpdated: Long
)

/**
 * Extension function to convert TrophyEntity to domain model
 */
fun TrophyEntity.toDomainModel(): com.trashapp.gcms.trophy.Trophy {
    return com.trashapp.gcms.trophy.Trophy(
        id = id,
        name = name,
        description = description,
        tier = Tier.valueOf(tier),
        rarity = com.trashapp.gcms.trophy.TrophyRarity.valueOf(rarity),
        xpReward = xpReward,
        pointsReward = pointsReward,
        prerequisites = TrophyPrerequisites(
            level = requiredLevel,
            abilities = if (requiredAbilities > 0) requiredAbilities else null,
            skills = if (requiredSkills > 0) requiredSkills else null,
            points = if (requiredPoints > 0) requiredPoints else null
        ),
        isMilestone = isMilestone
    )
}

/**
 * Extension function to convert domain model to TrophyEntity
 */
fun com.trashapp.gcms.trophy.Trophy.toEntity(playerId: String, isUnlocked: Boolean = false): TrophyEntity {
    return TrophyEntity(
        id = id,
        name = name,
        description = description,
        tier = tier.name,
        rarity = rarity.name,
        xpReward = xpReward,
        pointsReward = pointsReward,
        requiredLevel = prerequisites.level,
        requiredAbilities = prerequisites.abilities ?: 0,
        requiredSkills = prerequisites.skills ?: 0,
        requiredPoints = prerequisites.points ?: 0,
        isMilestone = isMilestone,
        isUnlocked = isUnlocked,
        unlockedAt = null,
        playerId = playerId
    )
}