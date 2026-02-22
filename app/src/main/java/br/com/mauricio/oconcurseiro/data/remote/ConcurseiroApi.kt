package br.com.mauricio.oconcurseiro.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ConcurseiroApi {

    @GET("/questoes")
    suspend fun listarQuestoes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String? = "criadoEm,desc",
        @Query("texto") texto: String? = null,
        @Query("disciplina") disciplina: String? = null,
        @Query("assunto") assunto: String? = null,
        @Query("banca") banca: String? = null,
        @Query("instituicao") instituicao: String? = null,
        @Query("ano") ano: Int? = null,
        @Query("cargo") cargo: String? = null,
        @Query("nivel") nivel: String? = null,
        @Query("modalidade") modalidade: String? = null
    ): PageResponse<QuestaoDto>
}