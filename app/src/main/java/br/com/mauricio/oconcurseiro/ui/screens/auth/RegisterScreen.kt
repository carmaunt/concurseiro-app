package br.com.mauricio.oconcurseiro.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.ui.theme.BorderDefault
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimary
import br.com.mauricio.oconcurseiro.ui.theme.ErrorBorder
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceBackground
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceCard
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceWhite
import br.com.mauricio.oconcurseiro.ui.theme.TextOnBrand
import br.com.mauricio.oconcurseiro.ui.theme.TextPrimary
import br.com.mauricio.oconcurseiro.ui.theme.TextSecondary
import br.com.mauricio.oconcurseiro.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onCadastroSuccess: () -> Unit,
    onVoltarLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var erroLocal by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Criar conta",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Cadastre-se para comentar e salvar seu progresso.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                erroLocal = null
            },
            singleLine = true,
            label = { Text("E-mail") },
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
            onValueChange = {
                senha = it
                erroLocal = null
            },
            singleLine = true,
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
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
            value = confirmarSenha,
            onValueChange = {
                confirmarSenha = it
                erroLocal = null
            },
            singleLine = true,
            label = { Text("Confirmar senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BorderDefault,
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceCard
            )
        )

        val mensagemErro = erroLocal ?: viewModel.erro
        if (mensagemErro != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = mensagemErro,
                color = ErrorBorder,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (senha != confirmarSenha) {
                    erroLocal = "As senhas não coincidem"
                    return@Button
                }

                if (senha.length < 6) {
                    erroLocal = "A senha deve ter pelo menos 6 caracteres"
                    return@Button
                }

                viewModel.cadastrar(
                    email = email.trim(),
                    senha = senha,
                    onSucesso = onCadastroSuccess
                )
            },
            enabled = !viewModel.isLoading &&
                    email.isNotBlank() &&
                    senha.isNotBlank() &&
                    confirmarSenha.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    color = TextOnBrand,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Criar conta",
                    color = TextOnBrand,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Já tem conta? Entrar",
            color = BrandPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onVoltarLogin() }
        )
    }
}