package it.mindtek.ruah.config

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

@SuppressLint("DiscouragedApi")
object ResourceProvider {
    @StringRes
    fun getString(context: Context, name: String): Int =
        context.resources.getIdentifier(name, "string", context.packageName)

    @DrawableRes
    fun getIcon(context: Context, name: String): Int =
        context.resources.getIdentifier(name, "drawable", context.packageName)

    @ColorInt
    fun getColor(context: Context, name: String): Int = ContextCompat.getColor(
        context,
        context.resources.getIdentifier(name, "color", context.packageName)
    )
}