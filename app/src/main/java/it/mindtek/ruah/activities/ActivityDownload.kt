package it.mindtek.ruah.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import it.mindtek.ruah.databinding.ActivityDownloadBinding
import it.mindtek.ruah.services.DownloadWorker

class ActivityDownload : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityDownloadBinding.inflate(layoutInflater)
        val workManager: WorkManager = WorkManager.getInstance(this)
        val request: OneTimeWorkRequest =
            OneTimeWorkRequest.Builder(DownloadWorker::class.java).run {
                setConstraints(Constraints.Builder().run {
                    setRequiredNetworkType(NetworkType.CONNECTED)
                    build()
                })
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                build()
            }
        workManager.enqueueUniqueWork(DOWNLOAD_WORKER, ExistingWorkPolicy.REPLACE, request)
        workManager.getWorkInfoByIdLiveData(request.id).observe(this) {

        }
    }

    companion object {
        private const val DOWNLOAD_WORKER = "download_worker"
    }
}