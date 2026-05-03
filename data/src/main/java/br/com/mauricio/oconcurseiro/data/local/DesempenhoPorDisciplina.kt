package br.com.mauricio.oconcurseiro.data.local

data class DesempenhoPorDisciplina(
    val disciplina: String,
    val total: Int,
    val acertos: Int
) {
    val aproveitamento: Int
        get() = if (total > 0) (acertos * 100 / total) else 0
}
