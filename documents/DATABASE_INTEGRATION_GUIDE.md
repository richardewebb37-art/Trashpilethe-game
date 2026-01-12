# Database Integration Guide

## Overview

The TRASH game now includes full database persistence for the Trophy and Challenge systems using Room database. This guide covers the database architecture, usage, and integration with existing systems.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Room Database                            │
│                   (TrashDatabase)                           │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────┐  ┌─────────────┐                            │
│  │  TrophyDao  │  │ ChallengeDao│                            │
│  │  (CRUD)     │  │  (CRUD)     │                            │
│  └──────┬──────┘  └──────┬──────┘                            │
│         │                │                                   │
│         └────────────────┼──────────┐                        │
│                          ▼          │                        │
│              ┌───────────────────┐   │                        │
│              │   Repository      │   │                        │
│              │  TrophyRepo       │   │                        │
│              │  ChallengeRepo    │   │                        │
│              └─────────┬─────────┘   │                        │
│                        │             │                        │
│                        ▼             ▼                        │
│              ┌──────────────┐ ┌──────────────┐                │
│              │TrophyManager │ │ChallengeMgr  │                │
│              └──────────────┘ └──────────────┘                │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## Database Entities

### Trophy Entities

#### TrophyEntity
Stores individual trophy data:
- `id`: Unique trophy identifier
- `name`: Trophy name
- `description`: Trophy description
- `tier`: Tier (Life, Beginner, Novice, Hard, Expert, Master)
- `rarity`: Rarity (Common, Uncommon, Rare, Epic, Legendary, Mythic)
- `xpReward`: XP awarded for unlocking
- `pointsReward`: Points awarded for unlocking
- `requiredLevel`: Minimum level required
- `requiredAbilities`: Minimum abilities required (optional)
- `requiredSkills`: Minimum skills required (optional)
- `requiredPoints`: Minimum points required (optional)
- `isMilestone`: Whether this is a milestone trophy
- `isUnlocked`: Whether the player has unlocked this trophy
- `unlockedAt`: Timestamp when unlocked
- `playerId`: Player identifier

#### TrophyCollectionEntity
Stores player's trophy collection stats:
- `playerId`: Player identifier
- `unlockedTrophies`: JSON string of unlocked trophy IDs
- `totalXPEarned`: Total XP earned from trophies
- `totalPointsEarned`: Total points earned from trophies
- `lastUpdated`: Last update timestamp

#### TrophyProgressEntity
Stores individual trophy progress:
- `trophyId`: Trophy identifier
- `playerId`: Player identifier
- `isUnlocked`: Whether unlocked
- `progress`: Progress (0.0 to 1.0)
- `unlockedAt`: Timestamp when unlocked
- `lastUpdated`: Last update timestamp

### Challenge Entities

#### ChallengeEntity
Stores individual challenge data:
- `id`: Unique challenge identifier
- `level`: Level for this challenge
- `description`: Challenge description
- `type`: Challenge type (SCORE, ABILITY_USE, etc.)
- `xpReward`: XP awarded for completion
- `pointsReward`: Points awarded for completion
- `unlocksLevel`: Whether completion unlocks next level
- `requiredScore`: Score requirement (optional)
- `requiredAbilities`: Ability count requirement (optional)
- `requiredSkills`: Skill count requirement (optional)
- `requiredPoints`: Points requirement (optional)
- `requiredCombos`: Combo requirement (optional)
- `requiredStreaks`: Streak requirement (optional)
- `requiredCardsPlayed`: Cards played requirement (optional)
- `requiredRoundWins`: Round wins requirement (optional)
- `requiredMatchWins`: Match wins requirement (optional)
- `timeLimit`: Time limit in seconds (optional)
- `isMilestone`: Whether this is a milestone challenge
- `isCompleted`: Whether the player has completed this challenge
- `completedAt`: Timestamp when completed
- `playerId`: Player identifier

