package com.zalj.schedule.VersionControl

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.zalj.schedule.BuildConfig
import java.io.File

class UpdateHelper(private val context: Context) {

    public fun update(url: String, distanceToSave: File) { // Set path for file
        // Check if file already exists
        if (distanceToSave.exists())
            distanceToSave.delete()

        // Set Download Manager request
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("Скачиваем обновление")
        //TODO Можно дописать какая версия скачивается
        request.setDescription("Скачивание обновления")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationUri(Uri.fromFile(distanceToSave))

        // Get download service and enqueue file
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        if (manager == null) {
            Toast.makeText(context, "Ошибка: На вашем устройстве отсутсвует менеджер загрузок", Toast.LENGTH_LONG).show()
            return
        }
        val downloadId = manager.enqueue(request)

        // Set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                val intentInstall = Intent(Intent.ACTION_VIEW)
                val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", distanceToSave)
                intentInstall.setDataAndType(uri, "application/vnd.android.package-archive")
                intentInstall.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

                if (context.packageManager.resolveActivity(intentInstall, PackageManager.MATCH_DEFAULT_ONLY) == null) {
                    Toast.makeText(context, "Ошибка: на устройстве отсутсвует менеджер установки приложений", Toast.LENGTH_LONG).show()
                    return
                }

                context.startActivity(intentInstall)
                context.unregisterReceiver(this)
                onDestroy()
            }
        }

        // Register receiver for when .apk download is compete
        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun onDestroy() {

    }
}

