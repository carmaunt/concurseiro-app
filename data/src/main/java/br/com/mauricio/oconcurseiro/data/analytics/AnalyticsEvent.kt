package br.com.mauricio.oconcurseiro.data.analytics

enum class AnalyticsEventName(val wireName: String) {
    APP_OPENED("app_opened"), SESSION_STARTED("session_started"), SESSION_ENDED("session_ended"),
    SCREEN_VIEWED("screen_viewed"), QUESTION_VIEWED("question_viewed"), QUESTION_ANSWERED("question_answered"),
    EXPLANATION_VIEWED("explanation_viewed"), FILTER_APPLIED("filter_applied"), SEARCH_PERFORMED("search_performed"),
    EMPTY_RESULT_VIEWED("empty_result_viewed"), DISCIPLINE_OPENED("discipline_opened"), SUBJECT_OPENED("subject_opened"),
    SUBSUBJECT_OPENED("subsubject_opened"), COMMENT_VIEWED("comment_viewed"), COMMENT_CREATED("comment_created"),
    ERROR_OCCURRED("error_occurred")
}

data class AnalyticsEvent(
    val name: AnalyticsEventName,
    val screenName: String? = null,
    val filterName: String? = null,
    val questionId: String? = null,
    val answerCorrect: Boolean? = null,
    val disciplinaId: Long? = null,
    val assuntoId: Long? = null,
    val subassuntoId: Long? = null,
    val bancaId: Long? = null,
    val instituicaoId: Long? = null,
    val provaId: Long? = null,
    val metadata: Map<String, Any> = emptyMap()
)
