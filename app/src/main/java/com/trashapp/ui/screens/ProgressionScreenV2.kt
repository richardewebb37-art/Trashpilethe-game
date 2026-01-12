package com.trashapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.trashapp.gcms.models.Player
import com.trashapp.gcms.progression.*
import com.trashapp.ui.components.*
import com.trashapp.ui.theme.AppColors

/**
 * Progression Screen V2
 * Enhanced with tier-aware display and groupings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressionScreenV2(
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
    var selectedTier by remember { mutableStateOf<Tier?>(null) }
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
                    .padding(16.dp)
            ) {
                // Player Stats Header with Tier Display
                PlayerStatsHeaderV2(
                    player = player,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Tier Filter
                TierFilter(
                    selectedTier = selectedTier,
                    onTierSelected = { selectedTier = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Content Area
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Skills Section (grouped by tier)
                    item {
                        ProgressionSectionHeader(
                            title = "SKILLS",
                            icon = "🎯",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    val skillsToShow = if (selectedTier == null) {
                        player.progressionTree.getAllSkills()
                    } else {
                        player.progressionTree.getSkillsForTier(selectedTier)
                    }
                    
                    items(skillsToShow) { skill ->
                        SkillCardV2(
                            skill = skill,
                            onBuySkill = onBuySkill,
                            onLevelUpSkill = onLevelUpSkill,
                            availablePoints = player.pointSystem.availablePoints,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Abilities Section (grouped by tier)
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        ProgressionSectionHeader(
                            title = "ABILITIES",
                            icon = "⚔️",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    val abilitiesToShow = if (selectedTier == null) {
                        player.progressionTree.getAllAbilities()
                    } else {
                        player.progressionTree.getAbilitiesForTier(selectedTier)
                    }
                    
                    items(abilitiesToShow) { ability ->
                        AbilityCardV2(
                            ability = ability,
                            onBuyAbility = onBuyAbility,
                            onUpgradeAbility = onUpgradeAbility,
                            availablePoints = player.pointSystem.availablePoints,
                            isUnlocked = player.progressionTree.isAbilityUnlocked(ability.id),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Player Stats Header with Tier Display
 */
