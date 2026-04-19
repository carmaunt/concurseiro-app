package br.com.mauricio.oconcurseiro.data.auth

import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.data.remote.RetrofitClient

object ConcurseiroApiProvider {
    val api: ConcurseiroApi
        get() = RetrofitClient.api
}

object TokenManager {
    @Volatile
    var accessToken: String? = null
        private set

    @Volatile
    var refreshToken: String? = null
        private set

    fun salvarTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun limpar() {
        accessToken = null
        refreshToken = null
    }
}