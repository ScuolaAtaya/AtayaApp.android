package it.mindtek.ruah.adapters

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.holders.EmptyLetterHolder
import it.mindtek.ruah.adapters.holders.LettersHolder
import it.mindtek.ruah.pojos.Syllable

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class SelectedLettersAdapter(
        private val givenLetters: MutableList<Syllable>,
        private val onLetterTap: ((syllable: Syllable) -> Unit)?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var letters = MutableList(givenLetters.size) { "" }

    override fun getItemViewType(position: Int): Int {
        return if (letters[position].isEmpty())
            0
        else
            1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_letter_empty, parent, false)
            EmptyLetterHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_letter_selected, parent, false)
            LettersHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType != 0) {
            val cast = holder as LettersHolder
            val item = letters[position]
            val syllable = givenLetters.first { it.id == item }
            val right = syllable.occurences.any { it == position }
            if (!right) {
                cast.card.background = ContextCompat.getDrawable(holder.view.context, R.drawable.card_red)
            } else {
                cast.card.background = ContextCompat.getDrawable(holder.view.context, R.drawable.card_blue)
            }
            cast.letter.text = syllable.text
            cast.view.setOnClickListener {
                letters[position] = ""
                onLetterTap?.invoke(syllable)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = letters.size

    fun select(letter: Syllable) {
        val firstEmpty = letters.indexOfFirst { it.isEmpty() }
        if (firstEmpty > -1) {
            letters[firstEmpty] = letter.id
            notifyDataSetChanged()
        }
    }

    fun completed(): Boolean {
        var wrong = false
        letters.forEachIndexed { index, i ->
            val syllable = givenLetters.firstOrNull { it.id == i }
            if (syllable != null) {
                if (syllable.occurences.none { it == index }) {
                    wrong = true
                }
            } else {
                wrong = true
            }
        }
        return !wrong
    }
}