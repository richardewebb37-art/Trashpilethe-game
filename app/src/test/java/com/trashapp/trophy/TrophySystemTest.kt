package com.trashapp.trophy

import com.trashapp.gcms.progression.Rarity
import com.trashapp.gcms.progression.Tier
import com.trashapp.gcms.trophy.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TrophySystem
 */
class TrophySystemTest {
    
    private lateinit var trophySystem: TrophySystem
    
    @Before
    fun setup() {
        trophySystem = TrophySystem()
    }
    
    /**
     * Test 1: Calculate trophy count for level
     * Verifies trophy count increases with level
     */
    @Test
    fun testCalculateTrophyCountForLevel() {
        println("\n=== Test 1: Calculate Trophy Count for Level ===")
        
        // Test Life tier (levels 1-5)
        for (level in 1..5) {
            val count = trophySystem.calculateTrophyCount(level, Tier.Life)
            assertTrue("Life tier should have 1-3 trophies", count in 1..3)
            println("Level $level (Life): $count trophies")
        }
        
        // Test Master tier (levels 141-200)
        for (level in 141..145) {
            val count = trophySystem.calculateTrophyCount(level, Tier.Master)
            assertTrue("Master tier should have 10-20+ trophies", count >= 10)
            println("Level $level (Master): $count trophies")
        }
        
        println("✅ Test 1 PASSED\n")
    }
    
    /**
     * Test 2: Calculate weighted trophy count with level bonus
     * Verifies level bonus is applied correctly
     */
    @Test
    fun testCalculateTrophyCountWithLevelBonus() {
        println("\n=== Test 2: Calculate Trophy Count with Level Bonus ===")
        
        val level5 = trophySystem.calculateTrophyCount(5, Tier.Life)
        val level50 = trophySystem.calculateTrophyCount(50, Tier.Novice)
        val level100 = trophySystem.calculateTrophyCount(100, Tier.Expert)
        val level200 = trophySystem.calculateTrophyCount(200, Tier.Master)
        
        println("Level 5 (Life): $level5 trophies")
        println("Level 50 (Novice): $level50 trophies")
        println("Level 100 (Expert): $level100 trophies")
        println("Level 200 (Master): $level200 trophies")
        
        assertTrue("Level 50 should have more trophies than Level 5", level50 > level5)
        assertTrue("Level 100 should have more trophies than Level 50", level100 > level50)
        assertTrue("Level 200 should have more trophies than Level 100", level200 > level100)
        
        println("✅ Test 2 PASSED\n")
    }
    
    /**
     * Test 3: Get rarity distribution for tier
     * Verifies rarity distribution matches tier specifications
     */
    @Test
    fun testGetRarityDistributionForTier() {
        println("\n=== Test 3: Get Rarity Distribution for Tier ===")
        
        // Test Life tier (mostly Common/Uncommon/Rare)
        val lifeDistribution = trophySystem.getRarityDistribution(Tier.Life)
        println("Life Tier Distribution:")
        lifeDistribution.forEach { (rarity, probability) ->
            println("  $rarity: ${probability * 100}%")
        }
        assertTrue("Life tier should have Common rarity", lifeDistribution.containsKey(TrophyRarity.Common))
        assertFalse("Life tier should not have Mythic rarity", lifeDistribution.containsKey(TrophyRarity.Mythic))
        
        // Test Master tier (all rarities)
        val masterDistribution = trophySystem.getRarityDistribution(Tier.Master)
        println("\nMaster Tier Distribution:")
        masterDistribution.forEach { (rarity, probability) ->
            println("  $rarity: ${probability * 100}%")
        }
        assertTrue("Master tier should have all rarities", masterDistribution.size == 6)
        assertTrue("Master tier should have Mythic rarity", masterDistribution.containsKey(TrophyRarity.Mythic))
        
        println("✅ Test 3 PASSED\n")
    }
    
