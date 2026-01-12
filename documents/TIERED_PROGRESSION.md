# TRASH - Tiered Progression System Documentation

## Overview

TRASH features a comprehensive tiered progression system with 200 levels, dynamic XP scaling, and interdependent skill/ability trees. This system provides players with a deep, engaging progression experience that scales appropriately from beginner to master level.

## Table of Contents

1. [Tier System](#tier-system)
2. [XP System](#xp-system)
3. [Points System](#points-system)
4. [Skills & Abilities](#skills--abilities)
5. [Unlock Logic](#unlock-logic)
6. [Content Generation](#content-generation)
7. [Dynamic Level Ceiling](#dynamic-level-ceiling)
8. [Technical Implementation](#technical-implementation)

---

## Tier System

### Tier Structure

The progression system is divided into 6 tiers, each covering specific level ranges:

| Tier | Levels | Description | Content Scale |
|------|--------|-------------|---------------|
| **Life** | 1-5 | Introductory abilities/skills; tutorials and starter moves | 4 skills, 8-10 abilities each |
| **Beginner** | 6-20 | Basic abilities; start unlocking XP-driven progression | 6-8 skills, 10-12 abilities each |
| **Novice** | 21-50 | Intermediate abilities/skills; some combinations required | 8-10 skills, 10-15 abilities each |
| **Hard** | 51-80 | Stronger abilities; skill synergy required | 10-12 skills, 12-16 abilities each |
| **Expert** | 81-140 | Advanced abilities/skills; significant XP required | 12-15 skills, 15-20 abilities each |
| **Master** | 141-200 | High-level abilities; challenging unlocks | 15-20 skills, 18-25 abilities each |

### Tier Characteristics

Each tier has unique characteristics that define gameplay experience:

- **Life**: Fast progression, low costs, basic mechanics
- **Beginner**: Moderate progression, introduces XP system
- **Novice**: Slower progression, requires strategic planning
- **Hard**: Challenging progression, rewards skill mastery
- **Expert**: Very slow progression, elite-level content
- **Master**: Extremely slow progression, ultimate power

---

## XP System

### Dynamic XP Scaling

XP requirements increase non-linearly with level using the formula:

```
XP_for_next_level = BaseXP * (Level^1.1) + Random(-5% to +5%)
```

### Base XP by Tier

| Tier | Base XP | Level Range | Example XP Requirements |
|------|---------|-------------|-------------------------|
| Life | 50 | 1-5 | Level 1→2: ~50 XP |
| Beginner | 100 | 6-20 | Level 10→11: ~1,000 XP |
| Novice | 300 | 21-50 | Level 35→36: ~6,000 XP |
| Hard | 600 | 51-80 | Level 65→66: ~15,000 XP |
| Expert | 1,200 | 81-140 | Level 110→111: ~60,000 XP |
| Master | 2,500 | 141-200 | Level 180→181: ~150,000 XP |

### XP Sources

XP is **ONLY** gained through purchasing abilities and skills:

- **Skill Purchase**: Grants XP based on tier and rarity
- **Ability Purchase**: Grants XP based on tier and rarity
- **Upgrades**: Grants additional XP multiplied by rank/level

### XP Loss & Penalty

Losing abilities/skills removes XP and may lower levels:

- **Refund**: Removes all XP gained from that ability/skill
- **Level Drop**: If XP falls below level threshold, player levels down
- **Regain Penalty**: Requires 10% extra XP to regain lost levels

```
XP_to_regain = lost_XP + (lost_XP × 1.1)
```

---

## Points System

### Point Sources

Points are earned through gameplay:

- **Score Conversion**: Game score converts to points
- **Achievements**: Bonus points for milestones
- **Daily Rewards**: Daily login bonuses
- **Win Streaks**: Bonus points for consecutive wins

### Point Costs

Point costs scale based on tier and include randomization:

```
Points_per_ability = BasePoints * TierMultiplier + Random(-10% to +10%)
```

### Cost Ranges by Tier

| Tier | Skill Cost Range | Ability Cost Range |
|------|------------------|-------------------|
| Life | 2-3 | 1-2 |
| Beginner | 5-8 | 2-4 |
| Novice | 10-18 | 4-9 |
| Hard | 20-35 | 8-17 |
| Expert | 40-70 | 15-35 |
| Master | 80-150 | 35-80 |

### Tier Multipliers

- **Life**: 1.0x
- **Beginner**: 1.5x
- **Novice**: 2.5x
- **Hard**: 4.0x
- **Expert**: 7.0x
- **Master**: 12.0x

---

## Skills & Abilities

### Skill Structure

Each **Skill** contains multiple **Abilities**:

- Skills are the parent nodes in the progression tree
- Abilities are child nodes unlocked by purchasing their parent skill
- Skills have 5 levels, Abilities have 10 ranks
- Both have randomized costs and XP values

### Ability Structure

Abilities provide specific gameplay benefits:

- **Combat**: Direct offensive abilities
- **Defense**: Defensive and protective abilities
- **Utility**: Helpful passive abilities
- **Luck**: Chance-based abilities
- **Strategy**: Tactical abilities
- **Special**: Unique, powerful abilities

### Rarity System

Items have 5 rarity tiers with XP multipliers:

| Rarity | XP Multiplier | Distribution |
|--------|---------------|--------------|
| Common | 1.0x | 50% |
| Uncommon | 1.25x | 30% |
| Rare | 1.5x | 15% |
| Epic | 2.0x | 4% |
| Legendary | 3.0x | 1% |

---

## Unlock Logic

### Skill Unlocking

Skills can be unlocked when:

1. Player has sufficient points
2. All prerequisite skills are unlocked
3. Player's tier allows access to that skill

### Ability Unlocking

Abilities are unlocked automatically when their parent skill is purchased.

### Prerequisite System

- **Life Tier**: No prerequisites
- **Other Tiers**: 30% chance of 1 prerequisite from previous tier
- Creates natural progression flow
- Prevents skipping too far ahead

### Dependency Tree

```
Life Skill 1 → Beginner Skill 1 → Novice Skill 1 → Hard Skill 1 → Expert Skill 1 → Master Skill 1
     ↓              ↓                 ↓               ↓                ↓                ↓
   8 Abilities   10 Abilities     12 Abilities    16 Abilities    20 Abilities    25 Abilities
```

---

## Content Generation

### Randomized Generation

The system generates randomized content to ensure variety:

1. **Number of Skills**: Random within tier range
2. **Abilities per Skill**: Random within tier range
3. **Point Costs**: ±10% variance from base
4. **XP Values**: ±5% variance from base
5. **Rarity**: Weighted random distribution

### Content Quantities

| Tier | Total Skills | Total Abilities | Avg XP per Skill | Avg XP per Ability |
|------|--------------|-----------------|------------------|-------------------|
| Life | 4 | 32-40 | 50-200 | 10-20 |
| Beginner | 6-8 | 60-96 | 100-500 | 20-40 |
| Novice | 8-10 | 80-150 | 200-1000 | 40-75 |
| Hard | 10-12 | 120-192 | 400-2000 | 80-150 |
| Expert | 12-15 | 180-300 | 800-4000 | 150-300 |
| Master | 15-20 | 270-500 | 1600-8000 | 300-600 |

**Total Content**: ~55-69 skills, ~732-1,278 abilities

---

## Dynamic Level Ceiling

### Soft Cap System

The system uses a "soft cap" approach:

1. **No Hard Level Cap**: Players can theoretically reach level 200
2. **XP Availability Controls Progression**: Limited by purchasable content
3. **Dynamic Expansion**: New content releases increase XP potential

### Level Ceiling Calculation

```
Dynamic_Ceiling = Total_XP_Available / XP_per_Level
```

### Example Scenarios

**Scenario 1: Early Game**
- Available XP: 500
- Level achievable: ~3
- Plenty of content left to explore

**Scenario 2: Mid Game**
- Available XP: 25,000
- Level achievable: ~35
- Still have multiple tiers to unlock

**Scenario 3: Late Game**
- Available XP: 2,500,000
- Level achievable: ~180
- Only Master tier content remains

---

## Technical Implementation

### Core Components

#### 1. Tier Enum
```kotlin
enum class Tier(
    val displayName: String,
    val levelRange: IntRange,
    val description: String
)
```

#### 2. XPSystem
- Dynamic XP calculation per level
- Level-to-XP conversion
- Progress tracking
- Penalty calculation

#### 3. PointsSystem
- Dynamic point cost calculation
- Tier multipliers
- Random variance application

#### 4. ProgressionTree
- Skill and ability management
- Purchase/refund logic
- Unlock dependency checking
- XP calculation

#### 5. ContentGenerator
- Randomized content generation
- Skill/ability creation
- Name/description generation
- Rarity assignment

### Data Models

#### Skill
```kotlin
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val tier: Tier,
    val category: String,
    val rarity: Rarity,
    val cost: Int,
    val xpGranted: Int,
    val maxLevel: Int,
    val currentLevel: Int,
    val prerequisites: List<String>,
    val unlocks: List<String>,
    val abilities: List<Ability>,
    val isUnlocked: Boolean
)
```

#### Ability
```kotlin
data class Ability(
    val id: String,
    val name: String,
    val description: String,
    val tier: Tier,
    val category: String,
    val rarity: Rarity,
    val cost: Int,
    val xpGranted: Int,
    val maxRank: Int,
    val currentRank: Int,
    val skillId: String,
    val isUnlocked: Boolean
)
```

### Key Formulas

#### Level from XP
```
Level = (XP / 100) + 1
```

#### XP for Next Level
```
XP_next = BaseXP × (Level^1.1) + Random(-5% to +5%)
```

#### Ability Cost
```
Cost = BasePoints × TierMultiplier + Random(-10% to +10%)
```

#### XP to Regain
```
XP_regain = lost_XP + (lost_XP × 1.1)
```

---

## Balance Considerations

### Early Game (Levels 1-20)
- Fast progression to engage players
- Low costs, high rewards
- Tutorial-like experience

### Mid Game (Levels 21-80)
- Balanced progression
- Moderate costs
- Strategic choices matter

### Late Game (Levels 81-140)
- Slow but rewarding progression
- High costs, high rewards
- Requires mastery

### End Game (Levels 141-200)
- Very slow progression
- Ultimate power
- Prestige content

---

## Future Expansion

### Adding New Content

To expand the system without breaking it:

1. **Add New Skills**: Simply add to existing tiers
2. **Add New Tiers**: Extend the Tier enum (beyond level 200)
3. **Add New Abilities**: Add to existing skills
4. **Adjust Formulas**: Modify base XP/point values

### Content Updates

The system is designed to be:
- **Data-Driven**: Content defined in data structures
- **Easily Extensible**: Add new content without code changes
- **Backward Compatible**: New content doesn't break existing saves
- **Balanced**: Formulas maintain balance across all tiers

---

## Conclusion

The tiered progression system provides a deep, engaging experience that scales appropriately from beginner to master level. With 200 levels, dynamic XP scaling, and randomized content, players will have hundreds of hours of progression content to explore.

The system's soft cap approach ensures that progression feels natural and rewarding without imposing artificial limits, while the randomized generation ensures variety and replayability.