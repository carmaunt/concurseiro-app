package br.com.mauricio.oconcurseiro.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.ui.theme.*
import br.com.mauricio.oconcurseiro.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartPractice: () -> Unit,
    onOpenFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            HomeHeader()

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandOrange)
                }
            } else if (viewModel.erro != null && !viewModel.statsCarregadas) {
                ErrorCard(
                    message = viewModel.erro!!,
                    onRetry = { viewModel.carregarEstatisticas() }
                )
            } else {
                ResolverQuestoesCard(onStartPractice)

                Spacer(Modifier.height(12.dp))

                DesempenhoSection(viewModel)

                Spacer(Modifier.height(24.dp))
            }
        }

        BottomNavBar(onOpenFilters)
    }
}

@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BrandOrange, BrandOrange.copy(alpha = 0.88f))
                )
            )
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 22.dp)
    ) {
        Text(
            text = "Olá, Concurseiro!",
            color = TextOnBrand,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
private fun ResolverQuestoesCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-14).dp),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 4.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(BrandOrangeBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Quiz,
                    contentDescription = null,
                    tint = BrandOrange,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Text(
                text = "Resolver questões",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TextPlaceholder,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun DesempenhoSection(viewModel: HomeViewModel) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Acompanhe seu desempenho",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary
        )

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = SurfaceWhite,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PercentageCircle(percentage = viewModel.porcentagem7dias)

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Últimos 7 dias",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "Resolvidas: ${viewModel.resolvidas7dias}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = " • ",
                                fontSize = 12.sp,
                                color = TextPlaceholder
                            )
                            Text(
                                text = "Certas: ${viewModel.acertos7dias}",
                                fontSize = 12.sp,
                                color = SuccessBorder
                            )
                            Text(
                                text = " • ",
                                fontSize = 12.sp,
                                color = TextPlaceholder
                            )
                            Text(
                                text = "Erradas: ${viewModel.erros7dias}",
                                fontSize = 12.sp,
                                color = ErrorBorder
                            )
                        }
                    }
                }

                if (viewModel.resolvidas7dias == 0) {
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Comece a resolver questões para ver seu desempenho",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPlaceholder,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PercentageCircle(percentage: Int) {
    val progressColor = if (percentage > 0) SuccessBorder else TextPlaceholder
    val bgColor = BorderDefault

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(60.dp)
    ) {
        Canvas(modifier = Modifier.size(60.dp)) {
            drawArc(
                color = bgColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
            if (percentage > 0) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * percentage / 100f,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.labelMedium,
            color = progressColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BottomNavBar(onOpenFilters: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Filled.Home,
                label = "Painel",
                selected = true,
                onClick = { }
            )
            BottomNavItem(
                icon = Icons.Filled.BarChart,
                label = "Desempenho",
                selected = false,
                onClick = { }
            )
            BottomNavItem(
                icon = Icons.Filled.FilterList,
                label = "Filtros",
                selected = false,
                onClick = onOpenFilters
            )
            BottomNavItem(
                icon = Icons.Outlined.EmojiEvents,
                label = "Desafios",
                selected = false,
                onClick = { }
            )
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = "Cursos",
                selected = false,
                onClick = { }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) BrandOrange else TextPlaceholder,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) BrandOrange else TextPlaceholder,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = ErrorBg
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorBorder
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text(
                    text = "Tentar novamente",
                    color = TextOnBrand,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
