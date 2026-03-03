package br.com.mauricio.oconcurseiro.ui.screens.filtro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.data.model.CatalogoItem
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.theme.*
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel

@Composable
fun FiltroScreen(
    viewModel: QuestaoViewModel,
    onBack: () -> Unit,
    onAplicarFiltro: (FiltroParams) -> Unit
) {
    val filtroAtual = viewModel.filtroAtual
    val catalogosCarregando = viewModel.catalogosCarregando

    var tab by remember { mutableIntStateOf(0) }
    var keyword by remember { mutableStateOf(filtroAtual.texto ?: "") }

    var disciplinaSelecionada by remember { mutableStateOf<CatalogoItem?>(null) }
    var assuntoSelecionado by remember { mutableStateOf<CatalogoItem?>(null) }
    var bancaSelecionada by remember { mutableStateOf<CatalogoItem?>(null) }
    var instituicaoSelecionada by remember { mutableStateOf<CatalogoItem?>(null) }

    var cargo by remember { mutableStateOf(filtroAtual.cargo ?: "") }
    var nivel by remember { mutableStateOf(filtroAtual.nivel ?: "") }
    var modalidade by remember { mutableStateOf(filtroAtual.modalidade ?: "") }
    var anoSelecionado by remember { mutableStateOf(filtroAtual.ano) }
    val scroll = rememberScrollState()

    var disciplinaRestaurada by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.disciplinas) {
        if (!disciplinaRestaurada && viewModel.disciplinas.isNotEmpty() && filtroAtual.disciplinaId != null) {
            disciplinaSelecionada = viewModel.disciplinas.find { it.id == filtroAtual.disciplinaId }
            disciplinaRestaurada = true
        }
    }

    LaunchedEffect(viewModel.bancas) {
        if (viewModel.bancas.isNotEmpty() && filtroAtual.bancaId != null && bancaSelecionada == null) {
            bancaSelecionada = viewModel.bancas.find { it.id == filtroAtual.bancaId }
        }
    }

    LaunchedEffect(viewModel.instituicoes) {
        if (viewModel.instituicoes.isNotEmpty() && filtroAtual.instituicaoId != null && instituicaoSelecionada == null) {
            instituicaoSelecionada = viewModel.instituicoes.find { it.id == filtroAtual.instituicaoId }
        }
    }

    LaunchedEffect(viewModel.assuntos) {
        if (viewModel.assuntos.isNotEmpty() && filtroAtual.assuntoId != null && assuntoSelecionado == null) {
            assuntoSelecionado = viewModel.assuntos.find { it.id == filtroAtual.assuntoId }
        }
    }

    LaunchedEffect(disciplinaSelecionada) {
        val id = disciplinaSelecionada?.id
        if (id != null) {
            viewModel.carregarAssuntosPorDisciplina(id)
        } else {
            viewModel.limparAssuntos()
        }
        if (disciplinaRestaurada) {
            assuntoSelecionado = null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        AppHeader(
            title = "Novo filtro",
            subtitle = "Escolher filtros",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {

            Text(
                text = "Escolher filtros",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceCard)
                    .padding(4.dp)
            ) {
                val bgSimples = if (tab == 0) BrandOrangeLight else Color.Transparent
                val fgSimples = if (tab == 0) BrandOrange else TextSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgSimples)
                        .clickable { tab = 0 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Simples", color = fgSimples, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.width(8.dp))

                val bgAvancado = if (tab == 1) BrandOrangeLight else Color.Transparent
                val fgAvancado = if (tab == 1) BrandOrange else TextSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgAvancado)
                        .clickable { tab = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Avançado", color = fgAvancado, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (tab == 0) {
                CampoTexto(
                    valor = keyword,
                    placeholder = "Palavra-chave",
                    onValueChange = { keyword = it }
                )

                Spacer(Modifier.height(14.dp))

                DropdownSelector(
                    label = "Disciplina",
                    itens = viewModel.disciplinas,
                    selecionado = disciplinaSelecionada,
                    onSelecionar = {
                        disciplinaRestaurada = true
                        disciplinaSelecionada = it
                    },
                    carregando = catalogosCarregando
                )

                Spacer(Modifier.height(14.dp))

                DropdownSelector(
                    label = "Assunto",
                    itens = viewModel.assuntos,
                    selecionado = assuntoSelecionado,
                    onSelecionar = { assuntoSelecionado = it },
                    enabled = disciplinaSelecionada != null,
                    placeholder = if (disciplinaSelecionada == null) "Selecione a disciplina primeiro" else "Selecione o assunto"
                )

                Spacer(Modifier.height(14.dp))

                DropdownSelector(
                    label = "Banca",
                    itens = viewModel.bancas,
                    selecionado = bancaSelecionada,
                    onSelecionar = { bancaSelecionada = it },
                    carregando = catalogosCarregando
                )

                Spacer(Modifier.height(14.dp))

                DropdownSelector(
                    label = "Instituição / Órgão",
                    itens = viewModel.instituicoes,
                    selecionado = instituicaoSelecionada,
                    onSelecionar = { instituicaoSelecionada = it },
                    carregando = catalogosCarregando
                )

                Spacer(Modifier.height(20.dp))

                Text("Anos", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChipAno("2026", selecionado = anoSelecionado == 2026) { anoSelecionado = if (anoSelecionado == 2026) null else 2026 }
                    ChipAno("2025", selecionado = anoSelecionado == 2025) { anoSelecionado = if (anoSelecionado == 2025) null else 2025 }
                    ChipAno("2024", selecionado = anoSelecionado == 2024) { anoSelecionado = if (anoSelecionado == 2024) null else 2024 }
                    ChipAno("2023", selecionado = anoSelecionado == 2023) { anoSelecionado = if (anoSelecionado == 2023) null else 2023 }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChipAno("2022", selecionado = anoSelecionado == 2022) { anoSelecionado = if (anoSelecionado == 2022) null else 2022 }
                    ChipAno("2021", selecionado = anoSelecionado == 2021) { anoSelecionado = if (anoSelecionado == 2021) null else 2021 }
                    ChipAno("2020", selecionado = anoSelecionado == 2020) { anoSelecionado = if (anoSelecionado == 2020) null else 2020 }
                }
            }
        }

        Surface(shadowElevation = 8.dp, color = SurfaceBackground) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .padding(
                        bottom = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                    ),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        keyword = ""
                        disciplinaSelecionada = null
                        assuntoSelecionado = null
                        bancaSelecionada = null
                        instituicaoSelecionada = null
                        cargo = ""
                        nivel = ""
                        modalidade = ""
                        anoSelecionado = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Text("Limpar filtros", color = TextSecondary, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        onAplicarFiltro(
                            FiltroParams(
                                texto = keyword.takeIf { it.isNotBlank() },
                                disciplinaId = disciplinaSelecionada?.id,
                                disciplina = disciplinaSelecionada?.nome,
                                assuntoId = assuntoSelecionado?.id,
                                assunto = assuntoSelecionado?.nome,
                                bancaId = bancaSelecionada?.id,
                                banca = bancaSelecionada?.nome,
                                instituicaoId = instituicaoSelecionada?.id,
                                instituicao = instituicaoSelecionada?.nome,
                                ano = anoSelecionado,
                                cargo = cargo.takeIf { it.isNotBlank() },
                                nivel = nivel.takeIf { it.isNotBlank() },
                                modalidade = modalidade.takeIf { it.isNotBlank() }
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Text("Filtrar", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    itens: List<CatalogoItem>,
    selecionado: CatalogoItem?,
    onSelecionar: (CatalogoItem?) -> Unit,
    enabled: Boolean = true,
    carregando: Boolean = false,
    placeholder: String = "Selecione $label"
) {
    var expandido by remember { mutableStateOf(false) }
    val isEnabled = enabled && !carregando
    val alpha = if (isEnabled) 1f else 0.5f

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextLabel.copy(alpha = alpha),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        if (selecionado != null) BrandOrange else BorderDefault,
                        RoundedCornerShape(14.dp)
                    )
                    .background(if (selecionado != null) BrandOrangeBackground else SurfaceWhite)
                    .clickable(enabled = isEnabled && itens.isNotEmpty()) { expandido = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayText = when {
                    selecionado != null -> selecionado.nome
                    carregando -> "Carregando..."
                    else -> placeholder
                }

                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selecionado != null) TextPrimary else TextPlaceholder.copy(alpha = alpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (selecionado != null) {
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { onSelecionar(null) }
                            .padding(4.dp)
                    )
                } else {
                    Text(
                        text = "▾",
                        fontSize = 18.sp,
                        color = TextSecondary.copy(alpha = alpha)
                    )
                }
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 300.dp)
            ) {
                if (itens.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Nenhum item disponível",
                                color = TextPlaceholder,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        onClick = { expandido = false }
                    )
                } else {
                    itens.forEach { item ->
                        val isSelected = selecionado?.id == item.id
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item.nome,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) BrandOrange else TextPrimary
                                )
                            },
                            onClick = {
                                onSelecionar(item)
                                expandido = false
                            },
                            modifier = Modifier.background(
                                if (isSelected) BrandOrangeBackground else Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CampoTexto(
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = placeholder,
            style = MaterialTheme.typography.labelMedium,
            color = TextLabel,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BorderDefault, RoundedCornerShape(14.dp))
                .background(SurfaceWhite)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = valor,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (valor.isBlank()) {
                        Text("Digite aqui...", color = TextPlaceholder, style = MaterialTheme.typography.bodyMedium)
                    }
                    inner()
                }
            )
        }
    }
}

@Composable
fun ChipAno(texto: String, selecionado: Boolean, onClick: () -> Unit) {
    val bg = if (selecionado) BrandOrangeLight else SurfaceChip
    val border = if (selecionado) BrandOrange else BorderDefault
    val textColor = if (selecionado) BrandOrange else TextSecondary

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = textColor, style = MaterialTheme.typography.labelSmall)
    }
}

