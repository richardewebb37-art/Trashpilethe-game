package com.trashapp.gcms.commands

/**
 * Progression Commands
 * Commands related to purchasing and managing abilities/skills in the progression tree
 */

/**
 * Buy Ability Command
 * Purchase an ability from the progression tree
 */
data class BuyAbilityCommand(
    val playerId: String,
    val abilityId: String
) : GCMSCommand()

/**
 * Buy Skill Command
 * Purchase a skill from the progression tree
 */
data class BuySkillCommand(
    val playerId: String,
    val skillId: String
) : GCMSCommand()

/**
 * Upgrade Ability Command
 * Upgrade an already purchased ability
 */
data class UpgradeAbilityCommand(
    val playerId: String,
    val abilityId: String
) : GCMSCommand()

/**
 * Level Up Skill Command
 * Level up an already purchased skill
 */
data class LevelUpSkillCommand(
    val playerId: String,
    val skillId: String
) : GCMSCommand()

/**
 * Refund Ability Command
 * Sell/refund an ability (lose XP)
 */
data class RefundAbilityCommand(
    val playerId: String,
    val abilityId: String
) : GCMSCommand()

/**
 * Refund Skill Command
 * Sell/refund a skill (lose XP)
 */
data class RefundSkillCommand(
    val playerId: String,
    val skillId: String
) : GCMSCommand()

/**
 * Add Points Command
 * Add points to player's balance (from score, actions, etc.)
 */
data class AddPointsCommand(
    val playerId: String,
    val amount: Int
) : GCMSCommand()

/**
 * Spend Points Command
 * Spend points (consumed by purchase commands)
 */
data class SpendPointsCommand(
    val playerId: String,
    val amount: Int
) : GCMSCommand()

/**
 * Add XP Command
 * Add XP directly (from achievements, bonuses)
 */
data class AddXPCommand(
    val playerId: String,
    val amount: Int
) : GCMSCommand()

/**
 * Lose XP Command
 * Remove XP (penalties, selling abilities)
 */
data class LoseXPCommand(
    val playerId: String,
    val amount: Int
) : GCMSCommand()