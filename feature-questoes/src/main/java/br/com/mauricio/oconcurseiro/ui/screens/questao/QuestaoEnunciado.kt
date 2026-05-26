package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.ui.theme.*

@Composable
fun QuestaoEnunciado(questao: Questao) {

    val textoAssociado = questao.textoAssociado
    var enunciadoAberto by remember(questao.id, textoAssociado) { mutableStateOf(textoAssociado.isNotBlank()) }

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
                    .clickable { enunciadoAberto = !enunciadoAberto }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = questao.textoApoioTitulo?.takeIf { it.isNotBlank() } ?: "Texto associado",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (enunciadoAberto) "−" else "+",
                    color = TextSecondary
                )
            }

            AnimatedVisibility(
                visible = enunciadoAberto,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))

                    MarkdownCompatText(
                        text = textoAssociado,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(18.dp))
                }
            }

            if (!enunciadoAberto) {
                Spacer(Modifier.height(10.dp))
            }
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