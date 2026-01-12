package com.trashapp.trophy

import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.trophy.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for TrophyGenerator
 */
class TrophyGeneratorTest {
    
    private val trophyGenerator = TrophyGenerator()
    
    /**
     * Test 1: Generate trophies for level
     * Verifies correct number of trophies generated
     */
    @Test
    fun testGenerateTrophiesForLevel() {
        println("\n=== Test 1: Generate Trophies for Level ===")
        
        // Test Life tier level
        val level1Trophies = trophyGenerator.generateForLevel(1)
        println("Level 1 trophies: ${level1Trophies.size}")
        assertTrue("Level 1 should generate 1-3 trophies", level1Trophies.size in 1..3)
        
        // Test Master tier level
        val level150Trophies = trophyGenerator.generateForLevel(150)
        println("Level 150 trophies: ${level150Trophies.size}")
        assertTrue("Level 150 should generate 10-15 trophies", level150Trophies.size in 10..15)
        
        // Verify all trophies have required fields
        level1Trophies.forEach { trophy ->
            assertNotNull("Trophy ID should not be null", trophy.id)
            assertNotNull("Trophy name should not be null", trophy.name)
            assertNotNull("Trophy description should not be null", trophy.description)
            assertNotNull("Trophy tier should not be null", trophy.tier)
            assertNotNull("Trophy rarity should not be null", trophy.rarity)
            assertTrue("XP reward should be positive", trophy.xpReward > 0)
            assertTrue("Points reward should be positive", trophy.pointsReward > 0)
        }
        
        println("✅ Test 1 PASSED\n")
    }
    
    /**
     * Test 2: Generate milestone trophies
     * Verifies milestone trophies are generated for special levels
     */
    @Test
    fun testGenerateMilestoneTrophies() {
        println("\n=== Test 2: Generate Milestone Trophies ===")
        
        val milestoneLevels = listOf(5, 10, 20, 30, 50, 75, 100, 125, 150, 175, 200)
        
        milestoneLevels.forEach { level ->
            val trophies = trophyGenerator.generateForLevel(level)
            val milestoneTrophies = trophies.filter { it.isMilestone }
            
            println("Level $level: ${trophies.size} total, ${milestoneTrophies.size} milestone")
            
            assertTrue("Milestone levels should have at least 1 milestone trophy", milestoneTrophies.isNotEmpty())
            
            milestoneTrophies.forEach { trophy ->
                assertTrue("Milestone trophy level requirement should match level", trophy.prerequisites.level == level)
            }
        }
        
        println("✅ Test 2 PASSED\n")
    }
    
    /**
     * Test 3: Generate trophies for all levels
     * Verifies total trophy count is reasonable
     */
    @Test
    fun testGenerateAllTrophies() {
        println("\n=== Test 3: Generate All Trophies ===")
        
        val allTrophies = trophyGenerator.generateForAllLevels()
        
        println("Total trophies generated: ${allTrophies.size}")
        
        // Verify total count
        assertTrue("Should generate at least 200 trophies", allTrophies.size >= 200)
        assertTrue("Should not exceed 500 trophies", allTrophies.size <= 500)
        
        // Verify distribution across tiers
        val trophiesByTier = allTrophies.groupBy { it.tier }
        println("\nTrophy distribution by tier:")
        trophiesByTier.forEach { (tier, trophies) ->
            println("  $tier: ${trophies.size} trophies")
            assertTrue("Each tier should have trophies", trophies.isNotEmpty())
        }
        
        // Verify all trophies have unique IDs
        val trophyIds = allTrophies.map { it.id }
        val uniqueIds = trophyIds.toSet()
        assertEquals("All trophy IDs should be unique", trophyIds.size, uniqueIds.size)
        
        println("✅ Test 3 PASSED\n")
    }
    
    /**
     * Test 4: Generate prerequisite-based trophies
     * Verifies trophies with various prerequisites
     */
    @Test
    fun testGeneratePrerequisiteTrophies() {
        println("\n=== Test 4: Generate Prerequisite Trophies ===")
        
        val trophies = trophyGenerator.generateForAllLevels()
        
        // Count trophies with different prerequisite types
        val levelPrerequisites = trophies.count { it.prerequisites.level > 0 }
        val abilityPrerequisites = trophies.count { it.prerequisites.abilities != null }
        val skillPrerequisites = trophies.count { it.prerequisites.skills != null }
        val pointPrerequisites = trophies.count { it.prerequisites.points != null }
        
        println("Trophies with level prerequisites: $levelPrerequisites")
        println("Trophies with ability prerequisites: $abilityPrerequisites")
        println("Trophies with skill prerequisites: $skillPrerequisites")
        println("Trophies with point prerequisites: $pointPrerequisites")
        
        // All trophies should have at least level prerequisite
        assertEquals("All trophies should have level prerequisite", trophies.size, levelPrerequisites)
        
        // Some trophies should have other prerequisites
        assertTrue("Some trophies should have ability prerequisites", abilityPrerequisites > 0)
        assertTrue("Some trophies should have skill prerequisites", skillPrerequisites > 0)
        assertTrue("Some trophies should have point prerequisites", pointPrerequisites > 0)
        
        println("✅ Test 4 PASSED\n")
    }
    
