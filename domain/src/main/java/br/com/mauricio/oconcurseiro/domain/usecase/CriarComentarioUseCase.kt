package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.Comentario
import br.com.mauricio.oconcurseiro.domain.repository.ComentarioRepositoryContract

class CriarComentarioUseCase(
    private val repository: ComentarioRepositoryContract
) {

    suspend operator fun invoke(
        questaoId: String,
        autor: String,
        texto: String
    ): Comentario {
        return repository.criarComentario(
            questaoId = questaoId,
            autor = autor,
            texto = texto
        )
    }
}
