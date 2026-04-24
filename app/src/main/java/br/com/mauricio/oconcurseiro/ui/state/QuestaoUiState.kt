package br.com.mauricio.oconcurseiro.ui.state

import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.ui.viewmodel.RespostaAnterior

data class QuestaoUiState(
    val isLoading: Boolean = false,
    val erro: String? = null,
    val questao: Questao? = null,
    val isEmpty: Boolean = false,
    val numeroAtual: Int = 1,
    val totalQuestoes: Int = 0,
    val paginaAtual: Int = 0,
    val respostaAnterior: RespostaAnterior? = null,
    val jaCarregou: Boolean = false
)