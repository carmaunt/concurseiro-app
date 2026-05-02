package br.com.mauricio.oconcurseiro.data.auth

import br.com.mauricio.oconcurseiro.data.remote.GoogleLoginRequestDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: android.content.Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun usuarioIdOuGuest(): String = auth.currentUser?.uid ?: "guest"

    fun usuarioAtual() = auth.currentUser

    fun estaAutenticado(): Boolean {
        TokenManager.carregarTokens(context)
        return auth.currentUser != null && !TokenManager.accessToken.isNullOrBlank()
    }

    suspend fun cadastrarComEmail(email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha).await()
    }

    suspend fun loginComEmail(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha).await()

        val firebaseToken = auth.currentUser
            ?.getIdToken(true)
            ?.await()
            ?.token
            ?: throw Exception("Erro ao obter token do Firebase")

        val response = ConcurseiroApiProvider.api.loginComFirebase(
            GoogleLoginRequestDto(firebaseToken)
        )

        val data = response.data ?: throw Exception("Erro no backend")

        TokenManager.salvarTokens(
            context = context,
            accessToken = data.accessToken,
            refreshToken = data.refreshToken
        )
    }

    suspend fun loginComGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()

        val firebaseToken = auth.currentUser
            ?.getIdToken(true)
            ?.await()
            ?.token
            ?: throw Exception("Erro ao obter token do Firebase")

        val response = ConcurseiroApiProvider.api.loginComFirebase(
            GoogleLoginRequestDto(firebaseToken)
        )

        val data = response.data ?: throw Exception("Erro no backend")

        TokenManager.salvarTokens(
            context = context,
            accessToken = data.accessToken,
            refreshToken = data.refreshToken
        )
    }

    fun logout() {
        auth.signOut()
        TokenManager.limpar(context)
    }
}