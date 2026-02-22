package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.com.mauricio.oconcurseiro.data.model.Questao
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository

class QuestaoViewModel : ViewModel() {

    private val repository = QuestaoRepository()

    val questao: Questao = repository.obterQuestao()
    val numeroAtual: Int = 85
    val totalQuestoes: Int = 313069
}