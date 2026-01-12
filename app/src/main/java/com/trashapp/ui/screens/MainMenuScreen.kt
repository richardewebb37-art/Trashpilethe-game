package com.trashapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.ui.components.WesternButton
import com.trashapp.ui.components.WesternSecondaryButton
import com.trashapp.ui.theme.AppColors

@Composable
fun MainMenuScreen(
    onPlayClick: () -> Unit,
    onQuickMatchClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppColors.AgedWood
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.SepiaDark,
                            AppColors.AgedWood,
                            AppColors.MediumWood
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo/Title
                Text(
                    text = "TRASH",
                    style = MaterialTheme.typography.displayLarge,
                    color = AppColors.Gold,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Wild West Card Game",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppColors.Parchment,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Play Button
                WesternButton(
                    text = "Play Game",
                    onClick = onPlayClick,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick Match Button
                WesternSecondaryButton(
                    text = "Quick Match (5)",
                    onClick = onQuickMatchClick,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Multiplayer Button
                WesternSecondaryButton(
                    text = "Multiplayer",
                    onClick = onMultiplayerClick,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Bottom Buttons
                RowOfButtons(
                    onProfileClick = onProfileClick,
                    onSettingsClick = onSettingsClick
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Player Info
                PlayerInfoBar()
            }
        }
    }
}

@Composable
private fun RowOfButtons(
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WesternSecondaryButton(
            text = "Profile",
            onClick = onProfileClick,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        
        WesternSecondaryButton(
            text = "Settings",
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}

@Composable
private fun PlayerInfoBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AppColors.MediumWood.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Player: COWBOY_ACE",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.Gold,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Level: 7  |  SP: 247  |  AP: 183",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Parchment
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "XP: 1,450 / 1,750",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.Parchment.copy(alpha = 0.8f)
            )
        }
    }
}