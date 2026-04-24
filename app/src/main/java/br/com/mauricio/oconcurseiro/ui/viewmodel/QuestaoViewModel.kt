package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.local.RespostaEntity
import br.com.mauricio.oconcurseiro.data.model.CatalogoItem
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import br.com.mauricio.oconcurseiro.data.mapper.QuestaoMapper
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import br.com.mauricio.oconcurseiro.ui.state.QuestaoUiState
import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class RespostaAnterior(
    val acertou: Boolean,
    val respostaSelecionada: String,
    val gabarito: String
)

@HiltViewModel
class QuestaoViewModel @Inject constructor(
    @param:dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val repository: QuestaoRepository,
    private val respostaDao: RespostaDao
) : ViewModel() {

    var uiState by mutableStateOf(QuestaoUiState())
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

    private val respondidasNaSessao = mutableSetOf<String>()
    private val authRepository = AuthRepository()

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

        uiState = uiState.copy(
            isLoading = true,
            erro = null,
            jaCarregou = true,
            respostaAnterior = null
        )

        viewModelScope.launch {
            try {
                val paginaAtual = uiState.paginaAtual

                val resp = repository.buscarPagina(
                    page = paginaAtual,
                    size = 1,
                    filtro = filtro
                )

                val resolvedTotal = resp.resolvedTotalElements.toInt()
                val dto = resp.content.firstOrNull()
                val q = dto?.let { QuestaoMapper.fromDto(it) }

                uiState = uiState.copy(
                    questao = q,
                    isEmpty = q == null,
                    totalQuestoes = if (q == null) 0 else if (resolvedTotal > 0) resolvedTotal else maxOf(paginaAtual + 1, 1),
                    numeroAtual = paginaAtual + 1,
                    paginaAtual = paginaAtual
                )

                if (q != null) {
                    verificarRespostaAnterior(q.id)
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    erro = mapErrorMessage(e),
                    questao = null
                )
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    private suspend fun verificarRespostaAnterior(questaoId: String) {
        if (questaoId in respondidasNaSessao) {
            uiState = uiState.copy(respostaAnterior = null)
            return
        }
        try {
            val resposta = respostaDao.ultimaRespostaPorQuestao(
                authRepository.usuarioIdOuGuest(),
                questaoId
            )

            val resp = resposta?.let {
                RespostaAnterior(
                    acertou = it.acertou,
                    respostaSelecionada = it.respostaSelecionada,
                    gabarito = it.gabarito
                )
            }

            uiState = uiState.copy(respostaAnterior = resp)

        } catch (_: Exception) { }
    }

    fun salvarResposta(
        questaoId: String,
        disciplina: String,
        respostaSelecionada: String,
        gabarito: String,
        acertou: Boolean
    ) {
        respondidasNaSessao.add(questaoId)

        viewModelScope.launch {
            try {
                respostaDao.inserir(
                    RespostaEntity(
                        usuarioId = authRepository.usuarioIdOuGuest(),
                        questaoId = questaoId,
                        disciplina = disciplina,
                        acertou = acertou,
                        respostaSelecionada = respostaSelecionada,
                        gabarito = gabarito
                    )
                )
            } catch (_: Exception) { }
        }
    }

    fun recarregar() {
        carregarQuestao(filtroAtual)
    }

    fun aplicarFiltro(filtro: FiltroParams) {
        uiState = uiState.copy(paginaAtual = 0)
        carregarQuestao(filtro)
    }

    fun proxima(filtro: FiltroParams = filtroAtual) {
        val paginaAtual = uiState.paginaAtual
        val total = uiState.totalQuestoes

        val ultimaPagina = if (total == 0) 0 else (total - 1)

        if (paginaAtual < ultimaPagina) {
            uiState = uiState.copy(paginaAtual = paginaAtual + 1)
            carregarQuestao(filtro)
        }
    }

    fun anterior(filtro: FiltroParams = filtroAtual) {
        val paginaAtual = uiState.paginaAtual

        if (paginaAtual > 0) {
            uiState = uiState.copy(paginaAtual = paginaAtual - 1)
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