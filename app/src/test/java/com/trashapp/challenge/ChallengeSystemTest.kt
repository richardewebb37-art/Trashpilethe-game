package com.trashapp.challenge

import com.trashapp.gcms.challenge.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ChallengeSystem
 */
class ChallengeSystemTest {
    
    private lateinit var challengeSystem: ChallengeSystem
    
    @Before
    fun setup() {
        challengeSystem = ChallengeSystem()
    }
    
    /**
     * Test 1: Calculate challenge count for level
     * Verifies challenge count increases with tier
     */
    @Test
    fun testCalculateChallengeCountForLevel() {
        println("\n=== Test 1: Calculate Challenge Count for Level ===")
        
        // Test Life tier (levels 1-5)
        for (level in 1..5) {
            val tier = Tier.fromLevel(level)
            val count = challengeSystem.calculateChallengeCount(level, tier)
            assertTrue("Life tier should have 1-3 challenges", count in 1..3)
            println("Level $level (Life): $count challenges")
        }
        
        // Test Master tier (levels 141-200)
        for (level in 141..145) {
            val tier = Tier.fromLevel(level)
            val count = challengeSystem.calculateChallengeCount(level, tier)
            assertTrue("Master tier should have 6-8 challenges", count in 6..8)
            println("Level $level (Master): $count challenges")
        }
        
        println("✅ Test 1 PASSED\n")
    }
    
    /**
     * Test 2: Calculate difficulty multiplier
     * Verifies difficulty increases with level
     */
    @Test
    fun testCalculateDifficultyMultiplier() {
        println("\n=== Test 2: Calculate Difficulty Multiplier ===")
        
        val level1Multiplier = challengeSystem.calculateDifficultyMultiplier(1)
        val level50Multiplier = challengeSystem.calculateDifficultyMultiplier(50)
        val level100Multiplier = challengeSystem.calculateDifficultyMultiplier(100)
        val level200Multiplier = challengeSystem.calculateDifficultyMultiplier(200)
        
        println("Level 1 Multiplier: $level1Multiplier")
        println("Level 50 Multiplier: $level50Multiplier")
        println("Level 100 Multiplier: $level100Multiplier")
        println("Level 200 Multiplier: $level200Multiplier")
        
        assertTrue("Level 50 should have higher multiplier than Level 1", level50Multiplier > level1Multiplier)
        assertTrue("Level 100 should have higher multiplier than Level 50", level100Multiplier > level50Multiplier)
        assertTrue("Level 200 should have higher multiplier than Level 100", level200Multiplier > level100Multiplier)
        
        // Verify specific ranges
        assertTrue("Level 1 multiplier should be ~1.0x", level1Multiplier in 0.9..1.1)
        assertTrue("Level 200 multiplier should be ~2.5x", level200Multiplier in 2.4..2.6)
        
        println("✅ Test 2 PASSED\n")
    }
    
    /**
     * Test 3: Calculate requirement value
     * Verifies requirements scale with difficulty
     */
    @Test
    fun testCalculateRequirementValue() {
        println("\n=== Test 3: Calculate Requirement Value ===")
        
        val baseValue = 100
        val level1Score = challengeSystem.calculateRequirement(1, ChallengeType.SCORE)
        val level50Score = challengeSystem.calculateRequirement(50, ChallengeType.SCORE)
        val level100Score = challengeSystem.calculateRequirement(100, ChallengeType.SCORE)
        
        println("Level 1 Score Requirement: $level1Score")
        println("Level 50 Score Requirement: $level50Score")
        println("Level 100 Score Requirement: $level100Score")
        
        assertTrue("Level 50 should have higher requirement than Level 1", level50Score > level1Score)
        assertTrue("Level 100 should have higher requirement than Level 50", level100Score > level50Score)
        
        // Test different challenge types
        val scoreReq = challengeSystem.calculateRequirement(10, ChallengeType.SCORE)
        val abilityReq = challengeSystem.calculateRequirement(10, ChallengeType.ABILITY_USE)
        val pointReq = challengeSystem.calculateRequirement(10, ChallengeType.POINT_ACCUMULATION)
        
        println("\nLevel 10 Requirements:")
        println("  Score: $scoreReq")
        println("  Abilities: $abilityReq")
        println("  Points: $pointReq")
        
        assertTrue("Requirements should be positive", scoreReq > 0)
        assertTrue("Requirements should be positive", abilityReq > 0)
        assertTrue("Requirements should be positive", pointReq > 0)
        
        println("✅ Test 3 PASSED\n")
    }
    
