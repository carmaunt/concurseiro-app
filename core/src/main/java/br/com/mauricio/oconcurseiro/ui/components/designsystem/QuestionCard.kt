package br.com.mauricio.oconcurseiro.ui.components.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimary
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimaryBackground
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimaryLight
import br.com.mauricio.oconcurseiro.ui.theme.BorderDefault
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceWhite
import br.com.mauricio.oconcurseiro.ui.theme.TextLabel
import br.com.mauricio.oconcurseiro.ui.theme.TextPrimary
import br.com.mauricio.oconcurseiro.ui.theme.TextSecondary

@Composable
fun QuestionCard(
    questionNumber: Int,
    questionText: String,
    bancaNome: String? = null,
    anoConcurso: Int? = null,
    textoAssociado: String? = null,
    textoAssociadoExpandido: Boolean = false,
    onToggleTextoAssociado: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OConcurseiroCard(
        modifier = modifier,
        backgroundColor = SurfaceWhite,
        elevation = 1.dp,
        cornerRadius = 16.dp,
        padding = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandPrimaryBackground)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$questionNumber",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "Questão",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (bancaNome != null || anoConcurso != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (bancaNome != null) {
                            QuestionBadge(text = bancaNome)
                        }
                        if (anoConcurso != null) {
                            QuestionBadge(text = "$anoConcurso")
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = questionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = 26.sp
                )

                if (!textoAssociado.isNullOrBlank() && onToggleTextoAssociado != null) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = BorderDefault, thickness = 0.5.dp)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleTextoAssociado() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Texto associado",
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (textoAssociadoExpandido) Icons.Outlined.ExpandLess
                                          else Icons.Outlined.ExpandMore,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedVisibility(
                        visible = textoAssociadoExpandido,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = textoAssociado,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 22.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandPrimaryBackground)
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(BrandPrimaryLight)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = BrandPrimary,
            fontSize = 11.sp
        )
    }
}
