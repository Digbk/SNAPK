package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AvatarBubble
import com.example.ui.theme.*
import com.example.ui.viewmodel.OneMinuteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryBookScreen(
  viewModel: OneMinuteViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val familyCircle by viewModel.primaryFamilyCircle.collectAsStateWithLifecycle()
  val availableMonths by viewModel.availableMemoryMonths.collectAsStateWithLifecycle()
  val selectedMonth by viewModel.selectedMemoryMonth.collectAsStateWithLifecycle()
  val responses by viewModel.familyResponses.collectAsStateWithLifecycle()

  val filteredResponses = responses.filter { it.monthYear == selectedMonth }

  fun exportMemoryAlbum() {
    val exportText = """
      📖 ${familyCircle?.circleName ?: "Family Circle"} — $selectedMonth Memory Book
      Compiled from 60-Second Daily Family Challenges!
      
      Moments recorded: ${filteredResponses.size}
      
      ${filteredResponses.joinToString("\n\n") { "• ${it.authorName} (${it.authorRole}): \"${it.textContent}\"\n  [${it.stickerTag}]" }}
      
      Created with SNAPK App.
    """.trimIndent()

    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, exportText)
      type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Memory Book"))
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
              "Monthly Memory Book",
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
          IconButton(onClick = { exportMemoryAlbum() }) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Export Album")
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
        .testTag("memory_book_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // MONTH PICKER ROW
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            "Select Edition:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("September 2026", "August 2026", "July 2026")) { month ->
              val isSelected = selectedMonth == month
              FilterChip(
                selected = isSelected,
                onClick = { viewModel.setSelectedMemoryMonth(month) },
                label = { Text(month, fontWeight = FontWeight.Bold) },
                leadingIcon = {
                  if (isSelected) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, Modifier.size(16.dp))
                  }
                },
                shape = RoundedCornerShape(12.dp)
              )
            }
          }
        }
      }

      // VINTAGE SCRAPBOOK ALBUM COVER
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.linearGradient(
                  listOf(
                    Color(0xFFFFFBEB),
                    Color(0xFFFEF3C7),
                    Color(0xFFFDE68A)
                  )
                )
              )
              .padding(24.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text("📔", fontSize = 42.sp)
              Text(
                text = "${familyCircle?.circleName ?: "Family"} Memory Book",
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF78350F)
                ),
                textAlign = TextAlign.Center
              )
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFB45309)
              ) {
                Text(
                  text = selectedMonth.uppercase(),
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                  )
                )
              }
              Text(
                text = "Preserved memories, spontaneous laughter, and heartwarming daily answers.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF92400E)
                ),
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }

      // SUMMARY STATS
      item {
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
                "${filteredResponses.size}",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
              )
              Text("Moments", style = MaterialTheme.typography.labelSmall)
            }
            Divider(modifier = Modifier.height(28.dp).width(1.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                "${filteredResponses.count { it.responseFormat == "DRAWING" }}",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = CoralSecondaryLight
              )
              Text("Doodles", style = MaterialTheme.typography.labelSmall)
            }
            Divider(modifier = Modifier.height(28.dp).width(1.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                "${filteredResponses.sumOf { it.reactionsCount }}",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = AmberTertiaryLight
              )
              Text("Family Hearts", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }

      // SCRAPBOOK PAGES (POLAROID MEMORY CARDS)
      items(filteredResponses) { entry ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            // Washi Tape Visual Accent
            Box(
              modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(100.dp)
                .height(14.dp)
                .background(
                  Brush.horizontalGradient(
                    listOf(CoralSecondaryLight.copy(alpha = 0.5f), AmberTertiaryLight.copy(alpha = 0.5f))
                  )
                )
            )

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
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  AvatarBubble(
                    name = entry.authorName,
                    emoji = entry.avatarEmoji,
                    size = 40.dp
                  )
                  Column {
                    Text(
                      entry.authorName,
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      entry.authorRole,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = CoralSecondaryLight.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = entry.stickerTag,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = CoralSecondaryLight
                    )
                  )
                }
              }

              // Question Prompt
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Text(
                  text = "Prompt: ${entry.challengeTitle}",
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }

              // Content snippet
              if (entry.responseFormat == "DRAWING") {
                Surface(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                  shape = RoundedCornerShape(14.dp),
                  color = Color.White,
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                  Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                  ) {
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text("🎨", fontSize = 32.sp)
                      Text(
                        entry.textContent,
                        style = MaterialTheme.typography.bodySmall.copy(
                          fontWeight = FontWeight.Medium,
                          color = Color(0xFF334155)
                        ),
                        textAlign = TextAlign.Center
                      )
                    }
                  }
                }
              } else {
                Text(
                  text = "\"${entry.textContent}\"",
                  style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                  )
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Saved in $selectedMonth",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text("❤️", fontSize = 14.sp)
                  Text(
                    "${entry.reactionsCount} hearts",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }
      }

      // EXPORT ACTION BUTTON
      item {
        Button(
          onClick = { exportMemoryAlbum() },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = AmberTertiaryLight
          )
        ) {
          Icon(Icons.Default.Share, contentDescription = null)
          Spacer(Modifier.width(8.dp))
          Text(
            "Export & Share $selectedMonth Album",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

    }
  }
}
