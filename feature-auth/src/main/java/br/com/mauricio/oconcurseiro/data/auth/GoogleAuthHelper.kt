package br.com.mauricio.oconcurseiro.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

private const val WEB_CLIENT_ID =
    "389724825283-44h5hhvdp7ion9ul7srmllej47p0llg8.apps.googleusercontent.com"

class GoogleLoginCanceladoException : Exception()

class GoogleLoginException(message: String, cause: Throwable? = null) : Exception(message, cause)

suspend fun obterIdTokenGoogle(context: Context): String {
    val credentialManager = CredentialManager.create(context)

    return try {
        obterIdTokenComOpcao(
            credentialManager = credentialManager,
            context = context,
            request = GetCredentialRequest.Builder()
                .addCredentialOption(
                    GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()
                )
                .build()
        )
    } catch (_: NoCredentialException) {
        obterIdTokenComOpcao(
            credentialManager = credentialManager,
            context = context,
            request = GetCredentialRequest.Builder()
                .addCredentialOption(
                    GetGoogleIdOption.Builder()
                        .setServerClientId(WEB_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(false)
                        .setAutoSelectEnabled(false)
                        .build()
                )
                .build()
        )
    } catch (_: GetCredentialCancellationException) {
        throw GoogleLoginCanceladoException()
    } catch (e: GetCredentialException) {
        throw GoogleLoginException(
            "Não foi possível iniciar o login com Google. Verifique se o Google Play Services está atualizado.",
            e
        )
    }
}

private suspend fun obterIdTokenComOpcao(
    credentialManager: CredentialManager,
    context: Context,
    request: GetCredentialRequest
): String {
    val result = credentialManager.getCredential(
        request = request,
        context = context
    )

    val credential = result.credential

    if (
        credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        val googleCredential = try {
            GoogleIdTokenCredential.createFrom(credential.data)
        } catch (e: GoogleIdTokenParsingException) {
            throw GoogleLoginException("Erro ao ler a credencial retornada pelo Google.", e)
        }
        return googleCredential.idToken
    }

    throw GoogleLoginException("O Google retornou uma credencial incompatível com este login.")
}
