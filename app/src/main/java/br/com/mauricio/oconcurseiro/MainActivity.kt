package br.com.mauricio.oconcurseiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.mauricio.oconcurseiro.ui.navigation.AppNavigation
import br.com.mauricio.oconcurseiro.ui.theme.OConcurseiroTheme
import br.com.mauricio.oconcurseiro.data.analytics.AnalyticsTracker

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        analyticsTracker.appOpened()

        setContent {
            OConcurseiroTheme {
                AppNavigation(analyticsTracker)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        analyticsTracker.startSession()
    }

    override fun onStop() {
        analyticsTracker.stopSession()
        super.onStop()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    OConcurseiroTheme {
        AppNavigation()
    }
}
