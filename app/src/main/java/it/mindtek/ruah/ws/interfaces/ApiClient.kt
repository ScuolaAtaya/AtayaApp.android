package it.mindtek.ruah.ws.interfaces

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object ApiClient {
    private lateinit var baseURL: String
    private lateinit var APIKey: String

    fun init(baseUrl: String, apiKey: String) {
        baseURL = baseUrl
        APIKey = apiKey
    }

    fun downloadFile(listener: DownloadProgressListener): Call<ResponseBody> =
        getDownload(baseURL, APIKey, listener).create(FileDownloadInterface::class.java)
            .downloadFile()

    fun needsUpdate(timestamp: Long): Call<ResponseBody> =
        getRetrofit(baseURL, APIKey).create(NeedsUpdateInterface::class.java).needsUpdate(timestamp)

    private fun getRetrofit(baseUrl: String, apiKey: String): Retrofit {
        val okHttpBuilder = OkHttpClient.Builder().run {
            addInterceptor {
                val request = it.request().newBuilder().addHeader("X-API-KEY", apiKey)
                it.proceed(request.build())
            }
            build()
        }
        return Retrofit.Builder().run {
            baseUrl(baseUrl)
            client(okHttpBuilder)
            build()
        }
    }

    private fun getDownload(
        baseUrl: String,
        apiKey: String,
        downloadProgressListener: DownloadProgressListener
    ): Retrofit {
        val okHttpBuilder = OkHttpClient.Builder().run {
            addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request().newBuilder().addHeader("X-API-KEY", apiKey)
                chain.proceed(request.build())
            }
            addInterceptor(DownloadProgressInterceptor(downloadProgressListener))
            retryOnConnectionFailure(true)
            connectTimeout(15L, TimeUnit.SECONDS)
            readTimeout(60L, TimeUnit.SECONDS)
            build()
        }
        return Retrofit.Builder().run {
            baseUrl(baseUrl)
            client(okHttpBuilder)
            build()
        }
    }
}