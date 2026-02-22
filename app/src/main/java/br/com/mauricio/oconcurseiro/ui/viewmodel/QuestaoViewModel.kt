package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import kotlinx.coroutines.launch

class QuestaoViewModel : ViewModel() {

    private val repository = QuestaoRepository()

    var questao: Questao? = null
        private set

    var isLoading: Boolean = false
        private set

    var erro: String? = null
        private set

    var numeroAtual: Int = 1
        private set

    var totalQuestoes: Int = 0
        private set

    init {
        carregarQuestao()
    }

    fun carregarQuestao() {
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                val lista = repository.listarQuestoes(size = 1)
                questao = lista.firstOrNull()
                numeroAtual = 1
                totalQuestoes = 1
            } catch (e: Exception) {
                erro = e.message
            } finally {
                isLoading = false
            }
        }
    }
}