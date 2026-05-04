package br.com.mauricio.oconcurseiro.domain.repository

import br.com.mauricio.oconcurseiro.domain.model.RespostaAnteriorQuestao
import br.com.mauricio.oconcurseiro.domain.model.RespostaQuestao

interface RespostaRepositoryContract {

    suspend fun salvarResposta(resposta: RespostaQuestao)

    suspend fun buscarRespostaAnterior(
        usuarioId: String,
        questaoId: String
    ): RespostaAnteriorQuestao?
}