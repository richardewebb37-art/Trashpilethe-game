package com.trashapp.gcms.commands

sealed class GCMSCommand {
    abstract val playerId: String
}

// Card Commands
data class DrawCardCommand(
    override val playerId: String,
    val fromPile: PileType = PileType.DECK
) : GCMSCommand()

data class PlaceCardCommand(
    override val playerId: String,
    val card: String, // Card ID
    val slotIndex: Int
) : GCMSCommand()

data class FlipCardCommand(
    override val playerId: String,
    val slotIndex: Int
) : GCMSCommand()

data class DiscardCardCommand(
    override val playerId: String,
    val card: String
) : GCMSCommand()

// Game Flow Commands
data class EndTurnCommand(
    override val playerId: String
) : GCMSCommand()

data class StartRoundCommand(
    override val playerId: String,
    val roundNumber: Int
) : GCMSCommand()

data class EndRoundCommand(
    override val playerId: String
) : GCMSCommand()

data class StartMatchCommand(
    override val playerId: String,
    val playerNames: List<String>
) : GCMSCommand()

data class EndMatchCommand(
    override val playerId: String,
    val winnerId: String,
    val finalScore: Int
) : GCMSCommand()

// Skill Commands
data class UseSkillCommand(
    override val playerId: String,
    val skillId: String
) : GCMSCommand()

data class UseAbilityCommand(
    override val playerId: String,
    val abilityId: String
) : GCMSCommand()

// Settings Commands
data class PauseGameCommand(
    override val playerId: String
) : GCMSCommand()

data class ResumeGameCommand(
    override val playerId: String
) : GCMSCommand()

enum class PileType {
    DECK,
    DISCARD
}