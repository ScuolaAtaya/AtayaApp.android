package it.mindtek.ruah.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.mindtek.ruah.R
import it.mindtek.ruah.databinding.ItemOptionBinding
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible

class OptionsAdapter(
    private val context: Context,
    private val answers: MutableList<String>,
    private val listener: OnClickListener
) : ListAdapter<ModelOptionItem, OptionsAdapter.ItemViewHolder>(object :
    DiffUtil.ItemCallback<ModelOptionItem>() {
    override fun areItemsTheSame(oldItem: ModelOptionItem, newItem: ModelOptionItem): Boolean =
        oldItem.option.id == newItem.option.id

    override fun areContentsTheSame(oldItem: ModelOptionItem, newItem: ModelOptionItem): Boolean =
        oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun completed(): Boolean {
        currentList.forEach {
            it.correct = it.answer == it.option.markerId
        }
        return currentList.all {
            it.correct == true
        }
    }

    inner class ItemViewHolder(private val binding: ItemOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelOptionItem) {
            binding.number.text = item.answer
            binding.optionText.text = item.option.body
            binding.correct.setGone()
            binding.wrong.setGone()
            binding.spinner.setGone()
            when (item.correct) {
                true -> binding.correct.setVisible()
                false -> binding.wrong.setVisible()
                else -> binding.spinner.setVisible()
            }
            binding.optionAudio.setOnClickListener {
                listener.onPlayOptionClicked(item.option)
            }
            if (!item.option.audio.credits.isNullOrEmpty()) {
                binding.optionCredits.setVisible()
                binding.optionCredits.text = item.option.audio.credits
            }
            binding.numberClickableView.setOnClickListener {
                ListPopupWindow(context).apply {
                    setAdapter(
                        ArrayAdapter(context, R.layout.item_number, R.id.numberText, answers)
                    )
                    anchorView = binding.numberClickableView
                    setOnItemClickListener { _, _, position, _ ->
                        item.correct = null
                        item.answer = answers[position]
                        listener.onNumberChanged(currentList.count {
                            !it.answer.isNullOrEmpty()
                        })
                        dismiss()
                    }
                    show()
                }
            }
        }
    }

    interface OnClickListener {
        fun onNumberChanged(answersNumber: Int)
        fun onPlayOptionClicked(option: ModelReadOption)
    }
}

data class ModelOptionItem(val option: ModelReadOption, var answer: String?, var correct: Boolean?)