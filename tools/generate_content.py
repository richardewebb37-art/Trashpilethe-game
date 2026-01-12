#!/usr/bin/env python3
"""
Generate content for TRASH game
Creates skills and abilities for all 6 tiers
"""

import json
import random
import os
from dataclasses import dataclass, asdict
from typing import List, Dict

# Configuration
TIER_CONFIG = {
    "Life": {
        "min_level": 1,
        "max_level": 5,
        "skill_count": (4, 6),
        "abilities_per_skill": (8, 12),
        "base_cost": (1, 10),
        "base_xp": (25, 50)
    },
    "Beginner": {
        "min_level": 6,
        "max_level": 20,
        "skill_count": (6, 9),
        "abilities_per_skill": (10, 14),
        "base_cost": (5, 25),
        "base_xp": (50, 100)
    },
    "Novice": {
        "min_level": 21,
        "max_level": 50,
        "skill_count": (8, 11),
        "abilities_per_skill": (12, 16),
        "base_cost": (10, 50),
        "base_xp": (100, 300)
    },
    "Hard": {
        "min_level": 51,
        "max_level": 80,
        "skill_count": (10, 13),
        "abilities_per_skill": (14, 18),
        "base_cost": (25, 100),
        "base_xp": (300, 600)
    },
    "Expert": {
        "min_level": 81,
        "max_level": 140,
        "skill_count": (12, 15),
        "abilities_per_skill": (16, 20),
        "base_cost": (50, 150),
        "base_xp": (600, 1200)
    },
    "Master": {
        "min_level": 141,
        "max_level": 200,
        "skill_count": (15, 19),
        "abilities_per_skill": (18, 25),
        "base_cost": (100, 225),
        "base_xp": (1200, 2500)
    }
}

RARITY_DISTRIBUTION = {
    "Life": {
        "Common": 0.60,
        "Uncommon": 0.30,
        "Rare": 0.10,
        "Epic": 0.00,
        "Legendary": 0.00,
        "Mythic": 0.00
    },
    "Beginner": {
        "Common": 0.40,
        "Uncommon": 0.35,
        "Rare": 0.20,
        "Epic": 0.05,
        "Legendary": 0.00,
        "Mythic": 0.00
    },
    "Novice": {
        "Common": 0.25,
        "Uncommon": 0.35,
        "Rare": 0.25,
        "Epic": 0.13,
        "Legendary": 0.02,
        "Mythic": 0.00
    },
    "Hard": {
        "Common": 0.15,
        "Uncommon": 0.30,
        "Rare": 0.30,
        "Epic": 0.20,
        "Legendary": 0.05,
        "Mythic": 0.00
    },
    "Expert": {
        "Common": 0.08,
        "Uncommon": 0.22,
        "Rare": 0.30,
        "Epic": 0.28,
        "Legendary": 0.10,
        "Mythic": 0.02
    },
    "Master": {
        "Common": 0.03,
        "Uncommon": 0.12,
        "Rare": 0.25,
        "Epic": 0.35,
        "Legendary": 0.20,
        "Mythic": 0.05
    }
}

RARITY_MULTIPLIERS = {
    "Common": 1.0,
    "Uncommon": 1.5,
    "Rare": 2.5,
    "Epic": 4.0,
    "Legendary": 7.0,
    "Mythic": 12.0
}

SKILL_CATEGORIES = ["Offense", "Defense", "Support", "Resource", "Luck", "Strategy", "Special"]
ABILITY_CATEGORIES = ["Combat", "Defense", "Utility", "Luck", "Strategy", "Special"]

# Wild West themed prefixes and suffixes
PREFIXES = [
    "Quick", "Sharp", "Silent", "Deadly", "Golden", "Rusty", "Wild", "Lucky",
    "Brave", "Swift", "Grim", "Savage", "Noble", "Wicked", "Swift", "Fierce",
    "Ancient", "Forgotten", "Legendary", "Mythic", "Divine", "Eternal"
]

SUFFIXES = [
    "Strike", "Guard", "Shot", "Blade", "Luck", "Fate", "Vengeance", "Justice",
    "Valor", "Honor", "Shadow", "Storm", "Thunder", "Light", "Dawn", "Dusk",
    "Rage", "Wrath", "Grace", "Power", "Mastery", "Domination", "Ascension"
]

