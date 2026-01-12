package com.trashapp.gcms.handlers

import com.trashapp.audio.AudioAssetManager
import com.trashapp.gcms.commands.*
import com.trashapp.gcms.events.*
import com.trashapp.gcms.models.GCMSState
import com.trashapp.gcms.challenge.*
import kotlinx.coroutines.flow.first

/**
 * Handles all challenge-related commands
 * 
 * This handler manages:
 * - Challenge generation for levels
 * - Challenge progress tracking
 * - Level advancement gating
 * - Game action tracking
 */
class ChallengeCommandHandler(
    private val audioManager: AudioAssetManager? = null
) : CommandHandler {
    
    private val challengeManager = ChallengeManager()
    private var isInitialized = false
    
    override fun canHandle(command: GCMSCommand): Boolean {
        return command is GenerateChallengesCommand ||
               command is UpdateChallengeProgressCommand ||
               command is CompleteChallengeCommand ||
               command is CheckLevelAdvancementCommand ||
               command is SetCurrentLevelChallengesCommand ||
               command is GetChallengesForLevelCommand ||
               command is GetChallengeProgressCommand ||
               command is ResetChallengesCommand ||
               command is TrackGameActionCommand
    }
    
    override suspend fun handle(command: GCMSCommand, state: GCMSState): Result<List<GCMSEvent>> {
        initialize(state)
        
        return when (command) {
            is GenerateChallengesCommand -> handleGenerateChallenges(command, state)
            is UpdateChallengeProgressCommand -> handleUpdateProgress(command, state)
            is CompleteChallengeCommand -> handleCompleteChallenge(command, state)
            is CheckLevelAdvancementCommand -> handleCheckAdvancement(command, state)
            is SetCurrentLevelChallengesCommand -> handleSetCurrentLevel(command, state)
            is GetChallengesForLevelCommand -> handleGetChallenges(command, state)
            is GetChallengeProgressCommand -> handleGetProgress(command, state)
            is ResetChallengesCommand -> handleReset(state)
            is TrackGameActionCommand -> handleTrackGameAction(command, state)
            else -> Result.failure(IllegalArgumentException("Unknown challenge command"))
        }
    }
    
    /**
     * Initialize challenge manager
     */
    private suspend fun initialize(state: GCMSState) {
        if (!isInitialized) {
            val player = state.players.firstOrNull()
            val startingLevel = player?.progressionTree?.getCurrentLevel() ?: 1
            challengeManager.initialize(startingLevel)
            isInitialized = true
        }
    }
    
    /**
     * Generate challenges for a level
     */
    private suspend fun handleGenerateChallenges(
        command: GenerateChallengesCommand,
        state: GCMSState
    ): Result<List<GCMSEvent>> {
        val challenges = challengeManager.generateChallengesForLevel(command.level)
        
        val events = listOf(
            ChallengesGeneratedEvent(
                level = command.level,
                challengeCount = challenges.size
            )
        )
        
        return Result.success(events)
    }
    
    /**
     * Update challenge progress
     */
    private suspend fun handleUpdateProgress(
        command: UpdateChallengeProgressCommand,
        state: GCMSState
    ): Result<List<GCMSEvent>> {
        val updated = challengeManager.updateChallengeProgress(
            challengeId = command.challengeId,
            progress = command.progress
        )
        
        if (!updated) {
            return Result.failure(IllegalStateException("Challenge not found or already completed"))
        }
        
        // Get the updated challenge
        val challenges = challengeManager.challenges.value.values.flatten()
        val challenge = challenges.find { it.id == command.challengeId }
        
        val events = mutableListOf<GCMSEvent>()
        
        challenge?.let {
            events.add(ChallengeProgressUpdatedEvent(
                challengeId = it.id,
                challengeName = it.name,
                progress = it.progress,
                maxProgress = it.maxProgress,
                percentage = it.getProgressPercentage()
            ))
            
            // Check if challenge was just completed
            if (it.isCompleted) {
                events.add(ChallengeCompletedEvent(
                    challenge = it,
                    xpEarned = it.reward.getTotalXP(),
                    pointsEarned = it.reward.getTotalPoints(),
                    achievement = it.reward.achievement
                ))
                
                // Play completion sound
                audioManager?.playSound(AudioAssetManager.SOUND_WIN)
                
                // Check if all challenges for level are completed
                val levelProgress = challengeManager.getProgressForLevel(it.level)
                levelProgress?.let { progress ->
                    if (progress.isComplete) {
                        events.add(LevelChallengesCompletedEvent(
                            level = it.level,
                            totalChallenges = progress.totalChallenges,
                            totalXPEarned = progress.totalXPEarned,
                            totalPointsEarned = progress.totalPointsEarned
                        ))
                    }
                }
            }
        }
        
        return Result.success(events)
    }
    
    /**
     * Complete a challenge
     */
    private suspend fun handleCompleteChallenge(
        command: CompleteChallengeCommand,
        state: GCMSState
    ): Result<List<GCMSEvent>> {
        val completed = challengeManager.completeChallenge(command.challengeId)
        
        if (!completed) {
            return Result.failure(IllegalStateException("Challenge not found or already completed"))
        }
        
        val challenges = challengeManager.challenges.value.values.flatten()
        val challenge = challenges.find { it.id == command.challengeId }
        
        val events = mutableListOf<GCMSEvent>()
        
        challenge?.let {
            events.add(ChallengeCompletedEvent(
                challenge = it,
                xpEarned = it.reward.getTotalXP(),
                pointsEarned = it.reward.getTotalPoints(),
                achievement = it.reward.achievement
            ))
            
            // Play completion sound
            audioManager?.playSound(AudioAssetManager.SOUND_WIN)
            
            // Check if all challenges for level are completed
            val levelProgress = challengeManager.getProgressForLevel(it.level)
            levelProgress?.let { progress ->
                if (progress.isComplete) {
                    events.add(LevelChallengesCompletedEvent(
                        level = it.level,
                        totalChallenges = progress.totalChallenges,
                        totalXPEarned = progress.totalXPEarned,
                        totalPointsEarned = progress.totalPointsEarned
                    ))
                }
            }
        }
        
        return Result.success(events)
    }
    
    /**
     * Check if player can advance to next level
     */
    private suspend fun handleCheckAdvancement(
        command: CheckLevelAdvancementCommand,
        state: GCMSState
    ): Result<List<GCMSEvent>> {
        val canAdvance = challengeManager.canAdvanceToNextLevel(
            currentLevel = command.currentLevel,
            hasRequiredXP = command.hasRequiredXP,
            hasRequiredPoints = command.hasRequiredPoints,
            hasRequiredAbilities = command.hasRequiredAbilities,
            hasRequiredSkills = command.hasRequiredSkills
        )
        
        val events = mutableListOf<GCMSEvent>()
        
        if (canAdvance) {
            events.add(CanAdvanceToNextLevelEvent(
                currentLevel = command.currentLevel,
                nextLevel = command.currentLevel + 1,
                requirementsMet = true
            ))
        } else {
            val missingRequirements = mutableListOf<String>()
            
            if (!command.hasRequiredXP) missingRequirements.add("XP")
            if (!command.hasRequiredPoints) missingRequirements.add("Points")
            if (!command.hasRequiredAbilities) missingRequirements.add("Abilities")
            if (!command.hasRequiredSkills) missingRequirements.add("Skills")
            
            // Check challenges
            val challenges = challengeManager.getChallengesForLevel(command.currentLevel)
            val incompleteChallenges = challenges.filter { !it.isCompleted }
            if (incompleteChallenges.isNotEmpty()) {
                missingRequirements.add("Challenges (${incompleteChallenges.size} remaining)")
            }
            
            events.add(LevelAdvancementBlockedEvent(
                currentLevel = command.currentLevel,
                missingRequirements = missingRequirements
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Set current level challenges
     */
    private suspend fun handleSetCurrentLevel(
        command: SetCurrentLevelChallengesCommand,
        state: GCMSState
    ): Result<List<GCMSEvent>> {
        challengeManager.setCurrentLevelChallenges(command.level)
        
        val challenges = challengeManager.getChallengesForLevel(command.level)
        
        return Result.success(listOf(
            ChallengesGeneratedEvent(
                level = command.level,
                challengeCount = challenges.size
            )
        ))
    }
    
    /**
     * Get challenges for a level
     */
    private suspend fun handleGetChallenges(
        command: GetChallengesForLevelCommand,
        state: GCMSState
    ): Result<List<GCMSEvent>> {
        val challenges = challengeManager.getChallengesForLevel(command.level)
        // Results can be accessed via ChallengeManager's StateFlow
        
        return Result.success(emptyList())
    }
    
    /**
     * Get challenge progress for a level
     */
    private suspend fun handleGetProgress(
        command: GetChallengeProgressCommand,
        state: GCMSState
    ): Result<List<GCMSEvent>> {
        val progress = challengeManager.getProgressForLevel(command.level)
        
        val events = mutableListOf<GCMSEvent>()
        
        progress?.let {
            events.add(ChallengeProgressSummaryEvent(
                level = command.level,
                progress = it
            ))
        }
        
        return Result.success(events)
    }
    
    /**
     * Reset challenge system
     */
    private suspend fun handleReset(state: GCMSState): Result<List<GCMSEvent>> {
        challengeManager.reset()
        isInitialized = false
        
        return Result.success(emptyList())
    }
    
    /**
     * Track game action for challenge progress
     */
    private suspend fun handleTrackGameAction(
        command: TrackGameActionCommand,
        state: GCMSState
    ): Result<List<GCMSEvent>> {
        val player = state.players.find { it.id == command.playerId }
            ?: return Result.failure(IllegalStateException("Player not found"))
        
        val currentLevel = player.progressionTree.getCurrentLevel()
        val challenges = challengeManager.getChallengesForLevel(currentLevel)
        
        val affectedChallenges = mutableListOf<String>()
        val progressUpdates = mutableMapOf<String, Int>()
        
        // Process each challenge to see if it's affected by this action
        challenges.forEach { challenge ->
            if (!challenge.isCompleted) {
                val progressIncrease = calculateProgressIncrease(
                    challenge,
                    command.actionType,
                    command.actionData
                )
                
                if (progressIncrease > 0) {
                    affectedChallenges.add(challenge.id)
                    progressUpdates[challenge.id] = progressIncrease
                    
                    // Update the challenge progress
                    challengeManager.updateChallengeProgress(challenge.id, progressIncrease)
                }
            }
        }
        
        val events = mutableListOf<GCMSEvent>()
        
        if (affectedChallenges.isNotEmpty()) {
            events.add(GameActionTrackedEvent(
                actionType = command.actionType,
                affectedChallenges = affectedChallenges,
                progressUpdates = progressUpdates
            ))
            
            // Emit progress update events for each affected challenge
            val updatedChallenges = challengeManager.getChallengesForLevel(currentLevel)
            affectedChallenges.forEach { challengeId ->
                val challenge = updatedChallenges.find { it.id == challengeId }
                challenge?.let {
                    events.add(ChallengeProgressUpdatedEvent(
                        challengeId = it.id,
                        challengeName = it.name,
                        progress = it.progress,
                        maxProgress = it.maxProgress,
                        percentage = it.getProgressPercentage()
                    ))
                    
                    // Check if challenge was just completed
                    if (it.isCompleted) {
                        events.add(ChallengeCompletedEvent(
                            challenge = it,
                            xpEarned = it.reward.getTotalXP(),
                            pointsEarned = it.reward.getTotalPoints(),
                            achievement = it.reward.achievement
                        ))
                        
                        audioManager?.playSound(AudioAssetManager.SOUND_WIN)
                    }
                }
            }
            
            // Check if all challenges are completed
            val progress = challengeManager.getProgressForLevel(currentLevel)
            progress?.let {
                if (it.isComplete) {
                    events.add(LevelChallengesCompletedEvent(
                        level = currentLevel,
                        totalChallenges = it.totalChallenges,
                        totalXPEarned = it.totalXPEarned,
                        totalPointsEarned = it.totalPointsEarned
                    ))
                }
            }
        }
        
        return Result.success(events)
    }
    
    /**
     * Calculate progress increase for a challenge based on game action
     */
    private fun calculateProgressIncrease(
        challenge: Challenge,
        actionType: String,
        actionData: Map<String, Any>
    ): Int {
        return when (challenge.type) {
            ChallengeType.SCORE -> {
                if (actionType == "score") {
                    val score = actionData["score"] as? Int ?: 0
                    val currentProgress = challenge.progress
                    val target = challenge.maxProgress
                    val newProgress = (currentProgress + score).coerceAtMost(target)
                    newProgress - currentProgress
                } else {
                    0
                }
            }
            ChallengeType.ABILITY_USE -> {
                if (actionType == "ability_use") {
                    val abilityId = actionData["abilityId"] as? String ?: ""
                    if (abilityId in challenge.requirements.abilitiesUsed) {
                        1
                    } else {
                        0
                    }
                } else {
                    0
                }
            }
            ChallengeType.SKILL_UNLOCK -> {
                if (actionType == "skill_unlock") {
                    val skillId = actionData["skillId"] as? String ?: ""
                    if (skillId in challenge.requirements.skillsUnlocked && 
                        challenge.progress < challenge.maxProgress) {
                        1
                    } else {
                        0
                    }
                } else {
                    0
                }
            }
            ChallengeType.POINT_ACCUMULATION -> {
                if (actionType == "points") {
                    val points = actionData["points"] as? Int ?: 0
                    val currentProgress = challenge.progress
                    val target = challenge.maxProgress
                    val newProgress = (currentProgress + points).coerceAtMost(target)
                    newProgress - currentProgress
                } else {
                    0
                }
            }
            ChallengeType.CARD_PLAYED -> {
                if (actionType == "card_played") {
                    1
                } else {
                    0
                }
            }
            ChallengeType.ROUND_WIN -> {
                if (actionType == "round_win") {
                    1
                } else {
                    0
                }
            }
            ChallengeType.MATCH_WIN -> {
                if (actionType == "match_win") {
                    1
                } else {
                    0
                }
            }
            ChallengeType.WIN_STREAK -> {
                if (actionType == "win_streak") {
                    val streak = actionData["streak"] as? Int ?: 0
                    if (streak >= challenge.requirements.winStreak && !challenge.isCompleted) {
                        challenge.maxProgress // Complete the challenge
                    } else {
                        0
                    }
                } else {
                    0
                }
            }
            ChallengeType.COMBO -> {
                if (actionType == "combo") {
                    val combo = actionData["combo"] as? List<String> ?: emptyList()
                    val requiredCombos = challenge.requirements.comboRequired?.abilityCombos ?: emptyList()
                    if (combo in requiredCombos && !challenge.isCompleted) {
                        1
                    } else {
                        0
                    }
                } else {
                    0
                }
            }
            ChallengeType.TIME_LIMIT -> {
                if (actionType == "score") {
                    val score = actionData["score"] as? Int ?: 0
                    val timeUsed = actionData["timeUsed"] as? Int ?: 0
                    if (timeUsed <= challenge.requirements.timeLimit) {
                        val currentProgress = challenge.progress
                        val target = challenge.maxProgress
                        val newProgress = (currentProgress + score).coerceAtMost(target)
                        newProgress - currentProgress
                    } else {
                        0
                    }
                } else {
                    0
                }
            }
        }
    }
}