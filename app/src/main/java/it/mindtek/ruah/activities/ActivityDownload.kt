package it.mindtek.ruah.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import it.mindtek.ruah.R
import it.mindtek.ruah.pojos.Download
import it.mindtek.ruah.services.DownloadService
import kotlinx.android.synthetic.main.activity_download.*

class ActivityDownload : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1337
    private val DOWNLOAD = "download"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        registerReceiver()
        downloadFile()
    }

    private fun downloadFile() {
//        if (checkPermission()) {
            startDownload()
//        } else {
//            requestPermission()
//        }
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
                val download = intent.getParcelableExtra<Download>(DOWNLOAD)
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
                val intent = Intent(this@ActivityDownload, ActivityUnits::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@ActivityDownload, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload()
            } else {
                Snackbar.make(constraint, getString(R.string.download_permission_denied), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        val MESSAGE_PROGRESS = "message_progress"
        val PARSE_COMPLETED = "book_parse_completed"
    }
}
