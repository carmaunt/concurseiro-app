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
import br.com.mauricio.oconcurseiro.data.analytics.AnalyticsTracker
import br.com.mauricio.oconcurseiro.data.local.GuestUsageManager
import br.com.mauricio.oconcurseiro.domain.usecase.MigrarProgressoVisitanteUseCase
import com.google.firebase.FirebaseException
import br.com.mauricio.oconcurseiro.util.mapErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val guestManager: GuestUsageManager,
    private val migrarProgressoVisitanteUseCase: MigrarProgressoVisitanteUseCase,
    private val analyticsTracker: AnalyticsTracker
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
                val migradas = preservarProgressoVisitante()
                usuarioAutenticado = repository.estaAutenticado()
                mensagemSucesso = mensagemDeAcesso(migradas)
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
                repository.loginComEmail(email, senha)
                val migradas = preservarProgressoVisitante()
                usuarioAutenticado = repository.estaAutenticado()
                mensagemSucesso = if (migradas > 0) {
                    "Conta criada e progresso preservado."
                } else {
                    "Conta criada com sucesso."
                }
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
                val migradas = preservarProgressoVisitante()
                usuarioAutenticado = repository.estaAutenticado()
                mensagemSucesso = mensagemDeAcesso(migradas)
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
                val migradas = preservarProgressoVisitante()
                usuarioAutenticado = repository.estaAutenticado()
                mensagemSucesso = mensagemDeAcesso(migradas)
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
                erro = mapErrorMessage(e)
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

    private suspend fun preservarProgressoVisitante(): Int {
        val usuarioId = repository.usuarioIdOuGuest()
        if (usuarioId == "guest") return 0

        val migradas = runCatching {
            migrarProgressoVisitanteUseCase(usuarioId)
        }.getOrDefault(0)

        if (migradas > 0) {
            guestManager.limparAposMigracao()
            analyticsTracker.guestProgressMigrated(migradas)
        }
        return migradas
    }

    private fun mensagemDeAcesso(respostasMigradas: Int): String {
        return if (respostasMigradas > 0) {
            "Login realizado. Seu progresso foi preservado."
        } else {
            "Login realizado com sucesso."
        }
    }
}
