package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AvatarBubble
import com.example.ui.theme.CoralSecondaryLight
import com.example.ui.theme.IndigoPrimaryLight
import com.example.ui.theme.StreakFire

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
  onSetupComplete: (name: String, handle: String, avatar: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var fullName by remember { mutableStateOf("") }
  var handle by remember { mutableStateOf("") }
  var selectedAvatar by remember { mutableStateOf("⚡") }
  var isSubmittedOnce by remember { mutableStateOf(false) }

  val avatarOptions = remember {
    listOf(
      "⚡", "🦊", "🚀", "🎨",
      "🦁", "🍕", "🎮", "🛹",
      "🎧", "🌟", "🦄", "🐱",
      "🐶", "🤖", "🌈", "🔥"
    )
  }

  val isNameValid = fullName.trim().isNotBlank()
  val isHandleValid = handle.trim().isNotBlank()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("profile_setup_screen")
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
      ) {
        // HERO BRANDING
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(top = 8.dp)
        ) {
          Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 6.dp
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "One Minute Logo",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
              )
            }
          }

          Text(
            text = "Welcome to One Minute",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = (-0.5).sp
            ),
            textAlign = TextAlign.Center
          )

          Text(
            text = "Daily 60-second social challenges with friends & family. Let's create your champion profile to get started:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
          )
        }

        // LIVE PROFILE PREVIEW CARD
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_preview_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Text(
              text = "YOUR PROFILE PREVIEW",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
              )
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(selectedAvatar, fontSize = 28.sp)
                }
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = fullName.trim().ifBlank { "Your Name" },
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black
                  ),
                  color = if (fullName.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )

                val displayHandle = if (handle.isNotBlank()) {
                  if (handle.trim().startsWith("@")) handle.trim() else "@${handle.trim()}"
                } else {
                  "@your_handle"
                }

                Text(
                  text = displayHandle,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }
            }

            // INITIAL 0 STATS BAR
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surface
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "🔥 0",
                    style = MaterialTheme.typography.labelLarge.copy(
                      fontWeight = FontWeight.Black,
                      color = StreakFire
                    )
                  )
                  Text("Streak", style = MaterialTheme.typography.labelSmall)
                }

                Divider(modifier = Modifier.height(20.dp).width(1.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "0",
                    style = MaterialTheme.typography.labelLarge.copy(
                      fontWeight = FontWeight.Black,
                      color = MaterialTheme.colorScheme.primary
                    )
                  )
                  Text("Completed", style = MaterialTheme.typography.labelSmall)
                }

                Divider(modifier = Modifier.height(20.dp).width(1.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "0",
                    style = MaterialTheme.typography.labelLarge.copy(
                      fontWeight = FontWeight.Black,
                      color = CoralSecondaryLight
                    )
                  )
                  Text("Chain Reach", style = MaterialTheme.typography.labelSmall)
                }
              }
            }
          }
        }

        // INPUT: FULL NAME
        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = { Text("Full Name") },
          placeholder = { Text("e.g., Alex Rivera") },
          leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          },
          isError = isSubmittedOnce && !isNameValid,
          supportingText = {
            if (isSubmittedOnce && !isNameValid) {
              Text("Please enter your full name", color = MaterialTheme.colorScheme.error)
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("full_name_input")
        )

        // INPUT: HANDLE
        OutlinedTextField(
          value = handle,
          onValueChange = { input ->
            handle = input.filter { it.isLetterOrDigit() || it == '_' || it == '@' }
          },
          label = { Text("Handle") },
          placeholder = { Text("e.g., alex") },
          leadingIcon = {
            Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          },
          prefix = {
            if (!handle.startsWith("@")) Text("@", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
          },
          isError = isSubmittedOnce && !isHandleValid,
          supportingText = {
            if (isSubmittedOnce && !isHandleValid) {
              Text("Please choose a handle for challenges", color = MaterialTheme.colorScheme.error)
            } else {
              Text("Your unique username for challenge chains & friends")
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("handle_input")
        )

        // SELECT AVATAR SECTION
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Select Avatar",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Chosen: $selectedAvatar",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )
          }

          Text(
            text = "Pick your favorite 60-second champion icon:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // AVATAR GRID (4x4)
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("avatar_selector_grid")
          ) {
            avatarOptions.chunked(4).forEach { rowEmojis ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                rowEmojis.forEach { emoji ->
                  val isSelected = selectedAvatar == emoji
                  val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1.0f,
                    animationSpec = spring(),
                    label = "avatar_scale"
                  )

                  Surface(
                    shape = CircleShape,
                    color = if (isSelected) {
                      MaterialTheme.colorScheme.primaryContainer
                    } else {
                      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    },
                    border = if (isSelected) {
                      BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
                    } else null,
                    shadowElevation = if (isSelected) 4.dp else 0.dp,
                    modifier = Modifier
                      .size(52.dp)
                      .scale(scale)
                      .clip(CircleShape)
                      .clickable { selectedAvatar = emoji }
                      .testTag("avatar_option_$emoji")
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(emoji, fontSize = 24.sp)
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // SUBMIT BUTTON
        Button(
          onClick = {
            isSubmittedOnce = true
            if (isNameValid && isHandleValid) {
              onSetupComplete(fullName, handle, selectedAvatar)
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("get_started_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              "Start One Minute",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Icon(Icons.Default.ArrowForward, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
