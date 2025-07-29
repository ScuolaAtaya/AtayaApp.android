package it.mindtek.ruah.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.mindtek.ruah.R
import it.mindtek.ruah.databinding.ItemLetterSelectableBinding

class SelectableLettersAdapter(private val listener: OnClickListener) :
    ListAdapter<ModelSyllableItem, SelectableLettersAdapter.ItemViewHolder>(object :
        DiffUtil.ItemCallback<ModelSyllableItem>() {
        override fun areItemsTheSame(
            oldItem: ModelSyllableItem,
            newItem: ModelSyllableItem
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ModelSyllableItem,
            newItem: ModelSyllableItem
        ): Boolean = oldItem == newItem
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemLetterSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun unlockLetter(letter: ModelSyllableItem) {
        currentList.first {
            it.id == letter.id
        }.enabled = true
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: ItemLetterSelectableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelSyllableItem) {
            binding.letter.text = item.text
            setCardBackground(item.enabled)
            binding.root.setOnClickListener {
                if (item.enabled) {
                    item.enabled = false
                    setCardBackground(false)
                    listener.onLetterTapped(item)
                }
            }
        }

        private fun setCardBackground(enabled: Boolean) {
            binding.card.background =
                if (enabled) ContextCompat.getDrawable(binding.root.context, R.drawable.letter_card_blue)
                else ContextCompat.getDrawable(binding.root.context, R.drawable.letter_card_disabled)
        }
    }

    interface OnClickListener {
        fun onLetterTapped(item: ModelSyllableItem)
    }
}

data class ModelSyllableItem(
    var id: String,
    val text: String,
    val occurences: MutableList<Int>,
    var enabled: Boolean
)