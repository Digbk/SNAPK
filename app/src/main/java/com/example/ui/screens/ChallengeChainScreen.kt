package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ChainParticipantEntity
import com.example.ui.components.AvatarBubble
import com.example.ui.theme.*
import com.example.ui.viewmodel.OneMinuteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeChainScreen(
  viewModel: OneMinuteViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val chains by viewModel.chains.collectAsStateWithLifecycle()
  val participants by viewModel.selectedChainParticipants.collectAsStateWithLifecycle()
  val selectedChainId by viewModel.selectedChainId.collectAsStateWithLifecycle()

  val currentChain = chains.find { it.id == selectedChainId } ?: chains.firstOrNull()

  var showInviteDialog by remember { mutableStateOf(false) }
  var friend1 by remember { mutableStateOf("Elena Woods") }
  var friend2 by remember { mutableStateOf("Noah Miller") }
  var friend3 by remember { mutableStateOf("Kavya Patel") }

  fun shareInviteLink(code: String) {
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(
        Intent.EXTRA_TEXT,
        "🔥 Join my Challenge Chain on One Minute! Beat my 60-second time with invite code: $code\nhttps://oneminute.app/chain/$code"
      )
      type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Invite to Chain"))
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              "Viral Challenge Chains",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.simulateChainInvite() },
            modifier = Modifier.testTag("simulate_chain_growth_button")
          ) {
            Icon(
              imageVector = Icons.Default.AutoMode,
              contentDescription = "Simulate Growth",
              tint = MaterialTheme.colorScheme.primary
            )
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
        .testTag("challenge_chain_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // CHAIN OVERVIEW HERO CARD
      if (currentChain == null) {
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
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                  )
                }
              }

              Text(
                text = "Ignite Your First Chain Reaction",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )

              Text(
                text = "Challenge 3 friends to start a viral chain. When each friend completes the 60-second challenge and invites 3 more, watch your tree grow across cities!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )

              // Starting 0 Stats
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "0 Friends",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.Black,
                      color = MaterialTheme.colorScheme.primary
                    )
                  )
                  Text("Chain Reach", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "Gen 0",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.Black,
                      color = CoralSecondaryLight
                    )
                  )
                  Text("Chain Depth", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    "0 Cities",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.Black,
                      color = AmberTertiaryLight
                    )
                  )
                  Text("Distance", style = MaterialTheme.typography.labelSmall)
                }
              }

              Button(
                onClick = { showInviteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
              ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Start a Chain With 3 Friends", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      currentChain?.let { chain ->
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
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.verticalGradient(
                    listOf(
                      IndigoPrimaryContainerLight.copy(alpha = 0.5f),
                      MaterialTheme.colorScheme.surface
                    )
                  )
                )
                .padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.primary
                ) {
                  Text(
                    text = "CHAIN CODE: ${chain.inviteCode}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Black,
                      color = Color.White,
                      letterSpacing = 1.sp
                    )
                  )
                }

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = CoralSecondaryLight.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = chain.milestoneReached,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = CoralSecondaryLight
                    )
                  )
                }
              }

              Text(
                text = chain.challengeTitle,
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.Black,
                  fontSize = 20.sp
                )
              )

              // Viral Reach Stats Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = "${participants.size}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = MaterialTheme.colorScheme.primary
                    )
                  )
                  Text("Total Participants", style = MaterialTheme.typography.labelSmall)
                }

                Column {
                  val generations = participants.maxOfOrNull { it.generation } ?: 1
                  Text(
                    text = "Gen $generations",
                    style = MaterialTheme.typography.headlineMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = CoralSecondaryLight
                    )
                  )
                  Text("Chain Depth", style = MaterialTheme.typography.labelSmall)
                }

                Column {
                  val cities = participants.map { it.location }.distinct().size
                  Text(
                    text = "$cities Cities",
                    style = MaterialTheme.typography.headlineMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = AmberTertiaryLight
                    )
                  )
                  Text("Distance Reached", style = MaterialTheme.typography.labelSmall)
                }
              }

              // Milestone Progress Bar
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Milestone: ${chain.milestoneReached}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                  Text("${participants.size} / 20 to Galaxy 🌌", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                LinearProgressIndicator(
                  progress = { (participants.size.toFloat() / 20f).coerceIn(0f, 1f) },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                  color = MaterialTheme.colorScheme.primary,
                  trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
              }

              // Action Buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = { showInviteDialog = true },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = null, Modifier.size(16.dp))
                  Spacer(Modifier.width(6.dp))
                  Text("Challenge 3 Friends", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                  onClick = { shareInviteLink(chain.inviteCode) },
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(imageVector = Icons.Default.Share, contentDescription = "Share", Modifier.size(16.dp))
                }
              }
            }
          }
        }
      }

      // GROWING CHAIN TREE VISUALIZER
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "Visual Chain Pathway",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )

          Text(
            "1 -> 3 -> 9 Multiplier",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }

      // Chain Participants By Generation
      items(participants) { participant ->
        val nodeColor = try {
          Color(android.graphics.Color.parseColor(participant.avatarColorHex))
        } catch (e: Exception) {
          MaterialTheme.colorScheme.primary
        }

        val indentDp = ((participant.generation - 1) * 16).coerceAtMost(48).dp

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = indentDp)
            .animateContentSize(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Visual Connection Line & Avatar
          Box(contentAlignment = Alignment.Center) {
            Surface(
              modifier = Modifier.size(44.dp),
              shape = CircleShape,
              color = nodeColor.copy(alpha = 0.2f),
              border = androidx.compose.foundation.BorderStroke(2.dp, nodeColor)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "G${participant.generation}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = nodeColor
                  )
                )
              }
            }
          }

          // Participant Card
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = participant.name,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Text(
                    text = participant.location,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                  )
                }
              }

              Text(
                text = participant.responseSnippet,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              if (participant.parentName != "Self") {
                Text(
                  text = "Challenged by ${participant.parentName}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }
            }
          }
        }
      }

    }
  }

  // DIALOG TO ADD 3 FRIENDS TO CHAIN
  if (showInviteDialog) {
    AlertDialog(
      onDismissRequest = { showInviteDialog = false },
      title = { Text("Invite 3 Friends to Chain 🌐") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            "Enter 3 friends to send this 60s challenge to:",
            style = MaterialTheme.typography.bodySmall
          )
          OutlinedTextField(
            value = friend1,
            onValueChange = { friend1 = it },
            label = { Text("Friend #1") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = friend2,
            onValueChange = { friend2 = it },
            label = { Text("Friend #2") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = friend3,
            onValueChange = { friend3 = it },
            label = { Text("Friend #3") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showInviteDialog = false
            val friends = listOf(friend1, friend2, friend3).filter { it.isNotBlank() }
            if (currentChain == null) {
              viewModel.launchChallengeChain(
                friends = if (friends.isNotEmpty()) friends else listOf("Liam", "Samantha", "Zack")
              ) {
                // Launched
              }
            } else {
              viewModel.launchChallengeChain(
                friends = if (friends.isNotEmpty()) friends else listOf("Liam", "Samantha", "Zack"),
                challengeTitleOverride = currentChain.challengeTitle,
                categoryOverride = currentChain.category
              ) {
                shareInviteLink(currentChain.inviteCode)
              }
            }
          }
        ) {
          Text("Send Invites 🚀")
        }
      },
      dismissButton = {
        TextButton(onClick = { showInviteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
