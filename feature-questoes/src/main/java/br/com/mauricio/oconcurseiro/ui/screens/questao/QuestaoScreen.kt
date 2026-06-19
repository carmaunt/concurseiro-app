package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CoPresent
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
    usuarioAutenticado: Boolean,
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
                    usuarioAutenticado = usuarioAutenticado,
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
    explicacaoDisponivel: Boolean = false,
    explicacaoDesbloqueada: Boolean = false,
    usuarioAutenticado: Boolean = false,
    onAbrirComentarios: () -> Unit = {},
    onAbrirExplicacao: () -> Unit = {}
) {
    val podeAbrirExplicacao = podeAtivarExplicacao(
        resolveuAgora = explicacaoDesbloqueada,
        usuarioAutenticado = usuarioAutenticado
    )

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
                .background(if (podeAbrirExplicacao) BrandPrimaryBackground else SurfaceCard)
                .clickable(enabled = podeAbrirExplicacao) { onAbrirExplicacao() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CoPresent,
                contentDescription = when {
                    podeAbrirExplicacao -> "Abrir explicação do professor"
                    !usuarioAutenticado -> "Faça login para acessar a explicação"
                    else -> "Explicação disponível após responder"
                },
                tint = if (podeAbrirExplicacao) BrandPrimary else TextPlaceholder,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceCard)
                .clickable { onAbrirComentarios() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "Abrir comentários",
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

internal fun podeAtivarExplicacao(
    resolveuAgora: Boolean,
    usuarioAutenticado: Boolean
): Boolean = resolveuAgora && usuarioAutenticado

@Composable
fun CorpoQuestao(
    questao: Questao,
    respostaAnterior: RespostaAnterior? = null,
    onPodeResolverQuestao: (questaoId: String) -> Boolean = { true },
    onResolver: (respostaSelecionada: String, acertou: Boolean) -> Unit = { _, _ -> },
    onResolvidaComSucesso: () -> Unit = {},
    onTentouResolver: () -> Unit = {}
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
            onResolvida = onResolvidaComSucesso,
            onTentouResolver = onTentouResolver
        )

        Spacer(Modifier.height(12.dp))
    }
}


@Composable
fun RodapeQuestao(
    podeAnterior: Boolean,
    podeProximo: Boolean,
    onAnterior: () -> Unit,
    onProximo: () -> Unit
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
    val blocks = remember(text) { parseMarkdownBlocks(text) }

    Column(modifier = modifier) {
        blocks.forEachIndexed { index, block ->
            if (index > 0) {
                Spacer(Modifier.height(10.dp))
            }

            when (block) {
                is MarkdownBlock.Paragraph -> {
                    Text(
                        text = parseMarkdownCompat(block.text),
                        style = style,
                        color = color
                    )
                }

                is MarkdownBlock.Table -> {
                    MarkdownTable(
                        table = block,
                        textStyle = style,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
private fun MarkdownTable(
    table: MarkdownBlock.Table,
    textStyle: androidx.compose.ui.text.TextStyle,
    color: Color
) {
    val scrollState = rememberScrollState()
    val cellPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    val columnWidths = remember(table.rows) {
        val columnCount = table.rows.maxOfOrNull { it.size } ?: 0
        List(columnCount) { columnIndex ->
            val maxLength = table.rows.maxOfOrNull { row ->
                row.getOrNull(columnIndex)?.length ?: 0
            } ?: 0

            when {
                maxLength <= 2 -> 48.dp
                maxLength <= 8 -> 80.dp
                else -> 136.dp
            }
        }
    }

    Column(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        table.rows.forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { columnIndex, cell ->
                    Box(
                        modifier = Modifier
                            .width(columnWidths[columnIndex])
                            .border(BorderStroke(0.5.dp, BorderDefault))
                            .background(if (rowIndex == 0) SurfaceCard else Color.Transparent)
                            .padding(cellPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = parseMarkdownCompat(cell),
                            style = textStyle,
                            color = color,
                            fontWeight = if (rowIndex == 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

private sealed interface MarkdownBlock {
    data class Paragraph(val text: String) : MarkdownBlock
    data class Table(val rows: List<List<String>>) : MarkdownBlock
}

private fun parseMarkdownBlocks(input: String): List<MarkdownBlock> {
    val lines = input.lines()
    val blocks = mutableListOf<MarkdownBlock>()
    val paragraph = mutableListOf<String>()
    var index = 0

    fun flushParagraph() {
        val text = paragraph.joinToString("\n").trim()
        if (text.isNotBlank()) {
            blocks += MarkdownBlock.Paragraph(text)
        }
        paragraph.clear()
    }

    while (index < lines.size) {
        val line = lines[index]

        if (isMarkdownTableStart(lines, index)) {
            flushParagraph()

            val rows = mutableListOf<List<String>>()
            rows += parseMarkdownTableRow(lines[index])
            index += 2

            while (index < lines.size && isMarkdownTableRow(lines[index])) {
                rows += parseMarkdownTableRow(lines[index])
                index++
            }

            blocks += MarkdownBlock.Table(rows)
            continue
        }

        paragraph += line
        index++
    }

    flushParagraph()
    return blocks
}

private fun isMarkdownTableStart(lines: List<String>, index: Int): Boolean {
    return index + 1 < lines.size &&
        isMarkdownTableRow(lines[index]) &&
        isMarkdownTableSeparator(lines[index + 1])
}

private fun isMarkdownTableRow(line: String): Boolean {
    val trimmed = line.trim()
    return trimmed.startsWith("|") && trimmed.endsWith("|") && trimmed.count { it == '|' } >= 2
}

private fun isMarkdownTableSeparator(line: String): Boolean {
    val cells = parseMarkdownTableRow(line)
    return cells.isNotEmpty() && cells.all { cell ->
        cell.isNotBlank() && cell.all { it == '-' || it == ':' || it.isWhitespace() }
    }
}

private fun parseMarkdownTableRow(line: String): List<String> {
    return line.trim()
        .removePrefix("|")
        .removeSuffix("|")
        .split("|")
        .map { it.trim() }
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
