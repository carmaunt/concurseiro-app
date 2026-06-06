package br.com.mauricio.oconcurseiro.ui.screens.comentarios

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
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
import br.com.mauricio.oconcurseiro.domain.model.Comentario
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.theme.*
import br.com.mauricio.oconcurseiro.ui.viewmodel.ComentariosViewModel
import br.com.mauricio.oconcurseiro.core.util.formatarData

@Composable
fun ComentariosScreen(
    viewModel: ComentariosViewModel,
    questaoId: String,
    usuarioAutenticado: Boolean,
    nomeUsuario: String = "Usuário",
    onLoginRequired: () -> Unit,
    onSessionExpired: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(questaoId, usuarioAutenticado) {
        if (usuarioAutenticado) {
            viewModel.carregarComentarios(questaoId)
        }
    }

    var textoComentario by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.erro, viewModel.erroEnvio) {
        val mensagemExpirada = "Sessão expirada"
        if (viewModel.erro?.contains(mensagemExpirada) == true ||
            viewModel.erroEnvio?.contains(mensagemExpirada) == true
        ) {
            onSessionExpired()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceBackground)) {

        AppHeader(
            title = "Comentários",
            onBack = onBack
        )

        val comentarios = viewModel.comentarios
        val listState = rememberLazyListState()

        val proximoDoFim by remember {
            derivedStateOf {
                val layoutInfo = listState.layoutInfo
                val totalItens = layoutInfo.totalItemsCount
                val ultimoVisivel = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                totalItens > 0 && ultimoVisivel >= totalItens - 3
            }
        }

        LaunchedEffect(proximoDoFim, usuarioAutenticado) {
            if (usuarioAutenticado && proximoDoFim && viewModel.temMaisPaginas && !viewModel.isLoading) {
                viewModel.carregarMais()
            }
        }

        if (!usuarioAutenticado) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Faça login para visualizar comentários de outros concurseiros",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (comentarios.isEmpty() && !viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum comentário ainda",
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = listState
            ) {
                items(comentarios) { comentario ->
                    ComentarioItem(
                        comentario = comentario,
                        jaCurtiu = viewModel.jaCurtiu(comentario.id),
                        jaDescurtiu = viewModel.jaDescurtiu(comentario.id),
                        onCurtir = {
                            if (usuarioAutenticado) {
                                viewModel.curtir(comentario.id)
                            } else {
                                onLoginRequired()
                            }
                        },
                        onDescurtir = {
                            if (usuarioAutenticado) {
                                viewModel.descurtir(comentario.id)
                            } else {
                                onLoginRequired()
                            }
                        }
                    )
                }

                if (viewModel.isLoading && viewModel.paginaAtual > 0) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = BrandPrimary
                            )
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
                    onValueChange = {
                        if (usuarioAutenticado) {
                            textoComentario = it
                        } else {
                            onLoginRequired()
                        }
                    },
                    enabled = true,
                    placeholder = {
                        Text(
                            text = "Escreva um comentário",
                            color = TextPlaceholder,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp, max = 120.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderDefault,
                        disabledBorderColor = BorderDefault,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceCard,
                        disabledContainerColor = SurfaceCard
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
                            if (usuarioAutenticado && textoComentario.isNotBlank() && !viewModel.isEnviando)
                                BrandPrimary
                            else
                                BrandPrimaryDisabled
                        )
                        .clickable(
                            enabled = !viewModel.isEnviando
                        ) {
                            if (!usuarioAutenticado) {
                                onLoginRequired()
                                return@clickable
                            }

                            if (textoComentario.isBlank()) {
                                return@clickable
                            }

                            viewModel.enviarComentario(
                                autor = nomeUsuario,
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
                if (selecionada) BrandPrimaryLight else SurfaceCard
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = if (selecionada) BrandPrimary else TextSecondary,
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
                    .background(BrandPrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comentario.autor.firstOrNull()?.uppercase() ?: "?",
                    color = BrandPrimary,
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
                    tint = if (jaCurtiu) BrandPrimary else TextSecondary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${comentario.curtidas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (jaCurtiu) BrandPrimary else TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(color = BorderDefault, thickness = 0.5.dp)
    }
}
