package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.FamilyResponseEntity
import com.example.data.model.ResponseFormat
import com.example.ui.components.AvatarBubble
import com.example.ui.theme.*
import com.example.ui.viewmodel.OneMinuteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyCircleScreen(
  viewModel: OneMinuteViewModel,
  onNavigateBack: () -> Unit,
  onOpenMemoryBook: () -> Unit,
  modifier: Modifier = Modifier
) {
  val familyCircle by viewModel.primaryFamilyCircle.collectAsStateWithLifecycle()
  val familyResponses by viewModel.familyResponses.collectAsStateWithLifecycle()

  var showAddResponseDialog by remember { mutableStateOf(false) }

  // New family response form state
  var selectedMemberRole by remember { mutableStateOf("Alex (You)") }
  var selectedAvatarEmoji by remember { mutableStateOf("⚡") }
  var responseText by remember { mutableStateOf("") }

  val memberRoles = listOf(
    Pair("Alex (You)", "⚡"),
    Pair("Mom (Sarah)", "👩‍🍳"),
    Pair("Dad (David)", "👨‍🔧"),
    Pair("Maya (Teen)", "👧"),
    Pair("Leo (Kid)", "👦"),
    Pair("Grandma", "👵")
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            familyCircle?.circleName ?: "Family Circle",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          FilledTonalButton(
            onClick = onOpenMemoryBook,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .padding(end = 8.dp)
              .testTag("open_memory_book_top_button")
          ) {
            Text("Memory Book 📖", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showAddResponseDialog = true },
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text("Answer Daily Prompt") },
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.testTag("add_family_response_fab")
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // CIRCLE HEADER & JOIN CODE CARD
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = AmberTertiaryContainerLight.copy(alpha = 0.5f)
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
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text("🏡", fontSize = 20.sp)
                Text(
                  "Private Family Circle",
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = AmberOnTertiaryContainerLight
                  )
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface
              ) {
                Text(
                  text = "CODE: ${familyCircle?.joinCode ?: "FAMILY-STARS"}",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }
            }

            Text(
              text = "A safe, intimate space where kids, teens, and parents share 60-second moments and compile monthly family albums.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Family Avatars Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              memberRoles.take(5).forEach { (role, emoji) ->
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                  AvatarBubble(name = role, emoji = emoji, size = 38.dp)
                  Text(
                    text = role.split(" ").first(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                  )
                }
              }
            }
          }
        }
      }

      // MEMORY BOOK PROMOTIONAL BANNER
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenMemoryBook)
            .testTag("memory_book_banner_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  listOf(CategoryMemories.copy(alpha = 0.15f), MaterialTheme.colorScheme.surface)
                )
              )
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CategoryMemories.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text("📖", fontSize = 26.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                "September 2026 Memory Book",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
              )
              Text(
                "Compiled automatically from daily family answers, photos & doodles",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = "Open",
              tint = CategoryMemories
            )
          }
        }
      }

      // FAMILY MOMENTS TIMELINE HEADER
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "Recent Family Moments",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )

          Text(
            "${familyResponses.size} Entries",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      // FAMILY RESPONSES LIST
      items(familyResponses) { response ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                AvatarBubble(
                  name = response.authorName,
                  emoji = response.avatarEmoji,
                  size = 40.dp
                )
                Column {
                  Text(
                    text = response.authorName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = response.authorRole,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CoralSecondaryLight.copy(alpha = 0.12f)
              ) {
                Text(
                  text = response.stickerTag,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = CoralSecondaryLight
                  )
                )
              }
            }

            Text(
              text = "Q: ${response.challengeTitle}",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )

            Text(
              text = response.textContent,
              style = MaterialTheme.typography.bodyMedium
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = response.monthYear,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text("❤️", fontSize = 12.sp)
                  Text(
                    text = "${response.reactionsCount}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(60.dp))
      }

    }
  }

  // DIALOG TO ADD FAMILY RESPONSE
  if (showAddResponseDialog) {
    AlertDialog(
      onDismissRequest = { showAddResponseDialog = false },
      title = { Text("Add Family Response 🏡") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Who is answering?", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            memberRoles.take(3).forEach { (role, emoji) ->
              val isSelected = selectedMemberRole == role
              FilterChip(
                selected = isSelected,
                onClick = {
                  selectedMemberRole = role
                  selectedAvatarEmoji = emoji
                },
                label = { Text("$emoji ${role.split(" ").first()}", fontSize = 11.sp) }
              )
            }
          }

          OutlinedTextField(
            value = responseText,
            onValueChange = { responseText = it },
            label = { Text("Your answer or story") },
            placeholder = { Text("Share what happened today...") },
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp),
            shape = RoundedCornerShape(12.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (responseText.isNotBlank()) {
              showAddResponseDialog = false
              viewModel.submitFamilyResponse(
                authorName = selectedMemberRole,
                authorRole = selectedMemberRole,
                avatarEmoji = selectedAvatarEmoji,
                content = responseText,
                format = ResponseFormat.TEXT
              )
              responseText = ""
            }
          },
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Save to Memory Book")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddResponseDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
