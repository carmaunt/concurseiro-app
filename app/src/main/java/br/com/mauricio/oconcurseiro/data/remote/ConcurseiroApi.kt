package br.com.mauricio.oconcurseiro.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ConcurseiroApi {

    @GET("/api/v1/questoes")
    suspend fun listarQuestoes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String? = "criadoEm,desc",
        @Query("texto") texto: String? = null,
        @Query("disciplina") disciplina: String? = null,
        @Query("disciplinaId") disciplinaId: Long? = null,
        @Query("assunto") assunto: String? = null,
        @Query("assuntoId") assuntoId: Long? = null,
        @Query("banca") banca: String? = null,
        @Query("bancaId") bancaId: Long? = null,
        @Query("instituicao") instituicao: String? = null,
        @Query("instituicaoId") instituicaoId: Long? = null,
        @Query("ano") ano: Int? = null,
        @Query("cargo") cargo: String? = null,
        @Query("nivel") nivel: String? = null,
        @Query("modalidade") modalidade: String? = null
    ): ApiResponse<PageResponse<QuestaoDto>>

    @GET("/api/v1/questoes/{idQuestion}")
    suspend fun buscarQuestao(
        @Path("idQuestion") idQuestion: String
    ): ApiResponse<QuestaoDto>

    @GET("/api/v1/catalogo/disciplinas")
    suspend fun listarDisciplinas(): ApiResponse<List<CatalogoItemDto>>

    @GET("/api/v1/catalogo/disciplinas/{disciplinaId}/assuntos")
    suspend fun listarAssuntosPorDisciplina(
        @Path("disciplinaId") disciplinaId: Long
    ): ApiResponse<List<CatalogoItemDto>>

    @GET("/api/v1/catalogo/bancas")
    suspend fun listarBancas(): ApiResponse<List<CatalogoItemDto>>

    @GET("/api/v1/catalogo/instituicoes")
    suspend fun listarInstituicoes(): ApiResponse<List<CatalogoItemDto>>

    @GET("/api/v1/catalogo/assuntos/{assuntoId}/subassuntos")
    suspend fun listarSubAssuntos(
        @Path("assuntoId") assuntoId: Long
    ): ApiResponse<List<CatalogoItemDto>>

    @GET("/api/v1/questoes/{questaoId}/comentarios")
    suspend fun listarComentarios(
        @Path("questaoId") questaoId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("ordenar") ordenar: String = "curtidas"
    ): ApiResponse<PageResponse<ComentarioResponseDto>>

    @POST("/api/v1/questoes/{questaoId}/comentarios")
    suspend fun criarComentario(
        @Path("questaoId") questaoId: String,
        @Body request: ComentarioRequestDto
    ): ApiResponse<ComentarioResponseDto>

    @POST("/api/v1/comentarios/{id}/curtir")
    suspend fun curtirComentario(
        @Path("id") id: Long
    ): ApiResponse<ComentarioResponseDto>

    @POST("/api/v1/comentarios/{id}/descurtir")
    suspend fun descurtirComentario(
        @Path("id") id: Long
    ): ApiResponse<ComentarioResponseDto>
}
