package com.trashapp.gcms.trophy

import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.progression.Tier.*
import com.trashapp.gcms.trophy.TrophyRarity.*

/**
 * Generates trophy definitions for all levels 1-200
 * 
 * This creates a comprehensive trophy database with:
 * - Prerequisite-based trophies (abilities, skills, points)
 * - Level-based trophies
 * - Tier-specific themes
 * - Rarity distributions
 */
class TrophyGenerator {
    
    companion object {
        // Trophy name prefixes by tier
        private val TIER_PREFIXES = mapOf(
            LIFE to listOf("Little", "Tiny", "First", "Starter"),
            BEGINNER to listOf("Mighty", "Swift", "Brave", "Keen"),
            NOVICE to listOf("Daring", "Bold", "Cunning", "Sharp"),
            HARD to listOf("Fierce", "Determined", "Steadfast", "Resolute"),
            EXPERT to listOf("Legendary", "Masterful", "Supreme", "Elite"),
            MASTER to listOf("Divine", "Eternal", "Ultimate", "Ascended")
        )
        
        // Trophy name suffixes by category
        private val CATEGORY_SUFFIXES = mapOf(
            "combat" to listOf("Warrior", "Fighter", "Battler", "Gladiator"),
            "strategy" to listOf("Tactician", "Strategist", "Planner", "Mastermind"),
            "luck" to listOf("Fortune", "Lucky", "Blessed", "Charmed"),
            "defense" to listOf("Guardian", "Protector", "Defender", "Shield"),
            "offense" to listOf("Conqueror", "Vanquisher", "Destroyer", "Crusher"),
            "utility" to listOf("Helper", "Assist", "Support", "Aid"),
            "resource" to listOf("Collector", "Gatherer", "Hunter", "Seeker"),
            "milestone" to listOf("Achiever", "Reacher", "Milestone", "Landmark")
        )
        
        // Trophy templates for specific levels
        private val MILESTONE_TROPHIES = mapOf(
            5 to TrophyTemplate("First Steps", "Complete your first tier", emptyList(), emptyList(), 0),
            10 to TrophyTemplate("Rising Star", "Reach double digits in level", emptyList(), emptyList(), 0),
            20 to TrophyTemplate("Novice Breakthrough", "Complete the Beginner tier", emptyList(), emptyList(), 0),
            30 to TrophyTemplate("Veteran Player", "30 levels of experience", emptyList(), emptyList(), 0),
            50 to TrophyTemplate("Hard Challenger", "Enter the Hard tier", emptyList(), emptyList(), 0),
            75 to TrophyTemplate("Dedicated Gamer", "75 levels achieved", emptyList(), emptyList(), 0),
            100 to TrophyTemplate("Century Club", "Reach the century mark", emptyList(), emptyList(), 0),
            125 to TrophyTemplate("Elite Ascendant", "125 levels of mastery", emptyList(), emptyList(), 0),
            150 to TrophyTemplate("Expert Legend", "Reach the Expert tier pinnacle", emptyList(), emptyList(), 0),
            175 to TrophyTemplate("Master Craftsman", "175 levels of dedication", emptyList(), emptyList(), 0),
            200 to TrophyTemplate("Ultimate Champion", "Reach maximum level", emptyList(), emptyList(), 0)
        )
    }
    
    /**
     * Generate all trophies for levels 1-200
     */
    fun generateAllTrophies(): List<Trophy> {
        val trophies = mutableListOf<Trophy>()
        
        // Generate milestone trophies
        MILESTONE_TROPHIES.forEach { (level, template) ->
            val tier = Tier.getTierForLevel(level)
            trophies.add(createTrophyFromTemplate(level, tier, template, RARE))
        }
        
        // Generate tier-specific trophies
        trophies.addAll(generateTierSpecificTrophies())
        
        // Generate prerequisite-based trophies
        trophies.addAll(generatePrerequisiteTrophies())
        
        // Generate random filler trophies
        trophies.addAll(generateRandomFillerTrophies())
        
        return trophies.distinctBy { it.id }
    }
    
