# Challenge System Integration Guide

## Overview

The Challenge System introduces side quests and achievements that gate level progression. Players must complete all challenges for their current level before they can advance, even if they have enough XP, points, or abilities/skills.

## Components

### 1. Challenge Data Model
**File:** `app/src/main/java/com/trashapp/gcms/challenge/Challenge.kt`

Represents a challenge with:
- ID, name, description
- Level and type
- Requirements (score, abilities, skills, points, combos, etc.)
- Rewards (XP, points, achievements)
- Rarity
- Progress tracking

### 2. Challenge System Logic
**File:** `app/src/main/java/com/trashapp/gcms/challenge/ChallengeSystem.kt`

Core challenge logic:
- Challenge count calculation per level (tier-based)
- Challenge type determination
- Difficulty scaling
- Requirement calculation
- Reward calculation
- Rarity determination
- Progress tracking

### 3. Challenge Manager
**File:** `app/src/main/java/com/trashapp/gcms/challenge/ChallengeManager.kt`

Manages challenge collection:
- Challenge generation for levels
- Progress tracking
- Level advancement checking
- Challenge queries

### 4. Challenge Generator
**File:** `app/src/main/java/com/trashapp/gcms/challenge/ChallengeGenerator.kt`

Generates challenge definitions:
- Milestone challenges (special levels: 5, 10, 20, 30, 50, 75, 100, 125, 150, 175, 200)
- Regular challenges (all levels 1-200)
- Dynamic difficulty
- Varied challenge types

### 5. GCMS Integration

#### Commands
**File:** `app/src/main/java/com/trashapp/gcms/commands/ChallengeCommand.kt`

- `GenerateChallengesCommand` - Generate challenges for a level
- `UpdateChallengeProgressCommand` - Update challenge progress
- `CompleteChallengeCommand` - Complete a challenge
- `CheckLevelAdvancementCommand` - Check if player can advance
- `SetCurrentLevelChallengesCommand` - Set current level challenges
- `GetChallengesForLevelCommand` - Get challenges for a level
- `GetChallengeProgressCommand` - Get progress for a level
- `ResetChallengesCommand` - Reset challenge system
- `TrackGameActionCommand` - Track game actions for progress

#### Events
**File:** `app/src/main/java/com/trashapp/gcms/events/ChallengeEvent.kt`

- `ChallengesGeneratedEvent` - Challenges generated
- `ChallengeProgressUpdatedEvent` - Progress updated
- `ChallengeCompletedEvent` - Challenge completed
- `LevelChallengesCompletedEvent` - All challenges for level completed
- `CanAdvanceToNextLevelEvent` - Player can advance
- `AdvancedToNextLevelEvent` - Player advanced
- `LevelAdvancementBlockedEvent` - Advancement blocked
- `ChallengeProgressSummaryEvent` - Progress summary
- `GameActionTrackedEvent` - Game action tracked

#### Handler
**File:** `app/src/main/java/com/trashapp/gcms/handlers/ChallengeCommandHandler.kt`

Handles all challenge commands and emits appropriate events.

## Challenge Types

### 1. Score Challenge
Reach a specific score in a single session.
```
Requirements: score: 500
Progress: Current score / Target score
```

### 2. Ability Use Challenge
Use abilities a certain number of times.
```
Requirements: abilitiesUsed: {"Quick Draw": 3, "Sheriff's Badge": 2}
Progress: Total abilities used / Target count
```

### 3. Skill Unlock Challenge
Unlock specific skills.
```
Requirements: skillsUnlocked: ["Card Shark", "Iron Will"]
Progress: Skills unlocked / Target count
```

### 4. Point Accumulation Challenge
Earn a certain number of points.
```
Requirements: pointsEarned: 250
Progress: Points earned / Target
```

### 5. Combo Challenge
Complete specific ability combinations.
```
Requirements: comboRequired: {
    abilityCombos: [["Quick Draw", "Sheriff's Badge"]],
    description: "Use Quick Draw and Sheriff's Badge in sequence"
}
Progress: Combos completed / Total combos
```

### 6. Win Streak Challenge
Achieve a win streak.
```
Requirements: winStreak: 3
Progress: Current streak / Target streak
```

### 7. Card Played Challenge
Play a certain number of cards.
```
Requirements: cardsPlayed: 20
Progress: Cards played / Target
```

### 8. Round Win Challenge
Win a certain number of rounds.
```
Requirements: roundsWon: 5
Progress: Rounds won / Target
```

### 9. Match Win Challenge
Win a certain number of matches.
```
Requirements: matchesWon: 2
Progress: Matches won / Target
```

### 10. Time Limit Challenge
Reach a score within a time limit.
```
Requirements: score: 300, timeLimit: 60 seconds
Progress: Score achieved / Target score
```

## Challenge Count by Tier

