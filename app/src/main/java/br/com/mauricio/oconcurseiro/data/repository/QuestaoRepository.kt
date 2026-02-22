package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.remote.PageResponse
import br.com.mauricio.oconcurseiro.data.remote.QuestaoDto
import br.com.mauricio.oconcurseiro.data.remote.RetrofitClient

class QuestaoRepository {

    suspend fun buscarPagina(
        page: Int = 0,
        size: Int = 1,
        texto: String? = null,
        disciplina: String? = null,
        banca: String? = null,
        ano: Int? = null
    ): PageResponse<QuestaoDto> {
        return RetrofitClient.api.listarQuestoes(
            page = page,
            size = size,
            sort = "criadoEm,desc",
            texto = texto,
            disciplina = disciplina,
            banca = banca,
            ano = ano
        )
    }
}