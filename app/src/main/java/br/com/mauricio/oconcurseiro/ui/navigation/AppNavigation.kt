package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.runtime.*
import br.com.mauricio.oconcurseiro.ui.screens.filtro.FiltroScreen
import br.com.mauricio.oconcurseiro.ui.screens.questao.QuestaoScreen

sealed class Screen {
    object Questao : Screen()
    object Filtro : Screen()
}

@Composable
fun AppNavigation() {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Questao) }

    when (currentScreen) {
        Screen.Questao -> {
            QuestaoScreen(
                numeroAtual = 1,
                totalQuestoes = 1,
                onOpenFiltro = { currentScreen = Screen.Filtro }
            )
        }

        Screen.Filtro -> {
            FiltroScreen(
                onBack = { currentScreen = Screen.Questao }
            )
        }
    }
}