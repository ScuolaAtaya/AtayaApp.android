package it.mindtek.ruah.ws.interfaces

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit

object ApiClient {
    private lateinit var baseURL: String
    private lateinit var apiKey: String

    fun init(baseUrl: String, apiKey: String) {
        this.baseURL = baseUrl
        this.apiKey = apiKey
    }

    fun downloadFile(): Call<ResponseBody> {
        val request = getRetrofit(baseURL, apiKey).create(FileDownloadInterface::class.java)
        return request.downloadFile()
    }

    fun needsUpdate(timestamp: Long): Call<ResponseBody> {
        val request = getRetrofit(baseURL, apiKey).create(NeedsUpdateInterface::class.java)
        return request.needsUpdate(timestamp)
    }

    private fun getRetrofit(baseUrl: String, apiKey: String): Retrofit =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getHttpClient(apiKey))
                    .build()

    private fun getHttpClient(apiKey: String): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addInterceptor {
            val request = it.request().newBuilder().addHeader("X-API-KEY", apiKey)
            it.proceed(request.build())
        }
        return okHttpBuilder.build()
    }
}