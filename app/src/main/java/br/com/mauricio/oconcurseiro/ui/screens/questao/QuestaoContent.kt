package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.ui.viewmodel.RespostaAnterior

@Composable
fun ColumnScope.QuestaoContent(
    questao: Questao,
    respostaAnterior: RespostaAnterior?,
    numeroAtual: Int,
    totalQuestoes: Int,
    paginaAtual: Int,
    onAbrirComentarios: (String) -> Unit,
    onResponder: (String, Boolean) -> Unit,
    onResolvidaComSucesso: () -> Unit,
    onAnterior: () -> Unit,
    onProximo: () -> Unit,
    onFiltro: () -> Unit,
    onPodeResolverQuestao: (String) -> Boolean
) {

    TopoResumoQuestao(
        questaoNumero = numeroAtual,
        questoesTotal = totalQuestoes,
        onAbrirComentarios = { onAbrirComentarios(questao.id) }
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
    ) {
        CorpoQuestao(
            questao = questao,
            respostaAnterior = respostaAnterior,
            onPodeResolverQuestao = onPodeResolverQuestao,
            onResolver = { respostaSelecionada, acertou ->
                onResponder(respostaSelecionada, acertou)
            },
            onResolvidaComSucesso = onResolvidaComSucesso
        )
    }

    RodapeQuestao(
        podeAnterior = paginaAtual > 0,
        podeProximo = paginaAtual < (totalQuestoes - 1),
        onAnterior = onAnterior,
        onProximo = onProximo,
        onFiltro = onFiltro
    )
}