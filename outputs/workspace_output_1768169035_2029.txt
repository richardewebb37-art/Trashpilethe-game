package com.trashapp.libgdx

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.trashapp.oboe.AudioEngine
import com.trashapp.skia.GraphicsEngine
import com.trashapp.gcms.GCMSController
import com.trashapp.gcms.models.GCMSState
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrashGame @Inject constructor(
    private val gcmsController: GCMSController
) : ApplicationAdapter() {
    
    private val audioEngine = AudioEngine.getInstance()
    private val graphicsEngine = GraphicsEngine.getInstance()
    
    private var gameLoopScope: CoroutineScope? = null
    private var gameState: GCMSState? = null
    private var lastFrameTime = 0f
    
    override fun create() {
        audioEngine.initialize()
        audioEngine.start()
        
        graphicsEngine.initialize(Gdx.graphics.width, Gdx.graphics.height)
        graphicsEngine.setWildWestTheme()
        graphicsEngine.enableWoodGrainEffect(true)
        graphicsEngine.enableVintageEffect(true)
        
        gameState = GCMSState.createInitialState(listOf(&quot;Player 1&quot;, &quot;Player 2&quot;))
        
        gameLoopScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        startGameLoop()
        
        audioEngine.playSound(&quot;game_start&quot;)
        
        graphicsEngine.addParticleEffect(&quot;fire_spark&quot;, Gdx.graphics.width / 2f, Gdx.graphics.height / 2f)
    }
    
    override fun render() {
        val deltaTime = Gdx.graphics.deltaTime
        lastFrameTime = Gdx.graphics.rawDeltaTime
        
        graphicsEngine.clearScreen(0.24f, 0.15f, 0.14f, 1.0f)
        
        graphicsEngine.updateParticles(deltaTime)
        
        renderGameState()
        
        graphicsEngine.render()
        
        processGCMS()
    }
    
    override fun resize(width: Int, height: Int) {
        graphicsEngine.resize(width, height)
    }
    
    override fun pause() {
        audioEngine.stop()
        gameLoopScope?.cancel()
    }
    
    override fun resume() {
        audioEngine.start()
        startGameLoop()
    }
    
    override fun dispose() {
        gameLoopScope?.cancel()
        audioEngine.stop()
        audioEngine.release()
        graphicsEngine.release()
    }
    
    private fun startGameLoop() {
        gameLoopScope?.launch {
            while (isActive) {
                updateGameLogic()
                delay(16)
            }
        }
    }
    
    private fun updateGameLogic() {
    }
    
    private fun renderGameState() {
        val state = gameState ?: return
        
        renderCards(state)
        
        renderUI(state)
    }
    
    private fun renderCards(state: GCMSState) {
        val cardWidth = Gdx.graphics.width * 0.15f
        val cardHeight = cardWidth * 1.4f
        
        state.currentPlayer.hand.forEachIndexed { index, card ->
            val x = Gdx.graphics.width * 0.1f + index * (cardWidth + 10f)
            val y = Gdx.graphics.height * 0.1f
            graphicsEngine.renderCardBack(x, y, cardWidth, cardHeight)
        }
        
        state.players.firstOrNull()?.hand?.forEachIndexed { index, card ->
            val x = Gdx.graphics.width * 0.1f + index * (cardWidth + 10f)
            val y = Gdx.graphics.height * 0.7f
            graphicsEngine.renderCard(
                x, y, cardWidth, cardHeight,
                card.suit.displayName,
                card.rank.displayName,
                true
            )
        }
        
        val discardPile = state.gameBoard.discardPile
        if (discardPile.isNotEmpty()) {
            val topCard = discardPile.last()
            graphicsEngine.renderCard(
                Gdx.graphics.width * 0.4f,
                Gdx.graphics.height * 0.4f,
                cardWidth,
                cardHeight,
                topCard.suit.displayName,
                topCard.rank.displayName,
                true
            )
        }
    }
    
    private fun renderUI(state: GCMSState) {
    }
    
    private fun processGCMS() {
    }
    
    fun submitGCMSCommand(command: com.trashapp.gcms.commands.GCMSCommand) {
        val state = gameState ?: return
        
        CoroutineScope(Dispatchers.IO).launch {
            gcmsController.submitCommand(command, state)
        }
    }
    
    fun updateGameState(newState: GCMSState) {
        gameState = newState
    }
}