package br.com.mauricio.oconcurseiro.data.auth

import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.data.remote.RetrofitClient
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

object ConcurseiroApiProvider {
    val api: ConcurseiroApi
        get() = RetrofitClient.api
}

object TokenManager {
    private const val PREFS_NAME = "auth_tokens"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    @Volatile
    var accessToken: String? = null
        private set

    @Volatile
    var refreshToken: String? = null
        private set

    private fun encryptedPrefs(context: android.content.Context): android.content.SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun carregarTokens(context: android.content.Context) {
        val prefs = encryptedPrefs(context)

        accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
        refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun salvarTokens(
        context: android.content.Context,
        accessToken: String,
        refreshToken: String
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken

        encryptedPrefs(context)
            .edit {
                putString(KEY_ACCESS_TOKEN, accessToken)
                    .putString(KEY_REFRESH_TOKEN, refreshToken)
            }
    }

    fun limpar(context: android.content.Context) {
        accessToken = null
        refreshToken = null

        encryptedPrefs(context)
            .edit {
                clear()
            }
    }
}