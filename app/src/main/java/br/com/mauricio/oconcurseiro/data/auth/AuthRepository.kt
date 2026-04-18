package br.com.mauricio.oconcurseiro.data.auth

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
    }

    fun logout() {
        auth.signOut()
    }
}