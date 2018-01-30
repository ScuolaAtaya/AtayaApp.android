package it.mindtek.ruah.ws.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by alessandrogaboardi on 17/01/2018.
 */
interface NeedsUpdateInterface {
    @GET("mock/book/update")
    fun needsUpdate(): Call<ResponseBody>
}