package br.com.mauricio.oconcurseiro.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.com.mauricio.oconcurseiro.notification.DailyMissionNotificationWorker

class DailyMissionDebugReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION) return

        Log.d(TAG, "Disparando Worker da missão diária via broadcast debug")
        val request = OneTimeWorkRequestBuilder<DailyMissionNotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }

    companion object {
        private const val TAG = "DailyMissionDebug"
        const val ACTION = "br.com.mauricio.oconcurseiro.DEBUG_DAILY_MISSION"
    }
}
