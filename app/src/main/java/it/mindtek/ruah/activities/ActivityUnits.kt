package it.mindtek.ruah.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityUnitsBinding
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.FragmentUnits
import it.mindtek.ruah.kotlin.extensions.replaceFragment
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityUnits : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUnitsBinding = ActivityUnitsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activityUnitsToolbar.setTopPadding()
        setSupportActionBar(binding.activityUnitsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getCategory()?.let {
            supportActionBar?.title =
                getString(ResourceProvider.getString(this, it.name.lowercase()))
            replaceFragment(FragmentUnits.newInstance(it), R.id.activity_units_fragment_spot, false)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun getCategory(): Category? =
        IntentCompat.getSerializableExtra(intent, CATEGORY_KEY, Category::class.java)

    companion object {
        const val CATEGORY_KEY: String = "category_key"

        fun createIntent(context: Context, category: Category): Intent =
            Intent(context, ActivityUnits::class.java).apply {
                putExtra(CATEGORY_KEY, category)
            }
    }
}