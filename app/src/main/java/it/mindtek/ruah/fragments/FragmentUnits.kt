package it.mindtek.ruah.fragments

import android.content.Intent
import android.content.res.Resources
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.activities.ActivityUnits
import it.mindtek.ruah.adapters.dividers.GridSpaceItemDecoration
import it.mindtek.ruah.config.LayoutUtils
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.FragmentUnitsBinding
import it.mindtek.ruah.databinding.ItemUnitBinding
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setBottomPadding
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible

class FragmentUnits : Fragment(), UnitsAdapter.OnClickListener {
    private lateinit var binding: FragmentUnitsBinding
    private lateinit var adapter: UnitsAdapter
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUnitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UnitsAdapter(this)
        binding.fragmentUnitsList.adapter = adapter
        binding.fragmentUnitsList.addItemDecoration(
            GridSpaceItemDecoration(
                LayoutUtils.dpToPx(requireActivity(), 16),
                LayoutUtils.dpToPx(requireActivity(), 16)
            )
        )
        binding.fragmentUnitsPrivacyPolicy.setBottomPadding()
        binding.fragmentUnitsPrivacyPolicy.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.fragmentUnitsPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.privacy_policy_url).toUri()
            })
        }
        getCategory()?.let { category: Category ->
            category.firstFunded?.let {
                binding.fragmentUnitsFirstFounded.setVisible()
                binding.fragmentUnitsFirstFounded.text = getString(it)
            } ?: binding.fragmentUnitsFirstFounded.setGone()
            category.firstFundedIcon?.let {
                binding.fragmentUnitsFirstFoundedIcon.setVisible()
                binding.fragmentUnitsFirstFoundedIcon.setImageResource(it)
                binding.fragmentUnitsFirstFoundedIcon.contentDescription =
                    category.firstFundedAlt?.let { alt: Int ->
                        getString(alt)
                    }
            } ?: binding.fragmentUnitsFirstFoundedIcon.setGone()
            category.secondFunded?.let {
                binding.fragmentUnitsSecondFounded.setVisible()
                binding.fragmentUnitsSecondFounded.text = getString(it)
            } ?: binding.fragmentUnitsSecondFounded.setGone()
            category.secondFundedIcon?.let {
                binding.fragmentUnitsSecondFoundedIcon.setVisible()
                binding.fragmentUnitsSecondFoundedIcon.setImageResource(it)
                binding.fragmentUnitsSecondFoundedIcon.contentDescription =
                    category.secondFundedAlt?.let { alt: Int ->
                        getString(alt)
                    }
            } ?: binding.fragmentUnitsSecondFoundedIcon.setGone()
            category.thirdFundedIcon?.let {
                binding.fragmentUnitsThirdFoundedIcon.setVisible()
                binding.fragmentUnitsThirdFoundedIcon.setImageResource(it)
                binding.fragmentUnitsThirdFoundedIcon.contentDescription =
                    category.thirdFundedAlt?.let { alt: Int ->
                        getString(alt)
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getCategory()?.let { category: Category ->
            db.unitDao().getUnitsByCategoryAsync(category).map {
                val list: MutableList<ModelUnitItem> = mutableListOf()
                it.map { unit: ModelUnit ->
                    try {
                        list.add(
                            ModelUnitItem(
                                unit.id,
                                getString(ResourceProvider.getString(requireActivity(), unit.name)),
                                unit.position,
                                unit.completed.size == 5,
                                ResourceProvider.getIcon(requireActivity(), unit.name),
                                ResourceProvider.getColor(requireActivity(), unit.name)
                            )
                        )
                    } catch (e: Resources.NotFoundException) {
                        Log.e("FragmentUnits", "Resources not found for unit: ${unit.name}", e)
                    }
                }
                list.sortedBy { item: ModelUnitItem ->
                    item.position
                }
            }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    adapter.submitList(it)
                }, {
                    Log.e("FragmentUnits", "Error loading categories or units", it)
                }).let {
                    disposable.add(it)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onItemClicked(unit: ModelUnitItem) {
        startActivity(Intent(requireActivity(), ActivityUnit::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unit.id)
        })
    }

    @Suppress("DEPRECATION")
    private fun getCategory(): Category? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requireArguments().getSerializable(ActivityUnits.CATEGORY_KEY, Category::class.java)
        else requireArguments().getSerializable(ActivityUnits.CATEGORY_KEY) as? Category

    companion object {
        fun newInstance(category: Category): FragmentUnits = FragmentUnits().apply {
            arguments = Bundle().apply {
                putSerializable(ActivityUnits.CATEGORY_KEY, category)
            }
        }
    }
}

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
            binding.root.setCardBackgroundColor(item.color)
            if (item.completed) binding.itemUnitCheck.setVisible() else binding.itemUnitCheck.setGone()
            binding.itemUnitImage.setImageResource(item.icon)
            binding.itemUnitTitle.text = item.title
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
    val title: String,
    val position: Int,
    val completed: Boolean,
    @param:DrawableRes val icon: Int,
    @param:ColorInt val color: Int
)