package it.mindtek.ruah.ws.interfaces

import it.mindtek.ruah.App
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Streaming


/**
 * Created by alessandro on 09/01/2018.
 */
interface FileDownloadInterface {
    @Headers("X-API-KEY: " + App.API_KEY)
    @GET("app/book")
    @Streaming
    fun downloadFile(): Call<ResponseBody>
}