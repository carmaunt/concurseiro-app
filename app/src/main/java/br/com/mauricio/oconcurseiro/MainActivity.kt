package br.com.mauricio.oconcurseiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import br.com.mauricio.oconcurseiro.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color(0xFFFF6A2A).toArgb()),
            navigationBarStyle = SystemBarStyle.light(
                Color(0xFFF6F7FB).toArgb(),
                Color(0xFFF6F7FB).toArgb()
            )
        )

        setContent {
            AppNavigation()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    AppNavigation()
}