| Tier       | Levels | Challenge Count |
|------------|--------|-----------------|
| Life       | 1-5    | 1-2 challenges  |
| Beginner   | 6-20   | 2-3 challenges  |
| Novice     | 21-50  | 3-4 challenges  |
| Hard       | 51-80  | 4-5 challenges  |
| Expert     | 81-140 | 5-6 challenges  |
| Master     | 141-200| 6-8 challenges  |

## Difficulty Scaling

### Multipliers per Tier
- Life: 1.0x
- Beginner: 1.2x
- Novice: 1.5x
- Hard: 1.8x
- Expert: 2.2x
- Master: 2.5x

### Base Score Targets
- Level 1: 100 points
- Level 10: 500 points
- Level 20: 1,000 points
- Level 50: 2,500 points
- Level 100: 6,000 points
- Level 200: 15,000 points

## Rewards by Tier

| Tier       | Base XP | Base Points |
|------------|---------|-------------|
| Life       | 50      | 10          |
| Beginner   | 100     | 25          |
| Novice     | 200     | 50          |
| Hard       | 400     | 100         |
| Expert     | 800     | 200         |
| Master     | 1500    | 400         |

### Rarity Multipliers
- Common: 1.0x
- Uncommon: 1.5x
- Rare: 2.0x
- Epic: 3.0x
- Legendary: 5.0x
- Mythic: 10.0x

## Usage Examples

### 1. Generate Challenges for a Level

```kotlin
controller.submitCommand(GenerateChallengesCommand(
    level = 25
), state)
```

### 2. Track Game Action for Progress

```kotlin
// Player scores points
controller.submitCommand(TrackGameActionCommand(
    actionType = "score",
    actionData = mapOf("score" to 150),
    playerId = "player_1"
), state)

// Player uses ability
controller.submitCommand(TrackGameActionCommand(
    actionType = "ability_use",
    actionData = mapOf("abilityId" to "Quick Draw"),
    playerId = "player_1"
), state)

// Player plays a card
controller.submitCommand(TrackGameActionCommand(
    actionType = "card_played",
    actionData = emptyMap(),
    playerId = "player_1"
), state)
```

### 3. Check if Player Can Advance

```kotlin
controller.submitCommand(CheckLevelAdvancementCommand(
    currentLevel = 25,
    hasRequiredXP = true,
    hasRequiredPoints = true,
    hasRequiredAbilities = true,
    hasRequiredSkills = true
), state)
```

### 4. Get Challenge Progress

```kotlin
controller.submitCommand(GetChallengeProgressCommand(
    level = 25
), state)
```

## Integration with Progression System

### Level Advancement Flow

```
1. Player earns XP and points
2. Player purchases abilities/skills
3. Player completes challenges
4. Check level advancement
5. If all requirements met → Advance to next level
6. Generate new challenges for next level
```

### Advancement Requirements

To advance to the next level, player must have:
1. ✅ Required XP
2. ✅ Required points
3. ✅ Required abilities
4. ✅ Required skills
5. ✅ All challenges completed for current level

### Challenge-Progression Interaction

```
Complete Challenge → Earn XP + Points → Purchase Abilities/Skills → More XP → Complete More Challenges
```

## Milestone Challenges

Special challenges at milestone levels:

### Level 5 - First Steps
- Score: 300 points
- Play 20 cards

### Level 10 - Double Digits
- Score: 500 points
- Win 5 rounds
- Use abilities 5 times

### Level 20 - Novice Achievement
- Score: 1,000 points
- Win 1 match
- Earn 500 points

### Level 30 - Veteran Player
- Score: 1,500 points
- 3 win streak
- Play 100 cards

### Level 50 - Half Century
- Score: 2,500 points
- Win 3 matches
- Complete combos

### Level 75 - Dedicated Gamer
- Score: 4,000 points
- 5 win streak
- Speed run: 3,000 points in 60 seconds

### Level 100 - Century Club
- Score: 6,000 points
- Win 5 matches
- Use abilities 20 times

### Level 125 - Elite Ascendant
- Score: 8,000 points
- Elite combos
- 7 win streak

### Level 150 - Expert Legend
- Score: 10,000 points
- Win 8 matches
- Speed run: 5,000 points in 45 seconds

### Level 175 - Master Craftsman
- Score: 12,000 points
- Master combos
- 10 win streak

### Level 200 - Ultimate Champion
- Score: 15,000 points
- Win 10 matches
- Ultimate combos
- Speed run: 8,000 points in 30 seconds

## Event Handling

### Listen for Challenge Events

```kotlin
// In UI or game logic
controller.events.collect { event ->
    when (event) {
        is ChallengeProgressUpdatedEvent -> {
            // Update progress bar
            updateChallengeProgressBar(event.challengeId, event.percentage)
        }
        is ChallengeCompletedEvent -> {
            // Show completion notification
            showChallengeCompletionPopup(event.challenge, event.xpEarned, event.pointsEarned)
        }
        is LevelChallengesCompletedEvent -> {
            // Celebrate level completion
            showLevelCompletionCelebration(event.level, event.totalXPEarned)
        }
        is LevelAdvancementBlockedEvent -> {
            // Show missing requirements
            showAdvancementBlockedDialog(event.missingRequirements)
        }
        is CanAdvanceToNextLevelEvent -> {
            // Enable advance button
            enableAdvanceButton(event.nextLevel)
        }
    }
}
```

