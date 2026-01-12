package com.trashapp.ui.trophy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.progression.getTierColor
import com.trashapp.gcms.trophy.*
import com.trashapp.ui.theme.AppColors

/**
 * Trophy Screen V2
 * Enhanced with tier-specific elements and filtering
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrophyScreenV2(
    onBackClick: () -> Unit,
    trophyManager: TrophyManager,
    modifier: Modifier = Modifier
) {
    val trophies by trophyManager.trophies.collectAsState()
    val stats by trophyManager.stats.collectAsState()
    
    var selectedTier by remember { mutableStateOf<Tier?>(null) }
    var selectedRarity by remember { mutableStateOf<TrophyRarity?>(null) }
    
    val filteredTrophies = trophies.filter { trophy ->
        val tierMatch = selectedTier == null || trophy.tier == selectedTier
        val rarityMatch = selectedRarity == null || trophy.rarity == selectedRarity
        tierMatch && rarityMatch
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TROPHIES",
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
                .padding(16.dp)
        ) {
            // Stats Header
            TrophyStatsHeader(
                stats = stats,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filters Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tier Filter
                TierFilterDropdown(
                    selectedTier = selectedTier,
                    onTierSelected = { selectedTier = it },
                    modifier = Modifier.weight(1f)
                )
                
                // Rarity Filter
                RarityFilterDropdown(
                    selectedRarity = selectedRarity,
                    onRaritySelected = { selectedRarity = it },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Trophy Grid
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTrophies) { trophy ->
                    TrophyCardV2(
                        trophy = trophy,
                        isUnlocked = stats.unlockedTrophies.contains(trophy.id),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Trophy Stats Header with Completion Percentage
 */
@Composable
fun TrophyStatsHeader(
    stats: TrophyStats,
    modifier: Modifier = Modifier
) {
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
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Trophies",
                    value = "${stats.unlockedTrophies.size}/${stats.totalTrophies}",
                    icon = "🏆"
                )
                
                StatItem(
                    label = "XP Earned",
                    value = stats.totalXPEarned.toString(),
                    icon = "⭐"
                )
                
                StatItem(
                    label = "Points",
                    value = stats.totalPointsEarned.toString(),
                    icon = "💰"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Collection Progress",
                        color = AppColors.Sepia,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stats.completionPercentage}%",
                        color = AppColors.Gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = stats.completionPercentage / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = AppColors.Gold,
                    trackColor = AppColors.DarkGray,
                )
            }
        }
    }
}

/**
 * Stat Item Component
 */
@Composable
fun StatItem(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = AppColors.Gold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = AppColors.Sepia,
            fontSize = 10.sp
        )
    }
}

/**
 * Tier Filter Dropdown
 */
@Composable
fun TierFilterDropdown(
    selectedTier: Tier?,
    onTierSelected: (Tier?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.DarkWood,
                contentColor = AppColors.Sepia
            )
        ) {
            Text(
                text = selectedTier?.name ?: "All Tiers",
                fontWeight = FontWeight.Bold
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(AppColors.DarkWood)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "All Tiers",
                        color = if (selectedTier == null) AppColors.Gold else AppColors.Sepia
                    )
                },
                onClick = {
                    onTierSelected(null)
                    expanded = false
                }
            )
            
            Tier.values().forEach { tier ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = getTierColor(tier),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = tier.name.substring(0, 1),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(4.dp, 2.dp)
                                )
                            }
                            Text(
                                text = tier.name,
                                color = if (selectedTier == tier) AppColors.Gold else AppColors.Sepia
                            )
                        }
                    },
                    onClick = {
                        onTierSelected(tier)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Rarity Filter Dropdown
 */
@Composable
fun RarityFilterDropdown(
    selectedRarity: TrophyRarity?,
    onRaritySelected: (TrophyRarity?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.DarkWood,
                contentColor = AppColors.Sepia
            )
        ) {
            Text(
                text = selectedRarity?.name ?: "All Rarities",
                fontWeight = FontWeight.Bold
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(AppColors.DarkWood)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "All Rarities",
                        color = if (selectedRarity == null) AppColors.Gold else AppColors.Sepia
                    )
                },
                onClick = {
                    onRaritySelected(null)
                    expanded = false
                }
            )
            
            TrophyRarity.values().forEach { rarity ->
                val rarityColor = getTrophyRarityColor(rarity)
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = rarityColor,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = rarity.name.substring(0, 1),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(4.dp, 2.dp)
                                )
                            }
                            Text(
                                text = rarity.name,
                                color = if (selectedRarity == rarity) AppColors.Gold else AppColors.Sepia
                            )
                        }
                    },
                    onClick = {
                        onRaritySelected(rarity)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Trophy Card V2 with tier-specific styling
 */
@Composable
fun TrophyCardV2(
    trophy: Trophy,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val tierColor = getTierColor(trophy.tier)
    val rarityColor = getTrophyRarityColor(trophy.rarity)
    
    Card(
        modifier = modifier
            .border(
                width = if (isUnlocked) 3.dp else 1.dp,
                color = if (isUnlocked) rarityColor else AppColors.DarkWood,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.DarkWood.copy(
                alpha = if (isUnlocked) 1.0f else 0.5f
            )
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trophy Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (isUnlocked) rarityColor else AppColors.DarkGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnlocked) "🏆" else "🔒",
                    fontSize = 32.sp
                )
            }
            
            // Trophy Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                            text = trophy.tier.name,
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
                            text = trophy.rarity.name,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(4.dp, 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = trophy.name,
                    color = if (isUnlocked) AppColors.Gold else AppColors.DarkGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = trophy.description,
                    color = if (isUnlocked) AppColors.Sepia else AppColors.DarkGray,
                    fontSize = 12.sp,
                    maxLines = 2
                )
                
                // Requirements
                if (trophy.prerequisites.level > 0) {
                    Text(
                        text = "Required Level: ${trophy.prerequisites.level}",
                        color = AppColors.Gold,
                        fontSize = 10.sp
                    )
                }
            }
            
            // Rewards
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (trophy.xpReward > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 12.sp)
                        Text(
                            text = "+${trophy.xpReward} XP",
                            color = if (isUnlocked) AppColors.Gold else AppColors.DarkGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if (trophy.pointsReward > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💰", fontSize = 12.sp)
                        Text(
                            text = "+${trophy.pointsReward} pts",
                            color = if (isUnlocked) AppColors.CoinGold else AppColors.DarkGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Get trophy rarity color for UI display
 */
fun getTrophyRarityColor(rarity: TrophyRarity): Color {
    return when (rarity) {
        TrophyRarity.Common -> Color(0xFF9E9E9E) // Gray
        TrophyRarity.Uncommon -> Color(0xFF4CAF50) // Green
        TrophyRarity.Rare -> Color(0xFF2196F3) // Blue
        TrophyRarity.Epic -> Color(0xFF9C27B0) // Purple
        TrophyRarity.Legendary -> Color(0xFFFF9800) // Orange
        TrophyRarity.Mythic -> Color(0xFFE91E63) // Pink
    }
}