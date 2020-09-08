package it.mindtek.ruah.kotlin.extensions

import android.view.View

/**
 * Created by alessandrogaboardi on 14/12/2017.
 */
fun View.setGone() {
    this.visibility = View.GONE
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}