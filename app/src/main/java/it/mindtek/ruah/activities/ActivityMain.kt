package it.mindtek.ruah.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityMainBinding
import it.mindtek.ruah.databinding.CategoryItemBinding
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.setBottomPadding
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityMain : AppCompatActivity(), CategoryAdapter.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activityMainTitle.setTopPadding()
        val adapter = CategoryAdapter(this, this)
        binding.activityMainList.adapter = adapter
        adapter.submitList(Category.entries)
        binding.activityMainPrivacyPolicy.setBottomPadding()
        binding.activityMainPrivacyPolicy.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.activityMainPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.privacy_policy_url).toUri()
            })
        }
    }

    override fun onItemClicked(item: Category) {
        TODO("Not yet implemented")
    }
}

class CategoryAdapter(private val context: Context, private val listener: OnClickListener) :
    ListAdapter<Category, CategoryAdapter.ItemViewHolder>(object :
        DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            val category: String = item.name.lowercase()
            binding.root.backgroundTintList =
                ColorStateList.valueOf(ResourceProvider.getColor(context, category))
            binding.categoryItemImage.setImageResource(ResourceProvider.getIcon(context, category))
            binding.categoryItemTitle.text =
                context.getString(ResourceProvider.getString(context, category))
            binding.root.setOnClickListener {
                listener.onItemClicked(item)
            }
        }
    }

    interface OnClickListener {
        fun onItemClicked(item: Category)
    }
}