package br.com.mauricio.oconcurseiro.domain.repository

import br.com.mauricio.oconcurseiro.domain.model.Comentario
import br.com.mauricio.oconcurseiro.domain.model.PaginaResultado

interface ComentarioRepositoryContract {

    suspend fun listarComentarios(
        questaoId: String,
        page: Int,
        size: Int,
        ordenar: String
    ): PaginaResultado<Comentario>

    suspend fun criarComentario(
        questaoId: String,
        autor: String,
        texto: String
    ): Comentario

    suspend fun curtirComentario(comentarioId: Long): Comentario

    suspend fun descurtirComentario(comentarioId: Long): Comentario
}
