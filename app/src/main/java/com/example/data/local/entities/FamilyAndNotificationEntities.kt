package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_circles")
data class FamilyCircleEntity(
  @PrimaryKey val id: String,
  val circleName: String,
  val joinCode: String,
  val memberCount: Int = 4,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_responses")
data class FamilyResponseEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val circleId: String,
  val challengeTitle: String,
  val category: String,
  val authorName: String,
  val authorRole: String, // Mom, Dad, Maya (Teen), Leo (Kid), Grandma
  val avatarEmoji: String,
  val responseFormat: String,
  val textContent: String,
  val mediaUri: String = "",
  val drawingPointsJson: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val monthYear: String = "September 2026", // Grouped for Monthly Memory Book
  val reactionsCount: Int = 3,
  val stickerTag: String = "❤️ Favorite"
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val message: String,
  val type: String, // DAILY_DROP, CHAIN_REACTION, FAMILY_MOMENT, BADGE_UNLOCKED
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false,
  val relatedActionId: String = ""
)
