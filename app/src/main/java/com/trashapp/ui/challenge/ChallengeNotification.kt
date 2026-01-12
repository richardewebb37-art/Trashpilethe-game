package com.trashapp.ui.challenge

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.trashapp.gcms.challenge.Challenge

/**
 * Challenge Completion Notification
 * 
 * Animated popup that appears when a challenge is completed
 * Shows:
 * - Challenge animation
 * - Challenge name and description
 * - Type indicator
 * - Rewards (XP and points)
 * - Achievement earned
 */
@Composable
fun ChallengeNotification(
    challenge: Challenge,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "alpha"
    )
    
    LaunchedEffect(challenge.id) {
        visible = true
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 3.dp,
                    color = Color(0xFF4CAF50)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                    
                    // Challenge icon with animation
                    ChallengeIconAnimation(challenge = challenge)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // "Challenge Complete" text
                    Text(
                        text = "🎉 Challenge Complete! 🎉",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Challenge name
                    Text(
                        text = challenge.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Challenge description
                    Text(
                        text = challenge.description,
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Type badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getTypeColor(challenge.type).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = challenge.type.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = getTypeColor(challenge.type),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Achievement section
                    AchievementCard(challenge = challenge)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Rewards section
                    RewardsCard(challenge = challenge)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Dismiss button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text(
                            text = "Excellent!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated Challenge Icon
 */
@Composable
private fun ChallengeIconAnimation(
    challenge: Challenge
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = Modifier
            .size(100.dp)
            .scale(scale)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50).copy(alpha = 0.3f),
                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(50)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getChallengeIcon(challenge.type),
            fontSize = 60.sp
        )
    }
}

/**
 * Achievement Display Card
 */
@Composable
private fun AchievementCard(challenge: Challenge) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A3E)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏅 Achievement Earned",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = challenge.reward.achievement,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Rewards Display Card
 */
@Composable
private fun RewardsCard(challenge: Challenge) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A3E)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Rewards",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardDisplay(
                    icon = "✨",
                    label = "XP",
                    value = challenge.reward.getTotalXP().toString(),
                    color = Color(0xFF9C27B0)
                )
                
                RewardDisplay(
                    icon = "🪙",
                    label = "Points",
                    value = challenge.reward.getTotalPoints().toString(),
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

/**
 * Individual Reward Display
 */
@Composable
private fun RewardDisplay(
    icon: String,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 28.sp
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.LightGray
        )
    }
}

/**
 * Get icon for challenge type
 */
private fun getChallengeIcon(type: com.trashapp.gcms.challenge.ChallengeType): String {
    return when (type) {
        com.trashapp.gcms.challenge.ChallengeType.SCORE -> "🎯"
        com.trashapp.gcms.challenge.ChallengeType.ABILITY_USE -> "⚡"
        com.trashapp.gcms.challenge.ChallengeType.SKILL_UNLOCK -> "📚"
        com.trashapp.gcms.challenge.ChallengeType.POINT_ACCUMULATION -> "💰"
        com.trashapp.gcms.challenge.ChallengeType.COMBO -> "🔥"
        com.trashapp.gcms.challenge.ChallengeType.WIN_STREAK -> "🏆"
        com.trashapp.gcms.challenge.ChallengeType.CARD_PLAYED -> "🃏"
        com.trashapp.gcms.challenge.ChallengeType.ROUND_WIN -> "⚔️"
        com.trashapp.gcms.challenge.ChallengeType.MATCH_WIN -> "🏅"
        com.trashapp.gcms.challenge.ChallengeType.TIME_LIMIT -> "⏱️"
    }
}

/**
 * Get color for challenge type
 */
@Composable
private fun getTypeColor(type: com.trashapp.gcms.challenge.ChallengeType): Color {
    return when (type) {
        com.trashapp.gcms.challenge.ChallengeType.SCORE -> Color(0xFFFF9800)
        com.trashapp.gcms.challenge.ChallengeType.ABILITY_USE -> Color(0xFF9C27B0)
        com.trashapp.gcms.challenge.ChallengeType.SKILL_UNLOCK -> Color(0xFF2196F3)
        com.trashapp.gcms.challenge.ChallengeType.POINT_ACCUMULATION -> Color(0xFF4CAF50)
        com.trashapp.gcms.challenge.ChallengeType.COMBO -> Color(0xFFF44336)
        com.trashapp.gcms.challenge.ChallengeType.WIN_STREAK -> Color(0xFFFFEB3B)
        com.trashapp.gcms.challenge.ChallengeType.CARD_PLAYED -> Color(0xFF607D8B)
        com.trashapp.gcms.challenge.ChallengeType.ROUND_WIN -> Color(0xFF795548)
        com.trashapp.gcms.challenge.ChallengeType.MATCH_WIN -> Color(0xFFE91E63)
        com.trashapp.gcms.challenge.ChallengeType.TIME_LIMIT -> Color(0xFF00BCD4)
    }
}