    /**
     * Test 5: Verify rarity distribution
     * Verifies rarity distribution matches expectations
     */
    @Test
    fun testVerifyRarityDistribution() {
        println("\n=== Test 5: Verify Rarity Distribution ===")
        
        val allTrophies = trophyGenerator.generateForAllLevels()
        
        // Count trophies by rarity
        val trophiesByRarity = allTrophies.groupBy { it.rarity }
        println("Rarity distribution:")
        trophiesByRarity.forEach { (rarity, trophies) ->
            val percentage = trophies.size * 100 / allTrophies.size
            println("  $rarity: ${trophies.size} (${percentage}%)")
        }
        
        // Verify all rarities are present
        TrophyRarity.values().forEach { rarity ->
            assertTrue("Should have $rarity trophies", trophiesByRarity[rarity]?.isNotEmpty() == true)
        }
        
        // Life tier should have mostly Common/Uncommon/Rare
        val lifeTrophies = allTrophies.filter { it.tier == Tier.Life }
        val lifeRarities = lifeTrophies.groupBy { it.rarity }
        println("\nLife tier rarity distribution:")
        lifeRarities.forEach { (rarity, trophies) ->
            val percentage = trophies.size * 100 / lifeTrophies.size
            println("  $rarity: ${trophies.size} (${percentage}%)")
        }
        
        // Master tier should have diverse rarities
        val masterTrophies = allTrophies.filter { it.tier == Tier.Master }
        val masterRarities = masterTrophies.groupBy { it.rarity }
        println("\nMaster tier rarity distribution:")
        masterRarities.forEach { (rarity, trophies) ->
            val percentage = trophies.size * 100 / masterTrophies.size
            println("  $rarity: ${trophies.size} (${percentage}%)")
        }
        
        assertTrue("Master tier should have more rarities than Life tier", masterRarities.size >= lifeRarities.size)
        
        println("✅ Test 5 PASSED\n")
    }
    
    /**
     * Test 6: Verify XP and points rewards
     * Verifies rewards scale correctly with rarity
     */
    @Test
    fun testVerifyRewards() {
        println("\n=== Test 6: Verify Rewards ===")
        
        val allTrophies = trophyGenerator.generateForAllLevels()
        
        // Calculate average rewards by rarity
        val rewardsByRarity = allTrophies.groupBy { it.rarity }
        println("Average rewards by rarity:")
        rewardsByRarity.forEach { (rarity, trophies) ->
            val avgXP = trophies.sumOf { it.xpReward } / trophies.size
            val avgPoints = trophies.sumOf { it.pointsReward } / trophies.size
            println("  $rarity: XP=$avgXP, Points=$avgPoints")
        }
        
        // Verify rewards increase with rarity
        val commonXP = rewardsByRarity[TrophyRarity.Common]?.sumOf { it.xpReward }?.div(rewardsByRarity[TrophyRarity.Common]!!.size) ?: 0
        val legendaryXP = rewardsByRarity[TrophyRarity.Legendary]?.sumOf { it.xpReward }?.div(rewardsByRarity[TrophyRarity.Legendary]!!.size) ?: 0
        
        assertTrue("Legendary should give more XP than Common", legendaryXP > commonXP)
        
        // Verify rewards are within expected ranges
        val minXP = allTrophies.minOf { it.xpReward }
        val maxXP = allTrophies.maxOf { it.xpReward }
        val minPoints = allTrophies.minOf { it.pointsReward }
        val maxPoints = allTrophies.maxOf { it.pointsReward }
        
        println("\nReward ranges:")
        println("  XP: $minXP - $maxXP")
        println("  Points: $minPoints - $maxPoints")
        
        assertEquals("Min XP should be 25", 25, minXP)
        assertEquals("Max XP should be 1000", 1000, maxXP)
        assertEquals("Min points should be 5", 5, minPoints)
        assertEquals("Max points should be 200", 200, maxPoints)
        
        println("✅ Test 6 PASSED\n")
    }
    
    /**
     * Test 7: Verify trophy naming
     * Verifies trophies have unique and descriptive names
     */
    @Test
    fun testVerifyTrophyNaming() {
        println("\n=== Test 7: Verify Trophy Naming ===")
        
        val allTrophies = trophyGenerator.generateForAllLevels()
        
        // Check for duplicate names
        val names = allTrophies.map { it.name }
        val duplicateNames = names.groupingBy { it }.eachCount().filter { it.value > 1 }
        
        if (duplicateNames.isNotEmpty()) {
            println("Warning: Found duplicate trophy names:")
            duplicateNames.forEach { (name, count) ->
                println("  '$name': $count occurrences")
            }
        }
        
        // Most trophies should have unique names
        val uniqueNames = names.toSet()
        val uniquePercentage = uniqueNames.size * 100 / names.size
        println("Unique names: $uniquePercentage%")
        
        assertTrue("Should have mostly unique trophy names", uniquePercentage >= 90)
        
        // Verify names are not empty
        allTrophies.forEach { trophy ->
            assertNotNull("Trophy name should not be null", trophy.name)
            assertTrue("Trophy name should not be empty", trophy.name.isNotEmpty())
            assertTrue("Trophy name should not be too short", trophy.name.length >= 3)
        }
        
        println("✅ Test 7 PASSED\n")
    }
    
    /**
     * Test 8: Verify tier matching
     * Verifies trophy tier matches level tier
     */
    @Test
    fun testVerifyTierMatching() {
        println("\n=== Test 8: Verify Tier Matching ===")
        
        val allTrophies = trophyGenerator.generateForAllLevels()
        
        var mismatches = 0
        
        allTrophies.forEach { trophy ->
            // Trophy tier should match level tier
            val expectedTier = Tier.fromLevel(trophy.prerequisites.level)
            if (trophy.tier != expectedTier) {
                mismatches++
                println("Mismatch: Level ${trophy.prerequisites.level} should be $expectedTier, but trophy is ${trophy.tier}")
            }
        }
        
        println("Tier mismatches: $mismatches")
        
        // All trophies should match their level tier
        assertEquals("All trophies should match their level tier", 0, mismatches)
        
        println("✅ Test 8 PASSED\n")
    }
}