package it.mindtek.ruah.adapters.holders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.item_letter_selected.view.*

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class LettersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val letter: TextView = itemView.letter
    val card: FrameLayout = itemView.card
    val view = itemView
}