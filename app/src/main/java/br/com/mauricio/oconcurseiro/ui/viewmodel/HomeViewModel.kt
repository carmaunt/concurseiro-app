package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.usecase.CarregarDesempenhoHomeUseCase
import br.com.mauricio.oconcurseiro.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val repository: QuestaoRepository,
    private val authRepository: AuthRepository,
    private val carregarDesempenhoHomeUseCase: CarregarDesempenhoHomeUseCase
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        carregarEstatisticas()
    }

    fun carregarEstatisticas() {
        uiState = uiState.copy(isLoading = true)
        uiState = uiState.copy(erro = null)

        viewModelScope.launch {
            try {
                val questoesJob = launch {
                    try {
                        val resp = repository.buscarPagina(
                            page = 0,
                            size = 1,
                            filtro = FiltroParams()
                        )
                        uiState = uiState.copy(totalQuestoes = resp.totalElements)
                    } catch (_: Exception) {
                    }
                }

                val disciplinasJob = launch {
                    try {
                        uiState = uiState.copy(totalDisciplinas = repository.listarDisciplinas().size)
                    } catch (_: Exception) {
                    }
                }

                val bancasJob = launch {
                    try {
                        uiState = uiState.copy(totalBancas = repository.listarBancas().size)
                    } catch (_: Exception) {
                    }
                }

                val instituicoesJob = launch {
                    try {
                        uiState = uiState.copy(totalInstituicoes = repository.listarInstituicoes().size)
                    } catch (_: Exception) {
                    }
                }

                val historicoJob = launch {
                    try {
                        carregarDesempenho()
                    } catch (_: Exception) {
                    }
                }

                questoesJob.join()
                disciplinasJob.join()
                bancasJob.join()
                instituicoesJob.join()
                historicoJob.join()

                uiState = uiState.copy(
                    statsCarregadas = uiState.totalQuestoes > 0 ||
                            uiState.totalDisciplinas > 0 ||
                            uiState.totalBancas > 0 ||
                            uiState.totalInstituicoes > 0
                )

                if (!uiState.statsCarregadas) {
                    uiState = uiState.copy(erro = "Não foi possível conectar ao servidor")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    erro = when (e) {
                        is UnknownHostException -> "Sem conexão com a internet"
                        is SocketTimeoutException -> "Servidor não respondeu"
                        else -> "Falha ao carregar dados"
                    }
                )
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun atualizarDesempenho() {
        viewModelScope.launch {
            try {
                carregarDesempenho()
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun carregarDesempenho() {
        val seteDiasAtras = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val usuarioId = authRepository.usuarioIdOuGuest()

        val desempenho = carregarDesempenhoHomeUseCase(
            usuarioId = usuarioId,
            desde = seteDiasAtras
        )

        uiState = uiState.copy(
            resolvidas7dias = desempenho.resolvidas7dias,
            acertos7dias = desempenho.acertos7dias,
            erros7dias = desempenho.erros7dias,
            totalResolvidas = desempenho.totalResolvidas,
            totalAcertos = desempenho.totalAcertos,
            desempenhoPorDisciplina = desempenho.desempenhoPorDisciplina
        )
    }
}