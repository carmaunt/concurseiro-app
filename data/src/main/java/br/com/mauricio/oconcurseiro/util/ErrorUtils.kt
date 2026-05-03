package br.com.mauricio.oconcurseiro.util

import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun mapErrorMessage(e: Exception): String {
    return when (e) {
        is UnknownHostException -> "Sem conexão com a internet"
        is SocketTimeoutException -> "Servidor não respondeu. Tente novamente."
        is HttpException -> {
            when (e.code()) {
                400 -> "Requisição inválida"
                401 -> "Sessão expirada. Faça login novamente."
                403 -> "Acesso negado"
                404 -> "Recurso não encontrado"
                500 -> "Erro interno do servidor"
                503 -> "Servidor indisponível no momento"
                else -> "Erro do servidor (${e.code()})"
            }
        }
        else -> e.message ?: "Ocorreu um erro inesperado"
    }
}
