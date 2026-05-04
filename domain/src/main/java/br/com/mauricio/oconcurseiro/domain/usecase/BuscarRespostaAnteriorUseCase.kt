package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.RespostaAnteriorQuestao
import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract

class BuscarRespostaAnteriorUseCase(
    private val repository: RespostaRepositoryContract
) {

    suspend operator fun invoke(
        usuarioId: String,
        questaoId: String
    ): RespostaAnteriorQuestao? {
        return repository.buscarRespostaAnterior(
            usuarioId = usuarioId,
            questaoId = questaoId
        )
    }
}