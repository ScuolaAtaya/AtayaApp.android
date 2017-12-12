package it.mindtek.ruah.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.holders.AnswerHolder
import it.mindtek.ruah.db.models.ModelAnswer

/**
 * Created by alessandrogaboardi on 07/12/2017.
 */
class AnswersAdapter(val answers: MutableList<ModelAnswer>): RecyclerView.Adapter<AnswerHolder>() {
    override fun onBindViewHolder(holder: AnswerHolder, position: Int) {
        holder.bind(answers[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_answer, parent, false)
        return AnswerHolder(view)
    }

    override fun getItemCount(): Int = answers.size
}