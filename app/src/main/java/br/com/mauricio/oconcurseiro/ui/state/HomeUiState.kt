package br.com.mauricio.oconcurseiro.ui.state

data class HomeUiState(
    val isLoading: Boolean = false,
    val erro: String? = null,
    val totalQuestoes: Long = 0L,
    val totalDisciplinas: Int = 0,
    val totalBancas: Int = 0,
    val totalInstituicoes: Int = 0,
    val statsCarregadas: Boolean = false,
    val resolvidas7dias: Int = 0,
    val acertos7dias: Int = 0,
    val erros7dias: Int = 0,
    val totalResolvidas: Int = 0,
    val totalAcertos: Int = 0
)