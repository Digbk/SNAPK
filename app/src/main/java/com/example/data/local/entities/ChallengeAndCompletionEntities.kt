package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val category: String,
  val suggestedFormat: String,
  val optionsJson: String = "", // Comma-separated or choices for Quick Choice
  val targetAudience: String = "All", // Kids, Teens, Family, All
  val timeLimitSeconds: Int = 60,
  val isDaily: Boolean = false,
  val dailyDate: String = "", // e.g. "2026-09-11"
  val tips: String = "Take a breath, tap start, and give it your 60 seconds!",
  val featuredOrder: Int = 0,
  val popularityCount: Int = 120
)

@Entity(tableName = "completions")
data class CompletionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val challengeId: String,
  val challengeTitle: String,
  val category: String,
  val formatUsed: String,
  val textContent: String = "",
  val mediaUri: String = "",
  val drawingPointsJson: String = "",
  val secondsTaken: Int = 52,
  val completedAt: Long = System.currentTimeMillis(),
  val dateStr: String = "", // "2026-09-11"
  val sharePreset: String = "VIBRANT"
)
