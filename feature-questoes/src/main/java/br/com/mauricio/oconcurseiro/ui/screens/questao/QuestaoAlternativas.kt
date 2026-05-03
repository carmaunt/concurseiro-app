package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.ui.components.designsystem.AnswerOption
import br.com.mauricio.oconcurseiro.ui.components.designsystem.AnswerState
import br.com.mauricio.oconcurseiro.ui.components.designsystem.OConcurseiroButton

@Composable
fun QuestaoAlternativas(
    questao: Questao,
    onResponder: (respostaSelecionada: String, acertou: Boolean) -> Unit,
    onResolvida: () -> Unit,
    onPodeResponder: () -> Boolean
) {
    var selecionada by remember(questao.id) { mutableIntStateOf(-1) }
    var resolvida by remember(questao.id) { mutableStateOf(false) }

    Column {
        questao.alternativas.forEachIndexed { index, alternativa ->
            val correta = alternativa.letra.equals(questao.gabarito, ignoreCase = true)
            val foiClicada = selecionada == index

            val estado = when {
                resolvida && correta -> AnswerState.CORRECT
                resolvida && foiClicada && !correta -> AnswerState.WRONG
                !resolvida && foiClicada -> AnswerState.SELECTED
                else -> AnswerState.DEFAULT
            }

            AnswerOption(
                letter = alternativa.letra,
                text = alternativa.texto,
                state = estado,
                onClick = { if (!resolvida) selecionada = index }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(Modifier.height(12.dp))

        OConcurseiroButton(
            text = if (!resolvida) "Resolver" else "✓ Resolvida",
            onClick = {
                if (selecionada < 0 || resolvida) return@OConcurseiroButton
                if (!onPodeResponder()) return@OConcurseiroButton
                val letraSelecionada = questao.alternativas[selecionada].letra
                val acertou = letraSelecionada.equals(questao.gabarito, ignoreCase = true)
                resolvida = true
                onResponder(letraSelecionada, acertou)
                onResolvida()
            },
            enabled = selecionada != -1 && !resolvida,
            fullWidth = true
        )
    }
}
