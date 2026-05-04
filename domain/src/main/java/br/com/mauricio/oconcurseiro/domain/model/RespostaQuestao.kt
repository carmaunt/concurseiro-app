package br.com.mauricio.oconcurseiro.domain.model

data class RespostaQuestao(
    val usuarioId: String,
    val questaoId: String,
    val disciplina: String,
    val acertou: Boolean,
    val respostaSelecionada: String,
    val gabarito: String
)

data class RespostaAnteriorQuestao(
    val acertou: Boolean,
    val respostaSelecionada: String,
    val gabarito: String
)