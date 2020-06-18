package it.mindtek.ruah.ws.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by alessandrogaboardi on 17/01/2018.
 */
interface NeedsUpdateInterface {
    @GET("app/book/v2/update/{timestamp}")
    fun needsUpdate(@Path("timestamp") timestamp: Long): Call<ResponseBody>
}