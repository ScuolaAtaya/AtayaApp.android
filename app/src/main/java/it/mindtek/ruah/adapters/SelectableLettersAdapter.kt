package it.mindtek.ruah.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelSyllable
import kotlinx.android.synthetic.main.item_letter_selectable.view.*

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
@SuppressLint("NotifyDataSetChanged")
class SelectableLettersAdapter(
        private val syllables: MutableList<ModelSyllable>,
        private val onLetterTap: ((syllable: ModelSyllable) -> Unit)?
) : RecyclerView.Adapter<SyllablesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllablesHolder = SyllablesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_letter_selectable, parent, false)
    )

    override fun getItemCount(): Int = syllables.size

    override fun onBindViewHolder(holder: SyllablesHolder, position: Int) {
        val syllable = syllables[position]
        holder.syllables.text = syllable.text
        holder.background.background = if (syllable.enabled)
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.card_blue)
        else ContextCompat.getDrawable(holder.itemView.context, R.drawable.card_disabled)
        holder.itemView.setOnClickListener {
            if (syllable.enabled) {
                syllable.enabled = false
                onLetterTap?.invoke(syllable)
                notifyDataSetChanged()
            }
        }
    }

    fun unlockLetter(letter: ModelSyllable) {
        syllables.first {
            it.id == letter.id
        }.enabled = true
        notifyDataSetChanged()
    }
}

class SyllablesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val syllables: TextView = itemView.letter
    val background: FrameLayout = itemView.card
}