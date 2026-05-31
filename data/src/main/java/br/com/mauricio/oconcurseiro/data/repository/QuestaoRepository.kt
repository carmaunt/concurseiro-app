package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.mapper.QuestaoMapper
import br.com.mauricio.oconcurseiro.data.remote.CatalogoItemDto
import br.com.mauricio.oconcurseiro.data.remote.ComentarioRequestDto
import br.com.mauricio.oconcurseiro.data.remote.ComentarioResponseDto
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.data.remote.PageResponse
import br.com.mauricio.oconcurseiro.data.remote.QuestaoDto
import br.com.mauricio.oconcurseiro.domain.model.CatalogoItem
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.PaginaResultado
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.domain.repository.QuestaoRepositoryContract
import javax.inject.Inject

class QuestaoRepository @Inject constructor(
    private val api: ConcurseiroApi
) : QuestaoRepositoryContract {

    override suspend fun buscarPagina(
        page: Int,
        size: Int,
        filtro: FiltroParams
    ): PaginaResultado<Questao> {
        val response = buscarPaginaDto(
            page = page,
            size = size,
            filtro = filtro
        )

        return PaginaResultado(
            content = response.content.map { QuestaoMapper.fromDto(it) },
            number = response.number,
            size = response.size,
            totalElements = response.resolvedTotalElements,
            totalPages = response.resolvedTotalPages,
            first = response.first,
            last = response.resolvedLast
        )
    }

    suspend fun buscarPaginaDto(
        page: Int = 0,
        size: Int = 1,
        filtro: FiltroParams = FiltroParams()
    ): PageResponse<QuestaoDto> {
        return api.listarQuestoes(
            page = page,
            size = size,
            sort = "criadoEm,desc",
            texto = filtro.texto,
            disciplina = if (filtro.disciplinaId != null) null else filtro.disciplina,
            disciplinaId = filtro.disciplinaId,
            assunto = if (filtro.assuntoId != null) null else filtro.assunto,
            assuntoId = filtro.assuntoId,
            subassunto = if (filtro.subassuntoId != null) null else filtro.subassunto,
            subassuntoId = filtro.subassuntoId,
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
        return api.buscarQuestao(idQuestion).data
    }

    override suspend fun listarDisciplinas(): List<CatalogoItem> {
        return listarDisciplinasDto().map { QuestaoMapper.catalogoFromDto(it) }
    }

    suspend fun listarDisciplinasDto(): List<CatalogoItemDto> {
        return api.listarDisciplinas().data
    }

    override suspend fun listarAssuntosPorDisciplina(
        disciplinaId: Long
    ): List<CatalogoItem> {
        return listarAssuntosPorDisciplinaDto(disciplinaId)
            .map { QuestaoMapper.catalogoFromDto(it) }
    }

    suspend fun listarAssuntosPorDisciplinaDto(disciplinaId: Long): List<CatalogoItemDto> {
        return api.listarAssuntosPorDisciplina(disciplinaId).data
    }

    override suspend fun listarBancas(): List<CatalogoItem> {
        return listarBancasDto().map { QuestaoMapper.catalogoFromDto(it) }
    }

    suspend fun listarBancasDto(): List<CatalogoItemDto> {
        return api.listarBancas().data
    }

    override suspend fun listarInstituicoes(): List<CatalogoItem> {
        return listarInstituicoesDto().map { QuestaoMapper.catalogoFromDto(it) }
    }

    suspend fun listarInstituicoesDto(): List<CatalogoItemDto> {
        return api.listarInstituicoes().data
    }

    override suspend fun listarSubAssuntos(assuntoId: Long): List<CatalogoItem> {
        return listarSubAssuntosDto(assuntoId)
            .map { QuestaoMapper.catalogoFromDto(it) }
    }

    suspend fun listarSubAssuntosDto(assuntoId: Long): List<CatalogoItemDto> {
        return api.listarSubAssuntos(assuntoId).data
    }

    suspend fun listarComentarios(
        questaoId: String,
        page: Int = 0,
        size: Int = 20,
        ordenar: String = "curtidas"
    ): PageResponse<ComentarioResponseDto> {
        return api.listarComentarios(questaoId, page, size, ordenar).data
    }

    suspend fun criarComentario(
        questaoId: String,
        autor: String,
        texto: String
    ): ComentarioResponseDto {
        return api.criarComentario(
            questaoId = questaoId,
            request = ComentarioRequestDto(autor, texto)
        ).data
    }

    suspend fun curtirComentario(id: Long): ComentarioResponseDto {
        return api.curtirComentario(id).data
    }

    suspend fun descurtirComentario(id: Long): ComentarioResponseDto {
        return api.descurtirComentario(id).data
    }
}
