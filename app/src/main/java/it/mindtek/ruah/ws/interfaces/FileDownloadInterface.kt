package it.mindtek.ruah.ws.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming

/**
 * Created by alessandro on 09/01/2018.
 */
interface FileDownloadInterface {
    @GET("app/book/v2")
    @Streaming
    fun downloadFile(): Call<ResponseBody>
}