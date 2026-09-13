package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ChallengeEntity
import com.example.data.model.ChallengeCategory
import com.example.ui.components.CategoryChip
import com.example.ui.components.SnapkHeader
import com.example.ui.components.StreakFlameBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.OneMinuteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: OneMinuteViewModel,
  onOpenChallenge: (ChallengeEntity) -> Unit,
  onNavigateToChains: () -> Unit,
  onNavigateToFamily: () -> Unit,
  onNavigateToNotifications: () -> Unit,
  onNavigateToCategoryExplore: (ChallengeCategory) -> Unit,
  modifier: Modifier = Modifier
) {
  val dailyChallenge by viewModel.dailyChallenge.collectAsStateWithLifecycle()
  val allChallenges by viewModel.allChallenges.collectAsStateWithLifecycle()
  val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()
  val chains by viewModel.chains.collectAsStateWithLifecycle()
  val unreadNotifications by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
  val familyCircle by viewModel.primaryFamilyCircle.collectAsStateWithLifecycle()
  val familyResponses by viewModel.familyResponses.collectAsStateWithLifecycle()

  val activeChain = chains.firstOrNull()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          SnapkHeader(
            iconSize = 36.dp,
            wordmarkSize = 20.sp,
            showBadge = true,
            badgeText = "60s"
          )
        },
        actions = {
          StreakFlameBadge(streakDays = streakDays, modifier = Modifier.padding(end = 6.dp))
          
          IconButton(
            onClick = onNavigateToNotifications,
            modifier = Modifier.testTag("notifications_button")
          ) {
            BadgedBox(
              badge = {
                if (unreadNotifications > 0) {
                  Badge { Text("$unreadNotifications") }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications"
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

      // TODAY'S 60-SECOND CHALLENGE HERO CARD
      item {
        dailyChallenge?.let { challenge ->
          val category = ChallengeCategory.fromString(challenge.category)

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .testTag("daily_challenge_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      category.color.copy(alpha = 0.12f),
                      MaterialTheme.colorScheme.surface
                    )
                  )
                )
                .padding(20.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    CategoryChip(category = category)
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                      Text(
                        text = "⚡ TODAY'S DROP",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.primary
                        )
                      )
                    }
                  }

                  Text(
                    text = "⏱️ 60s",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }

                Text(
                  text = challenge.title,
                  style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                  ),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                  text = challenge.description,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.TipsAndUpdates,
                    contentDescription = "Tips",
                    tint = AmberTertiaryLight,
                    modifier = Modifier.size(18.dp)
                  )
                  Text(
                    text = challenge.tips,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
                }

                Button(
                  onClick = { onOpenChallenge(challenge) },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_daily_challenge_button"),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = category.color
                  )
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
                    Text(
                      "Start 60-Second Challenge",
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                      )
                    )
                  }
                }
              }
            }
          }
        }
      }

      // CATEGORIES ROW
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "Explore Categories",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "10 Packs",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.primary
            )
          }

          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(ChallengeCategory.entries.toTypedArray()) { cat ->
              CategoryChip(
                category = cat,
                onClick = { onNavigateToCategoryExplore(cat) }
              )
            }
          }
        }
      }

      // VIRAL CHALLENGE CHAIN SPOTLIGHT CARD
      item {
        if (activeChain != null) {
          val chain = activeChain
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .clip(RoundedCornerShape(20.dp))
              .clickable(onClick = onNavigateToChains)
              .testTag("chain_spotlight_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = IndigoPrimaryContainerLight.copy(alpha = 0.45f)
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
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                  ) {
                    Icon(
                      imageVector = Icons.Default.Share,
                      contentDescription = "Chain",
                      tint = Color.White,
                      modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                    )
                  }
                  Text(
                    "VIRAL CHALLENGE CHAIN",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = MaterialTheme.colorScheme.primary,
                      letterSpacing = 0.5.sp
                    )
                  )
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = CoralSecondaryLight.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = chain.milestoneReached,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = CoralSecondaryLight
                    )
                  )
                }
              }

              Text(
                text = "'${chain.challengeTitle}'",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              // Chain stats pills
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surface
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text("👥", fontSize = 12.sp)
                    Text(
                      "${chain.totalParticipants} Friends Active",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surface
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text("📍", fontSize = 12.sp)
                    Text(
                      "${chain.citiesReached} Cities Reached",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                  }
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Invite code: ${chain.inviteCode}",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
                TextButton(onClick = onNavigateToChains) {
                  Text(
                    "View Chain Tree →",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        } else {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .clip(RoundedCornerShape(20.dp))
              .clickable(onClick = onNavigateToChains)
              .testTag("chain_spotlight_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = IndigoPrimaryContainerLight.copy(alpha = 0.45f)
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
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                  ) {
                    Icon(
                      imageVector = Icons.Default.Share,
                      contentDescription = "Chain",
                      tint = Color.White,
                      modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                    )
                  }
                  Text(
                    "VIRAL CHALLENGE CHAIN",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = MaterialTheme.colorScheme.primary,
                      letterSpacing = 0.5.sp
                    )
                  )
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = CoralSecondaryLight.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "Ready to Launch 🚀",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = CoralSecondaryLight
                    )
                  )
                }
              }

              Text(
                text = "Challenge 3 Friends to Multiply!",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold
                )
              )

              Text(
                text = "Complete today's 60-second challenge, invite 3 friends, and watch your chain spread across cities!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              // Chain stats pills
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surface
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text("👥", fontSize = 12.sp)
                    Text(
                      "0 Friends Active",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surface
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text("📍", fontSize = 12.sp)
                    Text(
                      "0 Cities Reached",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                  }
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Pass it forward",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
                TextButton(onClick = onNavigateToChains) {
                  Text(
                    "Launch Chain →",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }
      }

      // PRIVATE FAMILY CIRCLE HIGHLIGHT CARD
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onNavigateToFamily)
            .testTag("family_circle_highlight_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = AmberTertiaryContainerLight.copy(alpha = 0.45f)
          )
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text("🏡", fontSize = 18.sp)
                Text(
                  familyCircle?.circleName ?: "Family Circle",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AmberOnTertiaryContainerLight
                  )
                )
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface
              ) {
                Text(
                  text = "${familyResponses.size} Memories",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AmberTertiaryLight
                  )
                )
              }
            }

            Text(
              text = "Answer daily questions together and compile the monthly Family Memory Book.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Latest family response preview
            familyResponses.firstOrNull()?.let { latest ->
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Text(latest.avatarEmoji, fontSize = 24.sp)
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      "${latest.authorName} (${latest.authorRole})",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      latest.textContent,
                      style = MaterialTheme.typography.bodySmall,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                  Text(
                    latest.stickerTag,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                  )
                }
              }
            }

            OutlinedButton(
              onClick = onNavigateToFamily,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Open Monthly Memory Book 📖", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // QUICK SPRINT RECOMMENDATIONS
      item {
        Column(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            "Quick 60s Challenges",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )

          allChallenges.filter { !it.isDaily }.take(3).forEach { challenge ->
            val cat = ChallengeCategory.fromString(challenge.category)
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenChallenge(challenge) },
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
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cat.color.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(cat.icon, fontSize = 22.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }

                IconButton(onClick = { onOpenChallenge(challenge) }) {
                  Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go",
                    tint = cat.color
                  )
                }
              }
            }
          }
        }
      }

    }
  }
}
