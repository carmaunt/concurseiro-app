package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import br.com.mauricio.oconcurseiro.data.local.RespostaEntity
import br.com.mauricio.oconcurseiro.domain.model.DesempenhoDisciplina
import br.com.mauricio.oconcurseiro.domain.model.DesempenhoHome
import br.com.mauricio.oconcurseiro.domain.model.MissaoDiariaStatus
import br.com.mauricio.oconcurseiro.domain.model.RespostaAnteriorQuestao
import br.com.mauricio.oconcurseiro.domain.model.RespostaQuestao
import br.com.mauricio.oconcurseiro.domain.model.StatusMissaoDiaria
import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract
import java.util.Calendar
import javax.inject.Inject

class RespostaRepository @Inject constructor(
    private val respostaDao: RespostaDao
) : RespostaRepositoryContract {

    private companion object {
        const val DAILY_MISSION_TARGET = 5
    }

    override suspend fun salvarResposta(resposta: RespostaQuestao) {
        respostaDao.inserir(
            RespostaEntity(
                usuarioId = resposta.usuarioId,
                questaoId = resposta.questaoId,
                disciplina = resposta.disciplina,
                acertou = resposta.acertou,
                respostaSelecionada = resposta.respostaSelecionada,
                gabarito = resposta.gabarito
            )
        )
    }

    override suspend fun buscarRespostaAnterior(
        usuarioId: String,
        questaoId: String
    ): RespostaAnteriorQuestao? {
        return respostaDao.ultimaRespostaPorQuestao(
            usuarioId = usuarioId,
            questaoId = questaoId
        )?.let { resposta ->
            RespostaAnteriorQuestao(
                acertou = resposta.acertou,
                respostaSelecionada = resposta.respostaSelecionada,
                gabarito = resposta.gabarito
            )
        }
    }

    override suspend fun carregarDesempenhoHome(
        usuarioId: String,
        desde: Long
    ): DesempenhoHome {
        return DesempenhoHome(
            resolvidas7dias = respostaDao.totalRespostasDesde(usuarioId, desde),
            acertos7dias = respostaDao.totalAcertosDesde(usuarioId, desde),
            erros7dias = respostaDao.totalErrosDesde(usuarioId, desde),
            totalResolvidas = respostaDao.totalRespostas(usuarioId),
            totalAcertos = respostaDao.totalAcertos(usuarioId),
            desempenhoPorDisciplina = respostaDao.desempenhoPorDisciplinaDesde(
                usuarioId = usuarioId,
                desde = desde
            ).map { item ->
                DesempenhoDisciplina(
                    disciplina = item.disciplina,
                    total = item.total,
                    acertos = item.acertos
                )
            },
            missaoSemanal = carregarMissaoSemanal(usuarioId)
        )
    }

    private suspend fun carregarMissaoSemanal(usuarioId: String): List<MissaoDiariaStatus> {
        val hoje = Calendar.getInstance()
        val inicioSemana = inicioDaSemana(hoje)
        val fimSemana = (inicioSemana.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 7)
        }

        val respostas = respostaDao.respostasNoPeriodo(
            usuarioId = usuarioId,
            inicio = inicioSemana.timeInMillis,
            fim = fimSemana.timeInMillis
        )

        val questoesPorDia = respostas.groupBy(
            keySelector = { indiceDiaNaSemana(inicioSemana, it.respondidaEm) },
            valueTransform = { it.questaoId }
        ).mapValues { (_, questoes) -> questoes.toSet().size }

        return (0..6).map { indice ->
            val dia = (inicioSemana.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, indice)
            }
            val resolvidas = questoesPorDia[indice] ?: 0

            MissaoDiariaStatus(
                diaSemana = labelDiaSemana(indice),
                resolvidas = resolvidas,
                status = when {
                    resolvidas >= DAILY_MISSION_TARGET -> StatusMissaoDiaria.CUMPRIDA
                    dia.before(inicioDoDia(hoje)) -> StatusMissaoDiaria.NAO_CUMPRIDA
                    else -> StatusMissaoDiaria.PENDENTE
                }
            )
        }
    }

    private fun inicioDaSemana(referencia: Calendar): Calendar {
        return inicioDoDia(referencia).apply {
            firstDayOfWeek = Calendar.MONDAY
            while (get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                add(Calendar.DAY_OF_YEAR, -1)
            }
        }
    }

    private fun inicioDoDia(referencia: Calendar): Calendar {
        return (referencia.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun indiceDiaNaSemana(inicioSemana: Calendar, timestamp: Long): Int {
        val inicioDiaResposta = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val millisPorDia = 24 * 60 * 60 * 1000L
        return ((inicioDiaResposta.timeInMillis - inicioSemana.timeInMillis) / millisPorDia).toInt()
    }

    private fun labelDiaSemana(indice: Int): String {
        return listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom")[indice]
    }
}
