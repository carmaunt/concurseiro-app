package br.com.mauricio.oconcurseiro.data.remote

data class CadastroUsuarioFinalRequestDto(
    val nome: String,
    val email: String,
    val senha: String
)

data class LoginEmailRequestDto(
    val email: String,
    val senha: String
)

data class GoogleLoginRequestDto(
    val idToken: String
)

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val role: String
)