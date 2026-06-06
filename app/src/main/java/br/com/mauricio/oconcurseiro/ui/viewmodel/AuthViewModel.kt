package br.com.mauricio.oconcurseiro.ui.viewmodel

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.auth.GoogleLoginCanceladoException
import br.com.mauricio.oconcurseiro.data.auth.obterIdTokenGoogle
import br.com.mauricio.oconcurseiro.data.local.GuestUsageManager
import com.google.firebase.FirebaseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val guestManager: GuestUsageManager
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    var usuarioAutenticado by mutableStateOf(repository.estaAutenticado())
        private set

    var mostrarLimiteDialog by mutableStateOf(false)
        private set

    var loginDialogOrigemComentarios by mutableStateOf(false)
        private set

    var mensagemSucesso by mutableStateOf<String?>(null)
        private set

    val nomeUsuario: String
        get() {
            val user = repository.usuarioAtual()
            return user?.displayName?.takeIf { it.isNotBlank() }
                ?: user?.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
                ?: "Usuário"
        }

    fun estaAutenticado(): Boolean = usuarioAutenticado

    fun sincronizarAutenticacao() {
        usuarioAutenticado = repository.estaAutenticado()
    }

    fun abrirDialogLimite(origemComentarios: Boolean = false) {
        loginDialogOrigemComentarios = origemComentarios
        mostrarLimiteDialog = true
    }

    fun fecharDialog() {
        mostrarLimiteDialog = false
        loginDialogOrigemComentarios = false
    }

    fun podeResolverSemLogin(): Boolean = guestManager.podeResolverSemLogin()

    fun podeResolverQuestao(questaoId: String): Boolean {
        val pode = guestManager.podeResolverQuestao(questaoId)
        if (!pode) abrirDialogLimite()
        return pode
    }

    fun registrarResolucao(questaoId: String) {
        guestManager.registrarResolucao(questaoId)
    }

    fun loginEmail(email: String, senha: String, onSucesso: () -> Unit) {
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                repository.loginComEmail(email, senha)
                usuarioAutenticado = repository.estaAutenticado()
                onSucesso()
            } catch (e: Exception) {
                erro = e.message ?: "Erro ao fazer login"
            } finally {
                isLoading = false
            }
        }
    }

    fun cadastrar(email: String, senha: String, onSucesso: () -> Unit) {
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                repository.cadastrarComEmail(email, senha)
                repository.logout()
                usuarioAutenticado = repository.estaAutenticado()
                onSucesso()
            } catch (e: Exception) {
                erro = e.message ?: "Erro ao cadastrar"
            } finally {
                isLoading = false
            }
        }
    }

    fun loginComGoogle(idToken: String, onSucesso: () -> Unit) {
        isLoading = true
        erro = null

        viewModelScope.launch {
            try {
                repository.loginComGoogle(idToken)
                usuarioAutenticado = repository.estaAutenticado()
                onSucesso()
            } catch (e: Exception) {
                erro = "Erro ao login com Google"
            } finally {
                isLoading = false
            }
        }
    }

    fun iniciarLoginComGoogle() {
        isLoading = true
        erro = null
    }

    fun concluirLoginComGoogle(resultCode: Int, data: Intent?, onSucesso: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = obterIdTokenGoogle(resultCode, data)
                repository.loginComGoogle(token)
                usuarioAutenticado = repository.estaAutenticado()
                mensagemSucesso = "Login realizado com sucesso."
                onSucesso()
            } catch (_: GoogleLoginCanceladoException) {
                erro = null
            } catch (e: Exception) {
                erro = e.message ?: "Erro ao login com Google"
            } finally {
                isLoading = false
            }
        }
    }

    fun excluirConta(onSucesso: () -> Unit) {
        isLoading = true
        erro = null
        mensagemSucesso = null

        viewModelScope.launch {
            try {
                repository.excluirConta()
                usuarioAutenticado = repository.estaAutenticado()
                mensagemSucesso = "Conta excluída com sucesso."
                onSucesso()
            } catch (e: FirebaseException) {
                erro = "Por segurança, faça login novamente antes de excluir sua conta."
            } catch (e: Exception) {
                erro = e.message ?: "Erro ao excluir conta"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        repository.logout()
        usuarioAutenticado = repository.estaAutenticado()
        erro = null
        mensagemSucesso = null
    }

    fun consumirMensagemSucesso() {
        mensagemSucesso = null
    }
}
