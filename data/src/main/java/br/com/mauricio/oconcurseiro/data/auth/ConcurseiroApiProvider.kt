package br.com.mauricio.oconcurseiro.data.auth

import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.data.remote.RetrofitClient

object ConcurseiroApiProvider {
    val api: ConcurseiroApi
        get() = RetrofitClient.api
}
