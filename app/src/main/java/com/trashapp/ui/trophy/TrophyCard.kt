package com.trashapp.ui.trophy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.gcms.trophy.Trophy
import com.trashapp.gcms.trophy.TrophyRarity

/**
 * Trophy Card Component
 * 
 * Displays individual trophy with:
 * - Trophy icon and name
 * - Rarity color coding
 * - Lock/unlock status
 * - Progress and rewards
 * - Click handling
 */
@Composable
fun TrophyCard(
    trophy: Trophy,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rarityColors = getRarityColors(trophy.rarity)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (trophy.isUnlocked) {
                    Modifier.border(
                        width = 3.dp,
                        color = rarityColors.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trophy.isUnlocked) {
                Color(0xFF1A1A2E)
            } else {
                Color(0xFF0F0F1A)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (trophy.isUnlocked) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with rarity and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rarity badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rarityColors.primary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = trophy.rarity.displayName,
                        color = rarityColors.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // Lock status
                if (!trophy.isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Trophy icon placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                rarityColors.primary.copy(alpha = 0.3f),
                                rarityColors.secondary.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆",
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Trophy name
            Text(
                text = trophy.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (trophy.isUnlocked) Color.White else Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Trophy description
            Text(
                text = trophy.description,
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Requirements section
            if (trophy.requiredAbilities.isNotEmpty() ||
                trophy.requiredSkills.isNotEmpty() ||
                trophy.requiredPoints > 0) {
                TrophyRequirements(
                    trophy = trophy,
                    rarityColors = rarityColors
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Rewards section
            TrophyRewards(
                trophy = trophy,
                isUnlocked = trophy.isUnlocked
            )
        }
    }
}

/**
 * Trophy Requirements Display
 */
@Composable
private fun TrophyRequirements(
    trophy: Trophy,
    rarityColors: RarityColors
) {
    Column {
        Text(
            text = "Requirements:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
        
        if (trophy.requiredLevel > 0) {
            RequirementItem(
                icon = "⭐",
                text = "Level ${trophy.requiredLevel}",
                color = rarityColors.primary
            )
        }
        
        if (trophy.requiredPoints > 0) {
            RequirementItem(
                icon = "💰",
                text = "${trophy.requiredPoints} Points",
                color = rarityColors.secondary
            )
        }
        
        trophy.requiredAbilities.take(3).forEach { ability ->
            RequirementItem(
                icon = "⚡",
                text = ability,
                color = rarityColors.primary
            )
        }
        
        if (trophy.requiredAbilities.size > 3) {
            Text(
                text = "+ ${trophy.requiredAbilities.size - 3} more abilities",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 24.dp)
            )
        }
    }
}

/**
 * Trophy Rewards Display
 */
@Composable
private fun TrophyRewards(
    trophy: Trophy,
    isUnlocked: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RewardItem(
            icon = "✨",
            label = "XP",
            value = trophy.getXPValue(),
            unlocked = isUnlocked
        )
        
        RewardItem(
            icon = "🪙",
            label = "Points",
            value = trophy.getPointBonus(),
            unlocked = isUnlocked
        )
    }
}

/**
 * Requirement Item Component
 */
@Composable
private fun RequirementItem(
    icon: String,
    text: String,
    color: Color
) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 12.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = color
        )
    }
}

/**
 * Reward Item Component
 */
@Composable
private fun RewardItem(
    icon: String,
    label: String,
    value: Int,
    unlocked: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (unlocked) Color(0xFF4CAF50) else Color.Gray
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.LightGray
        )
    }
}

/**
 * Rarity Colors Data Class
 */
data class RarityColors(
    val primary: Color,
    val secondary: Color
)

/**
 * Get colors based on trophy rarity
 */
@Composable
private fun getRarityColors(rarity: TrophyRarity): RarityColors {
    return when (rarity) {
        TrophyRarity.COMMON -> RarityColors(
            primary = Color(0xFF9E9E9E),
            secondary = Color(0xFF757575)
        )
        TrophyRarity.UNCOMMON -> RarityColors(
            primary = Color(0xFF4CAF50),
            secondary = Color(0xFF388E3C)
        )
        TrophyRarity.RARE -> RarityColors(
            primary = Color(0xFF2196F3),
            secondary = Color(0xFF1976D2)
        )
        TrophyRarity.EPIC -> RarityColors(
            primary = Color(0xFF9C27B0),
            secondary = Color(0xFF7B1FA2)
        )
        TrophyRarity.LEGENDARY -> RarityColors(
            primary = Color(0xFFFF9800),
            secondary = Color(0xFFF57C00)
        )
        TrophyRarity.MYTHIC -> RarityColors(
            primary = Color(0xFFF44336),
            secondary = Color(0xFFD32F2F)
        )
    }
}