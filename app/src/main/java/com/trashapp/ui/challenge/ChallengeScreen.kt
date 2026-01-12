package com.trashapp.ui.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.gcms.challenge.Challenge
import com.trashapp.gcms.challenge.ChallengeProgress
import com.trashapp.gcms.challenge.ChallengeType

/**
 * Challenge Screen - Main Challenge UI
 * 
 * Displays:
 * - Current level challenges
 * - Progress tracking
 * - Level advancement status
 * - Challenge completion rewards
 * - Requirements checklist
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(
    currentLevel: Int,
    challenges: List<Challenge>,
    challengeProgress: ChallengeProgress,
    canAdvance: Boolean,
    onChallengeClick: (Challenge) -> Unit = {},
    onAdvanceLevel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🎯 Challenges - Level $currentLevel",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${challengeProgress.completedChallenges}/${challengeProgress.totalChallenges} Completed",
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
            // Progress Header
            ChallengeProgressHeader(
                challengeProgress = challengeProgress,
                canAdvance = canAdvance
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Requirements Checklist
            RequirementsChecklist(
                canAdvance = canAdvance,
                challengeProgress = challengeProgress
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Challenge List
            Text(
                text = "Challenges",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (challenges.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📋",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No challenges available",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(challenges) { challenge ->
                        ChallengeCard(
                            challenge = challenge,
                            onClick = { onChallengeClick(challenge) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Challenge Progress Header
 */
@Composable
private fun ChallengeProgressHeader(
    challengeProgress: ChallengeProgress,
    canAdvance: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A3E)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = { challengeProgress.completionPercentage / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = if (canAdvance) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressStat(
                    icon = "✅",
                    label = "Completed",
                    value = challengeProgress.completedChallenges.toString(),
                    color = Color(0xFF4CAF50)
                )
                
                ProgressStat(
                    icon = "⏳",
                    label = "In Progress",
                    value = challengeProgress.inProgressChallenges.toString(),
                    color = Color(0xFFFF9800)
                )
                
                ProgressStat(
                    icon = "🔒",
                    label = "Not Started",
                    value = challengeProgress.notStartedChallenges.toString(),
                    color = Color(0xFF757575)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Total rewards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardDisplay(
                    icon = "✨",
                    label = "Total XP",
                    value = challengeProgress.totalXPEarned
                )
                
                RewardDisplay(
                    icon = "🪙",
                    label = "Total Points",
                    value = challengeProgress.totalPointsEarned
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Advance button
            if (canAdvance) {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Advance to Level ${challengeProgress.totalChallenges + 1}",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Complete all challenges to advance",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Requirements Checklist
 */
@Composable
private fun RequirementsChecklist(
    canAdvance: Boolean,
    challengeProgress: ChallengeProgress
) {
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
                text = "Level Advancement Requirements",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // XP requirement
            RequirementItem(
                icon = "✨",
                label = "Required XP",
                status = true,
                completed = true
            )
            
            // Points requirement
            RequirementItem(
                icon = "🪙",
                label = "Required Points",
                status = true,
                completed = true
            )
            
            // Abilities requirement
            RequirementItem(
                icon = "⚡",
                label = "Required Abilities",
                status = true,
                completed = true
            )
            
            // Skills requirement
            RequirementItem(
                icon = "📚",
                label = "Required Skills",
                status = true,
                completed = true
            )
            
            // Challenges requirement
            RequirementItem(
                icon = "🎯",
                label = "Challenges",
                status = canAdvance,
                completed = challengeProgress.isComplete,
                detail = "${challengeProgress.completedChallenges}/${challengeProgress.totalChallenges}"
            )
        }
    }
}

/**
 * Requirement Item Component
 */
@Composable
private fun RequirementItem(
    icon: String,
    label: String,
    status: Boolean,
    completed: Boolean,
    detail: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (completed) "✅" else "❌",
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = icon,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (completed) Color.White else Color.Gray,
            modifier = Modifier.weight(1f)
        )
        
        detail?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Bold
            )
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * Progress Stat Component
 */
@Composable
private fun ProgressStat(
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
            fontSize = 20.sp
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.LightGray
        )
    }
}

/**
 * Reward Display Component
 */
@Composable
private fun RewardDisplay(
    icon: String,
    label: String,
    value: Int
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.LightGray
        )
    }
}