    /**
     * Generate trophies specific to each tier
     */
    private fun generateTierSpecificTrophies(): List<Trophy> {
        val trophies = mutableListOf<Trophy>()
        
        // Life Tier (1-5)
        for (level in 1..5) {
            trophies.add(Trophy(
                id = "life_milestone_$level",
                name = "Life Level $level",
                description = "Reach level $level in the Life tier",
                tier = LIFE,
                requiredLevel = level,
                rarity = COMMON
            ))
        }
        
        // Beginner Tier (6-20) - Combat and Strategy focus
        for (level in 6..20) {
            if (level % 5 == 0) {
                trophies.add(Trophy(
                    id = "beginner_combat_$level",
                    name = "Combat Initiate $level",
                    description = "Combat prowess at level $level",
                    tier = BEGINNER,
                    requiredLevel = level,
                    requiredPoints = level * 2,
                    rarity = UNCOMMON
                ))
            }
        }
        
        // Novice Tier (21-50) - Luck and Defense focus
        for (level in 21..50) {
            if (level % 7 == 0) {
                trophies.add(Trophy(
                    id = "novice_defense_$level",
                    name = "Defensive Mind $level",
                    description = "Master defense at level $level",
                    tier = NOVICE,
                    requiredLevel = level,
                    requiredPoints = level * 3,
                    rarity = RARE
                ))
            }
        }
        
        // Hard Tier (51-80) - Offense and Resource focus
        for (level in 51..80) {
            if (level % 5 == 0) {
                trophies.add(Trophy(
                    id = "hard_offense_$level",
                    name = "Offensive Power $level",
                    description = "Offensive mastery at level $level",
                    tier = HARD,
                    requiredLevel = level,
                    requiredPoints = level * 4,
                    rarity = EPIC
                ))
            }
        }
        
        // Expert Tier (81-140) - Multi-requirement trophies
        for (level in 81..140 step 10) {
            trophies.add(Trophy(
                id = "expert_mastery_$level",
                name = "Expert Mastery $level",
                description = "Demonstrate expert skills at level $level",
                tier = EXPERT,
                requiredLevel = level,
                requiredPoints = level * 5,
                requiredAbilities = listOf("Quick Draw", "Sheriff's Badge"),
                rarity = LEGENDARY
            ))
        }
        
        // Master Tier (141-200) - Ultimate trophies
        for (level in 141..200 step 10) {
            trophies.add(Trophy(
                id = "master_legend_$level",
                name = "Master Legend $level",
                description = "Achieve legendary status at level $level",
                tier = MASTER,
                requiredLevel = level,
                requiredPoints = level * 6,
                requiredAbilities = listOf("Quick Draw", "Sheriff's Badge", "Wild West Legend"),
                requiredSkills = listOf("Card Shark", "Iron Will"),
                rarity = MYTHIC
            ))
        }
        
        return trophies
    }
    
