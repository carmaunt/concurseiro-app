package br.com.mauricio.oconcurseiro.domain.repository

import br.com.mauricio.oconcurseiro.domain.model.CatalogoItem
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.PaginaResultado
import br.com.mauricio.oconcurseiro.domain.model.Questao

interface QuestaoRepositoryContract {

    suspend fun buscarPagina(
        page: Int = 0,
        size: Int = 1,
        filtro: FiltroParams = FiltroParams()
    ): PaginaResultado<Questao>

    suspend fun listarDisciplinas(): List<CatalogoItem>

    suspend fun listarBancas(): List<CatalogoItem>

    suspend fun listarInstituicoes(): List<CatalogoItem>

    suspend fun listarAssuntosPorDisciplina(
        disciplinaId: Long
    ): List<CatalogoItem>

    suspend fun listarSubAssuntos(
        assuntoId: Long
    ): List<CatalogoItem>
}