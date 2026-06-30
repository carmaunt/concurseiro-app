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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.domain.model.CatalogoItem
import java.util.Calendar
import java.text.Normalizer
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.theme.*
import br.com.mauricio.oconcurseiro.ui.viewmodel.QuestaoViewModel

private data class AssuntoArvoreItem(
    val key: String,
    val item: CatalogoItem,
    val texto: String,
    val isSubassunto: Boolean,
    val assuntoPaiId: Long? = null
)

private fun alternarAssuntoArvoreItem(
    item: AssuntoArvoreItem,
    selecionados: Set<String>,
    itens: List<AssuntoArvoreItem>
): Set<String> {
    if (item.key in selecionados) return selecionados - item.key

    return if (item.isSubassunto) {
        selecionados - "a:${item.assuntoPaiId}" + item.key
    } else {
        val subassuntosDoAssunto = itens
            .filter { it.assuntoPaiId == item.item.id }
            .map { it.key }
            .toSet()
        selecionados + item.key + subassuntosDoAssunto
    }
}

private fun normalizarBusca(valor: String): String {
    return Normalizer.normalize(valor, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()
        .trim()
}

private fun AssuntoArvoreItem.selecionadoPorAssuntoPai(selecionados: Set<String>): Boolean {
    return isSubassunto && "a:$assuntoPaiId" in selecionados
}

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
    var assuntoKeysSelecionadas by remember { mutableStateOf<Set<String>>(emptySet()) }
    var bancaSelecionada by remember { mutableStateOf<CatalogoItem?>(null) }
    var instituicaoSelecionada by remember { mutableStateOf<CatalogoItem?>(null) }

    var cargo by remember { mutableStateOf(filtroAtual.cargo ?: "") }
    var nivelSelecionado by remember { mutableStateOf(filtroAtual.nivel) }
    var modalidadeSelecionada by remember { mutableStateOf(filtroAtual.modalidade) }
    var anoSelecionado by remember { mutableStateOf(filtroAtual.ano) }
    val scroll = rememberScrollState()

    var disciplinaRestaurada by remember { mutableStateOf(false) }
    var assuntoRestaurado by remember { mutableStateOf(false) }

    val assuntoArvoreItens by remember(viewModel.assuntos, viewModel.subassuntosPorAssunto) {
        derivedStateOf {
            viewModel.assuntos.flatMapIndexed { assuntoIndex, assunto ->
                val numeroAssunto = assuntoIndex + 1
                val assuntoItem = AssuntoArvoreItem(
                    key = "a:${assunto.id}",
                    item = assunto,
                    texto = "$numeroAssunto - ${assunto.nome}",
                    isSubassunto = false
                )
                val subassuntoItens = viewModel.subassuntosPorAssunto[assunto.id]
                    .orEmpty()
                    .mapIndexed { subIndex, subassunto ->
                        AssuntoArvoreItem(
                            key = "s:${subassunto.id}",
                            item = subassunto,
                            texto = "$numeroAssunto.${subIndex + 1} ${subassunto.nome}",
                            isSubassunto = true,
                            assuntoPaiId = assunto.id
                        )
                    }
                listOf(assuntoItem) + subassuntoItens
            }
        }
    }

    val assuntoIdsSelecionados by remember(assuntoKeysSelecionadas) {
        derivedStateOf {
            assuntoKeysSelecionadas.mapNotNull { key ->
                key.removePrefix("a:").toLongOrNull().takeIf { key.startsWith("a:") }
            }
        }
    }

    val subassuntoIdsSelecionados by remember(assuntoKeysSelecionadas, assuntoArvoreItens, assuntoIdsSelecionados) {
        derivedStateOf {
            val assuntoIdsSelecionadosSet = assuntoIdsSelecionados.toSet()
            assuntoKeysSelecionadas.mapNotNull { key ->
                if (!key.startsWith("s:")) {
                    null
                } else {
                    val id = key.removePrefix("s:").toLongOrNull()
                    val item = assuntoArvoreItens.find { it.key == key }
                    id.takeUnless { item?.assuntoPaiId in assuntoIdsSelecionadosSet }
                }
            }
        }
    }

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
        if (!assuntoRestaurado && viewModel.assuntos.isNotEmpty()) {
            val assuntoIdsParaRestaurar = filtroAtual.assuntoIds?.toSet()
                ?: filtroAtual.assuntoId?.let { setOf(it) }
                ?: emptySet()
            val subassuntoIdsParaRestaurar = filtroAtual.subassuntoIds?.toSet()
                ?: filtroAtual.subassuntoId?.let { setOf(it) }
                ?: emptySet()

            if (assuntoIdsParaRestaurar.isNotEmpty() || subassuntoIdsParaRestaurar.isNotEmpty()) {
                assuntoKeysSelecionadas =
                    assuntoIdsParaRestaurar.map { "a:$it" }.toSet() +
                    subassuntoIdsParaRestaurar.map { "s:$it" }.toSet()
                assuntoRestaurado = true
            }
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
            assuntoKeysSelecionadas = emptySet()
            viewModel.limparSubAssuntos()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceWhite)) {

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
                val bgSimples = if (tab == 0) BrandPrimaryLight else Color.Transparent
                val fgSimples = if (tab == 0) BrandPrimary else TextSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgSimples)
                        .clickable { tab = 0 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Simples", color = fgSimples, style = MaterialTheme.typography.labelLarge)
                }

                Spacer(Modifier.width(8.dp))

                val bgAvancado = if (tab == 1) BrandPrimaryLight else Color.Transparent
                val fgAvancado = if (tab == 1) BrandPrimary else TextSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgAvancado)
                        .clickable { tab = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Avançado", color = fgAvancado, style = MaterialTheme.typography.labelLarge)
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
                        assuntoRestaurado = true
                        disciplinaSelecionada = it
                    },
                    carregando = catalogosCarregando
                )

                Spacer(Modifier.height(14.dp))

                val disciplinaNumero = disciplinaSelecionada?.let { disciplina ->
                    viewModel.disciplinas.indexOfFirst { it.id == disciplina.id }
                        .takeIf { it >= 0 }
                        ?.plus(1)
                }

                DropdownAssuntoArvore(
                    label = disciplinaSelecionada?.let { disciplina ->
                        "Disciplina: ${disciplinaNumero ?: "-"} - ${disciplina.nome}"
                    } ?: "Assunto",
                    itens = assuntoArvoreItens,
                    selecionados = assuntoKeysSelecionadas,
                    onSelecionar = { keys ->
                        assuntoRestaurado = true
                        assuntoKeysSelecionadas = keys
                    },
                    onCarregarSubAssuntos = { assuntoId ->
                        viewModel.carregarSubAssuntosDosAssuntos(listOf(assuntoId))
                    },
                    enabled = disciplinaSelecionada != null,
                    carregando = disciplinaSelecionada != null && viewModel.assuntos.isEmpty(),
                    carregandoSubassuntos = viewModel.subassuntosCarregando,
                    placeholder = if (disciplinaSelecionada == null) {
                        "Selecione a disciplina primeiro"
                    } else {
                        "Selecione assunto(s) e subassunto(s)"
                    }
                )

                Spacer(Modifier.height(14.dp))

                DropdownSelector(
                    label = "Banca",
                    itens = viewModel.bancas,
                    selecionado = bancaSelecionada,
                    onSelecionar = { bancaSelecionada = it },
                    carregando = catalogosCarregando
                )

                Spacer(Modifier.height(20.dp))

                Text("Anos", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Spacer(Modifier.height(12.dp))

                val anoAtual = remember { Calendar.getInstance().get(Calendar.YEAR) }
                val anos = remember { (anoAtual downTo 2018).toList() }
                anos.chunked(4).forEach { linha ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        linha.forEach { ano ->
                            ChipAno(
                                texto = ano.toString(),
                                selecionado = anoSelecionado == ano
                            ) {
                                anoSelecionado = if (anoSelecionado == ano) null else ano
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }

            if (tab == 1) {
                DropdownSelector(
                    label = "Instituição / Órgão",
                    itens = viewModel.instituicoes,
                    selecionado = instituicaoSelecionada,
                    onSelecionar = { instituicaoSelecionada = it },
                    carregando = catalogosCarregando
                )

                Spacer(Modifier.height(20.dp))

                Text("Modalidade", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChipOpcao("Múltipla Escolha", selecionado = modalidadeSelecionada == "MULTIPLA_ESCOLHA") {
                        modalidadeSelecionada = if (modalidadeSelecionada == "MULTIPLA_ESCOLHA") null else "MULTIPLA_ESCOLHA"
                    }
                    ChipOpcao("Certo/Errado", selecionado = modalidadeSelecionada == "CERTO_ERRADO") {
                        modalidadeSelecionada = if (modalidadeSelecionada == "CERTO_ERRADO") null else "CERTO_ERRADO"
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Nível", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChipOpcao("Superior", selecionado = nivelSelecionado == "SUPERIOR") {
                        nivelSelecionado = if (nivelSelecionado == "SUPERIOR") null else "SUPERIOR"
                    }
                    ChipOpcao("Médio", selecionado = nivelSelecionado == "MEDIO") {
                        nivelSelecionado = if (nivelSelecionado == "MEDIO") null else "MEDIO"
                    }
                    ChipOpcao("Fundamental", selecionado = nivelSelecionado == "FUNDAMENTAL") {
                        nivelSelecionado = if (nivelSelecionado == "FUNDAMENTAL") null else "FUNDAMENTAL"
                    }
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
                        assuntoKeysSelecionadas = emptySet()
                        bancaSelecionada = null
                        instituicaoSelecionada = null
                        cargo = ""
                        nivelSelecionado = null
                        modalidadeSelecionada = null
                        anoSelecionado = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Text("Limpar filtros", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
                }

                Button(
                    onClick = {
                        onAplicarFiltro(
                            FiltroParams(
                                texto = keyword.takeIf { it.isNotBlank() },
                                disciplinaId = disciplinaSelecionada?.id,
                                disciplina = disciplinaSelecionada?.nome,
                                assuntoId = if (assuntoIdsSelecionados.size == 1 && subassuntoIdsSelecionados.isEmpty()) assuntoIdsSelecionados.first() else null,
                                assunto = null,
                                assuntoIds = if (assuntoIdsSelecionados.size > 1 || subassuntoIdsSelecionados.isNotEmpty()) assuntoIdsSelecionados else null,
                                subassuntoId = if (subassuntoIdsSelecionados.size == 1 && assuntoIdsSelecionados.isEmpty()) subassuntoIdsSelecionados.first() else null,
                                subassunto = null,
                                subassuntoIds = if (subassuntoIdsSelecionados.size > 1 || assuntoIdsSelecionados.isNotEmpty()) subassuntoIdsSelecionados else null,
                                bancaId = bancaSelecionada?.id,
                                banca = bancaSelecionada?.nome,
                                instituicaoId = instituicaoSelecionada?.id,
                                instituicao = instituicaoSelecionada?.nome,
                                ano = anoSelecionado,
                                cargo = cargo.takeIf { it.isNotBlank() },
                                nivel = nivelSelecionado,
                                modalidade = modalidadeSelecionada
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Filtrar", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun DropdownAssuntoArvore(
    label: String,
    itens: List<AssuntoArvoreItem>,
    selecionados: Set<String>,
    onSelecionar: (Set<String>) -> Unit,
    onCarregarSubAssuntos: (Long) -> Unit,
    enabled: Boolean = true,
    carregando: Boolean = false,
    carregandoSubassuntos: Boolean = false,
    placeholder: String = "Selecione o assunto"
) {
    var expandido by remember { mutableStateOf(false) }
    var busca by remember { mutableStateOf("") }
    var assuntosExpandidos by remember { mutableStateOf<Set<Long>>(emptySet()) }
    val isEnabled = enabled && !carregando
    val alpha = if (isEnabled) 1f else 0.5f
    val listScroll = rememberScrollState()
    val temMaisAbaixo by remember { derivedStateOf { listScroll.canScrollForward } }
    val buscaNormalizada by remember(busca) { derivedStateOf { normalizarBusca(busca) } }
    val itensFiltrados by remember(itens, buscaNormalizada, assuntosExpandidos) {
        derivedStateOf {
            if (buscaNormalizada.isBlank()) {
                itens.filter { item ->
                    !item.isSubassunto || item.assuntoPaiId in assuntosExpandidos
                }
            } else {
                val assuntoIdsComSubassuntoEncontrado = itens
                    .filter { item ->
                        item.isSubassunto && normalizarBusca(item.item.nome).contains(buscaNormalizada)
                    }
                    .mapNotNull { it.assuntoPaiId }
                    .toSet()

                itens.filter { item ->
                    normalizarBusca(item.item.nome).contains(buscaNormalizada) ||
                            (!item.isSubassunto && item.item.id in assuntoIdsComSubassuntoEncontrado)
                }
            }
        }
    }

    val displayText = when {
        carregando -> "Carregando..."
        selecionados.isEmpty() -> placeholder
        selecionados.size == 1 -> itens.find { it.key == selecionados.first() }?.texto ?: "1 selecionado"
        else -> "${selecionados.size} selecionados"
    }

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
                        if (selecionados.isNotEmpty()) BrandPrimary else BorderDefault,
                        RoundedCornerShape(14.dp)
                    )
                    .background(if (selecionados.isNotEmpty()) BrandPrimaryBackground else SurfaceWhite)
                    .clickable(enabled = isEnabled) { expandido = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selecionados.isNotEmpty()) TextPrimary else TextPlaceholder.copy(alpha = alpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (selecionados.isNotEmpty()) {
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { onSelecionar(emptySet()) }
                            .padding(4.dp)
                    )
                } else {
                    Text(
                        text = "▾",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary.copy(alpha = alpha)
                    )
                }
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = {
                    expandido = false
                    busca = ""
                },
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                CampoTexto(
                    valor = busca,
                    placeholder = "Buscar assunto ou subassunto",
                    onValueChange = { busca = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )

                if (carregandoSubassuntos) {
                    Text(
                        text = "Carregando subassuntos...",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

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
                } else if (itensFiltrados.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Nenhum resultado encontrado",
                                color = TextPlaceholder,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        onClick = { }
                    )
                } else {
                    Box(modifier = Modifier.heightIn(max = 340.dp)) {
                        Column(modifier = Modifier.verticalScroll(listScroll)) {
                            itensFiltrados.forEach { item ->
                                val isSelected = item.key in selecionados || item.selecionadoPorAssuntoPai(selecionados)
                                val assuntoExpandido = !item.isSubassunto && item.item.id in assuntosExpandidos

                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(start = if (item.isSubassunto) 18.dp else 0.dp)
                                        ) {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = { checked ->
                                                    val newSet = if (checked) {
                                                        alternarAssuntoArvoreItem(item, selecionados, itens)
                                                    } else {
                                                        if (item.isSubassunto) {
                                                            selecionados - item.key - "a:${item.assuntoPaiId}"
                                                        } else {
                                                            val subassuntosDoAssunto = itens
                                                                .filter { it.assuntoPaiId == item.item.id }
                                                                .map { it.key }
                                                                .toSet()
                                                            selecionados - item.key - subassuntosDoAssunto
                                                        }
                                                    }
                                                    onSelecionar(newSet)
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = BrandPrimary,
                                                    checkmarkColor = TextOnBrand,
                                                    uncheckedColor = TextSecondary
                                                ),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.width(10.dp))
                                            Text(
                                                text = item.texto,
                                                style = if (isSelected) MaterialTheme.typography.labelMedium
                                                else MaterialTheme.typography.bodySmall,
                                                color = if (isSelected) BrandPrimary else TextPrimary,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (!item.isSubassunto) {
                                                Text(
                                                    text = if (assuntoExpandido) "−" else "+",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = TextSecondary,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(999.dp))
                                                        .clickable {
                                                            val assuntoId = item.item.id
                                                            assuntosExpandidos = if (assuntoExpandido) {
                                                                assuntosExpandidos - assuntoId
                                                            } else {
                                                                onCarregarSubAssuntos(assuntoId)
                                                                assuntosExpandidos + assuntoId
                                                            }
                                                        }
                                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        val newSet = if (isSelected) {
                                            if (item.isSubassunto) {
                                                selecionados - item.key - "a:${item.assuntoPaiId}"
                                            } else {
                                                val subassuntosDoAssunto = itens
                                                    .filter { it.assuntoPaiId == item.item.id }
                                                    .map { it.key }
                                                    .toSet()
                                                selecionados - item.key - subassuntosDoAssunto
                                            }
                                        } else {
                                            alternarAssuntoArvoreItem(item, selecionados, itens)
                                        }
                                        onSelecionar(newSet)
                                    },
                                    modifier = Modifier.background(
                                        if (isSelected) BrandPrimaryBackground else Color.Transparent
                                    )
                                )
                            }
                        }

                        if (temMaisAbaixo) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, SurfaceWhite)
                                        )
                                    ),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    text = "▾",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
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
    multiSelecionados: Set<Long> = emptySet(),
    onMultiSelecionar: ((Set<Long>) -> Unit)? = null,
    enabled: Boolean = true,
    carregando: Boolean = false,
    placeholder: String = "Selecione $label"
) {
    val isMultiMode = onMultiSelecionar != null
    var expandido by remember { mutableStateOf(false) }
    val isEnabled = enabled && !carregando
    val alpha = if (isEnabled) 1f else 0.5f
    val listScroll = rememberScrollState()
    val temMaisAbaixo by remember { derivedStateOf { listScroll.canScrollForward } }

    val hasSelection = if (isMultiMode) multiSelecionados.isNotEmpty() else selecionado != null

    val displayText = when {
        isMultiMode -> when {
            multiSelecionados.isEmpty() -> if (carregando) "Carregando..." else placeholder
            multiSelecionados.size == 1 -> itens.find { it.id == multiSelecionados.first() }?.nome ?: placeholder
            else -> "${multiSelecionados.size} selecionados"
        }
        selecionado != null -> selecionado.nome
        carregando -> "Carregando..."
        else -> placeholder
    }

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
                        if (hasSelection) BrandPrimary else BorderDefault,
                        RoundedCornerShape(14.dp)
                    )
                    .background(if (hasSelection) BrandPrimaryBackground else SurfaceWhite)
                    .clickable(enabled = isEnabled) { expandido = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (hasSelection) TextPrimary else TextPlaceholder.copy(alpha = alpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (hasSelection) {
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable {
                                if (isMultiMode) onMultiSelecionar!!(emptySet())
                                else onSelecionar(null)
                            }
                            .padding(4.dp)
                    )
                } else {
                    Text(
                        text = "▾",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary.copy(alpha = alpha)
                    )
                }
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier.fillMaxWidth(0.9f)
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
                    Box(modifier = Modifier.heightIn(max = 300.dp)) {
                        Column(modifier = Modifier.verticalScroll(listScroll)) {
                            itens.forEachIndexed { index, item ->
                                val isSelected = if (isMultiMode)
                                    multiSelecionados.contains(item.id)
                                else
                                    selecionado?.id == item.id

                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isMultiMode) {
                                                Checkbox(
                                                    checked = isSelected,
                                                    onCheckedChange = { checked ->
                                                        val newSet = if (checked)
                                                            multiSelecionados + item.id
                                                        else
                                                            multiSelecionados - item.id
                                                        onMultiSelecionar!!(newSet)
                                                    },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = BrandPrimary,
                                                        checkmarkColor = TextOnBrand,
                                                        uncheckedColor = TextSecondary
                                                    ),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(Modifier.width(10.dp))
                                            }
                                            Text(
                                                text = "${index + 1}. ${item.nome}",
                                                style = if (isSelected) MaterialTheme.typography.labelMedium
                                                else MaterialTheme.typography.bodySmall,
                                                color = if (isSelected) BrandPrimary else TextPrimary
                                            )
                                        }
                                    },
                                    onClick = {
                                        if (isMultiMode) {
                                            val newSet = if (isSelected)
                                                multiSelecionados - item.id
                                            else
                                                multiSelecionados + item.id
                                            onMultiSelecionar!!(newSet)
                                        } else {
                                            onSelecionar(item)
                                            expandido = false
                                        }
                                    },
                                    modifier = Modifier.background(
                                        if (isSelected) BrandPrimaryBackground else Color.Transparent
                                    )
                                )
                            }
                        }

                        if (temMaisAbaixo) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, SurfaceWhite)
                                        )
                                    ),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    text = "▾",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
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
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
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
    ChipOpcao(texto = texto, selecionado = selecionado, onClick = onClick)
}

@Composable
fun ChipOpcao(texto: String, selecionado: Boolean, onClick: () -> Unit) {
    val bg = if (selecionado) BrandPrimaryLight else SurfaceChip
    val border = if (selecionado) BrandPrimary else BorderDefault
    val textColor = if (selecionado) BrandPrimary else TextSecondary

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