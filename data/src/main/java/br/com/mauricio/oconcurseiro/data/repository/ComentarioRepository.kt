package br.com.mauricio.oconcurseiro.data.repository

import br.com.mauricio.oconcurseiro.data.remote.ComentarioRequestDto
import br.com.mauricio.oconcurseiro.data.remote.ComentarioResponseDto
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.domain.model.Comentario
import br.com.mauricio.oconcurseiro.domain.model.PaginaResultado
import br.com.mauricio.oconcurseiro.domain.repository.ComentarioRepositoryContract
import javax.inject.Inject

class ComentarioRepository @Inject constructor(
    private val api: ConcurseiroApi
) : ComentarioRepositoryContract {

    override suspend fun listarComentarios(
        questaoId: String,
        page: Int,
        size: Int,
        ordenar: String
    ): PaginaResultado<Comentario> {
        val response = api.listarComentarios(
            questaoId = questaoId,
            page = page,
            size = size,
            ordenar = ordenar
        ).data

        return PaginaResultado(
            content = response.content.map { it.toDomain() },
            number = response.number,
            size = response.size,
            totalElements = response.resolvedTotalElements,
            totalPages = response.resolvedTotalPages,
            first = response.first,
            last = response.resolvedLast
        )
    }

    override suspend fun criarComentario(
        questaoId: String,
        autor: String,
        texto: String
    ): Comentario {
        return api.criarComentario(
            questaoId = questaoId,
            request = ComentarioRequestDto(autor, texto)
        ).data.toDomain()
    }

    override suspend fun curtirComentario(comentarioId: Long): Comentario {
        return api.curtirComentario(comentarioId).data.toDomain()
    }

    override suspend fun descurtirComentario(comentarioId: Long): Comentario {
        return api.descurtirComentario(comentarioId).data.toDomain()
    }

    private fun ComentarioResponseDto.toDomain(): Comentario {
        return Comentario(
            id = id,
            questaoId = questaoId,
            autor = autor,
            texto = texto,
            curtidas = curtidas,
            descurtidas = descurtidas,
            criadoEm = criadoEm
        )
    }
}
