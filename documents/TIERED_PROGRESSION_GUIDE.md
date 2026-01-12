# TRASH - Tiered Progression System Guide

## Overview

The TRASH game features a comprehensive tiered progression system with 200 levels, organized into 6 thematic tiers. This system provides a natural progression curve with dynamic XP scaling, interdependent skill trees, and randomized content for variety.

---

## 1. Tier System

### Tiers and Level Ranges

| Tier | Levels | Description |
|------|--------|-------------|
| **Life** | 1-5 | Introductory abilities/skills; tutorials and starter moves |
| **Beginner** | 6-20 | Basic abilities; start unlocking XP-driven progression |
| **Novice** | 21-50 | Intermediate abilities/skills; some combinations required |
| **Hard** | 51-80 | Stronger abilities; skill synergy required |
| **Expert** | 81-140 | Advanced abilities/skills; significant XP required |
| **Master** | 141-200 | High-level abilities; challenging unlocks |

### Tier Characteristics

Each tier has unique characteristics:

#### Life Tier (Levels 1-5)
- **Skills per tier**: 4
- **Abilities per skill**: 8-10
- **Point cost range**: 10-20 points
- **Rarities**: Common, Uncommon
- **XP per skill**: 50-100 XP
- **XP per ability**: 10-20 XP

#### Beginner Tier (Levels 6-20)
- **Skills per tier**: 6-8
- **Abilities per skill**: 10-12
- **Point cost range**: 15-30 points
- **Rarities**: Common, Uncommon, Rare
- **XP per skill**: 100-200 XP
- **XP per ability**: 20-40 XP

#### Novice Tier (Levels 21-50)
- **Skills per tier**: 8-10
- **Abilities per skill**: 10-15
- **Point cost range**: 20-40 points
- **Rarities**: Uncommon, Rare, Epic
- **XP per skill**: 200-400 XP
- **XP per ability**: 40-80 XP

#### Hard Tier (Levels 51-80)
- **Skills per tier**: 10-12
- **Abilities per skill**: 12-16
- **Point cost range**: 25-50 points
- **Rarities**: Rare, Epic, Legendary
- **XP per skill**: 400-800 XP
- **XP per ability**: 80-150 XP

#### Expert Tier (Levels 81-140)
- **Skills per tier**: 12-15
- **Abilities per skill**: 15-20
- **Point cost range**: 30-60 points
- **Rarities**: Epic, Legendary
- **XP per skill**: 800-1600 XP
- **XP per ability**: 150-300 XP

#### Master Tier (Levels 141-200)
- **Skills per tier**: 15-20
- **Abilities per skill**: 18-25
- **Point cost range**: 40-80 points
- **Rarities**: Legendary
- **XP per skill**: 1600-3000+ XP
- **XP per ability**: 300-500+ XP

---

## 2. Dynamic XP System

### XP Scaling Formula

XP required for each level follows a non-linear scaling formula:

```
XP_for_next_level = BaseXP * (Level^1.1) + Random(-5% to +5%)
```

Where:
- **BaseXP** varies by tier (50-2500)
- **Level** is the current level (1-200)
- **Random variance** is ±5% to prevent predictability

### Base XP by Tier

| Tier | Base XP per Level |
|------|-------------------|
| Life | 50 |
| Beginner | 100 |
| Novice | 300 |
| Hard | 600 |
| Expert | 1200 |
| Master | 2500 |

### XP Examples

- **Level 1 → Level 2**: ~50 XP
- **Level 20 → Level 21**: ~1,500 XP
- **Level 50 → Level 51**: ~4,800 XP
- **Level 100 → Level 101**: ~12,000 XP
- **Level 150 → Level 151**: ~22,500 XP
- **Level 199 → Level 200**: ~35,000 XP

### Total XP to Reach Levels

| Level | Total XP Required |
|-------|-------------------|
| 1 | 0 |
| 5 | ~250 |
| 20 | ~2,000 |
| 50 | ~12,000 |
| 100 | ~50,000 |
| 150 | ~150,000 |
| 200 | ~350,000 |

---

## 3. Points System

### Earning Points

Players earn points through:
- **Score**: Points awarded based on match performance
- **Objectives**: Bonus points for completing objectives
- **Achievements**: Special awards for milestones
- **Daily bonuses**: Regular point incentives

