package com.trashapp.gcms.models

import com.trashapp.gcms.progression.PointSystem
import com.trashapp.gcms.progression.ProgressionTree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Player(
    val id: String,
    val name: String,
    val hand: List<Card> = emptyList(),
    val isCurrentTurn: Boolean = false,
    val score: Int = 0,
    val pointSystem: PointSystem = PointSystem.createInitial(),
    val progressionTree: ProgressionTree = ProgressionTree.createDefault()
) {
    /**
     * Get player's current level
     */
    val level: Int
        get() = pointSystem.currentLevel
    
    /**
     * Get player's available points
     */
    val availablePoints: Int
        get() = pointSystem.availablePoints
    
    /**
     * Check if player has reached max level ceiling
     */
    val isAtMaxLevel: Boolean
        get() = pointSystem.currentLevel >= progressionTree.calculateDynamicLevelCeiling()
}

data class GameBoard(
    val deck: Deck = Deck(),
    val discardPile: List<Card> = emptyList(),
    val slots: List<Card?> = List(10) { null }, // 10 playing slots
    val currentRound: Int = 1,
    val totalRounds: Int = 10
)

data class GCMSState(
    val players: List<Player>,
    val currentPlayerIndex: Int = 0,
    val gameBoard: GameBoard = GameBoard(),
    val gameStatus: GameStatus = GameStatus.WAITING_TO_START,
    val matchScore: Int = 0
) {
    val currentPlayer: Player
        get() = players[currentPlayerIndex]
    
    val isGameActive: Boolean
        get() = gameStatus == GameStatus.IN_PROGRESS
    
    val isRoundComplete: Boolean
        get() = gameStatus == GameStatus.ROUND_COMPLETE
    
    /**
     * Update player's progression state
     */
    fun updatePlayerProgression(playerId: String, newPointSystem: PointSystem, newProgressionTree: ProgressionTree): GCMSState {
        val updatedPlayers = players.map { player ->
            if (player.id == playerId) {
                player.copy(
                    pointSystem = newPointSystem,
                    progressionTree = newProgressionTree
                )
            } else {
                player
            }
        }
        return copy(players = updatedPlayers)
    }
    
    companion object {
        fun createInitialState(playerNames: List<String>): GCMSState {
            val players = playerNames.mapIndexed { index, name ->
                Player(
                    id = "player_$index",
                    name = name,
                    pointSystem = PointSystem.createInitial(),
                    progressionTree = ProgressionTree.createDefault()
                )
            }
            return GCMSState(
                players = players,
                gameBoard = GameBoard()
            )
        }
    }
}

enum class GameStatus {
    WAITING_TO_START,
    IN_PROGRESS,
    ROUND_COMPLETE,
    MATCH_COMPLETE,
    PAUSED
}