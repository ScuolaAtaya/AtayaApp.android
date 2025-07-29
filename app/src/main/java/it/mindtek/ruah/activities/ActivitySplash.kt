package it.mindtek.ruah.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import it.mindtek.ruah.App.Companion.APP_SP
import it.mindtek.ruah.ws.interfaces.ApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySplash : AppCompatActivity(), Callback<ResponseBody> {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { true }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            checkUpdates()
        }, 1000)
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        goToUnits()
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        if (response.isSuccessful) response.body()?.let {
            val result = String(it.bytes())
            if (result == "true") download() else goToUnits()
        } ?: goToUnits()
        else goToUnits()
    }

    private fun checkUpdates() {
        val timestamp: Long =
            getSharedPreferences(APP_SP, MODE_PRIVATE).getLong(TIMESTAMP, 0)
        val request: Call<ResponseBody> = ApiClient.needsUpdate(timestamp)
        request.enqueue(this)
    }

    private fun download() {
        startActivity(Intent(this, ActivityDownload::class.java))
        finish()
    }

    private fun goToUnits() {
        startActivity(Intent(this, ActivityMain::class.java))
        finish()
    }

    companion object {
        private const val TIMESTAMP: String = "timestamp"
    }
}