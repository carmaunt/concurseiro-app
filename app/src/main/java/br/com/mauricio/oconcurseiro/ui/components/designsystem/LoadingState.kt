package br.com.mauricio.oconcurseiro.ui.components.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimary
import br.com.mauricio.oconcurseiro.ui.theme.TextSecondary

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = BrandPrimary,
            strokeWidth = 3.dp
        )

        if (message != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InlineLoadingState(
    modifier: Modifier = Modifier,
    size: Int = 28
) {
    CircularProgressIndicator(
        modifier = modifier.size(size.dp),
        color = BrandPrimary,
        strokeWidth = 2.5.dp
    )
}
