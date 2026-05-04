package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.RespostaQuestao
import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract

class SalvarRespostaQuestaoUseCase(
    private val repository: RespostaRepositoryContract
) {

    suspend operator fun invoke(resposta: RespostaQuestao) {
        repository.salvarResposta(resposta)
    }
}