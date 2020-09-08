package it.mindtek.ruah.kotlin.extensions

import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import androidx.annotation.DrawableRes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat

/**
 * Created by alessandrogaboardi on 05/12/2017.
 */
@Suppress("DEPRECATION")
fun FloatingActionButton.setTintPreLollipop(color: Int, @DrawableRes iconRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        setTint(color)
    } else {
        val icon = ContextCompat.getDrawable(context, iconRes)
        val copy = icon?.constantState?.newDrawable()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            copy?.mutate()?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            copy?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
        setImageDrawable(copy)
    }
}

fun FloatingActionButton.setTint(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        imageTintList = ColorStateList.valueOf(color)
}