package br.com.mauricio.oconcurseiro.data.remote

data class GoogleLoginRequestDto(
    val idToken: String
)

data class RefreshTokenRequestDto(
    val refreshToken: String
)

data class RefreshTokenResponseDto(
    val accessToken: String,
    val refreshToken: String
)

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val role: String
)