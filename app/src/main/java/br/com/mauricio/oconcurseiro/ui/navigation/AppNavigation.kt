package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.ui.screens.filtro.FiltroScreen
import br.com.mauricio.oconcurseiro.ui.screens.questao.QuestaoScreen
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel

sealed class Screen {
    object Questao : Screen()
    object Filtro : Screen()
}

@Composable
fun AppNavigation() {

    val viewModel: QuestaoViewModel = viewModel()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Questao) }

    when (currentScreen) {
        Screen.Questao -> {
            QuestaoScreen(
                viewModel = viewModel,
                onOpenFiltro = { currentScreen = Screen.Filtro }
            )
        }

        Screen.Filtro -> {
            FiltroScreen(
                viewModel = viewModel,
                onBack = { currentScreen = Screen.Questao },
                onAplicarFiltro = { novoFiltro ->
                    viewModel.aplicarFiltro(novoFiltro)
                    currentScreen = Screen.Questao
                }
            )
        }
    }
}
