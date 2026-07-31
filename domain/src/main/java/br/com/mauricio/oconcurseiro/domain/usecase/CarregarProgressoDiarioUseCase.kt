package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.DailyStudyProgress
import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract

class CarregarProgressoDiarioUseCase(
    private val repository: RespostaRepositoryContract
) {
    suspend operator fun invoke(usuarioId: String): DailyStudyProgress {
        return repository.carregarProgressoDiario(usuarioId)
    }
}
