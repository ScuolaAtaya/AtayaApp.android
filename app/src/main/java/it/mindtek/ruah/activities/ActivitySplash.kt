package it.mindtek.ruah.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import it.mindtek.ruah.R
import it.mindtek.ruah.ws.interfaces.ApiClient
import okhttp3.ResponseBody
import org.jetbrains.anko.defaultSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySplash : AppCompatActivity(), Callback<ResponseBody> {
    private val apiClient = ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
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
        val timestamp = defaultSharedPreferences.getLong(ActivityUnits.TIMESTAMP, 0)
        val request = apiClient.needsUpdate(timestamp)
        request.enqueue(this)
    }

    private fun download() {
        val intent = Intent(this, ActivityDownload::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
    }

    private fun goToUnits() {
        startActivity(Intent(this, ActivityUnits::class.java))
    }
}