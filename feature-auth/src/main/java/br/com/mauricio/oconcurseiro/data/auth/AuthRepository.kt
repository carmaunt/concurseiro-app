package br.com.mauricio.oconcurseiro.data.auth

import br.com.mauricio.oconcurseiro.data.remote.GoogleLoginRequestDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: android.content.Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val api: br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi,
    private val tokenStorage: TokenStorage
) {
    init {
        tokenStorage.carregarTokens()
    }

    fun usuarioIdOuGuest(): String = auth.currentUser?.uid ?: "guest"

    fun usuarioAtual() = auth.currentUser

    fun estaAutenticado(): Boolean {
        return auth.currentUser != null && !tokenStorage.accessToken.isNullOrBlank()
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

        val response = api.loginComFirebase(
            GoogleLoginRequestDto(firebaseToken)
        )

        val data = response.data ?: throw Exception("Erro no backend")

        tokenStorage.salvarTokens(
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

        val response = api.loginComFirebase(
            GoogleLoginRequestDto(firebaseToken)
        )

        val data = response.data ?: throw Exception("Erro no backend")

        tokenStorage.salvarTokens(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken
        )
    }

    suspend fun excluirConta() {
        val user = auth.currentUser ?: throw Exception("Nenhum usuário autenticado")

        // TODO: quando o backend disponibilizar endpoint de exclusão de conta,
        // chamar a API antes de remover a conta do Firebase. O endpoint deve remover
        // ou anonimizar dados do usuário conforme a Política de Privacidade.
        user.delete().await()
        tokenStorage.limpar()
        auth.signOut()
    }

    fun logout() {
        auth.signOut()
        tokenStorage.limpar()
    }
}
