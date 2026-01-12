package com.trashapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trashapp.database.dao.ChallengeDao
import com.trashapp.database.dao.TrophyDao
import com.trashapp.database.entity.ChallengeEntity
import com.trashapp.database.entity.ChallengeProgressEntity
import com.trashapp.database.entity.LevelChallengesEntity
import com.trashapp.database.entity.LevelProgressEntity
import com.trashapp.database.entity.TrophyCollectionEntity
import com.trashapp.database.entity.TrophyEntity
import com.trashapp.database.entity.TrophyProgressEntity

/**
 * Room database for TRASH game
 * Handles persistence for Trophy and Challenge systems
 */
@Database(
    entities = [
        // Trophy entities
        TrophyEntity::class,
        TrophyCollectionEntity::class,
        TrophyProgressEntity::class,
        // Challenge entities
        ChallengeEntity::class,
        ChallengeProgressEntity::class,
        LevelChallengesEntity::class,
        LevelProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TrashDatabase : RoomDatabase() {
    
    abstract fun trophyDao(): TrophyDao
    abstract fun challengeDao(): ChallengeDao
    
    companion object {
        private const val DATABASE_NAME = "trash_game_database"
        
        @Volatile
        private var INSTANCE: TrashDatabase? = null
        
        /**
         * Get database instance
         */
        fun getInstance(context: Context): TrashDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrashDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Destroy database instance (for testing)
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

/**
 * Type converters for Room database
 */
class Converters {
    
    // Note: More complex type converters would be added here if needed
    // For now, we're storing enums as strings and lists as JSON strings
    
    /**
     * Migration from version 1 to 2 (example for future use)
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new columns or tables here
            // Example:
            // database.execSQL("ALTER TABLE trophies ADD COLUMN newColumn TEXT")
        }
    }
}