package it.mindtek.ruah.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible
import kotlinx.android.synthetic.main.item_answer.view.*

/**
 * Created by alessandrogaboardi on 07/12/2017.
 */
class AnswersAdapter(
    private val answers: MutableList<ModelAnswer>,
    private val answerSelectedCallback: ((answer: ModelAnswer) -> Unit)?,
    private val playAnswerCallback: ((answer: ModelAnswer) -> Unit)?
) : RecyclerView.Adapter<AnswerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerHolder = AnswerHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_answer, parent, false)
    )

    override fun getItemCount(): Int = answers.size

    override fun onBindViewHolder(holder: AnswerHolder, position: Int) {
        val answer = answers[position]
        holder.text.text = answer.body
        holder.select.setOnClickListener {
            holder.select.setGone()
            if (answer.correct) {
                holder.right.setVisible()
                answerSelectedCallback?.invoke(answer)
            } else {
                holder.wrong.setVisible()
                answerSelectedCallback?.invoke(answer)
            }
        }
        holder.audio.setOnClickListener {
            playAnswerCallback?.invoke(answer)
        }
        if (!answer.audio.credits.isNullOrBlank()) {
            holder.credits.setVisible()
            holder.credits.text = answer.audio.credits
        }
    }
}

class AnswerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val wrong: ImageView = itemView.wrong
    val right: ImageView = itemView.correct
    val select: RadioButton = itemView.radioSelect
    val text: TextView = itemView.answerText
    val audio: ImageView = itemView.answerAudio
    val credits: TextView = itemView.answerCredits
}