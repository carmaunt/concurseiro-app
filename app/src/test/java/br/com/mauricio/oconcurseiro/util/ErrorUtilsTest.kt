package br.com.mauricio.oconcurseiro.util

import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorUtilsTest {

    @Test
    fun `mapErrorMessage retorna sem conexao quando UnknownHostException`() {
        val result = mapErrorMessage(UnknownHostException("host not found"))
        assertEquals("Sem conexão com a internet", result)
    }

    @Test
    fun `mapErrorMessage retorna timeout quando SocketTimeoutException`() {
        val result = mapErrorMessage(SocketTimeoutException("timeout"))
        assertEquals("Servidor não respondeu. Tente novamente.", result)
    }

    @Test
    fun `mapErrorMessage retorna sessao expirada para 401`() {
        val response = Response.error<Unit>(401, okhttp3.ResponseBody.create(null, ""))
        val result = mapErrorMessage(HttpException(response))
        assertEquals("Sessão expirada. Faça login novamente.", result)
    }

    @Test
    fun `mapErrorMessage retorna acesso negado para 403`() {
        val response = Response.error<Unit>(403, okhttp3.ResponseBody.create(null, ""))
        val result = mapErrorMessage(HttpException(response))
        assertEquals("Acesso negado", result)
    }

    @Test
    fun `mapErrorMessage retorna nao encontrado para 404`() {
        val response = Response.error<Unit>(404, okhttp3.ResponseBody.create(null, ""))
        val result = mapErrorMessage(HttpException(response))
        assertEquals("Recurso não encontrado", result)
    }

    @Test
    fun `mapErrorMessage retorna erro interno para 500`() {
        val response = Response.error<Unit>(500, okhttp3.ResponseBody.create(null, ""))
        val result = mapErrorMessage(HttpException(response))
        assertEquals("Erro interno do servidor", result)
    }

    @Test
    fun `mapErrorMessage retorna servidor indisponivel para 503`() {
        val response = Response.error<Unit>(503, okhttp3.ResponseBody.create(null, ""))
        val result = mapErrorMessage(HttpException(response))
        assertEquals("Servidor indisponível no momento", result)
    }

    @Test
    fun `mapErrorMessage retorna codigo para http desconhecido`() {
        val response = Response.error<Unit>(418, okhttp3.ResponseBody.create(null, ""))
        val result = mapErrorMessage(HttpException(response))
        assertEquals("Erro do servidor (418)", result)
    }

    @Test
    fun `mapErrorMessage retorna mensagem generica para RuntimeException`() {
        val result = mapErrorMessage(RuntimeException("algo inesperado"))
        assertEquals("algo inesperado", result)
    }

    @Test
    fun `mapErrorMessage retorna fallback quando mensagem nula`() {
        val result = mapErrorMessage(RuntimeException())
        assertEquals("Ocorreu um erro inesperado", result)
    }
}
