package it.mindtek.ruah.adapters.holders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.item_letter_selectable.view.*

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class SyllablesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val syllables: TextView = itemView.letter
    val background: FrameLayout = itemView.card
    val view = itemView
}