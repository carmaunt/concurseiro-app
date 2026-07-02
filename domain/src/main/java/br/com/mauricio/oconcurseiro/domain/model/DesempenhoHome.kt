package br.com.mauricio.oconcurseiro.domain.model

data class DesempenhoHome(
    val resolvidas7dias: Int,
    val acertos7dias: Int,
    val erros7dias: Int,
    val totalResolvidas: Int,
    val totalAcertos: Int,
    val desempenhoPorDisciplina: List<DesempenhoDisciplina>,
    val missaoSemanal: List<MissaoDiariaStatus> = emptyList()
)

data class MissaoDiariaStatus(
    val diaSemana: String,
    val resolvidas: Int,
    val status: StatusMissaoDiaria
)

enum class StatusMissaoDiaria {
    CUMPRIDA,
    NAO_CUMPRIDA,
    PENDENTE
}