VERBS = [
    "unlocks", "grants", "enhances", "boosts", "increases", "improves", "empowers",
    "fortifies", "amplifies", "magnifies", "intensifies", "strengthens", "augments"
]

NOUNS = [
    "damage", "defense", "speed", "luck", "accuracy", "critical hits", "evasion",
    "health", "resources", "abilities", "skills", "attacks", "blocks", "counters"
]

def get_rarity(tier: str) -> str:
    """Get random rarity based on tier distribution"""
    distribution = RARITY_DISTRIBUTION[tier]
    rand = random.random()
    cumulative = 0.0
    for rarity, probability in distribution.items():
        cumulative += probability
        if rand <= cumulative:
            return rarity
    return "Common"

def generate_skill_name(tier: str) -> str:
    """Generate a Wild West themed skill name"""
    prefix = random.choice(PREFIXES)
    suffix = random.choice(SUFFIXES)
    return f"{prefix} {suffix}"

def generate_ability_name(skill_name: str, tier: str) -> str:
    """Generate an ability name related to the skill"""
    prefix = random.choice(PREFIXES)
    verb = random.choice(VERBS)
    noun = random.choice(NOUNS)
    return f"{prefix} {verb} {noun}"

def generate_skill_description(tier: str, category: str) -> str:
    """Generate a skill description"""
    verbs = ["enhances", "boosts", "improves", "increases"]
    effects = ["your", "your character's", "the"]
    targets = ["combat abilities", "defensive capabilities", "luck", "strategy", "resources"]
    return f"This skill {random.choice(verbs)} {random.choice(effects)} {random.choice(targets)} with Wild West power."

def generate_ability_description(tier: str, rarity: str, category: str) -> str:
    """Generate an ability description"""
    multipliers = RARITY_MULTIPLIERS[rarity]
    verbs = ["unlocks", "grants", "enhances", "boosts", "increases"]
    effects = ["powerful", "significant", "moderate", "minor"]
    benefits = [
        f"{int(10 * multipliers)}% bonus",
        f"{int(5 * multipliers)}x multiplier",
        "enhanced effects",
        "improved capabilities",
        "special bonuses"
    ]
    return f"{random.choice(verbs)} {random.choice(effects)} {random.choice(benefits)}."

@dataclass
class Skill:
    id: str
    name: str
    description: str
    tier: str
    category: str
    rarity: str
    baseCost: int
    xpGranted: int
    maxLevel: int
    abilities: List[str]

@dataclass
class Ability:
    id: str
    name: str
    description: str
    tier: str
    category: str
    rarity: str
    baseCost: int
    xpGranted: int
    maxRank: int
    skillId: str

def generate_skills_for_tier(tier: str, skill_counter: Dict[str, int]) -> List[Skill]:
    """Generate skills for a specific tier"""
    config = TIER_CONFIG[tier]
    skill_count = random.randint(*config["skill_count"])
    
    skills = []
    for i in range(skill_count):
        category = random.choice(SKILL_CATEGORIES)
        rarity = get_rarity(tier)
        
        skill_id = f"skill_{tier.lower()}_{skill_counter[tier]}"
        skill_counter[tier] += 1
        
        # Apply rarity multiplier
        multiplier = RARITY_MULTIPLIERS[rarity]
        base_cost = random.randint(*config["base_cost"])
        cost = int(base_cost * multiplier)
        
        base_xp = random.randint(*config["base_xp"])
        xp = int(base_xp * multiplier)
        
        skill = Skill(
            id=skill_id,
            name=generate_skill_name(tier),
            description=generate_skill_description(tier, category),
            tier=tier,
            category=category,
            rarity=rarity,
            baseCost=cost,
            xpGranted=xp,
            maxLevel=random.randint(5, 20),
            abilities=[]
        )
        skills.append(skill)
    
    return skills

