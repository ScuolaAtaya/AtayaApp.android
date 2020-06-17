package it.mindtek.ruah.ws.interfaces

import it.mindtek.ruah.R
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Path

object ApiClient {
    private lateinit var baseURL: String

    fun init(baseUrl: String) {
        this.baseURL = baseUrl
    }

    fun downloadFile(): Call<ResponseBody> {
        val retrofit = getRetrofit(baseURL)
        val request = retrofit.create(FileDownloadInterface::class.java)
        return request.downloadFile()
    }

    fun needsUpdate(timestamp: Long): Call<ResponseBody> {
        val retrofit = getRetrofit(baseURL)
        val request = retrofit.create(NeedsUpdateInterface::class.java)
        return request.needsUpdate(timestamp)
    }

    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .build()
    }
}