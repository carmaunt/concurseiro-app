package br.com.mauricio.oconcurseiro.data.analytics

import java.util.UUID
import javax.inject.Inject

class SessionManager @Inject constructor() {
    var sessionId: String = UUID.randomUUID().toString(); private set
    private var startedAt = 0L
    private var screensViewed = 0
    private var questionsAnswered = 0
    var active = false; private set

    fun start(): String {
        sessionId = UUID.randomUUID().toString(); startedAt = System.currentTimeMillis()
        screensViewed = 0; questionsAnswered = 0; active = true; return sessionId
    }
    fun screenViewed() { if (active) screensViewed++ }
    fun questionAnswered() { if (active) questionsAnswered++ }
    fun finish(): Map<String, Any> {
        if (!active) return emptyMap()
        active = false
        return mapOf("duration_seconds" to ((System.currentTimeMillis()-startedAt).coerceAtLeast(0)/1000), "screens_viewed" to screensViewed, "questions_answered" to questionsAnswered)
    }
}
