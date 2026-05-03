package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.components.designsystem.EmptyState
import br.com.mauricio.oconcurseiro.ui.components.designsystem.ErrorState
import br.com.mauricio.oconcurseiro.ui.components.designsystem.LoadingState
import br.com.mauricio.oconcurseiro.ui.theme.*
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.RespostaAnterior

@Composable
fun QuestaoScreen(
    viewModel: QuestaoViewModel,
    onOpenFiltro: () -> Unit,
    onBack: (() -> Unit)? = null,
    onAbrirComentarios: ((questaoId: String) -> Unit)? = null,
    onPodeResolverQuestao: (questaoId: String) -> Boolean = { true },
    onResolvidaComSucesso: (questaoId: String) -> Unit = {},
    onSolicitarProximaQuestao: () -> Unit = { viewModel.proxima() }
) {

    val uiState = viewModel.uiState

    val questao = uiState.questao
    val isLoading = uiState.isLoading
    val erro = uiState.erro
    val isEmpty = uiState.isEmpty

    Column(modifier = Modifier.fillMaxSize().background(SurfaceWhite)) {

        AppHeader(
            title = questao?.let { "Questão ${it.id}" } ?: "Carregando...",
            subtitle = questao?.disciplina ?: "",
            onBack = onBack
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingState(message = "Carregando questão...")
                }
            }

            isEmpty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = "Nenhuma questão encontrada",
                        message = "Tente alterar os filtros para encontrar questões",
                        actionLabel = "Alterar filtros",
                        onAction = onOpenFiltro
                    )
                }
            }

            erro != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorState(
                        title = "Ocorreu um erro",
                        message = erro,
                        onRetry = { viewModel.carregarQuestao() }
                    )
                }
            }

            questao != null -> {
                QuestaoContent(
                    questao = questao,
                    respostaAnterior = uiState.respostaAnterior,
                    numeroAtual = uiState.numeroAtual,
                    totalQuestoes = uiState.totalQuestoes,
                    paginaAtual = uiState.paginaAtual,
                    onAbrirComentarios = { id -> onAbrirComentarios?.invoke(id) },
                    onResponder = { respostaSelecionada, acertou ->
                        viewModel.salvarResposta(
                            questaoId = questao.id,
                            disciplina = questao.disciplina,
                            respostaSelecionada = respostaSelecionada,
                            gabarito = questao.gabarito,
                            acertou = acertou
                        )
                    },
                    onResolvidaComSucesso = {
                        onResolvidaComSucesso(questao.id)
                    },
                    onAnterior = { viewModel.anterior() },
                    onProximo = onSolicitarProximaQuestao,
                    onFiltro = onOpenFiltro,
                    onPodeResolverQuestao = onPodeResolverQuestao
                )
            }
        }
    }
}

@Composable
fun TopoResumoQuestao(
    questaoNumero: Int,
    questoesTotal: Int,
    onAbrirComentarios: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = questaoNumero.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "de $questoesTotal",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(BrandPrimary, shape = RoundedCornerShape(999.dp))
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceCard)
                .clickable { onAbrirComentarios() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "💬",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun CorpoQuestao(
    questao: Questao,
    respostaAnterior: RespostaAnterior? = null,
    onPodeResolverQuestao: (questaoId: String) -> Boolean = { true },
    onResolver: (respostaSelecionada: String, acertou: Boolean) -> Unit = { _, _ -> },
    onResolvidaComSucesso: () -> Unit = {}
) {
    var bannerVisivel by remember(questao.id) { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (respostaAnterior != null && bannerVisivel) {
            RespostaAnteriorBanner(
                acertou = respostaAnterior.acertou,
                onOcultar = { bannerVisivel = false }
            )
            Spacer(Modifier.height(14.dp))
        }

        QuestaoEnunciado(questao)

        Spacer(Modifier.height(12.dp))

        QuestaoAlternativas(
            questao = questao,
            onPodeResponder = { onPodeResolverQuestao(questao.id) },
            onResponder = onResolver,
            onResolvida = onResolvidaComSucesso
        )

        Spacer(Modifier.height(12.dp))
    }
}


@Composable
fun RodapeQuestao(
    podeAnterior: Boolean,
    podeProximo: Boolean,
    onAnterior: () -> Unit,
    onProximo: () -> Unit,
    onFiltro: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = SurfaceBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (podeAnterior) Color.Transparent else BorderDefault)
                    .clickable(enabled = podeAnterior) { onAnterior() }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹ Anterior",
                    color = if (podeAnterior) TextSecondary else TextPlaceholder,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (podeAnterior) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onFiltro,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(40.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text(
                    text = "Filtros",
                    color = TextOnBrand,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (podeProximo) Color.Transparent else BorderDefault)
                    .clickable(enabled = podeProximo) { onProximo() }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Próximo ›",
                    color = if (podeProximo) TextSecondary else TextPlaceholder,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (podeProximo) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun RespostaAnteriorBanner(
    acertou: Boolean,
    onOcultar: () -> Unit
) {
    val bgColor = if (acertou) SuccessBg else ErrorBg
    val borderColor = if (acertou) SuccessBorder else ErrorBorder
    val texto = if (acertou) "Você acertou essa questão anteriormente" else "Você errou essa questão anteriormente"
    val icone = if (acertou) "✓" else "✕"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .border(1.dp, borderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(borderColor.copy(alpha = 0.15f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icone,
                color = borderColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = borderColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Ocultar",
            style = MaterialTheme.typography.bodySmall,
            color = borderColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable { onOcultar() }
                .padding(4.dp)
        )
    }
}

@Composable
fun MarkdownCompatText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = parseMarkdownCompat(text),
        style = style,
        color = color,
        modifier = modifier
    )
}

private fun parseMarkdownCompat(input: String): AnnotatedString {
    val result = buildAnnotatedString {
        var i = 0

        while (i < input.length) {
            // negrito: **texto**
            if (i + 1 < input.length && input[i] == '*' && input[i + 1] == '*') {
                val end = input.indexOf("**", startIndex = i + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(input.substring(i + 2, end))
                    pop()
                    i = end + 2
                    continue
                }
            }

            // itálico: *texto*
            if (input[i] == '*') {
                val end = input.indexOf('*', startIndex = i + 1)
                if (end != -1) {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(input.substring(i + 1, end))
                    pop()
                    i = end + 1
                    continue
                }
            }

            append(input[i])
            i++
        }
    }

    return result
}