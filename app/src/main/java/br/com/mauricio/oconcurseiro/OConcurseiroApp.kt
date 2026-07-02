package br.com.mauricio.oconcurseiro

import android.app.Application
import br.com.mauricio.oconcurseiro.notification.DailyMissionNotificationScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OConcurseiroApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DailyMissionNotificationScheduler.schedule(this)
    }
}