#### ChallengeProgressEntity
Stores individual challenge progress:
- `challengeId`: Challenge identifier
- `playerId`: Player identifier
- `currentScore`: Current score progress
- `currentAbilities`: Current ability count
- `currentSkills`: Current skill count
- `currentPoints`: Current points
- `currentCombos`: Current combos
- `currentStreaks`: Current streaks
- `currentCardsPlayed`: Current cards played
- `currentRoundWins`: Current round wins
- `currentMatchWins`: Current match wins
- `isCompleted`: Whether completed
- `progress`: Progress (0.0 to 1.0)
- `completedAt`: Timestamp when completed
- `lastUpdated`: Last update timestamp

#### LevelChallengesEntity
Stores challenges for a specific level:
- `level`: Level number
- `playerId`: Player identifier
- `challengeIds`: JSON string of challenge IDs
- `completedCount`: Number of completed challenges
- `totalCount`: Total number of challenges
- `canAdvance`: Whether player can advance to next level
- `lastUpdated`: Last update timestamp

#### LevelProgressEntity
Stores overall progress for a level:
- `level`: Level number
- `playerId`: Player identifier
- `totalXP`: Total XP earned
- `requiredXP`: XP required to advance
- `points`: Current points
- `requiredPoints`: Points required
- `abilitiesCount`: Current ability count
- `requiredAbilities`: Abilities required
- `skillsCount`: Current skill count
- `requiredSkills`: Skills required
- `completedChallenges`: JSON string of completed challenge IDs
- `inProgressChallenges`: JSON string of in-progress challenge IDs
- `notStartedChallenges`: JSON string of not-started challenge IDs
- `totalChallenges`: Total number of challenges
- `canAdvanceToNextLevel`: Whether can advance
- `lastUpdated`: Last update timestamp

## Data Access Objects (DAOs)

### TrophyDao

**Insert Operations:**
- `insertTrophy(trophy: TrophyEntity)`: Insert a single trophy
- `insertTrophies(trophies: List<TrophyEntity>)`: Insert multiple trophies
- `insertTrophyCollection(collection: TrophyCollectionEntity)`: Insert trophy collection
- `insertTrophyProgress(progress: TrophyProgressEntity)`: Insert trophy progress
- `insertTrophyProgressList(progressList: List<TrophyProgressEntity>)`: Insert multiple progress entries

**Query Operations:**
- `getTrophiesForPlayer(playerId: String)`: Get all trophies for a player
- `getTrophiesByTier(playerId: String, tier: String)`: Get trophies by tier
- `getTrophiesByRarity(playerId: String, rarity: String)`: Get trophies by rarity
- `getTrophiesForLevel(playerId: String, level: Int)`: Get trophies for a specific level
- `getMilestoneTrophies(playerId: String)`: Get milestone trophies
- `getUnlockedTrophies(playerId: String)`: Get unlocked trophies
- `getLockedTrophies(playerId: String)`: Get locked trophies
- `getTrophyById(trophyId: String)`: Get a specific trophy
- `getTrophyCollection(playerId: String)`: Get trophy collection
- `getTrophyProgressForPlayer(playerId: String)`: Get all trophy progress
- `getTrophyProgress(trophyId: String, playerId: String)`: Get specific trophy progress

**Update Operations:**
- `updateTrophyUnlockStatus(trophyId: String, isUnlocked: Boolean, unlockedAt: Long?)`: Update unlock status
- `updateTrophyCollection(...)`: Update collection stats
- `updateTrophyProgress(...)`: Update trophy progress

**Delete Operations:**
- `deleteTrophiesForPlayer(playerId: String)`: Delete all trophies for a player
- `deleteTrophyCollection(playerId: String)`: Delete trophy collection
- `deleteTrophyProgressForPlayer(playerId: String)`: Delete all trophy progress
- `deleteTrophyProgress(trophyId: String, playerId: String)`: Delete specific trophy progress

**Count Operations:**
- `countTrophiesForPlayer(playerId: String)`: Count total trophies
- `countUnlockedTrophiesForPlayer(playerId: String)`: Count unlocked trophies

### ChallengeDao

**Insert Operations:**
- `insertChallenge(challenge: ChallengeEntity)`: Insert a single challenge
- `insertChallenges(challenges: List<ChallengeEntity>)`: Insert multiple challenges
- `insertChallengeProgress(progress: ChallengeProgressEntity)`: Insert challenge progress
- `insertChallengeProgressList(progressList: List<ChallengeProgressEntity>)`: Insert multiple progress entries
- `insertLevelChallenges(levelChallenges: LevelChallengesEntity)`: Insert level challenges
- `insertLevelProgress(levelProgress: LevelProgressEntity)`: Insert level progress

