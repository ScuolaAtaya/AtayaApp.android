package it.mindtek.ruah.kotlin.extensions

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat

/**
 * Created by alessandrogaboardi on 05/12/2017.
 */
fun FloatingActionButton.setTintPreLollipop(color: Int, @DrawableRes iconRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        setTint(color)
    }else{
        val icon = ContextCompat.getDrawable(context, iconRes)
        val copy = icon?.constantState?.newDrawable()
        copy?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        setImageDrawable(copy)
    }
}

fun FloatingActionButton.setTint(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        imageTintList = ColorStateList.valueOf(color)
}