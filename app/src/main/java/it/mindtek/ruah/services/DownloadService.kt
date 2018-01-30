package it.mindtek.ruah.services

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import ir.mahdi.mzip.zip.ZipArchive
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityDownload
import it.mindtek.ruah.ws.interfaces.FileDownloadInterface
import it.mindtek.ruah.kotlin.extensions.`while`
import it.mindtek.ruah.pojos.Download
import okhttp3.ResponseBody
import retrofit2.Retrofit
import com.google.gson.Gson
import it.mindtek.ruah.db.models.*
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.fromJson
import org.json.JSONArray
import org.json.JSONObject
import java.io.*


/**
 * Created by alessandro on 09/01/2018.
 */
class DownloadService() : IntentService("Download service") {
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var totalFileSize: Int = 0

    private val UNDERSTANDS = "understand"
    private val QUESTIONS = "questions"
    private val ANSWERS = "answers"
    private val SPEAK = "speak"
    private val READ = "read"
    private val OPTIONS = "options"
    private val WRITE = "write"

    private val TAG = javaClass.simpleName

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
            e.printStackTrace()
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {

        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(filesDir, "file.zip")
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
        getSharedPreferences("Application", Context.MODE_PRIVATE).edit().putBoolean("Updated", true).apply()
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

    private fun sendParseCompletionIntent(){
        val intent = Intent(ActivityDownload.PARSE_COMPLETED)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun onDownloadComplete() {

        val download = Download()
        download.progress = 100
        sendIntent(download)
        notificationManager?.cancel(0)
        notificationBuilder?.setProgress(0, 0, false)
        notificationBuilder?.setContentText("File Downloaded")
        notificationManager?.notify(0, notificationBuilder?.build())

        unzipFile()
        parseJSON()

        sendParseCompletionIntent()
    }

    private fun unzipFile(){
        val inputFile = File(filesDir, "file.zip")
        val outputFolder = File(filesDir, "data")
        ZipArchive.unzip(inputFile.absolutePath, outputFolder.absolutePath, "")
        outputFolder.list().forEach { println(it) }
    }

    private fun parseJSON(){
        val json = JSONObject(getJSON())
        saveUnderstands(json.getJSONArray(UNDERSTANDS))
        saveSpeak(json.getJSONArray(SPEAK))
        saveRead(json.getJSONArray(READ))
        saveWrite(json.getJSONArray(WRITE))
    }

    private fun saveWrite(writeJson: JSONArray){
        val writes = Gson().fromJson<MutableList<ModelWrite>>(writeJson)
        db.writeDao().saveCategories(writes)
    }

    private fun saveRead(readJson: JSONArray){
        val reads = mutableListOf<ModelRead>()
        val options = mutableListOf<ModelReadAnswer>()
        for (i in 0 until readJson.length()){
            val currentReadJson = readJson.getJSONObject(i)
            val currentOptionsJson = currentReadJson.getJSONArray(OPTIONS)

            val read = Gson().fromJson<ModelRead>(currentReadJson)
            val currentOptions = Gson().fromJson<MutableList<ModelReadAnswer>>(currentOptionsJson)

            reads.add(read)
            options.addAll(currentOptions)
        }
        db.readDao().saveCategories(reads)
        db.readDao().saveAnswers(options)
    }

    private fun saveSpeak(speakJson: JSONArray){
        val speaks = Gson().fromJson<MutableList<ModelSpeak>>(speakJson)
        db.speakDao().saveCategories(speaks)
    }

    private fun saveUnderstands(understandsJson: JSONArray){
        val understands = mutableListOf<ModelUnderstand>()
        val questions = mutableListOf<ModelQuestion>()
        val answers = mutableListOf<ModelAnswer>()
        for (i in 0 until understandsJson.length()){
            val understandJson = understandsJson.getJSONObject(i)
            val currentQuestionsJson = understandJson.getJSONArray(QUESTIONS)
            val currentAnswersJson = understandJson.getJSONArray(ANSWERS)

            val understand = Gson().fromJson<ModelUnderstand>(understandJson)
            val currentQuestions = Gson().fromJson<MutableList<ModelQuestion>>(currentQuestionsJson)
            val currentAnswers = Gson().fromJson<MutableList<ModelAnswer>>(currentAnswersJson)

            understands.add(understand)
            questions.addAll(currentQuestions)
            answers.addAll(currentAnswers)
        }
        db.understandDao().saveCategories(understands)
        db.understandDao().saveQuestions(questions)
        db.understandDao().saveAnswers(answers)
    }

    private fun getJSON(): String{
        val dir = File(filesDir, "data")
        val file = File(dir.absolutePath, "book.json")
        var result = ""
        val length = file.length()
        if (length < 1 || length > Integer.MAX_VALUE) {
            result = ""
            Log.w(TAG, "File is empty or huge: " + file)
        } else {
            try {
                FileReader(file).use({ `in` ->
                    val content = CharArray(length.toInt())

                    val numRead = `in`.read(content)
                    if (numRead.toLong() != length) {
                        Log.e(TAG, "Incomplete read of $file. Read chars $numRead of $length")
                    }
                    result = String(content, 0, numRead)
                })
            } catch (ex: Exception) {
                Log.e(TAG, "Failure reading " + file, ex)
                result = ""
            }

        }
        return result
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager?.cancel(0)
    }
}