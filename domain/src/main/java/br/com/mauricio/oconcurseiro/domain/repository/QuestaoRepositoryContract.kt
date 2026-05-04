package br.com.mauricio.oconcurseiro.domain.repository

import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.PaginaResultado
import br.com.mauricio.oconcurseiro.domain.model.Questao

interface QuestaoRepositoryContract {

    suspend fun buscarPagina(
        page: Int = 0,
        size: Int = 1,
        filtro: FiltroParams = FiltroParams()
    ): PaginaResultado<Questao>
}