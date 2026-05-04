package br.com.mauricio.oconcurseiro.domain.model

data class DesempenhoDisciplina(
    val disciplina: String,
    val total: Int,
    val acertos: Int
) {
    val aproveitamento: Int
        get() = if (total > 0) (acertos * 100 / total) else 0
}
