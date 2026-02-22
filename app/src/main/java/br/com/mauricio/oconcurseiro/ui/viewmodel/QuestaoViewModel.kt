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

    init {
        carregarQuestao(FiltroParams())
    }

    fun carregarQuestao(filtro: FiltroParams = FiltroParams()) {
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                val lista = repository.listarQuestoes(
                    size = 1,
                    texto = filtro.texto,
                    disciplina = filtro.disciplina,
                    banca = filtro.banca,
                    ano = filtro.ano
                )

                val q = lista.firstOrNull()
                questao = q

                if (q == null) {
                    erro = "Nenhuma questão encontrada para esse filtro."
                    totalQuestoes = 0
                } else {
                    numeroAtual = 1
                    totalQuestoes = 1
                }

            } catch (e: Exception) {
                questao = null
                erro = e.message ?: "Falha ao carregar questão."
            } finally {
                isLoading = false
            }
        }
    }
}