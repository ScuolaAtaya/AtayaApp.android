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

fun AppCompatButton.setColor(color: Int) {
    setTextColor(color)
    setDrawableColor(color)
}

fun AppCompatButton.setDrawableColor(color: Int) {
    val left: Drawable? = tint(compoundDrawables[0], color, context)
    val top: Drawable? = tint(compoundDrawables[1], color, context)
    val right: Drawable? = tint(compoundDrawables[2], color, context)
    val bottom: Drawable? = tint(compoundDrawables[3], color, context)
    setCompoundDrawables(left, top, right, bottom)
}

@Suppress("DEPRECATION")
fun tint(drawable: Drawable?, color: Int, context: Context): Drawable? = drawable?.let {
    it.constantState?.newDrawable()?.apply {
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