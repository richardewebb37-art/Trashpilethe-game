package com.trashapp.ui.trophy

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
import com.trashapp.gcms.trophy.Trophy

/**
 * Trophy Unlock Notification
 * 
 * Animated popup that appears when a trophy is unlocked
 * Shows:
 * - Trophy animation
 * - Trophy name and description
 * - Rarity
 * - Rewards (XP and points)
 */
@Composable
fun TrophyNotification(
    trophy: Trophy,
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
    
    LaunchedEffect(trophy.id) {
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
                    color = getRarityColor(trophy.rarity)
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
                    
                    // Trophy icon with animation
                    TrophyIconAnimation(trophy = trophy)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // "Trophy Unlocked" text
                    Text(
                        text = "🎉 Trophy Unlocked! 🎉",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Trophy name
                    Text(
                        text = trophy.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = getRarityColor(trophy.rarity),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Trophy description
                    Text(
                        text = trophy.description,
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Rarity badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getRarityColor(trophy.rarity).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = trophy.rarity.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = getRarityColor(trophy.rarity),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Rewards section
                    RewardsCard(trophy = trophy)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Dismiss button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getRarityColor(trophy.rarity)
                        )
                    ) {
                        Text(
                            text = "Awesome!",
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
 * Animated Trophy Icon
 */
@Composable
private fun TrophyIconAnimation(
    trophy: Trophy
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
                        getRarityColor(trophy.rarity).copy(alpha = 0.3f),
                        getRarityColor(trophy.rarity).copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(50)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🏆",
            fontSize = 60.sp
        )
    }
}

/**
 * Rewards Display Card
 */
@Composable
private fun RewardsCard(trophy: Trophy) {
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardDisplay(
                    icon = "✨",
                    label = "XP",
                    value = trophy.getXPValue().toString(),
                    color = Color(0xFF9C27B0)
                )
                
                RewardDisplay(
                    icon = "🪙",
                    label = "Points",
                    value = trophy.getPointBonus().toString(),
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
 * Get rarity color
 */
@Composable
private fun getRarityColor(rarity: com.trashapp.gcms.trophy.TrophyRarity): Color {
    return when (rarity) {
        com.trashapp.gcms.trophy.TrophyRarity.COMMON -> Color(0xFF9E9E9E)
        com.trashapp.gcms.trophy.TrophyRarity.UNCOMMON -> Color(0xFF4CAF50)
        com.trashapp.gcms.trophy.RARE -> Color(0xFF2196F3)
        com.trashapp.gcms.trophy.EPIC -> Color(0xFF9C27B0)
        com.trashapp.gcms.trophy.LEGENDARY -> Color(0xFFFF9800)
        com.trashapp.gcms.trophy.MYTHIC -> Color(0xFFF44336)
    }
}