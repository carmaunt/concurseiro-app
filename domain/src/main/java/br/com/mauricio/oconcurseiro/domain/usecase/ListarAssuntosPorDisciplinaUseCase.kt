package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.CatalogoItem
import br.com.mauricio.oconcurseiro.domain.repository.QuestaoRepositoryContract

class ListarAssuntosPorDisciplinaUseCase(
    private val repository: QuestaoRepositoryContract
) {

    suspend operator fun invoke(
        disciplinaId: Long
    ): List<CatalogoItem> {
        return repository.listarAssuntosPorDisciplina(disciplinaId)
    }
}