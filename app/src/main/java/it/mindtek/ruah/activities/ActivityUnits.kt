package it.mindtek.ruah.activities

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.ModelUnitItem
import it.mindtek.ruah.adapters.UnitsAdapter
import it.mindtek.ruah.databinding.ActivityUnitsBinding
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.kotlin.extensions.db

class ActivityUnits : AppCompatActivity(), UnitsAdapter.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUnitsBinding = ActivityUnitsBinding.inflate(layoutInflater)
        val adapter = UnitsAdapter(this)
        db.unitDao().getUnitsAsync().observe(this) {
            adapter.submitList(it.map { unit: ModelUnit ->
                ModelUnitItem(unit.id, unit.name, unit.position, unit.completed)
            })
        }
        binding.unitsRecycler.adapter = adapter
        binding.privacyPolicy.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.privacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.privacy_policy_url).toUri()
            })
        }
    }

    override fun onItemClicked(unit: ModelUnitItem) {
        startActivity(Intent(this@ActivityUnits, ActivityUnit::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unit.id)
        })
    }

    companion object {
        const val TIMESTAMP = "timestamp"
    }
}