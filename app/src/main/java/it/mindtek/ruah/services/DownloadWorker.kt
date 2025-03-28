package it.mindtek.ruah.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import ir.mahdi.mzip.zip.ZipArchive
import it.mindtek.ruah.App.Companion.APP_SP
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.*
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.fromJson
import it.mindtek.ruah.kotlin.extensions.`while`
import it.mindtek.ruah.pojos.Download
import it.mindtek.ruah.ws.interfaces.ApiClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.concurrent.Executors
import kotlin.math.pow
import kotlin.math.roundToInt

class DownloadWorker(context: Context, workerParameters: WorkerParameters) :
    ListenableWorker(context, workerParameters) {
    private var totalFileSize: Int = 0

    override fun startWork(): ListenableFuture<Result> = CallbackToFutureAdapter.getFuture {
        Executors.newSingleThreadExecutor().apply {
            execute {
                try {
                    ApiClient.downloadFile().execute().body()?.let {
                        downloadFile(it)
                    }
                } catch (e: IOException) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
                it.set(Result.success())
                shutdown()
            }
        }
    }

    override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> =
        CallbackToFutureAdapter.getFuture {
            it.set(
                ForegroundInfo(
                    101,
                    NotificationCompat.Builder(
                        applicationContext,
                        applicationContext.getString(R.string.notification_channel)
                    ).run {
                        setSmallIcon(R.drawable.download)
                        setContentText("Downloading File")
                        setOngoing(true)
                        build()
                    }
                )
            )
        }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {
        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(applicationContext.filesDir, FILE_ZIP)
        val output = FileOutputStream(outputFile)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
        `while`({ bis.read(data) }, { it != -1 }, { count ->
            total += count.toLong()
            totalFileSize = (fileSize / 1024.0.pow(2.0)).toInt()
            val current = (total / 1024.0.pow(2.0)).roundToInt().toDouble()
            val progress = (total * 100 / fileSize).toInt()
            val currentTime = System.currentTimeMillis() - startTime
            val download = Download()
            download.totalFileSize = totalFileSize
            if (currentTime > 1000 * timeCount) {
                download.currentFileSize = current.toInt()
                download.progress = progress
                updateNotification(download)
                timeCount++
            }
            output.write(data, 0, count)
        })
        onDownloadComplete()
        output.flush()
        output.close()
        bis.close()

    }

    private fun updateNotification(download: Download) {
        setForegroundAsync(
            ForegroundInfo(
                101,
                NotificationCompat.Builder(
                    applicationContext,
                    applicationContext.getString(R.string.notification_channel)
                ).run {
                    setSmallIcon(R.drawable.download)
                    setContentText("Downloading file " + download.currentFileSize + "/" + totalFileSize + " MB")
                    setProgress(100, download.progress, false)
                    setOngoing(true)
                    build()
                }
            ))
    }

    private fun onDownloadComplete() {
        setForegroundAsync(
            ForegroundInfo(
                101,
                NotificationCompat.Builder(
                    applicationContext,
                    applicationContext.getString(R.string.notification_channel)
                ).run {
                    setSmallIcon(R.drawable.download)
                    setContentText("File Downloaded")
                    setProgress(100, 100, false)
                    setAutoCancel(true)
                    build()
                }
            ))
        unzipFile()
        parseJSON()
    }

    private fun unzipFile() {
        val inputFile = File(applicationContext.filesDir, FILE_ZIP)
        val outputFolder = File(applicationContext.filesDir, DATA_DIR)
        ZipArchive.unzip(inputFile.absolutePath, outputFolder.absolutePath, "")
    }

    private fun parseJSON() {
        val json = JSONObject(getJSON())
        saveUnderstands(json.getJSONArray(UNDERSTANDS))
        saveSpeak(json.getJSONArray(SPEAK))
        saveRead(json.getJSONArray(READ))
        saveWrite(json.getJSONArray(WRITE))
        saveFinalTest(json.getJSONArray(FINAL_TEST))
        saveTimestamp(json.getLong(TIMESTAMP))
    }

    private fun saveTimestamp(timestamp: Long) {
        applicationContext.getSharedPreferences(APP_SP, Context.MODE_PRIVATE).edit {
            putLong(TIMESTAMP, timestamp)
        }
    }

    private fun saveWrite(writeJson: JSONArray) {
        val writes = Gson().fromJson<MutableList<ModelWrite>>(writeJson)
        writes.map {
            if (it.type == ADVANCED) it.letters = mutableListOf()
        }
        db.writeDao().saveCategories(writes)
    }

    private fun saveRead(readJson: JSONArray) {
        val reads = mutableListOf<ModelRead>()
        val options = mutableListOf<ModelReadOption>()
        (0 until readJson.length()).forEach {
            val currentReadJson = readJson.getJSONObject(it)
            val currentOptionsJson = currentReadJson.getJSONArray(OPTIONS)
            val read = Gson().fromJson<ModelRead>(currentReadJson)
            val currentOptions = Gson().fromJson<MutableList<ModelReadOption>>(currentOptionsJson)
            reads.add(read)
            options.addAll(currentOptions)
        }
        db.readDao().saveCategories(reads)
        db.readDao().saveOptions(options)
    }

    private fun saveSpeak(speakJson: JSONArray) {
        val speaks = Gson().fromJson<MutableList<ModelSpeak>>(speakJson)
        db.speakDao().saveCategories(speaks)
    }

    private fun saveUnderstands(understandsJson: JSONArray) {
        val understands = mutableListOf<ModelUnderstand>()
        val questions = mutableListOf<ModelQuestion>()
        val answers = mutableListOf<ModelAnswer>()
        (0 until understandsJson.length()).forEach {
            val understandJson = understandsJson.getJSONObject(it)
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

    private fun saveFinalTest(finalTestJson: JSONArray) {
        val finalTests = mutableListOf<ModelFinalTest>()
        val questions = mutableListOf<ModelFinalTestQuestion>()
        (0 until finalTestJson.length()).forEach {
            val currentFinalTestJson = finalTestJson.getJSONObject(it)
            val currentQuestionsJson = currentFinalTestJson.getJSONArray(QUESTIONS)
            val finalTest = Gson().fromJson<ModelFinalTest>(currentFinalTestJson)
            val currentQuestions =
                Gson().fromJson<MutableList<ModelFinalTestQuestion>>(currentQuestionsJson)
            finalTests.add(finalTest)
            questions.addAll(currentQuestions)
        }
        db.finalTestDao().saveCategories(finalTests)
        db.finalTestDao().saveQuestions(questions)
    }

    private fun getJSON(): String {
        val dir = File(applicationContext.filesDir, DATA_DIR)
        val file = File(dir.absolutePath, "book.json")
        var result: String
        val length = file.length()
        if (length < 1 || length > Integer.MAX_VALUE) {
            result = ""
            Log.w(TAG, "File is empty or huge: $file")
        } else {
            try {
                FileReader(file).use {
                    val content = CharArray(length.toInt())
                    val numRead = it.read(content)
                    if (numRead.toLong() != length)
                        Log.e(TAG, "Incomplete read of $file. Read chars $numRead of $length")
                    result = String(content, 0, numRead)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Failure reading $file", ex)
                result = ""
            }
        }
        return result
    }

    companion object {
        private const val TAG = "DownloadWorker"
        private const val DATA_DIR = "data"
        private const val FILE_ZIP = "file.zip"
        private const val TIMESTAMP = "timestamp"
        private const val UNDERSTANDS = "understand"
        private const val QUESTIONS = "questions"
        private const val ANSWERS = "answers"
        private const val SPEAK = "speak"
        private const val READ = "read"
        private const val OPTIONS = "options"
        private const val WRITE = "write"
        private const val FINAL_TEST = "final"
        private const val ADVANCED = "advanced"
    }
}