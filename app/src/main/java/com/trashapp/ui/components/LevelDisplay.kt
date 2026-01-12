package com.trashapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.ui.theme.AppColors

/**
 * Level Display Component
 * Shows the player's current level with Wild West styling
 */
@Composable
fun LevelDisplay(
    level: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.MediumWood,
                        AppColors.AgedWood
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = AppColors.Gold,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐",
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "LVL $level",
                color = AppColors.Gold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Level Display with XP Bar
 */
@Composable
fun LevelDisplayWithXP(
    level: Int,
    currentXP: Int,
    xpToNext: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.MediumWood,
                        AppColors.AgedWood
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = AppColors.Gold,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐",
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column {
                Text(
                    text = "Level $level",
                    color = AppColors.Gold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "$currentXP / $xpToNext XP",
                        color = AppColors.Parchment,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}