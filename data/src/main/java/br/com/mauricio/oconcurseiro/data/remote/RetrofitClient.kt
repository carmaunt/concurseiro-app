package br.com.mauricio.oconcurseiro.data.remote

import br.com.mauricio.oconcurseiro.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import br.com.mauricio.oconcurseiro.data.auth.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import kotlinx.coroutines.runBlocking


private class TokenRefreshAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = TokenManager.refreshTokenAtual()
            ?: return null

        val refreshResponse = runCatching {
            runBlocking {
                Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ConcurseiroApi::class.java)
                    .refreshToken(RefreshTokenRequestDto(refreshToken))
            }
        }.getOrNull() ?: return null

        val salvou = TokenManager.salvarTokensSemContexto(
            accessToken = refreshResponse.accessToken,
            refreshToken = refreshResponse.refreshToken
        )

        if (!salvou) {
            return null
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${refreshResponse.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse

        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }

        return count
    }
}
object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()

            val token = TokenManager.accessToken

            val newRequest = if (!token.isNullOrBlank()) {
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                request
            }

            chain.proceed(newRequest)
        }
        .authenticator(TokenRefreshAuthenticator())
        .addInterceptor(logging)
        .build()

    val api: ConcurseiroApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConcurseiroApi::class.java)
    }
}
