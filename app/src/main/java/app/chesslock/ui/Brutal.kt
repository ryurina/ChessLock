package app.chesslock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object B {
    val Paper = Color(0xFFFFF8E7)
    val Ink = Color(0xFF000000)
    val Yellow = Color(0xFFFFE600)
    val Pink = Color(0xFFFF6B9D)
    val Blue = Color(0xFF4D9FFF)
    val Green = Color(0xFF7CFF6B)
    val Red = Color(0xFFFF3B3B)
    val White = Color(0xFFFFFFFF)
}

/** Flat fill, thick border, hard offset shadow with no blur. */
fun Modifier.brutal(fill: Color = B.Yellow, shadow: Dp = 6.dp, border: Dp = 3.dp): Modifier =
    this.padding(end = shadow, bottom = shadow)
        .drawBehind {
            val s = shadow.toPx()
            drawRect(B.Ink, topLeft = Offset(s, s), size = size)
        }
        .background(fill)
        .border(border, B.Ink)

@Composable
fun BText(text: String, size: Int = 16, modifier: Modifier = Modifier, color: Color = B.Ink) =
    Text(text, modifier, color = color, style = TextStyle(fontSize = size.sp, fontWeight = FontWeight.Black, lineHeight = (size * 1.15).sp))

@Composable
fun BButton(text: String, modifier: Modifier = Modifier, fill: Color = B.Pink, onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val off = if (pressed) 4.dp else 0.dp
    Box(
        modifier.offset(off, off).brutal(fill, 6.dp - off)
            .clickable(src, null, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) { BText(text, 16) }
}

@Composable
fun BTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = lightColorScheme(background = B.Paper, surface = B.Paper), content = content)
