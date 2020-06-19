package it.mindtek.ruah.adapters.holders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible
import kotlinx.android.synthetic.main.item_answer.view.*

/**
 * Created by alessandrogaboardi on 07/12/2017.
 */
class AnswerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val wrong = itemView.wrong
    private val right = itemView.correct
    private val select = itemView.radioSelect
    private val answerText = itemView.answerText
    private val audio = itemView.answerAudio

    fun bind(answer: ModelAnswer, callback: ((answer: ModelAnswer) -> Unit)?, playAnswerCallback: ((answer: ModelAnswer) -> Unit)?) {
        answerText.text = answer.body
        select.setOnClickListener {
            select.setGone()
            if (answer.correct) {
                right.setVisible()
                callback?.invoke(answer)
            } else {
                wrong.setVisible()
                callback?.invoke(answer)
            }
        }
        audio.setOnClickListener {
            playAnswerCallback?.invoke(answer)
        }
    }
}