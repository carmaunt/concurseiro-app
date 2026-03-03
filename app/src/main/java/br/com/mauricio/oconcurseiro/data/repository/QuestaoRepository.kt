package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.data.remote.CatalogoItemDto
import br.com.mauricio.oconcurseiro.data.remote.ComentarioRequestDto
import br.com.mauricio.oconcurseiro.data.remote.ComentarioResponseDto
import br.com.mauricio.oconcurseiro.data.remote.PageResponse
import br.com.mauricio.oconcurseiro.data.remote.QuestaoDto
import br.com.mauricio.oconcurseiro.data.remote.RetrofitClient

class QuestaoRepository {

    suspend fun buscarPagina(
        page: Int = 0,
        size: Int = 1,
        filtro: FiltroParams = FiltroParams()
    ): PageResponse<QuestaoDto> {
        return RetrofitClient.api.listarQuestoes(
            page = page,
            size = size,
            sort = "criadoEm,desc",
            texto = filtro.texto,
            disciplina = if (filtro.disciplinaId != null) null else filtro.disciplina,
            disciplinaId = filtro.disciplinaId,
            assunto = if (filtro.assuntoId != null) null else filtro.assunto,
            assuntoId = filtro.assuntoId,
            banca = if (filtro.bancaId != null) null else filtro.banca,
            bancaId = filtro.bancaId,
            instituicao = if (filtro.instituicaoId != null) null else filtro.instituicao,
            instituicaoId = filtro.instituicaoId,
            ano = filtro.ano,
            cargo = filtro.cargo,
            nivel = filtro.nivel,
            modalidade = filtro.modalidade
        ).data
    }

    suspend fun buscarQuestao(idQuestion: String): QuestaoDto {
        return RetrofitClient.api.buscarQuestao(idQuestion).data
    }

    suspend fun listarDisciplinas(): List<CatalogoItemDto> {
        return RetrofitClient.api.listarDisciplinas().data
    }

    suspend fun listarAssuntosPorDisciplina(disciplinaId: Long): List<CatalogoItemDto> {
        return RetrofitClient.api.listarAssuntosPorDisciplina(disciplinaId).data
    }

    suspend fun listarBancas(): List<CatalogoItemDto> {
        return RetrofitClient.api.listarBancas().data
    }

    suspend fun listarInstituicoes(): List<CatalogoItemDto> {
        return RetrofitClient.api.listarInstituicoes().data
    }

    suspend fun listarSubAssuntos(assuntoId: Long): List<CatalogoItemDto> {
        return RetrofitClient.api.listarSubAssuntos(assuntoId).data
    }

    suspend fun listarComentarios(
        questaoId: String,
        page: Int = 0,
        size: Int = 20,
        ordenar: String = "curtidas"
    ): PageResponse<ComentarioResponseDto> {
        return RetrofitClient.api.listarComentarios(questaoId, page, size, ordenar).data
    }

    suspend fun criarComentario(
        questaoId: String,
        autor: String,
        texto: String
    ): ComentarioResponseDto {
        return RetrofitClient.api.criarComentario(questaoId, ComentarioRequestDto(autor, texto)).data
    }

    suspend fun curtirComentario(id: Long): ComentarioResponseDto {
        return RetrofitClient.api.curtirComentario(id).data
    }

    suspend fun descurtirComentario(id: Long): ComentarioResponseDto {
        return RetrofitClient.api.descurtirComentario(id).data
    }
}