**Query Operations:**
- `getChallengesForPlayer(playerId: String)`: Get all challenges for a player
- `getChallengesForLevel(playerId: String, level: Int)`: Get challenges for a specific level
- `getChallengesByType(playerId: String, type: String)`: Get challenges by type
- `getMilestoneChallenges(playerId: String)`: Get milestone challenges
- `getCompletedChallenges(playerId: String)`: Get completed challenges
- `getIncompleteChallenges(playerId: String)`: Get incomplete challenges
- `getChallengeById(challengeId: String)`: Get a specific challenge
- `getChallengeProgressForPlayer(playerId: String)`: Get all challenge progress
- `getChallengeProgress(challengeId: String, playerId: String)`: Get specific challenge progress
- `getLevelChallengesForPlayer(playerId: String)`: Get level challenges
- `getLevelChallenges(level: Int, playerId: String)`: Get challenges for specific level
- `getLevelProgressForPlayer(playerId: String)`: Get level progress
- `getLevelProgress(level: Int, playerId: String)`: Get progress for specific level

**Update Operations:**
- `updateChallengeCompletion(challengeId: String, isCompleted: Boolean, completedAt: Long?)`: Update completion status
- `updateChallengeScoreProgress(...)`: Update score progress
- `updateChallengeAbilitiesProgress(...)`: Update abilities progress
- `updateChallengeSkillsProgress(...)`: Update skills progress
- `updateChallengePointsProgress(...)`: Update points progress
- `updateChallengeCombosProgress(...)`: Update combos progress
- `updateChallengeStreaksProgress(...)`: Update streaks progress
- `updateChallengeCardsPlayedProgress(...)`: Update cards played progress
- `updateChallengeRoundWinsProgress(...)`: Update round wins progress
- `updateChallengeMatchWinsProgress(...)`: Update match wins progress
- `updateLevelChallengesCompletion(...)`: Update level challenges completion
- `updateLevelProgress(...)`: Update level progress

**Delete Operations:**
- `deleteChallengesForPlayer(playerId: String)`: Delete all challenges for a player
- `deleteChallengeProgressForPlayer(playerId: String)`: Delete all challenge progress
- `deleteChallengeProgress(challengeId: String, playerId: String)`: Delete specific challenge progress
- `deleteLevelChallengesForPlayer(playerId: String)`: Delete level challenges
- `deleteLevelProgressForPlayer(playerId: String)`: Delete level progress

**Count Operations:**
- `countChallengesForPlayer(playerId: String)`: Count total challenges
- `countCompletedChallengesForPlayer(playerId: String)`: Count completed challenges
- `countChallengesForLevel(playerId: String, level: Int)`: Count challenges for level
- `countCompletedChallengesForLevel(playerId: String, level: Int)`: Count completed challenges for level

## Repositories

### TrophyRepository

Bridges between TrophyManager and Room database:

```kotlin
class TrophyRepository(private val trophyDao: TrophyDao) {
    
    // Save trophies to database
    suspend fun saveTrophies(playerId: String, trophies: List<Trophy>)
    
    // Get trophies
    fun getTrophiesForPlayer(playerId: String): Flow<List<Trophy>>
    fun getTrophiesByTier(playerId: String, tier: Tier): Flow<List<Trophy>>
    fun getTrophiesByRarity(playerId: String, rarity: TrophyRarity): Flow<List<Trophy>>
    
    // Unlock trophy
    suspend fun unlockTrophy(trophyId: String, playerId: String): Boolean
    
    // Get stats
    suspend fun getTrophyStats(playerId: String): TrophyStats
    
    // Update progress
    suspend fun updateTrophyProgress(...)
}
```

### ChallengeRepository

Bridges between ChallengeManager and Room database:

