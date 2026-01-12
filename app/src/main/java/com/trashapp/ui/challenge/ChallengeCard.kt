package com.trashapp.ui.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import com.trashapp.gcms.challenge.Challenge
import com.trashapp.gcms.challenge.ChallengeType
import com.trashapp.gcms.trophy.TrophyRarity

/**
 * Challenge Card Component
 * 
 * Displays individual challenge with:
 * - Challenge icon and name
 * - Type indicator
 * - Progress bar
 * - Requirements
 * - Rewards
 * - Completion status
 */
@Composable
fun ChallengeCard(
    challenge: Challenge,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val typeColors = getTypeColors(challenge.type)
    val progress = challenge.getProgressPercentage()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (challenge.isCompleted) {
                    Modifier.border(
                        width = 3.dp,
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier.border(
                        width = 2.dp,
                        color = typeColors.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (challenge.isCompleted) {
                Color(0xFF1A2E1A)
            } else {
                Color(0xFF1A1A2E)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (challenge.isCompleted) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with type and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = typeColors.primary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = challenge.type.displayName,
                        color = typeColors.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // Completion status
                if (challenge.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "${challenge.progress}/${challenge.maxProgress}",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Challenge icon placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                typeColors.primary.copy(alpha = 0.3f),
                                typeColors.secondary.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getChallengeIcon(challenge.type),
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Challenge name
            Text(
                text = challenge.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (challenge.isCompleted) Color(0xFF4CAF50) else Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Challenge description
            Text(
                text = challenge.description,
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            if (!challenge.isCompleted) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = typeColors.primary,
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${String.format("%.1f", progress)}% Complete",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Rewards section
            ChallengeRewards(
                challenge = challenge,
                typeColors = typeColors
            )
        }
    }
}

/**
 * Challenge Rewards Display
 */
@Composable
private fun ChallengeRewards(
    challenge: Challenge,
    typeColors: ChallengeTypeColors
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A3E)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ChallengeRewardItem(
                icon = "✨",
                label = "XP",
                value = challenge.reward.getTotalXP(),
                color = Color(0xFF9C27B0)
            )
            
            ChallengeRewardItem(
                icon = "🪙",
                label = "Points",
                value = challenge.reward.getTotalPoints(),
                color = Color(0xFFFF9800)
            )
            
            if (challenge.reward.unlocksLevel > 0) {
                ChallengeRewardItem(
                    icon = "🔓",
                    label = "Unlocks",
                    value = challenge.reward.unlocksLevel,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

/**
 * Challenge Reward Item Component
 */
@Composable
private fun ChallengeRewardItem(
    icon: String,
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 14.sp
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (value > 0) color else Color.Gray
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color.LightGray
        )
    }
}

/**
 * Get icon for challenge type
 */
private fun getChallengeIcon(type: ChallengeType): String {
    return when (type) {
        ChallengeType.SCORE -> "🎯"
        ChallengeType.ABILITY_USE -> "⚡"
        ChallengeType.SKILL_UNLOCK -> "📚"
        ChallengeType.POINT_ACCUMULATION -> "💰"
        ChallengeType.COMBO -> "🔥"
        ChallengeType.WIN_STREAK -> "🏆"
        ChallengeType.CARD_PLAYED -> "🃏"
        ChallengeType.ROUND_WIN -> "⚔️"
        ChallengeType.MATCH_WIN -> "🏅"
        ChallengeType.TIME_LIMIT -> "⏱️"
    }
}

/**
 * Challenge Type Colors Data Class
 */
data class ChallengeTypeColors(
    val primary: Color,
    val secondary: Color
)

/**
 * Get colors based on challenge type
 */
@Composable
private fun getTypeColors(type: ChallengeType): ChallengeTypeColors {
    return when (type) {
        ChallengeType.SCORE -> ChallengeTypeColors(
            primary = Color(0xFFFF9800),
            secondary = Color(0xFFF57C00)
        )
        ChallengeType.ABILITY_USE -> ChallengeTypeColors(
            primary = Color(0xFF9C27B0),
            secondary = Color(0xFF7B1FA2)
        )
        ChallengeType.SKILL_UNLOCK -> ChallengeTypeColors(
            primary = Color(0xFF2196F3),
            secondary = Color(0xFF1976D2)
        )
        ChallengeType.POINT_ACCUMULATION -> ChallengeTypeColors(
            primary = Color(0xFF4CAF50),
            secondary = Color(0xFF388E3C)
        )
        ChallengeType.COMBO -> ChallengeTypeColors(
            primary = Color(0xFFF44336),
            secondary = Color(0xFFD32F2F)
        )
        ChallengeType.WIN_STREAK -> ChallengeTypeColors(
            primary = Color(0xFFFFEB3B),
            secondary = Color(0xFFFBC02D)
        )
        ChallengeType.CARD_PLAYED -> ChallengeTypeColors(
            primary = Color(0xFF607D8B),
            secondary = Color(0xFF455A64)
        )
        ChallengeType.ROUND_WIN -> ChallengeTypeColors(
            primary = Color(0xFF795548),
            secondary = Color(0xFF5D4037)
        )
        ChallengeType.MATCH_WIN -> ChallengeTypeColors(
            primary = Color(0xFFE91E63),
            secondary = Color(0xFFC2185B)
        )
        ChallengeType.TIME_LIMIT -> ChallengeTypeColors(
            primary = Color(0xFF00BCD4),
            secondary = Color(0xFF0097A7)
        )
    }
}