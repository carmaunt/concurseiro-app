package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.data.model.Questao

@Composable
fun QuestaoAlternativas(
    questao: Questao,
    onResponder: (respostaSelecionada: String, acertou: Boolean) -> Unit,
    onResolvida: () -> Unit,
    podeResponder: Boolean
) {
    var selecionada by remember(questao.id) { mutableIntStateOf(-1) }
    var resolvida by remember(questao.id) { mutableStateOf(false) }

    Column {

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

        QuestaoBotaoResolver(
            selecionada = selecionada,
            resolvida = resolvida,
            questao = questao,
            podeResponder = podeResponder,
            onResolver = {
                val letraSelecionada = questao.alternativas[selecionada].letra
                val acertou = letraSelecionada.equals(questao.gabarito, ignoreCase = true)

                resolvida = true
                onResponder(letraSelecionada, acertou)
                onResolvida()
            }
        )
    }
}

@Composable
fun QuestaoBotaoResolver(
    selecionada: Int,
    resolvida: Boolean,
    questao: Questao,
    podeResponder: Boolean,
    onResolver: () -> Unit
) {
    Button(
        onClick = {
            if (selecionada >= 0 && !resolvida && podeResponder) {
                onResolver()
            }
        },
        enabled = selecionada != -1 && !resolvida,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(if (!resolvida) "Resolver" else "Resolvida")
    }
}