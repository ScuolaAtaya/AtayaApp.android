package it.mindtek.ruah.config

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

object LayoutUtils {
    fun dpToPx(context: Context, dp: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        context.resources.displayMetrics
    ).roundToInt()

    fun spToPx(context: Context, sp: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp.toFloat(),
        context.resources.displayMetrics
    ).roundToInt()
}