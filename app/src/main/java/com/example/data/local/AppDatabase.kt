package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*

@Database(
  entities = [
    ChallengeEntity::class,
    CompletionEntity::class,
    ChainEntity::class,
    ChainParticipantEntity::class,
    FamilyCircleEntity::class,
    FamilyResponseEntity::class,
    AppNotificationEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun challengeDao(): ChallengeDao
  abstract fun completionDao(): CompletionDao
  abstract fun chainDao(): ChainDao
  abstract fun familyCircleDao(): FamilyCircleDao
  abstract fun notificationDao(): NotificationDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "one_minute_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
