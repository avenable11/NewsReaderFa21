package edu.ivytech.newsreaderfa21

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.preference.PreferenceManager
import androidx.work.*
import java.util.concurrent.TimeUnit

const val DOWNLOAD_WORK = "download_work"
const val NOTIFICATION_CHANNEL_ID = "news_reader"
class NewsReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ArticleRepository.initialize(this)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isInterval = prefs.getBoolean("is_interval", false)
        if(isInterval) {
            val interval = prefs.getString("interval", "1d")
            if (interval != null) {

                val constraints =
                    Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
                lateinit var workRequest: PeriodicWorkRequest
                when (interval) {
                    "1hr" -> workRequest =
                        PeriodicWorkRequest.Builder(
                            DownloadWorker::class.java,
                            1,
                            TimeUnit.HOURS,
                            50,
                            TimeUnit.MINUTES
                        )
                            .setConstraints(constraints)
                            .build()
                    "3hr" -> workRequest =
                        PeriodicWorkRequest.Builder(
                            DownloadWorker::class.java,
                            3,
                            TimeUnit.HOURS,
                            1,
                            TimeUnit.HOURS
                        )
                            .setConstraints(constraints)
                            .build()
                    "8hr" -> workRequest =
                        PeriodicWorkRequest.Builder(
                            DownloadWorker::class.java,
                            8,
                            TimeUnit.HOURS,
                            1,
                            TimeUnit.HOURS
                        )
                            .setConstraints(constraints)
                            .build()
                    "12hr" -> workRequest =
                        PeriodicWorkRequest.Builder(
                            DownloadWorker::class.java,
                            12,
                            TimeUnit.HOURS,
                            1,
                            TimeUnit.HOURS
                        )
                            .setConstraints(constraints)
                            .build()
                    else -> {
                        workRequest =
                            PeriodicWorkRequest.Builder(
                                DownloadWorker::class.java,
                                1,
                                TimeUnit.DAYS,
                                1, TimeUnit.HOURS
                            )
                                .setConstraints(constraints)
                                .build()
                    }
                }
                WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    DOWNLOAD_WORK,
                    ExistingPeriodicWorkPolicy.KEEP, workRequest
                )
            }
        } else {
            WorkManager.getInstance(this).cancelUniqueWork(DOWNLOAD_WORK)
            ArticleRepository.get().fetchArticles()
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager : NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}