    /**
     * Test 4: Get rarity for trophy based on tier
     * Verifies rarity selection follows distribution
     */
    @Test
    fun testGetRarityForTrophy() {
        println("\n=== Test 4: Get Rarity for Trophy ===")
        
        // Generate many trophies and check distribution
        val sampleSize = 1000
        val lifeRarities = mutableListOf<TrophyRarity>()
        val masterRarities = mutableListOf<TrophyRarity>()
        
        repeat(sampleSize) {
            lifeRarities.add(trophySystem.getRarity(Tier.Life))
            masterRarities.add(trophySystem.getRarity(Tier.Master))
        }
        
        // Count rarities
        val lifeCounts = lifeRarities.groupingBy { it }.eachCount()
        val masterCounts = masterRarities.groupingBy { it }.eachCount()
        
        println("Life Tier Rarity Distribution ($sampleSize samples):")
        lifeCounts.forEach { (rarity, count) ->
            println("  $rarity: $count (${count * 100 / sampleSize}%)")
        }
        
        println("\nMaster Tier Rarity Distribution ($sampleSize samples):")
        masterCounts.forEach { (rarity, count) ->
            println("  $rarity: $count (${count * 100 / sampleSize}%)")
        }
        
        // Life tier should have mostly Common
        assertTrue("Life tier should have many Common trophies", lifeCounts[TrophyRarity.Common]!! > lifeCounts.size * 0.5)
        
        // Master tier should have diverse rarities
        assertTrue("Master tier should have diverse rarities", masterCounts.size >= 5)
        
        println("✅ Test 4 PASSED\n")
    }
    
    /**
     * Test 5: Calculate XP reward based on rarity
     * Verifies XP rewards scale with rarity
     */
    @Test
    fun testCalculateXPReward() {
        println("\n=== Test 5: Calculate XP Reward ===")
        
        val commonXP = trophySystem.calculateXPReward(TrophyRarity.Common)
        val uncommonXP = trophySystem.calculateXPReward(TrophyRarity.Uncommon)
        val rareXP = trophySystem.calculateXPReward(TrophyRarity.Rare)
        val epicXP = trophySystem.calculateXPReward(TrophyRarity.Epic)
        val legendaryXP = trophySystem.calculateXPReward(TrophyRarity.Legendary)
        val mythicXP = trophySystem.calculateXPReward(TrophyRarity.Mythic)
        
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
        
        // Verify specific values
        assertEquals("Common should give 25 XP", 25, commonXP)
        assertEquals("Mythic should give 1000 XP", 1000, mythicXP)
        
        println("✅ Test 5 PASSED\n")
    }
    
    /**
     * Test 6: Calculate points reward based on rarity
     * Verifies points rewards scale with rarity
     */
    @Test
    fun testCalculatePointsReward() {
        println("\n=== Test 6: Calculate Points Reward ===")
        
        val commonPoints = trophySystem.calculatePointsReward(TrophyRarity.Common)
        val uncommonPoints = trophySystem.calculatePointsReward(TrophyRarity.Uncommon)
        val rarePoints = trophySystem.calculatePointsReward(TrophyRarity.Rare)
        val epicPoints = trophySystem.calculatePointsReward(TrophyRarity.Epic)
        val legendaryPoints = trophySystem.calculatePointsReward(TrophyRarity.Legendary)
        val mythicPoints = trophySystem.calculatePointsReward(TrophyRarity.Mythic)
        
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
        
        // Verify specific values
        assertEquals("Common should give 5 points", 5, commonPoints)
        assertEquals("Mythic should give 200 points", 200, mythicPoints)
        
        println("✅ Test 6 PASSED\n")
    }
    
