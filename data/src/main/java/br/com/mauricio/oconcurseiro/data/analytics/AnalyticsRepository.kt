package br.com.mauricio.oconcurseiro.data.analytics

import android.content.Context
import br.com.mauricio.oconcurseiro.data.remote.AnalyticsEventRequestDto
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.Questao
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ConcurseiroApi
) {
    private val prefs by lazy {
        context.getSharedPreferences("concurseiro_analytics", Context.MODE_PRIVATE)
    }

    private val deviceId: String by lazy {
        prefs.getString(KEY_DEVICE_ID, null) ?: UUID.randomUUID().toString().also { generated ->
            prefs.edit().putString(KEY_DEVICE_ID, generated).apply()
        }
    }

    private val sessionId: String = UUID.randomUUID().toString()

    suspend fun trackAppOpened() {
        send(AnalyticsEventRequestDto(eventName = "app_opened"))
    }

    suspend fun trackScreenView(screenName: String) {
        send(
            AnalyticsEventRequestDto(
                eventName = "screen_view",
                screenName = screenName
            )
        )
    }

    suspend fun trackQuestionAnswered(
        questao: Questao,
        acertou: Boolean
    ) {
        send(
            AnalyticsEventRequestDto(
                eventName = "question_answered",
                screenName = "questao",
                questionId = questao.id,
                acertou = acertou,
                disciplinaId = questao.disciplinaId,
                disciplinaNome = questao.disciplina,
                assuntoId = questao.assuntoId,
                assuntoNome = questao.assunto
            )
        )
    }

    suspend fun trackFilterApplied(filtro: FiltroParams) {
        val filterNames = buildList {
            if (!filtro.texto.isNullOrBlank()) add("texto")
            if (filtro.disciplinaId != null || !filtro.disciplina.isNullOrBlank()) add("disciplina")
            if (filtro.assuntoId != null || !filtro.assunto.isNullOrBlank() || !filtro.assuntoIds.isNullOrEmpty()) add("assunto")
            if (filtro.subassuntoId != null || !filtro.subassunto.isNullOrBlank() || !filtro.subassuntoIds.isNullOrEmpty()) add("subassunto")
            if (filtro.bancaId != null || !filtro.banca.isNullOrBlank()) add("banca")
            if (filtro.instituicaoId != null || !filtro.instituicao.isNullOrBlank()) add("instituicao")
            if (filtro.ano != null) add("ano")
            if (!filtro.cargo.isNullOrBlank()) add("cargo")
            if (!filtro.nivel.isNullOrBlank()) add("nivel")
            if (!filtro.modalidade.isNullOrBlank()) add("modalidade")
        }

        filterNames.forEach { filterName ->
            send(
                AnalyticsEventRequestDto(
                    eventName = "filter_applied",
                    screenName = "questoes",
                    filterName = filterName,
                    disciplinaId = filtro.disciplinaId,
                    disciplinaNome = filtro.disciplina,
                    assuntoId = filtro.assuntoId,
                    assuntoNome = filtro.assunto,
                    subassuntoId = filtro.subassuntoId,
                    subassuntoNome = filtro.subassunto
                )
            )
        }
    }

    private suspend fun send(event: AnalyticsEventRequestDto) {
        runCatching {
            api.registrarAnalyticsEvent(
                event.copy(
                    deviceId = deviceId,
                    sessionId = sessionId,
                    platform = "android"
                )
            )
        }
    }

    private companion object {
        const val KEY_DEVICE_ID = "device_id"
    }
}
