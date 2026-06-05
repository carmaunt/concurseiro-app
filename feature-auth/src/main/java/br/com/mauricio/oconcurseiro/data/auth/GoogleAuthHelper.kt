@file:Suppress("DEPRECATION")

package br.com.mauricio.oconcurseiro.data.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class GoogleLoginCanceladoException : Exception()

class GoogleLoginException(message: String, cause: Throwable? = null) : Exception(message, cause)

fun criarIntentLoginGoogle(context: Context): Intent {
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(GoogleAuthConfig.WEB_CLIENT_ID)
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, options).signInIntent
}

fun obterIdTokenGoogle(resultCode: Int, data: Intent?): String {
    if (resultCode == Activity.RESULT_CANCELED || data == null) {
        throw GoogleLoginCanceladoException()
    }

    val account = try {
        GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
    } catch (e: ApiException) {
        if (e.statusCode == CommonStatusCodes.CANCELED) {
            throw GoogleLoginCanceladoException()
        }
        throw GoogleLoginException(
            "Não foi possível concluir o login com Google (código ${e.statusCode}).",
            e
        )
    }

    return account.idToken
        ?: throw GoogleLoginException("O Google não retornou um token válido para este aplicativo.")
}
