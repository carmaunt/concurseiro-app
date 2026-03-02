package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class HomeViewModel : ViewModel() {

    private val repository = QuestaoRepository()

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
                        totalQuestoes = resp.totalElements
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

                questoesJob.join()
                disciplinasJob.join()
                bancasJob.join()
                instituicoesJob.join()

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
}
