package it.mindtek.ruah.interfaces

/**
 * Created by alessandrogaboardi on 07/12/2017.
 */
interface UnderstandActivityInterface {
    fun openQuestion(questionIndex: Int, index: Int)
    fun goToFinish()
    fun goToVideo(index: Int)
}