package it.mindtek.ruah.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.mindtek.ruah.databinding.ItemAnswerBinding
import it.mindtek.ruah.db.models.ModelMedia
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible

class AnswersAdapter(private val listener: OnClickListener) :
    ListAdapter<ModelAnswerItem, AnswersAdapter.ItemViewHolder>(object :
        DiffUtil.ItemCallback<ModelAnswerItem>() {
        override fun areItemsTheSame(oldItem: ModelAnswerItem, newItem: ModelAnswerItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ModelAnswerItem,
            newItem: ModelAnswerItem
        ): Boolean = oldItem == newItem
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: ItemAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelAnswerItem) {
            binding.answerText.text = item.body
            binding.radioSelect.setOnClickListener {
                binding.radioSelect.setGone()
                if (item.correct) {
                    binding.correct.setVisible()
                    listener.onAnswerSelected(item)
                } else {
                    binding.wrong.setVisible()
                    listener.onAnswerSelected(item)
                }
            }
            binding.answerAudio.setOnClickListener {
                listener.onAnswerAudioClicked(item)
            }
            if (!item.audio.credits.isNullOrBlank()) {
                binding.answerCredits.setVisible()
                binding.answerCredits.text = item.audio.credits
            }
        }
    }

    interface OnClickListener {
        fun onAnswerSelected(answer: ModelAnswerItem)
        fun onAnswerAudioClicked(answer: ModelAnswerItem)
    }
}

data class ModelAnswerItem(
    val id: Long,
    val body: String,
    val audio: ModelMedia,
    val correct: Boolean
)