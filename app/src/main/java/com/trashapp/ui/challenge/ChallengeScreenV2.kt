package com.trashapp.ui.challenge

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
import com.trashapp.gcms.challenge.*
import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.progression.getTierColor
import com.trashapp.gcms.progression.PointSystem
import com.trashapp.ui.theme.AppColors

/**
 * Challenge Screen V2
 * Enhanced with progression status and tier information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreenV2(
    onBackClick: () -> Unit,
    challengeManager: ChallengeManager,
    pointSystem: PointSystem,
    currentLevel: Int,
    modifier: Modifier = Modifier
) {
    val currentLevelChallenges by challengeManager.currentLevelChallenges.collectAsState()
    val levelProgress by challengeManager.levelProgress.collectAsState()
    val canAdvance by challengeManager.canAdvanceToNextLevel.collectAsState()
    
    val tier = Tier.fromLevel(currentLevel)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CHALLENGES",
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
            // Level and Tier Header
            LevelTierHeader(
                currentLevel = currentLevel,
                tier = tier,
                pointSystem = pointSystem,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Summary
            ChallengeProgressSummary(
                levelProgress = levelProgress,
                currentLevel = currentLevel,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Challenge Requirements Checklist
            RequirementsChecklist(
                levelProgress = levelProgress,
                currentLevel = currentLevel,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Advance Button
            if (canAdvance) {
                Button(
                    onClick = { /* TODO: Handle advance to next level */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Gold,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Advance to Level ${currentLevel + 1}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Challenges List
            Text(
                text = "LEVEL CHALLENGES",
                color = AppColors.Gold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentLevelChallenges) { challenge ->
                    ChallengeCardV2(
                        challenge = challenge,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Level and Tier Header
 */
@Composable
fun LevelTierHeader(
    currentLevel: Int,
    tier: Tier,
    pointSystem: PointSystem,
    modifier: Modifier = Modifier
) {
    val tierColor = getTierColor(tier)
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.DarkWood
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Display
            Column {
                Text(
                    text = "CURRENT LEVEL",
                    color = AppColors.Sepia,
                    fontSize = 12.sp
                )
                Text(
                    text = currentLevel.toString(),
                    color = AppColors.Gold,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Tier Badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = tierColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TIER",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tier.name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lv ${tier.minLevel}-${tier.maxLevel}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
            
            // Points Display
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "AVAILABLE POINTS",
                    color = AppColors.Sepia,
                    fontSize = 12.sp
                )
                Text(
                    text = pointSystem.availablePoints.toString(),
                    color = AppColors.CoinGold,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Challenge Progress Summary
 */
@Composable
fun ChallengeProgressSummary(
    levelProgress: LevelProgress,
    currentLevel: Int,
    modifier: Modifier = Modifier
) {
    val completedCount = levelProgress.completedChallenges.size
    val totalCount = levelProgress.totalChallenges
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    
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
            // Progress Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStatItem(
                    label = "Completed",
                    value = completedCount.toString(),
                    icon = "✅"
                )
                
                ProgressStatItem(
                    label = "In Progress",
                    value = levelProgress.inProgressChallenges.size.toString(),
                    icon = "🔄"
                )
                
                ProgressStatItem(
                    label = "Not Started",
                    value = levelProgress.notStartedChallenges.size.toString(),
                    icon = "⏳"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Overall Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Level Progress",
                        color = AppColors.Sepia,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = AppColors.Gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (progress >= 1.0f) AppColors.Gold else AppColors.CoinGold,
                    trackColor = AppColors.DarkGray,
                )
            }
        }
    }
}

/**
 * Progress Stat Item
 */
@Composable
fun ProgressStatItem(
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
            fontSize = 20.sp,
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
 * Requirements Checklist
 */
@Composable
fun RequirementsChecklist(
    levelProgress: LevelProgress,
    currentLevel: Int,
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
            Text(
                text = "ADVANCEMENT REQUIREMENTS",
                color = AppColors.Gold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // XP Requirement
            RequirementItem(
                label = "Total XP",
                current = levelProgress.totalXP,
                required = levelProgress.requiredXP,
                icon = "⭐",
                isMet = levelProgress.totalXP >= levelProgress.requiredXP
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Points Requirement
            RequirementItem(
                label = "Points",
                current = levelProgress.points,
                required = levelProgress.requiredPoints,
                icon = "💰",
                isMet = levelProgress.points >= levelProgress.requiredPoints
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Abilities Requirement
            RequirementItem(
                label = "Abilities Owned",
                current = levelProgress.abilitiesCount,
                required = levelProgress.requiredAbilities,
                icon = "⚔️",
                isMet = levelProgress.abilitiesCount >= levelProgress.requiredAbilities
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Skills Requirement
            RequirementItem(
                label = "Skills Owned",
                current = levelProgress.skillsCount,
                required = levelProgress.requiredSkills,
                icon = "🎯",
                isMet = levelProgress.skillsCount >= levelProgress.requiredSkills
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Challenges Requirement
            RequirementItem(
                label = "Challenges Completed",
                current = levelProgress.completedChallenges.size,
                required = levelProgress.totalChallenges,
                icon = "🏆",
                isMet = levelProgress.completedChallenges.size >= levelProgress.totalChallenges
            )
        }
    }
}

/**
 * Requirement Item
 */
@Composable
fun RequirementItem(
    label: String,
    current: Int,
    required: Int,
    icon: String,
    isMet: Boolean
) {
    val progress = if (required > 0) current.toFloat() / required else 1f
    val clampedProgress = progress.coerceIn(0f, 1f)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
            Text(
                text = label,
                color = AppColors.Sepia,
                fontSize = 12.sp
            )
        }
        
        Text(
            text = "$current/$required",
            color = if (isMet) AppColors.Gold else AppColors.Sepia,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
    
    Spacer(modifier = Modifier.height(4.dp))
    
    LinearProgressIndicator(
        progress = clampedProgress,
        modifier = Modifier.fillMaxWidth(),
        color = if (isMet) AppColors.Gold else AppColors.CoinGold,
        trackColor = AppColors.DarkGray,
    )
}

/**
 * Challenge Card V2 with progress tracking
 */
@Composable
fun ChallengeCardV2(
    challenge: Challenge,
    modifier: Modifier = Modifier
) {
    val isCompleted = challenge.progress >= 1.0f
    val typeColor = getChallengeTypeColor(challenge.type)
    
    Card(
        modifier = modifier
            .border(
                width = if (isCompleted) 3.dp else 1.dp,
                color = if (isCompleted) AppColors.Gold else AppColors.DarkWood,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.DarkWood.copy(
                alpha = if (isCompleted) 1.0f else 0.7f
            )
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Type Badge
                    Surface(
                        color = typeColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = challenge.type.name,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(4.dp, 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = challenge.description,
                        color = if (isCompleted) AppColors.Gold else AppColors.Sepia,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Status Icon
                Text(
                    text = if (isCompleted) "✅" else "🔄",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        color = AppColors.Sepia,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${(challenge.progress * 100).toInt()}%",
                        color = AppColors.Gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = challenge.progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isCompleted) AppColors.Gold else AppColors.CoinGold,
                    trackColor = AppColors.DarkGray,
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Requirements Display
            ChallengeRequirementsDisplay(
                requirements = challenge.requirements,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Rewards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (challenge.xpReward > 0) {
                    RewardItem(
                        icon = "⭐",
                        label = "XP",
                        value = challenge.xpReward.toString()
                    )
                }
                
                if (challenge.pointsReward > 0) {
                    RewardItem(
                        icon = "💰",
                        label = "Points",
                        value = challenge.pointsReward.toString()
                    )
                }
                
                if (challenge.unlocksLevel) {
                    RewardItem(
                        icon = "🔓",
                        label = "Unlocks",
                        value = "Next Level"
                    )
                }
            }
        }
    }
}

/**
 * Challenge Requirements Display
 */
@Composable
fun ChallengeRequirementsDisplay(
    requirements: ChallengeRequirements,
    modifier: Modifier = Modifier
) {
    val requirementItems = mutableListOf<Pair<String, String>>()
    
    requirements.score?.let { requirementItems.add("Score" to it.toString()) }
    requirements.abilities?.let { requirementItems.add("Abilities" to it.toString()) }
    requirements.skills?.let { requirementItems.add("Skills" to it.toString()) }
    requirements.points?.let { requirementItems.add("Points" to it.toString()) }
    requirements.combos?.let { requirementItems.add("Combos" to it.toString()) }
    requirements.streaks?.let { requirementItems.add("Streaks" to it.toString()) }
    requirements.cardsPlayed?.let { requirementItems.add("Cards" to it.toString()) }
    requirements.roundWins?.let { requirementItems.add("Round Wins" to it.toString()) }
    requirements.matchWins?.let { requirementItems.add("Match Wins" to it.toString()) }
    requirements.timeLimit?.let { requirementItems.add("Time Limit" to "${it}s") }
    
    if (requirementItems.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "REQUIREMENTS",
                color = AppColors.Sepia,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            requirementItems.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "•",
                                color = AppColors.Gold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "$label: $value",
                                color = AppColors.Sepia,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reward Item
 */
@Composable
fun RewardItem(
    icon: String,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 14.sp
        )
        Column {
            Text(
                text = label,
                color = AppColors.Sepia,
                fontSize = 10.sp
            )
            Text(
                text = value,
                color = AppColors.Gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Get challenge type color for UI display
 */
fun getChallengeTypeColor(type: ChallengeType): Color {
    return when (type) {
        ChallengeType.SCORE -> Color(0xFF4CAF50) // Green
        ChallengeType.ABILITY_USE -> Color(0xFF2196F3) // Blue
        ChallengeType.SKILL_UNLOCK -> Color(0xFF9C27B0) // Purple
        ChallengeType.POINT_ACCUMULATION -> Color(0xFFFF9800) // Orange
        ChallengeType.COMBO -> Color(0xFFE91E63) // Pink
        ChallengeType.WIN_STREAK -> Color(0xFFF44336) // Red
        ChallengeType.CARD_PLAYED -> Color(0xFF00BCD4) // Cyan
        ChallengeType.ROUND_WIN -> Color(0xFF8BC34A) // Light Green
        ChallengeType.MATCH_WIN -> Color(0xFFFFEB3B) // Yellow
        ChallengeType.TIME_LIMIT -> Color(0xFF607D8B) // Blue Gray
    }
}