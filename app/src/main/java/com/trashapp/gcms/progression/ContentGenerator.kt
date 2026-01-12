package com.trashapp.gcms.progression

/**
 * Content Generator for Tiered Progression System
 * Generates randomized skills and abilities for each tier
 * Uses configuration to determine counts, costs, and rarities
 */
class ContentGenerator {
    private val random = java.util.Random()
    private val configManager = TierConfigurationManager()

    /**
     * Generate all skills and abilities for all tiers
     */
    fun generateAllContent(): Pair<Map<String, Skill>, Map<String, Ability>> {
        val allSkills = mutableMapOf<String, Skill>()
        val allAbilities = mutableMapOf<String, Ability>()
        
        for (tier in Tier.values()) {
            val (skills, abilities) = generateContentForTier(tier)
            allSkills.putAll(skills)
            allAbilities.putAll(abilities)
        }
        
        return Pair(allSkills, allAbilities)
    }

    /**
     * Generate skills and abilities for a specific tier
     */
    fun generateContentForTier(tier: Tier): Pair<Map<String, Skill>, Map<String, Ability>> {
        val config = configManager.getConfiguration(tier)
        val numSkills = randomInRange(config.numSkills)
        val skills = mutableMapOf<String, Skill>()
        val abilities = mutableMapOf<String, Ability>()
        
        for (i in 1..numSkills) {
            val skill = generateSkill(tier, config, i, abilities)
            skills[skill.id] = skill
        }
        
        return Pair(skills, abilities)
    }

    /**
     * Generate a single skill with its abilities
     */
    private fun generateSkill(
        tier: Tier,
        config: TierConfiguration,
        index: Int,
        abilitiesMap: MutableMap<String, Ability>
    ): Skill {
        val skillId = "skill_${tier.name.lowercase()}_$index"
        val skillName = generateSkillName(tier)
        val skillDescription = generateSkillDescription(tier)
        val skillCategory = generateSkillCategory(tier)
        val skillRarity = generateSkillRarity(config.skillRarities)
        val skillCost = generateSkillCost(tier, config)
        val skillXP = generateSkillXP(tier, skillRarity)
        
        // Generate abilities for this skill
        val numAbilities = randomInRange(config.abilitiesPerSkill)
        val abilityIds = mutableListOf<String>()
        
        for (abilityIndex in 1..numAbilities) {
            val ability = generateAbility(tier, config, skillId, abilityIndex)
            abilitiesMap[ability.id] = ability
            abilityIds.add(ability.id)
        }
        
        return Skill(
            id = skillId,
            name = skillName,
            description = skillDescription,
            tier = tier,
            cost = skillCost,
            xpGranted = skillXP,
            category = skillCategory,
            rarity = skillRarity,
            maxLevel = 5,
            currentLevel = 0,
            prerequisites = generatePrerequisites(tier),
            unlocks = emptyList(),
            abilities = abilityIds,
            isUnlocked = tier == Tier.LIFE, // Life tier skills start unlocked
            isPurchased = false
        )
    }

    /**
     * Generate a single ability
     */
    private fun generateAbility(
        tier: Tier,
        config: TierConfiguration,
        skillId: String,
        index: Int
    ): Ability {
        val abilityId = "ability_${skillId}_$index"
        val abilityName = generateAbilityName(tier)
        val abilityDescription = generateAbilityDescription(tier)
        val abilityCategory = generateAbilityCategory(tier)
        val abilityRarity = generateAbilityRarity(config.abilityRarities)
        val abilityCost = generateAbilityCost(tier, config)
        val abilityXP = generateAbilityXP(tier, abilityRarity)
        
        return Ability(
            id = abilityId,
            name = abilityName,
            description = abilityDescription,
            tier = tier,
            cost = abilityCost,
            xpGranted = abilityXP,
            category = abilityCategory,
            rarity = abilityRarity,
            maxRank = 1 + random.nextInt(9), // 1-10 ranks
            currentRank = 0,
            skillId = skillId,
            prerequisites = emptyList(), // Unlocked by skill
            unlocks = emptyList(),
            isUnlocked = false, // Unlocked when parent skill is purchased
            isPurchased = false,
            isActive = false
        )
    }