### Spending Points

Points are used to purchase:
- **Skills**: Unlock entire skill trees (includes all abilities)
- **Abilities**: Individual ability upgrades (after skill unlock)

### Point Cost Calculation

```
Ability_Cost = BasePoints * TierMultiplier + Random(-10% to +10%)
Skill_Cost = Ability_Cost * (2.0 to 3.0)
```

#### Base Points by Tier

| Tier | Base Points | Tier Multiplier |
|------|-------------|-----------------|
| Life | 1 | 1.0x |
| Beginner | 3 | 1.5x |
| Novice | 5 | 2.5x |
| Hard | 10 | 4.0x |
| Expert | 20 | 7.0x |
| Master | 40 | 12.0x |

### Point Cost Examples

- **Life ability**: 1-2 points
- **Life skill**: 2-6 points
- **Expert ability**: 25-45 points
- **Expert skill**: 50-135 points
- **Master ability**: 40-75 points
- **Master skill**: 80-225 points

---

## 4. Skill Tree System

### Skill Structure

Each skill contains:
- **Multiple abilities** (randomized count based on tier)
- **Skill-level progression** (levels 1-10)
- **Prerequisites** (from previous tier skills)
- **Unlock mechanics** (purchasing skill unlocks all abilities)

### Ability Structure

Each ability:
- **Belongs to a skill** (unlocked when skill is purchased)
- **Has individual ranks** (ranks 1-10)
- **Independent progression** (can upgrade separately)
- **No prerequisites** (unlocked by parent skill)

### Unlock Flow

```
1. Player earns points → 
2. Purchases skill → 
3. Skill unlocks ALL abilities within it → 
4. Player gains XP from skill + all abilities → 
5. Player levels up →
6. Higher tier content becomes available
```

### Dependency System

- **Life tier**: No prerequisites
- **Other tiers**: 30% chance of 1 prerequisite from previous tier
- Creates natural progression path while allowing some flexibility

---

## 5. Progression Flow

### Starting Out (Levels 1-5)

1. **Starting points**: 100 points
2. **First purchase**: Activates XP progression
3. **Life tier skills**: 4 skills available, 8-10 abilities each
4. **Quick progression**: Low costs, fast leveling

### Growing (Levels 6-50)

1. **Beginner/Novice tiers**: More skills and abilities
2. **Moderate costs**: 15-40 points per ability
3. **Building foundation**: Unlock core skills and abilities
4. **XP accumulation**: Steady growth

### Advancing (Levels 51-140)

1. **Hard/Expert tiers**: Challenging content
2. **Higher costs**: 25-60 points per ability
3. **Skill synergy**: Requires strategic planning
4. **Significant XP**: Each purchase grants substantial XP

### Mastering (Levels 141-200)

1. **Master tier**: Ultimate challenges
2. **Premium costs**: 40-80 points per ability
3. **Legendary content**: Only the rarest abilities
4. **Prestige**: Achieving maximum progression

---

## 6. Rarity System

### Rarity Tiers

| Rarity | XP Multiplier | Distribution |
|--------|---------------|--------------|
| Common | 1.0x | Most common in lower tiers |
| Uncommon | 1.25x | Balanced distribution |
| Rare | 1.5x | Less common, mid-high tiers |
| Epic | 2.0x | Rare, high tiers |
| Legendary | 3.0x | Very rare, highest tiers |

### Rarity by Tier

- **Life**: Common, Uncommon
- **Beginner**: Common, Uncommon, Rare
- **Novice**: Uncommon, Rare, Epic
- **Hard**: Rare, Epic, Legendary
- **Expert**: Epic, Legendary
- **Master**: Legendary only

---

## 7. Randomization System

### What's Randomized?

1. **XP per level**: ±5% variance
2. **Point costs**: ±10% variance
3. **Abilities per skill**: Random within tier range
4. **Skill prerequisites**: 30% chance of dependency
5. **Ability rarity**: Weighted random selection
6. **Content names**: Randomly selected from tier-appropriate pools

### Randomization Benefits

- **Variety**: No two playthroughs identical
- **Replayability**: Different content each time
- **Natural feel**: Prevents predictable patterns
- **Balance**: Weighted randomness maintains fairness

