package it.mindtek.ruah.activities

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityMainBinding
import it.mindtek.ruah.databinding.ItemCategoryBinding
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setBottomPadding
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setTopPadding
import it.mindtek.ruah.kotlin.extensions.setVisible

class ActivityMain : AppCompatActivity(), CategoryAdapter.OnClickListener {
    private lateinit var adapter: CategoryAdapter
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activityMainTitle.setTopPadding()
        adapter = CategoryAdapter(this)
        binding.activityMainList.adapter = adapter
        binding.activityMainPrivacyPolicy.setBottomPadding()
        binding.activityMainPrivacyPolicy.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.activityMainPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.privacy_policy_url).toUri()
            })
        }
    }

    override fun onStart() {
        super.onStart()
        db.unitDao().getUnitsAsync().map {
            Category.entries.map { category: Category ->
                val categoryName = category.name.lowercase()
                ModelCategoryItem(
                    category = category,
                    title = getString(ResourceProvider.getString(this, categoryName)),
                    completed = it.filter { unit: ModelUnit ->
                        unit.category == category
                    }.all { unit: ModelUnit ->
                        unit.completed.size == 5
                    },
                    icon = ResourceProvider.getIcon(this, categoryName),
                    color = ResourceProvider.getColor(this, categoryName)
                )
            }
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                adapter.submitList(it)
            }, {
                Log.e("ActivityMain", "Error loading categories or units", it)
            }).let {
                disposable.add(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onItemClicked(item: Category) {
        startActivity(ActivityUnits.createIntent(this, item))
    }
}

class CategoryAdapter(private val listener: OnClickListener) :
    ListAdapter<ModelCategoryItem, CategoryAdapter.ItemViewHolder>(object :
        DiffUtil.ItemCallback<ModelCategoryItem>() {
        override fun areItemsTheSame(
            oldItem: ModelCategoryItem,
            newItem: ModelCategoryItem
        ): Boolean {
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(
            oldItem: ModelCategoryItem,
            newItem: ModelCategoryItem
        ): Boolean {
            return oldItem == newItem
        }
    }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelCategoryItem) {
            binding.root.setCardBackgroundColor(item.color)
            if (item.completed) binding.itemCategoryCheck.setVisible() else binding.itemCategoryCheck.setGone()
            binding.itemCategoryImage.setImageResource(item.icon)
            binding.itemCategoryTitle.text = item.title
            binding.root.setOnClickListener {
                listener.onItemClicked(item.category)
            }
        }
    }

    interface OnClickListener {
        fun onItemClicked(item: Category)
    }
}

data class ModelCategoryItem(
    val category: Category,
    val title: String,
    val completed: Boolean,
    @param:DrawableRes val icon: Int,
    @param:ColorInt val color: Int
)