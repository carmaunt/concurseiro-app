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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.data.model.FiltroParams
import br.com.mauricio.oconcurseiro.ui.components.AppHeader

@Composable
fun FiltroScreen(onBack: () -> Unit, onAplicarFiltro: (FiltroParams) -> Unit) {

    var tab by remember { mutableIntStateOf(0) } // 0 = Simples, 1 = Avançado
    var keyword by remember { mutableStateOf("") }
    val scroll = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {

        AppHeader(
            title = "Novo filtro",
            subtitle = "Escolher filtros",
            onBack = onBack
        )

        // Corpo rolável
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {

            Text(
                text = "Escolher filtros",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(Modifier.height(12.dp))

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF3F4F6))
                    .padding(4.dp)
            ) {

                val bgSimples = if (tab == 0) Color(0xFFFFE7DD) else Color.Transparent
                val fgSimples = if (tab == 0) Color(0xFFFF6A2A) else Color(0xFF6B7280)

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

                val bgAvancado = if (tab == 1) Color(0xFFFFE7DD) else Color.Transparent
                val fgAvancado = if (tab == 1) Color(0xFFFF6A2A) else Color(0xFF6B7280)

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

            Spacer(Modifier.height(12.dp))

            // Palavra-chave
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔎", fontSize = 18.sp, color = Color(0xFF6B7280))
                Spacer(Modifier.width(10.dp))

                BasicTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF111827)),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (keyword.isBlank()) {
                            Text("Palavra-chave", color = Color(0xFF9CA3AF), fontSize = 16.sp)
                        }
                        inner()
                    }
                )
            }

            Spacer(Modifier.height(14.dp))

            // Linhas
            LinhaFiltro(label = "Disciplinas", count = 0, enabled = true)
            LinhaFiltro(label = "Assuntos", count = 0, enabled = false)
            LinhaFiltro(label = "Bancas", count = 0, enabled = true)
            LinhaFiltro(label = "Cargos", count = 0, enabled = true)

            Spacer(Modifier.height(20.dp))

            Text("Anos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChipAno("2026")
                ChipAno("2025")
                ChipAno("2024")
                ChipAno("2023")
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChipAno("2022")
                ChipAno("2021")
                ChipAno("2020")
            }

            Spacer(Modifier.height(18.dp))

            Text("Apenas questões que", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChipFiltro("Não resolvi")
                ChipFiltro("Resolvi")
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChipFiltro("Acertei")
                ChipFiltro("Errei")
            }

            Spacer(Modifier.height(22.dp))

            Text("Excluir questões", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChipFiltro("Dos meus cadernos")
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChipFiltro("Anuladas")
                ChipFiltro("Desatualizadas")
            }
        }

        // Rodapé fixo
        Surface(shadowElevation = 8.dp, color = Color(0xFFF6F7FB)) {
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
                    onClick = { keyword = "" },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Text("Limpar filtros", color = Color(0xFF6B7280), fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        onAplicarFiltro(
                            FiltroParams(
                                texto = keyword.takeIf { it.isNotBlank() }
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6A2A))
                ) {
                    Text("Filtrar", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ChipAno(texto: String) {
    var selecionado by remember { mutableStateOf(false) }

    val bg = if (selecionado) Color(0xFFFFE7DD) else Color(0xFFF9FAFB)
    val border = if (selecionado) Color(0xFFFF6A2A) else Color(0xFFE5E7EB)
    val textColor = if (selecionado) Color(0xFFFF6A2A) else Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .clickable { selecionado = !selecionado }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = textColor, fontSize = 16.sp)
    }
}

@Composable
fun ChipFiltro(texto: String) {
    var selecionado by remember { mutableStateOf(false) }

    val bg = if (selecionado) Color(0xFFFFE7DD) else Color(0xFFF9FAFB)
    val border = if (selecionado) Color(0xFFFF6A2A) else Color(0xFFE5E7EB)
    val textColor = if (selecionado) Color(0xFFFF6A2A) else Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .clickable { selecionado = !selecionado }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = textColor, fontSize = 16.sp)
    }
}

@Composable
fun LinhaFiltro(label: String, count: Int, enabled: Boolean) {
    val alpha = if (enabled) 1f else 0.35f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = enabled) { }
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 18.sp, color = Color(0xFF111827).copy(alpha = alpha))
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .height(30.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFF3F4F6))
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("$count", color = Color(0xFF6B7280), fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.width(10.dp))
        Text("›", fontSize = 26.sp, color = Color(0xFF6B7280).copy(alpha = alpha))
    }

    Divider(color = Color(0xFFE5E7EB))
}