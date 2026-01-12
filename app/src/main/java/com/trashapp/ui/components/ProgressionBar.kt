package com.trashapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.ui.theme.AppColors

/**
 * Progression Bar Component
 * Shows XP progress to next level
 */
@Composable
fun ProgressionBar(
    progress: Float, // 0.0 to 1.0
    currentXP: Int,
    xpToNext: Int,
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    Box(
        modifier = modifier
            .background(
                color = AppColors.AgedWood.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = AppColors.Gold.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            AppColors.Gold,
                            AppColors.Brass
                        )
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            if (showText) {
                Text(
                    text = "$currentXP / $xpToNext XP",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * XP Progress Bar with Visual Progress
 */
@Composable
fun XPProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = AppColors.Gold,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            AppColors.Gold,
                            AppColors.Brass
                        )
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
        ) {
            val clampedProgress = progress.coerceIn(0f, 1f)
            val progressText = "${(clampedProgress * 100).toInt()}%"
            
            Text(
                text = progressText,
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = (100 * clampedProgress).toInt().dp,
                    vertical = 6.dp
                )
            )
        }
    }
}