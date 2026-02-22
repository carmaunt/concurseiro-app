package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.mauricio.oconcurseiro.ui.screens.filtro.FiltroScreen
import br.com.mauricio.oconcurseiro.ui.screens.questao.QuestaoScreen
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel

sealed class Screen {
    object Questao : Screen()
    object Filtro : Screen()
}

@Composable
fun AppNavigation() {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Questao) }

    when (currentScreen) {
        Screen.Questao -> {
            val viewModel: QuestaoViewModel = viewModel()
            val questao = viewModel.questao

            QuestaoScreen(
                questao = questao,
                numeroAtual = viewModel.numeroAtual,
                totalQuestoes = viewModel.totalQuestoes,
                onOpenFiltro = { currentScreen = Screen.Filtro }
            )
        }

        Screen.Filtro -> FiltroScreen(
            onBack = { currentScreen = Screen.Questao }
        )
    }
}