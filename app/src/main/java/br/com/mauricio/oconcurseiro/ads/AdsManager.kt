package br.com.mauricio.oconcurseiro.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import br.com.mauricio.oconcurseiro.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread

/**
 * Centraliza consentimento, inicialização e frequência dos anúncios.
 *
 * O SDK de anúncios só é inicializado após o UMP confirmar que solicitações
 * podem ser feitas. Em caso de erro na primeira coleta de consentimento, o
 * comportamento é fail-closed: o app segue funcionando, mas sem anúncios.
 */
@Singleton
class AdsManager @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val consentInformation =
        UserMessagingPlatform.getConsentInformation(appContext)
    private val initializationStarted = AtomicBoolean(false)
    private val mobileAdsInitialized = AtomicBoolean(false)
    private val interstitialLoadInProgress = AtomicBoolean(false)
    private val preferences = appContext.getSharedPreferences(
        ADS_PREFERENCES,
        Context.MODE_PRIVATE
    )

    private val _adsReady = MutableStateFlow(false)
    val adsReady: StateFlow<Boolean> = _adsReady.asStateFlow()

    private val _privacyOptionsRequired = MutableStateFlow(false)
    val privacyOptionsRequired: StateFlow<Boolean> =
        _privacyOptionsRequired.asStateFlow()

    @Volatile
    private var interstitialAd: InterstitialAd? = null
    private var answeredSinceInterstitial = 0

    fun requestConsent(activity: Activity) {
        val requestParameters = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            requestParameters,
            {
                updatePrivacyOptionsRequirement()
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    updatePrivacyOptionsRequirement()
                    initializeAdsIfAllowed()
                }
                initializeAdsIfAllowed()
            },
            {
                // Uma decisão válida de uma sessão anterior ainda pode permitir
                // anúncios mesmo se a atualização atual falhar por falta de rede.
                updatePrivacyOptionsRequirement()
                initializeAdsIfAllowed()
            }
        )

        // requestConsentInfoUpdate restaura sincronamente o estado anterior.
        initializeAdsIfAllowed()
    }

    fun showPrivacyOptions(
        activity: Activity,
        onComplete: (errorMessage: String?) -> Unit = {}
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            updatePrivacyOptionsRequirement()
            if (consentInformation.canRequestAds()) {
                initializeAdsIfAllowed()
            } else {
                interstitialAd = null
                _adsReady.value = false
            }
            onComplete(formError?.message)
        }
    }

    fun recordQuestionAnswered() {
        if (
            !_adsReady.value ||
            BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID.isBlank()
        ) {
            return
        }
        answeredSinceInterstitial++
        if (InterstitialFrequencyPolicy.hasEnoughAnswers(answeredSinceInterstitial)) {
            preloadInterstitial()
        }
    }

    /**
     * Exibe um intersticial somente em uma transição natural entre questões.
     * A continuação nunca depende de rede ou do carregamento do anúncio.
     */
    fun showInterstitialIfEligible(
        activity: Activity,
        onContinue: () -> Unit
    ) {
        val lastShownAt = preferences.getLong(LAST_INTERSTITIAL_AT, 0L)
        val eligible = InterstitialFrequencyPolicy.isEligible(
            answeredQuestions = answeredSinceInterstitial,
            lastShownAtMillis = lastShownAt,
            nowMillis = System.currentTimeMillis()
        )
        val ad = interstitialAd

        if (
            !_adsReady.value ||
            !eligible ||
            ad == null ||
            activity.isFinishing ||
            activity.isDestroyed
        ) {
            if (eligible && ad == null) preloadInterstitial()
            onContinue()
            return
        }

        interstitialAd = null
        var continuationDelivered = false

        fun continueOnce() {
            if (!continuationDelivered) {
                continuationDelivered = true
                onContinue()
            }
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                answeredSinceInterstitial = 0
                preferences.edit()
                    .putLong(LAST_INTERSTITIAL_AT, System.currentTimeMillis())
                    .apply()
                preloadInterstitial()
                continueOnce()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                preloadInterstitial()
                continueOnce()
            }
        }

        try {
            ad.show(activity)
        } catch (_: RuntimeException) {
            preloadInterstitial()
            continueOnce()
        }
    }

    private fun initializeAdsIfAllowed() {
        if (!consentInformation.canRequestAds() || !hasConfiguredAdUnits()) {
            interstitialAd = null
            _adsReady.value = false
            return
        }

        if (mobileAdsInitialized.get()) {
            _adsReady.value = true
            preloadInterstitial()
            return
        }

        if (!initializationStarted.compareAndSet(false, true)) return

        val requestConfiguration = RequestConfiguration.Builder()
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_PG)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        thread(name = "admob-initialization", isDaemon = true) {
            try {
                MobileAds.initialize(appContext) {
                    mainHandler.post {
                        mobileAdsInitialized.set(true)
                        if (consentInformation.canRequestAds()) {
                            _adsReady.value = true
                            preloadInterstitial()
                        } else {
                            _adsReady.value = false
                        }
                    }
                }
            } catch (_: RuntimeException) {
                mainHandler.post {
                    initializationStarted.set(false)
                    _adsReady.value = false
                }
            }
        }
    }

    private fun preloadInterstitial() {
        if (
            !_adsReady.value ||
            BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID.isBlank() ||
            interstitialAd != null ||
            !interstitialLoadInProgress.compareAndSet(false, true)
        ) {
            return
        }

        mainHandler.post {
            if (!_adsReady.value || !consentInformation.canRequestAds()) {
                interstitialLoadInProgress.set(false)
                return@post
            }
            try {
                InterstitialAd.load(
                    appContext,
                    BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            interstitialLoadInProgress.set(false)
                            interstitialAd =
                                if (_adsReady.value && consentInformation.canRequestAds()) {
                                    ad
                                } else {
                                    null
                                }
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            interstitialLoadInProgress.set(false)
                            interstitialAd = null
                        }
                    }
                )
            } catch (_: RuntimeException) {
                interstitialLoadInProgress.set(false)
                interstitialAd = null
            }
        }
    }

    private fun updatePrivacyOptionsRequirement() {
        _privacyOptionsRequired.value =
            consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    private fun hasConfiguredAdUnits(): Boolean {
        return BuildConfig.ADMOB_BANNER_AD_UNIT_ID.isNotBlank() ||
            BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID.isNotBlank()
    }

    private companion object {
        const val ADS_PREFERENCES = "admob_frequency"
        const val LAST_INTERSTITIAL_AT = "last_interstitial_at"
    }
}

internal object InterstitialFrequencyPolicy {
    private const val MIN_ANSWERS = 8
    private const val MIN_INTERVAL_MILLIS = 10 * 60 * 1000L

    fun hasEnoughAnswers(answeredQuestions: Int): Boolean {
        return answeredQuestions >= MIN_ANSWERS
    }

    fun isEligible(
        answeredQuestions: Int,
        lastShownAtMillis: Long,
        nowMillis: Long
    ): Boolean {
        if (!hasEnoughAnswers(answeredQuestions)) return false
        if (lastShownAtMillis <= 0L) return true
        return nowMillis - lastShownAtMillis >= MIN_INTERVAL_MILLIS
    }
}
