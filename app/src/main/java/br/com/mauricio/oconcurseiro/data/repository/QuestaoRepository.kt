package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.mapper.QuestaoMapper
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.data.remote.RetrofitClient

class QuestaoRepository {

    suspend fun listarQuestoes(
        page: Int = 0,
        size: Int = 1,
        texto: String? = null,
        disciplina: String? = null,
        banca: String? = null,
        ano: Int? = null
    ): List<Questao> {
        val resp = RetrofitClient.api.listarQuestoes(
            page = page,
            size = size,
            sort = "criadoEm,desc",
            texto = texto,
            disciplina = disciplina,
            banca = banca,
            ano = ano
        )
        return resp.content.map(QuestaoMapper::fromDto)
    }
}