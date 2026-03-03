package br.com.mauricio.oconcurseiro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import br.com.mauricio.oconcurseiro.ui.theme.BrandOrange
import br.com.mauricio.oconcurseiro.ui.theme.TextOnBrand

@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(BrandOrange)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (onBack != null) {
            Text(
                text = "‹",
                color = TextOnBrand,
                fontSize = 28.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clickable { onBack() }
            )
        }

        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                color = TextOnBrand,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onAction() }
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = TextOnBrand,
                style = MaterialTheme.typography.titleSmall
            )

            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = TextOnBrand.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
