package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.local.AppDatabase
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val repository: QuestaoRepository,
    private val authRepository: AuthRepository,
    private val respostaDao: RespostaDao
) : ViewModel() {


    var totalQuestoes: Long by mutableStateOf(0L)
        private set

    var totalDisciplinas: Int by mutableStateOf(0)
        private set

    var totalBancas: Int by mutableStateOf(0)
        private set

    var totalInstituicoes: Int by mutableStateOf(0)
        private set

    var isLoading: Boolean by mutableStateOf(true)
        private set

    var erro: String? by mutableStateOf(null)
        private set

    var statsCarregadas: Boolean by mutableStateOf(false)
        private set

    var resolvidas7dias: Int by mutableIntStateOf(0)
        private set

    var acertos7dias: Int by mutableIntStateOf(0)
        private set

    var erros7dias: Int by mutableIntStateOf(0)
        private set

    var totalResolvidas: Int by mutableIntStateOf(0)
        private set

    var totalAcertos: Int by mutableIntStateOf(0)
        private set

    val porcentagem7dias: Int
        get() = if (resolvidas7dias > 0) (acertos7dias * 100) / resolvidas7dias else 0

    init {
        carregarEstatisticas()
    }

    fun carregarEstatisticas() {
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                val questoesJob = launch {
                    try {
                        val resp = repository.buscarPagina(page = 0, size = 1, filtro = FiltroParams())
                        totalQuestoes = resp.resolvedTotalElements
                    } catch (_: Exception) { }
                }

                val disciplinasJob = launch {
                    try {
                        totalDisciplinas = repository.listarDisciplinas().size
                    } catch (_: Exception) { }
                }

                val bancasJob = launch {
                    try {
                        totalBancas = repository.listarBancas().size
                    } catch (_: Exception) { }
                }

                val instituicoesJob = launch {
                    try {
                        totalInstituicoes = repository.listarInstituicoes().size
                    } catch (_: Exception) { }
                }

                val historicoJob = launch {
                    try {
                        carregarDesempenho()
                    } catch (_: Exception) { }
                }

                questoesJob.join()
                disciplinasJob.join()
                bancasJob.join()
                instituicoesJob.join()
                historicoJob.join()

                statsCarregadas = totalQuestoes > 0 || totalDisciplinas > 0 || totalBancas > 0 || totalInstituicoes > 0

                if (!statsCarregadas) {
                    erro = "Não foi possível conectar ao servidor"
                }
            } catch (e: Exception) {
                erro = when (e) {
                    is UnknownHostException -> "Sem conexão com a internet"
                    is SocketTimeoutException -> "Servidor não respondeu"
                    else -> "Falha ao carregar dados"
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun atualizarDesempenho() {
        viewModelScope.launch {
            try {
                carregarDesempenho()
            } catch (_: Exception) { }
        }
    }

    private suspend fun carregarDesempenho() {
        val seteDiasAtras = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val usuarioId = authRepository.usuarioIdOuGuest()

        resolvidas7dias = respostaDao.totalRespostasDesde(usuarioId, seteDiasAtras)
        acertos7dias = respostaDao.totalAcertosDesde(usuarioId, seteDiasAtras)
        erros7dias = respostaDao.totalErrosDesde(usuarioId, seteDiasAtras)

        totalResolvidas = respostaDao.totalRespostas(usuarioId)
        totalAcertos = respostaDao.totalAcertos(usuarioId)
    }
}
