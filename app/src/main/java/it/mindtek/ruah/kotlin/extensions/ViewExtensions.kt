package it.mindtek.ruah.kotlin.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Created by alessandrogaboardi on 14/12/2017.
 */
fun View.setGone() {
    visibility = View.GONE
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setTopPadding() {
    val padTop: Int = paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(this) { v: View, insets: WindowInsetsCompat ->
        v.updatePadding(top = padTop + insets.getInsets(WindowInsetsCompat.Type.systemBars()).top)
        insets
    }
}

fun View.setBottomPadding() {
    val padBottom: Int = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v: View, insets: WindowInsetsCompat ->
        v.updatePadding(bottom = padBottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
        insets
    }
}