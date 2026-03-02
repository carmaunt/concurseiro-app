package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.foundation.BorderStroke
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
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.mauricio.oconcurseiro.data.model.FiltroParams

@Composable
fun QuestaoScreen(
    filtro: FiltroParams,
    numeroAtual: Int,
    totalQuestoes: Int,
    onOpenFiltro: () -> Unit
) {
    val viewModel: QuestaoViewModel = viewModel()

    LaunchedEffect(filtro) {
        viewModel.carregarQuestao(filtro)
    }

    val questao = viewModel.questao
    val isLoading = viewModel.isLoading
    val erro = viewModel.erro

    Column(modifier = Modifier.fillMaxSize()) {

        AppHeader(
            title = questao?.let { "Questão ${it.id}" } ?: "Carregando...",
            subtitle = questao?.disciplina ?: "",
            onBack = { },
            actionText = "⚙",
            onAction = onOpenFiltro
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            erro != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Erro: $erro")
                }
            }

            questao != null -> {
                TopoResumoQuestao(
                    questaoNumero = viewModel.numeroAtual,
                    questoesTotal = viewModel.totalQuestoes
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    CorpoQuestao(questao)
                }

                RodapeQuestao(
                    podeAnterior = viewModel.paginaAtual > 0,
                    podeProximo = viewModel.paginaAtual < ((viewModel.totalQuestoes - 1) / 1),
                    onAnterior = { viewModel.anterior(filtro) },
                    onProximo = { viewModel.proxima(filtro) }
                )
            }
        }
    }
}

@Composable
fun TopoResumoQuestao(
    questaoNumero: Int,
    questoesTotal: Int
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
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "de $questoesTotal",
                    fontSize = 18.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFFF6A2A), shape = RoundedCornerShape(999.dp))
            )
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Text(text = "+ Recursos", color = Color(0xFFFF6A2A), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CorpoQuestao(questao: Questao) {
    var selecionada by remember { mutableIntStateOf(-1) }
    var resolvida by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Ano: ${questao.ano}  Banca: ${questao.banca}\nÓrgão: ${questao.orgao}",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.height(20.dp))

        var enunciadoAberto by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF3F4F6))
                .clickable { enunciadoAberto = !enunciadoAberto }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Texto associado",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF111827),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (enunciadoAberto) "−" else "+",
                fontSize = 28.sp,
                color = Color(0xFF6B7280)
            )
        }

        if (enunciadoAberto) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = questao.enunciado,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF111827)
            )
            Spacer(Modifier.height(18.dp))
        } else {
            Spacer(Modifier.height(10.dp))
        }

        if (questao.questao.isNotBlank()) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = questao.questao,
                fontSize = 18.sp,
                color = Color(0xFF111827)
            )
        }

        Spacer(Modifier.height(20.dp)) // espaço antes das alternativas

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
            onClick = { resolvida = true },
            enabled = selecionada != -1 && !resolvida,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selecionada != -1 && !resolvida) Color(0xFFFF6A2A) else Color(0xFFFFC3A8),
                disabledContainerColor = Color(0xFFFFC3A8)
            )
        ) {
            Text(
                text = if (!resolvida) "Resolver" else "Resolvida",
                color = Color.White.copy(alpha = if (!resolvida) 1f else 0.6f),
                fontSize = 18.sp,
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
    // Cores base
    val neutralBg = Color(0xFFF3F4F6)
    val neutralBorder = Color.Transparent

    // Estados após resolver:
    val okBg = Color(0xFFDCFCE7)      // verde claro
    val okBorder = Color(0xFF16A34A)  // verde

    val errBg = Color(0xFFFEE2E2)     // vermelho claro
    val errBorder = Color(0xFFEF4444) // vermelho

    val selectedBg = Color(0xFFFFE7DD) // laranja claro antigo

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

        // Ícone de certo/errado (estilo da imagem)
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
                    .background(Color(0xFFE5E7EB), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letra,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = texto,
            fontSize = 16.sp,
            color = Color(0xFF111827)
        )
    }
}

@Composable
fun RodapeQuestao(
    podeAnterior: Boolean,
    podeProximo: Boolean,
    onAnterior: () -> Unit,
    onProximo: () -> Unit
) {
    val corAtiva = Color(0xFF6B7280)
    val corInativa = Color(0xFF6B7280).copy(alpha = 0.35f)

    Surface(
        shadowElevation = 8.dp,
        color = Color(0xFFF6F7FB)
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
                    .background(if (podeAnterior) Color.Transparent else Color(0xFFE5E7EB))
                    .clickable(enabled = podeAnterior) { onAnterior() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "‹  Anterior",
                    color = if (podeAnterior) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                    fontSize = 16.sp,
                    fontWeight = if (podeAnterior) FontWeight.SemiBold else FontWeight.Medium
                )
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = "Ir para questão",
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (podeProximo) Color.Transparent else Color(0xFFE5E7EB))
                    .clickable(enabled = podeProximo) { onProximo() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Próximo  ›",
                    color = if (podeProximo) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                    fontSize = 16.sp,
                    fontWeight = if (podeProximo) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}