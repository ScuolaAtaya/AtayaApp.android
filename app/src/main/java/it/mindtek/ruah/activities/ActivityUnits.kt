package it.mindtek.ruah.activities

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.UnitsAdapter
import it.mindtek.ruah.databinding.ActivityUnitsBinding

class ActivityUnits : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUnitsBinding = ActivityUnitsBinding.inflate(layoutInflater)
        val adapter = UnitsAdapter(this) {
            startActivity(Intent(this@ActivityUnits, ActivityUnit::class.java).apply {
                putExtra(ActivityUnit.EXTRA_UNIT_ID, it.id)
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

    companion object {
        const val TIMESTAMP = "timestamp"
    }
}