    /**
     * Generate trophies with prerequisites
     */
    private fun generatePrerequisiteTrophies(): List<Trophy> {
        val trophies = mutableListOf<Trophy>()
        
        // Ability-based trophies
        trophies.add(Trophy(
            id = "ability_quickdraw_master",
            name = "Quick Draw Master",
            description = "Master the Quick Draw ability",
            tier = BEGINNER,
            requiredLevel = 10,
            requiredAbilities = listOf("Quick Draw"),
            rarity = RARE
        ))
        
        trophies.add(Trophy(
            id = "ability_sheriff_protector",
            name = "Sheriff's Protector",
            description = "Wield the Sheriff's Badge effectively",
            tier = NOVICE,
            requiredLevel = 25,
            requiredAbilities = listOf("Sheriff's Badge"),
            requiredPoints = 50,
            rarity = EPIC
        ))
        
        trophies.add(Trophy(
            id = "ability_west_legend",
            name = "Wild West Legend",
            description = "Become a legend of the West",
            tier = MASTER,
            requiredLevel = 150,
            requiredAbilities = listOf("Wild West Legend"),
            requiredPoints = 500,
            rarity = MYTHIC
        ))
        
        // Skill-based trophies
        trophies.add(Trophy(
            id = "skill_cardshark_expert",
            name = "Card Shark Expert",
            description = "Master card reading skills",
            tier = NOVICE,
            requiredLevel = 30,
            requiredSkills = listOf("Card Shark"),
            rarity = RARE
        ))
        
        trophies.add(Trophy(
            id = "skill_ironwill_guardian",
            name = "Iron Will Guardian",
            description = "Unbreakable defense master",
            tier = HARD,
            requiredLevel = 60,
            requiredSkills = listOf("Iron Will"),
            requiredAbilities = listOf("Sheriff's Badge"),
            rarity = LEGENDARY
        ))
        
        // Combined requirement trophies
        trophies.add(Trophy(
            id = "combined_combant_specialist",
            name = "Combat Specialist",
            description = "Master combat abilities and skills",
            tier = HARD,
            requiredLevel = 55,
            requiredAbilities = listOf("Quick Draw", "Sheriff's Badge"),
            requiredSkills = listOf("Card Shark"),
            requiredPoints = 200,
            rarity = LEGENDARY
        ))
        
        trophies.add(Trophy(
            id = "combined_fortune_seeker",
            name = "Fortune Seeker",
            description = "Harness luck and resources",
            tier = EXPERT,
            requiredLevel = 100,
            requiredAbilities = listOf("Lucky Horseshoe", "Gold Rush"),
            requiredSkills = listOf("Fortune's Favor", "Resource Master"),
            requiredPoints = 300,
            rarity = LEGENDARY
        ))
        
        trophies.add(Trophy(
            id = "combined_ultimate_master",
            name = "Ultimate Master",
            description = "Master all aspects of the game",
            tier = MASTER,
            requiredLevel = 200,
            requiredAbilities = listOf("Quick Draw", "Sheriff's Badge", "Lucky Horseshoe", "Gold Rush", "Wild West Legend"),
            requiredSkills = listOf("Card Shark", "Iron Will", "Strategic Mind", "Resource Master", "Fortune's Favor"),
            requiredPoints = 1000,
            rarity = MYTHIC
        ))
        
        return trophies
    }
    
    /**
     * Generate random filler trophies for variety
     */
    private fun generateRandomFillerTrophies(): List<Trophy> {
        val trophies = mutableListOf<Trophy>()
        
        // Generate trophies for levels without specific ones
        for (level in 1..200) {
            val tier = Tier.getTierForLevel(level)
            
            // Skip levels that already have many trophies
            if (level % 10 == 0 || level % 5 == 0) continue
            
            // Random chance to generate trophy
            if (Math.random() < 0.3) {
                val category = listOf("combat", "strategy", "luck", "defense").random()
                val prefix = TIER_PREFIXES[tier]?.random() ?: "Brave"
                val suffix = CATEGORY_SUFFIXES[category]?.random() ?: "Achiever"
                
                trophies.add(Trophy(
                    id = "random_${category}_$level",
                    name = "$prefix $suffix",
                    description = "Achievement at level $level in $category",
                    tier = tier,
                    requiredLevel = level,
                    rarity = determineRandomRarity(tier)
                ))
            }
        }
        
        return trophies
    }
    
    /**
     * Determine random rarity based on tier
     */
    private fun determineRandomRarity(tier: Tier): TrophyRarity {
        return when (tier) {
            LIFE -> listOf(COMMON, UNCOMMON).random()
            BEGINNER -> listOf(COMMON, UNCOMMON, RARE).random()
            NOVICE -> listOf(UNCOMMON, RARE, EPIC).random()
            HARD -> listOf(RARE, EPIC, LEGENDARY).random()
            EXPERT -> listOf(EPIC, LEGENDARY, MYTHIC).random()
            MASTER -> listOf(LEGENDARY, MYTHIC).random()
        }
    }
    
    /**
     * Create trophy from template
     */
    private fun createTrophyFromTemplate(
        level: Int,
        tier: Tier,
        template: TrophyTemplate,
        rarity: TrophyRarity
    ): Trophy {
        return Trophy(
            id = "milestone_${level}",
            name = template.name,
            description = template.description,
            tier = tier,
            requiredLevel = level,
            requiredAbilities = template.requiredAbilities,
            requiredSkills = template.requiredSkills,
            requiredPoints = template.requiredPoints,
            rarity = rarity
        )
    }
}

/**
 * Trophy template for generation
 */
data class TrophyTemplate(
    val name: String,
    val description: String,
    val requiredAbilities: List<String>,
    val requiredSkills: List<String>,
    val requiredPoints: Int
)