    /**
     * Test 4: Calculate XP reward
     * Verifies XP rewards scale with rarity and level
     */
    @Test
    fun testCalculateXPReward() {
        println("\n=== Test 4: Calculate XP Reward ===")
        
        val baseXP = 100
        val commonXP = challengeSystem.calculateXPReward(baseXP, Rarity.Common)
        val uncommonXP = challengeSystem.calculateXPReward(baseXP, Rarity.Uncommon)
        val rareXP = challengeSystem.calculateXPReward(baseXP, Rarity.Rare)
        val epicXP = challengeSystem.calculateXPReward(baseXP, Rarity.Epic)
        val legendaryXP = challengeSystem.calculateXPReward(baseXP, Rarity.Legendary)
        val mythicXP = challengeSystem.calculateXPReward(baseXP, Rarity.Mythic)
        
        println("Common XP: $commonXP")
        println("Uncommon XP: $uncommonXP")
        println("Rare XP: $rareXP")
        println("Epic XP: $epicXP")
        println("Legendary XP: $legendaryXP")
        println("Mythic XP: $mythicXP")
        
        assertTrue("Uncommon should give more XP than Common", uncommonXP > commonXP)
        assertTrue("Rare should give more XP than Uncommon", rareXP > uncommonXP)
        assertTrue("Epic should give more XP than Rare", epicXP > rareXP)
        assertTrue("Legendary should give more XP than Epic", legendaryXP > epicXP)
        assertTrue("Mythic should give more XP than Legendary", mythicXP > legendaryXP)
        
        println("✅ Test 4 PASSED\n")
    }
    
    /**
     * Test 5: Calculate points reward
     * Verifies points rewards scale with rarity and level
     */
    @Test
    fun testCalculatePointsReward() {
        println("\n=== Test 5: Calculate Points Reward ===")
        
        val basePoints = 50
        val commonPoints = challengeSystem.calculatePointsReward(basePoints, Rarity.Common)
        val uncommonPoints = challengeSystem.calculatePointsReward(basePoints, Rarity.Uncommon)
        val rarePoints = challengeSystem.calculatePointsReward(basePoints, Rarity.Rare)
        val epicPoints = challengeSystem.calculatePointsReward(basePoints, Rarity.Epic)
        val legendaryPoints = challengeSystem.calculatePointsReward(basePoints, Rarity.Legendary)
        val mythicPoints = challengeSystem.calculatePointsReward(basePoints, Rarity.Mythic)
        
        println("Common Points: $commonPoints")
        println("Uncommon Points: $uncommonPoints")
        println("Rare Points: $rarePoints")
        println("Epic Points: $epicPoints")
        println("Legendary Points: $legendaryPoints")
        println("Mythic Points: $mythicPoints")
        
        assertTrue("Uncommon should give more points than Common", uncommonPoints > commonPoints)
        assertTrue("Rare should give more points than Uncommon", rarePoints > uncommonPoints)
        assertTrue("Epic should give more points than Rare", epicPoints > rarePoints)
        assertTrue("Legendary should give more points than Epic", legendaryPoints > epicPoints)
        assertTrue("Mythic should give more points than Legendary", mythicPoints > legendaryPoints)
        
        println("✅ Test 5 PASSED\n")
    }
    
    /**
     * Test 6: Calculate challenge progress
     * Verifies progress calculation is correct
     */
    @Test
    fun testCalculateChallengeProgress() {
        println("\n=== Test 6: Calculate Challenge Progress ===")
        
        // Create test challenge
        val scoreChallenge = Challenge(
            id = "challenge_1",
            level = 1,
            description = "Score 1000 points",
            type = ChallengeType.SCORE,
            xpReward = 100,
            pointsReward = 20,
            unlocksLevel = false,
            requirements = ChallengeRequirements(score = 1000),
            isMilestone = false
        )
        
        // Test progress calculations
        val progress0 = challengeSystem.calculateProgress(scoreChallenge, 0)
        val progress500 = challengeSystem.calculateProgress(scoreChallenge, 500)
        val progress1000 = challengeSystem.calculateProgress(scoreChallenge, 1000)
        
        println("Score 0: ${progress0 * 100}%")
        println("Score 500: ${progress500 * 100}%")
        println("Score 1000: ${progress1000 * 100}%")
        
        assertEquals("0 score should be 0% progress", 0.0f, progress0, 0.0f)
        assertEquals("500 score should be 50% progress", 0.5f, progress500, 0.0f)
        assertEquals("1000 score should be 100% progress", 1.0f, progress1000, 0.0f)
        
        // Test with ability requirement
        val abilityChallenge = Challenge(
            id = "challenge_2",
            level = 1,
            description = "Use 5 abilities",
            type = ChallengeType.ABILITY_USE,
            xpReward = 100,
            pointsReward = 20,
            unlocksLevel = false,
            requirements = ChallengeRequirements(abilities = 5),
            isMilestone = false
        )
        
        val abilityProgress0 = challengeSystem.calculateProgress(abilityChallenge, 0)
        val abilityProgress3 = challengeSystem.calculateProgress(abilityChallenge, 3)
        val abilityProgress5 = challengeSystem.calculateProgress(abilityChallenge, 5)
        
        println("\nAbilities 0: ${abilityProgress0 * 100}%")
        println("Abilities 3: ${abilityProgress3 * 100}%")
        println("Abilities 5: ${abilityProgress5 * 100}%")
        
        assertEquals("0 abilities should be 0% progress", 0.0f, abilityProgress0, 0.0f)
        assertEquals("3 abilities should be 60% progress", 0.6f, abilityProgress3, 0.0f)
        assertEquals("5 abilities should be 100% progress", 1.0f, abilityProgress5, 0.0f)
        
        println("✅ Test 6 PASSED\n")
    }
    
