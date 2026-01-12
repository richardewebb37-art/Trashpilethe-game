package com.trashapp.challenge

import com.trashapp.gcms.challenge.*
import com.trashapp.gcms.progression.Tier
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ChallengeGenerator
 */
class ChallengeGeneratorTest {
    
    private val challengeGenerator = ChallengeGenerator()
    
    /**
     * Test 1: Generate challenges for level
     * Verifies correct number of challenges generated
     */
    @Test
    fun testGenerateChallengesForLevel() {
        println("\n=== Test 1: Generate Challenges for Level ===")
        
        // Test Life tier level
        val level1Challenges = challengeGenerator.generateForLevel(1)
        println("Level 1 challenges: ${level1Challenges.size}")
        assertTrue("Level 1 should generate 1-3 challenges", level1Challenges.size in 1..3)
        
        // Test Master tier level
        val level150Challenges = challengeGenerator.generateForLevel(150)
        println("Level 150 challenges: ${level150Challenges.size}")
        assertTrue("Level 150 should generate 6-8 challenges", level150Challenges.size in 6..8)
        
        // Verify all challenges have required fields
        level1Challenges.forEach { challenge ->
            assertNotNull("Challenge ID should not be null", challenge.id)
            assertNotNull("Challenge description should not be null", challenge.description)
            assertNotNull("Challenge type should not be null", challenge.type)
            assertNotNull("Challenge requirements should not be null", challenge.requirements)
            assertTrue("XP reward should be positive", challenge.xpReward > 0)
            assertTrue("Points reward should be positive", challenge.pointsReward > 0)
        }
        
        println("✅ Test 1 PASSED\n")
    }
    
    /**
     * Test 2: Generate milestone challenges
     * Verifies milestone challenges for special levels
     */
    @Test
    fun testGenerateMilestoneChallenges() {
        println("\n=== Test 2: Generate Milestone Challenges ===")
        
        val milestoneLevels = listOf(5, 10, 20, 30, 50, 75, 100, 125, 150, 175, 200)
        
        milestoneLevels.forEach { level ->
            val challenges = challengeGenerator.generateForLevel(level)
            val milestoneChallenges = challenges.filter { it.isMilestone }
            
            println("Level $level: ${challenges.size} total, ${milestoneChallenges.size} milestone")
            
            // Milestone levels should have at least 1 milestone challenge
            assertTrue("Milestone levels should have at least 1 milestone challenge", milestoneChallenges.isNotEmpty())
        }
        
        println("✅ Test 2 PASSED\n")
    }
    
    /**
     * Test 3: Generate all challenges
     * Verifies total challenge count is reasonable
     */
    @Test
    fun testGenerateAllChallenges() {
        println("\n=== Test 3: Generate All Challenges ===")
        
        val allChallenges = challengeGenerator.generateForAllLevels()
        
        println("Total challenges generated: ${allChallenges.size}")
        
        // Verify total count
        assertTrue("Should generate at least 800 challenges", allChallenges.size >= 800)
        assertTrue("Should not exceed 1500 challenges", allChallenges.size <= 1500)
        
        // Verify distribution across tiers
        val challengesByTier = allChallenges.groupBy { Tier.fromLevel(it.level) }
        println("\nChallenge distribution by tier:")
        challengesByTier.forEach { (tier, challenges) ->
            println("  $tier: ${challenges.size} challenges")
            assertTrue("Each tier should have challenges", challenges.isNotEmpty())
        }
        
        // Verify all challenges have unique IDs
        val challengeIds = allChallenges.map { it.id }
        val uniqueIds = challengeIds.toSet()
        assertEquals("All challenge IDs should be unique", challengeIds.size, uniqueIds.size)
        
        println("✅ Test 3 PASSED\n")
    }
    
    /**
     * Test 4: Verify challenge type distribution
     * Verifies all challenge types are present
     */
    @Test
    fun testVerifyChallengeTypeDistribution() {
        println("\n=== Test 4: Verify Challenge Type Distribution ===")
        
        val allChallenges = challengeGenerator.generateForAllLevels()
        
        // Count challenges by type
        val challengesByType = allChallenges.groupBy { it.type }
        println("Challenge type distribution:")
        challengesByType.forEach { (type, challenges) ->
            val percentage = challenges.size * 100 / allChallenges.size
            println("  $type: ${challenges.size} ($percentage%)")
        }
        
        // Verify all challenge types are present
        ChallengeType.values().forEach { type ->
            assertTrue("Should have $type challenges", challengesByType[type]?.isNotEmpty() == true)
        }
        
        // Verify distribution is reasonable
        val typeCount = challengesByType.size
        println("\nTotal unique challenge types: $typeCount")
        assertTrue("Should have all challenge types", typeCount == ChallengeType.values().size)
        
        println("✅ Test 4 PASSED\n")
    }
    
