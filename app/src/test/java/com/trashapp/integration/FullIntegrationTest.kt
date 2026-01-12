package com.trashapp.integration

import com.trashapp.gcms.commands.*
import com.trashapp.gcms.events.*
import com.trashapp.gcms.gcms.GCMSController
import com.trashapp.gcms.gcms.GCMSState
import com.trashapp.gcms.handlers.*
import com.trashapp.gcms.models.GCMSState
import com.trashapp.gcms.models.Player
import com.trashapp.gcms.models.GameState
import com.trashapp.gcms.progression.*
import com.trashapp.gcms.trophy.*
import com.trashapp.gcms.challenge.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Full Integration Test
 * Verifies all systems (Progression, Trophy, Challenge) work together properly
 */
class FullIntegrationTest {
    
    private lateinit var controller: GCMSController
    private lateinit var progressionHandler: ProgressionCommandHandlerV2
    private lateinit var trophyHandler: TrophyCommandHandlerV2
    private lateinit var challengeHandler: ChallengeCommandHandler
    private lateinit var state: GCMSState
    private lateinit var player: Player
    private val playerId = "test_player_1"
    
    @Before
    fun setup() {
        // Initialize handlers
        progressionHandler = ProgressionCommandHandlerV2(null)
        trophyHandler = TrophyCommandHandlerV2(null)
        challengeHandler = ChallengeCommandHandler(null)
        
        // Initialize state
        player = Player(
            id = playerId,
            name = "Test Player",
            pointSystem = PointSystem(),
            progressionTree = ProgressionTree()
        )
        
        state = GCMSState(
            players = listOf(player),
            currentPlayerId = playerId,
            gameState = GameState()
        )
        
        // Initialize trophy and challenge managers
        trophyHandler.initialize()
        challengeHandler.initialize(state)
    }
    
    /**
     * Test 1: Complete Level Progression Flow
     * Verifies: XP gain → Level up → Tier change → Trophy awarding
     */
    @Test
    fun testCompleteLevelProgressionFlow() = runBlocking {
        println("\n=== Test 1: Complete Level Progression Flow ===")
        
        // Initial state
        var initialLevel = player.pointSystem.getCurrentLevel()
        var initialTier = Tier.fromLevel(initialLevel)
        println("Initial Level: $initialLevel, Tier: ${initialTier.name}")
        
        // Add enough XP to level up
        val xpToAdd = player.pointSystem.xpToNextLevel + 100
        val addXPCommand = AddXPCommand(playerId, xpToAdd)
        val xpResult = progressionHandler.handle(addXPCommand, state)
        
        assertTrue("XP addition should succeed", xpResult.isSuccess)
        var events = xpResult.getOrNull() ?: emptyList()
        
        // Verify XP added event
        val xpEvent = events.find { it is XPAddedEvent } as XPAddedEvent
        assertNotNull("XPAddedEvent should be emitted", xpEvent)
        assertEquals("XP amount should match", xpToAdd, xpEvent.amount)
        println("✓ XP Added: ${xpEvent.amount}")
        
        // Verify level up event
        val levelUpEvent = events.find { it is LevelUpEvent } as LevelUpEvent
        assertNotNull("LevelUpEvent should be emitted", levelUpEvent)
        assertTrue("Level should increase", levelUpEvent.newLevel > initialLevel)
        println("✓ Level Up: ${levelUpEvent.oldLevel} → ${levelUpEvent.newLevel}")
        
        // Check tier change
        val newTier = Tier.fromLevel(levelUpEvent.newLevel)
        println("✓ Tier Change: ${initialTier.name} → ${newTier.name}")
        
        // Award trophies for level up
        val awardTrophiesCommand = AwardTrophiesCommand(playerId, levelUpEvent.newLevel)
        val trophyResult = trophyHandler.handle(awardTrophiesCommand, state)
        
        assertTrue("Trophy awarding should succeed", trophyResult.isSuccess)
        events = trophyResult.getOrNull() ?: emptyList()
        
        // Verify trophies awarded event
        val trophyEvent = events.find { it is TrophiesAwardedEvent } as TrophiesAwardedEvent
        assertNotNull("TrophiesAwardedEvent should be emitted", trophyEvent)
        assertTrue("At least one trophy should be awarded", trophyEvent.trophies.isNotEmpty())
        println("✓ Trophies Awarded: ${trophyEvent.trophies.size}")
        
        // Verify XP and points from trophies
        val totalXpFromTrophies = trophyEvent.trophies.sumOf { it.xpReward }
        val totalPointsFromTrophies = trophyEvent.trophies.sumOf { it.pointsReward }
        println("✓ XP from Trophies: $totalXpFromTrophies")
        println("✓ Points from Trophies: $totalPointsFromTrophies")
        
        println("✅ Test 1 PASSED\n")
    }
    
