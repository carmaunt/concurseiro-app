package br.com.mauricio.oconcurseiro.domain.model

data class DesempenhoHome(
    val resolvidas7dias: Int,
    val acertos7dias: Int,
    val erros7dias: Int,
    val totalResolvidas: Int,
    val totalAcertos: Int,
    val desempenhoPorDisciplina: List<DesempenhoDisciplina>,
    val missaoSemanal: List<MissaoDiariaStatus> = emptyList(),
    val resolvidasHoje: Int = 0,
    val metaDiaria: Int = 5,
    val sequenciaAtual: Int = 0
)

data class MissaoDiariaStatus(
    val diaSemana: String,
    val resolvidas: Int,
    val status: StatusMissaoDiaria
)

data class DailyStudyProgress(
    val answered: Int,
    val goal: Int
) {
    val completed: Boolean
        get() = answered >= goal
}

enum class StatusMissaoDiaria {
    CUMPRIDA,
    NAO_CUMPRIDA,
    PENDENTE
}

object StudyStreakCalculator {
    /**
     * Recebe dias em ordem decrescente, começando por hoje.
     * Se a meta de hoje ainda não foi concluída, preserva a sequência até ontem.
     */
    fun currentStreak(completedFromToday: List<Boolean>): Int {
        if (completedFromToday.isEmpty()) return 0
        val startIndex = if (completedFromToday.first()) 0 else 1
        return completedFromToday
            .drop(startIndex)
            .takeWhile { it }
            .size
    }
}
