package br.com.mauricio.oconcurseiro.installreferrer

import android.content.Context
import android.net.Uri
import android.util.Log
import br.com.mauricio.oconcurseiro.data.analytics.AnalyticsTracker
import br.com.mauricio.oconcurseiro.data.analytics.AcquisitionAttribution
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstallReferrerTracker @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val analyticsTracker: AnalyticsTracker,
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun trackIfNeeded() {
        if (preferences.getBoolean(KEY_TRACKED, false)) return
        if (!isFreshInstall()) {
            markTracked()
            analyticsTracker.installReferrerUnavailable(
                reason = "existing_install_update_skipped",
                metadata = installTimeMetadata(),
            )
            return
        }

        val client = InstallReferrerClient.newBuilder(context).build()
        client.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> trackReferrer(client)
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        markTracked()
                        analyticsTracker.installReferrerUnavailable("feature_not_supported")
                        client.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        analyticsTracker.installReferrerUnavailable("service_unavailable")
                        client.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR -> {
                        markTracked()
                        analyticsTracker.installReferrerUnavailable("developer_error")
                        client.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED -> {
                        analyticsTracker.installReferrerUnavailable("service_disconnected")
                        client.endConnection()
                    }
                    else -> {
                        analyticsTracker.installReferrerUnavailable("unknown_response", mapOf("response_code" to responseCode))
                        client.endConnection()
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                analyticsTracker.installReferrerUnavailable("service_disconnected")
            }
        })
    }

    private fun trackReferrer(client: InstallReferrerClient) {
        runCatching {
            val response = client.installReferrer
            val rawReferrer = response.installReferrer.orEmpty()
            val referrerParams = parseReferrer(rawReferrer)
            analyticsTracker.setAcquisitionId(
                AcquisitionAttribution.normalize(referrerParams["referrer_landing_session_id"]),
            )
            val metadata = buildMap {
                put("referrer_found", rawReferrer.isNotBlank())
                put("referrer_parameter_count", referrerParams.size)
                put("referrer_click_timestamp_seconds", response.referrerClickTimestampSeconds)
                put("install_begin_timestamp_seconds", response.installBeginTimestampSeconds)
                put("google_play_instant", response.googlePlayInstantParam)
                putAll(referrerParams)
            }

            analyticsTracker.installAttributed(metadata)
            markTracked()
        }.onFailure {
            Log.w(TAG, "Falha ao ler install referrer", it)
            analyticsTracker.installReferrerUnavailable("read_failed", mapOf("error_type" to it.javaClass.simpleName))
        }
        client.endConnection()
    }

    private fun parseReferrer(rawReferrer: String): Map<String, String> {
        if (rawReferrer.isBlank()) return emptyMap()

        val decoded = runCatching {
            URLDecoder.decode(rawReferrer, Charsets.UTF_8.name())
        }.getOrDefault(rawReferrer)

        return Uri.parse("https://concurseiro.local/?$decoded")
            .queryParameterNames
            .associateWith { key -> Uri.parse("https://concurseiro.local/?$decoded").getQueryParameter(key).orEmpty() }
            .filterKeys { it in TRACKED_REFERRER_KEYS }
            .mapKeys { (key, _) -> "referrer_$key" }
    }

    private fun markTracked() {
        preferences.edit().putBoolean(KEY_TRACKED, true).apply()
    }

    private fun isFreshInstall(): Boolean {
        val packageInfo = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }.getOrNull() ?: return true

        val delta = (packageInfo.lastUpdateTime - packageInfo.firstInstallTime).coerceAtLeast(0)
        return delta <= FRESH_INSTALL_WINDOW_MS
    }

    private fun installTimeMetadata(): Map<String, Any> {
        val packageInfo = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }.getOrNull() ?: return emptyMap()

        return mapOf(
            "first_install_time_ms" to packageInfo.firstInstallTime,
            "last_update_time_ms" to packageInfo.lastUpdateTime,
        )
    }

    private companion object {
        const val TAG = "InstallReferrerTracker"
        const val PREFERENCES_NAME = "install_referrer_preferences"
        const val KEY_TRACKED = "install_referrer_tracked_v1"
        const val FRESH_INSTALL_WINDOW_MS = 30 * 60 * 1000L
        val TRACKED_REFERRER_KEYS = setOf(
            "utm_source",
            "utm_medium",
            "utm_campaign",
            "utm_content",
            "utm_term",
            "cta_id",
            "landing_session_id",
            "fbclid",
            "gclid",
        )
    }
}