## Game Action Tracking

### Tracking Scenarios

```kotlin
// Score earned
TrackGameActionCommand(
    actionType = "score",
    actionData = mapOf("score" to currentScore),
    playerId = playerId
)

// Ability used
TrackGameActionCommand(
    actionType = "ability_use",
    actionData = mapOf("abilityId" to abilityId),
    playerId = playerId
)

// Skill unlocked
TrackGameActionCommand(
    actionType = "skill_unlock",
    actionData = mapOf("skillId" to skillId),
    playerId = playerId
)

// Points earned
TrackGameActionCommand(
    actionType = "points",
    actionData = mapOf("points" to pointsEarned),
    playerId = playerId
)

// Card played
TrackGameActionCommand(
    actionType = "card_played",
    actionData = emptyMap(),
    playerId = playerId
)

// Round won
TrackGameActionCommand(
    actionType = "round_win",
    actionData = emptyMap(),
    playerId = playerId
)

// Match won
TrackGameActionCommand(
    actionType = "match_win",
    actionData = emptyMap(),
    playerId = playerId
)

// Win streak
TrackGameActionCommand(
    actionType = "win_streak",
    actionData = mapOf("streak" to currentStreak),
    playerId = playerId
)

// Combo completed
TrackGameActionCommand(
    actionType = "combo",
    actionData = mapOf("combo" to listOf("Quick Draw", "Sheriff's Badge")),
    playerId = playerId
)

// Time limit challenge
TrackGameActionCommand(
    actionType = "score",
    actionData = mapOf(
        "score" to score,
        "timeUsed" to timeElapsed
    ),
    playerId = playerId
)
```

## Database Schema

```sql
CREATE TABLE challenges (
    id TEXT PRIMARY KEY,
    level INTEGER NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    type TEXT NOT NULL,
    requirements TEXT NOT NULL, -- JSON
    reward TEXT NOT NULL, -- JSON
    rarity TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at INTEGER,
    progress INTEGER DEFAULT 0,
    max_progress INTEGER NOT NULL
);

CREATE TABLE player_challenges (
    player_id TEXT,
    challenge_id TEXT,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at INTEGER,
    progress INTEGER DEFAULT 0,
    PRIMARY KEY (player_id, challenge_id),
    FOREIGN KEY (challenge_id) REFERENCES challenges(id)
);
```

## Testing

### Unit Tests

```kotlin
@Test
fun testChallengeGeneration() {
    val generator = ChallengeGenerator()
    val challenges = generator.generateChallengesForLevel(25)
    assertTrue(challenges.isNotEmpty())
}

@Test
fun testChallengeProgress() {
    val system = ChallengeSystem()
    val challenges = listOf(
        Challenge(
            id = "test",
            level = 1,
            name = "Test",
            description = "Test",
            type = ChallengeType.SCORE,
            requirements = ChallengeRequirements(score = 100),
            reward = ChallengeReward("Test", "Test", 50, 10),
            rarity = TrophyRarity.COMMON,
            progress = 0,
            maxProgress = 100
        )
    )
    
    val progress = system.getProgressSummary(challenges)
    assertEquals(0, progress.completedChallenges)
}
```

## Performance Considerations

1. **Initialization** - Challenges generated on demand per level
2. **Caching** - ChallengeManager uses StateFlow for reactive updates
3. **Efficiency** - Progress checking is O(n) where n is challenges per level
4. **Memory** - ~200-300 challenge objects stored in memory per player

## Benefits

1. **Strategic Depth** - Players must complete challenges, not just grind XP
2. **Engagement** - varied challenge types keep gameplay interesting
3. **Progression Gating** - Prevents skipping ahead too quickly
4. **Achievement System** - Challenges serve as achievements
5. **Replayability** - Randomized challenges add variety
6. **Integration** - Works seamlessly with existing progression

## Future Enhancements

1. **Daily Challenges** - Refreshing challenges each day
2. **Weekly Challenges** - Special weekly challenges with bonus rewards
3. **Challenge Chains** - Sequences of related challenges
4. **Co-op Challenges** - Multiplayer challenges
5. **Challenge Leaderboards** - Compete for fastest completion
6. **Challenge Crafting** - Create custom challenges

## Summary

The Challenge System is fully integrated with:
- ✅ Progression system (gates level advancement)
- ✅ GCMS architecture (commands and events)
- ✅ XP and point rewards (challenges grant both)
- ✅ Game action tracking (automatic progress updates)
- ✅ Trophy system (challenges and trophies work together)
- ✅ Audio system (sound effects on completion)
- ✅ State management (reactive updates via StateFlow)

The system adds strategic depth to progression by requiring players to complete challenges before advancing, encouraging exploration of different gameplay mechanics!