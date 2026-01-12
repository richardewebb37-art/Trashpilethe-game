package com.trashapp.gcms.models

enum class Suit(val symbol: String, val displayName: String) {
    SPADES("♠", "Sheriff Stars"),
    HEARTS("♥", "Horseshoes"),
    CLUBS("♣", "Cactus"),
    DIAMONDS("♦", "Gold Nuggets")
}

enum class Rank(val value: Int, val displayName: String) {
    ACE(1, "A"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K"),
    JOKER(14, "Joker")
}

data class Card(
    val suit: Suit,
    val rank: Rank,
    val id: String = "${suit.name}_${rank.name}_${hashCode()}"
) {
    val isFaceUp: Boolean = false
    val isPlayable: Boolean = false
    
    fun flip(): Card = copy()
    
    fun canPlayOn(targetCard: Card?): Boolean {
        return targetCard == null || rank.value == targetCard.rank.value + 1
    }
    
    companion object {
        fun createDeck(): List<Card> {
            val deck = mutableListOf<Card>()
            for (suit in Suit.values()) {
                for (rank in Rank.values().filter { it != Rank.JOKER }) {
                    deck.add(Card(suit, rank))
                }
            }
            // Add 2 Jokers
            deck.add(Card(Suit.SPADES, Rank.JOKER))
            deck.add(Card(Suit.HEARTS, Rank.JOKER))
            return deck
        }
    }
}