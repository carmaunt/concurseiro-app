package br.com.mauricio.oconcurseiro.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.ui.screens.auth.GuestLimitLoginDialog
import br.com.mauricio.oconcurseiro.ui.screens.auth.LoginScreen
import br.com.mauricio.oconcurseiro.ui.screens.auth.RegisterScreen
import br.com.mauricio.oconcurseiro.ui.screens.aviso.AvisoLegalScreen
import br.com.mauricio.oconcurseiro.ui.screens.comentarios.ComentariosScreen
import br.com.mauricio.oconcurseiro.ui.screens.filtro.FiltroScreen
import br.com.mauricio.oconcurseiro.ui.screens.home.HomeScreen
import br.com.mauricio.oconcurseiro.ui.screens.privacidade.PrivacidadeDadosScreen
import br.com.mauricio.oconcurseiro.ui.screens.questao.QuestaoScreen
import br.com.mauricio.oconcurseiro.ui.screens.splash.SplashScreen
import br.com.mauricio.oconcurseiro.ui.viewmodel.AuthViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.ComentariosViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.HomeViewModel
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authViewModel.usuarioAutenticado) {
        homeViewModel.atualizarDesempenho()
    }

    LaunchedEffect(authViewModel.mensagemSucesso) {
        authViewModel.mensagemSucesso?.let { snackbarHostState.showSnackbar(it) }
    }

    if (authViewModel.mostrarLimiteDialog) {
        GuestLimitLoginDialog(
            titulo = if (authViewModel.loginDialogOrigemComentarios) {
                "Entre para participar"
            } else {
                "Limite diário atingido"
            },
            mensagem = if (authViewModel.loginDialogOrigemComentarios) {
                "Faça login para comentar, curtir ou interagir com outros estudantes."
            } else {
                "Você já resolveu 5 questões hoje. Faça login para continuar resolvendo sem limite."
            },
            viewModel = authViewModel,
            onDismiss = { authViewModel.fecharDialog() },
            onLoginSuccess = {
                val origemComentarios = authViewModel.loginDialogOrigemComentarios
                authViewModel.fecharDialog()
                if (!origemComentarios) {
                    navController.navigate(NavRoutes.Home.route) { popUpTo(0) }
                }
            },
            onAbrirCadastro = {
                authViewModel.fecharDialog()
                navController.navigate(NavRoutes.Register.route)
            },
            onLoginGoogleClick = {
                authViewModel.loginComGoogleComContexto(context) {
                    val origemComentarios = authViewModel.loginDialogOrigemComentarios
                    authViewModel.fecharDialog()
                    if (!origemComentarios) {
                        navController.navigate(NavRoutes.Home.route) { popUpTo(0) }
                    }
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        authViewModel.loginComGoogleComContexto(context) {
                            navController.navigate(NavRoutes.Home.route) {
                                popUpTo(NavRoutes.Login.route) { inclusive = true }
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
                        if (!authViewModel.usuarioAutenticado && !authViewModel.podeResolverSemLogin()) {
                            authViewModel.abrirDialogLimite()
                        } else {
                            if (!questaoViewModel.uiState.jaCarregou) {
                                questaoViewModel.carregarQuestao()
                            }
                            navController.navigate(NavRoutes.Questao.route)
                        }
                    },
                    onOpenFilters = { navController.navigate(NavRoutes.Filtro.route) },
                    onLogout = {
                        authViewModel.logout()
                        homeViewModel.atualizarDesempenho()
                    },
                    onLoginClick = { navController.navigate(NavRoutes.Login.route) },
                    onAvisoLegal = { navController.navigate(NavRoutes.AvisoLegal.route) },
                    onPrivacidadeDados = { navController.navigate(NavRoutes.Privacidade.route) },
                    usuarioAutenticado = authViewModel.usuarioAutenticado
                )
            }

            composable(NavRoutes.AvisoLegal.route) {
                AvisoLegalScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.Privacidade.route) {
                PrivacidadeDadosScreen(
                    usuarioAutenticado = authViewModel.usuarioAutenticado,
                    isLoading = authViewModel.isLoading,
                    erro = authViewModel.erro,
                    onBack = { navController.popBackStack() },
                    onAvisoLegal = { navController.navigate(NavRoutes.AvisoLegal.route) },
                    onExcluirConta = { onSucesso ->
                        authViewModel.excluirConta {
                            navController.navigate(NavRoutes.Home.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = true }
                            }
                            onSucesso()
                        }
                    }
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
                        authViewModel.usuarioAutenticado || authViewModel.podeResolverQuestao(questaoId)
                    },
                    onResolvidaComSucesso = { questaoId ->
                        if (!authViewModel.usuarioAutenticado) {
                            authViewModel.registrarResolucao(questaoId)
                            homeViewModel.atualizarDesempenho()
                        }
                    },
                    onSolicitarProximaQuestao = {
                        if (!authViewModel.usuarioAutenticado && !authViewModel.podeResolverSemLogin()) {
                            authViewModel.abrirDialogLimite()
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
                        if (!authViewModel.usuarioAutenticado && !authViewModel.podeResolverSemLogin()) {
                            authViewModel.abrirDialogLimite()
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
                    usuarioAutenticado = authViewModel.usuarioAutenticado,
                    nomeUsuario = authViewModel.nomeUsuario,
                    onLoginRequired = { authViewModel.abrirDialogLimite(origemComentarios = true) },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}