@Composable
fun PlayerStatsHeaderV2(
    player: Player,
    modifier: Modifier = Modifier
) {
    val level = player.pointSystem.getCurrentLevel()
    val tier = Tier.fromLevel(level)
    val tierColor = getTierColor(tier)
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.DarkWood
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Level and Tier Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Level Display
                Column {
                    Text(
                        text = "Level",
                        color = AppColors.Sepia,
                        fontSize = 14.sp
                    )
                    Text(
                        text = level.toString(),
                        color = AppColors.Gold,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Tier Display
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = tierColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TIER",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tier.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lv ${tier.minLevel}-${tier.maxLevel}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
                
                // Points Display
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Points",
                        color = AppColors.Sepia,
                        fontSize = 14.sp
                    )
                    Text(
                        text = player.pointSystem.availablePoints.toString(),
                        color = AppColors.CoinGold,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // XP Progress Bar
            XPProgressBar(
                currentXP = player.pointSystem.totalXP,
                xpToNext = player.pointSystem.xpToNextLevel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Tier Filter Component
 */
@Composable
fun TierFilter(
    selectedTier: Tier?,
    onTierSelected: (Tier?) -> Unit,
    modifier: Modifier = Modifier
) {
    val tiers = Tier.values()
    
    LazyColumn(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" option
        item {
            FilterChip(
                selected = selectedTier == null,
                onClick = { onTierSelected(null) },
                label = { Text("All Tiers") },
                leadingIcon = {
                    if (selectedTier == null) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AppColors.Gold
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.Gold,
                    selectedLabelColor = Color.Black,
                    containerColor = AppColors.DarkWood,
                    labelColor = AppColors.Sepia
                )
            )
        }
        
        // Tier options
        items(tiers) { tier ->
            val tierColor = getTierColor(tier)
            FilterChip(
                selected = selectedTier == tier,
                onClick = { onTierSelected(tier) },
                label = { 
                    Text(
                        tier.name,
                        fontWeight = if (selectedTier == tier) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = {
                    if (selectedTier == tier) {
                        Text(
                            "🏆",
                            fontSize = 16.sp
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = tierColor,
                    selectedLabelColor = Color.White,
                    containerColor = AppColors.DarkWood,
                    labelColor = AppColors.Sepia
                )
            )
        }
    }
}

/**
 * Progression Section Header
 */
@Composable
fun ProgressionSectionHeader(
    title: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        AppColors.Gold.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = title,
            color = AppColors.Gold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

/**
 * Skill Card V2 with tier display
 */
@Composable
fun SkillCardV2(
    skill: Skill,
    onBuySkill: (String) -> Unit,
    onLevelUpSkill: (String) -> Unit,
    availablePoints: Int,
    modifier: Modifier = Modifier
) {
    val tierColor = getTierColor(skill.tier)
    val rarityColor = getRarityColor(skill.rarity)
    val isPurchased = skill.currentLevel > 0
    
    Card(
        modifier = modifier
            .border(
                width = if (isPurchased) 3.dp else 1.dp,
                color = if (isPurchased) tierColor else AppColors.DarkWood,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.DarkWood.copy(alpha = if (isPurchased) 1.0f else 0.7f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPurchased) 8.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Tier Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Tier and Rarity Badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = tierColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = skill.tier.name,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp, 2.dp)
                            )
                        }
                        Surface(
                            color = rarityColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = skill.rarity.name,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp, 2.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = skill.name,
                        color = if (isPurchased) AppColors.Gold else AppColors.Sepia,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = skill.description,
                        color = AppColors.Sepia.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
                
                // Level Display
                if (isPurchased) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Lv",
                            color = AppColors.Sepia,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${skill.currentLevel}/${skill.maxLevel}",
                            color = AppColors.Gold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // XP Granted and Ability Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "+${skill.xpGranted} XP",
                        color = AppColors.Gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${skill.unlockedAbilities.size} abilities",
                        color = AppColors.Sepia,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Button
            if (!isPurchased) {
                val canAfford = availablePoints >= skill.baseCost
                Button(
                    onClick = { onBuySkill(skill.id) },
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) AppColors.Gold else AppColors.DarkGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Buy - ${skill.baseCost} pts",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (skill.currentLevel < skill.maxLevel) {
                val costMultiplier = 1.0 + (skill.currentLevel * 0.15)
                val cost = (skill.baseCost * costMultiplier).toInt()
                val canAfford = availablePoints >= cost
                
                Button(
                    onClick = { onLevelUpSkill(skill.id) },
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) AppColors.Gold else AppColors.DarkGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Level Up - $cost pts",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.Gold.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "MAX LEVEL",
                        color = AppColors.Gold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Ability Card V2 with tier display
 */
@Composable
fun AbilityCardV2(
    ability: Ability,
    onBuyAbility: (String) -> Unit,
    onUpgradeAbility: (String) -> Unit,
    availablePoints: Int,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val tierColor = getTierColor(ability.tier)
    val rarityColor = getRarityColor(ability.rarity)
    val isPurchased = ability.currentRank > 0
    
    Card(
        modifier = modifier
            .border(
                width = if (isPurchased) 3.dp else 1.dp,
                color = if (isPurchased) tierColor else AppColors.DarkWood,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.DarkWood.copy(
                alpha = when {
                    !isUnlocked -> 0.4f
                    isPurchased -> 1.0f
                    else -> 0.7f
                }
            )
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when {
                !isUnlocked -> 2.dp
                isPurchased -> 8.dp
                else -> 4.dp
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Tier Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Tier and Rarity Badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = tierColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = ability.tier.name,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp, 2.dp)
                            )
                        }
                        Surface(
                            color = rarityColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = ability.rarity.name,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp, 2.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = ability.name,
                        color = when {
                            !isUnlocked -> AppColors.DarkGray
                            isPurchased -> AppColors.Gold
                            else -> AppColors.Sepia
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = ability.description,
                        color = when {
                            !isUnlocked -> AppColors.DarkGray
                            else -> AppColors.Sepia.copy(alpha = 0.7f)
                        },
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
                
                // Rank Display
                if (isPurchased) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Rank",
                            color = AppColors.Sepia,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${ability.currentRank}/${ability.maxRank}",
                            color = AppColors.Gold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (!isUnlocked) {
                    Text(
                        text = "🔒",
                        fontSize = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // XP Granted
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⭐",
                    fontSize = 12.sp
                )
                Text(
                    text = "+${ability.xpGranted} XP",
                    color = AppColors.Gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Button
            if (isUnlocked && !isPurchased) {
                val canAfford = availablePoints >= ability.baseCost
                Button(
                    onClick = { onBuyAbility(ability.id) },
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) AppColors.Gold else AppColors.DarkGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Buy - ${ability.baseCost} pts",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (isPurchased && ability.currentRank < ability.maxRank) {
                val costMultiplier = 1.0 + (ability.currentRank * 0.2)
                val cost = (ability.baseCost * costMultiplier).toInt()
                val canAfford = availablePoints >= cost
                
                Button(
                    onClick = { onUpgradeAbility(ability.id) },
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) AppColors.Gold else AppColors.DarkGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Upgrade - $cost pts",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (ability.currentRank >= ability.maxRank) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.Gold.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "MAX RANK",
                        color = AppColors.Gold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else if (!isUnlocked) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.DarkGray
                    )
                ) {
                    Text(
                        text = "LOCKED - Requires Skill",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Get tier color for UI display
 */
fun getTierColor(tier: Tier): Color {
    return when (tier) {
        Tier.Life -> Color(0xFF90EE90) // Light Green
        Tier.Beginner -> Color(0xFF87CEEB) // Sky Blue
        Tier.Novice -> Color(0xFFDDA0DD) // Plum
        Tier.Hard -> Color(0xFFFF6B6B) // Coral Red
        Tier.Expert -> Color(0xFF9370DB) // Medium Purple
        Tier.Master -> Color(0xFFFFD700) // Gold
    }
}

/**
 * Get rarity color for UI display
 */
fun getRarityColor(rarity: Rarity): Color {
    return when (rarity) {
        Rarity.Common -> Color(0xFF9E9E9E) // Gray
        Rarity.Uncommon -> Color(0xFF4CAF50) // Green
        Rarity.Rare -> Color(0xFF2196F3) // Blue
        Rarity.Epic -> Color(0xFF9C27B0) // Purple
        Rarity.Legendary -> Color(0xFFFF9800) // Orange
        Rarity.Mythic -> Color(0xFFE91E63) // Pink
    }
}