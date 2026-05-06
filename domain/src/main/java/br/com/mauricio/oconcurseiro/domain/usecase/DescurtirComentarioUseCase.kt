package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.Comentario
import br.com.mauricio.oconcurseiro.domain.repository.ComentarioRepositoryContract

class DescurtirComentarioUseCase(
    private val repository: ComentarioRepositoryContract
) {

    suspend operator fun invoke(comentarioId: Long): Comentario {
        return repository.descurtirComentario(comentarioId)
    }
}
