package br.com.mauricio.oconcurseiro.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.com.mauricio.oconcurseiro.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.ui.theme.BorderDefault
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimary
import br.com.mauricio.oconcurseiro.ui.theme.ErrorBorder
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceBackground
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceCard
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceWhite
import br.com.mauricio.oconcurseiro.ui.theme.TextOnBrand
import br.com.mauricio.oconcurseiro.ui.theme.TextPlaceholder
import br.com.mauricio.oconcurseiro.ui.theme.TextPrimary
import br.com.mauricio.oconcurseiro.ui.theme.TextSecondary
import br.com.mauricio.oconcurseiro.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onAbrirCadastro: () -> Unit,
    onLoginGoogleClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "OConcurseiro",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = BrandPrimary
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Entre para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            label = { Text("E-mail") },
            placeholder = { Text("Digite seu e-mail") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BorderDefault,
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceCard
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            singleLine = true,
            label = { Text("Senha") },
            placeholder = { Text("Digite sua senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BorderDefault,
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceCard
            )
        )

        if (viewModel.erro != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = viewModel.erro ?: "",
                color = ErrorBorder,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.loginEmail(
                    email = email.trim(),
                    senha = senha,
                    onSucesso = onLoginSuccess
                )
            },
            enabled = email.isNotBlank() && senha.length >= 6 && !viewModel.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.isLoading) BrandPrimary.copy(alpha = 0.6f) else BrandPrimary
            )
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    color = TextOnBrand,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Entrar com e-mail",
                    color = TextOnBrand,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !viewModel.isLoading) { onLoginGoogleClick() },
            shape = RoundedCornerShape(16.dp),
            color = SurfaceWhite,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Continuar com Google",
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Não tem conta? Criar cadastro",
            color = BrandPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onAbrirCadastro() }
        )
    }
}