package it.mindtek.ruah.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import it.mindtek.ruah.R
import it.mindtek.ruah.pojos.Download
import it.mindtek.ruah.services.DownloadService
import kotlinx.android.synthetic.main.activity_download.*

class ActivityDownload : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        registerReceiver()
        startDownload()
    }

    private fun startDownload() {
        val intent = Intent(this, DownloadService::class.java)
        startService(intent)
    }

    private fun registerReceiver() {
        val bManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(MESSAGE_PROGRESS)
        intentFilter.addAction(PARSE_COMPLETED)
        bManager.registerReceiver(broadcastReceiver, intentFilter)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == MESSAGE_PROGRESS) {
                val download = intent.getParcelableExtra<Download>(DOWNLOAD)!!
                progress.progress = download.progress
                if (download.progress == 100) {
                    progressText.text = getString(R.string.downloaded)
                    progress.isIndeterminate = true
                } else {
                    progressText.text = String.format(getString(R.string.downloaded_mb), download.currentFileSize, download.totalFileSize)
                }
            } else if (intent.action == PARSE_COMPLETED) {
                progress.isIndeterminate = false
                progress.progress = 100
                startActivity(Intent(this@ActivityDownload, ActivityUnits::class.java))
                finish()
            }
        }
    }

    companion object {
        const val MESSAGE_PROGRESS = "message_progress"
        const val PARSE_COMPLETED = "book_parse_completed"
        const val DOWNLOAD = "download"
    }
}