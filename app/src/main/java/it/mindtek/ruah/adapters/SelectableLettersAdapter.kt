package it.mindtek.ruah.adapters

import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.holders.SyllablesHolder
import it.mindtek.ruah.pojos.Syllable

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class SelectableLettersAdapter(val syllables: MutableList<Syllable>, val onLetterTap: ((syllable: Syllable) -> Unit)?) : RecyclerView.Adapter<SyllablesHolder>() {
    override fun getItemCount(): Int = syllables.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllablesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_letter_selectable, parent, false)
        return SyllablesHolder(view)
    }

    override fun onBindViewHolder(holder: SyllablesHolder, position: Int) {
        val syllable = syllables[position]
        holder.syllables.text = syllable.text
        if (syllable.enabled) {
            holder.background.background = ContextCompat.getDrawable(holder.view.context, R.drawable.card_blue)
        } else {
            holder.background.background = ContextCompat.getDrawable(holder.view.context, R.drawable.card_disabled)
        }
        holder.view.setOnClickListener {
            if (syllable.enabled) {
                syllable.enabled = false
                onLetterTap?.invoke(syllable)
                notifyDataSetChanged()
            }
        }
    }

    fun unlockLetter(letter: Syllable){
        syllables.first { it.id == letter.id }.enabled = true
        notifyDataSetChanged()
    }
}