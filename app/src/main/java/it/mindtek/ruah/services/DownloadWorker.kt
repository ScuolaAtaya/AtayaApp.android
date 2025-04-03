package it.mindtek.ruah.services

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.edit
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import ir.mahdi.mzip.zip.ZipArchive
import it.mindtek.ruah.App.Companion.APP_SP
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityDownload
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.db.models.ModelFinalTest
import it.mindtek.ruah.db.models.ModelFinalTestQuestion
import it.mindtek.ruah.db.models.ModelQuestion
import it.mindtek.ruah.db.models.ModelRead
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.db.models.ModelSpeak
import it.mindtek.ruah.db.models.ModelUnderstand
import it.mindtek.ruah.db.models.ModelWrite
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.fromJson
import it.mindtek.ruah.ws.interfaces.ApiClient
import it.mindtek.ruah.ws.interfaces.DownloadProgressListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class DownloadWorker(context: Context, workerParameters: WorkerParameters) :
    ListenableWorker(context, workerParameters), DownloadProgressListener {
    private var lastUpdateTime: Long = 0L

    override fun startWork(): ListenableFuture<Result> = CallbackToFutureAdapter.getFuture {
        Executors.newSingleThreadExecutor().apply {
            execute {
                try {
                    ApiClient.downloadFile(this@DownloadWorker).execute().body()?.let {
                        val outputFile = File(applicationContext.filesDir, FILE_ZIP)
                        val inputStream: InputStream = it.byteStream()
                        val outputStream: OutputStream = outputFile.outputStream()
                        inputStream.copyTo(outputStream)
                        outputStream.flush()
                        outputStream.close()
                        inputStream.close()
                        onDownloadComplete()
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
                getForegroundInfo(
                    NotificationCompat.Builder(
                        applicationContext,
                        applicationContext.getString(R.string.notification_channel)
                    ).run {
                        setSmallIcon(R.drawable.download)
                        setContentText(getString(applicationContext, R.string.download))
                        setOngoing(true)
                        build()
                    }
                )
            )
        }

    override fun update(readMB: Long, totalMB: Long, percent: Int) {
        val currentTime: Long = System.currentTimeMillis()
        if (currentTime - lastUpdateTime > 100) { // added a delay to avoid too many updates
            setProgressAsync(Data.Builder().run {
                putLong(ActivityDownload.READ_MB, readMB)
                putLong(ActivityDownload.TOTAL_MB, totalMB)
                putInt(ActivityDownload.PROGRESS, percent)
                build()
            })
            setForegroundAsync(
                getForegroundInfo(
                    NotificationCompat.Builder(
                        applicationContext,
                        applicationContext.getString(R.string.notification_channel)
                    ).run {
                        setSmallIcon(R.drawable.download)
                        setContentText(
                            String.format(
                                getString(applicationContext, R.string.downloaded_mb),
                                readMB,
                                totalMB
                            )
                        )
                        setProgress(100, percent, false)
                        setOngoing(true)
                        build()
                    }
                )
            )
            lastUpdateTime = currentTime
        }
    }

    private fun onDownloadComplete() {
        setProgressAsync(Data.Builder().run {
            putBoolean(ActivityDownload.COMPLETED, true)
            putInt(ActivityDownload.PROGRESS, 100)
            build()
        })
        setForegroundAsync(
            getForegroundInfo(
                NotificationCompat.Builder(
                    applicationContext,
                    applicationContext.getString(R.string.notification_channel)
                ).run {
                    setSmallIcon(R.drawable.download)
                    setContentText(getString(applicationContext, R.string.downloaded))
                    setProgress(100, 100, false)
                    setAutoCancel(true)
                    build()
                }
            )
        )
        val inputFile = File(applicationContext.filesDir, FILE_ZIP)
        val outputFolder = File(applicationContext.filesDir, DATA_DIR)
        ZipArchive.unzip(inputFile.absolutePath, outputFolder.absolutePath, "")
        getJSON()?.let {
            val json = JSONObject(it)
            db.runInTransaction(Callable {
                saveUnderstands(json.getJSONArray(UNDERSTANDS))
                saveSpeak(json.getJSONArray(SPEAK))
                saveRead(json.getJSONArray(READ))
                saveWrite(json.getJSONArray(WRITE))
                saveFinalTest(json.getJSONArray(FINAL_TEST))
                saveTimestamp(json.getLong(TIMESTAMP))
            })
        }
    }

    private fun getJSON(): String? {
        val dir = File(applicationContext.filesDir, DATA_DIR)
        val file = File(dir.absolutePath, FILE_JSON)
        val length = file.length()
        return if (length < 1 || length > Integer.MAX_VALUE) {
            Log.w(TAG, "File is empty or huge: $file")
            null
        } else try {
            val content = CharArray(length.toInt())
            val numRead = FileReader(file).read(content)
            if (numRead.toLong() != length)
                Log.e(TAG, "Incomplete read of $file. Read chars $numRead of $length")
            String(content, 0, numRead)
        } catch (ex: Exception) {
            Log.e(TAG, "Failure reading $file", ex)
            null
        }
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

    private fun saveSpeak(speakJson: JSONArray) {
        val speaks = Gson().fromJson<MutableList<ModelSpeak>>(speakJson)
        db.speakDao().saveCategories(speaks)
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

    private fun saveWrite(writeJson: JSONArray) {
        val writes = Gson().fromJson<MutableList<ModelWrite>>(writeJson)
        writes.map {
            if (it.type == ADVANCED) it.letters = mutableListOf()
        }
        db.writeDao().saveCategories(writes)
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

    private fun saveTimestamp(timestamp: Long) {
        applicationContext.getSharedPreferences(APP_SP, Context.MODE_PRIVATE).edit {
            putLong(TIMESTAMP, timestamp)
        }
    }

    private fun getForegroundInfo(notification: Notification): ForegroundInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ForegroundInfo(101, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        else ForegroundInfo(101, notification)

    companion object {
        private const val TAG = "DownloadWorker"
        private const val DATA_DIR = "data"
        private const val FILE_ZIP = "file.zip"
        private const val FILE_JSON = "book.json"
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