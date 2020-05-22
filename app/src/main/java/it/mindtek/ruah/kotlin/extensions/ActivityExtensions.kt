package it.mindtek.ruah.kotlin.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
fun AppCompatActivity.replaceFragment(fragment: Fragment, @IdRes placeholder: Int, backstack: Boolean = false){
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(placeholder, fragment)
    if(backstack)
        transaction.addToBackStack(fragment.javaClass.canonicalName)
    transaction.commit()
}