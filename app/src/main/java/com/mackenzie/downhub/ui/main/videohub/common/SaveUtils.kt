package com.mackenzie.downhub.ui.main.videohub.common

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.mackenzie.downhub.BuildConfig
import com.mackenzie.downhub.R
import java.io.File

class SaveUtils {

    fun downloadAndInstallUpdate(context: Context, latestVer: String): Boolean {
        val flavorLink = latestVer.getFlavorLink(context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !context.hasWriteExternalStoragePermission()) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
            return false
        } else {
            val request = DownloadManager.Request(flavorLink.toUri())
                .setTitle(context.getString(R.string.app_name))
                .setDescription(context.getString(R.string.dialog_update_accept))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "Naughty-${BuildConfig.BUILD_TYPE}-V${latestVer}-C${BuildConfig.VERSION_CODE}.apk")
                .setMimeType("application/vnd.android.package-archive")

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            // Registrar un receptor para detectar cuando finaliza la descarga
            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        // Iniciar la instalación
                        val query = DownloadManager.Query().setFilterById(downloadId)
                        val cursor = downloadManager.query(query)
                        if (cursor.moveToFirst()) {
                            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            val status = cursor.getInt(statusIndex)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                val apkUri: Uri? = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    val fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)
                                    if (fileNameIndex != -1) {
                                        val localfileName = cursor.getString(fileNameIndex)
                                        localfileName?.let {
                                            Uri.fromFile(File(it))
                                        } ?: run { null }
                                    } else {
                                        null
                                    }
                                } else { downloadManager.getUriForDownloadedFile(downloadId) }
                                apkUri?.let { installApk(context, it) }
                            }
                        }
                        cursor.close()
                        context.unregisterReceiver(this)
                    }
                }
            }

            ContextCompat.registerReceiver(
                context,
                onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_EXPORTED
            )
            return true
        }
    }

    fun installApk(context: Context, apkUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }
}