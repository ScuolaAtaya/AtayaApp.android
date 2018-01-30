package it.mindtek.ruah.ws.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Streaming
import retrofit2.http.GET



/**
 * Created by alessandro on 09/01/2018.
 */
interface FileDownloadInterface {
    @GET("mock/book")
    @Streaming
    fun downloadFile(): Call<ResponseBody>
}