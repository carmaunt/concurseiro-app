package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QuestaoScreen(
    questao: Questao,
    numeroAtual: Int,
    totalQuestoes: Int,
    onOpenFiltro: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        val viewModel: QuestaoViewModel = viewModel()
        val questao = viewModel.questao
        val idQuestao: String = questao.id
        AppHeader(
            title = "Questão $idQuestao",
            subtitle = questao.disciplina,
            onBack = { },
            actionText = "⚙",
            onAction = onOpenFiltro
        )

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

        RodapeQuestao()
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

        Text(
            text = questao.enunciado,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )

        Spacer(Modifier.height(24.dp))

        questao.alternativas.forEachIndexed { index, alternativa ->

            Alternativa(
                letra = alternativa.letra,
                texto = alternativa.texto,
                selecionada = selecionada == index
            ) {
                selecionada = index
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(Modifier.height(12.dp))
        Spacer(Modifier.weight(1f))

        Button(
            onClick = { },
            enabled = selecionada != -1,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selecionada != -1) Color(0xFFFF6A2A) else Color(0xFFFFC3A8),
                disabledContainerColor = Color(0xFFFFC3A8)
            )
        ) {
            Text(
                text = "Resolver",
                color = if (selecionada != -1) Color.White else Color.White.copy(alpha = 0.5f),
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
    onClick: () -> Unit
) {
    val bg = if (selecionada) Color(0xFFFFE7DD) else Color(0xFFF3F4F6)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        Spacer(Modifier.width(16.dp))

        Text(
            text = texto,
            fontSize = 16.sp,
            color = Color(0xFF111827)
        )
    }
}

@Composable
fun RodapeQuestao() {
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
            Text(
                text = "‹  Anterior",
                color = Color(0xFF6B7280),
                fontSize = 16.sp
            )

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

            Text(
                text = "Próximo  ›",
                color = Color(0xFF6B7280),
                fontSize = 16.sp
            )
        }
    }
}