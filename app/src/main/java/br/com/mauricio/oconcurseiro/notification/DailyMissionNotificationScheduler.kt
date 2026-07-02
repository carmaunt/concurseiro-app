package br.com.mauricio.oconcurseiro.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DailyMissionNotificationScheduler {

    private const val WORK_NAME = "daily_mission_notification"
    private const val NOTIFICATION_HOUR = 19
    private const val NOTIFICATION_MINUTE = 30

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<DailyMissionNotificationWorker>(
            1,
            TimeUnit.DAYS
        )
            .setInitialDelay(delayUntilNextNotification(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun delayUntilNextNotification(): Long {
        val now = Calendar.getInstance()
        val scheduled = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR)
            set(Calendar.MINUTE, NOTIFICATION_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (!after(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return scheduled.timeInMillis - now.timeInMillis
    }
}
