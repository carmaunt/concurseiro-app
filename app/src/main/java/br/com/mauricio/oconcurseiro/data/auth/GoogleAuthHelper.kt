package br.com.mauricio.oconcurseiro.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

suspend fun obterIdTokenGoogle(context: Context): String? {
    val credentialManager = CredentialManager.create(context)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId("389724825283-44h5hhvdp7ion9ul7srmllej47p0llg8.apps.googleusercontent.com")
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val result = credentialManager.getCredential(
        request = request,
        context = context
    )

    val credential = result.credential

    if (credential is androidx.credentials.CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        return googleCredential.idToken
    }

    return null
}