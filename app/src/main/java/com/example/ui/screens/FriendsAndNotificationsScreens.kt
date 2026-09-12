package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AvatarBubble
import com.example.ui.theme.*
import com.example.ui.viewmodel.OneMinuteViewModel

data class FriendItem(
  val id: String,
  val name: String,
  val handle: String,
  val avatarColor: Color,
  val streakDays: Int,
  val lastChallengeTitle: String,
  val status: String = "Ready"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
  viewModel: OneMinuteViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

  var snackbarMessage by remember { mutableStateOf<String?>(null) }

  val friendsList = remember {
    listOf(
      FriendItem("f1", "Liam Parker", "@liamp", Color(0xFFFF6B6B), 8, "Drew 'Flying Taco Pug'", "Challenged"),
      FriendItem("f2", "Samantha Reed", "@sam_r", Color(0xFF10B981), 14, "Finished 3 mind facts in 42s", "Active"),
      FriendItem("f3", "Zack Chen", "@zack_c", Color(0xFFF59E0B), 5, "Yellow Scavenger photo", "Active"),
      FriendItem("f4", "Maya Lin", "@mayalin", Color(0xFFEC4899), 11, "Kindness note sent", "Active"),
      FriendItem("f5", "Jordan K.", "@jordank", Color(0xFF3B82F6), 3, "Food Blitz letter P", "Ready")
    )
  }

  fun sharePersonalInviteCode() {
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(
        Intent.EXTRA_TEXT,
        "⚡ Join me on One Minute! Add me with invite code: 1M-ALEX-77 and let's trade daily 60-second challenges!\nhttps://oneminute.app/add/1M-ALEX-77"
      )
      type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Invite Friend to One Minute"))
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Friends & Invites",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
        .testTag("friends_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // INVITE CODE HERO CARD
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = IndigoPrimaryContainerLight.copy(alpha = 0.5f)
          )
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "Your Challenge Code",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary
              ) {
                Text(
                  "1M-ALEX-77",
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                  )
                )
              }
            }

            Text(
              "Share your personal link to challenge friends directly to daily 60-second face-offs!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
              onClick = { sharePersonalInviteCode() },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Share, contentDescription = null, Modifier.size(16.dp))
              Spacer(Modifier.width(8.dp))
              Text("Share Invite Code via WhatsApp / SMS", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // FRIENDS LIST HEADER
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "Active Friends (${friendsList.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            "All Positive & Safe",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
          )
        }
      }

      // FRIENDS ITEMS
      items(friendsList) { friend ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            AvatarBubble(name = friend.name, backgroundColor = friend.avatarColor, size = 44.dp)

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  friend.name,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  "🔥 ${friend.streakDays}d",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = StreakFire
                  )
                )
              }
              Text(
                friend.lastChallengeTitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
              )
            }

            OutlinedButton(
              onClick = {
                snackbarMessage = "60s Nudge sent to ${friend.name}! ⚡"
              },
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text("Nudge ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      snackbarMessage?.let { msg ->
        item {
          Snackbar(
            action = {
              TextButton(onClick = { snackbarMessage = null }) {
                Text("Dismiss", color = Color.White)
              }
            }
          ) {
            Text(msg)
          }
        }
      }

    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
  viewModel: OneMinuteViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Notifications",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          TextButton(onClick = { viewModel.markNotificationsRead() }) {
            Text("Mark Read", fontWeight = FontWeight.Bold)
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
        .testTag("notifications_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (notifications.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text("🔔", fontSize = 40.sp)
              Text("All caught up!", fontWeight = FontWeight.Bold)
              Text(
                "New 60s challenges drop daily at 9:00 AM.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(notifications) { notif ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (notif.isRead) 1.dp else 2.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(
                    when (notif.type) {
                      "DAILY_DROP" -> IndigoPrimaryLight.copy(alpha = 0.15f)
                      "CHAIN_REACTION" -> CoralSecondaryLight.copy(alpha = 0.15f)
                      "FAMILY_MOMENT" -> AmberTertiaryLight.copy(alpha = 0.15f)
                      else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (notif.type) {
                    "DAILY_DROP" -> "⚡"
                    "CHAIN_REACTION" -> "🌐"
                    "FAMILY_MOMENT" -> "🏡"
                    else -> "✨"
                  },
                  fontSize = 20.sp
                )
              }

              Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = notif.title,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = notif.message,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }
  }
}
