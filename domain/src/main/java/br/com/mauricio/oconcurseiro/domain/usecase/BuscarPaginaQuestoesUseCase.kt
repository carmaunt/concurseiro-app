package br.com.mauricio.oconcurseiro.domain.usecase

import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.PaginaResultado
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.domain.repository.QuestaoRepositoryContract

class BuscarPaginaQuestoesUseCase(
    private val repository: QuestaoRepositoryContract
) {

    suspend operator fun invoke(
        page: Int = 0,
        size: Int = 1,
        filtro: FiltroParams = FiltroParams()
    ): PaginaResultado<Questao> {
        return repository.buscarPagina(
            page = page,
            size = size,
            filtro = filtro
        )
    }
}