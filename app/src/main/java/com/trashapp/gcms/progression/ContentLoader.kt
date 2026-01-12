package com.trashapp.gcms.progression

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Loads skills and abilities from JSON files
 * This replaces the placeholder content with generated content
 */
object ContentLoader {
    
    private val json = Json { ignoreUnknownKeys = true }
    private val assetsDir = File("app/src/main/assets/progression")
    
    /**
     * Load all skills from JSON file
     */
    fun loadSkills(): List<Skill> {
        val file = File(assetsDir, "skills.json")
        
        if (!file.exists()) {
            println("⚠️  skills.json not found, generating content...")
            GenerateContent.generateAll()
        }
        
        val skillsData = json.decodeFromString<List<SkillData>>(file.readText())
        return skillsData.map { data ->
            Skill(
                id = data.id,
                name = data.name,
                description = data.description,
                tier = Tier.valueOf(data.tier),
                category = SkillCategory.valueOf(data.category),
                rarity = Rarity.valueOf(data.rarity),
                baseCost = data.baseCost,
                xpGranted = data.xpGranted,
                maxLevel = data.maxLevel,
                currentLevel = 0,
                prerequisites = emptyList(),
                unlockedAbilities = data.abilities,
                abilityProgress = mutableMapOf()
            )
        }
    }
    
    /**
     * Load all abilities from JSON file
     */
    fun loadAbilities(): List<Ability> {
        val file = File(assetsDir, "abilities.json")
        
        if (!file.exists()) {
            println("⚠️  abilities.json not found, generating content...")
            GenerateContent.generateAll()
        }
        
        val abilitiesData = json.decodeFromString<List<AbilityData>>(file.readText())
        return abilitiesData.map { data ->
            Ability(
                id = data.id,
                name = data.name,
                description = data.description,
                tier = Tier.valueOf(data.tier),
                category = AbilityCategory.valueOf(data.category),
                rarity = Rarity.valueOf(data.rarity),
                baseCost = data.baseCost,
                xpGranted = data.xpGranted,
                maxRank = data.maxRank,
                currentRank = 0,
                prerequisites = emptyList(),
                skillId = data.skillId
            )
        }
    }
    
    /**
     * Initialize progression tree with loaded content
     */
    fun initializeProgressionTree(): ProgressionTree {
        val skills = loadSkills()
        val abilities = loadAbilities()
        
        return ProgressionTree().apply {
            // Add all skills
            skills.forEach { skill ->
                addSkill(skill)
            }
            
            // Add all abilities
            abilities.forEach { ability ->
                addAbility(ability)
            }
            
            println("✅ Loaded ${skills.size} skills and ${abilities.size} abilities")
        }
    }
    
    @Serializable
    data class SkillData(
        val id: String,
        val name: String,
        val description: String,
        val tier: String,
        val category: String,
        val rarity: String,
        val baseCost: Int,
        val xpGranted: Int,
        val maxLevel: Int,
        val abilities: List<String>
    )
    
    @Serializable
    data class AbilityData(
        val id: String,
        val name: String,
        val description: String,
        val tier: String,
        val category: String,
        val rarity: String,
        val baseCost: Int,
        val xpGranted: Int,
        val maxRank: Int,
        val skillId: String
    )
}