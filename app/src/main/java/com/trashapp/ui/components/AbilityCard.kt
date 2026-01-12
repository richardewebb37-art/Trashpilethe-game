package com.trashapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trashapp.gcms.progression.Ability
import com.trashapp.gcms.progression.Skill
import com.trashapp.ui.theme.AppColors

/**
 * Ability Card Component
 * Displays an ability with purchase/upgrade options
 */
@Composable
fun AbilityCard(
    ability: Ability,
    canAfford: Boolean,
    onPurchase: () -> Unit,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !ability.isUnlocked -> Color.Gray.copy(alpha = 0.5f)
        ability.isPurchased -> AppColors.MediumWood
        else -> AppColors.AgedWood
    }
    
    val borderColor = when (ability.rarity) {
        com.trashapp.gcms.progression.AbilityRarity.COMMON -> AppColors.Brass
        com.trashapp.gcms.progression.AbilityRarity.UNCOMMON -> Color(0xFF4CAF50)
        com.trashapp.gcms.progression.AbilityRarity.RARE -> Color(0xFF2196F3)
        com.trashapp.gcms.progression.AbilityRarity.EPIC -> Color(0xFF9C27B0)
        com.trashapp.gcms.progression.AbilityRarity.LEGENDARY -> AppColors.Gold
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = canAfford && ability.isUnlocked && !ability.isPurchased) {
                onPurchase()
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (ability.isUnlocked) {
            BorderStroke(2.dp, borderColor)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (ability.isPurchased) "✅" else "🔒",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ability.name,
                        color = AppColors.Gold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ability.rarity.name,
                        color = borderColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Cost/Status
                if (ability.isPurchased) {
                    Text(
                        text = "Rank ${ability.currentRank}/${ability.maxRank}",
                        color = AppColors.Parchment,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "💰 ${ability.cost}",
                        color = if (canAfford) AppColors.Gold else Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = ability.description,
                color = AppColors.Parchment,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            // XP Granted
            Text(
                text = "+${ability.xpGranted} XP",
                color = AppColors.Gold,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // Upgrade Button (if purchased and can upgrade)
            if (ability.isPurchased && ability.canUpgrade()) {
                Spacer(modifier = Modifier.height(8.dp))
                WesternSecondaryButton(
                    text = "Upgrade (💰 ${ability.upgradeCost()})",
                    onClick = onUpgrade,
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Skill Card Component
 * Displays a skill with purchase/level up options
 */
@Composable
fun SkillCard(
    skill: Skill,
    canAfford: Boolean,
    onPurchase: () -> Unit,
    onLevelUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !skill.isUnlocked -> Color.Gray.copy(alpha = 0.5f)
        skill.isPurchased -> AppColors.MediumWood
        else -> AppColors.AgedWood
    }
    
    val borderColor = when (skill.rarity) {
        com.trashapp.gcms.progression.SkillRarity.COMMON -> AppColors.Brass
        com.trashapp.gcms.progression.SkillRarity.UNCOMMON -> Color(0xFF4CAF50)
        com.trashapp.gcms.progression.SkillRarity.RARE -> Color(0xFF2196F3)
        com.trashapp.gcms.progression.SkillRarity.EPIC -> Color(0xFF9C27B0)
        com.trashapp.gcms.progression.SkillRarity.LEGENDARY -> AppColors.Gold
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = canAfford && skill.isUnlocked && !skill.isPurchased) {
                onPurchase()
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (skill.isUnlocked) {
            BorderStroke(2.dp, borderColor)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (skill.isPurchased) "✅" else "🔒",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.name,
                        color = AppColors.Gold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = skill.rarity.name,
                        color = borderColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Cost/Status
                if (skill.isPurchased) {
                    Text(
                        text = "Lvl ${skill.currentLevel}/${skill.maxLevel}",
                        color = AppColors.Parchment,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "💰 ${skill.cost}",
                        color = if (canAfford) AppColors.Gold else Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = skill.description,
                color = AppColors.Parchment,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            // XP Granted
            Text(
                text = "+${skill.xpGranted} XP",
                color = AppColors.Gold,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // Level Progress Bar
            if (skill.isPurchased) {
                Spacer(modifier = Modifier.height(8.dp))
                XPProgressBar(
                    progress = skill.levelProgress(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Level Up Button (if purchased and can level up)
            if (skill.isPurchased && skill.canLevelUp()) {
                Spacer(modifier = Modifier.height(8.dp))
                WesternSecondaryButton(
                    text = "Level Up (💰 ${skill.levelUpCost()})",
                    onClick = onLevelUp,
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}