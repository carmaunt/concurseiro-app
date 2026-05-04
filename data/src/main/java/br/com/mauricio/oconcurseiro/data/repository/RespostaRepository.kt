package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import br.com.mauricio.oconcurseiro.data.local.RespostaEntity
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
}