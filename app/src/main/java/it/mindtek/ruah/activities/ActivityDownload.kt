package it.mindtek.ruah.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Dexter.withContext(this)
            .withPermission(Manifest.permission.POST_NOTIFICATIONS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    workManager.enqueueUniqueWork(DOWNLOAD_WORKER, ExistingWorkPolicy.KEEP, request)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    workManager.enqueueUniqueWork(DOWNLOAD_WORKER, ExistingWorkPolicy.KEEP, request)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).check()
        else workManager.enqueueUniqueWork(DOWNLOAD_WORKER, ExistingWorkPolicy.KEEP, request)
    }

    override fun onDestroy() {
        super.onDestroy()
        workManager.cancelUniqueWork(DOWNLOAD_WORKER)
    }

    companion object {
        const val READ_MB: String = "read_mb"
        const val TOTAL_MB: String = "total_mb"
        const val PROGRESS: String = "progress"
        const val COMPLETED: String = "completed"
        private const val DOWNLOAD_WORKER: String = "download_worker"
    }
}