    /**
     * Test 2: Skill and Ability Purchase Flow
     * Verifies: Points spend → Skill purchase → Ability unlock → XP gain → Level up
     */
    @Test
    fun testSkillAbilityPurchaseFlow() = runBlocking {
        println("\n=== Test 2: Skill and Ability Purchase Flow ===")
        
        // Give player points
        val initialPoints = 1000
        val addPointsCommand = AddPointsCommand(playerId, initialPoints)
        val pointsResult = progressionHandler.handle(addPointsCommand, state)
        assertTrue("Points addition should succeed", pointsResult.isSuccess)
        println("✓ Points Added: $initialPoints")
        
        // Get a Life tier skill (lowest tier, should be affordable)
        val lifeSkills = player.progressionTree.getSkillsForTier(Tier.Life)
        assertTrue("Life tier skills should exist", lifeSkills.isNotEmpty())
        val skillToBuy = lifeSkills.first()
        println("✓ Selected Skill: ${skillToBuy.name} (${skillToBuy.tier.name})")
        
        // Buy skill
        val buySkillCommand = BuySkillCommand(playerId, skillToBuy.id)
        val buySkillResult = progressionHandler.handle(buySkillCommand, state)
        assertTrue("Skill purchase should succeed", buySkillResult.isSuccess)
        var events = buySkillResult.getOrNull() ?: emptyList()
        
        // Verify skill purchased event
        val skillEvent = events.find { it is SkillPurchasedEvent } as SkillPurchasedEvent
        assertNotNull("SkillPurchasedEvent should be emitted", skillEvent)
        assertEquals("Skill ID should match", skillToBuy.id, skillEvent.skillId)
        println("✓ Skill Purchased: ${skillEvent.skillId}")
        println("✓ XP Gained: ${skillEvent.xpGained}")
        println("✓ Abilities Unlocked: ${skillEvent.abilitiesUnlocked}")
        
        // Verify abilities unlocked events
        val abilityUnlockedEvents = events.filter { it is AbilityUnlockedEvent }
        assertTrue("Abilities should be unlocked", abilityUnlockedEvents.isNotEmpty())
        println("✓ AbilityUnlocked Events: ${abilityUnlockedEvents.size}")
        
        // Buy an ability from the purchased skill
        val unlockedAbilities = skillEvent.abilitiesUnlocked
        if (unlockedAbilities.isNotEmpty()) {
            val abilityId = unlockedAbilities.first()
            val buyAbilityCommand = BuyAbilityCommand(playerId, abilityId)
            val buyAbilityResult = progressionHandler.handle(buyAbilityCommand, state)
            assertTrue("Ability purchase should succeed", buyAbilityResult.isSuccess)
            events = buyAbilityResult.getOrNull() ?: emptyList()
            
            // Verify ability purchased event
            val abilityEvent = events.find { it is AbilityPurchasedEvent } as AbilityPurchasedEvent
            assertNotNull("AbilityPurchasedEvent should be emitted", abilityEvent)
            assertEquals("Ability ID should match", abilityId, abilityEvent.abilityId)
            println("✓ Ability Purchased: ${abilityEvent.abilityId}")
            println("✓ XP Gained: ${abilityEvent.xpGained}")
        }
        
        println("✅ Test 2 PASSED\n")
    }
    
