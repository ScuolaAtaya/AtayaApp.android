package it.mindtek.ruah.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.holders.EmptyLetterHolder
import it.mindtek.ruah.adapters.holders.LettersHolder
import it.mindtek.ruah.pojos.Syllable

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class SelectedLettersAdapter(val word: String, val givenLetters: MutableList<Syllable>, val onLetterTap: (() -> Unit)?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var letters = MutableList(givenLetters.size, { "" })

    override fun getItemViewType(position: Int): Int {
        if (letters[position].isEmpty())
            return 0
        else
            return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_letter_empty, parent, false)
            return EmptyLetterHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_letter_selected, parent, false)
            return LettersHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 0) {

        } else {
            val cast = holder as LettersHolder
            val item = letters[position]
            cast.letter.text = item
        }
    }

    override fun getItemCount(): Int = letters.size

    fun select(letter: String) {
        val firstEmpty = letters.indexOfFirst { it.isEmpty() }
        if (firstEmpty > -1) {
            letters[firstEmpty] = letter
            notifyDataSetChanged()
        }
    }
}