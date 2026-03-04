package br.com.mauricio.oconcurseiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import br.com.mauricio.oconcurseiro.ui.navigation.AppNavigation
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimary
import br.com.mauricio.oconcurseiro.ui.theme.OConcurseiroTheme
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(BrandPrimary.toArgb()),
            navigationBarStyle = SystemBarStyle.light(
                SurfaceBackground.toArgb(),
                SurfaceBackground.toArgb()
            )
        )

        setContent {
            OConcurseiroTheme {
                AppNavigation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    OConcurseiroTheme {
        AppNavigation()
    }
}