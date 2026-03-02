package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.ui.screens.comentarios.ComentariosScreen
import br.com.mauricio.oconcurseiro.ui.screens.filtro.FiltroScreen
import br.com.mauricio.oconcurseiro.ui.screens.home.HomeScreen
import br.com.mauricio.oconcurseiro.ui.screens.questao.QuestaoScreen
import br.com.mauricio.oconcurseiro.ui.viewmodel.ComentariosViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.HomeViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel

sealed class Screen {
    object Home : Screen()
    object Questao : Screen()
    object Filtro : Screen()
    data class Comentarios(val questaoId: String) : Screen()
}

@Composable
fun AppNavigation() {

    val homeViewModel: HomeViewModel = viewModel()
    val questaoViewModel: QuestaoViewModel = viewModel()
    val comentariosViewModel: ComentariosViewModel = viewModel()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    when (val screen = currentScreen) {
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
                onBack = {
                    homeViewModel.atualizarDesempenho()
                    currentScreen = Screen.Home
                },
                onAbrirComentarios = { questaoId ->
                    currentScreen = Screen.Comentarios(questaoId)
                }
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

        is Screen.Comentarios -> {
            ComentariosScreen(
                viewModel = comentariosViewModel,
                questaoId = screen.questaoId,
                onBack = { currentScreen = Screen.Questao }
            )
        }
    }
}
