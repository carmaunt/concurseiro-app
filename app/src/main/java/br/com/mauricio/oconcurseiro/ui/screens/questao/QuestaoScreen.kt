package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Search
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
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.theme.*
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.RespostaAnterior

@Composable
fun QuestaoScreen(
    viewModel: QuestaoViewModel,
    onOpenFiltro: () -> Unit,
    onBack: (() -> Unit)? = null,
    onAbrirComentarios: ((questaoId: String) -> Unit)? = null
) {
    val questao = viewModel.questao
    val isLoading = viewModel.isLoading
    val erro = viewModel.erro
    val isEmpty = viewModel.isEmpty

    Column(modifier = Modifier.fillMaxSize()) {

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
                    CircularProgressIndicator(color = BrandOrange)
                }
            }

            isEmpty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextPlaceholder
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma questão encontrada",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Tente alterar os filtros para encontrar questões",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = onOpenFiltro,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                        ) {
                            Text(
                                text = "Alterar filtros",
                                color = TextOnBrand,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            erro != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = ErrorBorder
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Ocorreu um erro",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = erro,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.carregarQuestao() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                        ) {
                            Text(
                                text = "Tentar novamente",
                                color = TextOnBrand,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            questao != null -> {
                TopoResumoQuestao(
                    questaoNumero = viewModel.numeroAtual,
                    questoesTotal = viewModel.totalQuestoes,
                    onAbrirComentarios = { questao?.id?.let { id -> onAbrirComentarios?.invoke(id) } }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    CorpoQuestao(
                        questao = questao,
                        respostaAnterior = viewModel.respostaAnterior,
                        onResolver = { respostaSelecionada, acertou ->
                            viewModel.salvarResposta(
                                questaoId = questao.id,
                                disciplina = questao.disciplina,
                                respostaSelecionada = respostaSelecionada,
                                gabarito = questao.gabarito,
                                acertou = acertou
                            )
                        }
                    )
                }

                RodapeQuestao(
                    podeAnterior = viewModel.paginaAtual > 0,
                    podeProximo = viewModel.paginaAtual < ((viewModel.totalQuestoes - 1) / 1),
                    onAnterior = { viewModel.anterior() },
                    onProximo = { viewModel.proxima() },
                    onFiltro = onOpenFiltro
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
                    .background(BrandOrange, shape = RoundedCornerShape(999.dp))
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
    onResolver: (respostaSelecionada: String, acertou: Boolean) -> Unit = { _, _ -> }
) {
    var selecionada by remember(questao.id) { mutableIntStateOf(-1) }
    var resolvida by remember(questao.id) { mutableStateOf(false) }
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

        Text(
            text = "Ano: ${questao.ano}  Banca: ${questao.banca}\nÓrgão: ${questao.orgao}  Cargo: ${questao.cargo}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(Modifier.height(20.dp))

        var enunciadoAberto by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceCard)
                .clickable { enunciadoAberto = !enunciadoAberto }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Texto associado",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (enunciadoAberto) "−" else "+",
                fontSize = 28.sp,
                color = TextSecondary
            )
        }

        AnimatedVisibility(
            visible = enunciadoAberto,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = questao.enunciado,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(18.dp))
            }
        }

        if (!enunciadoAberto) {
            Spacer(Modifier.height(10.dp))
        }

        if (questao.questao.isNotBlank()) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = questao.questao,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }

        Spacer(Modifier.height(20.dp))

        questao.alternativas.forEachIndexed { index, alternativa ->
            val correta = alternativa.letra.equals(questao.gabarito, ignoreCase = true)
            val foiClicada = selecionada == index

            Alternativa(
                letra = alternativa.letra,
                texto = alternativa.texto,
                selecionada = foiClicada,
                resolvida = resolvida,
                correta = correta
            ) {
                if (!resolvida) selecionada = index
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                resolvida = true
                if (selecionada >= 0 && selecionada < questao.alternativas.size) {
                    val letraSelecionada = questao.alternativas[selecionada].letra
                    val acertou = letraSelecionada.equals(questao.gabarito, ignoreCase = true)
                    onResolver(letraSelecionada, acertou)
                }
            },
            enabled = selecionada != -1 && !resolvida,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selecionada != -1 && !resolvida) BrandOrange else BrandOrangeDisabled,
                disabledContainerColor = BrandOrangeDisabled
            )
        ) {
            Text(
                text = if (!resolvida) "Resolver" else "Resolvida",
                color = TextOnBrand.copy(alpha = if (!resolvida) 1f else 0.6f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun Alternativa(
    letra: String,
    texto: String,
    selecionada: Boolean,
    resolvida: Boolean,
    correta: Boolean,
    onClick: () -> Unit
) {
    val neutralBg = SurfaceCard
    val neutralBorder = Color.Transparent

    val okBg = SuccessBg
    val okBorder = SuccessBorder

    val errBg = ErrorBg
    val errBorder = ErrorBorder

    val selectedBg = BrandOrangeLight

    val (bg, border, icon) = when {
        !resolvida && selecionada -> Triple(selectedBg, neutralBorder, null)
        !resolvida -> Triple(neutralBg, neutralBorder, null)
        correta -> Triple(okBg, okBorder, "✓")
        selecionada && !correta -> Triple(errBg, errBorder, "✕")
        else -> Triple(neutralBg, neutralBorder, null)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, shape = RoundedCornerShape(16.dp))
            .then(
                if (border != Color.Transparent) Modifier.border(2.dp, border, RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(border.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontWeight = FontWeight.Bold,
                    color = border
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(BorderCircle, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letra,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
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
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
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
