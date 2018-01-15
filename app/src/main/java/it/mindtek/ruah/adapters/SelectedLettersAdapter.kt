package it.mindtek.ruah.adapters

import android.support.v4.content.ContextCompat
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
class SelectedLettersAdapter(val word: String, val givenLetters: MutableList<Syllable>, val onLetterTap: ((syllable: Syllable) -> Unit)?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var letters = MutableList(givenLetters.size, { -1 })

    override fun getItemViewType(position: Int): Int {
        if (letters[position] == -1)
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
            val syllable = givenLetters.first{ it.id == item }
            val right = syllable.order.any { it == position }
            if (!right) {
                cast.card.setCardBackgroundColor(ContextCompat.getColor(cast.card.context, R.color.red))
            } else {
                cast.card.setCardBackgroundColor(ContextCompat.getColor(cast.card.context, R.color.lavoro))
            }
            cast.letter.text = syllable.text
            cast.view.setOnClickListener {
                letters[position] = -1
                onLetterTap?.invoke(syllable)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = letters.size

    fun select(letter: Syllable) {
        val firstEmpty = letters.indexOfFirst { it == -1 }
        if (firstEmpty > -1) {
            letters[firstEmpty] = letter.id
            notifyDataSetChanged()
        }
    }

    fun completed(): Boolean {
        var wrong = false
        letters.forEachIndexed { index, i ->
            val syllable = givenLetters.firstOrNull { it.id == i }
            if(syllable != null) {
                if(syllable.order.none{ it == index}){
                    wrong = true
                }
            }else{
                wrong = true
            }
        }
        return !wrong
    }
}