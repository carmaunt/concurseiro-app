package br.com.mauricio.oconcurseiro.data.auth

import br.com.mauricio.oconcurseiro.data.remote.GoogleLoginRequestDto
import br.com.mauricio.oconcurseiro.data.remote.ExcluirContaRequestDto
import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: android.content.Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val api: br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi,
    private val tokenStorage: TokenStorage,
    private val respostaDao: RespostaDao
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
        val usuarioId = user.uid
        val firebaseIdToken = user.getIdToken(true).await().token
            ?: throw Exception("Não foi possível confirmar sua identidade. Faça login novamente.")

        api.excluirConta(ExcluirContaRequestDto(firebaseIdToken))

        respostaDao.excluirRespostasDoUsuario(usuarioId)
        tokenStorage.limpar()
        auth.signOut()
    }

    fun logout() {
        auth.signOut()
        tokenStorage.limpar()
    }
}
