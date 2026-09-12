package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ChallengeEntity
import com.example.data.model.ChallengeCategory
import com.example.ui.components.CategoryChip
import com.example.ui.viewmodel.OneMinuteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
  viewModel: OneMinuteViewModel,
  onOpenChallenge: (ChallengeEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val allChallenges by viewModel.allChallenges.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

  var showSuggestDialog by remember { mutableStateOf(false) }
  var suggestedTitle by remember { mutableStateOf("") }
  var suggestedDesc by remember { mutableStateOf("") }

  val filteredChallenges = allChallenges.filter { challenge ->
    val matchesCategory = selectedCategory == null || challenge.category.equals(selectedCategory?.name, ignoreCase = true)
    val matchesQuery = searchQuery.isBlank() ||
      challenge.title.contains(searchQuery, ignoreCase = true) ||
      challenge.description.contains(searchQuery, ignoreCase = true) ||
      challenge.category.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesQuery
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Discover Challenges",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
          )
        },
        actions = {
          FilledTonalButton(
            onClick = { showSuggestDialog = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Submit 60s Idea", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        .testTag("explore_screen"),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

      // SEARCH BAR
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("challenge_search_field"),
          placeholder = { Text("Search 60-second challenges...") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
          ),
          singleLine = true
        )
      }

      // CATEGORY FILTER TABS
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          FilterChip(
            selected = selectedCategory == null,
            onClick = { viewModel.selectCategoryFilter(null) },
            label = { Text("All Packs", fontWeight = FontWeight.Bold) },
            shape = RoundedCornerShape(16.dp)
          )
        }

        items(ChallengeCategory.entries.toTypedArray()) { cat ->
          CategoryChip(
            category = cat,
            isSelected = selectedCategory == cat,
            onClick = {
              if (selectedCategory == cat) {
                viewModel.selectCategoryFilter(null)
              } else {
                viewModel.selectCategoryFilter(cat)
              }
            }
          )
        }
      }

      // RESULTS LIST
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(filteredChallenges) { challenge ->
          val cat = ChallengeCategory.fromString(challenge.category)

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenChallenge(challenge) }
              .testTag("challenge_item_${challenge.id}"),
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
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(cat.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(cat.icon, fontSize = 16.sp)
                  }
                  Text(
                    cat.displayName,
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = cat.color
                    )
                  )
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                  ) {
                    Text(
                      text = challenge.targetAudience,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                    )
                  }

                  Text(
                    text = "⏱️ ${challenge.timeLimitSeconds}s",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }

              Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )

              Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🔥 ${challenge.popularityCount} completed",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                  onClick = { onOpenChallenge(challenge) },
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = cat.color),
                  contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                  Text("Try 60s Challenge", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }

    }
  }

  // SUGGEST NEW CHALLENGE DIALOG
  if (showSuggestDialog) {
    AlertDialog(
      onDismissRequest = { showSuggestDialog = false },
      title = { Text("Suggest a 60-Second Challenge 💡") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            "Have a fun, safe prompt idea for families and friends? Propose it here!",
            style = MaterialTheme.typography.bodySmall
          )
          OutlinedTextField(
            value = suggestedTitle,
            onValueChange = { suggestedTitle = it },
            label = { Text("Challenge Title") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = suggestedDesc,
            onValueChange = { suggestedDesc = it },
            label = { Text("What to do in 60s?") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showSuggestDialog = false
            suggestedTitle = ""
            suggestedDesc = ""
          }
        ) {
          Text("Submit Idea")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSuggestDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