```kotlin
class ChallengeRepository(private val challengeDao: ChallengeDao) {
    
    // Save challenges to database
    suspend fun saveChallenges(playerId: String, challenges: List<Challenge>)
    suspend fun saveChallengesForLevel(playerId: String, level: Int, challenges: List<Challenge>)
    
    // Get challenges
    fun getChallengesForPlayer(playerId: String): Flow<List<Challenge>>
    fun getChallengesForLevel(playerId: String, level: Int): Flow<List<Challenge>>
    
    // Update progress
    suspend fun updateChallengeScoreProgress(...)
    suspend fun updateChallengeAbilitiesProgress(...)
    // ... and more progress update methods
    
    // Complete challenge
    suspend fun completeChallenge(challengeId: String, playerId: String, level: Int): Boolean
    
    // Get level progress
    suspend fun getLevelProgress(level: Int, playerId: String): LevelProgressEntity?
}
```

## Database Initialization

```kotlin
// Get database instance
val database = TrashDatabase.getInstance(context)

// Get DAOs
val trophyDao = database.trophyDao()
val challengeDao = database.challengeDao()

// Get repositories
val trophyRepository = TrophyRepository(trophyDao)
val challengeRepository = ChallengeRepository(challengeDao)
```

## Usage Examples

### Example 1: Save Trophies for a Player

```kotlin
// Generate trophies
val trophies = TrophyGenerator.generateForAllLevels()

// Save to database
lifecycleScope.launch {
    trophyRepository.saveTrophies(playerId, trophies)
}
```

### Example 2: Unlock a Trophy

```kotlin
// Check eligibility first
if (trophySystem.checkEligibility(trophy, playerLevel, abilitiesCount, skillsCount, points)) {
    // Unlock trophy
    lifecycleScope.launch {
        val success = trophyRepository.unlockTrophy(trophy.id, playerId)
        if (success) {
            // Play unlock sound
            audioManager.playSound("trophy_unlock")
        }
    }
}
```

### Example 3: Save Challenges for a Level

```kotlin
// Generate challenges
val challenges = ChallengeGenerator.generateForLevel(level)

// Save to database
lifecycleScope.launch {
    challengeRepository.saveChallengesForLevel(playerId, level, challenges)
}
```

### Example 4: Update Challenge Progress

```kotlin
// Update score progress
lifecycleScope.launch {
    challengeRepository.updateChallengeScoreProgress(
        challengeId = challenge.id,
        playerId = playerId,
        currentScore = currentScore,
        requiredScore = challenge.requirements.score!!
    )
}
```

### Example 5: Get Trophy Stats

```kotlin
// Get stats
lifecycleScope.launch {
    val stats = trophyRepository.getTrophyStats(playerId)
    println("Completion: ${stats.completionPercentage}%")
    println("Total XP: ${stats.totalXPEarned}")
}
```

## Migrations

Currently using `fallbackToDestructiveMigration()` for development. For production, implement proper migrations:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns or tables
        database.execSQL("ALTER TABLE trophies ADD COLUMN newColumn TEXT")
    }
}

// In database builder
Room.databaseBuilder(context, TrashDatabase::class.java, DATABASE_NAME)
    .addMigrations(MIGRATION_1_2)
    .build()
```

## Best Practices

1. **Use Flow for reactive updates**: All DAO queries return Flow for automatic updates when data changes.

2. **Use coroutines for database operations**: All DAO methods are suspend functions and must be called from a coroutine.

3. **Batch operations**: Use batch insert methods for better performance when inserting multiple records.

4. **Use transactions**: Use `@Transaction` annotation for complex operations that need atomicity.

5. **Handle errors**: Always handle potential database errors with try-catch blocks.

6. **Close database**: Database is automatically managed by Room, but be aware of memory usage.

## Performance Considerations

- Use indexes on frequently queried columns (level, tier, rarity, playerId)
- Avoid querying large datasets without pagination
- Use batch operations for inserts and updates
- Consider using Paging 3 library for large lists

## Testing

See `DatabaseIntegrationTest.kt` for comprehensive database tests. All database operations should be tested with Room's in-memory database.

## Summary

The database integration provides complete persistence for Trophy and Challenge systems, ensuring player progress is saved and can be restored across sessions. The repository pattern provides a clean abstraction layer between the business logic and the database, making the codebase more maintainable and testable.