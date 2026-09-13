package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BadgeItem
import com.example.ui.components.AvatarBubble
import com.example.ui.components.SnapkLogoIcon
import com.example.ui.components.SnapkWordmark
import com.example.ui.components.StreakFlameBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.OneMinuteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  viewModel: OneMinuteViewModel,
  onNavigateToNotifications: () -> Unit,
  onNavigateToFriends: () -> Unit,
  onShowOnboarding: () -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
  val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()
  val completions by viewModel.completions.collectAsStateWithLifecycle()
  val chains by viewModel.chains.collectAsStateWithLifecycle()
  val familyResponses by viewModel.familyResponses.collectAsStateWithLifecycle()
  val badges by viewModel.badges.collectAsStateWithLifecycle()

  var showEditProfileDialog by remember { mutableStateOf(false) }
  var editedName by remember { mutableStateOf(userProfile.name) }
  var editedHandle by remember { mutableStateOf(userProfile.handle) }
  var editedEmoji by remember { mutableStateOf(userProfile.avatarEmoji) }

  val totalReach = chains.sumOf { it.totalParticipants }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Profile & Badges",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
          )
        },
        actions = {
          IconButton(onClick = onShowOnboarding) {
            Icon(Icons.Default.HelpOutline, contentDescription = "How it works")
          }
          IconButton(onClick = onNavigateToFriends) {
            Icon(Icons.Default.People, contentDescription = "Friends")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("profile_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // USER BIO CARD
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
              ) {
                AvatarBubble(
                  name = userProfile.name,
                  emoji = userProfile.avatarEmoji,
                  size = 56.dp
                )
                Column {
                  Text(
                    text = userProfile.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                  )
                  Text(
                    text = userProfile.handle,
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = MaterialTheme.colorScheme.primary,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }
              }

              IconButton(onClick = {
                editedName = userProfile.name
                editedHandle = userProfile.handle
                editedEmoji = userProfile.avatarEmoji
                showEditProfileDialog = true
              }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
              }
            }

            Text(
              text = userProfile.bio,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // High-Impact Stats Bar
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "🔥 $streakDays",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = StreakFire
                    )
                  )
                  Text("Day Streak", style = MaterialTheme.typography.labelSmall)
                }

                Divider(modifier = Modifier.height(26.dp).width(1.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "${completions.size}",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = MaterialTheme.colorScheme.primary
                    )
                  )
                  Text("Completed", style = MaterialTheme.typography.labelSmall)
                }

                Divider(modifier = Modifier.height(26.dp).width(1.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "$totalReach",
                    style = MaterialTheme.typography.titleMedium.copy(
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
      }

      // ACHIEVEMENT BADGES SECTION
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "Achievement Badges",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "${badges.count { it.isUnlocked }} / ${badges.size} Unlocked",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )
          }

          LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(badges) { badge ->
              Card(
                modifier = Modifier
                  .width(140.dp)
                  .height(160.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.SpaceBetween
                ) {
                  Box(
                    modifier = Modifier
                      .size(48.dp)
                      .clip(CircleShape)
                      .background(
                        if (badge.isUnlocked) AmberTertiaryContainerLight else Color.LightGray.copy(alpha = 0.2f)
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = if (badge.isUnlocked) badge.iconEmoji else "🔒",
                      fontSize = 24.sp
                    )
                  }

                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = badge.title,
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                      ),
                      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                      maxLines = 1
                    )
                    Text(
                      text = badge.unlockRequirement,
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      ),
                      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                      maxLines = 2
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (badge.isUnlocked) Color(0xFF10B981).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = if (badge.isUnlocked) "UNLOCKED" else "LOCKED",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (badge.isUnlocked) Color(0xFF10B981) else Color.Gray
                      )
                    )
                  }
                }
              }
            }
          }
        }
      }

      // COMPLETED CHALLENGES ARCHIVE
      item {
        Text(
          "Your 60s Challenge Archive",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }

      if (completions.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            )
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text("⚡", fontSize = 32.sp)
                Text(
                  "No challenges logged yet today!",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  "Take today's 60-second challenge on the Home screen!",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      } else {
        items(completions) { item ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
              ) {
                Box(
                  modifier = Modifier.size(40.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("⚡", fontSize = 18.sp)
                }
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.challengeTitle,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Finished in ${item.secondsTaken}s • ${item.formatUsed}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF10B981).copy(alpha = 0.15f)
              ) {
                Text(
                  text = "Completed",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                  )
                )
              }
            }
          }
        }
      }

      // PROFILE MANAGEMENT CARD
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          )
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              "Account & Setup",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = {
                  editedName = userProfile.name
                  editedHandle = userProfile.handle
                  editedEmoji = userProfile.avatarEmoji
                  showEditProfileDialog = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Edit Profile ✏️", fontSize = 13.sp)
              }

              OutlinedButton(
                onClick = {
                  viewModel.resetProfileSetup()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Re-run Setup ⚙️", fontSize = 13.sp)
              }
            }
          }
        }
      }

      // SNAPK BRANDED FOOTER
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SnapkLogoIcon(size = 36.dp, cornerRadius = 10.dp)
          SnapkWordmark(fontSize = 18.sp)
          Text(
            text = "SNAPK v1.0 • The 60-Second Social Challenge App",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

    }
  }

  // EDIT PROFILE DIALOG
  if (showEditProfileDialog) {
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Profile 👤") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = editedName,
            onValueChange = { editedName = it },
            label = { Text("Full Name") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          OutlinedTextField(
            value = editedHandle,
            onValueChange = { editedHandle = it },
            label = { Text("Handle") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Text("Select Avatar:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          val avatarOptions = listOf("⚡", "🦊", "🚀", "🎨", "🦁", "🍕", "🎮", "🛹", "🎧", "🌟", "🦄", "🐱", "🐶", "🤖", "🌈", "🔥")
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(avatarOptions) { emoji ->
              Surface(
                shape = CircleShape,
                color = if (editedEmoji == emoji) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                border = if (editedEmoji == emoji) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .clickable { editedEmoji = emoji }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(emoji, fontSize = 20.sp)
                }
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showEditProfileDialog = false
            viewModel.updateProfile(editedName, editedHandle, editedEmoji)
          }
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun OnboardingDialog(
  onDismiss: () -> Unit
) {
  var currentStep by remember { mutableIntStateOf(0) }

  val steps = listOf(
    Triple(
      "⏱️ 60 Seconds on SNAPK",
      "No endless scrolling. Every day you get one fun, 60-second challenge in categories like Funny, Creativity, Knowledge, and Photography. Free, positive, and safe for all ages!",
      IndigoPrimaryLight
    ),
    Triple(
      "🌐 Viral Challenge Chains",
      "After beating the clock, challenge 3 friends! As each friend finishes and challenges 3 more, your chain multiplies. Watch participant counts and distance travel across cities!",
      CoralSecondaryLight
    ),
    Triple(
      "🏡 Private Family Circles",
      "Invite your family into an intimate private circle. Answer daily bonding questions together and let the app automatically generate monthly Family Memory Books!",
      AmberTertiaryLight
    )
  )

  val (title, description, color) = steps[currentStep]

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SnapkLogoIcon(size = 24.dp, cornerRadius = 6.dp)
        Text(title, fontWeight = FontWeight.Black, fontSize = 17.sp)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
          description,
          style = MaterialTheme.typography.bodyMedium,
          lineHeight = 22.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Step Dots
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          steps.indices.forEach { index ->
            Box(
              modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(if (currentStep == index) 16.dp else 8.dp, 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (currentStep == index) color else Color.LightGray.copy(alpha = 0.4f))
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (currentStep < steps.size - 1) {
            currentStep += 1
          } else {
            onDismiss()
          }
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
      ) {
        Text(if (currentStep < steps.size - 1) "Next →" else "Let's Play! ⚡", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Skip")
      }
    }
  )
}
