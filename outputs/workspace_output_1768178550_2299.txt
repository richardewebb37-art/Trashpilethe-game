package com.trashapp.gcms

import com.trashapp.audio.AudioAssetManager
import com.trashapp.gcms.commands.GCMSCommand
import com.trashapp.gcms.events.GCMSEvent
import com.trashapp.gcms.handlers.CommandHandler
import com.trashapp.gcms.models.GCMSState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GCMSController @Inject constructor(
    private val handlers: List<CommandHandler>
) {
    private var audioManager: AudioAssetManager? = null
    
    private val _currentState = MutableSharedFlow<GCMSState>()
    val currentState: SharedFlow<GCMSState> = _currentState.asSharedFlow()
    
    private val _events = MutableSharedFlow<GCMSEvent>()
    val events: SharedFlow<GCMSEvent> = _events.asSharedFlow()
    
    /**
     * Set the audio manager for sound effects
     */
    fun setAudioManager(audioManager: AudioAssetManager) {
        this.audioManager = audioManager
    }
    
    suspend fun submitCommand(command: GCMSCommand, currentState: GCMSState): Result<Unit> {
        val handler = handlers.find { it.canHandle(command) }
            ?: return Result.failure(IllegalArgumentException("No handler found for command"))
        
        return handler.handle(command, currentState).fold(
            onSuccess = { events ->
                events.forEach { event ->
                    emitEvent(event)
                    // Play sound effects based on event type
                    playEventSound(event)
                }
                Result.success(Unit)
            },
            onFailure = { error ->
                emitErrorEvent(error, command, currentState)
                Result.failure(error)
            }
        )
    }
    
    private suspend fun emitEvent(event: GCMSEvent) {
        _events.emit(event)
    }
    
    private fun playEventSound(event: GCMSEvent) {
        audioManager?.let { manager ->
            when (event) {
                is com.trashapp.gcms.events.CardDrawnEvent -> {
                    manager.playSound(AudioAssetManager.SOUND_CARD_DEAL)
                }
                is com.trashapp.gcms.events.CardPlacedEvent -> {
                    manager.playSound(AudioAssetManager.SOUND_CARD_PLACE)
                }
                is com.trashapp.gcms.events.CardFlippedEvent -> {
                    manager.playSound(AudioAssetManager.SOUND_CARD_FLIP)
                }
                is com.trashapp.gcms.events.RoundStartedEvent -> {
                    manager.playSound(AudioAssetManager.SOUND_SHUFFLE)
                }
                is com.trashapp.gcms.events.MatchStartedEvent -> {
                    manager.playSound(AudioAssetManager.SOUND_SHUFFLE)
                }
                is com.trashapp.gcms.events.MatchEndedEvent -> {
                    val winner = event.winnerId
                    if (winner != null) {
                        manager.playSound(AudioAssetManager.SOUND_WIN)
                    } else {
                        manager.playSound(AudioAssetManager.SOUND_LOSE)
                    }
                }
                else -> {
                    // No sound for other events
                }
            }
        }
    }
    
    private suspend fun emitErrorEvent(error: Throwable, command: GCMSCommand, state: GCMSState) {
        // Emit error event for UI to handle
        val errorEvent = com.trashapp.gcms.events.ErrorEvent(
            playerId = (command as? com.trashapp.gcms.commands.GCMSCommand)?.let { 
                when(it) {
                    is com.trashapp.gcms.commands.DrawCardCommand -> it.playerId
                    is com.trashapp.gcms.commands.PlaceCardCommand -> it.playerId
                    is com.trashapp.gcms.commands.FlipCardCommand -> it.playerId
                    is com.trashapp.gcms.commands.StartMatchCommand -> it.playerId
                    else -> "unknown"
                }
            } ?: "unknown",
            error = error.message ?: "Unknown error",
            command = command::class.simpleName ?: "Unknown command"
        )
        _events.emit(errorEvent)
    }
}