package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.ui.screens.filtro.FiltroScreen
import br.com.mauricio.oconcurseiro.ui.screens.home.HomeScreen
import br.com.mauricio.oconcurseiro.ui.screens.questao.QuestaoScreen
import br.com.mauricio.oconcurseiro.ui.viewmodel.HomeViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel

sealed class Screen {
    object Home : Screen()
    object Questao : Screen()
    object Filtro : Screen()
}

@Composable
fun AppNavigation() {

    val homeViewModel: HomeViewModel = viewModel()
    val questaoViewModel: QuestaoViewModel = viewModel()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    when (currentScreen) {
        Screen.Home -> {
            HomeScreen(
                viewModel = homeViewModel,
                onStartPractice = {
                    if (!questaoViewModel.jaCarregou) {
                        questaoViewModel.carregarQuestao()
                    }
                    currentScreen = Screen.Questao
                },
                onOpenFilters = {
                    currentScreen = Screen.Filtro
                }
            )
        }

        Screen.Questao -> {
            QuestaoScreen(
                viewModel = questaoViewModel,
                onOpenFiltro = { currentScreen = Screen.Filtro },
                onBack = { currentScreen = Screen.Home }
            )
        }

        Screen.Filtro -> {
            FiltroScreen(
                viewModel = questaoViewModel,
                onBack = {
                    currentScreen = if (questaoViewModel.jaCarregou) Screen.Questao else Screen.Home
                },
                onAplicarFiltro = { novoFiltro ->
                    questaoViewModel.aplicarFiltro(novoFiltro)
                    currentScreen = Screen.Questao
                }
            )
        }
    }
}
