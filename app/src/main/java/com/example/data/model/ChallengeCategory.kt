package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class ChallengeCategory(
  val displayName: String,
  val icon: String,
  val color: Color,
  val description: String
) {
  FUNNY("Funny", "😂", CategoryFunny, "Hilarious 60-second prompts & laughs"),
  CREATIVITY("Creativity", "🎨", CategoryCreativity, "Quick doodles, ideas & imagination"),
  KNOWLEDGE("Knowledge", "🧠", CategoryKnowledge, "Rapid trivia, puzzles & brain sparks"),
  FAMILY("Family", "🏡", CategoryFamily, "Heartwarming prompts for all generations"),
  FRIENDS("Friends", "🤝", CategoryFriends, "Inside jokes, banter & friend challenges"),
  MEMORIES("Memories", "✨", CategoryMemories, "Cherished nostalgia & story moments"),
  PHOTOGRAPHY("Photography", "📸", CategoryPhotography, "Creative 1-minute angles & everyday snaps"),
  KINDNESS("Kindness", "💛", CategoryKindness, "Micro-acts of compassion & warm messages"),
  SCHOOL("School", "🎒", CategorySchool, "Fun campus hacks, study sprints & recess giggles"),
  QUICK_GAMES("Quick Games", "⚡", CategoryQuickGames, "60-second physical or mental mini-challenges");

  companion object {
    fun fromString(value: String): ChallengeCategory {
      return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
        ?: CREATIVITY
    }
  }
}

enum class ResponseFormat(val label: String, val iconName: String) {
  TEXT("Text", "edit"),
  PHOTO("Photo", "camera"),
  DRAWING("Doodle", "brush"),
  QUICK_CHOICE("Quick Choice", "check_circle");

  companion object {
    fun fromString(value: String): ResponseFormat {
      return entries.find { it.name.equals(value, ignoreCase = true) } ?: TEXT
    }
  }
}

data class BadgeItem(
  val id: String,
  val title: String,
  val description: String,
  val iconEmoji: String,
  val isUnlocked: Boolean,
  val unlockRequirement: String
)
