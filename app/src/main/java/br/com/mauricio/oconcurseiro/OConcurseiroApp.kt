package br.com.mauricio.oconcurseiro

import android.app.Application
import br.com.mauricio.oconcurseiro.installreferrer.InstallReferrerTracker
import br.com.mauricio.oconcurseiro.notification.DailyMissionNotificationScheduler
import br.com.mauricio.oconcurseiro.data.preferences.StudyPlanPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class OConcurseiroApp : Application() {
    @Inject lateinit var installReferrerTracker: InstallReferrerTracker
    @Inject lateinit var studyPlanPreferences: StudyPlanPreferences

    override fun onCreate() {
        super.onCreate()
        studyPlanPreferences.initializeForCurrentInstall()
        DailyMissionNotificationScheduler.schedule(this)
        installReferrerTracker.trackIfNeeded()
    }
}