    /**
     * Test 5: Verify challenge requirements
     * Verifies requirements are valid and scale correctly
     */
    @Test
    fun testVerifyChallengeRequirements() {
        println("\n=== Test 5: Verify Challenge Requirements ===")
        
        val allChallenges = challengeGenerator.generateForAllLevels()
        
        // Count challenges with different requirement types
        val scoreRequirements = allChallenges.count { it.requirements.score != null }
        val abilityRequirements = allChallenges.count { it.requirements.abilities != null }
        val skillRequirements = allChallenges.count { it.requirements.skills != null }
        val pointRequirements = allChallenges.count { it.requirements.points != null }
        val comboRequirements = allChallenges.count { it.requirements.combos != null }
        val streakRequirements = allChallenges.count { it.requirements.streaks != null }
        val cardRequirements = allChallenges.count { it.requirements.cardsPlayed != null }
        val roundWinRequirements = allChallenges.count { it.requirements.roundWins != null }
        val matchWinRequirements = allChallenges.count { it.requirements.matchWins != null }
        val timeLimitRequirements = allChallenges.count { it.requirements.timeLimit != null }
        
        println("Challenges with score requirements: $scoreRequirements")
        println("Challenges with ability requirements: $abilityRequirements")
        println("Challenges with skill requirements: $skillRequirements")
        println("Challenges with point requirements: $pointRequirements")
        println("Challenges with combo requirements: $comboRequirements")
        println("Challenges with streak requirements: $streakRequirements")
        println("Challenges with card requirements: $cardRequirements")
        println("Challenges with round win requirements: $roundWinRequirements")
        println("Challenges with match win requirements: $matchWinRequirements")
        println("Challenges with time limit requirements: $timeLimitRequirements")
        
        // All requirement types should be present
        assertTrue("Should have score requirements", scoreRequirements > 0)
        assertTrue("Should have ability requirements", abilityRequirements > 0)
        assertTrue("Should have skill requirements", skillRequirements > 0)
        assertTrue("Should have point requirements", pointRequirements > 0)
        
        // Verify requirements are positive
        allChallenges.forEach { challenge ->
            challenge.requirements.score?.let { assertTrue("Score requirement should be positive", it > 0) }
            challenge.requirements.abilities?.let { assertTrue("Ability requirement should be positive", it > 0) }
            challenge.requirements.skills?.let { assertTrue("Skill requirement should be positive", it > 0) }
            challenge.requirements.points?.let { assertTrue("Point requirement should be positive", it > 0) }
            challenge.requirements.combos?.let { assertTrue("Combo requirement should be positive", it > 0) }
            challenge.requirements.streaks?.let { assertTrue("Streak requirement should be positive", it > 0) }
            challenge.requirements.cardsPlayed?.let { assertTrue("Card requirement should be positive", it > 0) }
            challenge.requirements.roundWins?.let { assertTrue("Round win requirement should be positive", it > 0) }
            challenge.requirements.matchWins?.let { assertTrue("Match win requirement should be positive", it > 0) }
            challenge.requirements.timeLimit?.let { assertTrue("Time limit should be positive", it > 0) }
        }
        
        println("✅ Test 5 PASSED\n")
    }
    
