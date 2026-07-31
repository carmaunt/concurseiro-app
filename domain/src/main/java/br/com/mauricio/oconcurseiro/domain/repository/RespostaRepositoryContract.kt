package br.com.mauricio.oconcurseiro.domain.repository

import br.com.mauricio.oconcurseiro.domain.model.DesempenhoHome
import br.com.mauricio.oconcurseiro.domain.model.DailyStudyProgress
import br.com.mauricio.oconcurseiro.domain.model.RespostaAnteriorQuestao
import br.com.mauricio.oconcurseiro.domain.model.RespostaQuestao

interface RespostaRepositoryContract {

    suspend fun salvarResposta(resposta: RespostaQuestao)

    suspend fun buscarRespostaAnterior(
        usuarioId: String,
        questaoId: String
    ): RespostaAnteriorQuestao?

    suspend fun carregarDesempenhoHome(
        usuarioId: String,
        desde: Long
    ): DesempenhoHome

    suspend fun migrarProgressoVisitante(usuarioId: String): Int

    suspend fun carregarProgressoDiario(usuarioId: String): DailyStudyProgress
}
