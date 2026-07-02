package br.com.mauricio.oconcurseiro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import br.com.mauricio.oconcurseiro.ui.navigation.AppNavigation
import br.com.mauricio.oconcurseiro.ui.theme.OConcurseiroTheme
import br.com.mauricio.oconcurseiro.data.analytics.AnalyticsTracker

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var analyticsTracker: AnalyticsTracker
    private var openQuestionsFromNotification by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openQuestionsFromNotification = intent.shouldOpenQuestions()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        analyticsTracker.appOpened()

        setContent {
            OConcurseiroTheme {
                AppNavigation(
                    analyticsTracker = analyticsTracker,
                    openQuestionsFromNotification = openQuestionsFromNotification,
                    onNotificationNavigationHandled = {
                        openQuestionsFromNotification = false
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.shouldOpenQuestions()) {
            openQuestionsFromNotification = true
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

    private fun Intent?.shouldOpenQuestions(): Boolean {
        return this?.getBooleanExtra(EXTRA_OPEN_QUESTIONS, false) == true
    }

    companion object {
        const val EXTRA_OPEN_QUESTIONS = "br.com.mauricio.oconcurseiro.OPEN_QUESTIONS"
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    OConcurseiroTheme {
        AppNavigation()
    }
}
