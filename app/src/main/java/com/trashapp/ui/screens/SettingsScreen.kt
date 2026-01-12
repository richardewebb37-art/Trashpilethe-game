package com.trashapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.ui.theme.AppColors

/**
 * Settings Screen
 * Allows users to configure audio settings and game preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    audioSettings: AudioSettings,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
                        color = AppColors.Gold,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.AgedWood)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Audio Settings Section
            AudioSettingsCard(
                audioSettings = audioSettings,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Gameplay Settings Section
            GameplaySettingsCard(
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Display Settings Section
            DisplaySettingsCard(
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // About Section
            AboutCard(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AudioSettingsCard(
    audioSettings: AudioSettings,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Section Header
            Text(
                text = "🔊 AUDIO",
                color = AppColors.Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Master Volume
            VolumeSlider(
                label = "Master Volume",
                icon = "🔊",
                value = audioSettings.masterVolume,
                onValueChange = { audioSettings.setMasterVolume(it) },
                isMuted = audioSettings.isMasterMuted,
                onMuteToggle = { audioSettings.toggleMasterMute() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(color = AppColors.MediumWood.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Music Volume
            VolumeSlider(
                label = "Music Volume",
                icon = "🎵",
                value = audioSettings.musicVolume,
                onValueChange = { audioSettings.setMusicVolume(it) },
                isMuted = audioSettings.isMusicMuted,
                onMuteToggle = { audioSettings.toggleMusicMute() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sound Effects Volume
            VolumeSlider(
                label = "Sound Effects",
                icon = "🔔",
                value = audioSettings.sfxVolume,
                onValueChange = { audioSettings.setSfxVolume(it) },
                isMuted = audioSettings.isSfxMuted,
                onMuteToggle = { audioSettings.toggleSfxMute() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Test Sound Button
            Button(
                onClick = { audioSettings.playTestSound() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Gold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🔊 Test Sound",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun VolumeSlider(
    label: String,
    icon: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Text(
            text = icon,
            fontSize = 28.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        
        // Label and Slider
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label,
                    color = AppColors.Parchment,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                // Volume Percentage
                Text(
                    text = "${(if (isMuted) 0f else value * 100).toInt()}%",
                    color = AppColors.Gold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Slider(
                value = if (isMuted) 0f else value,
                onValueChange = { 
                    if (!isMuted) {
                        onValueChange(it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = AppColors.Gold,
                    activeTrackColor = AppColors.MediumWood,
                    inactiveTrackColor = AppColors.AgedWood.copy(alpha = 0.5f)
                )
            )
        }
        
        // Mute Button
        IconButton(
            onClick = onMuteToggle,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = if (isMuted) "🔇" else "🔊",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun GameplaySettingsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Section Header
            Text(
                text = "🎮 GAMEPLAY",
                color = AppColors.Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Gameplay settings
            SettingRow(
                label = "Auto-End Turn",
                value = "ON"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingRow(
                label = "Animation Speed",
                value = "Normal"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingRow(
                label = "Show Hints",
                value = "YES"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingRow(
                label = "Tutorial Mode",
                value = "OFF"
            )
        }
    }
}

@Composable
fun DisplaySettingsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Section Header
            Text(
                text = "🖼️ DISPLAY",
                color = AppColors.Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Display settings
            SettingRow(
                label = "Theme",
                value = "Wild West"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingRow(
                label = "Card Style",
                value = "Vintage"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingRow(
                label = "Particle Effects",
                value = "HIGH"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingRow(
                label = "Anti-Aliasing",
                value = "4x MSAA"
            )
        }
    }
}

@Composable
fun AboutCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Section Header
            Text(
                text = "ℹ️ ABOUT",
                color = AppColors.Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "TRASH - Wild West Card Game",
                color = AppColors.Parchment,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Version 1.0.0",
                color = AppColors.Parchment.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "A premium card game with custom engines for professional-grade graphics and audio.",
                color = AppColors.Parchment.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Built with LibGDX, Oboe, and Skia",
                color = AppColors.Parchment.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SettingRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = AppColors.Parchment,
            fontSize = 16.sp
        )
        
        Text(
            text = value,
            color = AppColors.Gold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Audio Settings Data Class
 * Holds the state for audio settings
 */
data class AudioSettings(
    var masterVolume: Float = 1.0f,
    var musicVolume: Float = 0.7f,
    var sfxVolume: Float = 0.9f,
    var isMasterMuted: Boolean = false,
    var isMusicMuted: Boolean = false,
    var isSfxMuted: Boolean = false,
    val onTestSound: () -> Unit = {}
) {
    fun setMasterVolume(value: Float) {
        masterVolume = value.coerceIn(0f, 1f)
    }
    
    fun setMusicVolume(value: Float) {
        musicVolume = value.coerceIn(0f, 1f)
    }
    
    fun setSfxVolume(value: Float) {
        sfxVolume = value.coerceIn(0f, 1f)
    }
    
    fun toggleMasterMute() {
        isMasterMuted = !isMasterMuted
    }
    
    fun toggleMusicMute() {
        isMusicMuted = !isMusicMuted
    }
    
    fun toggleSfxMute() {
        isSfxMuted = !isSfxMuted
    }
    
    fun playTestSound() {
        onTestSound()
    }
}