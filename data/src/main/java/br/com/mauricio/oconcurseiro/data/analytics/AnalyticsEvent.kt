package br.com.mauricio.oconcurseiro.data.analytics

enum class AnalyticsEventName(val wireName: String) {
    APP_OPENED("app_opened"), SESSION_STARTED("session_started"), SESSION_ENDED("session_ended"),
    SCREEN_VIEWED("screen_viewed"), QUESTION_VIEWED("question_viewed"), QUESTION_ANSWERED("question_answered"),
    EXPLANATION_VIEWED("explanation_viewed"), FILTER_APPLIED("filter_applied"), SEARCH_PERFORMED("search_performed"),
    EMPTY_RESULT_VIEWED("empty_result_viewed"), DISCIPLINE_OPENED("discipline_opened"), SUBJECT_OPENED("subject_opened"),
    SUBSUBJECT_OPENED("subsubject_opened"), COMMENT_VIEWED("comment_viewed"), COMMENT_CREATED("comment_created"),
    APP_INSTALL_ATTRIBUTED("app_install_attributed"), INSTALL_REFERRER_UNAVAILABLE("install_referrer_unavailable"),
    ONBOARDING_STARTED("onboarding_started"), GOAL_SELECTED("goal_selected"),
    STUDY_PLAN_CREATED("study_plan_created"), ONBOARDING_COMPLETED("onboarding_completed"),
    NOTIFICATION_PERMISSION_RESULT("notification_permission_result"),
    DAILY_MISSION_STARTED("daily_mission_started"), DAILY_MISSION_COMPLETED("daily_mission_completed"),
    REMINDER_OPENED("reminder_opened"), GUEST_PROGRESS_MIGRATED("guest_progress_migrated"),
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
