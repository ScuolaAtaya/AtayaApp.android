package it.mindtek.ruah.kotlin.extensions

import android.content.Context
import android.os.Build
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.mindtek.ruah.db.AppDatabase
import okhttp3.ResponseBody
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

val db: AppDatabase
    get() {
        return AppDatabase.getInstance()
    }

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun compat21(compatible: (() -> Unit)?, incompatible: (() -> Unit)?) {
    compatCheck(Build.VERSION_CODES.LOLLIPOP, compatible, incompatible)
}

fun compatCheck(version: Int, compatible: (() -> Unit)?, incompatible: (() -> Unit)?) {
    if (Build.VERSION.SDK_INT >= version) {
        compatible?.invoke()
    } else {
        incompatible?.invoke()
    }
}

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

inline fun <T> `while`(nextValue: () -> T, condition: (T) -> Boolean, body: (T) -> Unit) {
    var value = nextValue()
    while (condition(value)) {
        body(value)
        value = nextValue()
    }
}

fun readFile(context: Context, fileName: String): File {
    val path = context.filesDir.absolutePath + File.separator + fileName
    return File(path)
}

fun replaceFile(context: Context, file: File) {

}

fun deleteFile(file: File): Boolean {
    return file.delete()
}