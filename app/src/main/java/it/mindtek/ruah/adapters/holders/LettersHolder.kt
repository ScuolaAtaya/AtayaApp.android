package it.mindtek.ruah.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_letter_selected.view.*

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class LettersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val letter = itemView.letter
    val card = itemView.card
    val view = itemView
}