---

## 8. Dynamic Level Ceiling

### Soft Cap System

There is no hard cap on levels. Progression is naturally limited by:

1. **Available content**: XP only from purchased skills/abilities
2. **Point scarcity**: Higher tiers require many points
3. **Time investment**: Reaching level 200 requires significant gameplay

### Expanding Content

- **New releases**: Add new skills/abilities to increase XP potential
- **Dynamic ceiling**: Level cap rises naturally with content
- **No artificial limits**: Players progress as far as content allows

---

## 9. Penalty System

### XP Loss

Losing skills/abilities removes XP:
- **Skill refund**: Removes skill XP + all ability XP
- **Ability refund**: Removes ability XP

### Level Loss

Losing enough XP can lower level:
- **Penalty multiplier**: 1.1x (10% penalty)
- **Regaining requires**: Original XP + 10% penalty
- **Prevents cycling**: Discourages rapid buy/sell

### Example

- Lose 1,000 XP → Drop from Level 20 to Level 19
- Regain requires: 1,000 + 100 (10%) = 1,100 XP

---

## 10. Content Examples

### Life Tier Skills

1. **Quick Draw**
   - 8 abilities
   - Cost: 15 points
   - XP: 50
   - Abilities: Quick Reflexes, Sharp Eyes, Steady Hand, etc.

2. **Sharpshooter**
   - 9 abilities
   - Cost: 18 points
   - XP: 60
   - Abilities: Perfect Aim, Sharp Focus, etc.

### Master Tier Skills

1. **The Immortal Sheriff**
   - 20 abilities
   - Cost: 180 points
   - XP: 3,000
   - Abilities: Transcendent Draw, Beyond Bluff, etc.

2. **Wild West Deity**
   - 25 abilities
   - Cost: 225 points
   - XP: 4,500
   - Abilities: Supreme Reality, Perfect Eternity, etc.

---

## 11. Implementation Notes

### Key Files

- **Tier.kt**: Enum defining all tiers and level ranges
- **XPSystem.kt**: Dynamic XP calculation and level management
- **PointsSystem.kt**: Point cost calculation and management
- **TierConfiguration.kt**: Configuration for each tier
- **Skill.kt**: Skill model with ability support
- **Ability.kt**: Ability model with rank system
- **ProgressionTree.kt**: Complete progression tree management
- **ContentGenerator.kt**: Randomized content generation

### Integration Points

1. **GCMS**: Commands and events for progression
2. **UI**: Progression screens and displays
3. **Database**: Persistence of progression state
4. **Game Loop**: XP and point updates

---

## 12. Balance Considerations

### Adjustable Parameters

All key values are configurable:
- **Base XP per tier**: Adjust progression speed
- **Point multipliers**: Adjust economy balance
- **Abilities per skill**: Adjust content density
- **Prerequisite chances**: Adjust difficulty curve
- **Randomization variance**: Adjust variety vs consistency

### Testing Recommendations

1. **Early game**: Ensure smooth progression through Life tier
2. **Mid game**: Verify appropriate pacing in Beginner/Novice tiers
3. **Late game**: Test high-tier balance and progression
4. **Full playthrough**: Verify level 200 is achievable but challenging

---

## 13. Future Expansions

### Easy to Extend

The system is designed for easy expansion:

1. **Add new tiers**: Simple enum addition
2. **Add new skills**: Content generation handles automatically
3. **Adjust balance**: Configuration changes only
4. **New mechanics**: Extend existing models

### Potential Additions

- **Seasonal content**: Limited-time skills/abilities
- **Special events**: Bonus XP or points
- **Prestige system**: Reset with bonuses
- **Skill trees**: Complex branching paths
- **Custom abilities**: Player-created content

---

## Summary

The TRASH tiered progression system provides:

✅ **200 levels** organized into 6 thematic tiers
✅ **Dynamic XP scaling** with non-linear progression
✅ **Interdependent skill trees** with unlock dependencies
✅ **Randomized content** for variety and replayability
✅ **Soft level cap** controlled by available content
✅ **Penalty system** to prevent abuse
✅ **Easy expansion** for future content updates

The system is fully data-driven, well-balanced, and provides a satisfying progression journey from beginner to master.