package it.mindtek.ruah.activities

import android.content.Intent
import android.content.res.Resources
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.ModelUnitItem
import it.mindtek.ruah.adapters.UnitsAdapter
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityMainBinding
import it.mindtek.ruah.db.models.ModelCategory
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setBottomPadding
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityMain : AppCompatActivity(), UnitsAdapter.OnClickListener {
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = UnitsAdapter(this)
        binding.root.setTopPadding()
        binding.unitsRecycler.adapter = adapter
        binding.privacyPolicy.setBottomPadding()
        binding.privacyPolicy.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.privacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.privacy_policy_url).toUri()
            })
        }
        Single.zip(
            db.categoryDao().getCategoriesAsync(),
            db.unitDao().getUnitsAsync()
        ) { categories: MutableList<ModelCategory>, units: MutableList<ModelUnit> ->
            Pair(categories, units)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map {
                val list: MutableList<ModelUnitItem> = mutableListOf()
                it.first.forEach { category: ModelCategory ->
                    val children: List<ModelUnit> = it.second.filter { unit: ModelUnit ->
                        unit.categoryId == category.id
                    }
                    try {
                        list.add(
                            ModelUnitItem(
                                category.id,
                                getString(ResourceProvider.getString(this, category.name)),
                                category.position,
                                children.all { unit: ModelUnit ->
                                    unit.completed.size == 5
                                },
                                children,
                                ResourceProvider.getIcon(this, category.name),
                                ResourceProvider.getColor(this, category.name)
                            )
                        )
                    } catch (e: Resources.NotFoundException) {
                        Log.e(
                            "ActivityUnits",
                            "Resources not found for category: ${category.name}",
                            e
                        )
                    }
                }
                it.second.filter { unit: ModelUnit ->
                    unit.categoryId == null
                }.forEach { unit: ModelUnit ->
                    try {
                        list.add(
                            ModelUnitItem(
                                unit.id,
                                getString(ResourceProvider.getString(this, unit.name)),
                                unit.position,
                                unit.completed.size == 5,
                                null,
                                ResourceProvider.getIcon(this, unit.name),
                                ResourceProvider.getColor(this, unit.name)
                            )
                        )
                    } catch (e: Resources.NotFoundException) {
                        Log.e("ActivityUnits", "Resources not found for unit: ${unit.name}", e)
                    }
                }
                list.sortedBy { item: ModelUnitItem ->
                    item.position
                }
            }.subscribe({
                adapter.submitList(it)
            }, {
                Log.e("ActivityUnits", "Error loading categories or units", it)
            }).let {
                disposable.add(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onItemClicked(unit: ModelUnitItem) {
        startActivity(Intent(this@ActivityMain, ActivityUnit::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unit.id)
        })
    }
}