package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.usecase.CarregarDesempenhoHomeUseCase
import br.com.mauricio.oconcurseiro.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: QuestaoRepository,
    private val authRepository: AuthRepository,
    private val carregarDesempenhoHomeUseCase: CarregarDesempenhoHomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarEstatisticas()
    }

    fun carregarEstatisticas() {
        _uiState.update {
            it.copy(
                isLoading = true,
                erro = null
            )
        }

        viewModelScope.launch {
            try {
                val questoesJob = launch {
                    try {
                        val resp = repository.buscarPagina(
                            page = 0,
                            size = 1,
                            filtro = FiltroParams()
                        )

                        _uiState.update {
                            it.copy(totalQuestoes = resp.totalElements)
                        }
                    } catch (_: Exception) {
                    }
                }

                val disciplinasJob = launch {
                    try {
                        _uiState.update {
                            it.copy(totalDisciplinas = repository.listarDisciplinas().size)
                        }
                    } catch (_: Exception) {
                    }
                }

                val bancasJob = launch {
                    try {
                        _uiState.update {
                            it.copy(totalBancas = repository.listarBancas().size)
                        }
                    } catch (_: Exception) {
                    }
                }

                val instituicoesJob = launch {
                    try {
                        _uiState.update {
                            it.copy(totalInstituicoes = repository.listarInstituicoes().size)
                        }
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

                val estadoAtual = _uiState.value
                val statsCarregadas = estadoAtual.totalQuestoes > 0 ||
                        estadoAtual.totalDisciplinas > 0 ||
                        estadoAtual.totalBancas > 0 ||
                        estadoAtual.totalInstituicoes > 0

                _uiState.update {
                    it.copy(statsCarregadas = statsCarregadas)
                }

                if (!statsCarregadas) {
                    _uiState.update {
                        it.copy(erro = "Não foi possível conectar ao servidor")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        erro = when (e) {
                            is UnknownHostException -> "Sem conexão com a internet"
                            is SocketTimeoutException -> "Servidor não respondeu"
                            else -> "Falha ao carregar dados"
                        }
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
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

        _uiState.update {
            it.copy(
                resolvidas7dias = desempenho.resolvidas7dias,
                acertos7dias = desempenho.acertos7dias,
                erros7dias = desempenho.erros7dias,
                totalResolvidas = desempenho.totalResolvidas,
                totalAcertos = desempenho.totalAcertos,
                desempenhoPorDisciplina = desempenho.desempenhoPorDisciplina,
                missaoSemanal = desempenho.missaoSemanal
            )
        }
    }
}