    // Helper methods for content generation

    private fun randomInRange(range: IntRange): Int {
        return range.random()
    }

    private fun generateSkillCost(tier: Tier, config: TierConfiguration): Int {
        val baseCost = config.pointCostRange.random()
        val variance = (random.nextDouble() - 0.5) * 0.2 // ±10% variance
        return (baseCost * (1 + variance)).toInt().coerceAtLeast(1)
    }

    private fun generateAbilityCost(tier: Tier, config: TierConfiguration): Int {
        val baseCost = config.pointCostRange.random() / 2
        val variance = (random.nextDouble() - 0.5) * 0.2 // ±10% variance
        return (baseCost * (1 + variance)).toInt().coerceAtLeast(1)
    }

    private fun generateSkillXP(tier: Tier, rarity: SkillRarity): Int {
        val baseXP = 100 * (tier.ordinal + 1)
        val multiplier = when (rarity) {
            SkillRarity.COMMON -> 1.0f
            SkillRarity.UNCOMMON -> 1.25f
            SkillRarity.RARE -> 1.5f
            SkillRarity.EPIC -> 2.0f
            SkillRarity.LEGENDARY -> 3.0f
        }
        return (baseXP * multiplier).toInt()
    }

    private fun generateAbilityXP(tier: Tier, rarity: AbilityRarity): Int {
        val baseXP = 75 * (tier.ordinal + 1)
        val multiplier = when (rarity) {
            AbilityRarity.COMMON -> 1.0f
            AbilityRarity.UNCOMMON -> 1.25f
            AbilityRarity.RARE -> 1.5f
            AbilityRarity.EPIC -> 2.0f
            AbilityRarity.LEGENDARY -> 3.0f
        }
        return (baseXP * multiplier).toInt()
    }

    private fun generateSkillRarity(rarities: List<SkillRarity>): SkillRarity {
        val weights = rarities.map { rarity ->
            when (rarity) {
                SkillRarity.COMMON -> 50
                SkillRarity.UNCOMMON -> 30
                SkillRarity.RARE -> 15
                SkillRarity.EPIC -> 4
                SkillRarity.LEGENDARY -> 1
            }
        }
        
        val totalWeight = weights.sum()
        var randomWeight = random.nextInt(totalWeight)
        
        for (i in rarities.indices) {
            randomWeight -= weights[i]
            if (randomWeight <= 0) {
                return rarities[i]
            }
        }
        
        return rarities.first()
    }

    private fun generateAbilityRarity(rarities: List<AbilityRarity>): AbilityRarity {
        val weights = rarities.map { rarity ->
            when (rarity) {
                AbilityRarity.COMMON -> 50
                AbilityRarity.UNCOMMON -> 30
                AbilityRarity.RARE -> 15
                AbilityRarity.EPIC -> 4
                AbilityRarity.LEGENDARY -> 1
            }
        }
        
        val totalWeight = weights.sum()
        var randomWeight = random.nextInt(totalWeight)
        
        for (i in rarities.indices) {
            randomWeight -= weights[i]
            if (randomWeight <= 0) {
                return rarities[i]
            }
        }
        
        return rarities.first()
    }

