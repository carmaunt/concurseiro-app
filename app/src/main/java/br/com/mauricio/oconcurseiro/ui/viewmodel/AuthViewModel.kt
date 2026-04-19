package br.com.mauricio.oconcurseiro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    var usuarioAutenticado by mutableStateOf(repository.estaAutenticado())
        private set

    fun estaAutenticado(): Boolean = usuarioAutenticado

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

        android.util.Log.d("FIREBASE_TOKEN", idToken)

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

    fun logout() {
        repository.logout()
        usuarioAutenticado = repository.estaAutenticado()
        erro = null
    }
}