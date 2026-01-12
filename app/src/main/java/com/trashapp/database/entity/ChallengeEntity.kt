package com.trashapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trashapp.gcms.challenge.ChallengeRequirements
import com.trashapp.gcms.challenge.ChallengeType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room entity for Challenge data persistence
 */
@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey
    val id: String,
    val level: Int,
    val description: String,
    val type: String, // Store as string, convert to ChallengeType enum
    val xpReward: Int,
    val pointsReward: Int,
    val unlocksLevel: Boolean,
    
    // Challenge requirements
    val requiredScore: Int?,
    val requiredAbilities: Int?,
    val requiredSkills: Int?,
    val requiredPoints: Int?,
    val requiredCombos: Int?,
    val requiredStreaks: Int?,
    val requiredCardsPlayed: Int?,
    val requiredRoundWins: Int?,
    val requiredMatchWins: Int?,
    val timeLimit: Int?,
    
    val isMilestone: Boolean,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null, // Timestamp when completed
    val playerId: String
)

/**
 * Room entity for Challenge Progress (player's challenge progress)
 */
@Entity(tableName = "challenge_progress")
data class ChallengeProgressEntity(
    @PrimaryKey
    val challengeId: String,
    val playerId: String,
    
    // Current progress values
    val currentScore: Int = 0,
    val currentAbilities: Int = 0,
    val currentSkills: Int = 0,
    val currentPoints: Int = 0,
    val currentCombos: Int = 0,
    val currentStreaks: Int = 0,
    val currentCardsPlayed: Int = 0,
    val currentRoundWins: Int = 0,
    val currentMatchWins: Int = 0,
    
    val isCompleted: Boolean = false,
    val progress: Float = 0.0f, // 0.0 to 1.0
    val completedAt: Long? = null,
    val lastUpdated: Long
)

/**
 * Room entity for Level Challenges (challenges for a specific level)
 */
@Entity(tableName = "level_challenges")
data class LevelChallengesEntity(
    @PrimaryKey
    val level: Int,
    val playerId: String,
    val challengeIds: String, // JSON string of challenge IDs
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val canAdvance: Boolean = false,
    val lastUpdated: Long
)

/**
 * Room entity for Level Progress (overall progress for a level)
 */
@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey
    val level: Int,
    val playerId: String,
    val totalXP: Int = 0,
    val requiredXP: Int = 0,
    val points: Int = 0,
    val requiredPoints: Int = 0,
    val abilitiesCount: Int = 0,
    val requiredAbilities: Int = 0,
    val skillsCount: Int = 0,
    val requiredSkills: Int = 0,
    val completedChallenges: String, // JSON string of challenge IDs
    val inProgressChallenges: String, // JSON string of challenge IDs
    val notStartedChallenges: String, // JSON string of challenge IDs
    val totalChallenges: Int = 0,
    val canAdvanceToNextLevel: Boolean = false,
    val lastUpdated: Long
)

/**
 * Extension function to convert ChallengeEntity to domain model
 */
fun ChallengeEntity.toDomainModel(): com.trashapp.gcms.challenge.Challenge {
    return com.trashapp.gcms.challenge.Challenge(
        id = id,
        level = level,
        description = description,
        type = ChallengeType.valueOf(type),
        xpReward = xpReward,
        pointsReward = pointsReward,
        unlocksLevel = unlocksLevel,
        requirements = ChallengeRequirements(
            score = requiredScore,
            abilities = requiredAbilities,
            skills = requiredSkills,
            points = requiredPoints,
            combos = requiredCombos,
            streaks = requiredStreaks,
            cardsPlayed = requiredCardsPlayed,
            roundWins = requiredRoundWins,
            matchWins = requiredMatchWins,
            timeLimit = timeLimit
        ),
        isMilestone = isMilestone
    )
}

/**
 * Extension function to convert domain model to ChallengeEntity
 */
fun com.trashapp.gcms.challenge.Challenge.toEntity(playerId: String, isCompleted: Boolean = false): ChallengeEntity {
    return ChallengeEntity(
        id = id,
        level = level,
        description = description,
        type = type.name,
        xpReward = xpReward,
        pointsReward = pointsReward,
        unlocksLevel = unlocksLevel,
        requiredScore = requirements.score,
        requiredAbilities = requirements.abilities,
        requiredSkills = requirements.skills,
        requiredPoints = requirements.points,
        requiredCombos = requirements.combos,
        requiredStreaks = requirements.streaks,
        requiredCardsPlayed = requirements.cardsPlayed,
        requiredRoundWins = requirements.roundWins,
        requiredMatchWins = requirements.matchWins,
        timeLimit = requirements.timeLimit,
        isMilestone = isMilestone,
        isCompleted = isCompleted,
        completedAt = null,
        playerId = playerId
    )
}

/**
 * Extension function to convert ChallengeProgressEntity to domain model
 */
fun ChallengeProgressEntity.toDomainModel(challengeEntity: ChallengeEntity): com.trashapp.gcms.challenge.Challenge {
    val challenge = challengeEntity.toDomainModel()
    // Note: Challenge doesn't have current progress fields in domain model
    // Progress is managed separately in ChallengeManager
    return challenge
}