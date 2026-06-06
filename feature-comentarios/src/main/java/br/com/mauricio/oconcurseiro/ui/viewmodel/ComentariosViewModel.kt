package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.domain.model.Comentario
import br.com.mauricio.oconcurseiro.domain.usecase.CriarComentarioUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.CurtirComentarioUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.DescurtirComentarioUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.ListarComentariosUseCase
import br.com.mauricio.oconcurseiro.util.mapErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComentariosViewModel @Inject constructor(
    private val listarComentariosUseCase: ListarComentariosUseCase,
    private val criarComentarioUseCase: CriarComentarioUseCase,
    private val curtirComentarioUseCase: CurtirComentarioUseCase,
    private val descurtirComentarioUseCase: DescurtirComentarioUseCase
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
        erroEnvio = null

        viewModelScope.launch {
            try {
                val resp = listarComentariosUseCase(
                    questaoId = questaoId,
                    page = paginaAtual,
                    size = 20,
                    ordenar = ordenacao
                )

                comentarios = if (resetar) resp.content else comentarios + resp.content
                temMaisPaginas = !resp.last
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
                val novo = criarComentarioUseCase(
                    questaoId = questaoId,
                    autor = autor,
                    texto = texto
                )
                comentarios = listOf(novo) + comentarios
                erroEnvio = null
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
                curtirComentarioUseCase(comentarioId)
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
                descurtirComentarioUseCase(comentarioId)
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
}
