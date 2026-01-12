package com.trashapp.ui.trophy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.gcms.trophy.Trophy
import com.trashapp.gcms.trophy.TrophyRarity

/**
 * Trophy Screen - Main Trophy Collection UI
 * 
 * Displays:
 * - Trophy collection statistics
 * - Trophy filtering by tier and rarity
 * - Trophy search
 * - Trophy grid display
 * - Progress tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrophyScreen(
    trophies: List<Trophy>,
    onTrophyClick: (Trophy) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTier by remember { mutableStateOf<TierFilter?>(null) }
    var selectedRarity by remember { mutableStateOf<TrophyRarity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredTrophies = remember(trophies, selectedTier, selectedRarity, searchQuery) {
        trophies.filter { trophy ->
            // Filter by tier
            val tierMatch = selectedTier == null || 
                when (selectedTier) {
                    TierFilter.Life -> trophy.tier.name == "LIFE"
                    TierFilter.Beginner -> trophy.tier.name == "BEGINNER"
                    TierFilter.Novice -> trophy.tier.name == "NOVICE"
                    TierFilter.Hard -> trophy.tier.name == "HARD"
                    TierFilter.Expert -> trophy.tier.name == "EXPERT"
                    TierFilter.Master -> trophy.tier.name == "MASTER"
                }
            
            // Filter by rarity
            val rarityMatch = selectedRarity == null || trophy.rarity == selectedRarity
            
            // Filter by search
            val searchMatch = searchQuery.isEmpty() ||
                trophy.name.contains(searchQuery, ignoreCase = true) ||
                trophy.description.contains(searchQuery, ignoreCase = true)
            
            tierMatch && rarityMatch && searchMatch
        }
    }
    
    val unlockedTrophies = trophies.count { it.isUnlocked }
    val completionPercentage = if (trophies.isNotEmpty()) {
        (unlockedTrophies.toFloat() / trophies.size) * 100f
    } else {
        0f
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🏆 Trophy Collection",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$unlockedTrophies / ${trophies.size} Unlocked • ${String.format("%.1f", completionPercentage)}%",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Statistics Header
            TrophyStatistics(
                trophies = trophies,
                completionPercentage = completionPercentage
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search trophies...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2A2A3E),
                    unfocusedContainerColor = Color(0xFF2A2A3E)
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filter Chips
            FilterSection(
                selectedTier = selectedTier,
                selectedRarity = selectedRarity,
                onTierSelected = { selectedTier = it },
                onRaritySelected = { selectedRarity = it },
                onClearFilters = {
                    selectedTier = null
                    selectedRarity = null
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Trophy Grid
            if (filteredTrophies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trophies found",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTrophies) { trophy ->
                        TrophyCard(
                            trophy = trophy,
                            onClick = { onTrophyClick(trophy) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Trophy Statistics Header
 */
@Composable
private fun TrophyStatistics(
    trophies: List<Trophy>,
    completionPercentage: Float
) {
    val unlocked = trophies.count { it.isUnlocked }
    val totalXP = trophies.filter { it.isUnlocked }.sumOf { it.getXPValue() }
    val totalPoints = trophies.filter { it.isUnlocked }.sumOf { it.getPointBonus() }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A3E)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Progress Bar
            LinearProgressIndicator(
                progress = { completionPercentage / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4CAF50),
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Trophies",
                    value = "$unlocked/${trophies.size}",
                    color = Color(0xFF4CAF50)
                )
                
                StatItem(
                    label = "XP Earned",
                    value = totalXP.toString(),
                    color = Color(0xFF9C27B0)
                )
                
                StatItem(
                    label = "Points",
                    value = totalPoints.toString(),
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

/**
 * Stat Item Component
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.LightGray
        )
    }
}

/**
 * Filter Section
 */
@Composable
private fun FilterSection(
    selectedTier: TierFilter?,
    selectedRarity: TrophyRarity?,
    onTierSelected: (TierFilter?) -> Unit,
    onRaritySelected: (TrophyRarity?) -> Unit,
    onClearFilters: () -> Unit
) {
    Column {
        // Tier Filters
        Text(
            text = "Tiers",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTier == null,
                onClick = { onTierSelected(null) },
                label = { Text("All") }
            )
            
            TierFilter.values().forEach { tier ->
                FilterChip(
                    selected = selectedTier == tier,
                    onClick = { onTierSelected(if (selectedTier == tier) null else tier) },
                    label = { Text(tier.displayName) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Rarity Filters
        Text(
            text = "Rarity",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedRarity == null,
                onClick = { onRaritySelected(null) },
                label = { Text("All") }
            )
            
            TrophyRarity.values().forEach { rarity ->
                val rarityColor = when (rarity) {
                    TrophyRarity.COMMON -> Color(0xFF9E9E9E)
                    TrophyRarity.UNCOMMON -> Color(0xFF4CAF50)
                    TrophyRarity.RARE -> Color(0xFF2196F3)
                    TrophyRarity.EPIC -> Color(0xFF9C27B0)
                    TrophyRarity.LEGENDARY -> Color(0xFFFF9800)
                    TrophyRarity.MYTHIC -> Color(0xFFF44336)
                }
                
                FilterChip(
                    selected = selectedRarity == rarity,
                    onClick = { onRaritySelected(if (selectedRarity == rarity) null else rarity) },
                    label = { Text(rarity.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = rarityColor.copy(alpha = 0.3f),
                        selectedLabelColor = rarityColor
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Clear Filters Button
        if (selectedTier != null || selectedRarity != null) {
            Button(
                onClick = onClearFilters,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                )
            ) {
                Text("Clear Filters")
            }
        }
    }
}

/**
 * Tier Filter Enum
 */
enum class TierFilter(val displayName: String) {
    Life("Life"),
    Beginner("Beginner"),
    Novice("Novice"),
    Hard("Hard"),
    Expert("Expert"),
    Master("Master")
}