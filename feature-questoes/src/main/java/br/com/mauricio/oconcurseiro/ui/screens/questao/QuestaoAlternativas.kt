package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.ui.components.designsystem.AnswerOption
import br.com.mauricio.oconcurseiro.ui.components.designsystem.AnswerState
import br.com.mauricio.oconcurseiro.ui.components.designsystem.OConcurseiroButton
import br.com.mauricio.oconcurseiro.ui.theme.TextPrimary
import br.com.mauricio.oconcurseiro.ui.theme.TextSecondary
import br.com.mauricio.oconcurseiro.ui.theme.WarningBar
import br.com.mauricio.oconcurseiro.ui.theme.WarningBg

@Composable
fun QuestaoAlternativas(
    questao: Questao,
    onResponder: (respostaSelecionada: String, acertou: Boolean) -> Unit,
    onResolvida: () -> Unit,
    onPodeResponder: () -> Boolean
) {
    var selecionada by remember(questao.id) { mutableIntStateOf(-1) }
    var resolvida by remember(questao.id) { mutableStateOf(false) }
    val isAnulada = questao.isAnulada

    Column {
        if (isAnulada) {
            QuestaoAnuladaAviso()
        }

        val alternativas = if (isAnulada && questao.alternativas.isEmpty()) {
            listOf(
                br.com.mauricio.oconcurseiro.domain.model.Alternativa(letra = "C", texto = "Certo"),
                br.com.mauricio.oconcurseiro.domain.model.Alternativa(letra = "E", texto = "Errado")
            )
        } else {
            questao.alternativas
        }

        alternativas.forEachIndexed { index, alternativa ->
            val correta = alternativa.letra.equals(questao.gabarito, ignoreCase = true)
            val foiClicada = selecionada == index

            val estado = when {
                !isAnulada && resolvida && correta -> AnswerState.CORRECT
                !isAnulada && resolvida && foiClicada && !correta -> AnswerState.WRONG
                !isAnulada && !resolvida && foiClicada -> AnswerState.SELECTED
                else -> AnswerState.DEFAULT
            }

            AnswerOption(
                letter = alternativa.letra,
                text = alternativa.texto,
                state = estado,
                onClick = { if (!isAnulada && !resolvida) selecionada = index },
                enabled = !isAnulada
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(Modifier.height(12.dp))

        OConcurseiroButton(
            text = when {
                isAnulada -> "Questão anulada"
                !resolvida -> "Resolver"
                else -> "✓ Resolvida"
            },
            onClick = {
                if (isAnulada || selecionada < 0 || resolvida) return@OConcurseiroButton
                if (!onPodeResponder()) return@OConcurseiroButton
                val letraSelecionada = alternativas[selecionada].letra
                val acertou = letraSelecionada.equals(questao.gabarito, ignoreCase = true)
                resolvida = true
                onResponder(letraSelecionada, acertou)
                onResolvida()
            },
            enabled = !isAnulada && selecionada != -1 && !resolvida,
            fullWidth = true
        )
    }
}

@Composable
private fun QuestaoAnuladaAviso() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(WarningBg, RoundedCornerShape(12.dp))
            .border(1.dp, WarningBar.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(5.dp)
                .background(WarningBar)
        )
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Questão anulada",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Este item foi anulado pela banca e está disponível somente para consulta.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }

    Spacer(Modifier.height(12.dp))
}
