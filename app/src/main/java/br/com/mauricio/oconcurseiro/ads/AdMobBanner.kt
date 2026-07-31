package br.com.mauricio.oconcurseiro.ads

import android.view.View
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.mauricio.oconcurseiro.BuildConfig
import br.com.mauricio.oconcurseiro.ui.theme.TextPlaceholder
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Banner adaptativo ancorado. O AdView acompanha o ciclo de vida da tela e é
 * destruído quando sai da composição para evitar retenção da Activity.
 */
@Composable
@Suppress("DEPRECATION") // API adaptativa recomendada na documentação do SDK Legacy 25.x.
fun AdMobBanner(
    adsReady: Boolean,
    modifier: Modifier = Modifier
) {
    val adUnitId = BuildConfig.ADMOB_BANNER_AD_UNIT_ID
    if (!adsReady || adUnitId.isBlank()) return

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthInDp = maxWidth.value.toInt().coerceAtLeast(1)
        var isLoaded by remember(widthInDp, adUnitId) { mutableStateOf(false) }

        val adView = remember(widthInDp, adUnitId) {
            AdView(context).apply {
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context,
                        widthInDp
                    )
                )
                this.adUnitId = adUnitId
                visibility = View.INVISIBLE
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        visibility = View.VISIBLE
                        isLoaded = true
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        visibility = View.GONE
                        isLoaded = false
                    }
                }
            }
        }

        DisposableEffect(adView, lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> adView.resume()
                    Lifecycle.Event.ON_PAUSE -> adView.pause()
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                adView.destroy()
            }
        }

        LaunchedEffect(adView) {
            adView.loadAd(AdRequest.Builder().build())
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoaded) {
                Text(
                    text = "Publicidade",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPlaceholder,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            AndroidView(
                factory = { adView },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
