package br.com.mauricio.oconcurseiro.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.mauricio.oconcurseiro.data.preferences.StudyPlanPreferences
import br.com.mauricio.oconcurseiro.data.preferences.StudyReminder
import br.com.mauricio.oconcurseiro.ui.components.designsystem.OConcurseiroButton
import br.com.mauricio.oconcurseiro.ui.components.designsystem.OConcurseiroTextButton
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimary
import br.com.mauricio.oconcurseiro.ui.theme.BrandPrimaryBackground
import br.com.mauricio.oconcurseiro.ui.theme.BorderDefault
import br.com.mauricio.oconcurseiro.ui.theme.HeaderBackground
import br.com.mauricio.oconcurseiro.ui.theme.LogoGreen
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceBackground
import br.com.mauricio.oconcurseiro.ui.theme.SurfaceWhite
import br.com.mauricio.oconcurseiro.ui.theme.TextOnBrand
import br.com.mauricio.oconcurseiro.ui.theme.TextPrimary
import br.com.mauricio.oconcurseiro.ui.theme.TextSecondary

private data class ReminderChoice(
    val label: String,
    val value: StudyReminder?
)

@Composable
fun OnboardingScreen(
    onStart: (dailyGoal: Int, reminder: StudyReminder?) -> Unit,
    onSkipReminder: (dailyGoal: Int) -> Unit
) {
    var selectedGoal by remember {
        mutableIntStateOf(StudyPlanPreferences.DEFAULT_DAILY_GOAL)
    }
    var selectedReminder by remember { mutableStateOf<StudyReminder?>(null) }

    val reminders = remember {
        listOf(
            ReminderChoice("12:00", StudyReminder(12, 0)),
            ReminderChoice("19:30", StudyReminder(19, 30)),
            ReminderChoice("21:00", StudyReminder(21, 0))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(HeaderBackground, HeaderBackground.copy(alpha = 0.92f))
                    )
                )
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .padding(horizontal = 24.dp, vertical = 26.dp)
        ) {
            Column {
                Surface(
                    shape = CircleShape,
                    color = LogoGreen.copy(alpha = 0.18f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = LogoGreen,
                        modifier = Modifier.padding(12.dp).size(26.dp)
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Um plano simples para estudar todos os dias",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextOnBrand,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Escolha uma meta possível. Você começa pelas questões agora e ajusta o ritmo depois.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextOnBrand.copy(alpha = 0.82f)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            PlanSection(
                icon = Icons.Outlined.Flag,
                title = "Sua meta diária",
                subtitle = "Comece pequeno o bastante para manter a constância."
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudyPlanPreferences.ALLOWED_DAILY_GOALS.sorted().forEach { goal ->
                        ChoiceCard(
                            label = "$goal",
                            supportingText = when (goal) {
                                5 -> "Leve"
                                10 -> "Foco"
                                else -> "Intenso"
                            },
                            selected = selectedGoal == goal,
                            onClick = { selectedGoal = goal },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            PlanSection(
                icon = Icons.Outlined.Alarm,
                title = "Quer um lembrete?",
                subtitle = "Opcional e no horário que fizer sentido para você."
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reminders.forEach { choice ->
                        ChoiceCard(
                            label = choice.label,
                            supportingText = null,
                            selected = selectedReminder == choice.value,
                            onClick = {
                                selectedReminder =
                                    if (selectedReminder == choice.value) null else choice.value
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (selectedReminder != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "O Android pedirá sua autorização. Você pode desativar quando quiser.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BrandPrimaryBackground,
                border = BorderStroke(1.dp, BorderDefault)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = LogoGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = "Sem cadastro obrigatório para responder sua primeira questão.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            OConcurseiroButton(
                text = "Começar com $selectedGoal questões",
                onClick = { onStart(selectedGoal, selectedReminder) }
            )

            if (selectedReminder != null) {
                OConcurseiroTextButton(
                    text = "Continuar sem lembrete",
                    onClick = { onSkipReminder(selectedGoal) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = TextSecondary
                )
            } else {
                Text(
                    text = "Leva menos de um minuto",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(
            modifier = Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        )
    }
}

@Composable
private fun PlanSection(
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(BrandPrimary.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.size(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ChoiceCard(
    label: String,
    supportingText: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) BrandPrimary.copy(alpha = 0.08f) else SurfaceWhite,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) BrandPrimary else BorderDefault
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) BrandPrimary else TextPrimary,
                fontWeight = FontWeight.Bold
            )
            supportingText?.let {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
