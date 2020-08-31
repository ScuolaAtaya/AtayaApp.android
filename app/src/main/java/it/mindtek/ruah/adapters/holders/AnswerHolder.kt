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
    private val text = itemView.answerText
    private val audio = itemView.answerAudio
    private val credits = itemView.answerCredits

    fun bind(answer: ModelAnswer, callback: ((answer: ModelAnswer) -> Unit)?, playAnswerCallback: ((answer: ModelAnswer) -> Unit)?) {
        text.text = answer.body
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
        if (answer.audio.credits.isNotBlank()) {
            credits.setVisible()
            credits.text = answer.audio.credits
        }
    }
}