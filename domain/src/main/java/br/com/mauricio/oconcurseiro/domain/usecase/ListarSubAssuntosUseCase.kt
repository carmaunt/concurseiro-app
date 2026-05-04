package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.CatalogoItem
import br.com.mauricio.oconcurseiro.domain.repository.QuestaoRepositoryContract

class ListarSubAssuntosUseCase(
    private val repository: QuestaoRepositoryContract
) {

    suspend operator fun invoke(
        assuntoId: Long
    ): List<CatalogoItem> {
        return repository.listarSubAssuntos(assuntoId)
    }
}