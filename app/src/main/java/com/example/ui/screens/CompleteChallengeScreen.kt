package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.CompletionEntity
import com.example.data.model.ChallengeCategory
import com.example.data.model.ResponseFormat
import com.example.ui.components.CategoryChip
import com.example.ui.components.CircularCountdownTimer
import com.example.ui.theme.*
import com.example.ui.viewmodel.DrawStroke
import com.example.ui.viewmodel.OneMinuteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteChallengeScreen(
  viewModel: OneMinuteViewModel,
  onNavigateBack: () -> Unit,
  onChallengeCompleted: (CompletionEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val challenge by viewModel.activeChallenge.collectAsStateWithLifecycle()
  val remainingSeconds by viewModel.timerSecondsRemaining.collectAsStateWithLifecycle()
  val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
  val responseFormat by viewModel.responseFormat.collectAsStateWithLifecycle()
  val textResponse by viewModel.textResponse.collectAsStateWithLifecycle()
  val photoUri by viewModel.photoUri.collectAsStateWithLifecycle()
  val drawingStrokes by viewModel.drawingStrokes.collectAsStateWithLifecycle()
  val selectedColor by viewModel.selectedDrawingColor.collectAsStateWithLifecycle()
  val selectedStrokeWidth by viewModel.selectedStrokeWidth.collectAsStateWithLifecycle()
  val selectedQuickChoice by viewModel.selectedQuickChoice.collectAsStateWithLifecycle()

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      viewModel.setPhotoUri(uri.toString())
    }
  }

  // Trigger light haptic when timer finishes or starts
  fun vibrateLight() {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    vibrator?.let {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        it.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        it.vibrate(40)
      }
    }
  }

  val activeChallenge = challenge ?: return

  val category = ChallengeCategory.fromString(activeChallenge.category)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "60s Challenge",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        },
        actions = {
          CategoryChip(category = category, modifier = Modifier.padding(end = 12.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
      ) {
        Box(modifier = Modifier.padding(16.dp)) {
          Button(
            onClick = {
              vibrateLight()
              viewModel.submitCurrentChallenge { completion ->
                onChallengeCompleted(completion)
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("submit_challenge_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = category.color
            )
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(imageVector = Icons.Default.Check, contentDescription = "Complete")
              Text(
                "Complete & Generate Share Card ⚡",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }
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
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

      // CHALLENGE TITLE & DESCRIPTION CARD
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = activeChallenge.title,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              fontSize = 20.sp
            )
          )
          Text(
            text = activeChallenge.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // 60-SECOND CIRCULAR COUNTDOWN TIMER & CONTROLS
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          CircularCountdownTimer(
            remainingSeconds = remainingSeconds,
            totalSeconds = activeChallenge.timeLimitSeconds,
            isRunning = isTimerRunning
          )

          // Timer Controller Buttons
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            FilledTonalButton(
              onClick = {
                vibrateLight()
                if (isTimerRunning) viewModel.pauseTimer() else viewModel.startTimer()
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("toggle_timer_button")
            ) {
              Icon(
                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isTimerRunning) "Pause" else "Start"
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                if (isTimerRunning) "Pause Timer" else "Start 60s",
                fontWeight = FontWeight.Bold
              )
            }

            IconButton(
              onClick = {
                vibrateLight()
                viewModel.resetTimer()
              },
              modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Timer")
            }
          }

          // Micro-Encouragement Message
          Text(
            text = when {
              remainingSeconds == 60 -> "Hit start and let your 60-second timer spark!"
              remainingSeconds > 30 -> "Great momentum! Keep the speed alive ⚡"
              remainingSeconds > 10 -> "Only ${remainingSeconds}s left! You got this!"
              remainingSeconds > 0 -> "Final sprint! Finish strong! 🚀"
              else -> "Time's up! Phenomenal effort!"
            },
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color = if (remainingSeconds <= 10) CoralSecondaryLight else MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
          )
        }
      }

      // RESPONSE FORMAT SELECTOR TABS
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          "How will you answer?",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ResponseFormat.entries.forEach { format ->
            val isSelected = responseFormat == format
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.setResponseFormat(format) },
              label = { Text(format.label, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
              leadingIcon = {
                when (format) {
                  ResponseFormat.TEXT -> Icon(Icons.Outlined.Edit, contentDescription = null, Modifier.size(16.dp))
                  ResponseFormat.PHOTO -> Icon(Icons.Outlined.CameraAlt, contentDescription = null, Modifier.size(16.dp))
                  ResponseFormat.DRAWING -> Icon(Icons.Outlined.Brush, contentDescription = null, Modifier.size(16.dp))
                  ResponseFormat.QUICK_CHOICE -> Icon(Icons.Outlined.FormatListBulleted, contentDescription = null, Modifier.size(16.dp))
                }
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      // ACTIVE RESPONSE INPUT AREA
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          when (responseFormat) {
            ResponseFormat.TEXT -> {
              Text(
                "Write your 60-second response",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )

              OutlinedTextField(
                value = textResponse,
                onValueChange = { viewModel.setTextResponse(it) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(130.dp)
                  .testTag("text_response_field"),
                placeholder = { Text("Express your answer here in 60 seconds...") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = category.color,
                  unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
              )

              // Quick Emoji Sentiment Bar
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  "Quick Reactions:",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  listOf("😂", "🔥", "✨", "❤️", "⚡", "🌮").forEach { emoji ->
                    Surface(
                      shape = CircleShape,
                      color = MaterialTheme.colorScheme.surfaceVariant,
                      modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable {
                          viewModel.setTextResponse(if (textResponse.isBlank()) emoji else "$textResponse $emoji")
                        }
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Text(emoji, fontSize = 16.sp)
                      }
                    }
                  }
                }
              }
            }

            ResponseFormat.PHOTO -> {
              Text(
                "Add a photo for this challenge",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )

              if (photoUri != null) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
                    .border(2.dp, category.color, RoundedCornerShape(16.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = "Photo attached",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(40.dp)
                    )
                    Text("Photo attached successfully!", fontWeight = FontWeight.Bold)
                    TextButton(onClick = { viewModel.setPhotoUri(null) }) {
                      Text("Remove photo", color = CoralSecondaryLight)
                    }
                  }
                }
              } else {
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Button(
                    onClick = {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                  ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select from Gallery")
                  }

                  // Simulated quick camera capture option
                  OutlinedButton(
                    onClick = {
                      viewModel.setPhotoUri("simulated://scavenger_snap_yellow")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                  ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Snap Photo with Camera")
                  }
                }
              }
            }

            ResponseFormat.DRAWING -> {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    "60s Doodle Canvas",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )

                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = { viewModel.undoDrawingStroke() }) {
                      Text("Undo")
                    }
                    TextButton(onClick = { viewModel.clearDrawing() }) {
                      Text("Clear", color = CoralSecondaryLight)
                    }
                  }
                }

                // Interactive Canvas
                var currentPoints by remember { mutableStateOf<List<Pair<Float, Float>>>(emptyList()) }

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .pointerInput(selectedColor, selectedStrokeWidth) {
                      detectDragGestures(
                        onDragStart = { offset ->
                          currentPoints = listOf(Pair(offset.x, offset.y))
                        },
                        onDrag = { change, _ ->
                          change.consume()
                          currentPoints = currentPoints + Pair(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                          if (currentPoints.isNotEmpty()) {
                            viewModel.addDrawingStroke(
                              DrawStroke(currentPoints, selectedColor, selectedStrokeWidth)
                            )
                            currentPoints = emptyList()
                          }
                        }
                      )
                    }
                ) {
                  Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw completed strokes
                    drawingStrokes.forEach { stroke ->
                      if (stroke.points.size > 1) {
                        val path = Path().apply {
                          moveTo(stroke.points[0].first, stroke.points[0].second)
                          for (i in 1 until stroke.points.size) {
                            lineTo(stroke.points[i].first, stroke.points[i].second)
                          }
                        }
                        drawPath(
                          path = path,
                          color = stroke.color,
                          style = Stroke(
                            width = stroke.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                          )
                        )
                      }
                    }

                    // Draw active dragging stroke
                    if (currentPoints.size > 1) {
                      val activePath = Path().apply {
                        moveTo(currentPoints[0].first, currentPoints[0].second)
                        for (i in 1 until currentPoints.size) {
                          lineTo(currentPoints[i].first, currentPoints[i].second)
                        }
                      }
                      drawPath(
                        path = activePath,
                        color = selectedColor,
                        style = Stroke(
                          width = selectedStrokeWidth,
                          cap = StrokeCap.Round,
                          join = StrokeJoin.Round
                        )
                      )
                    }
                  }

                  if (drawingStrokes.isEmpty() && currentPoints.isEmpty()) {
                    Box(
                      modifier = Modifier.fillMaxSize(),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        "Touch & drag to sketch your 60-second masterpiece! 🎨",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                      )
                    }
                  }
                }

                // Palette & Brush Size Selector
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Color dots
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val colors = listOf(
                      Color(0xFF4F46E5),
                      Color(0xFFFF6B6B),
                      Color(0xFF10B981),
                      Color(0xFFF59E0B),
                      Color(0xFFEC4899),
                      Color(0xFF1E293B)
                    )
                    colors.forEach { color ->
                      Box(
                        modifier = Modifier
                          .size(28.dp)
                          .clip(CircleShape)
                          .background(color)
                          .border(
                            width = if (selectedColor == color) 3.dp else 1.dp,
                            color = if (selectedColor == color) Color.White else Color.Transparent,
                            shape = CircleShape
                          )
                          .clickable { viewModel.setSelectedDrawingColor(color) }
                      )
                    }
                  }

                  // Stroke width toggle
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(4f to "Thin", 8f to "Med", 14f to "Bold").forEach { (width, label) ->
                      FilterChip(
                        selected = selectedStrokeWidth == width,
                        onClick = { viewModel.setSelectedStrokeWidth(width) },
                        label = { Text(label, fontSize = 10.sp) }
                      )
                    }
                  }
                }
              }
            }

            ResponseFormat.QUICK_CHOICE -> {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                  "Make your pick in 60 seconds:",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                val options = listOf(
                  "Option A: Absolutely YES without a doubt!",
                  "Option B: Only if my best friend does it first 😂",
                  "Option C: Ask me again next Sunday",
                  "Option D: Secret fourth option!"
                )

                options.forEach { option ->
                  val isSelected = selectedQuickChoice == option
                  Surface(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(12.dp))
                      .clickable { viewModel.setSelectedQuickChoice(option) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) category.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, category.color) else null
                  ) {
                    Row(
                      modifier = Modifier.padding(14.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedQuickChoice(option) }
                      )
                      Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
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
  }
}
