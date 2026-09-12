package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChallengeCategory
import com.example.ui.theme.StreakFire

@Composable
fun CategoryChip(
  category: ChallengeCategory,
  isSelected: Boolean = false,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(20.dp)
  val baseModifier = if (onClick != null) {
    modifier
      .clip(shape)
      .clickable(onClick = onClick)
      .testTag("category_chip_${category.name.lowercase()}")
  } else {
    modifier.clip(shape)
  }

  Surface(
    modifier = baseModifier,
    shape = shape,
    color = if (isSelected) category.color else category.color.copy(alpha = 0.12f),
    contentColor = if (isSelected) Color.White else category.color
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(text = category.icon, fontSize = 14.sp)
      Text(
        text = category.displayName,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )
      )
    }
  }
}

@Composable
fun StreakFlameBadge(
  streakDays: Int,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.clip(RoundedCornerShape(16.dp)),
    shape = RoundedCornerShape(16.dp),
    color = StreakFire.copy(alpha = 0.12f),
    contentColor = StreakFire
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.LocalFireDepartment,
        contentDescription = "Streak flame",
        tint = StreakFire,
        modifier = Modifier.size(18.dp)
      )
      Text(
        text = "$streakDays Days",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = StreakFire
        )
      )
    }
  }
}

@Composable
fun CircularCountdownTimer(
  remainingSeconds: Int,
  totalSeconds: Int = 60,
  isRunning: Boolean,
  size: Dp = 160.dp,
  strokeWidth: Dp = 12.dp,
  modifier: Modifier = Modifier
) {
  val progress = (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 500, easing = LinearEasing),
    label = "timer_progress"
  )

  val timerColor by animateColorAsState(
    targetValue = when {
      remainingSeconds > 30 -> MaterialTheme.colorScheme.primary
      remainingSeconds > 15 -> Color(0xFFF59E0B)
      else -> Color(0xFFFF4842)
    },
    label = "timer_color"
  )

  Box(
    modifier = modifier
      .size(size)
      .testTag("circular_countdown_timer"),
    contentAlignment = Alignment.Center
  ) {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
      // Background track
      drawArc(
        color = timerColor.copy(alpha = 0.15f),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
      )
      // Animated active arc
      drawArc(
        color = timerColor,
        startAngle = -90f,
        sweepAngle = 360f * animatedProgress,
        useCenter = false,
        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = "${remainingSeconds}s",
        style = MaterialTheme.typography.displaySmall.copy(
          fontWeight = FontWeight.Black,
          color = timerColor
        )
      )
      Text(
        text = if (isRunning) "KEEP GOING!" else "PAUSED",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
    }
  }
}

@Composable
fun AvatarBubble(
  name: String,
  emoji: String? = null,
  backgroundColor: Color = MaterialTheme.colorScheme.primary,
  size: Dp = 40.dp,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(backgroundColor),
    contentAlignment = Alignment.Center
  ) {
    if (emoji != null && emoji.isNotBlank()) {
      Text(text = emoji, fontSize = (size.value * 0.48f).sp)
    } else {
      Text(
        text = name.take(1).uppercase(),
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
    }
  }
}
