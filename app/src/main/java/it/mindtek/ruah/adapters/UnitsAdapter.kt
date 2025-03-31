package it.mindtek.ruah.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ItemUnitBinding
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible

class UnitsAdapter(private val listener: OnClickListener) :
    ListAdapter<ModelUnitItem, UnitsAdapter.ItemViewHolder>(object :
        DiffUtil.ItemCallback<ModelUnitItem>() {
        override fun areItemsTheSame(oldItem: ModelUnitItem, newItem: ModelUnitItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ModelUnitItem, newItem: ModelUnitItem): Boolean =
            oldItem == newItem
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: ItemUnitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelUnitItem) {
            binding.unitNumber.text = item.position.toString()
            binding.unitBackground.setBackgroundColor(
                ResourceProvider.getColor(binding.root.context, item.name)
            )
            if (item.completed.size >= 5) binding.check.setVisible() else binding.check.setGone()
            @DrawableRes val icon: Int = ResourceProvider.getIcon(binding.root.context, item.name)
            Glide.with(binding.root.context).load(icon).into(binding.unitIcon)
            @StringRes val name: Int = ResourceProvider.getString(binding.root.context, item.name)
            binding.unitText.text = binding.root.context.getString(name)
            binding.root.setOnClickListener {
                listener.onItemClicked(item)
            }
        }
    }

    interface OnClickListener {
        fun onItemClicked(unit: ModelUnitItem)
    }
}

data class ModelUnitItem(
    val id: Int,
    val name: String,
    val position: Int,
    val completed: MutableList<Int>
)