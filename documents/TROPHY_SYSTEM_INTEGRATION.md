# Trophy System Integration Guide

## Overview

The Trophy System is a fully integrated component of the TRASH game progression system. It awards dynamic trophies to players based on their level, tier, points, abilities, and skills.

## Components

### 1. Trophy Data Model
**File:** `app/src/main/java/com/trashapp/gcms/trophy/Trophy.kt`

Represents a trophy with:
- ID, name, description
- Tier (Life, Beginner, Novice, Hard, Expert, Master)
- Required level, abilities, skills, points
- Rarity (Common, Uncommon, Rare, Epic, Legendary, Mythic)
- Unlock status and timestamp

### 2. Trophy System Logic
**File:** `app/src/main/java/com/trashapp/gcms/trophy/TrophySystem.kt`

Core trophy logic:
- Trophy count calculation per level-up (tier-based ranges)
- Rarity determination (weighted distribution per tier)
- Eligibility checking
- Trophy awarding with randomization

### 3. Trophy Manager
**File:** `app/src/main/java/com/trashapp/gcms/trophy/TrophyManager.kt`

Manages trophy collection:
- Trophy storage and tracking
- Unlock status management
- Collection statistics
- Progress notification

### 4. Trophy Generator
**File:** `app/src/main/java/com/trashapp/gcms/trophy/TrophyGenerator.kt`

Generates trophy definitions:
- Milestone trophies (levels 5, 10, 20, 30, 50, 75, 100, 125, 150, 175, 200)
- Tier-specific trophies
- Prerequisite-based trophies
- Random filler trophies for variety

### 5. GCMS Integration

#### Commands
**File:** `app/src/main/java/com/trashapp/gcms/commands/TrophyCommand.kt`

- `AwardTrophiesCommand` - Award trophies on level up
- `UnlockTrophyCommand` - Manually unlock a trophy
- `CheckTrophyEligibilityCommand` - Check eligible trophies
- `GetTrophiesByTierCommand` - Get trophies by tier
- `GetTrophiesByRarityCommand` - Get trophies by rarity
- `ResetTrophiesCommand` - Reset trophy collection
- `GenerateTrophiesCommand` - Generate all trophies
- `ClaimTrophyRewardsCommand` - Claim trophy rewards

#### Events
**File:** `app/src/main/java/com/trashapp/gcms/events/TrophyEvent.kt`

- `TrophiesAwardedEvent` - Trophies awarded on level up
- `TrophyUnlockedEvent` - Specific trophy unlocked
- `TrophyEligibleEvent` - Player eligible for trophy
- `TrophyProgressUpdatedEvent` - Collection progress updated
- `TrophyMilestoneEvent` - Milestone reached
- `TierTrophiesCompleteEvent` - All trophies in tier unlocked

#### Handler
**File:** `app/src/main/java/com/trashapp/gcms/handlers/TrophyCommandHandlerV2.kt`

Handles all trophy commands and emits appropriate events.

## Trophy Allocation Logic

### Tier-Based Trophy Ranges

| Tier       | Levels | Trophy Range |
|------------|--------|--------------|
| Life       | 1-5    | 1-3          |
| Beginner   | 6-20   | 2-5          |
| Novice     | 21-50  | 3-7          |
| Hard       | 51-80  | 5-10         |
| Expert     | 81-140 | 8-15         |
| Master     | 141-200| 10-20+       |

### Rarity Distribution per Tier

**Life Tier (1-5):**
- Common: 70%
- Uncommon: 25%
- Rare: 5%

**Beginner Tier (6-20):**
- Common: 60%
- Uncommon: 30%
- Rare: 8%
- Epic: 2%

**Novice Tier (21-50):**
- Common: 50%
- Uncommon: 35%
- Rare: 12%
- Epic: 3%

**Hard Tier (51-80):**
- Common: 35%
- Uncommon: 40%
- Rare: 18%
- Epic: 6%
- Legendary: 1%

**Expert Tier (81-140):**
- Common: 20%
- Uncommon: 35%
- Rare: 30%
- Epic: 12%
- Legendary: 3%

**Master Tier (141-200):**
- Common: 10%
- Uncommon: 25%
- Rare: 35%
- Epic: 20%
- Legendary: 8%
- Mythic: 2%

### XP and Point Rewards by Rarity

| Rarity    | XP Value | Point Bonus |
|-----------|----------|-------------|
| Common    | 25       | 5           |
| Uncommon  | 50       | 10          |
| Rare      | 100      | 25          |
| Epic      | 250      | 50          |
| Legendary | 500      | 100         |
| Mythic    | 1000     | 200         |

## Usage Examples

### 1. Initialize Trophy System

```kotlin
// Generate trophies on app start
val generator = TrophyGenerator()
val trophies = generator.generateAllTrophies()
trophyManager.initialize(trophies)
```

### 2. Award Trophies on Level Up

```kotlin
// In ProgressionCommandHandler when level increases
if (newLevel > oldLevel) {
    val tier = Tier.getTierForLevel(newLevel)
    
    controller.submitCommand(AwardTrophiesCommand(
        playerId = playerId,
        level = newLevel,
        tier = tier.name
    ), state)
}
```

### 3. Check Eligible Trophies

```kotlin
controller.submitCommand(CheckTrophyEligibilityCommand(
    playerId = playerId,
    playerLevel = currentLevel,
    playerPoints = availablePoints
), state)
```

### 4. Get Trophies by Tier

```kotlin
controller.submitCommand(GetTrophiesByTierCommand(
    tier = "EXPERT"
), state)

// Access via TrophyManager's StateFlow
val expertTrophies = trophyManager.getTrophiesByTier(Tier.EXPERT)
```

