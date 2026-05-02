package br.com.mauricio.oconcurseiro.data.remote

data class GoogleLoginRequestDto(
    val idToken: String
)

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val role: String
)