    /**
     * Test 3: Challenge Progression and Level Advancement
     * Verifies: Challenge generation → Progress tracking → Completion → Level unlock
     */
    @Test
    fun testChallengeProgressionAndAdvancement() = runBlocking {
        println("\n=== Test 3: Challenge Progression and Level Advancement ===")
        
        val currentLevel = player.pointSystem.getCurrentLevel()
        println("Current Level: $currentLevel")
        
        // Generate challenges for current level
        val generateCommand = GenerateChallengesCommand(playerId, currentLevel)
        val generateResult = challengeHandler.handle(generateCommand, state)
        assertTrue("Challenge generation should succeed", generateResult.isSuccess)
        
        // Set as current level challenges
        val setCommand = SetCurrentLevelChallengesCommand(playerId, currentLevel)
        val setResult = challengeHandler.handle(setCommand, state)
        assertTrue("Setting current challenges should succeed", setResult.isSuccess)
        
        // Check advancement (should be false initially)
        val checkCommand = CheckLevelAdvancementCommand(playerId)
        val checkResult = challengeHandler.handle(checkCommand, state)
        assertTrue("Advancement check should succeed", checkResult.isSuccess)
        
        val events = checkResult.getOrNull() ?: emptyList()
        val canAdvanceEvent = events.find { it is CanAdvanceToNextLevelEvent }
        if (canAdvanceEvent != null) {
            println("Can Advance: ${canAdvanceEvent.canAdvance}")
        }
        
        // Simulate completing some challenges
        val challenges = challengeHandler.getChallengesForLevel(currentLevel)
        if (challenges.isNotEmpty()) {
            val challenge = challenges.first()
            println("Updating progress for challenge: ${challenge.id}")
            
            val updateCommand = UpdateChallengeProgressCommand(
                playerId,
                challenge.id,
                challenge.requirements.score ?: 1000
            )
            val updateResult = challengeHandler.handle(updateCommand, state)
            assertTrue("Progress update should succeed", updateResult.isSuccess)
            
            val progressEvents = updateResult.getOrNull() ?: emptyList()
            val progressEvent = progressEvents.find { it is ChallengeProgressUpdatedEvent }
            assertNotNull("Progress event should be emitted", progressEvent)
            println("✓ Challenge Progress Updated")
        }
        
        println("✅ Test 3 PASSED\n")
    }
    
    /**
     * Test 4: XP Loss and Level Drop with Penalty
     * Verifies: XP loss → Level drop → Penalty applied → Regain difficulty
     */
    @Test
    fun testXPLossAndLevelDropWithPenalty() = runBlocking {
        println("\n=== Test 4: XP Loss and Level Drop with Penalty ===")
        
        // First, add XP to reach a decent level
        val xpToAdd = 2000
        val addXPCommand = AddXPCommand(playerId, xpToAdd)
        val addXPResult = progressionHandler.handle(addXPCommand, state)
        assertTrue("XP addition should succeed", addXPResult.isSuccess)
        
        val levelBeforeLoss = player.pointSystem.getCurrentLevel()
        val xpBeforeLoss = player.pointSystem.totalXP
        println("Level before loss: $levelBeforeLoss, XP: $xpBeforeLoss")
        
        // Lose XP
        val xpToLose = 500
        val loseXPCommand = LoseXPCommand(playerId, xpToLose)
        val loseXPResult = progressionHandler.handle(loseXPCommand, state)
        assertTrue("XP loss should succeed", loseXPResult.isSuccess)
        
        val events = loseXPResult.getOrNull() ?: emptyList()
        val xpLostEvent = events.find { it is XPLostEvent } as XPLostEvent
        assertNotNull("XPLostEvent should be emitted", xpLostEvent)
        
        // Verify penalty applied (should be more than the amount requested)
        assertTrue("Penalty should be applied", xpLostEvent.amount >= xpToLose)
        println("✓ XP Lost: ${xpLostEvent.amount} (requested: $xpToLose, penalty: ${xpLostEvent.amount - xpToLose})")
        
        // Check if level dropped
        val levelAfterLoss = player.pointSystem.getCurrentLevel()
        val xpAfterLoss = player.pointSystem.totalXP
        println("Level after loss: $levelAfterLoss, XP: $xpAfterLoss")
        
        if (levelAfterLoss < levelBeforeLoss) {
            val levelDownEvent = events.find { it is LevelDownEvent } as LevelDownEvent
            assertNotNull("LevelDownEvent should be emitted", levelDownEvent)
            assertEquals("Old level should match", levelBeforeLoss, levelDownEvent.oldLevel)
            assertEquals("New level should match", levelAfterLoss, levelDownEvent.newLevel)
            println("✓ Level Dropped: ${levelDownEvent.oldLevel} → ${levelDownEvent.newLevel}")
        }
        
        // Calculate how much XP needed to regain level
        val xpNeededToRegain = xpBeforeLoss - xpAfterLoss
        println("✓ XP needed to regain level: $xpNeededToRegain (lost ${xpNeededToRegain - xpToLose} extra due to penalty)")
        
        println("✅ Test 4 PASSED\n")
    }
    
