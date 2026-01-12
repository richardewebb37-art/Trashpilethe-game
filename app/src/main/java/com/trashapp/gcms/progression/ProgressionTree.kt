package com.trashapp.gcms.progression

/**
 * Progression Tree
 * Manages the complete skill and ability tree structure
 * Handles unlocking logic and tracks player progression
 * Enhanced to support complete 200-level progression with all skills/abilities
 */
data class ProgressionTree(
    val abilities: Map<String, Ability> = emptyMap(),
    val skills: Map<String, Skill> = emptyMap(),
    val totalUnlockedAbilities: Int = 0,
    val totalUnlockedSkills: Int = 0,
    val totalAvailableXP: Int = 0  // Total XP available from all purchasable items
) {
    /**
     * Create a new ProgressionTree with all skills and abilities loaded
     */
    companion object {
        /**
         * Create a fresh progression tree with all skills/abilities
         */
        fun create(): ProgressionTree {
            val allSkills = SkillAbilityData.getAllSkills()
            val allAbilities = SkillAbilityData.getAllAbilities()
            
            val skillMap = allSkills.associateBy { it.id }
            val abilityMap = allAbilities.associateBy { it.id }
            
            return ProgressionTree(
                abilities = abilityMap,
                skills = skillMap
            ).updateUnlockStatus()
        }
        
        /**
         * Create progression tree with given skill/ability maps
         */
        fun fromMaps(skills: Map<String, Skill>, abilities: Map<String, Ability>): ProgressionTree {
            return ProgressionTree(
                abilities = abilities,
                skills = skills
            ).updateUnlockStatus()
        }
    }
    
    /**
     * Get an ability by ID
     */
    fun getAbility(id: String): Ability? {
        return abilities[id]
    }
    
    /**
     * Get a skill by ID
     */
    fun getSkill(id: String): Skill? {
        return skills[id]
    }
    
    /**
     * Check if ability prerequisites are met
     */
    fun areAbilityPrerequisitesMet(abilityId: String, playerLevel: Int = 1): Boolean {
        val ability = getAbility(abilityId) ?: return false
        
        // Check level requirement
        if (ability.levelRequired > playerLevel) {
            return false
        }
        
        // Check prerequisites
        return ability.prerequisites.all { prerequisiteId ->
            // Check if prerequisite is a skill
            val skill = getSkill(prerequisiteId)
            if (skill != null) {
                skill.isPurchased
            } else {
                // Check if prerequisite is an ability
                val prerequisiteAbility = getAbility(prerequisiteId)
                prerequisiteAbility?.isPurchased == true
            }
        }
    }
    
    /**
     * Check if skill prerequisites are met
     */
    fun areSkillPrerequisitesMet(skillId: String, playerLevel: Int = 1): Boolean {
        val skill = getSkill(skillId) ?: return false
        
        // Check level requirement
        if (skill.levelRequired > playerLevel) {
            return false
        }
        
        // Check prerequisites
        return skill.prerequisites.all { prerequisiteId ->
            val prerequisite = getSkill(prerequisiteId)
            prerequisite?.isPurchased == true
        }
    }
    
    /**
     * Update unlock status for all items based on level and prerequisites
     */
    fun updateUnlockStatus(playerLevel: Int = 1): ProgressionTree {
        val updatedAbilities = abilities.mapValues { (id, ability) ->
            val shouldBeUnlocked = areAbilityPrerequisitesMet(id, playerLevel)
            ability.copy(isUnlocked = shouldBeUnlocked)
        }
        
        val updatedSkills = skills.mapValues { (id, skill) ->
            val shouldBeUnlocked = areSkillPrerequisitesMet(id, playerLevel)
            skill.copy(isUnlocked = shouldBeUnlocked)
        }
        
        val unlockedAbilityCount = updatedAbilities.values.count { it.isUnlocked }
        val unlockedSkillCount = updatedSkills.values.count { it.isUnlocked }
        
        val availableXP = updatedAbilities.values
            .filter { it.isUnlocked }
            .sumOf { it.xpGranted } +
            updatedSkills.values
            .filter { it.isUnlocked }
            .sumOf { it.xpGranted }
        
        return copy(
            abilities = updatedAbilities,
            skills = updatedSkills,
            totalUnlockedAbilities = unlockedAbilityCount,
            totalUnlockedSkills = unlockedSkillCount,
            totalAvailableXP = availableXP
        )
    }
    
    /**
     * Purchase a skill
     */
    fun purchaseSkill(skillId: String, currentPoints: Int): Pair<ProgressionTree, Int> {
        val skill = getSkill(skillId) ?: return Pair(this, currentPoints)
        
        if (!skill.canPurchase(currentPoints)) {
            return Pair(this, currentPoints)
        }
        
        val updatedSkill = skill.purchase()
        val newPoints = currentPoints - skill.cost
        
        return Pair(
            copy(
                skills = skills + (skillId to updatedSkill)
            ).updateUnlockStatus(),
            newPoints
        )
    }
    
    /**
     * Purchase an ability
     */
    fun purchaseAbility(abilityId: String, currentPoints: Int): Pair<ProgressionTree, Int> {
        val ability = getAbility(abilityId) ?: return Pair(this, currentPoints)
        
        if (!ability.canPurchase(currentPoints)) {
            return Pair(this, currentPoints)
        }
        
        val updatedAbility = ability.purchase()
        val newPoints = currentPoints - ability.cost
        
        return Pair(
            copy(
                abilities = abilities + (abilityId to updatedAbility)
            ).updateUnlockStatus(),
            newPoints
        )
    }
    
    /**
     * Use an ability
     */
    fun useAbility(abilityId: String, currentAP: Int): Pair<ProgressionTree, Int>? {
        val ability = getAbility(abilityId) ?: return null
        
        if (!ability.canUse()) {
            return null
        }
        
        if (currentAP < ability.cost) {
            return null
        }
        
        val updatedAbility = ability.use()
        val newAP = currentAP - ability.cost
        
        return Pair(
            copy(
                abilities = abilities + (abilityId to updatedAbility)
            ),
            newAP
        )
    }
    
    /**
     * Reset round usage for all abilities
     */
    fun resetRoundUsage(): ProgressionTree {
        val updatedAbilities = abilities.mapValues { (_, ability) ->
            ability.resetRoundUsage()
        }
        
        return copy(abilities = updatedAbilities)
    }
    
    /**
     * Reset match usage for all abilities
     */
    fun resetMatchUsage(): ProgressionTree {
        val updatedAbilities = abilities.mapValues { (_, ability) ->
            ability.resetMatchUsage()
        }
        
        return copy(abilities = updatedAbilities)
    }
    
    /**
     * Get skills for a specific tier
     */
    fun getSkillsForTier(tier: Tier): List<Skill> {
        return skills.values.filter { it.tier == tier }
    }
    
    /**
     * Get abilities for a specific tier
     */
    fun getAbilitiesForTier(tier: Tier): List<Ability> {
        return abilities.values.filter { it.tier == tier }
    }
    
    /**
     * Get skills for a specific category
     */
    fun getSkillsForCategory(category: SkillCategory): List<Skill> {
        return skills.values.filter { it.category == category }
    }
    
    /**
     * Get abilities for a specific category
     */
    fun getAbilitiesForCategory(category: AbilityCategory): List<Ability> {
        return abilities.values.filter { it.category == category }
    }
    
    /**
     * Get skills available at a specific level
     */
    fun getSkillsForLevel(level: Int): List<Skill> {
        return skills.values.filter { it.levelRequired <= level && it.isUnlocked }
    }
    
    /**
     * Get abilities available at a specific level
     */
    fun getAbilitiesForLevel(level: Int): List<Ability> {
        return abilities.values.filter { it.levelRequired <= level && it.isUnlocked }
    }
    
    /**
     * Get total XP from all purchased skills and abilities
     */
    fun calculateTotalXP(): Int {
        var totalXP = 0
        
        skills.values.filter { it.isPurchased }.forEach { skill ->
            totalXP += skill.xpGranted * skill.currentLevel
        }
        
        abilities.values.filter { it.isPurchased }.forEach { ability ->
            totalXP += ability.getTotalXPGranted()
        }
        
        return totalXP
    }
    
    /**
     * Get current level based on total XP
     */
    fun getCurrentLevel(): Int {
        val xpSystem = XPSystem()
        return xpSystem.calculateLevelFromXP(calculateTotalXP())
    }
    
    /**
     * Get current tier based on level
     */
    fun getCurrentTier(): Tier {
        return Tier.getTierForLevel(getCurrentLevel())
    }
    
    /**
     * Get statistics about the progression tree
     */
    fun getStatistics(): ProgressionStatistics {
        val purchasedSkills = skills.values.count { it.isPurchased }
        val purchasedAbilities = abilities.values.count { it.isPurchased }
        
        return ProgressionStatistics(
            totalSkills = skills.size,
            totalAbilities = abilities.size,
            purchasedSkills = purchasedSkills,
            purchasedAbilities = purchasedAbilities,
            unlockedSkills = totalUnlockedSkills,
            unlockedAbilities = totalUnlockedAbilities,
            totalXP = calculateTotalXP(),
            currentLevel = getCurrentLevel(),
            currentTier = getCurrentTier()
        )
    }
}

/**
 * Statistics about the progression tree
 */
data class ProgressionStatistics(
    val totalSkills: Int,
    val totalAbilities: Int,
    val purchasedSkills: Int,
    val purchasedAbilities: Int,
    val unlockedSkills: Int,
    val unlockedAbilities: Int,
    val totalXP: Int,
    val currentLevel: Int,
    val currentTier: Tier
)