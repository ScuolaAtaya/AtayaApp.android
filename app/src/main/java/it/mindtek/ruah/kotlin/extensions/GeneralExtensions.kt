package it.mindtek.ruah.kotlin.extensions

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
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

fun Snackbar.setBottomMargin(parent: View): Snackbar {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        val params: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        val insets: Insets? =
            ViewCompat.getRootWindowInsets(parent)?.getInsets(WindowInsetsCompat.Type.systemBars())
        params.bottomMargin = insets?.bottom ?: 0 // Apply bottom inset as margin
        view.layoutParams = params
    }
    return this
}