    /**
     * Test 6: Verify challenge difficulty scaling
     * Verifies requirements increase with level
     */
    @Test
    fun testVerifyDifficultyScaling() {
        println("\n=== Test 6: Verify Difficulty Scaling ===")
        
        val level1Challenges = challengeGenerator.generateForLevel(1)
        val level50Challenges = challengeGenerator.generateForLevel(50)
        val level100Challenges = challengeGenerator.generateForLevel(100)
        
        // Calculate average score requirements
        val level1AvgScore = level1Challenges
            .mapNotNull { it.requirements.score }
            .average()
        val level50AvgScore = level50Challenges
            .mapNotNull { it.requirements.score }
            .average()
        val level100AvgScore = level100Challenges
            .mapNotNull { it.requirements.score }
            .average()
        
        println("Average score requirements:")
        println("  Level 1: $level1AvgScore")
        println("  Level 50: $level50AvgScore")
        println("  Level 100: $level100AvgScore")
        
        assertTrue("Level 50 should have higher score requirements than Level 1", level50AvgScore > level1AvgScore)
        assertTrue("Level 100 should have higher score requirements than Level 50", level100AvgScore > level50AvgScore)
        
        // Calculate average XP rewards
        val level1AvgXP = level1Challenges.map { it.xpReward }.average()
        val level50AvgXP = level50Challenges.map { it.xpReward }.average()
        val level100AvgXP = level100Challenges.map { it.xpReward }.average()
        
        println("\nAverage XP rewards:")
        println("  Level 1: $level1AvgXP")
        println("  Level 50: $level50AvgXP")
        println("  Level 100: $level100AvgXP")
        
        assertTrue("Level 50 should give more XP than Level 1", level50AvgXP > level1AvgXP)
        assertTrue("Level 100 should give more XP than Level 50", level100AvgXP > level50AvgXP)
        
        println("✅ Test 6 PASSED\n")
    }
    
    /**
     * Test 7: Verify challenge naming
     * Verifies challenges have descriptive names
     */
    @Test
    fun testVerifyChallengeNaming() {
        println("\n=== Test 7: Verify Challenge Naming ===")
        
        val allChallenges = challengeGenerator.generateForAllLevels()
        
        // Check for duplicate descriptions
        val descriptions = allChallenges.map { it.description }
        val duplicateDescriptions = descriptions.groupingBy { it }.eachCount().filter { it.value > 1 }
        
        if (duplicateDescriptions.isNotEmpty()) {
            println("Warning: Found duplicate challenge descriptions:")
            duplicateDescriptions.forEach { (desc, count) ->
                println("  '$desc': $count occurrences")
            }
        }
        
        // Verify descriptions are not empty
        allChallenges.forEach { challenge ->
            assertNotNull("Challenge description should not be null", challenge.description)
            assertTrue("Challenge description should not be empty", challenge.description.isNotEmpty())
            assertTrue("Challenge description should not be too short", challenge.description.length >= 5)
        }
        
        println("✅ Test 7 PASSED\n")
    }
    
    /**
     * Test 8: Verify special challenge templates
     * Verifies special challenges have appropriate types
     */
    @Test
    fun testVerifySpecialChallengeTemplates() {
        println("\n=== Test 8: Verify Special Challenge Templates ===")
        
        // Test Match Winner challenge
        val matchWinnerChallenges = challengeGenerator.generateForLevel(50)
            .filter { it.type == ChallengeType.MATCH_WIN }
        
        println("Match winner challenges at level 50: ${matchWinnerChallenges.size}")
        
        if (matchWinnerChallenges.isNotEmpty()) {
            matchWinnerChallenges.forEach { challenge ->
                assertNotNull("Match winner should have match win requirement", challenge.requirements.matchWins)
                assertTrue("Match win requirement should be positive", challenge.requirements.matchWins!! > 0)
            }
        }
        
        // Test Combo challenge
        val comboChallenges = challengeGenerator.generateForLevel(50)
            .filter { it.type == ChallengeType.COMBO }
        
        println("Combo challenges at level 50: ${comboChallenges.size}")
        
        if (comboChallenges.isNotEmpty()) {
            comboChallenges.forEach { challenge ->
                assertNotNull("Combo challenge should have combo requirement", challenge.requirements.combos)
                assertTrue("Combo requirement should be positive", challenge.requirements.combos!! > 0)
            }
        }
        
        // Test Time Limit challenge
        val timeLimitChallenges = challengeGenerator.generateForLevel(50)
            .filter { it.type == ChallengeType.TIME_LIMIT }
        
        println("Time limit challenges at level 50: ${timeLimitChallenges.size}")
        
        if (timeLimitChallenges.isNotEmpty()) {
            timeLimitChallenges.forEach { challenge ->
                assertNotNull("Time limit challenge should have time limit", challenge.requirements.timeLimit)
                assertTrue("Time limit should be positive", challenge.requirements.timeLimit!! > 0)
            }
        }
        
        println("✅ Test 8 PASSED\n")
    }
}