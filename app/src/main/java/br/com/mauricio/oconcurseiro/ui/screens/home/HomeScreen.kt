package br.com.mauricio.oconcurseiro.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.domain.model.DesempenhoDisciplina
import br.com.mauricio.oconcurseiro.ui.components.designsystem.ErrorState
import br.com.mauricio.oconcurseiro.ui.state.HomeUiState
import br.com.mauricio.oconcurseiro.ui.theme.*
import br.com.mauricio.oconcurseiro.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartPractice: () -> Unit,
    onOpenFilters: () -> Unit,
    onLogout: () -> Unit,
    onLoginClick: () -> Unit,
    usuarioAutenticado: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()
    val erroAtual = uiState.erro

    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.atualizarDesempenho()
            },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                HomeHeader(
                    usuarioAutenticado = usuarioAutenticado,
                    onLoginClick = onLoginClick,
                    onLogout = onLogout
                )

                if (uiState.isLoading && !isRefreshing) {
                    ResolverQuestoesCardSkeleton()
                    Spacer(Modifier.height(12.dp))
                    DesempenhoSectionSkeleton()
                    Spacer(Modifier.height(20.dp))
                    RadarDisciplinasSkeleton()
                    Spacer(Modifier.height(24.dp))
                } else if (erroAtual != null && !uiState.statsCarregadas) {
                    ErrorState(
                        message = erroAtual,
                        onRetry = { viewModel.carregarEstatisticas() }
                    )
                } else {
                    ResolverQuestoesCard(onStartPractice)

                    Spacer(Modifier.height(12.dp))

                    DesempenhoSection(uiState)

                    Spacer(Modifier.height(20.dp))

                    RadarDisciplinasSection(uiState)

                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        BottomNavBar(
            onOpenFilters = onOpenFilters
        )
    }
}

@Composable
private fun HomeHeader(
    usuarioAutenticado: Boolean,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        HeaderBackground,
                        HeaderBackground.copy(alpha = 0.88f)
                    )
                )
            )
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Olá, Concurseiro!",
                color = TextOnBrand,
                style = MaterialTheme.typography.headlineLarge
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TextOnBrand.copy(alpha = 0.6f)),
                color = Color.Transparent,
                modifier = Modifier
                    .clickable {
                        if (usuarioAutenticado) onLogout() else onLoginClick()
                    }
            ) {
                Text(
                    text = if (usuarioAutenticado) "Sair" else "Entrar",
                    color = TextOnBrand,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
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
                    .background(BrandPrimaryBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Quiz,
                    contentDescription = null,
                    tint = BrandPrimary,
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
private fun DesempenhoSection(uiState: HomeUiState) {
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
                    PercentageCircle(
                        percentage = if (uiState.resolvidas7dias > 0) {
                            uiState.acertos7dias * 100 / uiState.resolvidas7dias
                        } else {
                            0
                        }
                    )

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
                                text = "Resolvidas: ${uiState.resolvidas7dias}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Text(
                                text = " • ",
                                fontSize = 12.sp,
                                color = TextPlaceholder
                            )

                            Text(
                                text = "Certas: ${uiState.acertos7dias}",
                                fontSize = 12.sp,
                                color = SuccessBorder
                            )

                            Text(
                                text = " • ",
                                fontSize = 12.sp,
                                color = TextPlaceholder
                            )

                            Text(
                                text = "Erradas: ${uiState.erros7dias}",
                                fontSize = 12.sp,
                                color = ErrorBorder
                            )
                        }
                    }
                }

                if (uiState.resolvidas7dias == 0) {
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
private fun BottomNavBar(
    onOpenFilters: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenFilters() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Filtros",
                        tint = BrandPrimary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = "Filtros",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarDisciplinasSection(uiState: HomeUiState) {
    val disciplinas = uiState.desempenhoPorDisciplina
    if (disciplinas.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Radar de disciplinas",
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
                disciplinas.forEachIndexed { index, item ->
                    DisciplinaProgressRow(item)

                    if (index < disciplinas.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = BorderDefault,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisciplinaProgressRow(item: DesempenhoDisciplina) {
    val barColor = when {
        item.aproveitamento >= 70 -> SuccessBorder
        item.aproveitamento >= 40 -> WarningBar
        else -> ErrorBorder
    }

    val bgColor = when {
        item.aproveitamento >= 70 -> SuccessBg
        item.aproveitamento >= 40 -> WarningBg
        else -> ErrorBg
    }

    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationStarted = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) item.aproveitamento / 100f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "progress_${item.disciplina}"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.aproveitamento}%",
                style = MaterialTheme.typography.labelMedium,
                color = barColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.disciplina,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "${item.total} questão${if (item.total != 1) "ões" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50))
                    .background(BorderDefault)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(barColor)
                )
            }
        }
    }
}
