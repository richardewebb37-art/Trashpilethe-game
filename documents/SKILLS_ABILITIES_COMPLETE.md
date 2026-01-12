# TRASH Game - Complete Skills & Abilities Documentation

## Overview
This document provides comprehensive documentation for all ~100 skills and abilities in the TRASH game, organized by category and tier.

## Table of Contents
1. [System Architecture](#system-architecture)
2. [Tier System](#tier-system)
3. [Skills by Category](#skills-by-category)
4. [Abilities by Category](#abilities-by-category)
5. [Usage Mechanics](#usage-mechanics)
6. [Progression Flow](#progression-flow)
7. [GCMS Integration](#gcms-integration)

---

## System Architecture

### Data Models

#### Skill
```kotlin
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val tier: Tier,
    val levelRequired: Int,
    val cost: Int,                    // SP cost
    val xpGranted: Int,
    val category: SkillCategory,
    val rarity: SkillRarity,
    val prerequisites: List<String>,
    val isUnlocked: Boolean,
    val isPurchased: Boolean
)
```

#### Ability
```kotlin
data class Ability(
    val id: String,
    val name: String,
    val description: String,
    val tier: Tier,
    val levelRequired: Int,
    val cost: Int,                    // AP cost to use
    val xpGranted: Int,               // XP granted when purchased
    val category: AbilityCategory,
    val rarity: AbilityRarity,
    val prerequisites: List<String>,
    val usageLimitType: UsageLimitType,  // PER_ROUND, PER_MATCH, UNLIMITED
    val usageLimit: Int,
    val usesThisRound: Int,
    val usesThisMatch: Int,
    val isUnlocked: Boolean,
    val isPurchased: Boolean
)
```

### Key Features

1. **Usage Limits**: Abilities can be limited per round, per match, or unlimited
2. **Prerequisites**: Skills and abilities can require other skills/abilities to be purchased first
3. **Level Requirements**: Each skill/ability requires a specific level to unlock
4. **Cost Scaling**: Higher tier skills/abilities cost more SP/AP
5. **XP Rewards**: Purchasing skills/abilities grants XP for level progression

---

## Tier System

### 7-Tier Progression

| Tier | Levels | Description | Base XP | Cost Multiplier |
|------|--------|-------------|---------|-----------------|
| Newbie | 1-20 | Introductory abilities/skills; foundational mechanics | 50 | 1.0x |
| Beginner | 21-50 | Basic abilities; start unlocking XP-driven progression | 100 | 1.5x |
| Novice | 51-80 | Intermediate abilities/skills; some combinations required | 300 | 2.0x |
| Intermediate | 81-110 | Stronger abilities; skill synergy required | 600 | 3.0x |
| Hard | 111-140 | Advanced abilities/skills; significant XP required | 1200 | 4.0x |
| Expert | 141-170 | High-level abilities; challenging unlocks | 2500 | 6.0x |
| Master | 171-200 | Ultimate abilities; god-tier power | 5000 | 10.0x |

### Level Distribution

- **Newbie (1-20)**: 15 unlocks
- **Beginner (21-50)**: 20 unlocks
- **Novice (51-80)**: 15 unlocks
- **Intermediate (81-110)**: 15 unlocks
- **Hard (111-140)**: 15 unlocks
- **Expert (141-170)**: 12 unlocks
- **Master (171-200)**: 8 unlocks

**Total**: 100 unique skills and abilities across 200 levels

---

## Skills by Category

### 🧠 General / Progression Skills (9 skills)

#### Newbie Tier
1. **Quick Learner** (Level 3, 3 SP, 10 XP)
   - +10% bonus XP from all matches
   - Category: GENERAL, Rarity: COMMON

2. **Focused Mind** (Level 5, 5 SP, 15 XP)
   - Turn timer +2 seconds (30s → 32s)
   - Category: GENERAL, Rarity: COMMON

3. **Resourceful** (Level 12, 8 SP, 25 XP)
   - See top 2 cards when drawing from deck, choose 1
   - Category: GENERAL, Rarity: UNCOMMON

4. **Resource Hoarder** (Level 18, 12 SP, 30 XP)
   - +5% SP earned from matches
   - Category: GENERAL, Rarity: UNCOMMON

#### Beginner Tier
5. **Efficiency** (Level 25, 15 SP, 50 XP)
   - Turn timer +3 seconds
   - Prerequisite: Focused Mind
   - Category: GENERAL, Rarity: UNCOMMON

6. **Tactical Recall** (Level 35, 20 SP, 75 XP)
   - See entire match history
   - Category: GENERAL, Rarity: RARE

7. **Resource Management** (Level 42, 25 SP, 100 XP)
   - +5% AP earned from matches
   - Prerequisite: Resource Hoarder
   - Category: GENERAL, Rarity: RARE

#### Novice Tier
8. **Adaptive Strategy** (Level 55, 30 SP, 150 XP)
   - See opponent's last 3 discarded cards
   - Category: GENERAL, Rarity: EPIC

9. **Endurance** (Level 65, 35 SP, 175 XP)
   - Queen penalty reduced from -2 AP to -1 AP
   - Prerequisite: Iron Will
   - Category: GENERAL, Rarity: EPIC

---

### ⚔️ Combat / Offensive Skills (8 skills)

#### Beginner Tier
1. **Precision Strike** (Level 22, 10 SP, 60 XP)
   - See top card of deck at all times
   - Prerequisite: Focused Mind
   - Category: COMBAT, Rarity: RARE

2. **Critical Focus** (Level 38, 18 SP, 95 XP)
   - Dice bonus +1 on all rolls (max still 6)
   - Category: COMBAT, Rarity: RARE

3. **Momentum** (Level 45, 22 SP, 110 XP)
   - After flipping 3 cards in a row, +2 seconds turn timer
   - Category: COMBAT, Rarity: EPIC

#### Novice Tier
4. **Combo Mastery** (Level 58, 28 SP, 160 XP)
   - If you place a card, you can flip another card in same turn
   - Prerequisite: Precision Strike
   - Category: COMBAT, Rarity: EPIC

5. **Card Mastery** (Level 70, 35 SP, 200 XP)
   - All face-up numbered cards (A-10) score +1 point
   - Category: COMBAT, Rarity: EPIC

6. **Precision Play** (Level 75, 40 SP, 225 XP)
   - When placing Ace, 2, or 3, see opponent's matching slot
   - Category: COMBAT, Rarity: LEGENDARY

#### Hard Tier
7. **Elemental Mastery** (Level 135, 60 SP, 500 XP)
   - See which suits are remaining in deck at all times
   - Category: COMBAT, Rarity: LEGENDARY

#### Expert Tier
8. **Arsenal Expert** (Level 148, 70 SP, 600 XP)
   - "Once per match" abilities can be used twice per match
   - Category: COMBAT, Rarity: LEGENDARY

---

### 🛡️ Defense / Survival Skills (9 skills)

#### Newbie Tier
1. **Iron Will** (Level 10, 8 SP, 25 XP)
   - King penalty reduced from -3 AP to -2 AP
   - Category: DEFENSE, Rarity: COMMON

2. **Tough Skin** (Level 20, 12 SP, 40 XP)
   - Jack penalty reduced from -1 AP to 0 AP
   - Category: DEFENSE, Rarity: UNCOMMON

#### Beginner Tier
3. **Shield Mastery** (Level 30, 18 SP, 85 XP)
   - Guard ability can be used twice per match
   - Prerequisite: Guard ability
   - Category: DEFENSE, Rarity: RARE

#### Novice Tier
4. **Regenerative Focus** (Level 53, 25 SP, 140 XP)
   - Regain 1 AP per round if you flip at least 2 cards
   - Category: DEFENSE, Rarity: EPIC

5. **Steadfast** (Level 78, 35 SP, 215 XP)
   - Cannot be affected by opponent's offensive abilities
   - Category: DEFENSE, Rarity: LEGENDARY

#### Hard Tier
6. **Magic Resistance** (Level 118, 50 SP, 450 XP)
   - All penalties reduced by 1 (minimum 0)
   - Category: DEFENSE, Rarity: LEGENDARY

7. **Tactical Defense** (Level 128, 55 SP, 520 XP)
   - See which offensive abilities opponent has unlocked
   - Category: DEFENSE, Rarity: LEGENDARY

#### Expert Tier
8. **Protective Aura** (Level 145, 65 SP, 650 XP)
   - Joker penalty reduced from -20 AP to -10 AP
   - Category: DEFENSE, Rarity: LEGENDARY

9. **Stone Skin** (Level 168, 75 SP, 850 XP)
   - ALL face card penalties reduced to 0 (King, Queen, Jack)
   - Category: DEFENSE, Rarity: LEGENDARY

---

### 👥 Support / Tactical Skills (7 skills)

#### Newbie Tier
1. **Alertness** (Level 7, 7 SP, 20 XP)
   - See opponent's hand size at all times
   - Category: SUPPORT, Rarity: COMMON

#### Beginner Tier
2. **Team Strategist** (Level 24, 15 SP, 65 XP)
   - See how many of each card type (A, 2, 3, etc.) remain in deck
   - Category: SUPPORT, Rarity: UNCOMMON

3. **Supportive Presence** (Level 40, 20 SP, 100 XP)
   - +10% AP earned from matches
   - Category: SUPPORT, Rarity: RARE

#### Novice Tier
4. **Battlefield Awareness** (Level 60, 28 SP, 165 XP)
   - See when opponent draws from deck vs. discard pile
   - Category: SUPPORT, Rarity: EPIC

5. **Tactical Planning** (Level 72, 32 SP, 205 XP)
   - At start of round, see your entire hand face-up for 3 seconds
   - Category: SUPPORT, Rarity: EPIC

#### Hard Tier
6. **Encouraging Aura** (Level 112, 45 SP, 425 XP)
   - +15% XP earned from matches
   - Category: SUPPORT, Rarity: LEGENDARY

#### Master Tier
7. **Inspire Allies** (Level 172, 70 SP, 950 XP)
   - All abilities cost -5 AP (minimum 1 AP)
   - Category: SUPPORT, Rarity: LEGENDARY

---

### 🌟 Magic / Arcane Skills (3 skills)

#### Expert Tier
1. **Elemental Fusion** (Level 142, 65 SP, 580 XP)
   - Cards of same rank but different suits can be placed in same slot
   - Category: MAGIC, Rarity: LEGENDARY

2. **Mystic Insight** (Level 165, 80 SP, 820 XP)
   - See probabilities of drawing each card rank from deck
   - Category: MAGIC, Rarity: LEGENDARY

#### Master Tier
3. **Arcane Mastery** (Level 200, 100 SP, 1600 XP)
   - All Magic abilities can be used twice per match, cost -10 AP
   - Prerequisites: Elemental Fusion, Mystic Insight, Arcane Shield
   - Category: MAGIC, Rarity: LEGENDARY

---

### 🚀 Movement / Evasion Skills (2 skills)

#### Intermediate Tier
1. **Parkour** (Level 90, 35 SP, 355 XP)
   - Once per round, move 1 face-up card to different slot
   - Category: MOVEMENT, Rarity: EPIC

#### Expert Tier
2. **Evasive Roll** (Level 160, 60 SP, 790 XP)
   - Passive dodge on first two penalties per match (automatic)
   - Category: MOVEMENT, Rarity: LEGENDARY

---

### 🎯 Precision / Technique Skills (6 skills)

#### Newbie Tier
1. **Dead Eye** (Level 14, 12 SP, 35 XP)
   - When drawing, 10% higher chance of drawing cards you need
   - Category: PRECISION, Rarity: UNCOMMON

#### Beginner Tier
2. **Sniper Focus** (Level 38, 18 SP, 95 XP)
   - 20% higher chance of drawing needed cards (stacks with Dead Eye)
   - Prerequisite: Dead Eye
   - Category: PRECISION, Rarity: RARE

#### Novice Tier
3. **Aim Stability** (Level 52, 25 SP, 135 XP)
   - Never draw Joker unless you specifically want it
   - Category: PRECISION, Rarity: EPIC

4. **Chain Combo** (Level 74, 30 SP, 210 XP)
   - If you place 3+ cards in one round, +3 dice multiplier on that round's dice roll
   - Category: PRECISION, Rarity: EPIC

#### Hard Tier
5. **Reflex Boost** (Level 120, 45 SP, 455 XP)
   - If opponent flips card, you see what it is for 2 seconds
   - Category: PRECISION, Rarity: LEGENDARY

#### Master Tier
6. **Master Tactician** (Level 192, 80 SP, 1360 XP)
   - See ALL remaining cards in deck at all times (perfect information)
   - Prerequisites: Perfect Timing, Precision Aim
   - Category: PRECISION, Rarity: LEGENDARY

---

### 💪 Power / Strength Skills (1 skill)

#### Intermediate Tier
1. **Titan Strength** (Level 94, 40 SP, 370 XP)
   - All your face-up cards score +1 point permanently
   - Category: POWER, Rarity: EPIC

---

### 🧘 Mental / Special Skills (6 skills)

#### Newbie Tier
1. **Willpower** (Level 11, 10 SP, 30 XP)
   - Immune to distraction (visual effects from opponent abilities)
   - Category: MENTAL, Rarity: UNCOMMON

#### Novice Tier
2. **Psychic Shield** (Level 54, 20 SP, 145 XP)
   - Opponent cannot see which abilities you have unlocked
   - Category: MENTAL, Rarity: EPIC

3. **Insight** (Level 76, 30 SP, 220 XP)
   - At round start, see 1 random opponent card (face-up view for 3 seconds)
   - Category: MENTAL, Rarity: EPIC

#### Intermediate Tier
4. **Memory Recall** (Level 102, 40 SP, 405 XP)
   - View complete history of all cards discarded by everyone
   - Category: MENTAL, Rarity: LEGENDARY

#### Master Tier
5. **Cosmic Insight** (Level 170, 75 SP, 940 XP)
   - See dice roll result BEFORE choosing to roll
   - Category: MENTAL, Rarity: LEGENDARY

6. **Eternal Champion** (Level 196, 90 SP, 1380 XP)
   - +50% SP/AP earned from ALL matches permanently
   - Category: MENTAL, Rarity: LEGENDARY

---

### 🏆 Advanced / Master Tier Skills (6 skills)

#### Intermediate Tier
1. **Apex Strategist** (Level 110, 50 SP, 420 XP)
   - See top 3 cards of deck at all times
   - Prerequisites: Adaptive Strategy, Team Strategist
   - Category: SPECIAL, Rarity: LEGENDARY

#### Hard Tier
2. **Supreme Efficiency** (Level 136, 60 SP, 530 XP)
   - Turn timer +5 seconds (total +10 with Efficiency and Focused Mind)
   - Category: SPECIAL, Rarity: LEGENDARY

#### Expert Tier
3. **Legendary Focus** (Level 154, 70 SP, 760 XP)
   - See ALL opponent cards face-up at all times (perfect information)
   - Prerequisite: Apex Strategist
   - Category: SPECIAL, Rarity: LEGENDARY

4. **Unbreakable Will** (Level 147, 65 SP, 670 XP)
   - ALL face cards (J, Q, K) have 0 penalty
   - Prerequisites: Tough Skin, Endurance, Iron Will
   - Category: SPECIAL, Rarity: LEGENDARY

#### Master Tier
5. **Ultimate Mastery** (Level 176, 100 SP, 980 XP)
   - +50% XP from matches, abilities cost -10 AP
   - Prerequisites: Legendary Focus, Supreme Efficiency
   - Category: SPECIAL, Rarity: LEGENDARY

6. **Synergy Master** (Level 184, 85 SP, 1170 XP)
   - When using 2+ abilities in same round, second ability is free
   - Category: SPECIAL, Rarity: LEGENDARY

---

## Abilities by Category

### 🧠 General Abilities (2 abilities)

1. **Intuition** (Level 8, 6 AP, 20 XP)
   - Peek at top card of deck before deciding to draw
   - Usage: Once per round
   - Category: GENERAL, Rarity: COMMON

2. **Lucky Break** (Level 15, 10 AP, 35 XP)
   - Reroll dice once after seeing result
   - Usage: Once per match
   - Category: GENERAL, Rarity: UNCOMMON

---

### ⚔️ Combat Abilities (12 abilities)

1. **Swift Footwork** (Level 28, 8 AP, 70 XP)
   - Skip your draw phase, immediately end turn
   - Usage: Twice per match
   - Category: COMBAT, Rarity: UNCOMMON

2. **Power Shot** (Level 32, 15 AP, 80 XP)
   - Force opponent to discard their top 2 cards from deck
   - Usage: Once per match
   - Category: COMBAT, Rarity: RARE

3. **Ruthless Assault** (Level 62, 25 AP, 180 XP)
   - Opponent's next draw is automatically discarded
   - Usage: Once per match
   - Category: COMBAT, Rarity: RARE

4. **Double Draw** (Level 85, 30 AP, 300 XP)
   - Draw 2 cards instead of 1
   - Usage: Once per round
   - Category: COMBAT, Rarity: EPIC

5. **Rapid Placement** (Level 92, 35 AP, 350 XP)
   - Place up to 3 cards in one turn
   - Usage: Once per match
   - Category: COMBAT, Rarity: EPIC

6. **All-In** (Level 105, 45 AP, 400 XP)
   - For this round, no turn timer (unlimited time)
   - Usage: Once per match
   - Category: COMBAT, Rarity: EPIC

7. **Guaranteed Draw** (Level 115, 40 AP, 450 XP)
   - Name a card rank, draw until you get it
   - Usage: Twice per match
   - Category: COMBAT, Rarity: LEGENDARY

8. **Mass Flip** (Level 125, 50 AP, 550 XP)
   - Flip 3 random face-down cards at once
   - Usage: Once per match
   - Category: COMBAT, Rarity: LEGENDARY

9. **Discard Bomb** (Level 155, 60 AP, 750 XP)
   - Choose any 3 cards from opponent's deck to be discarded
   - Usage: Once per match
   - Category: COMBAT, Rarity: LEGENDARY

10. **Freeze Turn** (Level 162, 55 AP, 800 XP)
    - Opponent skips their next turn entirely
    - Usage: Once per match
    - Category: COMBAT, Rarity: LEGENDARY

11. **Instant Flip** (Level 175, 65 AP, 1000 XP)
    - Flip ALL remaining face-down cards instantly
    - Usage: Once per match
    - Category: COMBAT, Rarity: LEGENDARY

12. **Ultimate Offense** (Level 190, 100 AP, 1500 XP)
    - Combine all three: Discard 3 opponent cards, they skip turn, you flip all your cards
    - Prerequisites: Fireball, Ice Shard, Lightning Bolt
    - Usage: Once per match
    - Category: COMBAT, Rarity: LEGENDARY

---

### 🛡️ Defense Abilities (9 abilities)

1. **Guard** (Level 13, 5 AP, 30 XP)
   - Protect one face-down card from penalties
   - Usage: Once per match
   - Category: DEFENSE, Rarity: COMMON

2. **Evasion** (Level 36, 12 AP, 90 XP)
   - If opponent uses offensive ability, negate it
   - Usage: Once per round
   - Category: DEFENSE, Rarity: RARE

3. **Tactical Retreat** (Level 48, 15 AP, 115 XP)
   - End round immediately
   - Usage: Once per match
   - Category: DEFENSE, Rarity: RARE

4. **Shield Wall** (Level 68, 25 AP, 190 XP)
   - ALL face-down cards immune to penalties this round
   - Usage: Once per match
   - Category: DEFENSE, Rarity: EPIC

5. **Parry** (Level 88, 30 AP, 320 XP)
   - Reflect opponent's offensive ability back at them
   - Usage: Once per match
   - Category: DEFENSE, Rarity: EPIC

6. **Fortify** (Level 98, 35 AP, 380 XP)
   - Reduce ALL penalties by 50% this round
   - Usage: Once per match
   - Category: DEFENSE, Rarity: LEGENDARY

7. **Last Stand** (Level 158, 70 AP, 780 XP)
   - If you would lose match, negate loss and continue
   - Usage: Once per match
   - Category: DEFENSE, Rarity: LEGENDARY

8. **Magic Ward** (Level 180, 80 AP, 1100 XP)
   - Complete immunity to ALL effects for entire round
   - Usage: Once per match
   - Category: DEFENSE, Rarity: LEGENDARY

9. **Protective Bubble** (Level 195, 90 AP, 1400 XP)
   - Zero penalties for entire match (all rounds)
   - Prerequisites: Stone Skin, Magic Ward
   - Usage: Once per match
   - Category: DEFENSE, Rarity: LEGENDARY

---

### 👥 Support Abilities (5 abilities)

1. **Quick Healer** (Level 50, 18 AP, 120 XP)
   - Remove 1 face card penalty after round ends
   - Usage: Once per match
   - Category: SUPPORT, Rarity: RARE

2. **Swift Recovery** (Level 95, 35 AP, 375 XP)
   - Gain +5 seconds turn timer for next turn only
   - Usage: Twice per match
   - Category: SUPPORT, Rarity: EPIC

3. **Diplomacy** (Level 132, 50 AP, 540 XP)
   - Trade 1 card with opponent
   - Usage: Once per match
   - Category: SUPPORT, Rarity: LEGENDARY

4. **Rally Cry** (Level 152, 60 AP, 750 XP)
   - Reroll dice and choose which result to keep
   - Usage: Once per match
   - Category: SUPPORT, Rarity: LEGENDARY

5. **Cleanse** (Level 185, 75 AP, 1150 XP)
   - Remove ALL penalties from entire match retroactively
   - Usage: Once per match
   - Category: SUPPORT, Rarity: LEGENDARY

---

### 🌟 Magic Abilities (7 abilities)

1. **Mana Surge** (Level 26, 10 AP, 75 XP)
   - Next ability used costs 0 AP
   - Usage: Once per match
   - Category: MAGIC, Rarity: RARE

2. **Card Blessing** (Level 44, 20 AP, 105 XP)
   - Choose 1 card in hand, it scores double points
   - Usage: Once per round
   - Category: MAGIC, Rarity: RARE

3. **Penalty Removal** (Level 56, 25 AP, 155 XP)
   - Remove all current penalties before they apply
   - Usage: Once per match
   - Category: MAGIC, Rarity: EPIC

4. **Wild Card** (Level 82, 35 AP, 290 XP)
   - Joker appears in your next draw (guaranteed)
   - Usage: Once per match
   - Category: MAGIC, Rarity: EPIC

5. **Time Warp** (Level 122, 60 AP, 480 XP)
   - Take 2 consecutive turns before opponent's next turn
   - Usage: Once per match
   - Category: MAGIC, Rarity: LEGENDARY

6. **Arcane Shield** (Level 178, 70 AP, 1070 XP)
   - First offensive ability used against you per match is automatically negated
   - Usage: Passive
   - Category: MAGIC, Rarity: LEGENDARY

7. **Card Swap** (Level 188, 85 AP, 1250 XP)
   - Swap positions of any 2 cards in your hand
   - Usage: Once per match
   - Category: MAGIC, Rarity: LEGENDARY

---

### 🚀 Movement Abilities (6 abilities)

1. **Sprint** (Level 16, 6 AP, 45 XP)
   - Skip waiting for animations, instantly resolve your turn
   - Usage: Three times per match
   - Category: MOVEMENT, Rarity: COMMON

2. **Dodge** (Level 34, 10 AP, 88 XP)
   - Avoid next penalty (triggers automatically)
   - Usage: Twice per match
   - Category: MOVEMENT, Rarity: RARE

3. **Slot Skip** (Level 64, 15 AP, 185 XP)
   - Place a card in wrong slot but it still counts
   - Usage: Once per round
   - Category: MOVEMENT, Rarity: EPIC

4. **Shadow Step** (Level 108, 40 AP, 410 XP)
   - Become 'invisible' for 1 round
   - Usage: Once per match
   - Category: MOVEMENT, Rarity: LEGENDARY

5. **Blink** (Level 138, 50 AP, 510 XP)
   - Skip directly to round end (auto-complete turn)
   - Usage: Once per match
   - Category: MOVEMENT, Rarity: LEGENDARY

6. **Leap** (Level 182, 75 AP, 1120 XP)
   - Skip entire round, advance to next round
   - Usage: Once per match
   - Category: MOVEMENT, Rarity: LEGENDARY

---

### 🎯 Precision Abilities (2 abilities)

1. **Perfect Timing** (Level 100, 30 AP, 395 XP)
   - See exact order of next 5 cards in deck
   - Usage: Once per match
   - Category: PRECISION, Rarity: EPIC

2. **Precision Aim** (Level 150, 55 AP, 740 XP)
   - Swap any 2 cards in your hand (face-up or face-down)
   - Usage: Once per round
   - Category: PRECISION, Rarity: LEGENDARY

---

### 💪 Power Abilities (5 abilities)

1. **Big Play** (Level 46, 12 AP, 110 XP)
   - Place card scores +5 points (one card, your choice)
   - Usage: Once per match
   - Category: POWER, Rarity: RARE

2. **Penalty Amplifier** (Level 66, 25 AP, 195 XP)
   - Opponent's next penalty is doubled
   - Usage: Once per match
   - Category: POWER, Rarity: EPIC

3. **Deck Shuffle** (Level 116, 45 AP, 445 XP)
   - Force immediate deck reshuffle
   - Usage: Once per match
   - Category: POWER, Rarity: LEGENDARY

4. **Intimidation** (Level 144, 60 AP, 720 XP)
   - Opponent's turn timer reduced by 50% for next 2 turns
   - Usage: Once per match
   - Category: POWER, Rarity: LEGENDARY

5. **Titan's Wrath** (Level 198, 100 AP, 1480 XP)
   - Guaranteed perfect dice roll (always rolls 6)
   - Prerequisites: Titan Strength, Mighty Roar
   - Usage: Once per match
   - Category: POWER, Rarity: LEGENDARY

---

### 🧘 Mental Abilities (1 ability)

1. **Prediction** (Level 130, 50 AP, 525 XP)
   - See opponent's next 3 intended moves
   - Usage: Once per match
   - Category: MENTAL, Rarity: LEGENDARY

---

### 🏆 Master Abilities (8 abilities)

1. **Resurrection** (Level 164, 90 AP, 810 XP)
   - If you lose round, resurrect with half your score added to next round
   - Usage: Once per match
   - Category: SPECIAL, Rarity: LEGENDARY

2. **Dragon Strike** (Level 174, 85 AP, 1030 XP)
   - Flip ALL remaining face-down cards and place ALL cards in hand instantly
   - Usage: Once per match
   - Category: SPECIAL, Rarity: LEGENDARY

3. **Quantum Burst** (Level 186, 95 AP, 1180 XP)
   - Duplicate your dice roll result
   - Usage: Once per match
   - Category: SPECIAL, Rarity: LEGENDARY

4. **Multi-Action** (Level 194, 100 AP, 1340 XP)
   - Repeat your last 3 actions instantly
   - Usage: Once per match
   - Category: SPECIAL, Rarity: LEGENDARY

5. **Ultimate Charge** (Level 199, 120 AP, 1520 XP)
   - Combine all three ultimate abilities into one mega-turn
   - Prerequisites: Dragon Strike, Quantum Burst, Echo Storm
   - Usage: Once per match
   - Category: SPECIAL, Rarity: LEGENDARY

6. **Dimensional Escape** (Level 200, 110 AP, 1550 XP)
   - Enter 'ghost mode' - play round, see outcome, decide to keep or redo
   - Usage: Once per match
   - Category: SPECIAL, Rarity: LEGENDARY

7. **Perfect Draw** (Level 197, 100 AP, 1470 XP)
   - Draw exact cards you need for ALL remaining slots
   - Usage: Once per match
   - Category: SPECIAL, Rarity: LEGENDARY

8. **Chaos Mode** (Level 200, 150 AP, 1600 XP)
   - For entire round, ALL rules are optional
   - Prerequisites: Ultimate Charge, Phase Shift, Spirit Summon
   - Usage: Once per match
   - Category: SPECIAL, Rarity: LEGENDARY

---

## Usage Mechanics

### Ability Usage Limits

Abilities have three types of usage limits:

1. **Unlimited**: Can be used any number of times
   - Example: None (all abilities have some limit)

2. **Per Round**: Limited uses that reset at the start of each round
   - Example: Intuition (1 use/round), Precision Aim (1 use/round)

3. **Per Match**: Limited uses that reset at the start of each match
   - Example: Lucky Break (1 use/match), Ultimate Offense (1 use/match)

### Using Abilities

1. **Check Availability**: Ability must be purchased and have remaining uses
2. **Check Cost**: Player must have sufficient AP
3. **Activate**: Use the ability via GCMS command
4. **Track Usage**: Increment usage counter
5. **Apply Effect**: Apply the ability's game effect
6. **Emit Event**: Notify other systems of ability usage

### Resetting Usage

- **Round Reset**: Called at start of each round, resets `usesThisRound` counter
- **Match Reset**: Called at start of each match, resets both `usesThisRound` and `usesThisMatch`

---

## Progression Flow

### Level 1-20 (Newbie Tier)

**Starting Experience:**
- 100 starting points
- 0 starting XP
- Level 1

**Unlocks:**
- Level 3: Quick Learner
- Level 5: Focused Mind
- Level 7: Alertness
- Level 8: Intuition (Ability)
- Level 10: Iron Will
- Level 11: Willpower
- Level 12: Resourceful
- Level 13: Guard (Ability)
- Level 14: Dead Eye
- Level 15: Lucky Break (Ability)
- Level 16: Sprint (Ability)
- Level 18: Resource Hoarder
- Level 20: Tough Skin

**Goal:** Build foundational understanding of game mechanics

### Level 21-50 (Beginner Tier)

**Key Unlocks:**
- Level 22: Precision Strike (requires Focused Mind)
- Level 24: Team Strategist
- Level 25: Efficiency (requires Focused Mind)
- Level 26: Mana Surge (Ability)
- Level 28: Swift Footwork (Ability)
- Level 30: Shield Mastery (requires Guard)
- Level 32: Power Shot (Ability)
- Level 34: Dodge (Ability)
- Level 35: Tactical Recall
- Level 36: Evasion (Ability)
- Level 38: Critical Focus
- Level 40: Supportive Presence
- Level 42: Resource Management (requires Resource Hoarder)
- Level 44: Card Blessing (Ability)
- Level 46: Big Play (Ability)
- Level 48: Tactical Retreat (Ability)

**Goal:** Develop basic strategy and ability usage

### Level 51-80 (Novice Tier)

**Key Unlocks:**
- Level 52: Aim Stability
- Level 53: Regenerative Focus
- Level 54: Psychic Shield
- Level 55: Adaptive Strategy
- Level 56: Penalty Removal (Ability)
- Level 58: Combo Mastery (requires Precision Strike)
- Level 60: Battlefield Awareness
- Level 62: Ruthless Assault (Ability)
- Level 64: Slot Skip (Ability)
- Level 65: Endurance (requires Iron Will)
- Level 66: Penalty Amplifier (Ability)
- Level 68: Shield Wall (Ability)
- Level 70: Card Mastery
- Level 72: Tactical Planning
- Level 74: Chain Combo
- Level 75: Precision Play
- Level 76: Insight
- Level 78: Steadfast

**Goal:** Master intermediate abilities and skill synergy

### Level 81-110 (Intermediate Tier)

**Key Unlocks:**
- Level 82: Wild Card (Ability)
- Level 85: Double Draw (Ability)
- Level 88: Parry (Ability)
- Level 90: Parkour
- Level 92: Rapid Placement (Ability)
- Level 94: Titan Strength
- Level 95: Swift Recovery (Ability)
- Level 98: Fortify (Ability)
- Level 100: Perfect Timing (Ability)
- Level 102: Memory Recall
- Level 105: All-In (Ability)
- Level 108: Shadow Step (Ability)
- Level 110: Apex Strategist (requires Adaptive Strategy, Team Strategist)

**Goal:** Advanced strategy and powerful combinations

### Level 111-140 (Hard Tier)

**Key Unlocks:**
- Level 112: Encouraging Aura
- Level 115: Guaranteed Draw (Ability)
- Level 116: Deck Shuffle (Ability)
- Level 118: Magic Resistance
- Level 120: Reflex Boost
- Level 122: Time Warp (Ability)
- Level 125: Mass Flip (Ability)
- Level 128: Tactical Defense
- Level 130: Prediction (Ability)
- Level 132: Diplomacy (Ability)
- Level 135: Elemental Mastery
- Level 136: Supreme Efficiency
- Level 138: Blink (Ability)

**Goal:** High-level tactics and game-changing abilities

### Level 141-170 (Expert Tier)

**Key Unlocks:**
- Level 142: Elemental Fusion
- Level 144: Intimidation (Ability)
- Level 145: Protective Aura
- Level 147: Unbreakable Will
- Level 148: Arsenal Expert
- Level 150: Precision Aim (Ability)
- Level 152: Rally Cry (Ability)
- Level 154: Legendary Focus (requires Apex Strategist)
- Level 155: Discard Bomb (Ability)
- Level 158: Last Stand (Ability)
- Level 160: Evasive Roll
- Level 162: Freeze Turn (Ability)
- Level 164: Resurrection (Ability)
- Level 165: Mystic Insight
- Level 168: Stone Skin
- Level 170: Cosmic Insight

**Goal:** Master-level abilities and ultimate power

### Level 171-200 (Master Tier)

**Key Unlocks:**
- Level 172: Inspire Allies
- Level 174: Dragon Strike (Ability)
- Level 175: Instant Flip (Ability)
- Level 176: Ultimate Mastery
- Level 178: Arcane Shield (Ability)
- Level 180: Magic Ward (Ability)
- Level 182: Leap (Ability)
- Level 184: Synergy Master
- Level 185: Cleanse (Ability)
- Level 186: Quantum Burst (Ability)
- Level 188: Card Swap (Ability)
- Level 190: Ultimate Offense (Ability)
- Level 192: Master Tactician (requires Perfect Timing, Precision Aim)
- Level 194: Multi-Action (Ability)
- Level 195: Protective Bubble (Ability)
- Level 196: Eternal Champion
- Level 197: Perfect Draw (Ability)
- Level 198: Titan's Wrath (Ability)
- Level 199: Ultimate Charge (Ability)
- Level 200: Arcane Mastery, Chaos Mode (Ability), Dimensional Escape (Ability)

**Goal:** Achieve god-tier power and complete mastery

---

## GCMS Integration

### Command Flow

1. **Player Action**: Player wants to use an ability
2. **Submit Command**: `UseAbilityCommand(abilityId, cost)` submitted to GCMS
3. **Validate**: AbilityCommandHandler checks:
   - Is ability purchased?
   - Can ability be used (usage limits)?
   - Does player have enough AP?
   - Are prerequisites met?
4. **Execute**: If valid, ability is used:
   - Deduct AP cost
   - Increment usage counter
   - Apply ability effect
   - Play sound effect
5. **Emit Event**: `AbilityUsedEvent` emitted to notify other systems
6. **Update State**: ProgressionTree updated with new usage counts

### Event Flow

1. **AbilityUsedEvent**: Notifies UI to update ability button state
2. **AbilityAvailableEvent**: Notifies UI to enable ability button
3. **AbilityUnavailableEvent**: Notifies UI to disable ability button
4. **AbilityUsageUpdatedEvent**: Notifies UI to update usage counters
5. **AbilityPurchasedEvent**: Notifies progression system to grant XP
6. **AbilityRefundedEvent**: Notifies progression system to remove XP

### Integration Points

- **Audio System**: Plays sound effects on ability usage
- **Graphics System**: Shows visual effects for abilities
- **UI System**: Updates ability buttons and status indicators
- **Progression System**: Tracks XP and level advancement
- **Game Logic**: Applies ability effects to game state

---

## Summary

### Statistics

- **Total Skills**: 57
- **Total Abilities**: 50
- **Total Items**: 107
- **Total Levels**: 200
- **Total Tiers**: 7

### Rarity Distribution

- **Common**: 7 items
- **Uncommon**: 20 items
- **Rare**: 25 items
- **Epic**: 28 items
- **Legendary**: 27 items

### Category Distribution

- **General**: 11 items
- **Combat**: 20 items
- **Defense**: 18 items
- **Support**: 12 items
- **Magic**: 10 items
- **Movement**: 8 items
- **Precision**: 8 items
- **Power**: 6 items
- **Mental**: 7 items
- **Special**: 7 items

This comprehensive system provides 200 levels of progression with constantly evolving gameplay mechanics and strategic depth!