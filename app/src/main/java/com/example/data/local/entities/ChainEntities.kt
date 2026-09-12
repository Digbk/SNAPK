package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenge_chains")
data class ChainEntity(
  @PrimaryKey val id: String,
  val originChallengeId: String,
  val challengeTitle: String,
  val category: String,
  val creatorName: String,
  val inviteCode: String,
  val createdAt: Long = System.currentTimeMillis(),
  val totalParticipants: Int = 1,
  val depthGenerations: Int = 1,
  val citiesReached: Int = 1,
  val milestoneReached: String = "Spark Ignited",
  val status: String = "ACTIVE", // ACTIVE, VIRAL, COMPLETED
  val isUserInitiated: Boolean = true
)

@Entity(tableName = "chain_participants")
data class ChainParticipantEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val chainId: String,
  val name: String,
  val relation: String, // Friend, Sister, Classmate, etc.
  val generation: Int, // 1 = you, 2 = your 3 friends, 3 = their friends
  val parentName: String,
  val completedAt: Long = System.currentTimeMillis(),
  val responseSnippet: String,
  val avatarColorHex: String,
  val location: String = "Nearby"
)
