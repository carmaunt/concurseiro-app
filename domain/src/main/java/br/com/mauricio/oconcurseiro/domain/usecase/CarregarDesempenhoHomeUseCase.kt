package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.DesempenhoHome
import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract

class CarregarDesempenhoHomeUseCase(
    private val repository: RespostaRepositoryContract
) {

    suspend operator fun invoke(
        usuarioId: String,
        desde: Long
    ): DesempenhoHome {
        return repository.carregarDesempenhoHome(
            usuarioId = usuarioId,
            desde = desde
        )
    }
}
