package br.com.mauricio.oconcurseiro.ui.screens.privacidade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.theme.*

@Composable
fun PrivacidadeDadosScreen(
    usuarioAutenticado: Boolean,
    isLoading: Boolean,
    erro: String?,
    onBack: () -> Unit,
    onAvisoLegal: () -> Unit,
    onExcluirConta: (onSucesso: () -> Unit) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var mostrarConfirmacaoExclusao by remember { mutableStateOf(false) }

    if (mostrarConfirmacaoExclusao) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) mostrarConfirmacaoExclusao = false },
            title = { Text("Excluir minha conta?") },
            text = {
                Text(
                    "Esta ação é permanente. Os dados da sua conta serão removidos ou anonimizados conforme a Política de Privacidade. Confirme apenas se deseja continuar."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !isLoading,
                    onClick = {
                        onExcluirConta {
                            mostrarConfirmacaoExclusao = false
                            onBack()
                        }
                    }
                ) {
                    Text("Excluir conta")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isLoading,
                    onClick = { mostrarConfirmacaoExclusao = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        AppHeader(
            title = "Privacidade e dados",
            subtitle = "Controle, transparência e informações da conta",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrivacyCard {
                Text(
                    text = "Como usamos dados",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(10.dp))

                PrivacyParagraph("O app usa autenticação para permitir login, comentários e interações entre estudantes.")
                PrivacyParagraph("Podemos usar dados técnicos para estabilidade, diagnóstico de falhas e melhoria do aplicativo.")
                PrivacyParagraph("Você pode solicitar ou executar a exclusão da sua conta quando estiver logado.")
                PrivacyParagraph("O Concurseiro é independente e não representa órgão público, entidade governamental ou banca organizadora.")
            }

            PrivacyActionItem(
                icon = Icons.Outlined.PrivacyTip,
                title = "Política de Privacidade",
                description = "Leia a política completa publicada oficialmente.",
                trailingIcon = Icons.AutoMirrored.Outlined.OpenInNew,
                onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) }
            )

            Button(
                onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver Política de Privacidade completa")
            }

            PrivacyActionItem(
                icon = Icons.Outlined.Gavel,
                title = "Aviso legal",
                description = "Entenda a independência do app e consulte fontes oficiais.",
                onClick = onAvisoLegal
            )

            if (usuarioAutenticado) {
                PrivacyActionItem(
                    icon = Icons.Outlined.DeleteOutline,
                    title = "Excluir minha conta",
                    description = "Remover acesso, sessão local e dados vinculados quando aplicável.",
                    danger = true,
                    onClick = { mostrarConfirmacaoExclusao = true }
                )
            }

            if (!erro.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = ErrorBg
                ) {
                    Text(
                        text = erro,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorBorder,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceWhite,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            content = content
        )
    }
}

@Composable
private fun PrivacyParagraph(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}

@Composable
private fun PrivacyActionItem(
    icon: ImageVector,
    title: String,
    description: String,
    trailingIcon: ImageVector? = null,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = if (danger) ErrorBorder else TextPrimary

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (trailingIcon != null) {
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = TextPlaceholder,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
