package br.com.mauricio.oconcurseiro.ui.screens.aviso

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.ui.components.AppHeader
import br.com.mauricio.oconcurseiro.ui.theme.*

@Composable
fun AvisoLegalScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        AppHeader(
            title = "Aviso legal",
            subtitle = "Fontes oficiais e isenção de responsabilidade",
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
            DisclaimerCard {
                DisclaimerParagraph(
                    text = "O Concurseiro é um aplicativo independente de estudos para concursos públicos. " +
                        "Não representamos, não somos afiliados, não somos endossados e não possuímos vínculo com " +
                        "qualquer órgão público, entidade governamental, banca organizadora ou serviço governamental."
                )

                Spacer(Modifier.height(12.dp))

                DisclaimerParagraph(
                    text = "O aplicativo tem finalidade educacional e serve para prática de questões, revisão de " +
                        "conteúdos e acompanhamento de desempenho. O Concurseiro não oferece serviços governamentais, " +
                        "não realiza inscrições em concursos, não publica editais oficiais e não substitui informações " +
                        "divulgadas pelos órgãos responsáveis."
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceWhite,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Fontes oficiais",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "As informações oficiais sobre concursos públicos — editais, inscrições, cargos, datas, " +
                            "resultados, requisitos e regras de participação — devem sempre ser confirmadas diretamente " +
                            "nas fontes oficiais de cada concurso:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(14.dp))

                    FonteOficialItem(
                        label = "Site oficial do órgão público responsável pelo concurso"
                    )
                    FonteOficialItem(
                        label = "Site oficial da banca organizadora"
                    )
                    FonteOficialItem(
                        label = "Diário Oficial correspondente"
                    )

                    Spacer(Modifier.height(8.dp))

                    LinkOficialButton(
                        label = "Portal Gov.br",
                        url = "https://www.gov.br",
                        onClick = { uriHandler.openUri("https://www.gov.br") }
                    )

                    Spacer(Modifier.height(8.dp))

                    LinkOficialButton(
                        label = "Diário Oficial da União",
                        url = "https://www.in.gov.br",
                        onClick = { uriHandler.openUri("https://www.in.gov.br") }
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "App independente. Consulte sempre o edital, o órgão responsável e a banca organizadora para informações oficiais.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DisclaimerCard(content: @Composable ColumnScope.() -> Unit) {
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
private fun DisclaimerParagraph(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        lineHeight = MaterialTheme.typography.bodySmall.lineHeight
    )
}

@Composable
private fun FonteOficialItem(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            color = BrandPrimary,
            modifier = Modifier.padding(end = 8.dp, top = 1.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun LinkOficialButton(
    label: String,
    url: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = BrandPrimaryBackground,
        contentColor = BrandPrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPrimary
                )
                Text(
                    text = url,
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandPrimary.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Outlined.OpenInNew,
                contentDescription = "Abrir link",
                modifier = Modifier.size(18.dp),
                tint = BrandPrimary
            )
        }
    }
}
