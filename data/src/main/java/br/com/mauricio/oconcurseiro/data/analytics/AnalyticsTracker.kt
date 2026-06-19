package br.com.mauricio.oconcurseiro.data.analytics

import android.content.Context
import android.util.Log
import br.com.mauricio.oconcurseiro.data.remote.AnalyticsEventRequestDto
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.Questao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ConcurseiroApi
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val preferences = context.getSharedPreferences("analytics_preferences", Context.MODE_PRIVATE)
    private val deviceId: String = preferences.getString(DEVICE_ID_KEY, null)
        ?: UUID.randomUUID().toString().also { preferences.edit().putString(DEVICE_ID_KEY, it).apply() }
    private var sessionId: String = UUID.randomUUID().toString()
    private var sessionStartedAt: Long = 0
    private var heartbeatJob: Job? = null

    private val appVersion: String by lazy {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "unknown"
    }

    fun appOpened() = track("app_open")

    fun startSession() {
        if (heartbeatJob?.isActive == true) return
        sessionId = UUID.randomUUID().toString()
        sessionStartedAt = System.currentTimeMillis()
        track("session_start")
        heartbeatJob = scope.launch {
            while (isActive) {
                trackNow("user_online")
                delay(60_000)
            }
        }
    }

    fun stopSession() {
        heartbeatJob?.cancel()
        heartbeatJob = null
        val duration = (System.currentTimeMillis() - sessionStartedAt).coerceAtLeast(0)
        track("session_end", interactionDurationMs = duration)
    }

    fun screenViewed(screenName: String) = track("screen_view", screenName = screenName)

    fun questionViewed(question: Questao) = track(
        eventName = "question_viewed",
        screenName = "questao",
        questionId = question.id,
        disciplinaId = question.disciplinaId,
        assuntoId = question.assuntoId
    )

    fun questionAnswered(question: Questao, correct: Boolean) = track(
        eventName = "question_answered",
        screenName = "questao",
        questionId = question.id,
        answerCorrect = correct,
        disciplinaId = question.disciplinaId,
        assuntoId = question.assuntoId
    )

    fun filtersApplied(filters: FiltroParams) {
        val names = buildList {
            if (!filters.texto.isNullOrBlank()) add("texto")
            if (filters.disciplinaId != null) add("disciplina")
            if (filters.assuntoId != null || !filters.assuntoIds.isNullOrEmpty()) add("assunto")
            if (filters.subassuntoId != null || !filters.subassuntoIds.isNullOrEmpty()) add("subassunto")
            if (filters.bancaId != null) add("banca")
            if (filters.instituicaoId != null) add("instituicao")
            if (filters.ano != null) add("ano")
            if (!filters.cargo.isNullOrBlank()) add("cargo")
            if (!filters.nivel.isNullOrBlank()) add("nivel")
            if (!filters.modalidade.isNullOrBlank()) add("modalidade")
        }
        (names.ifEmpty { listOf("sem_filtros") }).forEach { name ->
            track(
                eventName = "filter_applied",
                screenName = "filtro",
                filterName = name,
                disciplinaId = filters.disciplinaId,
                assuntoId = filters.assuntoId,
                subassuntoId = filters.subassuntoId
            )
        }
    }

    private fun track(
        eventName: String,
        screenName: String? = null,
        filterName: String? = null,
        questionId: String? = null,
        answerCorrect: Boolean? = null,
        disciplinaId: Long? = null,
        assuntoId: Long? = null,
        subassuntoId: Long? = null,
        interactionDurationMs: Long? = null
    ) {
        scope.launch {
            trackNow(eventName, screenName, filterName, questionId, answerCorrect, disciplinaId, assuntoId, subassuntoId, interactionDurationMs)
        }
    }

    private suspend fun trackNow(
        eventName: String,
        screenName: String? = null,
        filterName: String? = null,
        questionId: String? = null,
        answerCorrect: Boolean? = null,
        disciplinaId: Long? = null,
        assuntoId: Long? = null,
        subassuntoId: Long? = null,
        interactionDurationMs: Long? = null
    ) {
        runCatching {
            api.registrarEventoAnalytics(
                AnalyticsEventRequestDto(
                    eventName = eventName,
                    deviceId = deviceId,
                    sessionId = sessionId,
                    screenName = screenName,
                    filterName = filterName,
                    questionId = questionId,
                    answerCorrect = answerCorrect,
                    disciplinaId = disciplinaId,
                    assuntoId = assuntoId,
                    subassuntoId = subassuntoId,
                    interactionDurationMs = interactionDurationMs,
                    appVersion = appVersion
                )
            )
        }.onSuccess {
            Log.d(TAG, "Evento enviado: $eventName")
        }.onFailure {
            Log.w(TAG, "Falha ao enviar evento $eventName: ${it.javaClass.simpleName}")
        }
    }

    private companion object {
        const val TAG = "ConcurseiroAnalytics"
        const val DEVICE_ID_KEY = "anonymous_device_id"
    }
}