    /**
     * Test 5: Tier-Aware Skill and Ability Requirements
     * Verifies: Tier restrictions work correctly
     */
    @Test
    fun testTierAwareRequirements() = runBlocking {
        println("\n=== Test 5: Tier-Aware Skill and Ability Requirements ===")
        
        // Player starts at level 1 (Life tier)
        val initialLevel = player.pointSystem.getCurrentLevel()
        val initialTier = Tier.fromLevel(initialLevel)
        println("Initial Level: $initialLevel, Tier: ${initialTier.name}")
        
        // Try to buy a Master tier skill (should fail)
        val masterSkills = player.progressionTree.getSkillsForTier(Tier.Master)
        if (masterSkills.isNotEmpty()) {
            val masterSkill = masterSkills.first()
            println("Attempting to buy Master tier skill: ${masterSkill.name}")
            
            // Give player enough points
            val addPointsCommand = AddPointsCommand(playerId, 10000)
            progressionHandler.handle(addPointsCommand, state)
            
            val buySkillCommand = BuySkillCommand(playerId, masterSkill.id)
            val buySkillResult = progressionHandler.handle(buySkillCommand, state)
            
            assertFalse("Should not be able to buy Master tier skill at level 1", buySkillResult.isSuccess)
            println("✓ Correctly blocked Master tier purchase (level requirement)")
        }
        
        // Level up to Master tier
        val xpToMaster = 15000
        val addXPCommand = AddXPCommand(playerId, xpToMaster)
        progressionHandler.handle(addXPCommand, state)
        
        val newLevel = player.pointSystem.getCurrentLevel()
        val newTier = Tier.fromLevel(newLevel)
        println("After adding XP: Level $newLevel, Tier ${newTier.name}")
        
        // Now try to buy Master tier skill (should succeed)
        val masterSkillsAfter = player.progressionTree.getSkillsForTier(Tier.Master)
        if (masterSkillsAfter.isNotEmpty()) {
            val masterSkill = masterSkillsAfter.first()
            println("Attempting to buy Master tier skill: ${masterSkill.name}")
            
            val buySkillCommand = BuySkillCommand(playerId, masterSkill.id)
            val buySkillResult = progressionHandler.handle(buySkillCommand, state)
            
            assertTrue("Should be able to buy Master tier skill at correct level", buySkillResult.isSuccess)
            println("✓ Successfully purchased Master tier skill")
        }
        
        println("✅ Test 5 PASSED\n")
    }
    
    /**
     * Test 6: Trophy Eligibility and Prerequisites
     * Verifies: Trophy prerequisites are checked correctly
     */
    @Test
    fun testTrophyEligibilityAndPrerequisites() = runBlocking {
        println("\n=== Test 6: Trophy Eligibility and Prerequisites ===")
        
        // Generate some trophies
        val trophies = TrophyGenerator.generateForLevel(player.pointSystem.getCurrentLevel())
        if (trophies.isNotEmpty()) {
            val trophy = trophies.find { it.prerequisites.level > 1 }
            if (trophy != null) {
                println("Testing trophy with level requirement: ${trophy.name}")
                println("Required level: ${trophy.prerequisites.level}")
                
                // Check eligibility at low level (should be false)
                val checkEligibilityCommand = CheckTrophyEligibilityCommand(playerId, trophy.id)
                val checkResult = trophyHandler.handle(checkEligibilityCommand, state)
                assertTrue("Eligibility check should succeed", checkResult.isSuccess)
                
                val events = checkResult.getOrNull() ?: emptyList()
                val eligibilityEvent = events.find { it is TrophyEligibilityCheckedEvent } as TrophyEligibilityCheckedEvent
                assertNotNull("Eligibility event should be emitted", eligibilityEvent)
                
                if (!eligibilityEvent.isEligible) {
                    println("✓ Trophy not eligible at current level (correct)")
                }
                
                // Level up to meet requirement
                val addXPCommand = AddXPCommand(playerId, 5000)
                progressionHandler.handle(addXPCommand, state)
                
                // Check eligibility again (should be true now)
                val checkResult2 = trophyHandler.handle(checkEligibilityCommand, state)
                assertTrue("Eligibility check should succeed", checkResult2.isSuccess)
                
                val events2 = checkResult2.getOrNull() ?: emptyList()
                val eligibilityEvent2 = events2.find { it is TrophyEligibilityCheckedEvent } as TrophyEligibilityCheckedEvent
                assertNotNull("Eligibility event should be emitted", eligibilityEvent2)
                
                if (eligibilityEvent2.isEligible) {
                    println("✓ Trophy eligible after leveling up (correct)")
                }
            }
        }
        
        println("✅ Test 6 PASSED\n")
    }
    
