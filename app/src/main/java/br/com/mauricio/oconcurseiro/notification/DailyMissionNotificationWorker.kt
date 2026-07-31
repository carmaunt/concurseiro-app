package br.com.mauricio.oconcurseiro.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.mauricio.oconcurseiro.MainActivity
import br.com.mauricio.oconcurseiro.R
import br.com.mauricio.oconcurseiro.data.local.AppDatabase
import br.com.mauricio.oconcurseiro.data.preferences.StudyPlanPreferences
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class DailyMissionNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val studyPlanPreferences = StudyPlanPreferences(applicationContext)
        val metaDiaria = studyPlanPreferences.dailyGoal
        if (studyPlanPreferences.reminder == null) return Result.success()

        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        val (inicioHoje, fimHoje) = periodoHoje()

        val resolvidasHoje = AppDatabase.getInstance(applicationContext)
            .respostaDao()
            .totalRespostasNoPeriodo(usuarioId, inicioHoje, fimHoje)

        if (resolvidasHoje >= metaDiaria) {
            return Result.success()
        }

        showNotification(
            restantes = (metaDiaria - resolvidasHoje).coerceAtLeast(1),
            metaDiaria = metaDiaria
        )
        return Result.success()
    }

    private fun showNotification(
        restantes: Int,
        metaDiaria: Int
    ) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_QUESTIONS, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mensagem = if (restantes == 1) {
            "Falta só 1 questão para concluir sua meta de hoje."
        } else {
            "Faltam $restantes questões para concluir sua meta de $metaDiaria."
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Continue de onde parou")
            .setContentText(mensagem)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(mensagem)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(
            NOTIFICATION_ID,
            notification
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Missão diária",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Lembretes da missão diária de questões"
        }

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun periodoHoje(): Pair<Long, Long> {
        val inicio = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val fim = (inicio.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }

        return inicio.timeInMillis to fim.timeInMillis
    }

    companion object {
        private const val CHANNEL_ID = "daily_mission"
        private const val NOTIFICATION_ID = 1001
    }
}
