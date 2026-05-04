package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.CatalogosQuestoes
import br.com.mauricio.oconcurseiro.domain.repository.QuestaoRepositoryContract

class CarregarCatalogosQuestoesUseCase(
    private val repository: QuestaoRepositoryContract
) {

    suspend operator fun invoke(): CatalogosQuestoes {
        return CatalogosQuestoes(
            disciplinas = repository.listarDisciplinas(),
            bancas = repository.listarBancas(),
            instituicoes = repository.listarInstituicoes()
        )
    }
}