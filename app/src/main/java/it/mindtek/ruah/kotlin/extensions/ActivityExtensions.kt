package it.mindtek.ruah.kotlin.extensions

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

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