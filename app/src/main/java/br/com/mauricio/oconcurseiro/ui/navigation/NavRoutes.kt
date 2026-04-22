package br.com.mauricio.oconcurseiro.ui.navigation

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Home : NavRoutes("home")
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object Filtro : NavRoutes("filtro")
    object Questao : NavRoutes("questao")
    object Comentarios : NavRoutes("comentarios/{questaoId}") {
        fun createRoute(questaoId: String) = "comentarios/$questaoId"
    }
}