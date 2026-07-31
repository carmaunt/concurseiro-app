package br.com.mauricio.oconcurseiro.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import br.com.mauricio.oconcurseiro.data.preferences.StudyPlanPreferences
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DailyMissionNotificationScheduler {

    private const val WORK_NAME = "daily_mission_notification"

    fun schedule(
        context: Context,
        replaceExisting: Boolean = false
    ) {
        val preferences = StudyPlanPreferences(context.applicationContext)
        preferences.initializeForCurrentInstall()
        val reminder = preferences.reminder

        if (reminder == null) {
            cancel(context)
            return
        }

        val request = PeriodicWorkRequestBuilder<DailyMissionNotificationWorker>(
            1,
            TimeUnit.DAYS
        )
            .setInitialDelay(
                delayUntilNextNotification(reminder.hour, reminder.minute),
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            if (replaceExisting) {
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            } else {
                ExistingPeriodicWorkPolicy.KEEP
            },
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun delayUntilNextNotification(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val scheduled = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (!after(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return scheduled.timeInMillis - now.timeInMillis
    }
}
