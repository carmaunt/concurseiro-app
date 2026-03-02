package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.runtime.*
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.ui.screens.filtro.FiltroScreen
import br.com.mauricio.oconcurseiro.ui.screens.questao.QuestaoScreen

sealed class Screen {
    object Questao : Screen()
    object Filtro : Screen()
}

@Composable
fun AppNavigation() {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Questao) }

    var filtroAtual by remember { mutableStateOf(FiltroParams()) }

    when (currentScreen) {
        Screen.Questao -> {
            QuestaoScreen(
                filtro = filtroAtual,
                numeroAtual = 1,
                totalQuestoes = 1,
                onOpenFiltro = { currentScreen = Screen.Filtro }
            )
        }

        Screen.Filtro -> {
            FiltroScreen(
                filtroAtual = filtroAtual,
                onBack = { currentScreen = Screen.Questao },
                onAplicarFiltro = { novoFiltro ->
                    filtroAtual = novoFiltro
                    currentScreen = Screen.Questao
                }
            )
        }
    }
}