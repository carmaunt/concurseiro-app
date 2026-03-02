package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        carregarQuestao(FiltroParams())
    }

    fun carregarQuestao(filtro: FiltroParams = FiltroParams()) {
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                val resp = repository.buscarPagina(
                    page = paginaAtual,
                    size = 1,
                    texto = filtro.texto,
                    disciplina = filtro.disciplina,
                    banca = filtro.banca,
                    ano = filtro.ano
                )

                totalQuestoes = resp.totalElements.toInt()

                val dto = resp.content.firstOrNull()
                val q = dto?.let { QuestaoMapper.fromDto(it) }
                questao = q

                if (q == null) {
                    erro = "Nenhuma questão encontrada para esse filtro."
                    totalQuestoes = 0
                } else {
                    numeroAtual = paginaAtual+1
                }

            } catch (e: Exception) {
                questao = null
                erro = e.message ?: "Falha ao carregar questão."
            } finally {
                isLoading = false
            }
        }
    }

    fun proxima(filtro: FiltroParams = FiltroParams()) {
        val ultimaPagina = if (totalQuestoes == 0) 0 else (totalQuestoes - 1) / 1
        if (paginaAtual < ultimaPagina) {
            paginaAtual++
            carregarQuestao(filtro)
        }
    }

    fun anterior(filtro: FiltroParams = FiltroParams()) {
        if (paginaAtual > 0) {
            paginaAtual--
            carregarQuestao(filtro)
        }
    }
}