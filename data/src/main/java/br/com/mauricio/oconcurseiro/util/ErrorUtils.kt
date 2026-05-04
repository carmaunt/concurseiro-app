package br.com.mauricio.oconcurseiro.util

import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

fun mapErrorMessage(e: Exception): String {
    return when (e) {
        is UnknownHostException -> "Sem conexão com a internet"
        is SocketTimeoutException -> "Servidor não respondeu. Tente novamente."
        is ConnectException -> "Não foi possível conectar ao servidor"
        is SSLException -> "Falha de segurança na conexão"
        is EOFException -> "Resposta incompleta do servidor"
        is JsonSyntaxException -> "Resposta inválida do servidor"
        is HttpException -> mapHttpErrorMessage(e)
        else -> e.message?.takeIf { it.isNotBlank() } ?: "Ocorreu um erro inesperado"
    }
}

private fun mapHttpErrorMessage(e: HttpException): String {
    return when (e.code()) {
        400 -> "Requisição inválida"
        401 -> "Sessão expirada. Faça login novamente."
        403 -> "Acesso negado"
        404 -> "Recurso não encontrado"
        408 -> "Tempo de requisição esgotado"
        409 -> "Conflito ao processar a solicitação"
        422 -> "Dados inválidos enviados ao servidor"
        429 -> "Muitas tentativas. Aguarde um pouco e tente novamente."
        500 -> "Erro interno do servidor"
        502 -> "Falha temporária no servidor"
        503 -> "Servidor indisponível no momento"
        504 -> "Servidor demorou demais para responder"
        in 400..499 -> "Erro na solicitação (${e.code()})"
        in 500..599 -> "Erro no servidor (${e.code()})"
        else -> "Erro inesperado (${e.code()})"
    }
}