package it.mindtek.ruah.ws.interfaces

import it.mindtek.ruah.App
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * Created by alessandrogaboardi on 17/01/2018.
 */
interface NeedsUpdateInterface {
    @Headers("X-API-KEY: " + App.API_KEY)
    @GET("app/book/v2/update/{timestamp}")
    fun needsUpdate(@Path("timestamp") timestamp: Long): Call<ResponseBody>
}