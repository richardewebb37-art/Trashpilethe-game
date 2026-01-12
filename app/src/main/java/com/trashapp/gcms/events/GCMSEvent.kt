package com.trashapp.gcms.events

import com.trashapp.gcms.models.Card

sealed class GCMSEvent {
    abstract val timestamp: Long
}

// Card Events
data class CardDrawnEvent(
    val playerId: String,
    val card: Card,
    val fromPile: String,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class CardPlacedEvent(
    val playerId: String,
    val card: Card,
    val slotIndex: Int,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class CardFlippedEvent(
    val playerId: String,
    val card: Card,
    val slotIndex: Int,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class CardDiscardedEvent(
    val playerId: String,
    val card: Card,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

// Game Flow Events
data class TurnStartedEvent(
    val playerId: String,
    val turnNumber: Int,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class TurnEndedEvent(
    val playerId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class RoundStartedEvent(
    val roundNumber: Int,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class RoundEndedEvent(
    val roundNumber: Int,
    val winnerId: String?,
    val scores: Map<String, Int>,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class MatchStartedEvent(
    val playerNames: List<String>,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class MatchEndedEvent(
    val winnerId: String,
    val finalScore: Int,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

// Skill Events
data class SkillUsedEvent(
    val playerId: String,
    val skillId: String,
    val effect: String,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class AbilityUsedEvent(
    val playerId: String,
    val abilityId: String,
    val effect: String,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

// State Events
data class StateChangedEvent(
    val stateSnapshot: String, // JSON serialized state
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class GamePausedEvent(
    val playerId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

data class GameResumedEvent(
    val playerId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()

// Error Events
data class ErrorEvent(
    val playerId: String,
    val error: String,
    val command: String,
    override val timestamp: Long = System.currentTimeMillis()
) : GCMSEvent()