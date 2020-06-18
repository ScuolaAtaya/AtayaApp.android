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
        val retrofit = getRetrofit(baseURL, apiKey)
        val request = retrofit.create(FileDownloadInterface::class.java)
        return request.downloadFile()
    }

    fun needsUpdate(timestamp: Long): Call<ResponseBody> {
        val retrofit = getRetrofit(baseURL, apiKey)
        val request = retrofit.create(NeedsUpdateInterface::class.java)
        return request.needsUpdate(timestamp)
    }

    private fun getRetrofit(baseUrl: String, apiKey: String): Retrofit {
        val okHttp: OkHttpClient = getHttpClient(apiKey)
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttp)
                .build()
    }

    private fun getHttpClient(apiKey: String): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                    .addHeader("X-API-KEY", apiKey)
            chain.proceed(request.build())
        }
        return okHttpBuilder.build()
    }
}