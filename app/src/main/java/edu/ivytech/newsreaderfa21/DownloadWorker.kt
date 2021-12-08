package edu.ivytech.newsreaderfa21

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "worker"
class DownloadWorker(val context:Context, workerParams : WorkerParameters) : Worker(context, workerParams){
    override fun doWork(): Result {
        Log.d(TAG, "worker triggered")
        ArticleRepository.get().fetchArticles()
        if(ArticleRepository.get().getNewArticles().isEmpty())
            Log.d(TAG, "no new articles")
        else {
            Log.d(TAG, "new articles available")
            val intent = MainActivity.newIntent(context)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val resources = context.resources
            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(resources.getString(R.string.new_articles_title))
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle(resources.getString(R.string.new_articles_title))
                .setContentText(resources.getString(R.string.new_articles_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(0, notification)
        }
        return Result.success()
    }

}