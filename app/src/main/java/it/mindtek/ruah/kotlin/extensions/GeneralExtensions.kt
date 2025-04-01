package it.mindtek.ruah.kotlin.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.mindtek.ruah.db.AppDatabase
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

val db: AppDatabase
    get() = AppDatabase.getInstance()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJson(json: JSONObject): T =
    fromJson(json.toString(), object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJson(json: JSONArray): T =
    fromJson(json.toString(), object : TypeToken<T>() {}.type)