def generate_abilities_for_skill(skill: Skill, ability_counter: Dict[str, int]) -> List[Ability]:
    """Generate abilities for a specific skill"""
    config = TIER_CONFIG[skill.tier]
    ability_count = random.randint(*config["abilities_per_skill"])
    
    abilities = []
    for i in range(ability_count):
        category = random.choice(ABILITY_CATEGORIES)
        rarity = get_rarity(skill.tier)
        
        ability_id = f"ability_{skill.tier.lower()}_{ability_counter[skill.tier]}"
        ability_counter[skill.tier] += 1
        
        # Apply rarity multiplier
        multiplier = RARITY_MULTIPLIERS[rarity]
        base_cost = random.randint(*config["base_cost"])
        cost = int(base_cost * multiplier * 0.3)  # Abilities cheaper than skills
        
        base_xp = random.randint(*config["base_xp"])
        xp = int(base_xp * multiplier * 0.5)  # Abilities give less XP than skills
        
        ability = Ability(
            id=ability_id,
            name=generate_ability_name(skill.name, skill.tier),
            description=generate_ability_description(skill.tier, rarity, category),
            tier=skill.tier,
            category=category,
            rarity=rarity,
            baseCost=cost,
            xpGranted=xp,
            maxRank=random.randint(3, 10),
            skillId=skill.id
        )
        abilities.append(ability)
    
    return abilities

def generate_all_content():
    """Generate all skills and abilities"""
    print("🎮 Starting content generation for TRASH game...")
    
    # Create output directory
    output_dir = "app/src/main/assets/progression"
    os.makedirs(output_dir, exist_ok=True)
    
    # Generate skills for all tiers
    all_skills = []
    all_abilities = []
    skill_counter = {tier: 0 for tier in TIER_CONFIG.keys()}
    ability_counter = {tier: 0 for tier in TIER_CONFIG.keys()}
    
    for tier in TIER_CONFIG.keys():
        skills = generate_skills_for_tier(tier, skill_counter)
        all_skills.extend(skills)
        
        # Generate abilities for each skill
        for skill in skills:
            abilities = generate_abilities_for_skill(skill, ability_counter)
            all_abilities.extend(abilities)
            
            # Link abilities to skill
            skill.abilities = [ab.id for ab in abilities]
    
    # Save to JSON files
    skills_file = os.path.join(output_dir, "skills.json")
    abilities_file = os.path.join(output_dir, "abilities.json")
    
    with open(skills_file, 'w') as f:
        json.dump([asdict(skill) for skill in all_skills], f, indent=2)
    
    with open(abilities_file, 'w') as f:
        json.dump([asdict(ability) for ability in all_abilities], f, indent=2)
    
    print(f"💾 Saved {len(all_skills)} skills to skills.json")
    print(f"💾 Saved {len(all_abilities)} abilities to abilities.json")
    
    # Print statistics
    print_statistics(all_skills, all_abilities)
    
    print("✅ Content generation complete!")
    print(f"📁 Files saved to: {output_dir}")

def print_statistics(skills: List[Skill], abilities: List[Ability]):
    """Print content statistics"""
    print("\n📊 Content Statistics:")
    print("━" * 50)
    
    # Stats by tier
    for tier in TIER_CONFIG.keys():
        tier_skills = [s for s in skills if s.tier == tier]
        tier_abilities = [a for a in abilities if a.tier == tier]
        
        print(f"\n🏷️  Tier: {tier} (Levels {TIER_CONFIG[tier]['min_level']}-{TIER_CONFIG[tier]['max_level']})")
        print(f"   Skills: {len(tier_skills)}")
        print(f"   Abilities: {len(tier_abilities)}")
        if tier_skills:
            print(f"   Avg Abilities per Skill: {len(tier_abilities) / len(tier_skills):.1f}")
        
        # Rarity distribution
        print("   Rarity Distribution:")
        for rarity in ["Common", "Uncommon", "Rare", "Epic", "Legendary", "Mythic"]:
            count = len([a for a in tier_abilities if a.rarity == rarity])
            if count > 0:
                print(f"     {rarity}: {count}")
    
    # Total stats
    print("\n📈 Total Statistics:")
    print(f"   Total Skills: {len(skills)}")
    print(f"   Total Abilities: {len(abilities)}")
    if skills:
        print(f"   Avg Abilities per Skill: {len(abilities) / len(skills):.1f}")
    
    # Cost ranges
    costs = [ab.baseCost for ab in abilities]
    print("\n💰 Cost Ranges:")
    print(f"   Min: {min(costs)}")
    print(f"   Max: {max(costs)}")
    print(f"   Avg: {sum(costs) / len(costs):.1f}")
    
    # XP ranges
    xp_values = [ab.xpGranted for ab in abilities] + [s.xpGranted for s in skills]
    print("\n⭐ XP Ranges:")
    print(f"   Min: {min(xp_values)}")
    print(f"   Max: {max(xp_values)}")
    print(f"   Avg: {sum(xp_values) / len(xp_values):.1f}")

if __name__ == "__main__":
    generate_all_content()