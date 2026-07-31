package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract

class MigrarProgressoVisitanteUseCase(
    private val repository: RespostaRepositoryContract
) {
    suspend operator fun invoke(usuarioId: String): Int {
        require(usuarioId.isNotBlank() && usuarioId != "guest") {
            "É necessário um usuário autenticado para preservar o progresso."
        }
        return repository.migrarProgressoVisitante(usuarioId)
    }
}
