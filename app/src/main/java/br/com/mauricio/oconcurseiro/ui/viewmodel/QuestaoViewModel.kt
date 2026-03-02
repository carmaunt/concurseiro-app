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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import br.com.mauricio.oconcurseiro.data.mapper.QuestaoMapper
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class QuestaoViewModel : ViewModel() {

    private val repository = QuestaoRepository()

    var questao: Questao? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set

    var erro: String? by mutableStateOf(null)
        private set

    var isEmpty: Boolean by mutableStateOf(false)
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

    var jaCarregou: Boolean by mutableStateOf(false)
        private set

    init {
        carregarCatalogos()
    }

    private fun mapErrorMessage(e: Exception): String {
        return when (e) {
            is UnknownHostException -> "Sem conexão com a internet"
            is SocketTimeoutException -> "Servidor não respondeu. Tente novamente."
            is HttpException -> {
                when (e.code()) {
                    400 -> "Requisição inválida"
                    404 -> "Recurso não encontrado"
                    500 -> "Erro interno do servidor"
                    503 -> "Servidor indisponível no momento"
                    else -> "Erro do servidor (${e.code()})"
                }
            }
            else -> e.message ?: "Falha ao carregar questão."
        }
    }

    fun carregarQuestao(filtro: FiltroParams = filtroAtual) {
        filtroAtual = filtro
        isLoading = true
        erro = null
        isEmpty = false
        jaCarregou = true

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
                    isEmpty = true
                    totalQuestoes = 0
                } else {
                    numeroAtual = paginaAtual + 1
                }

            } catch (e: Exception) {
                questao = null
                erro = mapErrorMessage(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun recarregar() {
        carregarQuestao(filtroAtual)
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
            } catch (_: Exception) {
                delay(2000)
                try {
                    disciplinas = repository.listarDisciplinas().map { QuestaoMapper.catalogoFromDto(it) }
                } catch (_: Exception) { }
            }
            verificarCompleto()
        }
        viewModelScope.launch {
            try {
                bancas = repository.listarBancas().map { QuestaoMapper.catalogoFromDto(it) }
            } catch (_: Exception) {
                delay(2000)
                try {
                    bancas = repository.listarBancas().map { QuestaoMapper.catalogoFromDto(it) }
                } catch (_: Exception) { }
            }
            verificarCompleto()
        }
        viewModelScope.launch {
            try {
                instituicoes = repository.listarInstituicoes().map { QuestaoMapper.catalogoFromDto(it) }
            } catch (_: Exception) {
                delay(2000)
                try {
                    instituicoes = repository.listarInstituicoes().map { QuestaoMapper.catalogoFromDto(it) }
                } catch (_: Exception) { }
            }
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
