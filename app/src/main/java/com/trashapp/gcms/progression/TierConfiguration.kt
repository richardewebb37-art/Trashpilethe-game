package com.trashapp.gcms.progression

/**
 * Tier Configuration Data Class
 * Defines content scaling for each progression tier
 */
data class TierConfiguration(
    val tier: Tier,
    val numSkills: IntRange,
    val abilitiesPerSkill: IntRange,
    val pointCostRange: IntRange,
    val abilityRarities: List<AbilityRarity>,
    val skillRarities: List<SkillRarity>
)

/**
 * Configuration manager for all tiers
 * Defines how many skills/abilities per tier and their characteristics
 */
class TierConfigurationManager {
    private val configurations = mapOf(
        Tier.LIFE to TierConfiguration(
            tier = Tier.LIFE,
            numSkills = 4..4,
            abilitiesPerSkill = 8..10,
            pointCostRange = 10..20,
            abilityRarities = listOf(AbilityRarity.COMMON, AbilityRarity.COMMON, AbilityRarity.UNCOMMON),
            skillRarities = listOf(SkillRarity.COMMON, SkillRarity.UNCOMMON)
        ),
        Tier.BEGINNER to TierConfiguration(
            tier = Tier.BEGINNER,
            numSkills = 6..8,
            abilitiesPerSkill = 10..12,
            pointCostRange = 15..30,
            abilityRarities = listOf(AbilityRarity.COMMON, AbilityRarity.UNCOMMON, AbilityRarity.RARE),
            skillRarities = listOf(SkillRarity.COMMON, SkillRarity.UNCOMMON, SkillRarity.RARE)
        ),
        Tier.NOVICE to TierConfiguration(
            tier = Tier.NOVICE,
            numSkills = 8..10,
            abilitiesPerSkill = 10..15,
            pointCostRange = 20..40,
            abilityRarities = listOf(AbilityRarity.UNCOMMON, AbilityRarity.RARE, AbilityRarity.EPIC),
            skillRarities = listOf(SkillRarity.UNCOMMON, SkillRarity.RARE, SkillRarity.EPIC)
        ),
        Tier.HARD to TierConfiguration(
            tier = Tier.HARD,
            numSkills = 10..12,
            abilitiesPerSkill = 12..16,
            pointCostRange = 25..50,
            abilityRarities = listOf(AbilityRarity.RARE, AbilityRarity.EPIC, AbilityRarity.LEGENDARY),
            skillRarities = listOf(SkillRarity.RARE, SkillRarity.EPIC, SkillRarity.LEGENDARY)
        ),
        Tier.EXPERT to TierConfiguration(
            tier = Tier.EXPERT,
            numSkills = 12..15,
            abilitiesPerSkill = 15..20,
            pointCostRange = 30..60,
            abilityRarities = listOf(AbilityRarity.EPIC, AbilityRarity.LEGENDARY),
            skillRarities = listOf(SkillRarity.EPIC, SkillRarity.LEGENDARY)
        ),
        Tier.MASTER to TierConfiguration(
            tier = Tier.MASTER,
            numSkills = 15..20,
            abilitiesPerSkill = 18..25,
            pointCostRange = 40..80,
            abilityRarities = listOf(AbilityRarity.LEGENDARY),
            skillRarities = listOf(SkillRarity.LEGENDARY)
        )
    )

    fun getConfiguration(tier: Tier): TierConfiguration {
        return configurations[tier]!!
    }

    fun getAllConfigurations(): List<TierConfiguration> {
        return Tier.values().map { configurations[it]!! }
    }
}