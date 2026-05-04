package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import br.com.mauricio.oconcurseiro.data.local.RespostaEntity
import br.com.mauricio.oconcurseiro.domain.model.DesempenhoDisciplina
import br.com.mauricio.oconcurseiro.domain.model.DesempenhoHome
import br.com.mauricio.oconcurseiro.domain.model.RespostaAnteriorQuestao
import br.com.mauricio.oconcurseiro.domain.model.RespostaQuestao
import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract
import javax.inject.Inject

class RespostaRepository @Inject constructor(
    private val respostaDao: RespostaDao
) : RespostaRepositoryContract {

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
            desempenhoPorDisciplina = respostaDao.desempenhoPorDisciplina(usuarioId).map { item ->
                DesempenhoDisciplina(
                    disciplina = item.disciplina,
                    total = item.total,
                    acertos = item.acertos
                )
            }
        )
    }
}
