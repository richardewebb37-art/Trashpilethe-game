package com.trashapp.gcms.handlers

import com.trashapp.audio.AudioAssetManager
import com.trashapp.gcms.commands.*
import com.trashapp.gcms.events.*
import com.trashapp.gcms.models.GCMSState

class CardCommandHandler(
    private val audioManager: AudioAssetManager? = null
) : CommandHandler {
    
    override fun canHandle(command: GCMSCommand): Boolean {
        return command is DrawCardCommand ||
               command is PlaceCardCommand ||
               command is FlipCardCommand ||
               command is DiscardCardCommand
    }
    
    override suspend fun handle(command: GCMSCommand, state: GCMSState): Result<List<GCMSEvent>> {
        return when (command) {
            is DrawCardCommand -> handleDrawCard(command, state)
            is PlaceCardCommand -> handlePlaceCard(command, state)
            is FlipCardCommand -> handleFlipCard(command, state)
            is DiscardCardCommand -> handleDiscardCard(command, state)
            else -> Result.failure(IllegalArgumentException("Unknown card command"))
        }
    }
    
    private fun handleDrawCard(cmd: DrawCardCommand, state: GCMSState): Result<List<GCMSEvent>> {
        // Validation
        if (!canDrawCard(state, cmd.playerId)) {
            return Result.failure(IllegalStateException("Cannot draw card - not your turn"))
        }
        
        if (state.gameBoard.deck.isEmpty) {
            return Result.failure(IllegalStateException("Deck is empty"))
        }
        
        // Execute
        val card = state.gameBoard.deck.draw()
            ?: return Result.failure(IllegalStateException("Failed to draw card"))
        
        // Play card deal sound effect
        audioManager?.playSound(AudioAssetManager.SOUND_CARD_DEAL)
        
        val events = listOf(
            CardDrawnEvent(cmd.playerId, card, cmd.fromPile.name),
            StateChangedEvent(state.toString()) // In real impl, serialize properly
        )
        
        return Result.success(events)
    }
    
    private fun handlePlaceCard(cmd: PlaceCardCommand, state: GCMSState): Result<List<GCMSEvent>> {
        // Find the card in player's hand
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val card = player.hand.find { it.id == cmd.card }
            ?: return Result.failure(IllegalStateException("Card not found in hand"))
        
        // Validate slot
        if (cmd.slotIndex < 0 || cmd.slotIndex >= 10) {
            return Result.failure(IllegalArgumentException("Invalid slot index"))
        }
        
        // Play card placement sound effect
        audioManager?.playSound(AudioAssetManager.SOUND_CARD_PLACE)
        
        val events = listOf(
            CardPlacedEvent(cmd.playerId, card, cmd.slotIndex),
            StateChangedEvent(state.toString())
        )
        
        return Result.success(events)
    }
    
    private fun handleFlipCard(cmd: FlipCardCommand, state: GCMSState): Result<List<GCMSEvent>> {
        // Validate slot
        if (cmd.slotIndex < 0 || cmd.slotIndex >= 10) {
            return Result.failure(IllegalArgumentException("Invalid slot index"))
        }
        
        // Get card from slot
        val card = state.gameBoard.slots[cmd.slotIndex]
            ?: return Result.failure(IllegalStateException("No card in slot"))
        
        // Play card flip sound effect
        audioManager?.playSound(AudioAssetManager.SOUND_CARD_FLIP)
        
        val events = listOf(
            CardFlippedEvent(cmd.playerId, card, cmd.slotIndex),
            StateChangedEvent(state.toString())
        )
        
        return Result.success(events)
    }
    
    private fun handleDiscardCard(cmd: DiscardCardCommand, state: GCMSState): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == cmd.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val card = player.hand.find { it.id == cmd.card }
            ?: return Result.failure(IllegalStateException("Card not found in hand"))
        
        // Play card discard sound effect
        audioManager?.playSound(AudioAssetManager.SOUND_CARD_PLACE)
        
        val events = listOf(
            CardDiscardedEvent(cmd.playerId, card),
            StateChangedEvent(state.toString())
        )
        
        return Result.success(events)
    }
    
    private fun canDrawCard(state: GCMSState, playerId: String): Boolean {
        return state.currentPlayer.id == playerId && state.isGameActive
    }
}