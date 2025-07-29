package it.mindtek.ruah.kotlin.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    @IdRes placeholder: Int,
    backStack: Boolean = false
) {
    supportFragmentManager.beginTransaction().apply {
        replace(placeholder, fragment)
        if (backStack) addToBackStack(fragment.javaClass.canonicalName)
        commit()
    }
}