    /**
     * Test 7: Generate challenge summary
     * Verifies summary calculation is correct
     */
    @Test
    fun testGenerateChallengeSummary() {
        println("\n=== Test 7: Generate Challenge Summary ===")
        
        val challenges = listOf(
            Challenge(
                id = "challenge_1",
                level = 1,
                description = "Score 1000 points",
                type = ChallengeType.SCORE,
                xpReward = 100,
                pointsReward = 20,
                unlocksLevel = false,
                requirements = ChallengeRequirements(score = 1000),
                isMilestone = false
            ),
            Challenge(
                id = "challenge_2",
                level = 1,
                description = "Use 5 abilities",
                type = ChallengeType.ABILITY_USE,
                xpReward = 100,
                pointsReward = 20,
                unlocksLevel = false,
                requirements = ChallengeRequirements(abilities = 5),
                isMilestone = false
            ),
            Challenge(
                id = "challenge_3",
                level = 1,
                description = "Unlock a skill",
                type = ChallengeType.SKILL_UNLOCK,
                xpReward = 100,
                pointsReward = 20,
                unlocksLevel = false,
                requirements = ChallengeRequirements(skills = 1),
                isMilestone = true
            )
        )
        
        val completedIds = listOf("challenge_1")
        val inProgressIds = listOf("challenge_2")
        
        val summary = challengeSystem.generateSummary(challenges, completedIds, inProgressIds, 100, 50, 5, 1)
        
        println("Total Challenges: ${summary.totalChallenges}")
        println("Completed: ${summary.completedChallenges.size}")
        println("In Progress: ${summary.inProgressChallenges.size}")
        println("Not Started: ${summary.notStartedChallenges.size}")
        println("Total XP: ${summary.totalXP}")
        println("Total Points: ${summary.points}")
        println("Abilities Count: ${summary.abilitiesCount}")
        println("Skills Count: ${summary.skillsCount}")
        
        assertEquals("Total challenges should be 3", 3, summary.totalChallenges)
        assertEquals("Completed count should be 1", 1, summary.completedChallenges.size)
        assertEquals("In progress count should be 1", 1, summary.inProgressChallenges.size)
        assertEquals("Not started count should be 1", 1, summary.notStartedChallenges.size)
        assertEquals("Total XP should be 100", 100, summary.totalXP)
        assertEquals("Points should be 50", 50, summary.points)
        assertEquals("Abilities count should be 5", 5, summary.abilitiesCount)
        assertEquals("Skills count should be 1", 1, summary.skillsCount)
        
        println("✅ Test 7 PASSED\n")
    }
    
    /**
     * Test 8: Check level advancement
     * Verifies advancement criteria
     */
    @Test
    fun testCheckLevelAdvancement() {
        println("\n=== Test 8: Check Level Advancement ===")
        
        val challenges = listOf(
            Challenge(
                id = "challenge_1",
                level = 1,
                description = "Score 1000 points",
                type = ChallengeType.SCORE,
                xpReward = 100,
                pointsReward = 20,
                unlocksLevel = false,
                requirements = ChallengeRequirements(score = 1000),
                isMilestone = false
            ),
            Challenge(
                id = "challenge_2",
                level = 1,
                description = "Use 5 abilities",
                type = ChallengeType.ABILITY_USE,
                xpReward = 100,
                pointsReward = 20,
                unlocksLevel = false,
                requirements = ChallengeRequirements(abilities = 5),
                isMilestone = false
            )
        )
        
        val summary = LevelProgress(
            level = 1,
            totalXP = 100,
            requiredXP = 100,
            points = 50,
            requiredPoints = 50,
            abilitiesCount = 5,
            requiredAbilities = 3,
            skillsCount = 1,
            requiredSkills = 1,
            completedChallenges = listOf("challenge_1"),
            inProgressChallenges = listOf("challenge_2"),
            notStartedChallenges = emptyList(),
            totalChallenges = 2,
            canAdvanceToNextLevel = false
        )
        
        // Test with all requirements met
        val canAdvance = challengeSystem.checkAdvancement(summary)
        println("Can advance to next level: $canAdvance")
        assertTrue("Should be able to advance when all requirements met", canAdvance)
        
        // Test with incomplete challenges
        val summaryIncomplete = summary.copy(
            completedChallenges = listOf("challenge_1"),
            canAdvanceToNextLevel = false
        )
        val cannotAdvance = challengeSystem.checkAdvancement(summaryIncomplete)
        println("Can advance with incomplete challenges: $cannotAdvance")
        assertFalse("Should NOT be able to advance with incomplete challenges", cannotAdvance)
        
        // Test with insufficient XP
        val summaryLowXP = summary.copy(
            totalXP = 50,
            requiredXP = 100,
            canAdvanceToNextLevel = false
        )
        val cannotAdvanceXP = challengeSystem.checkAdvancement(summaryLowXP)
        println("Can advance with low XP: $cannotAdvanceXP")
        assertFalse("Should NOT be able to advance with low XP", cannotAdvanceXP)
        
        println("✅ Test 8 PASSED\n")
    }
}