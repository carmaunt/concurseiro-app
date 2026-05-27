package br.com.mauricio.oconcurseiro.ui.components.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.ui.theme.BorderCircle
import br.com.mauricio.oconcurseiro.ui.theme.BorderDefault
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimary
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimaryLight
import br.com.mauricio.oconcurseiro.ui.theme.ErrorBg
import br.com.mauricio.oconcurseiro.ui.theme.ErrorBorder
import br.com.mauricio.oconcurseiro.ui.theme.SuccessBg
import br.com.mauricio.oconcurseiro.ui.theme.SuccessBorder
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceWhite
import br.com.mauricio.oconcurseiro.ui.theme.TextPrimary
import br.com.mauricio.oconcurseiro.ui.theme.TextSecondary

enum class AnswerState {
    DEFAULT,
    SELECTED,
    CORRECT,
    WRONG
}

@Composable
fun AnswerOption(
    letter: String,
    text: String,
    state: AnswerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, letterBg, letterColor, textColor) = when (state) {
        AnswerState.DEFAULT -> AnswerColors(
            bg = SurfaceWhite,
            border = BorderDefault,
            letterBg = BorderCircle,
            letter = TextSecondary,
            text = TextPrimary
        )
        AnswerState.SELECTED -> AnswerColors(
            bg = BrandPrimaryLight,
            border = BrandPrimary,
            letterBg = BrandPrimary,
            letter = Color.White,
            text = BrandPrimary
        )
        AnswerState.CORRECT -> AnswerColors(
            bg = SuccessBg,
            border = SuccessBorder,
            letterBg = SuccessBorder,
            letter = Color.White,
            text = SuccessBorder
        )
        AnswerState.WRONG -> AnswerColors(
            bg = ErrorBg,
            border = ErrorBorder,
            letterBg = ErrorBorder,
            letter = Color.White,
            text = ErrorBorder
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = state == AnswerState.DEFAULT || state == AnswerState.SELECTED) { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(letterBg),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                AnswerState.CORRECT -> Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                AnswerState.WRONG -> Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                else -> Text(
                    text = letter,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = letterColor,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = parseInlineMarkdown(text),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = if (state != AnswerState.DEFAULT) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(1f),
            lineHeight = 20.sp
        )
    }
}

private fun parseInlineMarkdown(raw: String): AnnotatedString {
    return buildAnnotatedString {
        var index = 0

        while (index < raw.length) {
            when {
                raw.startsWith("**", index) -> {
                    val end = raw.indexOf("**", startIndex = index + 2)
                    if (end > index + 2) {
                        val content = raw.substring(index + 2, end)
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append(content)
                        pop()
                        index = end + 2
                    } else {
                        append(raw[index])
                        index++
                    }
                }

                raw[index] == '*' -> {
                    val end = raw.indexOf('*', startIndex = index + 1)
                    if (end > index + 1) {
                        val content = raw.substring(index + 1, end)
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        append(content)
                        pop()
                        index = end + 1
                    } else {
                        append(raw[index])
                        index++
                    }
                }

                else -> {
                    append(raw[index])
                    index++
                }
            }
        }
    }
}

private data class AnswerColors(
    val bg: Color,
    val border: Color,
    val letterBg: Color,
    val letter: Color,
    val text: Color
)