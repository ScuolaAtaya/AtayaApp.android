package it.mindtek.ruah.kotlin.extensions

import android.support.v4.app.Fragment

/**
 * Created by alessandrogaboardi on 14/12/2017.
 */
val Fragment.canAccessActivity: Boolean get() {
    return !this.isRemoving && this.isAdded && !this.isDetached
}