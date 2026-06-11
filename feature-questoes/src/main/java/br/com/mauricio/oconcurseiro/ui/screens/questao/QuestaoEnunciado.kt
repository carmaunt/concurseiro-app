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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.ui.theme.*
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
    val linhas: List<List<String>> = emptyList()
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
private fun TabelaTextoApoio(titulo: String?, tabela: TextoApoioTabela) {
    val horizontalScroll = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth()) {
        TituloTextoApoio(titulo)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScroll)
                .border(1.dp, BorderDefault, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                Row(modifier = Modifier.background(SurfaceChip)) {
                    tabela.colunas.forEach { coluna ->
                        CelulaTabelaTextoApoio(
                            texto = coluna,
                            destaque = true
                        )
                    }
                }

                tabela.linhas.forEach { linha ->
                    Row {
                        tabela.colunas.forEachIndexed { index, _ ->
                            CelulaTabelaTextoApoio(
                                texto = linha.getOrNull(index).orEmpty(),
                                destaque = false
                            )
                        }
                    }
                }
            }
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

@Composable
private fun CelulaTabelaTextoApoio(texto: String, destaque: Boolean) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal,
        color = TextPrimary,
        modifier = Modifier
            .widthIn(min = 128.dp)
            .border(0.5.dp, BorderDefault)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    )
}

private fun parseTabelaTextoApoio(json: String?): TextoApoioTabela? {
    if (json.isNullOrBlank()) return null

    return runCatching {
        val root = JSONObject(json)
        TextoApoioTabela(
            colunas = root.optJSONArray("colunas").toStringList(),
            linhas = root.optJSONArray("linhas").toNestedStringList()
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
