package br.com.mauricio.oconcurseiro.domain.model

data class DesempenhoHome(
    val resolvidas7dias: Int,
    val acertos7dias: Int,
    val erros7dias: Int,
    val totalResolvidas: Int,
    val totalAcertos: Int,
    val desempenhoPorDisciplina: List<DesempenhoDisciplina>
)
