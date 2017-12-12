package it.mindtek.ruah.kotlin.extensions

import android.annotation.TargetApi
import android.os.Build
import android.support.annotation.RequiresApi
import it.mindtek.ruah.db.AppDatabase
import java.util.concurrent.Executors

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

val db: AppDatabase get() {
    return AppDatabase.getInstance()
}

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun compat21(compatible: (() -> Unit)?, incompatible: (() -> Unit)?){
    compatCheck(Build.VERSION_CODES.LOLLIPOP, compatible, incompatible)
}

fun compatCheck(version: Int, compatible: (() -> Unit)?, incompatible: (() -> Unit)?){
    if(Build.VERSION.SDK_INT >= version){
        compatible?.invoke()
    }else{
        incompatible?.invoke()
    }
}