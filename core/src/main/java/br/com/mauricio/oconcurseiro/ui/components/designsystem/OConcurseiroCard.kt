package br.com.mauricio.oconcurseiro.ui.components.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceWhite

@Composable
fun OConcurseiroCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceWhite,
    elevation: Dp = 1.dp,
    cornerRadius: Dp = 16.dp,
    padding: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth().then(
            if (onClick != null) Modifier.clip(RoundedCornerShape(cornerRadius)).clickable { onClick() }
            else Modifier
        ),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        shadowElevation = elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            content = content
        )
    }
}

@Composable
fun OConcurseiroCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceWhite,
    elevation: Dp = 1.dp,
    cornerRadius: Dp = 16.dp,
    horizontalPadding: Dp = 20.dp,
    verticalPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth().then(
            if (onClick != null) Modifier.clip(RoundedCornerShape(cornerRadius)).clickable { onClick() }
            else Modifier
        ),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        shadowElevation = elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            content = content
        )
    }
}