### 5. Get Trophies by Rarity

```kotlin
controller.submitCommand(GetTrophiesByRarityCommand(
    rarity = "LEGENDARY"
), state)

// Access via TrophyManager's StateFlow
val legendaryTrophies = trophyManager.getTrophiesByRarity(TrophyRarity.LEGENDARY)
```

## Integration with Progression System

### Automatic Trophy Awarding

When a player levels up (via ability/skill purchase or XP gain), the trophy system automatically awards trophies:

1. **Level Up Occurs** - Player gains a level
2. **Trophy Command Submitted** - `AwardTrophiesCommand` submitted to GCMS
3. **Trophy Count Calculated** - Based on tier and level
4. **Eligibility Checked** - Player's abilities, skills, and points evaluated
5. **Trophies Awarded** - Random selection from eligible trophies
6. **Rewards Granted** - XP and points from trophies added to player
7. **Events Emitted** - `TrophiesAwardedEvent`, `TrophyUnlockedEvent`, etc.

### Trophy XP and Points Flow

```
Level Up → Trophy Award → XP + Points → Level Up (recursive)
```

This creates a positive feedback loop where earning trophies grants additional XP and points, which can lead to further level-ups and more trophies.

## Event Handling

### Listen for Trophy Events

```kotlin
// In UI or game logic
controller.events.collect { event ->
    when (event) {
        is TrophiesAwardedEvent -> {
            // Display trophy notification
            showTrophyNotification(event.trophies, event.level)
            
            // Show XP and point rewards
            showRewardNotification(event.totalXP, event.totalPoints)
        }
        is TrophyUnlockedEvent -> {
            // Show individual trophy unlock
            showTrophyUnlockPopup(event.trophy)
        }
        is TrophyMilestoneEvent -> {
            // Celebrate milestone
            showMilestoneCelebration(event.milestone, event.bonusReward)
        }
        is TierTrophiesCompleteEvent -> {
            // Show tier completion
            showTierCompletion(event.tier, event.trophiesUnlocked)
        }
    }
}
```

## Trophy Categories

### Milestone Trophies
Awarded at specific levels (5, 10, 20, 30, 50, 75, 100, 125, 150, 175, 200)

### Tier-Specific Trophies
Themed trophies for each tier:
- Life: Basic achievement trophies
- Beginner: Combat and strategy focus
- Novice: Luck and defense focus
- Hard: Offense and resource focus
- Expert: Multi-requirement trophies
- Master: Ultimate challenge trophies

### Prerequisite-Based Trophies
Require specific combinations:
- Abilities only
- Skills only
- Points only
- Abilities + Skills
- Skills + Points
- Abilities + Points
- Abilities + Skills + Points

### Random Filler Trophies
Generated for variety at random levels

## Database Schema

```sql
CREATE TABLE trophies (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    tier TEXT NOT NULL,
    required_level INTEGER NOT NULL,
    required_abilities TEXT, -- JSON array
    required_skills TEXT, -- JSON array
    required_points INTEGER DEFAULT 0,
    rarity TEXT NOT NULL,
    icon TEXT,
    is_unlocked BOOLEAN DEFAULT FALSE,
    unlocked_at INTEGER
);

CREATE TABLE player_trophies (
    player_id TEXT,
    trophy_id TEXT,
    unlocked_at INTEGER,
    PRIMARY KEY (player_id, trophy_id),
    FOREIGN KEY (trophy_id) REFERENCES trophies(id)
);
```

## Testing

### Unit Tests

```kotlin
@Test
fun testTrophyAwarding() {
    val system = TrophySystem()
    val count = system.calculateTrophyCount(10, Tier.BEGINNER)
    assertTrue(count in 2..5)
}

@Test
fun testEligibility() {
    val trophy = Trophy(
        id = "test",
        name = "Test",
        description = "Test",
        tier = Tier.BEGINNER,
        requiredLevel = 10,
        requiredAbilities = listOf("Quick Draw"),
        rarity = TrophyRarity.RARE
    )
    
    val canUnlock = trophy.canUnlock(
        playerLevel = 10,
        playerPoints = 0,
        playerAbilities = setOf("Quick Draw"),
        playerSkills = emptySet()
    )
    
    assertTrue(canUnlock)
}
```

## Performance Considerations

1. **Initialization** - Trophy generation happens once on app start
2. **Caching** - TrophyManager uses StateFlow for reactive updates
3. **Efficiency** - Eligibility checking is O(n) where n is total trophies
4. **Memory** - ~200-300 trophy objects stored in memory

## Future Enhancements

1. **Seasonal Trophies** - Limited-time trophies for events
2. **Community Trophies** - Leaderboard-based trophies
3. **Trophy Trading** - Allow trading trophies between players
4. **Trophy Crafting** - Combine trophies to create new ones
5. **Trophy Upgrades** - Upgrade trophy rarity
6. **Trophy Sets** - Bonus rewards for collecting trophy sets

## Summary

The Trophy System is fully integrated with:
- ✅ Progression system (level-ups trigger trophy awards)
- ✅ GCMS architecture (commands and events)
- ✅ XP and point rewards (trophies grant both)
- ✅ Tier system (dynamic rewards per tier)
- ✅ Prerequisites (abilities, skills, points)
- ✅ Audio system (sound effects on trophy unlock)
- ✅ State management (reactive updates via StateFlow)

The system encourages players to strategically unlock abilities and skills to earn specific trophies, creating dynamic and engaging progression!