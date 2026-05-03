package br.com.mauricio.oconcurseiro.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

suspend fun obterIdTokenGoogle(context: Context): String? {
    val credentialManager = CredentialManager.create(context)

    val googleOption = GetSignInWithGoogleOption.Builder(
        "389724825283-44h5hhvdp7ion9ul7srmllej47p0llg8.apps.googleusercontent.com"
    ).build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleOption)
        .build()

    val result = try {
        credentialManager.getCredential(
            request = request,
            context = context
        )
    } catch (_: NoCredentialException) {
        return null
    } catch (_: Exception) {
        return null
    }

    val credential = result.credential

    if (
        credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        return googleCredential.idToken
    }

    return null
}