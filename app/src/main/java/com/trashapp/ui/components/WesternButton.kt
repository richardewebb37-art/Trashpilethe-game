package com.trashapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.audio.AudioAssetManager
import com.trashapp.ui.theme.AppColors

@Composable
fun WesternButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    audioManager: AudioAssetManager? = null
) {
    Button(
        onClick = {
            // Play button click sound
            audioManager?.playSound(AudioAssetManager.SOUND_BUTTON_CLICK)
            // Execute the original onClick
            onClick()
        },
        modifier = modifier
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.Gold,
                        AppColors.Brass
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.ButtonPrimary,
            disabledContainerColor = AppColors.ButtonPrimary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Text(
                text = text.uppercase(),
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                color = AppColors.TextOnButton,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun WesternSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    audioManager: AudioAssetManager? = null
) {
    Button(
        onClick = {
            // Play button click sound
            audioManager?.playSound(AudioAssetManager.SOUND_BUTTON_CLICK)
            // Execute the original onClick
            onClick()
        },
        modifier = modifier
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.Brass,
                        AppColors.Gold
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.ButtonSecondary,
            disabledContainerColor = AppColors.ButtonSecondary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Text(
                text = text.uppercase(),
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                color = AppColors.TextOnButton,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}