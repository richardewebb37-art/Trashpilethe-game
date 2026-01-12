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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.gcms.models.GCMSState
import com.trashapp.ui.components.*
import com.trashapp.ui.theme.AppColors

/**
 * Progression Screen
 * Main screen for viewing and purchasing abilities and skills
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressionScreen(
    onBackClick: () -> Unit,
    gameState: GCMSState,
    currentPlayerId: String,
    onBuyAbility: (String) -> Unit,
    onBuySkill: (String) -> Unit,
    onUpgradeAbility: (String) -> Unit,
    onLevelUpSkill: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val player = gameState.players.find { it.id == currentPlayerId }
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PROGRESSION",
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
        if (player != null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(AppColors.AgedWood)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Player Stats Header
                PlayerStatsHeader(
                    player = player,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Abilities Section
                AbilitiesSection(
                    player = player,
                    onBuyAbility = onBuyAbility,
                    onUpgradeAbility = onUpgradeAbility,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Skills Section
                SkillsSection(
                    player = player,
                    onBuySkill = onBuySkill,
                    onLevelUpSkill = onLevelUpSkill,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PlayerStatsHeader(
    player: com.trashapp.gcms.models.Player,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.MediumWood.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Player Name
            Text(
                text = player.name,
                color = AppColors.Gold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Level and Points Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LevelDisplayWithXP(
                    level = player.level,
                    currentXP = player.pointSystem.totalXP,
                    xpToNext = player.pointSystem.xpToNextLevel(),
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                PointsDisplayWithLabel(
                    points = player.availablePoints,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // XP Progress Bar
            XPProgressBar(
                progress = player.pointSystem.progressToNextLevel(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progression Status
            Text(
                text = if (player.pointSystem.isProgressionActive) {
                    "Progression Active"
                } else {
                    "Purchase abilities/skills to start earning XP"
                },
                color = if (player.pointSystem.isProgressionActive) {
                    Color(0xFF4CAF50)
                } else {
                    AppColors.Parchment.copy(alpha = 0.7f)
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AbilitiesSection(
    player: com.trashapp.gcms.models.Player,
    onBuyAbility: (String) -> Unit,
    onUpgradeAbility: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.MediumWood.copy(alpha = 0.9f)
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
                text = "⚔️ ABILITIES",
                color = AppColors.Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Abilities List
            val abilities = player.progressionTree.abilities.values.toList()
            abilities.forEach { ability ->
                AbilityCard(
                    ability = ability,
                    canAfford = player.availablePoints >= ability.cost,
                    onPurchase = { onBuyAbility(ability.id) },
                    onUpgrade = { onUpgradeAbility(ability.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
fun SkillsSection(
    player: com.trashapp.gcms.models.Player,
    onBuySkill: (String) -> Unit,
    onLevelUpSkill: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.MediumWood.copy(alpha = 0.9f)
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
                text = "📚 SKILLS",
                color = AppColors.Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Skills List
            val skills = player.progressionTree.skills.values.toList()
            skills.forEach { skill ->
                SkillCard(
                    skill = skill,
                    canAfford = player.availablePoints >= skill.cost,
                    onPurchase = { onBuySkill(skill.id) },
                    onLevelUp = { onLevelUpSkill(skill.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}