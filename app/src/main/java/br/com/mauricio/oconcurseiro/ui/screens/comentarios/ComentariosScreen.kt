package br.com.mauricio.oconcurseiro.ui.screens.comentarios

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.data.model.Comentario
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.theme.*
import br.com.mauricio.oconcurseiro.ui.viewmodel.ComentariosViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun ComentariosScreen(
    viewModel: ComentariosViewModel,
    questaoId: String,
    onBack: () -> Unit
) {
    LaunchedEffect(questaoId) {
        viewModel.carregarComentarios(questaoId)
    }

    var textoComentario by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceBackground)) {

        AppHeader(
            title = "Comentários",
            onBack = onBack
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TabOrdenacao(
                texto = "Mais curtidos",
                selecionada = viewModel.ordenacao == "curtidas",
                onClick = { viewModel.alterarOrdenacao("curtidas") },
                modifier = Modifier.weight(1f)
            )
            TabOrdenacao(
                texto = "Mais recentes",
                selecionada = viewModel.ordenacao == "recentes",
                onClick = { viewModel.alterarOrdenacao("recentes") },
                modifier = Modifier.weight(1f)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                viewModel.isLoading && viewModel.comentarios.isEmpty() -> {
                    CircularProgressIndicator(
                        color = BrandOrange,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                viewModel.erro != null && viewModel.comentarios.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.erro ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.carregarComentarios(questaoId) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                        ) {
                            Text("Tentar novamente", color = TextOnBrand)
                        }
                    }
                }

                viewModel.comentarios.isEmpty() && !viewModel.isLoading -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💬",
                            fontSize = 48.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Nenhum comentário ainda",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Seja o primeiro a comentar!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                else -> {
                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(viewModel.comentarios, key = { it.id }) { comentario ->
                            ComentarioItem(
                                comentario = comentario,
                                jaCurtiu = viewModel.jaCurtiu(comentario.id),
                                jaDescurtiu = viewModel.jaDescurtiu(comentario.id),
                                onCurtir = { viewModel.curtir(comentario.id) },
                                onDescurtir = { viewModel.descurtir(comentario.id) }
                            )
                        }

                        if (viewModel.temMaisPaginas) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (viewModel.isLoading) {
                                        CircularProgressIndicator(
                                            color = BrandOrange,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        TextButton(onClick = { viewModel.carregarMais() }) {
                                            Text(
                                                "Carregar mais",
                                                color = BrandOrange,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (viewModel.erroEnvio != null) {
            Text(
                text = viewModel.erroEnvio ?: "",
                color = ErrorBorder,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Surface(
            shadowElevation = 8.dp,
            color = SurfaceWhite
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(
                        bottom = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoComentario,
                    onValueChange = { textoComentario = it },
                    placeholder = {
                        Text(
                            "Escreva um comentário",
                            color = TextPlaceholder,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp, max = 120.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = BorderDefault,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceCard
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 4
                )

                Spacer(Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (textoComentario.isNotBlank() && !viewModel.isEnviando)
                                BrandOrange
                            else
                                BrandOrangeDisabled
                        )
                        .clickable(enabled = textoComentario.isNotBlank() && !viewModel.isEnviando) {
                            viewModel.enviarComentario(
                                autor = "Usuário",
                                texto = textoComentario,
                                onSucesso = { textoComentario = "" }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isEnviando) {
                        CircularProgressIndicator(
                            color = TextOnBrand,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = TextOnBrand,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabOrdenacao(
    texto: String,
    selecionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (selecionada) BrandOrangeLight else SurfaceCard
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = if (selecionada) BrandOrange else TextSecondary,
            fontWeight = if (selecionada) FontWeight.SemiBold else FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ComentarioItem(
    comentario: Comentario,
    jaCurtiu: Boolean,
    jaDescurtiu: Boolean,
    onCurtir: () -> Unit,
    onDescurtir: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrandOrangeLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comentario.autor.firstOrNull()?.uppercase() ?: "?",
                    color = BrandOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comentario.autor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = formatarData(comentario.criadoEm),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Text(
                text = "⋮",
                fontSize = 20.sp,
                color = TextSecondary,
                modifier = Modifier
                    .clickable { }
                    .padding(8.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = comentario.texto,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderDefault, RoundedCornerShape(20.dp))
                    .clickable { onDescurtir() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = "Descurtir",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(180f),
                    tint = if (jaDescurtiu) ErrorBorder else TextSecondary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${comentario.descurtidas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (jaDescurtiu) ErrorBorder else TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderDefault, RoundedCornerShape(20.dp))
                    .clickable { onCurtir() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (jaCurtiu) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Curtir",
                    modifier = Modifier.size(16.dp),
                    tint = if (jaCurtiu) BrandOrange else TextSecondary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${comentario.curtidas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (jaCurtiu) BrandOrange else TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(color = BorderDefault, thickness = 0.5.dp)
    }
}

fun formatarData(dataStr: String): String {
    return try {
        val cleaned = dataStr
            .replace(Regex("\\+\\d{2}:\\d{2}$"), "")
            .replace(Regex("-\\d{2}:\\d{2}$"), "")
            .replace("Z", "")
            .substringBefore(".")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("pt", "BR"))
        parser.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        val date = parser.parse(cleaned) ?: return dataStr
        val formatter = SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale("pt", "BR"))
        formatter.format(date)
    } catch (_: Exception) {
        dataStr
    }
}
