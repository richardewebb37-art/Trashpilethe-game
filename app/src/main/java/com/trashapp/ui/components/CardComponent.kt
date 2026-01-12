package com.trashapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.gcms.models.Card
import com.trashapp.ui.theme.AppColors

@Composable
fun PlayingCard(
    card: Card,
    isFaceUp: Boolean = true,
    isPlayable: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(2.5f / 3.5f)
            .clickable(onClick = onClick, enabled = isPlayable),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFaceUp) {
                AppColors.CardFace
            } else {
                AppColors.CardBack
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isPlayable) {
                AppColors.Gold
            } else {
                AppColors.CardBorder
            }
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            if (isFaceUp) {
                CardFaceContent(card)
            } else {
                CardBackContent()
            }
        }
    }
}

@Composable
private fun CardFaceContent(card: Card) {
    val color = when (card.suit) {
        com.trashapp.gcms.models.Suit.HEARTS,
        com.trashapp.gcms.models.Suit.DIAMONDS -> Color.Red
        else -> Color.Black
    }
    
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = "${card.rank.displayName}${card.suit.symbol}",
            color = color,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CardBackContent() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🤠",
            fontSize = 32.sp
        )
    }
}