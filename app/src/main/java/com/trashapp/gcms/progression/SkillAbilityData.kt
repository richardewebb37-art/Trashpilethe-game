package com.trashapp.gcms.progression

/**
 * Complete Skills and Abilities Data
 * Contains all ~100 skills/abilities for the TRASH game
 * Organized by category and level
 */
object SkillAbilityData {
    
    /**
     * Get all skills
     */
    fun getAllSkills(): List<Skill> {
        return generalSkills +
               combatSkills +
               defenseSkills +
               supportSkills +
               magicSkills +
               movementSkills +
               precisionSkills +
               powerSkills +
               mentalSkills +
               masterSkills
    }
    
    /**
     * Get all abilities
     */
    fun getAllAbilities(): List<Ability> {
        return generalAbilities +
               combatAbilities +
               defenseAbilities +
               supportAbilities +
               magicAbilities +
               movementAbilities +
               precisionAbilities +
               powerAbilities +
               mentalAbilities +
               masterAbilities
    }
    
    // ==================== GENERAL / PROGRESSION ====================
    
    private val generalSkills = listOf(
        Skill(
            id = "skill_quick_learner",
            name = "Quick Learner",
            description = "+10% bonus XP from all matches. Level up faster, unlock content sooner.",
            tier = Tier.NEWBIE,
            levelRequired = 3,
            cost = 3,
            xpGranted = 10,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.COMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_focused_mind",
            name = "Focused Mind",
            description = "Turn timer +2 seconds (30s → 32s). Extra thinking time reduces rushed mistakes.",
            tier = Tier.NEWBIE,
            levelRequired = 5,
            cost = 5,
            xpGranted = 15,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.COMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_resourceful",
            name = "Resourceful",
            description = "When drawing from deck, see top 2 cards and choose 1. Strategic advantage.",
            tier = Tier.NEWBIE,
            levelRequired = 12,
            cost = 8,
            xpGranted = 25,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.UNCOMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_resource_hoarder",
            name = "Resource Hoarder",
            description = "+5% SP earned from matches. Unlock skills faster over time.",
            tier = Tier.NEWBIE,
            levelRequired = 18,
            cost = 12,
            xpGranted = 30,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.UNCOMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_efficiency",
            name = "Efficiency",
            description = "Turn timer +3 seconds (32s → 35s with Focused Mind). Critical for complex hands.",
            tier = Tier.BEGINNER,
            levelRequired = 25,
            cost = 15,
            xpGranted = 50,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.UNCOMMON,
            prerequisites = listOf("skill_focused_mind"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_tactical_recall",
            name = "Tactical Recall",
            description = "See entire match history (all cards played/discarded by everyone). Full information.",
            tier = Tier.BEGINNER,
            levelRequired = 35,
            cost = 20,
            xpGranted = 75,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.RARE,
            isUnlocked = false
        ),
        Skill(
            id = "skill_resource_management",
            name = "Resource Management",
            description = "+5% AP earned from matches. Unlock abilities faster.",
            tier = Tier.BEGINNER,
            levelRequired = 42,
            cost = 25,
            xpGranted = 100,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.RARE,
            prerequisites = listOf("skill_resource_hoarder"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_adaptive_strategy",
            name = "Adaptive Strategy",
            description = "See opponent's last 3 discarded cards. Know what they rejected.",
            tier = Tier.NOVICE,
            levelRequired = 55,
            cost = 30,
            xpGranted = 150,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_endurance",
            name = "Endurance",
            description = "Queen penalty reduced from -2 AP to -1 AP. Less punishment.",
            tier = Tier.NOVICE,
            levelRequired = 65,
            cost = 35,
            xpGranted = 175,
            category = SkillCategory.GENERAL,
            rarity = SkillRarity.EPIC,
            prerequisites = listOf("skill_iron_will"),
            isUnlocked = false
        )
    )
    
    private val generalAbilities = listOf(
        Ability(
            id = "ability_intuition",
            name = "Intuition",
            description = "Peek at top card of deck before deciding to draw. Eliminates blind guessing.",
            tier = Tier.NEWBIE,
            levelRequired = 8,
            cost = 6,
            xpGranted = 20,
            category = AbilityCategory.GENERAL,
            rarity = AbilityRarity.COMMON,
            usageLimitType = UsageLimitType.PER_ROUND,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = true
        ),
        Ability(
            id = "ability_lucky_break",
            name = "Lucky Break",
            description = "Reroll dice once after seeing result. Can turn bad rolls into huge bonuses.",
            tier = Tier.NEWBIE,
            levelRequired = 15,
            cost = 10,
            xpGranted = 35,
            category = AbilityCategory.GENERAL,
            rarity = AbilityRarity.UNCOMMON,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = true
        )
    )
    
    // ==================== COMBAT / OFFENSIVE ====================
    
    private val combatSkills = listOf(
        Skill(
            id = "skill_precision_strike",
            name = "Precision Strike",
            description = "See top card of deck at all times. Permanent peek. Huge advantage.",
            tier = Tier.BEGINNER,
            levelRequired = 22,
            cost = 10,
            xpGranted = 60,
            category = SkillCategory.COMBAT,
            rarity = SkillRarity.RARE,
            prerequisites = listOf("skill_focused_mind"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_critical_focus",
            name = "Critical Focus",
            description = "Dice bonus +1 on all rolls (max still 6). Better average scoring.",
            tier = Tier.BEGINNER,
            levelRequired = 38,
            cost = 18,
            xpGranted = 95,
            category = SkillCategory.COMBAT,
            rarity = SkillRarity.RARE,
            isUnlocked = false
        ),
        Skill(
            id = "skill_momentum",
            name = "Momentum",
            description = "After flipping 3 cards in a row, +2 seconds turn timer. Reward for hot streak.",
            tier = Tier.BEGINNER,
            levelRequired = 45,
            cost = 22,
            xpGranted = 110,
            category = SkillCategory.COMBAT,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_combo_mastery",
            name = "Combo Mastery",
            description = "If you place a card, you can flip another card in same turn. Double action.",
            tier = Tier.NOVICE,
            levelRequired = 58,
            cost = 28,
            xpGranted = 160,
            category = SkillCategory.COMBAT,
            rarity = SkillRarity.EPIC,
            prerequisites = listOf("skill_precision_strike"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_card_mastery",
            name = "Card Mastery",
            description = "All face-up numbered cards (A-10) score +1 point. Free bonus scoring.",
            tier = Tier.NOVICE,
            levelRequired = 70,
            cost = 35,
            xpGranted = 200,
            category = SkillCategory.COMBAT,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_archery",
            name = "Precision Play",
            description = "When placing Ace, 2, or 3, see opponent's matching slot. Information advantage.",
            tier = Tier.NOVICE,
            levelRequired = 75,
            cost = 40,
            xpGranted = 225,
            category = SkillCategory.COMBAT,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_elemental_mastery",
            name = "Elemental Mastery",
            description = "See which suits are remaining in deck at all times. Plan around probabilities.",
            tier = Tier.HARD,
            levelRequired = 135,
            cost = 60,
            xpGranted = 500,
            category = SkillCategory.COMBAT,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_arsenal_expert",
            name = "Arsenal Expert",
            description = "'Once per match' abilities can be used twice per match. Double offensive capability.",
            tier = Tier.EXPERT,
            levelRequired = 148,
            cost = 70,
            xpGranted = 600,
            category = SkillCategory.COMBAT,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        )
    )
    
    private val combatAbilities = listOf(
        Ability(
            id = "ability_swift_footwork",
            name = "Swift Footwork",
            description = "Skip your draw phase, immediately end turn (fast pass). Pressure opponent.",
            tier = Tier.BEGINNER,
            levelRequired = 28,
            cost = 8,
            xpGranted = 70,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.UNCOMMON,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 2,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_power_shot",
            name = "Power Shot",
            description = "Force opponent to discard their top 2 cards from deck. Sabotage their strategy.",
            tier = Tier.BEGINNER,
            levelRequired = 32,
            cost = 15,
            xpGranted = 80,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.OPPONENT,
            isUnlocked = false
        ),
        Ability(
            id = "ability_ruthless_assault",
            name = "Ruthless Assault",
            description = "Opponent's next draw is automatically discarded (they must draw again). Turn advantage.",
            tier = Tier.NOVICE,
            levelRequired = 62,
            cost = 25,
            xpGranted = 180,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.OPPONENT,
            isUnlocked = false
        ),
        Ability(
            id = "ability_dual_wielding",
            name = "Double Draw",
            description = "Draw 2 cards instead of 1 (must place or discard both). Double opportunities.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 85,
            cost = 30,
            xpGranted = 300,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_ROUND,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_flurry_attack",
            name = "Rapid Placement",
            description = "Place up to 3 cards in one turn (if you have them and they fit). Explosive turn.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 92,
            cost = 35,
            xpGranted = 350,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_berserker_rage",
            name = "All-In",
            description = "For this round, no turn timer (unlimited time per turn). Perfect calculation.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 105,
            cost = 45,
            xpGranted = 400,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_piercing_shot",
            name = "Guaranteed Draw",
            description = "Name a card rank, draw until you get it. Direct card acquisition.",
            tier = Tier.HARD,
            levelRequired = 115,
            cost = 40,
            xpGranted = 450,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 2,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.DECK,
            isUnlocked = false
        ),
        Ability(
            id = "ability_whirlwind_strike",
            name = "Mass Flip",
            description = "Flip 3 random face-down cards at once. Quick revelation and scoring.",
            tier = Tier.HARD,
            levelRequired = 125,
            cost = 50,
            xpGranted = 550,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_fireball",
            name = "Discard Bomb",
            description = "Choose any 3 cards from opponent's deck to be discarded. Devastating.",
            tier = Tier.EXPERT,
            levelRequired = 155,
            cost = 60,
            xpGranted = 750,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.OPPONENT,
            isUnlocked = false
        ),
        Ability(
            id = "ability_ice_shard",
            name = "Freeze Turn",
            description = "Opponent skips their next turn entirely. You get 2 turns in row.",
            tier = Tier.EXPERT,
            levelRequired = 162,
            cost = 55,
            xpGranted = 800,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.OPPONENT,
            isUnlocked = false
        ),
        Ability(
            id = "ability_lightning_bolt",
            name = "Instant Flip",
            description = "Flip ALL remaining face-down cards instantly. Game-ender.",
            tier = Tier.MASTER,
            levelRequired = 175,
            cost = 65,
            xpGranted = 1000,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_arcane_blast",
            name = "Ultimate Offense",
            description = "Combine all three: Discard 3 opponent cards, they skip turn, you flip all your cards. Nuclear option.",
            tier = Tier.MASTER,
            levelRequired = 190,
            cost = 100,
            xpGranted = 1500,
            category = AbilityCategory.COMBAT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.BOTH,
            prerequisites = listOf("ability_fireball", "ability_ice_shard", "ability_lightning_bolt"),
            isUnlocked = false
        )
    )
    
    // ==================== DEFENSE / SURVIVAL ====================
    
    private val defenseSkills = listOf(
        Skill(
            id = "skill_iron_will",
            name = "Iron Will",
            description = "King penalty reduced from -3 AP to -2 AP. Penalty mitigation.",
            tier = Tier.NEWBIE,
            levelRequired = 10,
            cost = 8,
            xpGranted = 25,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.COMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_tough_skin",
            name = "Tough Skin",
            description = "Jack penalty reduced from -1 AP to 0 AP. Jacks no longer penalize you.",
            tier = Tier.NEWBIE,
            levelRequired = 20,
            cost = 12,
            xpGranted = 40,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.UNCOMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_shield_mastery",
            name = "Shield Mastery",
            description = "Guard ability can be used twice per match. Double penalty protection.",
            tier = Tier.BEGINNER,
            levelRequired = 30,
            cost = 18,
            xpGranted = 85,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.RARE,
            prerequisites = listOf("ability_guard"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_regenerative_focus",
            name = "Regenerative Focus",
            description = "Regain 1 AP per round if you flip at least 2 cards. Sustain ability usage.",
            tier = Tier.NOVICE,
            levelRequired = 53,
            cost = 25,
            xpGranted = 140,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_steadfast",
            name = "Steadfast",
            description = "Cannot be affected by opponent's offensive abilities. Passive defense.",
            tier = Tier.NOVICE,
            levelRequired = 78,
            cost = 35,
            xpGranted = 215,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_magic_resistance",
            name = "Magic Resistance",
            description = "All penalties reduced by 1 (minimum 0). Permanent penalty reduction.",
            tier = Tier.HARD,
            levelRequired = 118,
            cost = 50,
            xpGranted = 450,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_tactical_defense",
            name = "Tactical Defense",
            description = "See which offensive abilities opponent has unlocked. Plan defenses accordingly.",
            tier = Tier.HARD,
            levelRequired = 128,
            cost = 55,
            xpGranted = 520,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_protective_aura",
            name = "Protective Aura",
            description = "Joker penalty reduced from -20 AP to -10 AP. Makes Joker safer to use.",
            tier = Tier.EXPERT,
            levelRequired = 145,
            cost = 65,
            xpGranted = 650,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_stone_skin",
            name = "Stone Skin",
            description = "ALL face card penalties reduced to 0 (King, Queen, Jack). Only Joker can hurt you.",
            tier = Tier.EXPERT,
            levelRequired = 168,
            cost = 75,
            xpGranted = 850,
            category = SkillCategory.DEFENSE,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        )
    )
    
    private val defenseAbilities = listOf(
        Ability(
            id = "ability_guard",
            name = "Guard",
            description = "Protect one face-down card from penalties (choose at end of round). Zero penalty.",
            tier = Tier.NEWBIE,
            levelRequired = 13,
            cost = 5,
            xpGranted = 30,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.COMMON,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = true
        ),
        Ability(
            id = "ability_evasion",
            name = "Evasion",
            description = "If opponent uses offensive ability, negate it. Their ability is wasted.",
            tier = Tier.BEGINNER,
            levelRequired = 36,
            cost = 12,
            xpGranted = 90,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_ROUND,
            usageLimit = 1,
            effectType = EffectType.TRIGGERED,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_tactical_retreat",
            name = "Tactical Retreat",
            description = "End round immediately (both players score current hands as-is). Lock in your lead.",
            tier = Tier.BEGINNER,
            levelRequired = 48,
            cost = 15,
            xpGranted = 115,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.BOTH,
            isUnlocked = false
        ),
        Ability(
            id = "ability_shield_wall",
            name = "Shield Wall",
            description = "ALL face-down cards immune to penalties this round. Complete protection.",
            tier = Tier.NOVICE,
            levelRequired = 68,
            cost = 25,
            xpGranted = 190,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_parry",
            name = "Parry",
            description = "Reflect opponent's offensive ability back at them. Devastating counter.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 88,
            cost = 30,
            xpGranted = 320,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.TRIGGERED,
            effectTarget = EffectTarget.OPPONENT,
            isUnlocked = false
        ),
        Ability(
            id = "ability_fortify",
            name = "Fortify",
            description = "Reduce ALL penalties by 50% this round. Halve all penalties.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 98,
            cost = 35,
            xpGranted = 380,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_last_stand",
            name = "Last Stand",
            description = "If you would lose match, negate loss and continue (one-time save). Miracle recovery.",
            tier = Tier.EXPERT,
            levelRequired = 158,
            cost = 70,
            xpGranted = 780,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.TRIGGERED,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_magic_ward",
            name = "Magic Ward",
            description = "Complete immunity to ALL effects for entire round (offense, penalties, everything). Invincibility.",
            tier = Tier.MASTER,
            levelRequired = 180,
            cost = 80,
            xpGranted = 1100,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_protective_bubble",
            name = "Protective Bubble",
            description = "Zero penalties for entire match (all rounds). Pure offensive freedom.",
            tier = Tier.MASTER,
            levelRequired = 195,
            cost = 90,
            xpGranted = 1400,
            category = AbilityCategory.DEFENSE,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            prerequisites = listOf("skill_stone_skin", "ability_magic_ward"),
            isUnlocked = false
        )
    )
    
    // ==================== SUPPORT / TACTICAL ====================
    
    private val supportSkills = listOf(
        Skill(
            id = "skill_alertness",
            name = "Alertness",
            description = "See opponent's hand size at all times. Track their progress, adjust urgency.",
            tier = Tier.NEWBIE,
            levelRequired = 7,
            cost = 7,
            xpGranted = 20,
            category = SkillCategory.SUPPORT,
            rarity = SkillRarity.COMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_team_strategist",
            name = "Team Strategist",
            description = "See how many of each card type (A, 2, 3, etc.) remain in deck. Calculate probabilities.",
            tier = Tier.BEGINNER,
            levelRequired = 24,
            cost = 15,
            xpGranted = 65,
            category = SkillCategory.SUPPORT,
            rarity = SkillRarity.UNCOMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_supportive_presence",
            name = "Supportive Presence",
            description = "+10% AP earned from matches. Major boost.",
            tier = Tier.BEGINNER,
            levelRequired = 40,
            cost = 20,
            xpGranted = 100,
            category = SkillCategory.SUPPORT,
            rarity = SkillRarity.RARE,
            isUnlocked = false
        ),
        Skill(
            id = "skill_battlefield_awareness",
            name = "Battlefield Awareness",
            description = "See when opponent draws from deck vs. discard pile. Track their strategy.",
            tier = Tier.NOVICE,
            levelRequired = 60,
            cost = 28,
            xpGranted = 165,
            category = SkillCategory.SUPPORT,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_tactical_planning",
            name = "Tactical Planning",
            description = "At start of round, see your entire hand face-up for 3 seconds, then cards flip face-down. Perfect information.",
            tier = Tier.NOVICE,
            levelRequired = 72,
            cost = 32,
            xpGranted = 205,
            category = SkillCategory.SUPPORT,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_encouraging_aura",
            name = "Encouraging Aura",
            description = "+15% XP earned from matches. Stacks with Quick Learner for +25% total XP. Massive leveling speed.",
            tier = Tier.HARD,
            levelRequired = 112,
            cost = 45,
            xpGranted = 425,
            category = SkillCategory.SUPPORT,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_inspire_allies",
            name = "Inspire Allies",
            description = "All abilities cost -5 AP (minimum 1 AP). More ability usage per match. Economic advantage.",
            tier = Tier.MASTER,
            levelRequired = 172,
            cost = 70,
            xpGranted = 950,
            category = SkillCategory.SUPPORT,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        )
    )
    
    private val supportAbilities = listOf(
        Ability(
            id = "ability_quick_healer",
            name = "Quick Healer",
            description = "Remove 1 face card penalty after round ends. Damage control.",
            tier = Tier.BEGINNER,
            levelRequired = 50,
            cost = 18,
            xpGranted = 120,
            category = AbilityCategory.SUPPORT,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_swift_recovery",
            name = "Swift Recovery",
            description = "Gain +5 seconds turn timer for next turn only. Perfect for complex decisions.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 95,
            cost = 35,
            xpGranted = 375,
            category = AbilityCategory.SUPPORT,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 2,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_diplomacy",
            name = "Diplomacy",
            description = "Trade 1 card with opponent (both choose 1 card from hand, swap). Creative strategy.",
            tier = Tier.HARD,
            levelRequired = 132,
            cost = 50,
            xpGranted = 540,
            category = AbilityCategory.SUPPORT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.BOTH,
            isUnlocked = false
        ),
        Ability(
            id = "ability_rally_cry",
            name = "Rally Cry",
            description = "Reroll dice and choose which result to keep (original or reroll). Guaranteed optimal dice.",
            tier = Tier.EXPERT,
            levelRequired = 152,
            cost = 60,
            xpGranted = 750,
            category = AbilityCategory.SUPPORT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_cleanse",
            name = "Cleanse",
            description = "Remove ALL penalties from entire match retroactively. Fresh start scoring.",
            tier = Tier.MASTER,
            levelRequired = 185,
            cost = 75,
            xpGranted = 1150,
            category = AbilityCategory.SUPPORT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        )
    )
    
    // ==================== MAGIC / ARCANE ====================
    
    private val magicSkills = listOf(
        Skill(
            id = "skill_elemental_fusion",
            name = "Elemental Fusion",
            description = "Cards of same rank but different suits can be placed in same slot (stack). Double value per slot.",
            tier = Tier.EXPERT,
            levelRequired = 142,
            cost = 65,
            xpGranted = 580,
            category = SkillCategory.MAGIC,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_mystic_insight",
            name = "Mystic Insight",
            description = "See probabilities of drawing each card rank from deck. Mathematical precision.",
            tier = Tier.EXPERT,
            levelRequired = 165,
            cost = 80,
            xpGranted = 820,
            category = SkillCategory.MAGIC,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_arcane_mastery",
            name = "Arcane Mastery",
            description = "All Magic abilities can be used twice per match, cost -10 AP. Ultimate magic power.",
            tier = Tier.MASTER,
            levelRequired = 200,
            cost = 100,
            xpGranted = 1600,
            category = SkillCategory.MAGIC,
            rarity = SkillRarity.LEGENDARY,
            prerequisites = listOf("skill_elemental_fusion", "skill_mystic_insight", "ability_arcane_shield"),
            isUnlocked = false
        )
    )
    
    private val magicAbilities = listOf(
        Ability(
            id = "ability_mana_surge",
            name = "Mana Surge",
            description = "Next ability used costs 0 AP. Combo setup. Use expensive ability for free.",
            tier = Tier.BEGINNER,
            levelRequired = 26,
            cost = 10,
            xpGranted = 75,
            category = AbilityCategory.MAGIC,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_enchantment",
            name = "Card Blessing",
            description = "Choose 1 card in hand, it scores double points. Significant scoring boost.",
            tier = Tier.BEGINNER,
            levelRequired = 44,
            cost = 20,
            xpGranted = 105,
            category = AbilityCategory.MAGIC,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_ROUND,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_curse_break",
            name = "Penalty Removal",
            description = "Remove all current penalties before they apply (at round end). Zero penalties.",
            tier = Tier.NOVICE,
            levelRequired = 56,
            cost = 25,
            xpGranted = 155,
            category = AbilityCategory.MAGIC,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_summoning",
            name = "Wild Card",
            description = "Joker appears in your next draw (guaranteed). Use it strategically.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 82,
            cost = 35,
            xpGranted = 290,
            category = AbilityCategory.MAGIC,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.DECK,
            isUnlocked = false
        ),
        Ability(
            id = "ability_time_warp",
            name = "Time Warp",
            description = "Take 2 consecutive turns before opponent's next turn. Double turn burst.",
            tier = Tier.HARD,
            levelRequired = 122,
            cost = 60,
            xpGranted = 480,
            category = AbilityCategory.MAGIC,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_arcane_shield",
            name = "Arcane Shield",
            description = "First offensive ability used against you per match is automatically negated. Free protection.",
            tier = Tier.MASTER,
            levelRequired = 178,
            cost = 70,
            xpGranted = 1070,
            category = AbilityCategory.MAGIC,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PASSIVE,
            usageLimit = 0,
            effectType = EffectType.PASSIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_teleportation",
            name = "Card Swap",
            description = "Swap positions of any 2 cards in your hand. Perfect positioning.",
            tier = Tier.MASTER,
            levelRequired = 188,
            cost = 85,
            xpGranted = 1250,
            category = AbilityCategory.MAGIC,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        )
    )
    
    // ==================== MOVEMENT / EVASION ====================
    
    private val movementSkills = listOf(
        Skill(
            id = "skill_parkour",
            name = "Parkour",
            description = "Once per round, move 1 face-up card to different slot. Mistake correction.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 90,
            cost = 35,
            xpGranted = 355,
            category = SkillCategory.MOVEMENT,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_evasive_roll",
            name = "Evasive Roll",
            description = "Passive dodge on first two penalties per match (automatic). Free saves.",
            tier = Tier.EXPERT,
            levelRequired = 160,
            cost = 60,
            xpGranted = 790,
            category = SkillCategory.MOVEMENT,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        )
    )
    
    private val movementAbilities = listOf(
        Ability(
            id = "ability_sprint",
            name = "Sprint",
            description = "Skip waiting for animations, instantly resolve your turn. Speed up gameplay.",
            tier = Tier.NEWBIE,
            levelRequired = 16,
            cost = 6,
            xpGranted = 45,
            category = AbilityCategory.MOVEMENT,
            rarity = AbilityRarity.COMMON,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 3,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = true
        ),
        Ability(
            id = "ability_dodge",
            name = "Dodge",
            description = "Avoid next penalty (triggers automatically when penalty would occur). Take 0 penalty.",
            tier = Tier.BEGINNER,
            levelRequired = 34,
            cost = 10,
            xpGranted = 88,
            category = AbilityCategory.MOVEMENT,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 2,
            effectType = EffectType.TRIGGERED,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_acrobatics",
            name = "Slot Skip",
            description = "Place a card in wrong slot but it still counts. Emergency placement.",
            tier = Tier.NOVICE,
            levelRequired = 64,
            cost = 15,
            xpGranted = 185,
            category = AbilityCategory.MOVEMENT,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_ROUND,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_shadow_step",
            name = "Shadow Step",
            description = "Become 'invisible' for 1 round (opponent can't see your actions). Surprise factor.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 108,
            cost = 40,
            xpGranted = 410,
            category = AbilityCategory.MOVEMENT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_blink",
            name = "Blink",
            description = "Skip directly to round end (auto-complete turn with best possible outcome). Perfect turn.",
            tier = Tier.HARD,
            levelRequired = 138,
            cost = 50,
            xpGranted = 510,
            category = AbilityCategory.MOVEMENT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_leap",
            name = "Leap",
            description = "Skip entire round, advance to next round with current hand state preserved. Move to next round instantly.",
            tier = Tier.MASTER,
            levelRequired = 182,
            cost = 75,
            xpGranted = 1120,
            category = AbilityCategory.MOVEMENT,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        )
    )
    
    // ==================== PRECISION / TECHNIQUE ====================
    
    private val precisionSkills = listOf(
        Skill(
            id = "skill_dead_eye",
            name = "Dead Eye",
            description = "When drawing, 10% higher chance of drawing cards you need. Better odds.",
            tier = Tier.NEWBIE,
            levelRequired = 14,
            cost = 12,
            xpGranted = 35,
            category = SkillCategory.PRECISION,
            rarity = SkillRarity.UNCOMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_sniper_focus",
            name = "Sniper Focus",
            description = "20% higher chance of drawing needed cards (stacks with Dead Eye for 30% total). Better luck.",
            tier = Tier.BEGINNER,
            levelRequired = 38,
            cost = 18,
            xpGranted = 95,
            category = SkillCategory.PRECISION,
            rarity = SkillRarity.RARE,
            prerequisites = listOf("skill_dead_eye"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_aim_stability",
            name = "Aim Stability",
            description = "Never draw Joker unless you specifically want it. No accidental Joker penalty.",
            tier = Tier.NOVICE,
            levelRequired = 52,
            cost = 25,
            xpGranted = 135,
            category = SkillCategory.PRECISION,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_chain_combo",
            name = "Chain Combo",
            description = "If you place 3+ cards in one round, +3 dice multiplier on that round's dice roll. Huge bonus.",
            tier = Tier.NOVICE,
            levelRequired = 74,
            cost = 30,
            xpGranted = 210,
            category = SkillCategory.PRECISION,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_reflex_boost",
            name = "Reflex Boost",
            description = "If opponent flips card, you see what it is for 2 seconds before it's hidden again. Glimpse their cards.",
            tier = Tier.HARD,
            levelRequired = 120,
            cost = 45,
            xpGranted = 455,
            category = SkillCategory.PRECISION,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_master_tactician",
            name = "Master Tactician",
            description = "See ALL remaining cards in deck at all times (perfect information). Zero luck factor.",
            tier = Tier.MASTER,
            levelRequired = 192,
            cost = 80,
            xpGranted = 1360,
            category = SkillCategory.PRECISION,
            rarity = SkillRarity.LEGENDARY,
            prerequisites = listOf("ability_perfect_timing", "ability_precision_aim"),
            isUnlocked = false
        )
    )
    
    private val precisionAbilities = listOf(
        Ability(
            id = "ability_perfect_timing",
            name = "Perfect Timing",
            description = "See exact order of next 5 cards in deck. Plan 5 turns ahead. Perfect knowledge.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 100,
            cost = 30,
            xpGranted = 395,
            category = AbilityCategory.PRECISION,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.DECK,
            isUnlocked = false
        ),
        Ability(
            id = "ability_precision_aim",
            name = "Precision Aim",
            description = "Swap any 2 cards in your hand (face-up or face-down). Optimized hand arrangement.",
            tier = Tier.EXPERT,
            levelRequired = 150,
            cost = 55,
            xpGranted = 740,
            category = AbilityCategory.PRECISION,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_ROUND,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        )
    )
    
    // ==================== POWER / STRENGTH ====================
    
    private val powerSkills = listOf(
        Skill(
            id = "skill_titan_strength",
            name = "Titan Strength",
            description = "All your face-up cards score +1 point permanently. Major scoring buff.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 94,
            cost = 40,
            xpGranted = 370,
            category = SkillCategory.POWER,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        )
    )
    
    private val powerAbilities = listOf(
        Ability(
            id = "ability_heavy_strike",
            name = "Big Play",
            description = "Place card scores +5 points (one card, your choice). Significant scoring boost.",
            tier = Tier.BEGINNER,
            levelRequired = 46,
            cost = 12,
            xpGranted = 110,
            category = AbilityCategory.POWER,
            rarity = AbilityRarity.RARE,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_crushing_blow",
            name = "Penalty Amplifier",
            description = "Opponent's next penalty is doubled. Punish their mistakes harder.",
            tier = Tier.NOVICE,
            levelRequired = 66,
            cost = 25,
            xpGranted = 195,
            category = AbilityCategory.POWER,
            rarity = AbilityRarity.EPIC,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.OPPONENT,
            isUnlocked = false
        ),
        Ability(
            id = "ability_earth_shatter",
            name = "Deck Shuffle",
            description = "Force immediate deck reshuffle (including discard pile). Chaos factor, information denial.",
            tier = Tier.HARD,
            levelRequired = 116,
            cost = 45,
            xpGranted = 445,
            category = AbilityCategory.POWER,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.DECK,
            isUnlocked = false
        ),
        Ability(
            id = "ability_mighty_roar",
            name = "Intimidation",
            description = "Opponent's turn timer reduced by 50% for next 2 turns. Pressure them into mistakes.",
            tier = Tier.EXPERT,
            levelRequired = 144,
            cost = 60,
            xpGranted = 720,
            category = AbilityCategory.POWER,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.OPPONENT,
            isUnlocked = false
        ),
        Ability(
            id = "ability_titans_wrath",
            name = "Titan's Wrath",
            description = "Guaranteed perfect dice roll (always rolls 6, multiplier applies). No randomness, pure power.",
            tier = Tier.MASTER,
            levelRequired = 198,
            cost = 100,
            xpGranted = 1480,
            category = AbilityCategory.POWER,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            prerequisites = listOf("skill_titan_strength", "ability_mighty_roar"),
            isUnlocked = false
        )
    )
    
    // ==================== MENTAL / SPECIAL ====================
    
    private val mentalSkills = listOf(
        Skill(
            id = "skill_willpower",
            name = "Willpower",
            description = "Immune to distraction (visual effects from opponent abilities don't affect your screen). Pure focus.",
            tier = Tier.NEWBIE,
            levelRequired = 11,
            cost = 10,
            xpGranted = 30,
            category = SkillCategory.MENTAL,
            rarity = SkillRarity.UNCOMMON,
            isUnlocked = true
        ),
        Skill(
            id = "skill_psychic_shield",
            name = "Psychic Shield",
            description = "Opponent cannot see which abilities you have unlocked. Information warfare.",
            tier = Tier.NOVICE,
            levelRequired = 54,
            cost = 20,
            xpGranted = 145,
            category = SkillCategory.MENTAL,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_insight",
            name = "Insight",
            description = "At round start, see 1 random opponent card (face-up view for 3 seconds). Peek at their strategy.",
            tier = Tier.NOVICE,
            levelRequired = 76,
            cost = 30,
            xpGranted = 220,
            category = SkillCategory.MENTAL,
            rarity = SkillRarity.EPIC,
            isUnlocked = false
        ),
        Skill(
            id = "skill_memory_recall",
            name = "Memory Recall",
            description = "View complete history of all cards discarded by everyone (not just last 3). Full discard archive.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 102,
            cost = 40,
            xpGranted = 405,
            category = SkillCategory.MENTAL,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_cosmic_insight",
            name = "Cosmic Insight",
            description = "See dice roll result BEFORE choosing to roll. Timing mastery.",
            tier = Tier.MASTER,
            levelRequired = 170,
            cost = 75,
            xpGranted = 940,
            category = SkillCategory.MENTAL,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_eternal_champion",
            name = "Eternal Champion",
            description = "+50% SP/AP earned from ALL matches permanently. Exponential progression boost.",
            tier = Tier.MASTER,
            levelRequired = 196,
            cost = 90,
            xpGranted = 1380,
            category = SkillCategory.MENTAL,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        )
    )
    
    private val mentalAbilities = listOf(
        Ability(
            id = "ability_dream_walk",
            name = "Prediction",
            description = "See opponent's next 3 intended moves (what they plan to do). Read their mind.",
            tier = Tier.HARD,
            levelRequired = 130,
            cost = 50,
            xpGranted = 525,
            category = AbilityCategory.MENTAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.OPPONENT,
            isUnlocked = false
        )
    )
    
    // ==================== ADVANCED / MASTER TIER ====================
    
    private val masterSkills = listOf(
        Skill(
            id = "skill_apex_strategist",
            name = "Apex Strategist",
            description = "See top 3 cards of deck at all times (upgraded from Precision Strike's 1 card). Plan 3 draws ahead.",
            tier = Tier.INTERMEDIATE,
            levelRequired = 110,
            cost = 50,
            xpGranted = 420,
            category = SkillCategory.SPECIAL,
            rarity = SkillRarity.LEGENDARY,
            prerequisites = listOf("skill_adaptive_strategy", "skill_team_strategist"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_legendary_focus",
            name = "Legendary Focus",
            description = "See ALL opponent cards face-up at all times (perfect information). Zero guessing.",
            tier = Tier.EXPERT,
            levelRequired = 154,
            cost = 70,
            xpGranted = 760,
            category = SkillCategory.SPECIAL,
            rarity = SkillRarity.LEGENDARY,
            prerequisites = listOf("skill_apex_strategist"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_supreme_efficiency",
            name = "Supreme Efficiency",
            description = "Turn timer +5 seconds (total +10 with Efficiency and Focused Mind). Nearly infinite time.",
            tier = Tier.HARD,
            levelRequired = 136,
            cost = 60,
            xpGranted = 530,
            category = SkillCategory.SPECIAL,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        ),
        Skill(
            id = "skill_unbreakable_will",
            name = "Unbreakable Will",
            description = "ALL face cards (J, Q, K) have 0 penalty. Only Joker can hurt you.",
            tier = Tier.EXPERT,
            levelRequired = 147,
            cost = 65,
            xpGranted = 670,
            category = SkillCategory.SPECIAL,
            rarity = SkillRarity.LEGENDARY,
            prerequisites = listOf("skill_tough_skin", "skill_endurance", "skill_iron_will"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_ultimate_mastery",
            name = "Ultimate Mastery",
            description = "+50% XP from matches, abilities cost -10 AP. Master-tier efficiency. Peak performance.",
            tier = Tier.MASTER,
            levelRequired = 176,
            cost = 100,
            xpGranted = 980,
            category = SkillCategory.SPECIAL,
            rarity = SkillRarity.LEGENDARY,
            prerequisites = listOf("skill_legendary_focus", "skill_supreme_efficiency"),
            isUnlocked = false
        ),
        Skill(
            id = "skill_synergy_master",
            name = "Synergy Master",
            description = "When using 2+ abilities in same round, second ability is free. Ability chains.",
            tier = Tier.MASTER,
            levelRequired = 184,
            cost = 85,
            xpGranted = 1170,
            category = SkillCategory.SPECIAL,
            rarity = SkillRarity.LEGENDARY,
            isUnlocked = false
        )
    )
    
    private val masterAbilities = listOf(
        Ability(
            id = "ability_phoenix_dive",
            name = "Resurrection",
            description = "If you lose round, resurrect with half your score added to next round. Comeback mechanic.",
            tier = Tier.EXPERT,
            levelRequired = 164,
            cost = 90,
            xpGranted = 810,
            category = AbilityCategory.SPECIAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.TRIGGERED,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_dragon_strike",
            name = "Dragon Strike",
            description = "Flip ALL remaining face-down cards and place ALL cards in hand instantly. Nuclear finish.",
            tier = Tier.MASTER,
            levelRequired = 174,
            cost = 85,
            xpGranted = 1030,
            category = AbilityCategory.SPECIAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_quantum_burst",
            name = "Quantum Burst",
            description = "Duplicate your dice roll result (if multiplier is 7 and you roll 5, you get 35×2 = 70 bonus). Astronomical scoring.",
            tier = Tier.MASTER,
            levelRequired = 186,
            cost = 95,
            xpGranted = 1180,
            category = AbilityCategory.SPECIAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_echo_storm",
            name = "Multi-Action",
            description = "Repeat your last 3 actions instantly (draws, placements, flips). Double productivity.",
            tier = Tier.MASTER,
            levelRequired = 194,
            cost = 100,
            xpGranted = 1340,
            category = AbilityCategory.SPECIAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_ultimate_charge",
            name = "Ultimate Charge",
            description = "Combine all three ultimate abilities into one mega-turn. Flip everything, double dice, repeat actions. Unstoppable.",
            tier = Tier.MASTER,
            levelRequired = 199,
            cost = 120,
            xpGranted = 1520,
            category = AbilityCategory.SPECIAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            prerequisites = listOf("ability_dragon_strike", "ability_quantum_burst", "ability_echo_storm"),
            isUnlocked = false
        ),
        Ability(
            id = "ability_phase_shift",
            name = "Dimensional Escape",
            description = "Enter 'ghost mode' - play entire round with perfect information, then decide if you want to keep it or redo. Time trial.",
            tier = Tier.MASTER,
            levelRequired = 200,
            cost = 110,
            xpGranted = 1550,
            category = AbilityCategory.SPECIAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            isUnlocked = false
        ),
        Ability(
            id = "ability_spirit_summon",
            name = "Perfect Draw",
            description = "Draw exact cards you need for ALL remaining slots. Perfect hand completion.",
            tier = Tier.MASTER,
            levelRequired = 197,
            cost = 100,
            xpGranted = 1470,
            category = AbilityCategory.SPECIAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.DECK,
            isUnlocked = false
        ),
        Ability(
            id = "ability_elemental_storm",
            name = "Chaos Mode",
            description = "For entire round, ALL rules are optional (place cards anywhere, no penalties, unlimited actions). God mode. Total freedom.",
            tier = Tier.MASTER,
            levelRequired = 200,
            cost = 150,
            xpGranted = 1600,
            category = AbilityCategory.SPECIAL,
            rarity = AbilityRarity.LEGENDARY,
            usageLimitType = UsageLimitType.PER_MATCH,
            usageLimit = 1,
            effectType = EffectType.ACTIVE,
            effectTarget = EffectTarget.SELF,
            prerequisites = listOf("ability_ultimate_charge", "ability_phase_shift", "ability_spirit_summon"),
            isUnlocked = false
        )
    )
}