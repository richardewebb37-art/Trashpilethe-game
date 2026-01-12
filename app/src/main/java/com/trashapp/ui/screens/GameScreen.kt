package com.trashapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.gcms.models.Card
import com.trashapp.ui.components.CardComponent
import com.trashapp.ui.components.WesternButton
import com.trashapp.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ROUND 3/10",
                            color = AppColors.Gold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MATCH SCORE: 245",
                            color = AppColors.Parchment
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = AppColors.Gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.MediumWood
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.AgedWood)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Opponent Hand (Face-down)
                Text(
                    text = "OPPONENT",
                    color = AppColors.Parchment,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(8) {
                        OpponentCard()
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Game Board
                GameBoard()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Your Hand
                Text(
                    text = "YOUR HAND",
                    color = AppColors.Gold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(getSampleCards()) { card ->
                        CardComponent(
                            card = card,
                            isFaceUp = true,
                            isPlayable = true,
                            onClick = { /* Handle card click */ }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Game Status
                Text(
                    text = "YOUR TURN - Draw a card",
                    color = AppColors.Gold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OpponentCard() {
    CardComponent(
        card = Card(com.trashapp.gcms.models.Suit.SPADES, com.trashapp.gcms.models.Rank.ACE),
        isFaceUp = false,
        isPlayable = false
    )
}

@Composable
private fun GameBoard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = AppColors.MediumWood.copy(alpha = 0.5f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Deck and Discard Piles
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Deck
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CardComponent(
                        card = Card(com.trashapp.gcms.models.Suit.SPADES, com.trashapp.gcms.models.Rank.ACE),
                        isFaceUp = false
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "DECK 42",
                        color = AppColors.Parchment,
                        fontSize = 12.sp
                    )
                }
                
                // Discard Pile
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CardComponent(
                        card = Card(com.trashapp.gcms.models.Suit.CLUBS, com.trashapp.gcms.models.Rank.FIVE),
                        isFaceUp = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "DISCARD",
                        color = AppColors.Parchment,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Playing Slots (simplified for now)
            Text(
                text = "PLAYING AREA",
                color = AppColors.Gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getSampleCards(): List<Card> {
    return listOf(
        Card(com.trashapp.gcms.models.Suit.SPADES, com.trashapp.gcms.models.Rank.ACE),
        Card(com.trashapp.gcms.models.Suit.HEARTS, com.trashapp.gcms.models.Rank.TWO),
        Card(com.trashapp.gcms.models.Suit.DIAMONDS, com.trashapp.gcms.models.Rank.FOUR),
        Card(com.trashapp.gcms.models.Suit.CLUBS, com.trashapp.gcms.models.Rank.SIX),
        Card(com.trashapp.gcms.models.Suit.SPADES, com.trashapp.gcms.models.Rank.EIGHT),
        Card(com.trashapp.gcms.models.Suit.HEARTS, com.trashapp.gcms.models.Rank.TEN),
        Card(com.trashapp.gcms.models.Suit.DIAMONDS, com.trashapp.gcms.models.Rank.JACK),
        Card(com.trashapp.gcms.models.Suit.CLUBS, com.trashapp.gcms.models.Rank.QUEEN),
        Card(com.trashapp.gcms.models.Suit.SPADES, com.trashapp.gcms.models.Rank.KING),
        Card(com.trashapp.gcms.models.Suit.HEARTS, com.trashapp.gcms.models.Rank.ACE)
    )
}