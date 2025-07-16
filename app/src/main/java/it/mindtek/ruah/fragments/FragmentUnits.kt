package it.mindtek.ruah.fragments

import android.content.Intent
import android.content.res.Resources
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.ModelUnitItem
import it.mindtek.ruah.adapters.UnitsAdapter
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.FragmentUnitsBinding
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setBottomPadding

class FragmentUnits : Fragment(), UnitsAdapter.OnClickListener {
    private lateinit var binding: FragmentUnitsBinding
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
        val adapter = UnitsAdapter(this)
        binding.fragmentUnitsList.adapter = adapter
        binding.fragmentUnitsPrivacyPolicy.setBottomPadding()
        binding.fragmentUnitsPrivacyPolicy.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.fragmentUnitsPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.privacy_policy_url).toUri()
            })
        }
        getCategory()?.let { category: String ->
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
                        Log.e("ActivityUnits", "Resources not found for unit: ${unit.name}", e)
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
                    Log.e("ActivityUnits", "Error loading categories or units", it)
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
    private fun getCategory(): String? = requireArguments().getString(CATEGORY_KEY)

    companion object {
        private const val CATEGORY_KEY: String = "category"
    }
}