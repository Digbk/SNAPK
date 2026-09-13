package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.CompletionEntity
import com.example.data.model.ChallengeCategory
import com.example.ui.components.CategoryChip
import com.example.ui.components.SnapkLogoIcon
import com.example.ui.components.SnapkWordmark
import com.example.ui.theme.*
import com.example.ui.viewmodel.OneMinuteViewModel

enum class SocialPlatformPreset(val label: String, val icon: String, val ratioDesc: String) {
  INSTAGRAM_STORY("Instagram", "📸", "9:16 Story"),
  WHATSAPP("WhatsApp", "💬", "Chat Card"),
  SQUARE_POST("Feed Post", "🖼️", "1:1 Square"),
  SHORTS("Shorts/TikTok", "⚡", "Vertical Clip");
}

data class CardThemePalette(val name: String, val brush: Brush, val textColor: Color, val accentColor: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareCardScreen(
  viewModel: OneMinuteViewModel,
  completion: CompletionEntity?,
  onNavigateHome: () -> Unit,
  onNavigateToChains: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

  var selectedPlatform by remember { mutableStateOf(SocialPlatformPreset.INSTAGRAM_STORY) }
  var selectedThemeIndex by remember { mutableIntStateOf(0) }
  var showInvite3FriendsDialog by remember { mutableStateOf(false) }

  var friend1 by remember { mutableStateOf("Liam Parker") }
  var friend2 by remember { mutableStateOf("Samantha Reed") }
  var friend3 by remember { mutableStateOf("Zack Chen") }

  val themePalettes = listOf(
    CardThemePalette(
      name = "Sunset Spark",
      brush = Brush.linearGradient(listOf(Color(0xFFFF6B6B), Color(0xFFF59E0B), Color(0xFF8B5CF6))),
      textColor = Color.White,
      accentColor = Color(0xFFFFD166)
    ),
    CardThemePalette(
      name = "Electric Indigo",
      brush = Brush.linearGradient(listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFF06B6D4))),
      textColor = Color.White,
      accentColor = Color(0xFF38BDF8)
    ),
    CardThemePalette(
      name = "Mint Aura",
      brush = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF0EA5E9), Color(0xFF6366F1))),
      textColor = Color.White,
      accentColor = Color(0xFFA7F3D0)
    ),
    CardThemePalette(
      name = "Midnight Glow",
      brush = Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))),
      textColor = Color.White,
      accentColor = Color(0xFF818CF8)
    )
  )

  val currentPalette = themePalettes[selectedThemeIndex]

  fun shareCardNative() {
    val shareText = """
      ⚡ I just crushed the 60-second SNAPK challenge:
      '${completion?.challengeTitle}' in ${completion?.secondsTaken ?: 50} seconds!
      🔥 Streak: $streakDays Days
      
      Can you beat my time?
      Join my Challenge Chain on SNAPK!
    """.trimIndent()

    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, shareText)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Challenge Card")
    context.startActivity(shareIntent)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Shareable Challenge Card",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateHome) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // PLATFORM PRESET TABS
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SocialPlatformPreset.entries.forEach { preset ->
          val isSelected = selectedPlatform == preset
          FilterChip(
            selected = isSelected,
            onClick = { selectedPlatform = preset },
            label = { Text("${preset.icon} ${preset.label}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          )
        }
      }

      // COLOR THEME PRESET SELECTOR
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Card Visual Style:",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          themePalettes.forEachIndexed { index, palette ->
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(palette.brush)
                .border(
                  width = if (selectedThemeIndex == index) 3.dp else 1.dp,
                  color = if (selectedThemeIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent,
                  shape = CircleShape
                )
                .clickable { selectedThemeIndex = index }
            )
          }
        }
      }

      // LIVE BEAUTIFUL SHARABLE CHALLENGE CARD PREVIEW
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
          .testTag("shareable_challenge_card_preview"),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(currentPalette.brush)
            .padding(24.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Header Bar
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                SnapkLogoIcon(size = 28.dp, cornerRadius = 7.dp)
                SnapkWordmark(
                  fontSize = 15.sp,
                  textColor = currentPalette.textColor
                )
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.2f)
              ) {
                Text(
                  text = "🔥 $streakDays Day Streak",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                )
              }
            }

            // Challenge Title
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = currentPalette.accentColor.copy(alpha = 0.25f)
              ) {
                Text(
                  text = completion?.category?.uppercase() ?: "CREATIVITY",
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = currentPalette.accentColor,
                    letterSpacing = 1.sp
                  )
                )
              }

              Text(
                text = completion?.challengeTitle ?: "60-Second Speed Challenge",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Black,
                  color = currentPalette.textColor,
                  textAlign = TextAlign.Center
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )
            }

            // User's Answer Box
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              color = Color.White.copy(alpha = 0.15f)
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                val format = completion?.formatUsed ?: "TEXT"
                when (format) {
                  "DRAWING" -> {
                    Text("🎨 Speed Doodle Finished!", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                      "Completed in ${completion?.secondsTaken ?: 48}s on SNAPK canvas!",
                      color = Color.White.copy(alpha = 0.85f),
                      fontSize = 12.sp,
                      textAlign = TextAlign.Center
                    )
                  }
                  "PHOTO" -> {
                    Text("📸 Photo Captured!", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                      "60s Scavenger snapshot unlocked!",
                      color = Color.White.copy(alpha = 0.85f),
                      fontSize = 12.sp
                    )
                  }
                  else -> {
                    Text(
                      text = "\"${completion?.textContent?.ifBlank { "Completed in 52 seconds flat!" } ?: "Crushed the 60s timer!"}\"",
                      style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                      ),
                      maxLines = 3,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = currentPalette.accentColor
                ) {
                  Text(
                    text = "⏱️ Beat The Clock: ${completion?.secondsTaken ?: 48}s",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Black,
                      color = Color(0xFF0F172A)
                    )
                  )
                }
              }
            }

            // Footer Viral Invitation
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  "By ${userProfile.name}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                )
                Text(
                  "SNAPK Chain: SNAP-9842",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.7f)
                  )
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White
              ) {
                Text(
                  text = "Challenge 3 Friends 🤝",
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                  )
                )
              }
            }
          }
        }
      }

      // PRIMARY SHARING ACTION BUTTONS
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { shareCardNative() },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("share_card_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = WhatsAppGreen
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
            Text(
              "Share to WhatsApp & Friends 🚀",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        Button(
          onClick = { showInvite3FriendsDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("challenge_3_friends_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(imageVector = Icons.Default.GroupAdd, contentDescription = "Chain")
            Text(
              "Challenge 3 Friends (Start Viral Chain)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        OutlinedButton(
          onClick = onNavigateHome,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp)
        ) {
          Text("Done & Back to Home", fontWeight = FontWeight.Bold)
        }
      }

    }
  }

  // DIALOG TO CHALLENGE 3 FRIENDS (VIRAL CHAIN IGNITION)
  if (showInvite3FriendsDialog) {
    AlertDialog(
      onDismissRequest = { showInvite3FriendsDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("🌐 Challenge 3 Friends")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            "Each friend who completes this will challenge 3 more people, multiplying your chain across cities!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedTextField(
            value = friend1,
            onValueChange = { friend1 = it },
            label = { Text("Friend #1") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = friend2,
            onValueChange = { friend2 = it },
            label = { Text("Friend #2") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = friend3,
            onValueChange = { friend3 = it },
            label = { Text("Friend #3") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showInvite3FriendsDialog = false
            viewModel.launchChallengeChain(listOf(friend1, friend2, friend3)) {
              onNavigateToChains()
            }
          },
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Launch Chain 🚀")
        }
      },
      dismissButton = {
        TextButton(onClick = { showInvite3FriendsDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
