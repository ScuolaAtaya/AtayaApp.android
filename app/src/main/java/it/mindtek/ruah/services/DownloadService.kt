package it.mindtek.ruah.services

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import ir.mahdi.mzip.zip.ZipArchive
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityDownload
import it.mindtek.ruah.interfaces.FileDownloadInterface
import it.mindtek.ruah.kotlin.extensions.`while`
import it.mindtek.ruah.pojos.Download
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by alessandro on 09/01/2018.
 */
class DownloadService() : IntentService("Download service") {
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var totalFileSize: Int = 0

    override fun onHandleIntent(intent: Intent?) {

        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channel))
                .setSmallIcon(R.drawable.download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true)
        notificationManager?.notify(0, notificationBuilder?.build())

        initDownload()

    }

    private fun initDownload() {
        val base_url = getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder()
                .baseUrl(base_url)
                .build()

        val retrofitInterface = retrofit.create(FileDownloadInterface::class.java)

        val request = retrofitInterface.downloadFile()
        try {
            val body = request.execute().body()
            body?.let {
                downloadFile(it)
            }
        } catch (e: IOException) {
            e.printStackTrace();
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {

        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "file.zip")
        val output = FileOutputStream(outputFile)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
        `while`({ bis.read(data) }, { it != -1 }, { count ->
            total += count.toLong()
            totalFileSize = (fileSize / Math.pow(1024.0, 2.0)).toInt()
            val current = Math.round(total / Math.pow(1024.0, 2.0)).toDouble()

            val progress = (total * 100 / fileSize).toInt()

            val currentTime = System.currentTimeMillis() - startTime

            val download = Download()
            download.totalFileSize = totalFileSize

            if (currentTime > 1000 * timeCount) {

                download.currentFileSize = current.toInt()
                download.progress = progress
                sendNotification(download)
                timeCount++
            }

            output.write(data, 0, count)
        })
        onDownloadComplete()
        output.flush()
        output.close()
        bis.close()

    }

    private fun sendNotification(download: Download) {

        sendIntent(download)
        notificationBuilder?.setProgress(100, download.progress, false)
        notificationBuilder?.setContentText("Downloading file " + download.currentFileSize + "/" + totalFileSize + " MB")
        notificationManager?.notify(0, notificationBuilder?.build())
    }

    private fun sendIntent(download: Download) {

        val intent = Intent(ActivityDownload.MESSAGE_PROGRESS)
        intent.putExtra("download", download)
        LocalBroadcastManager.getInstance(this@DownloadService).sendBroadcast(intent)
    }

    private fun onDownloadComplete() {

        val download = Download()
        download.progress = 100
        sendIntent(download)

        unzipFile()

        notificationManager?.cancel(0)
        notificationBuilder?.setProgress(0, 0, false)
        notificationBuilder?.setContentText("File Downloaded")
        notificationManager?.notify(0, notificationBuilder?.build())

    }

    private fun unzipFile(){
        val inputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "file.zip")
        val outputFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "data")
        ZipArchive.unzip(inputFile.absolutePath, outputFolder.absolutePath, "")
        outputFolder.list().forEach { println(it) }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager?.cancel(0)
    }
}