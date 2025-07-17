package it.mindtek.ruah.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import it.mindtek.ruah.R
import it.mindtek.ruah.databinding.ItemLetterEmptyBinding
import it.mindtek.ruah.databinding.ItemLetterSelectableBinding

@SuppressLint("NotifyDataSetChanged")
class SelectedLettersAdapter(
    private val givenLetters: MutableList<ModelSyllableItem>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<ViewHolder>() {
    private var letters: MutableList<String> = MutableList(givenLetters.size) { "" }

    override fun getItemViewType(position: Int): Int = if (letters[position].isEmpty()) 0 else 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        if (viewType == 0) EmptyLetterHolder(
            ItemLetterEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        else ItemViewHolder(
            ItemLetterSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = letters.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType != 0) (holder as ItemViewHolder).bind(letters[position], position)
    }

    fun select(letter: ModelSyllableItem) {
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

    inner class ItemViewHolder(private val binding: ItemLetterSelectableBinding) :
        ViewHolder(binding.root) {
        fun bind(item: String, position: Int) {
            val syllable = givenLetters.first {
                it.id == item
            }
            val right = syllable.occurences.any {
                it == position
            }
            binding.card.background = if (right)
                ContextCompat.getDrawable(binding.root.context, R.drawable.letter_card_blue)
            else ContextCompat.getDrawable(binding.root.context, R.drawable.letter_card_red)
            binding.letter.text = syllable.text
            binding.root.setOnClickListener {
                letters[position] = ""
                listener.onLetterTapped(syllable)
                notifyDataSetChanged()
            }
        }
    }

    inner class EmptyLetterHolder(binding: ItemLetterEmptyBinding) : ViewHolder(binding.root)

    interface OnClickListener {
        fun onLetterTapped(item: ModelSyllableItem)
    }
}