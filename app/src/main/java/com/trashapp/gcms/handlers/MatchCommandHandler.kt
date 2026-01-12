package com.trashapp.gcms.handlers

import com.trashapp.gcms.commands.*
import com.trashapp.gcms.events.*
import com.trashapp.gcms.models.*

class MatchCommandHandler : CommandHandler {
    
    override fun canHandle(command: GCMSCommand): Boolean {
        return command is StartMatchCommand ||
               command is EndMatchCommand ||
               command is StartRoundCommand ||
               command is EndRoundCommand
    }
    
    override suspend fun handle(command: GCMSCommand, state: GCMSState): Result<List<GCMSEvent>> {
        return when (command) {
            is StartMatchCommand -> handleStartMatch(command, state)
            is EndMatchCommand -> handleEndMatch(command, state)
            is StartRoundCommand -> handleStartRound(command, state)
            is EndRoundCommand -> handleEndRound(command, state)
            else -> Result.failure(IllegalArgumentException("Unknown match command"))
        }
    }
    
    private fun handleStartMatch(cmd: StartMatchCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val initialState = GCMSState.createInitialState(cmd.playerNames)
        
        val events = listOf(
            MatchStartedEvent(cmd.playerNames),
            RoundStartedEvent(1),
            StateChangedEvent(initialState.toString())
        )
        
        return Result.success(events)
    }
    
    private fun handleEndMatch(cmd: EndMatchCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val events = listOf(
            MatchEndedEvent(cmd.winnerId, cmd.finalScore),
            StateChangedEvent(state.toString())
        )
        
        return Result.success(events)
    }
    
    private fun handleStartRound(cmd: StartRoundCommand, state: GCMSState): Result<List<GCMSEvent>> {
        if (cmd.roundNumber > state.gameBoard.totalRounds) {
            return Result.failure(IllegalStateException("Cannot start round - match complete"))
        }
        
        val events = listOf(
            RoundStartedEvent(cmd.roundNumber),
            TurnStartedEvent(state.currentPlayer.id, cmd.roundNumber),
            StateChangedEvent(state.toString())
        )
        
        return Result.success(events)
    }
    
    private fun handleEndRound(cmd: EndRoundCommand, state: GCMSState): Result<List<GCMSEvent>> {
        // Calculate scores for this round
        val scores = state.players.associate { player ->
            player.id to calculateRoundScore(player, state)
        }
        
        val winnerId = scores.maxByOrNull { it.value }?.key
        
        val events = listOf(
            RoundEndedEvent(
                roundNumber = state.gameBoard.currentRound,
                winnerId = winnerId,
                scores = scores
            ),
            StateChangedEvent(state.toString())
        )
        
        return Result.success(events)
    }
    
    private fun calculateRoundScore(player: Player, state: GCMSState): Int {
        // Basic scoring - will be enhanced with your rules
        var score = 0
        
        // Count face-up cards
        val faceUpCards = state.gameBoard.slots.count { it != null }
        score += faceUpCards * 5
        
        // Add player's current score
        score += player.score
        
        return score
    }
}