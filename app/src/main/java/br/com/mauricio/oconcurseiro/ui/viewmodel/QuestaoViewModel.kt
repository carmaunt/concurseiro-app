package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.model.CatalogoItem
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import kotlinx.coroutines.launch
import br.com.mauricio.oconcurseiro.data.mapper.QuestaoMapper

class QuestaoViewModel : ViewModel() {

    private val repository = QuestaoRepository()

    var questao: Questao? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set

    var erro: String? by mutableStateOf(null)
        private set

    var numeroAtual: Int by mutableStateOf(1)
        private set

    var totalQuestoes: Int by mutableStateOf(0)
        private set

    var paginaAtual: Int by mutableStateOf(0)
        private set

    var filtroAtual: FiltroParams by mutableStateOf(FiltroParams())
        private set

    var disciplinas: List<CatalogoItem> by mutableStateOf(emptyList())
        private set

    var bancas: List<CatalogoItem> by mutableStateOf(emptyList())
        private set

    var instituicoes: List<CatalogoItem> by mutableStateOf(emptyList())
        private set

    var assuntos: List<CatalogoItem> by mutableStateOf(emptyList())
        private set

    var subassuntos: List<CatalogoItem> by mutableStateOf(emptyList())
        private set

    var catalogosCarregando: Boolean by mutableStateOf(true)
        private set

    init {
        carregarQuestao(FiltroParams())
        carregarCatalogos()
    }

    fun carregarQuestao(filtro: FiltroParams = filtroAtual) {
        filtroAtual = filtro
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                val resp = repository.buscarPagina(
                    page = paginaAtual,
                    size = 1,
                    filtro = filtro
                )

                totalQuestoes = resp.totalElements.toInt()

                val dto = resp.content.firstOrNull()
                val q = dto?.let { QuestaoMapper.fromDto(it) }
                questao = q

                if (q == null) {
                    erro = "Nenhuma questão encontrada para esse filtro."
                    totalQuestoes = 0
                } else {
                    numeroAtual = paginaAtual + 1
                }

            } catch (e: Exception) {
                questao = null
                erro = e.message ?: "Falha ao carregar questão."
            } finally {
                isLoading = false
            }
        }
    }

    fun aplicarFiltro(filtro: FiltroParams) {
        paginaAtual = 0
        carregarQuestao(filtro)
    }

    fun proxima(filtro: FiltroParams = filtroAtual) {
        val ultimaPagina = if (totalQuestoes == 0) 0 else (totalQuestoes - 1) / 1
        if (paginaAtual < ultimaPagina) {
            paginaAtual++
            carregarQuestao(filtro)
        }
    }

    fun anterior(filtro: FiltroParams = filtroAtual) {
        if (paginaAtual > 0) {
            paginaAtual--
            carregarQuestao(filtro)
        }
    }

    private fun carregarCatalogos() {
        catalogosCarregando = true
        var pendentes = 3

        fun verificarCompleto() {
            pendentes--
            if (pendentes <= 0) catalogosCarregando = false
        }

        viewModelScope.launch {
            try {
                disciplinas = repository.listarDisciplinas().map { QuestaoMapper.catalogoFromDto(it) }
            } catch (_: Exception) { }
            verificarCompleto()
        }
        viewModelScope.launch {
            try {
                bancas = repository.listarBancas().map { QuestaoMapper.catalogoFromDto(it) }
            } catch (_: Exception) { }
            verificarCompleto()
        }
        viewModelScope.launch {
            try {
                instituicoes = repository.listarInstituicoes().map { QuestaoMapper.catalogoFromDto(it) }
            } catch (_: Exception) { }
            verificarCompleto()
        }
    }

    fun carregarAssuntosPorDisciplina(disciplinaId: Long) {
        viewModelScope.launch {
            try {
                assuntos = repository.listarAssuntosPorDisciplina(disciplinaId)
                    .map { QuestaoMapper.catalogoFromDto(it) }
            } catch (_: Exception) {
                assuntos = emptyList()
            }
        }
    }

    fun limparAssuntos() {
        assuntos = emptyList()
        subassuntos = emptyList()
    }

    fun carregarSubAssuntos(assuntoId: Long) {
        viewModelScope.launch {
            try {
                subassuntos = repository.listarSubAssuntos(assuntoId)
                    .map { QuestaoMapper.catalogoFromDto(it) }
            } catch (_: Exception) {
                subassuntos = emptyList()
            }
        }
    }
}
