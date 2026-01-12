package com.trashapp.gcms.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Random

class Deck(private val cards: MutableList<Card> = Card.createDeck().toMutableList()) {
    
    private val random = Random()
    
    val size: Int
        get() = cards.size
    
    val isEmpty: Boolean
        get() = cards.isEmpty()
    
    fun draw(): Card? {
        return if (cards.isNotEmpty()) {
            cards.removeAt(cards.size - 1)
        } else {
            null
        }
    }
    
    fun drawMultiple(count: Int): List<Card> {
        val drawn = mutableListOf<Card>()
        repeat(count) {
            draw()?.let { drawn.add(it) }
        }
        return drawn
    }
    
    fun peek(): Card? {
        return cards.lastOrNull()
    }
    
    fun addCard(card: Card) {
        cards.add(0, card)
    }
    
    fun addCards(cardsToAdd: List<Card>) {
        cardsToAdd.reversed().forEach { addCard(it) }
    }
    
    suspend fun shuffle() {
        withContext(Dispatchers.Default) {
            // Fisher-Yates shuffle algorithm
            for (i in cards.size - 1 downTo 1) {
                val j = random.nextInt(i + 1)
                cards[i] = cards[j].also { cards[j] = cards[i] }
            }
        }
    }
    
    fun reset() {
        cards.clear()
        cards.addAll(Card.createDeck())
    }
    
    fun toList(): List<Card> = cards.toList()
}

object DeckBuilder {
    fun createDeck(shuffled: Boolean = false): Deck {
        val deck = Deck()
        return if (shuffled) {
            // Shuffle will be called separately via coroutine
            deck
        } else {
            deck
        }
    }
}