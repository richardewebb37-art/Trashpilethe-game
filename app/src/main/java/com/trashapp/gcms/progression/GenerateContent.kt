package com.trashapp.gcms.progression

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Generates and saves all skills and abilities for the TRASH game
 * This replaces placeholder content with fully generated tiered content
 */
object GenerateContent {
    
    private val json = Json { prettyPrint = true }
    private val outputDir = File("app/src/main/assets/progression")
    
    fun generateAll() {
        println("🎮 Starting content generation for TRASH game...")
        
        // Ensure output directory exists
        outputDir.mkdirs()
        
        // Generate skills and abilities for all tiers
        val contentGenerator = ContentGenerator()
        
        // Get all skills from all tiers
        val allSkills = mutableListOf<Skill>()
        for (tier in Tier.values()) {
            val skills = contentGenerator.generateSkillsForTier(tier)
            allSkills.addAll(skills)
        }
        
        // Generate abilities for each skill
        val allAbilities = mutableListOf<Ability>()
        allSkills.forEach { skill ->
            val abilities = contentGenerator.generateAbilitiesForSkill(skill)
            allAbilities.addAll(abilities)
        }
        
        // Save to JSON files
        saveSkills(allSkills)
        saveAbilities(allAbilities)
        
        // Generate statistics
        printStatistics(allSkills, allAbilities)
        
        println("✅ Content generation complete!")
        println("📁 Files saved to: ${outputDir.absolutePath}")
    }
    
    private fun saveSkills(skills: List<Skill>) {
        val file = File(outputDir, "skills.json")
        file.writeText(json.encodeToString(skills))
        println("💾 Saved ${skills.size} skills to skills.json")
    }
    
    private fun saveAbilities(abilities: List<Ability>) {
        val file = File(outputDir, "abilities.json")
        file.writeText(json.encodeToString(abilities))
        println("💾 Saved ${abilities.size} abilities to abilities.json")
    }
    
    private fun printStatistics(skills: List<Skill>, abilities: List<Ability>) {
        println("\n📊 Content Statistics:")
        println("━" * 50)
        
        // Stats by tier
        Tier.values().forEach { tier ->
            val tierSkills = skills.filter { it.tier == tier }
            val tierAbilities = abilities.filter { it.tier == tier }
            
            println("\n🏷️  Tier: ${tier.name} (Levels ${tier.minLevel}-${tier.maxLevel})")
            println("   Skills: ${tierSkills.size}")
            println("   Abilities: ${tierAbilities.size}")
            println("   Avg Abilities per Skill: ${tierAbilities.size.toFloat() / tierSkills.size}")
            
            // Rarity distribution
            println("   Rarity Distribution:")
            Rarity.values().forEach { rarity ->
                val count = tierAbilities.count { it.rarity == rarity }
                println("     ${rarity.name}: $count")
            }
        }
        
        // Total stats
        println("\n📈 Total Statistics:")
        println("   Total Skills: ${skills.size}")
        println("   Total Abilities: ${abilities.size}")
        println("   Avg Abilities per Skill: ${abilities.size.toFloat() / skills.size}")
        
        // Cost ranges
        val costs = abilities.map { it.baseCost }
        println("\n💰 Cost Ranges:")
        println("   Min: ${costs.minOrNull()}")
        println("   Max: ${costs.maxOrNull()}")
        println("   Avg: ${costs.average()}")
        
        // XP ranges
        val xpValues = abilities.map { it.xpGranted } + skills.map { it.xpGranted }
        println("\n⭐ XP Ranges:")
        println("   Min: ${xpValues.minOrNull()}")
        println("   Max: ${xpValues.maxOrNull()}")
        println("   Avg: ${xpValues.average()}")
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