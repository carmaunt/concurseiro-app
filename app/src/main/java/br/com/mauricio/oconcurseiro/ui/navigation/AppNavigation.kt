package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.mauricio.oconcurseiro.data.auth.obterIdTokenGoogle
import br.com.mauricio.oconcurseiro.ui.screens.auth.LoginScreen
import br.com.mauricio.oconcurseiro.ui.screens.auth.RegisterScreen
import br.com.mauricio.oconcurseiro.ui.screens.comentarios.ComentariosScreen
import br.com.mauricio.oconcurseiro.ui.screens.filtro.FiltroScreen
import br.com.mauricio.oconcurseiro.ui.screens.home.HomeScreen
import br.com.mauricio.oconcurseiro.ui.screens.questao.QuestaoScreen
import br.com.mauricio.oconcurseiro.ui.screens.splash.SplashScreen
import br.com.mauricio.oconcurseiro.ui.viewmodel.AuthViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.ComentariosViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.HomeViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    object Questao : Screen()
    object Filtro : Screen()
    object Login : Screen()
    object Register : Screen()
    data class Comentarios(val questaoId: String) : Screen()
}

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val questaoViewModel: QuestaoViewModel = viewModel()
    val comentariosViewModel: ComentariosViewModel = viewModel()

    val context = LocalContext.current
    val guestManager = remember {
        br.com.mauricio.oconcurseiro.data.local.GuestUsageManager(context)
    }
    var mostrarLimiteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authViewModel.usuarioAutenticado) {
        homeViewModel.atualizarDesempenho()
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    if (mostrarLimiteDialog) {
        br.com.mauricio.oconcurseiro.ui.screens.auth.GuestLimitLoginDialog(
            viewModel = authViewModel,
            onDismiss = { mostrarLimiteDialog = false },
            onLoginSuccess = {
                mostrarLimiteDialog = false
                currentScreen = Screen.Home
            },
            onAbrirCadastro = {
                mostrarLimiteDialog = false
                currentScreen = Screen.Register
            },
            onLoginGoogleClick = {
                CoroutineScope(Main).launch {
                    val token: String? = obterIdTokenGoogle(context)
                    if (token != null) {
                        authViewModel.loginComGoogle(token) {
                            mostrarLimiteDialog = false
                            currentScreen = Screen.Home
                        }
                    }
                }
            }
        )
    }


    when (val screen = currentScreen) {
        Screen.Splash -> {
            SplashScreen(
                onFinished = {
                    currentScreen = Screen.Home
                }
            )
        }

        Screen.Login -> {
            val context = LocalContext.current
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { currentScreen = Screen.Home },
                onAbrirCadastro = { currentScreen = Screen.Register },
                onLoginGoogleClick = {
                    CoroutineScope(Main).launch {
                        val token: String? = obterIdTokenGoogle(context)
                        if (token != null) {
                            authViewModel.loginComGoogle(token) {
                                currentScreen = Screen.Home
                            }
                        }
                    }
                }
            )
        }

        Screen.Register -> {
            RegisterScreen(
                viewModel = authViewModel,
                onCadastroSuccess = { currentScreen = Screen.Login },
                onVoltarLogin = { currentScreen = Screen.Login }
            )
        }

        Screen.Home -> {

            HomeScreen(
                viewModel = homeViewModel,
                onStartPractice = {
                    if (!authViewModel.usuarioAutenticado && !guestManager.podeResolverSemLogin()) {
                        mostrarLimiteDialog = true
                        return@HomeScreen
                    }

                    if (!questaoViewModel.jaCarregou) {
                        questaoViewModel.carregarQuestao()
                    }

                    currentScreen = Screen.Questao
                },
                onOpenFilters = {
                    currentScreen = Screen.Filtro
                },
                onLogout = {
                    authViewModel.logout()
                    currentScreen = Screen.Home
                },
                onLoginClick = {
                    currentScreen = Screen.Login
                },
                usuarioAutenticado = authViewModel.usuarioAutenticado
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
                },
                onPodeResolverQuestao = { questaoId ->
                    if (authViewModel.usuarioAutenticado) {
                        true
                    } else {
                        val podeResolver = guestManager.podeResolverQuestao(questaoId)
                        if (!podeResolver) {
                            mostrarLimiteDialog = true
                        }
                        podeResolver
                    }
                },
                onResolvidaComSucesso = { questaoId ->
                    if (!authViewModel.usuarioAutenticado) {
                        guestManager.registrarResolucao(questaoId)
                        homeViewModel.atualizarDesempenho()
                    }
                },
                onSolicitarProximaQuestao = {
                    if (!authViewModel.usuarioAutenticado && !guestManager.podeResolverSemLogin()) {
                        mostrarLimiteDialog = true
                    } else {
                        questaoViewModel.proxima()
                    }
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
                    if (!authViewModel.usuarioAutenticado && !guestManager.podeResolverSemLogin()) {
                        mostrarLimiteDialog = true
                        return@FiltroScreen
                    }

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