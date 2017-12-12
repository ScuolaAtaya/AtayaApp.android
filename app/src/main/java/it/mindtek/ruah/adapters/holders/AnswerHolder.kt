package it.mindtek.ruah.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.pojos.PojoQuestion
import kotlinx.android.synthetic.main.item_answer.view.*

/**
 * Created by alessandrogaboardi on 07/12/2017.
 */
class AnswerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val wrong = itemView.wrong
    val right = itemView.correct
    val answer = itemView.answerText
    val audio = itemView.answerAudio

    fun bind(answer: ModelAnswer){

    }
}