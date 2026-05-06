package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.Comentario
import br.com.mauricio.oconcurseiro.domain.model.PaginaResultado
import br.com.mauricio.oconcurseiro.domain.repository.ComentarioRepositoryContract

class ListarComentariosUseCase(
    private val repository: ComentarioRepositoryContract
) {

    suspend operator fun invoke(
        questaoId: String,
        page: Int = 0,
        size: Int = 20,
        ordenar: String = "curtidas"
    ): PaginaResultado<Comentario> {
        return repository.listarComentarios(
            questaoId = questaoId,
            page = page,
            size = size,
            ordenar = ordenar
        )
    }
}
