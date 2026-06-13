package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.ui.theme.*
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun QuestaoEnunciado(questao: Questao) {

    val textoAssociado = questao.textoAssociado
    var textoAssociadoAberto by remember(questao.id, textoAssociado) { mutableStateOf(false) }

    Column {

        Text(
            text = "Ano: ${questao.ano}  Banca: ${questao.banca}\nÓrgão: ${questao.orgao}  Cargo: ${questao.cargo}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        if (textoAssociado.isNotBlank()) {
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, RoundedCornerShape(14.dp))
                    .clickable { textoAssociadoAberto = !textoAssociadoAberto }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = questao.textoApoioTitulo?.takeIf { it.isNotBlank() } ?: "Texto associado",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (textoAssociadoAberto) "−" else "+",
                    color = TextSecondary
                )
            }

            AnimatedVisibility(
                visible = textoAssociadoAberto,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))

                    TextoApoioRenderer(
                        titulo = null,
                        conteudo = questao.textoApoioConteudo,
                        tipo = questao.textoApoioTipo,
                        conteudoJson = questao.textoApoioJson
                    )

                    if (questao.enunciado.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        MarkdownCompatText(
                            text = questao.enunciado,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }

                    Spacer(Modifier.height(18.dp))
                }
            }

            Spacer(Modifier.height(if (textoAssociadoAberto) 0.dp else 10.dp))
        }

        if (questao.questao.isNotBlank()) {
            Spacer(Modifier.height(14.dp))
            MarkdownCompatText(
                text = questao.questao,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

private data class TextoApoioTabela(
    val colunas: List<String> = emptyList(),
    val linhas: List<List<String>> = emptyList(),
    val rodape: String = ""
)

private data class TextoApoioImagem(
    val url: String,
    val alt: String,
    val largura: Int?,
    val altura: Int?
)

@Composable
fun TextoApoioRenderer(
    titulo: String?,
    conteudo: String?,
    tipo: String?,
    conteudoJson: String?
) {
    val tipoNormalizado = tipo?.uppercase() ?: "TEXTO"
    val textoFallback = conteudo.orEmpty()

    when (tipoNormalizado) {
        "TABELA" -> {
            val tabela = remember(conteudoJson) { parseTabelaTextoApoio(conteudoJson) }
            if (tabela != null && tabela.colunas.isNotEmpty()) {
                TabelaTextoApoio(titulo = titulo, tabela = tabela)
            } else if (textoFallback.isNotBlank()) {
                TextoApoioSimples(titulo = titulo, conteudo = textoFallback)
            }
        }

        "CODIGO" -> {
            if (textoFallback.isNotBlank()) {
                BlocoCodigoTextoApoio(titulo = titulo, conteudo = textoFallback)
            }
        }

        "IMAGEM" -> {
            val imagem = remember(conteudoJson) { parseImagemTextoApoio(conteudoJson) }
            if (imagem != null) {
                ImagemTextoApoio(
                    titulo = titulo,
                    imagem = imagem,
                    textoFallback = textoFallback
                )
            } else if (textoFallback.isNotBlank()) {
                TextoApoioSimples(titulo = titulo, conteudo = textoFallback)
            }
        }

        else -> {
            if (textoFallback.isNotBlank()) {
                TextoApoioSimples(titulo = titulo, conteudo = textoFallback)
            }
        }
    }
}

@Composable
private fun TextoApoioSimples(titulo: String?, conteudo: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TituloTextoApoio(titulo)
        MarkdownCompatText(
            text = conteudo,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun BlocoCodigoTextoApoio(titulo: String?, conteudo: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TituloTextoApoio(titulo)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SurfaceChip,
            border = BorderStroke(1.dp, BorderDefault),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = conteudo,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun ImagemTextoApoio(
    titulo: String?,
    imagem: TextoApoioImagem,
    textoFallback: String
) {
    var carregando by remember(imagem.url) { mutableStateOf(true) }
    var falhou by remember(imagem.url) { mutableStateOf(false) }
    val contexto = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        TituloTextoApoio(titulo)

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val proporcao = calcularProporcaoImagem(imagem)
            val alturaImagem = (maxWidth / proporcao).coerceIn(120.dp, 560.dp)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaImagem)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceChip)
                    .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(contexto)
                        .data(imagem.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = imagem.alt.ifBlank { textoFallback },
                    contentScale = ContentScale.Fit,
                    onLoading = {
                        carregando = true
                        falhou = false
                    },
                    onSuccess = {
                        carregando = false
                        falhou = false
                    },
                    onError = {
                        carregando = false
                        falhou = true
                    },
                    modifier = Modifier.matchParentSize()
                )

                if (carregando) {
                    CircularProgressIndicator(
                        color = TextSecondary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp)
                    )
                }

                if (falhou) {
                    Text(
                        text = imagem.alt.ifBlank {
                            textoFallback.ifBlank { "Não foi possível carregar a imagem." }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

private fun calcularProporcaoImagem(imagem: TextoApoioImagem): Float {
    val largura = imagem.largura ?: return 4f / 3f
    val altura = imagem.altura ?: return 4f / 3f
    if (largura <= 0 || altura <= 0) return 4f / 3f

    return (largura.toFloat() / altura.toFloat()).coerceIn(0.45f, 3.5f)
}

@Composable
private fun TabelaTextoApoio(titulo: String?, tabela: TextoApoioTabela) {
    val horizontalScroll = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth()) {
        TituloTextoApoio(titulo)

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val largurasColunas = remember(tabela, maxWidth) {
                calcularLargurasColunas(tabela, maxWidth)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll)
            ) {
                Column(
                    modifier = Modifier.border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .background(SurfaceChip)
                    ) {
                        tabela.colunas.forEachIndexed { index, coluna ->
                            CelulaTabelaTextoApoio(
                                texto = coluna,
                                destaque = true,
                                largura = largurasColunas[index]
                            )
                        }
                    }

                    tabela.linhas.forEach { linha ->
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            tabela.colunas.forEachIndexed { index, _ ->
                                CelulaTabelaTextoApoio(
                                    texto = linha.getOrNull(index).orEmpty(),
                                    destaque = false,
                                    largura = largurasColunas[index]
                                )
                            }
                        }
                    }
                }
            }
        }

        if (tabela.rodape.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            MarkdownCompatText(
                text = tabela.rodape,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun TituloTextoApoio(titulo: String?) {
    if (!titulo.isNullOrBlank()) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

private fun calcularLargurasColunas(
    tabela: TextoApoioTabela,
    larguraDisponivel: Dp
): List<Dp> {
    if (tabela.colunas.isEmpty()) return emptyList()

    val larguraMinima = 56.dp
    val largurasDesejadas = tabela.colunas.mapIndexed { index, coluna ->
        val maiorTexto = sequenceOf(coluna)
            .plus(tabela.linhas.asSequence().map { linha -> linha.getOrNull(index).orEmpty() })
            .maxOf { it.length }

        (maiorTexto * 7 + 16).dp.coerceIn(larguraMinima, 180.dp)
    }
    val totalDesejado = largurasDesejadas.fold(0.dp, Dp::plus)

    if (totalDesejado <= larguraDisponivel) {
        val espacoExtra = (larguraDisponivel - totalDesejado) / tabela.colunas.size
        return largurasDesejadas.map { it + espacoExtra }
    }

    val totalMinimo = larguraMinima * tabela.colunas.size
    if (totalMinimo >= larguraDisponivel) {
        return List(tabela.colunas.size) { larguraMinima }
    }

    val reducaoNecessaria = totalDesejado - larguraDisponivel
    val espacoRedutivel = totalDesejado - totalMinimo

    return largurasDesejadas.map { largura ->
        val participacao = (largura - larguraMinima) / espacoRedutivel
        largura - reducaoNecessaria * participacao
    }
}

@Composable
private fun CelulaTabelaTextoApoio(texto: String, destaque: Boolean, largura: Dp) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal,
        color = TextPrimary,
        modifier = Modifier
            .width(largura)
            .fillMaxHeight()
            .border(0.5.dp, BorderDefault)
            .padding(horizontal = 6.dp, vertical = 6.dp)
    )
}

private fun parseTabelaTextoApoio(json: String?): TextoApoioTabela? {
    if (json.isNullOrBlank()) return null

    return runCatching {
        val root = JSONObject(json)
        TextoApoioTabela(
            colunas = root.optJSONArray("colunas").toStringList(),
            linhas = root.optJSONArray("linhas").toNestedStringList(),
            rodape = root.optString("rodape")
        )
    }.getOrNull()
}

private fun parseImagemTextoApoio(json: String?): TextoApoioImagem? {
    if (json.isNullOrBlank()) return null

    return runCatching {
        val root = JSONObject(json)
        val url = root.optString("url").trim()
        require(url.isNotBlank())

        TextoApoioImagem(
            url = url,
            alt = root.optString("alt").trim(),
            largura = root.optInt("largura").takeIf { it > 0 },
            altura = root.optInt("altura").takeIf { it > 0 }
        )
    }.getOrNull()
}

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return List(length()) { index -> optString(index) }
}

private fun JSONArray?.toNestedStringList(): List<List<String>> {
    if (this == null) return emptyList()
    return List(length()) { index ->
        optJSONArray(index).toStringList()
    }
}
