package it.mindtek.ruah.adapters.holders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_letter_selectable.view.*

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class SyllablesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val syllables = itemView.letter
    val background = itemView.card
    val view = itemView
}