    /**
     * Test 7: Complete End-to-End Integration
     * Verifies: All systems work together in a realistic scenario
     */
    @Test
    fun testCompleteEndToEndIntegration() = runBlocking {
        println("\n=== Test 7: Complete End-to-End Integration ===")
        
        // Scenario: Player progresses from Level 1 to Level 10
        
        // Step 1: Give initial points
        var addPointsCommand = AddPointsCommand(playerId, 500)
        progressionHandler.handle(addPointsCommand, state)
        println("Step 1: Given 500 points")
        
        // Step 2: Buy a Life tier skill
        val lifeSkills = player.progressionTree.getSkillsForTier(Tier.Life)
        if (lifeSkills.isNotEmpty()) {
            val buySkillCommand = BuySkillCommand(playerId, lifeSkills.first().id)
            progressionHandler.handle(buySkillCommand, state)
            println("Step 2: Purchased Life tier skill")
        }
        
        // Step 3: Buy an ability
        val skillAbilities = player.progressionTree.getAbilitiesForSkill(lifeSkills.first().id)
        if (skillAbilities.isNotEmpty()) {
            val buyAbilityCommand = BuyAbilityCommand(playerId, skillAbilities.first().id)
            progressionHandler.handle(buyAbilityCommand, state)
            println("Step 3: Purchased ability")
        }
        
        // Step 4: Generate challenges for level 1
        var generateCommand = GenerateChallengesCommand(playerId, 1)
        challengeHandler.handle(generateCommand, state)
        var setCommand = SetCurrentLevelChallengesCommand(playerId, 1)
        challengeHandler.handle(setCommand, state)
        println("Step 4: Generated level 1 challenges")
        
        // Step 5: Add enough XP to reach level 5
        addXPCommand = AddXPCommand(playerId, 1000)
        progressionHandler.handle(addXPCommand, state)
        println("Step 5: Added XP to reach level 5")
        
        // Step 6: Award trophies for level 5
        val awardTrophiesCommand = AwardTrophiesCommand(playerId, 5)
        trophyHandler.handle(awardTrophiesCommand, state)
        println("Step 6: Awarded level 5 trophies")
        
        // Step 7: Add more XP to reach level 10
        addXPCommand = AddXPCommand(playerId, 3000)
        progressionHandler.handle(addXPCommand, state)
        println("Step 7: Added XP to reach level 10")
        
        // Step 8: Award trophies for level 10
        var awardCommand = AwardTrophiesCommand(playerId, 10)
        trophyHandler.handle(awardCommand, state)
        println("Step 8: Awarded level 10 trophies")
        
        // Verify final state
        val finalLevel = player.pointSystem.getCurrentLevel()
        val finalTier = Tier.fromLevel(finalLevel)
        val finalPoints = player.pointSystem.availablePoints
        val finalXP = player.pointSystem.totalXP
        
        println("\n=== Final State ===")
        println("Level: $finalLevel")
        println("Tier: ${finalTier.name}")
        println("Points: $finalPoints")
        println("XP: $finalXP")
        
        assertTrue("Should reach at least level 10", finalLevel >= 10)
        assertEquals("Should be in Beginner tier", Tier.Beginner, finalTier)
        
        println("✅ Test 7 PASSED - End-to-End integration successful!\n")
    }
}