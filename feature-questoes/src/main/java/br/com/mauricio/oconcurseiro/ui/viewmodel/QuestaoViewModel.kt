package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.domain.model.CatalogoItem
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.domain.model.RespostaQuestao
import br.com.mauricio.oconcurseiro.domain.usecase.BuscarPaginaQuestoesUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.BuscarRespostaAnteriorUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.CarregarCatalogosQuestoesUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.ListarAssuntosPorDisciplinaUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.ListarSubAssuntosUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.SalvarRespostaQuestaoUseCase
import br.com.mauricio.oconcurseiro.ui.state.QuestaoUiState
import br.com.mauricio.oconcurseiro.util.mapErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RespostaAnterior(
    val acertou: Boolean,
    val respostaSelecionada: String,
    val gabarito: String
)

@HiltViewModel
class QuestaoViewModel @Inject constructor(
    private val buscarPaginaQuestoesUseCase: BuscarPaginaQuestoesUseCase,
    private val carregarCatalogosQuestoesUseCase: CarregarCatalogosQuestoesUseCase,
    private val listarAssuntosPorDisciplinaUseCase: ListarAssuntosPorDisciplinaUseCase,
    private val listarSubAssuntosUseCase: ListarSubAssuntosUseCase,
    private val salvarRespostaQuestaoUseCase: SalvarRespostaQuestaoUseCase,
    private val buscarRespostaAnteriorUseCase: BuscarRespostaAnteriorUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val BATCH_SIZE = 10
        private const val PRE_FETCH_THRESHOLD = 3
    }

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

    // --- Batch pre-loading state (internal, not exposed in UiState) ---
    private var loteAtual: List<Questao> = emptyList()
    private var indexNoLote: Int = 0
    private var lotePagina: Int = 0
    private var totalPaginas: Int = 0
    private var proxLoteCache: List<Questao>? = null
    private var proxLotePaginaCache: Int = -1
    private var preFetchJob: Job? = null

    private val respondidasNaSessao = mutableSetOf<String>()

    init {
        carregarCatalogos()
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    fun carregarQuestao(filtro: FiltroParams = filtroAtual) {
        filtroAtual = filtro
        resetarLote()

        uiState = uiState.copy(
            isLoading = true,
            erro = null,
            jaCarregou = true,
            respostaAnterior = null,
            isPreCarregando = false
        )

        viewModelScope.launch {
            try {
                val resp = buscarPaginaQuestoesUseCase(
                    page = 0,
                    size = BATCH_SIZE,
                    filtro = filtro
                )

                loteAtual = resp.content
                lotePagina = 0
                indexNoLote = 0
                totalPaginas = resp.totalPages

                val resolvedTotal = resp.totalElements.toInt()
                val q = loteAtual.firstOrNull()

                uiState = uiState.copy(
                    questao = q,
                    isEmpty = q == null,
                    isLoading = false,
                    totalQuestoes = when {
                        q == null         -> 0
                        resolvedTotal > 0 -> resolvedTotal
                        else              -> maxOf(BATCH_SIZE, 1)
                    },
                    numeroAtual = 1,
                    paginaAtual = 0
                )

                if (q != null) {
                    verificarRespostaAnterior(q.id)
                    agendarPreFetch()
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    erro = mapErrorMessage(e),
                    questao = null
                )
            }
        }
    }

    fun proxima(filtro: FiltroParams = filtroAtual) {
        val paginaAbsolutaAtual = uiState.paginaAtual
        val total = uiState.totalQuestoes
        if (total > 0 && paginaAbsolutaAtual >= total - 1) return

        if (indexNoLote < loteAtual.size - 1) {
            // Navigate within the current batch — zero network calls
            indexNoLote++
            val q = loteAtual[indexNoLote]
            val novaPaginaAbsoluta = paginaAbsolutaAtual + 1
            uiState = uiState.copy(
                questao = q,
                numeroAtual = novaPaginaAbsoluta + 1,
                paginaAtual = novaPaginaAbsoluta,
                respostaAnterior = null,
                erro = null
            )
            viewModelScope.launch { verificarRespostaAnterior(q.id) }
            agendarPreFetch()
        } else {
            // End of current batch — transition to next batch
            val proxPagina = lotePagina + 1
            if (proxPagina >= totalPaginas) return

            uiState = uiState.copy(isLoading = true, erro = null, respostaAnterior = null)

            viewModelScope.launch {
                try {
                    val questoes = consumirProxLoteCache(proxPagina)
                        ?: carregarLote(proxPagina)

                    if (questoes.isEmpty()) {
                        uiState = uiState.copy(isLoading = false, isEmpty = true)
                        return@launch
                    }

                    loteAtual = questoes
                    lotePagina = proxPagina
                    indexNoLote = 0

                    val q = loteAtual[0]
                    val novaPaginaAbsoluta = paginaAbsolutaAtual + 1
                    uiState = uiState.copy(
                        questao = q,
                        isEmpty = false,
                        isLoading = false,
                        numeroAtual = novaPaginaAbsoluta + 1,
                        paginaAtual = novaPaginaAbsoluta
                    )

                    verificarRespostaAnterior(q.id)
                    agendarPreFetch()

                } catch (e: Exception) {
                    uiState = uiState.copy(isLoading = false, erro = mapErrorMessage(e))
                }
            }
        }
    }

    fun anterior(filtro: FiltroParams = filtroAtual) {
        val paginaAbsolutaAtual = uiState.paginaAtual
        if (paginaAbsolutaAtual <= 0) return

        if (indexNoLote > 0) {
            // Navigate backward within current batch — zero network calls
            indexNoLote--
            val q = loteAtual[indexNoLote]
            val novaPaginaAbsoluta = paginaAbsolutaAtual - 1
            uiState = uiState.copy(
                questao = q,
                numeroAtual = novaPaginaAbsoluta + 1,
                paginaAtual = novaPaginaAbsoluta,
                respostaAnterior = null,
                erro = null
            )
            viewModelScope.launch { verificarRespostaAnterior(q.id) }
        } else if (lotePagina > 0) {
            // Cross batch boundary backward — load previous batch
            val prevPagina = lotePagina - 1
            uiState = uiState.copy(isLoading = true, erro = null, respostaAnterior = null)

            viewModelScope.launch {
                try {
                    val questoes = carregarLote(prevPagina)

                    if (questoes.isEmpty()) {
                        uiState = uiState.copy(isLoading = false)
                        return@launch
                    }

                    loteAtual = questoes
                    lotePagina = prevPagina
                    indexNoLote = loteAtual.size - 1

                    val q = loteAtual[indexNoLote]
                    val novaPaginaAbsoluta = paginaAbsolutaAtual - 1
                    uiState = uiState.copy(
                        questao = q,
                        isEmpty = false,
                        isLoading = false,
                        numeroAtual = novaPaginaAbsoluta + 1,
                        paginaAtual = novaPaginaAbsoluta
                    )

                    verificarRespostaAnterior(q.id)

                } catch (e: Exception) {
                    uiState = uiState.copy(isLoading = false, erro = mapErrorMessage(e))
                }
            }
        }
    }

    fun recarregar() {
        carregarQuestao(filtroAtual)
    }

    fun aplicarFiltro(filtro: FiltroParams) {
        carregarQuestao(filtro)
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
                salvarRespostaQuestaoUseCase(
                    RespostaQuestao(
                        usuarioId = authRepository.usuarioIdOuGuest(),
                        questaoId = questaoId,
                        disciplina = disciplina,
                        acertou = acertou,
                        respostaSelecionada = respostaSelecionada,
                        gabarito = gabarito
                    )
                )
            } catch (_: Exception) {
            }
        }
    }

    fun carregarAssuntosPorDisciplina(disciplinaId: Long) {
        viewModelScope.launch {
            try {
                assuntos = listarAssuntosPorDisciplinaUseCase(disciplinaId)
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
                subassuntos = listarSubAssuntosUseCase(assuntoId)
            } catch (_: Exception) {
                subassuntos = emptyList()
            }
        }
    }

    fun limparSubAssuntos() {
        subassuntos = emptyList()
    }

    // -------------------------------------------------------------------------
    // Batch pre-loading helpers (private)
    // -------------------------------------------------------------------------

    private fun agendarPreFetch() {
        val proxPagina = lotePagina + 1
        if (proxPagina >= totalPaginas) return
        if (proxLotePaginaCache == proxPagina) return

        val questoesRestantes = loteAtual.size - 1 - indexNoLote
        if (questoesRestantes > PRE_FETCH_THRESHOLD) return

        preFetchJob?.cancel()
        preFetchJob = viewModelScope.launch {
            uiState = uiState.copy(isPreCarregando = true)
            try {
                val resp = buscarPaginaQuestoesUseCase(
                    page = proxPagina,
                    size = BATCH_SIZE,
                    filtro = filtroAtual
                )
                if (resp.content.isNotEmpty()) {
                    proxLoteCache = resp.content
                    proxLotePaginaCache = proxPagina
                }
            } catch (_: Exception) {
                proxLoteCache = null
                proxLotePaginaCache = -1
            } finally {
                uiState = uiState.copy(isPreCarregando = false)
            }
        }
    }

    private suspend fun carregarLote(pagina: Int): List<Questao> {
        val resp = buscarPaginaQuestoesUseCase(
            page = pagina,
            size = BATCH_SIZE,
            filtro = filtroAtual
        )
        return resp.content
    }

    private fun consumirProxLoteCache(pagina: Int): List<Questao>? {
        if (proxLotePaginaCache != pagina) return null
        val cached = proxLoteCache ?: return null
        proxLoteCache = null
        proxLotePaginaCache = -1
        preFetchJob?.cancel()
        preFetchJob = null
        return cached
    }

    private fun resetarLote() {
        preFetchJob?.cancel()
        preFetchJob = null
        loteAtual = emptyList()
        indexNoLote = 0
        lotePagina = 0
        totalPaginas = 0
        proxLoteCache = null
        proxLotePaginaCache = -1
    }

    private suspend fun verificarRespostaAnterior(questaoId: String) {
        if (questaoId in respondidasNaSessao) {
            uiState = uiState.copy(respostaAnterior = null)
            return
        }

        try {
            val resposta = buscarRespostaAnteriorUseCase(
                usuarioId = authRepository.usuarioIdOuGuest(),
                questaoId = questaoId
            )

            uiState = uiState.copy(
                respostaAnterior = resposta?.let {
                    RespostaAnterior(
                        acertou = it.acertou,
                        respostaSelecionada = it.respostaSelecionada,
                        gabarito = it.gabarito
                    )
                }
            )
        } catch (_: Exception) {
        }
    }

    private fun carregarCatalogos() {
        viewModelScope.launch {
            catalogosCarregando = true
            var tentativa = 0
            var sucesso = false

            while (tentativa < 3 && !sucesso) {
                if (tentativa > 0) delay(1500L * tentativa)
                try {
                    val catalogos = carregarCatalogosQuestoesUseCase()
                    disciplinas = catalogos.disciplinas
                    bancas = catalogos.bancas
                    instituicoes = catalogos.instituicoes
                    sucesso = true
                } catch (_: Exception) {
                    tentativa++
                    if (tentativa >= 3) {
                        disciplinas = emptyList()
                        bancas = emptyList()
                        instituicoes = emptyList()
                    }
                }
            }

            catalogosCarregando = false
        }
    }
}
