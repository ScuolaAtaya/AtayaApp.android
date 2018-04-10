package it.mindtek.ruah.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible
import it.mindtek.ruah.pojos.PojoQuestion
import kotlinx.android.synthetic.main.item_answer.view.*

/**
 * Created by alessandrogaboardi on 07/12/2017.
 */
class AnswerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val wrong = itemView.wrong
    val right = itemView.correct
    val select = itemView.radioSelect
    val answerText = itemView.answerText
    val audio = itemView.answerAudio

    fun bind(answer: ModelAnswer, callback: ((answer: ModelAnswer) -> Unit)?, playAnswerCallback: ((answer: ModelAnswer) -> Unit)?){
        answerText.text = answer.body
        select.setOnClickListener {
            select.setGone()
            if(answer.correct) {
                right.setVisible()
                callback?.invoke(answer)
            }else {
                wrong.setVisible()
                callback?.invoke(answer)
            }
        }
        audio.setOnClickListener {
            playAnswerCallback?.invoke(answer)
        }
    }
}