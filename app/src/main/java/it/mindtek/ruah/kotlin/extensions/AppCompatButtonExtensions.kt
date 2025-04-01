package it.mindtek.ruah.kotlin.extensions

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.widget.AppCompatButton
import it.mindtek.ruah.config.LayoutUtils

/**
 * Created by alessandrogaboardi on 05/12/2017.
 */
const val LEFT = 0
const val TOP = 1
const val RIGHT = 2
const val BOTTOM = 3

fun AppCompatButton.setColor(color: Int) {
    setTextColor(color)
    setDrawableColor(color)
}

fun AppCompatButton.setDrawableColor(color: Int) {
    val drawables = compoundDrawables
    val left = tint(drawables[LEFT], color, context)
    val top = tint(drawables[TOP], color, context)
    val right = tint(drawables[RIGHT], color, context)
    val bottom = tint(drawables[BOTTOM], color, context)
    setCompoundDrawables(left, top, right, bottom)
}

@Suppress("DEPRECATION")
fun tint(drawable: Drawable?, color: Int, context: Context): Drawable? = drawable?.let {
    drawable.constantState?.newDrawable()?.apply {
        setBounds(0, 0, LayoutUtils.dpToPx(context, 24), LayoutUtils.dpToPx(context, 24))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            mutate().colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        else mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}

fun AppCompatButton.disable() {
    isEnabled = false
}

fun AppCompatButton.enable() {
    isEnabled = true
}