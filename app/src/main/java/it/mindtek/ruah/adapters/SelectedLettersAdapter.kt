package it.mindtek.ruah.adapters

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelSyllable
import kotlinx.android.synthetic.main.item_letter_selected.view.*

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class SelectedLettersAdapter(
        private val givenLetters: MutableList<ModelSyllable>,
        private val onLetterTap: ((syllable: ModelSyllable) -> Unit)?
) : RecyclerView.Adapter<ViewHolder>() {
    private var letters = MutableList(givenLetters.size) { "" }

    override fun getItemViewType(position: Int): Int = if (letters[position].isEmpty()) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = if (viewType == 0)
        EmptyLetterHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_letter_empty, parent, false))
    else LettersHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_letter_selected, parent, false))

    override fun getItemCount(): Int = letters.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType != 0) {
            holder as LettersHolder
            val item = letters[position]
            val syllable = givenLetters.first {
                it.id == item
            }
            val right = syllable.occurences.any {
                it == position
            }
            holder.card.background = if (right)
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.card_blue)
            else ContextCompat.getDrawable(holder.itemView.context, R.drawable.card_red)
            holder.letter.text = syllable.text
            holder.itemView.setOnClickListener {
                letters[position] = ""
                onLetterTap?.invoke(syllable)
                notifyDataSetChanged()
            }
        }
    }

    fun select(letter: ModelSyllable) {
        val firstEmpty = letters.indexOfFirst {
            it.isEmpty()
        }
        if (firstEmpty > -1) {
            letters[firstEmpty] = letter.id
            notifyDataSetChanged()
        }
    }

    fun completed(): Boolean {
        var wrong = false
        letters.forEachIndexed { index: Int, i: String ->
            val syllable = givenLetters.firstOrNull {
                it.id == i
            }
            if (syllable != null) {
                val noOccurrences = syllable.occurences.none {
                    it == index
                }
                if (noOccurrences) wrong = true
            } else wrong = true
        }
        return !wrong
    }
}

class LettersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val letter: TextView = itemView.letter
    val card: FrameLayout = itemView.card
}

class EmptyLetterHolder(itemView: View) : RecyclerView.ViewHolder(itemView)