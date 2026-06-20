package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.analytics.AnalyticsTracker
import br.com.mauricio.oconcurseiro.domain.model.CatalogoItem
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    private val authRepository: AuthRepository,
    private val analyticsTracker: AnalyticsTracker
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

    var subassuntosPorAssunto: Map<Long, List<CatalogoItem>> by mutableStateOf(emptyMap())
        private set

    var subassuntosCarregando: Boolean by mutableStateOf(false)
        private set

    var catalogosCarregando: Boolean by mutableStateOf(true)
        private set

    private val respondidasNaSessao = mutableSetOf<String>()

    init {
        carregarCatalogos()
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

                val resp = buscarPaginaQuestoesUseCase(
                    page = paginaAtual,
                    size = 1,
                    filtro = filtro
                )

                val resolvedTotal = resp.totalElements.toInt()
                val q = resp.content.firstOrNull()

                uiState = uiState.copy(
                    questao = q,
                    isEmpty = q == null,
                    totalQuestoes = if (q == null) {
                        0
                    } else if (resolvedTotal > 0) {
                        resolvedTotal
                    } else {
                        maxOf(paginaAtual + 1, 1)
                    },
                    numeroAtual = paginaAtual + 1,
                    paginaAtual = paginaAtual
                )

                if (q != null) {
                    analyticsTracker.questionViewed(q)
                    verificarRespostaAnterior(q.id)
                } else {
                    analyticsTracker.emptyResult("questao")
                }

            } catch (e: Exception) {
                analyticsTracker.error(e.javaClass.simpleName, "questao")
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
            val resposta = buscarRespostaAnteriorUseCase(
                usuarioId = authRepository.usuarioIdOuGuest(),
                questaoId = questaoId
            )

            val respostaAnterior = resposta?.let {
                RespostaAnterior(
                    acertou = it.acertou,
                    respostaSelecionada = it.respostaSelecionada,
                    gabarito = it.gabarito
                )
            }

            if (uiState.questao?.id == questaoId) {
                uiState = uiState.copy(respostaAnterior = respostaAnterior)
            }

        } catch (_: Exception) {
        }
    }

    fun salvarResposta(
        questaoId: String,
        disciplina: String,
        respostaSelecionada: String,
        gabarito: String,
        acertou: Boolean
    ) {
        respondidasNaSessao.add(questaoId)
        uiState.questao
            ?.takeIf { it.id == questaoId }
            ?.let { analyticsTracker.questionAnswered(it, acertou) }

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

        viewModelScope.launch {
            try {
                val catalogos = carregarCatalogosQuestoesUseCase()

                disciplinas = catalogos.disciplinas
                bancas = catalogos.bancas
                instituicoes = catalogos.instituicoes
            } catch (_: Exception) {
                disciplinas = emptyList()
                bancas = emptyList()
                instituicoes = emptyList()
            } finally {
                catalogosCarregando = false
            }
        }
    }

    fun carregarAssuntosPorDisciplina(disciplinaId: Long) {
        viewModelScope.launch {
            try {
                assuntos = listarAssuntosPorDisciplinaUseCase(disciplinaId)
                subassuntosPorAssunto = emptyMap()
            } catch (_: Exception) {
                assuntos = emptyList()
                subassuntosPorAssunto = emptyMap()
            }
        }
    }

    fun limparAssuntos() {
        assuntos = emptyList()
        subassuntos = emptyList()
        subassuntosPorAssunto = emptyMap()
        subassuntosCarregando = false
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

    fun carregarSubAssuntosDosAssuntos(assuntoIds: List<Long>) {
        val idsPendentes = assuntoIds
            .distinct()
            .filterNot { subassuntosPorAssunto.containsKey(it) }

        if (idsPendentes.isEmpty() || subassuntosCarregando) return

        viewModelScope.launch {
            subassuntosCarregando = true

            try {
                val carregados = coroutineScope {
                    idsPendentes.map { assuntoId ->
                        async {
                            val subassuntos = try {
                                listarSubAssuntosUseCase(assuntoId)
                            } catch (_: Exception) {
                                emptyList()
                            }
                            assuntoId to subassuntos
                        }
                    }.awaitAll()
                }

                subassuntosPorAssunto = subassuntosPorAssunto + carregados
            } finally {
                subassuntosCarregando = false
            }
        }
    }
}
