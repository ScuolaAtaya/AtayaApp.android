package it.mindtek.ruah.kotlin.extensions

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.widget.AppCompatButton
import org.jetbrains.anko.dip


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
    this.setCompoundDrawables(left, top, right, bottom)
}

@Suppress("DEPRECATION")
fun tint(drawable: Drawable?, color: Int, context: Context): Drawable? {
    drawable?.let {
        val copy = drawable.constantState!!.newDrawable()
        copy.setBounds(0, 0, context.dip(24), context.dip(24))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            copy.mutate().colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        else copy.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        return copy
    } ?: return null
}

fun AppCompatButton.disable() {
    this.isEnabled = false
}

fun AppCompatButton.enable() {
    this.isEnabled = true
}