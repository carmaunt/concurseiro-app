package br.com.mauricio.oconcurseiro.data.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class StudyReminder(
    val hour: Int,
    val minute: Int
)

@Singleton
class StudyPlanPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    val dailyGoal: Int
        get() = normalizeGoal(preferences.getInt(KEY_DAILY_GOAL, DEFAULT_DAILY_GOAL))

    val reminder: StudyReminder?
        get() {
            if (!preferences.getBoolean(KEY_REMINDER_ENABLED, false)) return null
            return StudyReminder(
                hour = preferences.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)
                    .coerceIn(0, 23),
                minute = preferences.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE)
                    .coerceIn(0, 59)
            )
        }

    fun initializeForCurrentInstall() {
        if (preferences.contains(KEY_ONBOARDING_COMPLETED)) return

        if (isExistingUpdatedInstall()) {
            preferences.edit()
                .putBoolean(KEY_ONBOARDING_COMPLETED, true)
                .putInt(KEY_DAILY_GOAL, DEFAULT_DAILY_GOAL)
                .putBoolean(KEY_REMINDER_ENABLED, true)
                .putInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)
                .putInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE)
                .apply()
        }
    }

    fun isOnboardingCompleted(): Boolean {
        initializeForCurrentInstall()
        return preferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun savePlan(dailyGoal: Int, reminder: StudyReminder?) {
        preferences.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .putInt(KEY_DAILY_GOAL, normalizeGoal(dailyGoal))
            .putBoolean(KEY_REMINDER_ENABLED, reminder != null)
            .putInt(KEY_REMINDER_HOUR, reminder?.hour ?: DEFAULT_REMINDER_HOUR)
            .putInt(KEY_REMINDER_MINUTE, reminder?.minute ?: DEFAULT_REMINDER_MINUTE)
            .apply()
    }

    fun disableReminder() {
        preferences.edit()
            .putBoolean(KEY_REMINDER_ENABLED, false)
            .apply()
    }

    fun markMissionCompletionIfFirstToday(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
        if (preferences.getString(KEY_LAST_MISSION_COMPLETION, null) == today) {
            return false
        }

        preferences.edit()
            .putString(KEY_LAST_MISSION_COMPLETION, today)
            .apply()
        return true
    }

    private fun isExistingUpdatedInstall(): Boolean {
        return runCatching {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.lastUpdateTime - packageInfo.firstInstallTime > UPDATE_TOLERANCE_MS
        }.getOrDefault(false)
    }

    companion object {
        const val DEFAULT_DAILY_GOAL = 5
        const val DEFAULT_REMINDER_HOUR = 19
        const val DEFAULT_REMINDER_MINUTE = 30
        val ALLOWED_DAILY_GOALS = setOf(5, 10, 20)

        private const val PREFERENCES_NAME = "study_plan_preferences"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed_v1"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
        private const val KEY_LAST_MISSION_COMPLETION = "last_mission_completion"
        private const val UPDATE_TOLERANCE_MS = 60_000L

        fun normalizeGoal(value: Int): Int =
            value.takeIf { it in ALLOWED_DAILY_GOALS } ?: DEFAULT_DAILY_GOAL
    }
}
