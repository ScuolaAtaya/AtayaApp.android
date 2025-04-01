package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import it.mindtek.ruah.R
import it.mindtek.ruah.databinding.ActivityDownloadBinding
import it.mindtek.ruah.services.DownloadWorker

class ActivityDownload : AppCompatActivity() {
    private lateinit var workManager: WorkManager
    private lateinit var request: OneTimeWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDownloadBinding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        workManager = WorkManager.getInstance(this)
        request = OneTimeWorkRequest.Builder(DownloadWorker::class.java).run {
            setConstraints(Constraints.Builder().run {
                setRequiredNetworkType(NetworkType.CONNECTED)
                build()
            })
            setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            build()
        }
        workManager.getWorkInfoByIdLiveData(request.id).observe(this) {
            it?.let {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    startActivity(Intent(this, ActivityUnits::class.java))
                    finish()
                } else {
                    binding.progress.progress = it.progress.getInt(PROGRESS, 0)
                    binding.progressText.text = if (it.progress.getBoolean(COMPLETED, false))
                        getString(R.string.downloaded) else String.format(
                        getString(R.string.downloaded_mb),
                        it.progress.getLong(READ_MB, 0),
                        it.progress.getLong(TOTAL_MB, 0)
                    )
                }
            }
        }
        workManager.enqueueUniqueWork(DOWNLOAD_WORKER, ExistingWorkPolicy.KEEP, request)
    }

    override fun onDestroy() {
        super.onDestroy()
        workManager.cancelUniqueWork(DOWNLOAD_WORKER)
    }

    companion object {
        const val READ_MB = "read_mb"
        const val TOTAL_MB = "total_mb"
        const val PROGRESS = "progress"
        const val COMPLETED = "completed"
        private const val DOWNLOAD_WORKER = "download_worker"
    }
}