    /**
     * Test 7: Check trophy eligibility
     * Verifies prerequisite checking works correctly
     */
    @Test
    fun testCheckTrophyEligibility() {
        println("\n=== Test 7: Check Trophy Eligibility ===")
        
        // Create test trophies
        val lowLevelTrophy = Trophy(
            id = "trophy_1",
            name = "Beginner Trophy",
            description = "Complete level 1",
            tier = Tier.Life,
            rarity = TrophyRarity.Common,
            xpReward = 25,
            pointsReward = 5,
            prerequisites = TrophyPrerequisites(level = 1),
            isMilestone = false
        )
        
        val highLevelTrophy = Trophy(
            id = "trophy_2",
            name = "Master Trophy",
            description = "Complete level 100",
            tier = Tier.Master,
            rarity = TrophyRarity.Legendary,
            xpReward = 1000,
            pointsReward = 200,
            prerequisites = TrophyPrerequisites(level = 100),
            isMilestone = true
        )
        
        val playerLevel1 = 1
        val playerLevel50 = 50
        val playerLevel100 = 100
        
        // Test low level trophy
        val lowLevelEligible1 = trophySystem.checkEligibility(lowLevelTrophy, playerLevel1, 0, 0, 0)
        assertTrue("Level 1 player should be eligible for level 1 trophy", lowLevelEligible1)
        println("Level 1 player eligible for level 1 trophy: $lowLevelEligible1")
        
        // Test high level trophy
        val highLevelEligible50 = trophySystem.checkEligibility(highLevelTrophy, playerLevel50, 0, 0, 0)
        assertFalse("Level 50 player should NOT be eligible for level 100 trophy", highLevelEligible50)
        println("Level 50 player eligible for level 100 trophy: $highLevelEligible50")
        
        val highLevelEligible100 = trophySystem.checkEligibility(highLevelTrophy, playerLevel100, 0, 0, 0)
        assertTrue("Level 100 player should be eligible for level 100 trophy", highLevelEligible100)
        println("Level 100 player eligible for level 100 trophy: $highLevelEligible100")
        
        // Test with ability prerequisite
        val abilityTrophy = Trophy(
            id = "trophy_3",
            name = "Ability Master",
            description = "Own 10 abilities",
            tier = Tier.Beginner,
            rarity = TrophyRarity.Rare,
            xpReward = 100,
            pointsReward = 20,
            prerequisites = TrophyPrerequisites(level = 1, abilities = 10),
            isMilestone = false
        )
        
        val abilityEligible0 = trophySystem.checkEligibility(abilityTrophy, 10, 0, 0, 0)
        assertFalse("Player with 0 abilities should NOT be eligible", abilityEligible0)
        println("Player with 0 abilities eligible: $abilityEligible0")
        
        val abilityEligible10 = trophySystem.checkEligibility(abilityTrophy, 10, 10, 0, 0)
        assertTrue("Player with 10 abilities should be eligible", abilityEligible10)
        println("Player with 10 abilities eligible: $abilityEligible10")
        
        println("✅ Test 7 PASSED\n")
    }
    
    /**
     * Test 8: Calculate total trophy stats
     * Verifies statistics calculation is correct
     */
    @Test
    fun testCalculateTrophyStats() {
        println("\n=== Test 8: Calculate Trophy Stats ===")
        
        val trophies = listOf(
            Trophy(
                id = "trophy_1",
                name = "Trophy 1",
                description = "Description",
                tier = Tier.Life,
                rarity = TrophyRarity.Common,
                xpReward = 25,
                pointsReward = 5,
                prerequisites = TrophyPrerequisites(level = 1),
                isMilestone = false
            ),
            Trophy(
                id = "trophy_2",
                name = "Trophy 2",
                description = "Description",
                tier = Tier.Life,
                rarity = TrophyRarity.Rare,
                xpReward = 100,
                pointsReward = 20,
                prerequisites = TrophyPrerequisites(level = 1),
                isMilestone = false
            ),
            Trophy(
                id = "trophy_3",
                name = "Trophy 3",
                description = "Description",
                tier = Tier.Life,
                rarity = TrophyRarity.Legendary,
                xpReward = 500,
                pointsReward = 100,
                prerequisites = TrophyPrerequisites(level = 1),
                isMilestone = true
            )
        )
        
        val unlockedTrophies = listOf("trophy_1", "trophy_3")
        
        val stats = trophySystem.calculateStats(trophies, unlockedTrophies)
        
        println("Total Trophies: ${stats.totalTrophies}")
        println("Unlocked Count: ${stats.unlockedTrophies.size}")
        println("Total XP Earned: ${stats.totalXPEarned}")
        println("Total Points Earned: ${stats.totalPointsEarned}")
        println("Completion Percentage: ${stats.completionPercentage}%")
        
        assertEquals("Total trophies should be 3", 3, stats.totalTrophies)
        assertEquals("Unlocked count should be 2", 2, stats.unlockedTrophies.size)
        assertEquals("Total XP should be 525", 525, stats.totalXPEarned)
        assertEquals("Total points should be 105", 105, stats.totalPointsEarned)
        assertEquals("Completion should be 67%", 67, stats.completionPercentage)
        
        println("✅ Test 8 PASSED\n")
    }
}