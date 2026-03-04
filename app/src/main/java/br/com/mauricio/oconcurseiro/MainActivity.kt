package br.com.mauricio.oconcurseiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.mauricio.oconcurseiro.ui.navigation.AppNavigation
import br.com.mauricio.oconcurseiro.ui.theme.OConcurseiroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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
