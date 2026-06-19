package br.com.mauricio.oconcurseiro.ui.screens.questao

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.domain.model.Questao
import br.com.mauricio.oconcurseiro.ui.viewmodel.RespostaAnterior

@Composable
fun ColumnScope.QuestaoContent(
    questao: Questao,
    usuarioAutenticado: Boolean,
    respostaAnterior: RespostaAnterior?,
    numeroAtual: Int,
    totalQuestoes: Int,
    paginaAtual: Int,
    onAbrirComentarios: (String) -> Unit,
    onResponder: (String, Boolean) -> Unit,
    onResolvidaComSucesso: () -> Unit,
    onAnterior: () -> Unit,
    onProximo: () -> Unit,
    onPodeResolverQuestao: (String) -> Boolean
) {
    var tentouResolver by remember(questao.id) { mutableStateOf(false) }
    var mostrarExplicacao by remember(questao.id) { mutableStateOf(false) }
    val explicacao = questao.explicacao?.trim().orEmpty()
    val conteudoExplicacao = textoExplicacaoOuIndisponivel(questao.explicacao)

    TopoResumoQuestao(
        questaoNumero = numeroAtual,
        questoesTotal = totalQuestoes,
        explicacaoDisponivel = explicacao.isNotBlank(),
        explicacaoDesbloqueada = tentouResolver,
        usuarioAutenticado = usuarioAutenticado,
        onAbrirComentarios = { onAbrirComentarios(questao.id) },
        onAbrirExplicacao = {
            if (usuarioAutenticado && tentouResolver) {
                mostrarExplicacao = true
            }
        }
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
    ) {
        CorpoQuestao(
            questao = questao,
            respostaAnterior = respostaAnterior,
            onPodeResolverQuestao = onPodeResolverQuestao,
            onResolver = { respostaSelecionada, acertou ->
                onResponder(respostaSelecionada, acertou)
            },
            onResolvidaComSucesso = onResolvidaComSucesso,
            onTentouResolver = { tentouResolver = true }
        )
    }

    RodapeQuestao(
        podeAnterior = paginaAtual > 0,
        podeProximo = paginaAtual < (totalQuestoes - 1),
        onAnterior = onAnterior,
        onProximo = onProximo
    )

    if (mostrarExplicacao) {
        ExplicacaoBottomSheet(
            explicacao = conteudoExplicacao,
            onDismiss = { mostrarExplicacao = false }
        )
    }
}

internal fun textoExplicacaoOuIndisponivel(explicacao: String?): String =
    explicacao?.trim()?.takeIf { it.isNotBlank() }
        ?: "A explicação desta questão ainda não foi cadastrada."

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExplicacaoBottomSheet(
    explicacao: String,
    onDismiss: () -> Unit
) {
    val alturaConteudo = (LocalConfiguration.current.screenHeightDp.dp * 0.8f) - 48.dp
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(alturaConteudo)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explicação",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDismiss) { Text("Fechar") }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = explicacao,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
    }
}
