package it.mindtek.ruah.kotlin.extensions

import androidx.fragment.app.Fragment
import java.io.File

/**
 * Created by alessandrogaboardi on 14/12/2017.
 */
val Fragment.canAccessActivity: Boolean
    get() = !isRemoving && isAdded && !isDetached

val Fragment.fileFolder: File
    get() = File(activity?.filesDir, "data")