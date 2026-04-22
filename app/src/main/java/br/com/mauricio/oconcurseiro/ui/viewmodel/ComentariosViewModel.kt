package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.model.Comentario
import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ComentariosViewModel @Inject constructor(
    private val repository: QuestaoRepository
) : ViewModel() {

    var questaoId: String by mutableStateOf("")
        private set

    var comentarios: List<Comentario> by mutableStateOf(emptyList())
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set

    var erro: String? by mutableStateOf(null)
        private set

    var ordenacao: String by mutableStateOf("curtidas")
        private set

    var paginaAtual: Int by mutableStateOf(0)
        private set

    var temMaisPaginas: Boolean by mutableStateOf(false)
        private set

    var isEnviando: Boolean by mutableStateOf(false)
        private set

    var erroEnvio: String? by mutableStateOf(null)
        private set

    private val curtidasLocais = mutableSetOf<Long>()
    private val descurtidasLocais = mutableSetOf<Long>()

    fun carregarComentarios(questaoId: String, resetar: Boolean = true) {
        this.questaoId = questaoId
        if (resetar) {
            paginaAtual = 0
            comentarios = emptyList()
        }
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                val resp = repository.listarComentarios(
                    questaoId = questaoId,
                    page = paginaAtual,
                    size = 20,
                    ordenar = ordenacao
                )
                val novos = resp.content.map { dto ->
                    Comentario(
                        id = dto.id,
                        questaoId = dto.questaoId,
                        autor = dto.autor,
                        texto = dto.texto,
                        curtidas = dto.curtidas,
                        descurtidas = dto.descurtidas,
                        criadoEm = dto.criadoEm
                    )
                }
                comentarios = if (resetar) novos else comentarios + novos
                temMaisPaginas = !resp.resolvedLast
            } catch (e: Exception) {
                erro = mapErrorMessage(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun carregarMais() {
        if (!temMaisPaginas || isLoading) return
        paginaAtual++
        carregarComentarios(questaoId, resetar = false)
    }

    fun alterarOrdenacao(novaOrdenacao: String) {
        if (novaOrdenacao == ordenacao) return
        ordenacao = novaOrdenacao
        carregarComentarios(questaoId)
    }

    fun enviarComentario(autor: String, texto: String, onSucesso: () -> Unit = {}) {
        if (texto.isBlank() || autor.isBlank() || questaoId.isBlank()) return
        isEnviando = true
        erroEnvio = null

        viewModelScope.launch {
            try {
                val dto = repository.criarComentario(questaoId, autor, texto)
                val novo = Comentario(
                    id = dto.id,
                    questaoId = dto.questaoId,
                    autor = dto.autor,
                    texto = dto.texto,
                    curtidas = dto.curtidas,
                    descurtidas = dto.descurtidas,
                    criadoEm = dto.criadoEm
                )
                comentarios = listOf(novo) + comentarios
                onSucesso()
            } catch (e: Exception) {
                erroEnvio = mapErrorMessage(e)
            } finally {
                isEnviando = false
            }
        }
    }

    fun curtir(comentarioId: Long) {
        if (comentarioId in curtidasLocais) return
        curtidasLocais.add(comentarioId)

        comentarios = comentarios.map {
            if (it.id == comentarioId) it.copy(curtidas = it.curtidas + 1) else it
        }

        viewModelScope.launch {
            try {
                repository.curtirComentario(comentarioId)
            } catch (_: Exception) {
                comentarios = comentarios.map {
                    if (it.id == comentarioId) it.copy(curtidas = it.curtidas - 1) else it
                }
                curtidasLocais.remove(comentarioId)
            }
        }
    }

    fun descurtir(comentarioId: Long) {
        if (comentarioId in descurtidasLocais) return
        descurtidasLocais.add(comentarioId)

        comentarios = comentarios.map {
            if (it.id == comentarioId) it.copy(descurtidas = it.descurtidas + 1) else it
        }

        viewModelScope.launch {
            try {
                repository.descurtirComentario(comentarioId)
            } catch (_: Exception) {
                comentarios = comentarios.map {
                    if (it.id == comentarioId) it.copy(descurtidas = it.descurtidas - 1) else it
                }
                descurtidasLocais.remove(comentarioId)
            }
        }
    }

    fun jaCurtiu(comentarioId: Long): Boolean = comentarioId in curtidasLocais
    fun jaDescurtiu(comentarioId: Long): Boolean = comentarioId in descurtidasLocais

    private fun mapErrorMessage(e: Exception): String {
        return when (e) {
            is UnknownHostException -> "Sem conexão com a internet"
            is SocketTimeoutException -> "Servidor não respondeu. Tente novamente."
            is HttpException -> {
                when (e.code()) {
                    400 -> "Requisição inválida"
                    404 -> "Recurso não encontrado"
                    500 -> "Erro interno do servidor"
                    else -> "Erro do servidor (${e.code()})"
                }
            }
            else -> e.message ?: "Erro ao carregar comentários"
        }
    }
}
