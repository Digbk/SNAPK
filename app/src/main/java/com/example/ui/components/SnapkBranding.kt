package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SnapkDark
import com.example.ui.theme.SnapkYellow

/**
 * Custom Canvas-rendered SNAPK Logo Icon matching the user's branding:
 * Black squircle with the yellow stylized geometric "SN" monogram and 3 radiant spark rays.
 */
@Composable
fun SnapkLogoIcon(
  modifier: Modifier = Modifier,
  size: Dp = 42.dp,
  cornerRadius: Dp = 12.dp,
  animateSparks: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "snapk_sparks")
  val sparkPulse by if (animateSparks) {
    infiniteTransition.animateFloat(
      initialValue = 0.85f,
      targetValue = 1.15f,
      animationSpec = infiniteRepeatable(
        animation = tween(1200, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "spark_pulse"
    )
  } else {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
  }

  Surface(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(cornerRadius))
      .shadow(4.dp, RoundedCornerShape(cornerRadius))
      .testTag("snapk_logo_icon"),
    color = SnapkDark,
    shape = RoundedCornerShape(cornerRadius)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = this.size.width
      val h = this.size.height
      val scale = w / 100f

      // Draw background rounded squircle inside canvas
      drawRoundRect(
        color = SnapkDark,
        topLeft = Offset(0f, 0f),
        size = Size(w, h),
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
      )

      // Spark lines at top right of the "N" stem
      val strokeW = 4.2f * scale
      val sparkColor = SnapkYellow

      // Spark 1 (near vertical, ~75 deg)
      val p1Start = Offset(58f * scale, 28f * scale)
      val p1End = Offset(57f * scale, (28f - 11f * sparkPulse) * scale)
      drawLine(
        color = sparkColor,
        start = p1Start,
        end = p1End,
        strokeWidth = strokeW,
        cap = StrokeCap.Round
      )

      // Spark 2 (diagonal, ~45 deg)
      val p2Start = Offset(65f * scale, 31f * scale)
      val p2End = Offset((65f + 9f * sparkPulse) * scale, (31f - 9f * sparkPulse) * scale)
      drawLine(
        color = sparkColor,
        start = p2Start,
        end = p2End,
        strokeWidth = strokeW * 1.05f,
        cap = StrokeCap.Round
      )

      // Spark 3 (horizontal-ish, ~20 deg)
      val p3Start = Offset(68f * scale, 37f * scale)
      val p3End = Offset((68f + 10f * sparkPulse) * scale, (37f - 4f * sparkPulse) * scale)
      drawLine(
        color = sparkColor,
        start = p3Start,
        end = p3End,
        strokeWidth = strokeW,
        cap = StrokeCap.Round
      )

      // Main Stylized "SN" Monogram
      val ribbonStroke = 7.8f * scale

      // Upper S curve / ribbon
      val upperS = Path().apply {
        moveTo(59f * scale, 42f * scale)
        lineTo(43f * scale, 31f * scale)
        cubicTo(
          37f * scale, 27f * scale,
          27f * scale, 30f * scale,
          26f * scale, 37f * scale
        )
        cubicTo(
          25f * scale, 44f * scale,
          30f * scale, 49f * scale,
          38f * scale, 54f * scale
        )
        lineTo(49f * scale, 60f * scale)
        cubicTo(
          53f * scale, 63f * scale,
          53f * scale, 66f * scale,
          51f * scale, 69f * scale
        )
        cubicTo(
          49f * scale, 71f * scale,
          45f * scale, 72f * scale,
          40f * scale, 71f * scale
        )
        lineTo(33f * scale, 68f * scale)
      }

      drawPath(
        path = upperS,
        color = SnapkYellow,
        style = Stroke(
          width = ribbonStroke,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // Lower S hook transitioning up into N upright stem
      val lowerSAndN = Path().apply {
        moveTo(27f * scale, 56f * scale)
        cubicTo(
          25f * scale, 63f * scale,
          29f * scale, 69f * scale,
          36f * scale, 71f * scale
        )
        cubicTo(
          42f * scale, 72f * scale,
          47f * scale, 68f * scale,
          51f * scale, 63f * scale
        )
        lineTo(64f * scale, 50f * scale)
        lineTo(64f * scale, 71f * scale)
      }

      drawPath(
        path = lowerSAndN,
        color = SnapkYellow,
        style = Stroke(
          width = ribbonStroke,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }
  }
}

/**
 * The SNAPK Wordmark with the signature yellow triangle inside the letter "A".
 */
@Composable
fun SnapkWordmark(
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 24.sp,
  textColor: Color = MaterialTheme.colorScheme.onBackground
) {
  Row(
    modifier = modifier.testTag("snapk_wordmark"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(1.dp)
  ) {
    Text(
      text = "SN",
      style = MaterialTheme.typography.titleLarge.copy(
        fontSize = fontSize,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.SansSerif,
        letterSpacing = 1.sp,
        color = textColor
      )
    )

    // The Letter "A" with iconic yellow triangle cutout
    Box(
      contentAlignment = Alignment.BottomCenter,
      modifier = Modifier
        .wrapContentSize()
        .padding(horizontal = 0.5.dp)
    ) {
      Text(
        text = "A",
        style = MaterialTheme.typography.titleLarge.copy(
          fontSize = fontSize,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.SansSerif,
          letterSpacing = 1.sp,
          color = textColor
        )
      )

      // Solid yellow triangle inside the A arch
      Canvas(
        modifier = Modifier
          .size(
            width = (fontSize.value * 0.36f).dp,
            height = (fontSize.value * 0.32f).dp
          )
          .padding(bottom = (fontSize.value * 0.12f).dp)
      ) {
        val path = Path().apply {
          moveTo(size.width / 2f, 0f)
          lineTo(size.width, size.height)
          lineTo(0f, size.height)
          close()
        }
        drawPath(path = path, color = SnapkYellow)
      }
    }

    Text(
      text = "PK",
      style = MaterialTheme.typography.titleLarge.copy(
        fontSize = fontSize,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.SansSerif,
        letterSpacing = 1.sp,
        color = textColor
      )
    )
  }
}

/**
 * Full branded header bar combining the SNAPK logo icon, wordmark, and optional badge.
 */
@Composable
fun SnapkHeader(
  modifier: Modifier = Modifier,
  iconSize: Dp = 38.dp,
  wordmarkSize: TextUnit = 22.sp,
  showBadge: Boolean = true,
  badgeText: String = "60s"
) {
  Row(
    modifier = modifier.testTag("snapk_header"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    SnapkLogoIcon(size = iconSize, animateSparks = true)

    Column(verticalArrangement = Arrangement.Center) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        SnapkWordmark(fontSize = wordmarkSize)

        if (showBadge) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = SnapkYellow
          ) {
            Text(
              text = badgeText,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                color = SnapkDark,
                fontSize = 10.sp
              )
            )
          }
        }
      }

      Text(
        text = "DAILY 60-SEC CHALLENGE",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp,
          fontSize = 9.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
    }
  }
}

/**
 * Large Hero branding presentation for Onboarding and Profile setup.
 */
@Composable
fun SnapkHeroBrand(
  modifier: Modifier = Modifier,
  iconSize: Dp = 72.dp
) {
  Column(
    modifier = modifier.testTag("snapk_hero_brand"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    SnapkLogoIcon(
      size = iconSize,
      cornerRadius = 20.dp,
      animateSparks = true
    )

    SnapkWordmark(
      fontSize = 32.sp,
      textColor = MaterialTheme.colorScheme.onBackground
    )

    Surface(
      shape = RoundedCornerShape(8.dp),
      color = SnapkYellow.copy(alpha = 0.2f)
    ) {
      Text(
        text = "THE 60-SECOND SOCIAL CHALLENGE",
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Black,
          color = if (isSystemInDarkTheme()) SnapkYellow else Color(0xFFB45309),
          letterSpacing = 1.2.sp,
          fontSize = 10.sp
        )
      )
    }
  }
}
