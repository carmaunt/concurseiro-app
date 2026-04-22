package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val questaoViewModel: QuestaoViewModel = hiltViewModel()
    val comentariosViewModel: ComentariosViewModel = hiltViewModel()
    val navController = rememberNavController()

    val context = LocalContext.current
    val guestManager = remember {
        br.com.mauricio.oconcurseiro.data.local.GuestUsageManager(context)
    }
    var mostrarLimiteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authViewModel.usuarioAutenticado) {
        homeViewModel.atualizarDesempenho()
    }

    if (mostrarLimiteDialog) {
        br.com.mauricio.oconcurseiro.ui.screens.auth.GuestLimitLoginDialog(
            viewModel = authViewModel,
            onDismiss = { mostrarLimiteDialog = false },
            onLoginSuccess = {
                mostrarLimiteDialog = false
                navController.navigate(NavRoutes.Home.route) {
                    popUpTo(0)
                }
            },
            onAbrirCadastro = {
                mostrarLimiteDialog = false
                navController.navigate(NavRoutes.Register.route)
            },
            onLoginGoogleClick = {
                CoroutineScope(Main).launch {
                    val token: String? = obterIdTokenGoogle(context)
                    if (token != null) {
                        authViewModel.loginComGoogle(token) {
                            mostrarLimiteDialog = false
                            navController.navigate(NavRoutes.Home.route) {
                                popUpTo(0)
                            }
                        }
                    }
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onAbrirCadastro = { navController.navigate(NavRoutes.Register.route) },
                onLoginGoogleClick = {
                    CoroutineScope(Main).launch {
                        val token: String? = obterIdTokenGoogle(context)
                        if (token != null) {
                            authViewModel.loginComGoogle(token) {
                                navController.navigate(NavRoutes.Home.route) {
                                    popUpTo(NavRoutes.Login.route) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            )
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onCadastroSuccess = { navController.popBackStack() },
                onVoltarLogin = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onStartPractice = {
                    if (!authViewModel.usuarioAutenticado && !guestManager.podeResolverSemLogin()) {
                        mostrarLimiteDialog = true
                    } else {
                        if (!questaoViewModel.jaCarregou) {
                            questaoViewModel.carregarQuestao()
                        }
                        navController.navigate(NavRoutes.Questao.route)
                    }
                },
                onOpenFilters = {
                    navController.navigate(NavRoutes.Filtro.route)
                },
                onLogout = {
                    authViewModel.logout()
                    homeViewModel.atualizarDesempenho()
                },
                onLoginClick = {
                    navController.navigate(NavRoutes.Login.route)
                },
                usuarioAutenticado = authViewModel.usuarioAutenticado
            )
        }

        composable(NavRoutes.Questao.route) {
            QuestaoScreen(
                viewModel = questaoViewModel,
                onOpenFiltro = { navController.navigate(NavRoutes.Filtro.route) },
                onBack = {
                    homeViewModel.atualizarDesempenho()
                    navController.popBackStack()
                },
                onAbrirComentarios = { questaoId ->
                    navController.navigate(NavRoutes.Comentarios.createRoute(questaoId))
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

        composable(NavRoutes.Filtro.route) {
            FiltroScreen(
                viewModel = questaoViewModel,
                onBack = { navController.popBackStack() },
                onAplicarFiltro = { novoFiltro ->
                    if (!authViewModel.usuarioAutenticado && !guestManager.podeResolverSemLogin()) {
                        mostrarLimiteDialog = true
                    } else {
                        questaoViewModel.aplicarFiltro(novoFiltro)
                        navController.navigate(NavRoutes.Questao.route) {
                            popUpTo(NavRoutes.Filtro.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = NavRoutes.Comentarios.route,
            arguments = listOf(navArgument("questaoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val questaoId = backStackEntry.arguments?.getString("questaoId") ?: ""
            ComentariosScreen(
                viewModel = comentariosViewModel,
                questaoId = questaoId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