    private fun generateSkillName(tier: Tier): String {
        val skillNames = mapOf(
            Tier.LIFE to listOf(
                "Quick Draw", "Sharpshooter", "Lucky Shot", "Dusty Trail"
            ),
            Tier.BEGINNER to listOf(
                "Saddle Up", "Horseback Rider", "Saloon Brawler",
                "Card Shark", "Poker Face", "Whiskey Wisdom"
            ),
            Tier.NOVICE to listOf(
                "Sheriff's Badge", "Deputy Duty", "Posse Leader",
                "Gunslinger", "Outlaw Hunter", "Gold Prospector"
            ),
            Tier.HARD to listOf(
                "Legendary Gunslinger", "Master of Cards",
                "Sheriff of the West", "Outlaw King"
            ),
            Tier.EXPERT to listOf(
                "Wild West Legend", "Frontier Myth",
                "Gunslinger's Ghost", "Outlaw's Last Stand"
            ),
            Tier.MASTER to listOf(
                "The Immortal Sheriff", "Gunslinger's Legacy",
                "Outlaw's Redemption", "Frontier God"
            )
        )
        
        return skillNames[tier]?.random() ?: "Unknown Skill"
    }

    private fun generateAbilityName(tier: Tier): String {
        val abilityNames = mapOf(
            Tier.LIFE to listOf(
                "Quick Reflexes", "Sharp Eyes", "Steady Hand", "Basic Draw"
            ),
            Tier.BEGINNER to listOf(
                "Enhanced Draw", "Card Counting", "Bluffing",
                "Quick Reload", "Sharp Focus", "Lucky Charm"
            ),
            Tier.NOVICE to listOf(
                "Master Draw", "Card Manipulation", "Expert Bluff",
                "Perfect Aim", "Sharp Instincts", "Fortune's Favor"
            ),
            Tier.HARD to listOf(
                "Legendary Draw", "Godlike Bluff", "Perfect Cards",
                "Infinite Luck", "Supreme Focus", "Ultimate Aim"
            ),
            Tier.EXPERT to listOf(
                "Mythical Draw", "Impossible Bluff", "Transcendent Cards",
                "Eternal Luck", "Godlike Focus", "Perfect Reality"
            ),
            Tier.MASTER to listOf(
                "Transcendent Draw", "Beyond Bluff", "Ascended Cards",
                "Infinite Fortune", "Perfect Eternity", "Supreme Reality"
            )
        )
        
        return abilityNames[tier]?.random() ?: "Unknown Ability"
    }

    private fun generateSkillDescription(tier: Tier): String {
        val descriptions = listOf(
            "A fundamental skill for any cowboy.",
            "Essential techniques for surviving the Wild West.",
            "Advanced tactics for seasoned gunslingers.",
            "Expert techniques for legendary outlaws.",
            "Mythical powers known only to the greatest.",
            "Transcendent abilities beyond mortal understanding."
        )
        return descriptions[tier.ordinal]
    }

    private fun generateAbilityDescription(tier: Tier): String {
        val descriptions = listOf(
            "A basic ability to help you get started.",
            "An improved ability with greater power.",
            "An advanced ability for experienced players.",
            "An expert ability with significant impact.",
            "A legendary ability of immense power.",
            "A transcendent ability beyond mortal limits."
        )
        return descriptions[tier.ordinal]
    }

    private fun generateSkillCategory(tier: Tier): SkillCategory {
        val categories = listOf(
            SkillCategory.OFFENSE,
            SkillCategory.DEFENSE,
            SkillCategory.SUPPORT,
            SkillCategory.RESOURCE,
            SkillCategory.LUCK,
            SkillCategory.STRATEGY,
            SkillCategory.SPECIAL
        )
        return categories.random()
    }

    private fun generateAbilityCategory(tier: Tier): AbilityCategory {
        val categories = listOf(
            AbilityCategory.COMBAT,
            AbilityCategory.DEFENSE,
            AbilityCategory.UTILITY,
            AbilityCategory.LUCK,
            AbilityCategory.STRATEGY,
            AbilityCategory.SPECIAL
        )
        return categories.random()
    }

    private fun generatePrerequisites(tier: Tier): List<String> {
        if (tier == Tier.LIFE) return emptyList()
        
        // 30% chance to have prerequisites (simplified for now)
        if (random.nextFloat() < 0.3f) {
            return listOf("placeholder_prerequisite")
        }
        
        return emptyList()
    }
}