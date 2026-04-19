package br.com.mauricio.oconcurseiro.data.auth

import br.com.mauricio.oconcurseiro.data.remote.GoogleLoginRequestDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun usuarioIdOuGuest(): String = auth.currentUser?.uid ?: "guest"

    fun usuarioAtual() = auth.currentUser

    fun estaAutenticado(): Boolean = auth.currentUser != null

    suspend fun cadastrarComEmail(email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha).await()
    }

    suspend fun loginComEmail(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha).await()
    }

    suspend fun loginComGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()

        val firebaseToken = auth.currentUser
            ?.getIdToken(true)
            ?.await()
            ?.token
            ?: throw Exception("Erro ao obter token do Firebase")

        val response = ConcurseiroApiProvider.api.loginComGoogle(
            GoogleLoginRequestDto(firebaseToken)
        )

        val data = response.data ?: throw Exception("Erro no backend")

        TokenManager.salvarTokens(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken
        )
    }

    fun logout() {
        auth.signOut()
    }
}