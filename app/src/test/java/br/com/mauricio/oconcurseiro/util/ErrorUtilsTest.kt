package br.com.mauricio.oconcurseiro.util

import com.google.gson.JsonSyntaxException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

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
    fun `mapErrorMessage retorna erro de conexao quando ConnectException`() {
        val result = mapErrorMessage(ConnectException("connection refused"))
        assertEquals("Não foi possível conectar ao servidor", result)
    }

    @Test
    fun `mapErrorMessage retorna falha SSL quando SSLException`() {
        val result = mapErrorMessage(SSLException("ssl error"))
        assertEquals("Falha de segurança na conexão", result)
    }

    @Test
    fun `mapErrorMessage retorna resposta incompleta quando EOFException`() {
        val result = mapErrorMessage(EOFException("unexpected end"))
        assertEquals("Resposta incompleta do servidor", result)
    }

    @Test
    fun `mapErrorMessage retorna resposta invalida quando JsonSyntaxException`() {
        val result = mapErrorMessage(JsonSyntaxException("malformed json"))
        assertEquals("Resposta inválida do servidor", result)
    }

    @Test
    fun `mapErrorMessage retorna requisicao invalida para 400`() {
        val result = mapErrorMessage(httpException(400))
        assertEquals("Requisição inválida", result)
    }

    @Test
    fun `mapErrorMessage retorna sessao expirada para 401`() {
        val result = mapErrorMessage(httpException(401))
        assertEquals("Sessão expirada. Faça login novamente.", result)
    }

    @Test
    fun `mapErrorMessage retorna acesso negado para 403`() {
        val result = mapErrorMessage(httpException(403))
        assertEquals("Acesso negado", result)
    }

    @Test
    fun `mapErrorMessage retorna nao encontrado para 404`() {
        val result = mapErrorMessage(httpException(404))
        assertEquals("Recurso não encontrado", result)
    }

    @Test
    fun `mapErrorMessage retorna timeout HTTP para 408`() {
        val result = mapErrorMessage(httpException(408))
        assertEquals("Tempo de requisição esgotado", result)
    }

    @Test
    fun `mapErrorMessage retorna conflito para 409`() {
        val result = mapErrorMessage(httpException(409))
        assertEquals("Conflito ao processar a solicitação", result)
    }

    @Test
    fun `mapErrorMessage retorna dados invalidos para 422`() {
        val result = mapErrorMessage(httpException(422))
        assertEquals("Dados inválidos enviados ao servidor", result)
    }

    @Test
    fun `mapErrorMessage retorna muitas tentativas para 429`() {
        val result = mapErrorMessage(httpException(429))
        assertEquals("Muitas tentativas. Aguarde um pouco e tente novamente.", result)
    }

    @Test
    fun `mapErrorMessage retorna erro interno para 500`() {
        val result = mapErrorMessage(httpException(500))
        assertEquals("Erro interno do servidor", result)
    }

    @Test
    fun `mapErrorMessage retorna falha temporaria para 502`() {
        val result = mapErrorMessage(httpException(502))
        assertEquals("Falha temporária no servidor", result)
    }

    @Test
    fun `mapErrorMessage retorna servidor indisponivel para 503`() {
        val result = mapErrorMessage(httpException(503))
        assertEquals("Servidor indisponível no momento", result)
    }

    @Test
    fun `mapErrorMessage retorna gateway timeout para 504`() {
        val result = mapErrorMessage(httpException(504))
        assertEquals("Servidor demorou demais para responder", result)
    }

    @Test
    fun `mapErrorMessage retorna erro generico de solicitacao para 418`() {
        val result = mapErrorMessage(httpException(418))
        assertEquals("Erro na solicitação (418)", result)
    }

    @Test
    fun `mapErrorMessage retorna erro generico de servidor para 599`() {
        val result = mapErrorMessage(httpException(599))
        assertEquals("Erro no servidor (599)", result)
    }

    @Test
    fun `mapErrorMessage retorna mensagem da excecao comum`() {
        val result = mapErrorMessage(RuntimeException("algo inesperado"))
        assertEquals("algo inesperado", result)
    }

    @Test
    fun `mapErrorMessage retorna fallback quando mensagem nula`() {
        val result = mapErrorMessage(RuntimeException())
        assertEquals("Ocorreu um erro inesperado", result)
    }

    @Test
    fun `mapErrorMessage retorna fallback quando mensagem vazia`() {
        val result = mapErrorMessage(RuntimeException(""))
        assertEquals("Ocorreu um erro inesperado", result)
    }

    private fun httpException(code: Int): HttpException {
        val response = Response.error<Unit>(code, "".toResponseBody(null))
        return HttpException(response)
    }
}