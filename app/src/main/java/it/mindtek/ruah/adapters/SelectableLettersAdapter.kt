package it.mindtek.ruah.adapters

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

    fun unlockLetter(letter: ModelSyllableItem) {
        currentList.first {
            it.id == letter.id
        }.enabled = true
    }

    inner class ItemViewHolder(private val binding: ItemLetterSelectableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelSyllableItem) {
            binding.letter.text = item.text
            binding.card.background = if (item.enabled)
                ContextCompat.getDrawable(binding.root.context, R.drawable.card_blue)
            else ContextCompat.getDrawable(binding.root.context, R.drawable.card_disabled)
            binding.root.setOnClickListener {
                if (item.enabled) {
                    item.enabled = false
                    listener.onLetterTapped(item)
                }
            }
        }
    }

    interface OnClickListener {
        fun onLetterTapped(item: ModelSyllableItem)
    }
}

data class ModelSyllableItem(var id: String, val text: String, var enabled: Boolean)