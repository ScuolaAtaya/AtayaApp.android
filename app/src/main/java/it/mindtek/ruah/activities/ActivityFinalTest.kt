package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityFinalTestBinding
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.final_test.FragmentFinalTest
import it.mindtek.ruah.interfaces.FinalTestActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment

class ActivityFinalTest : AppCompatActivity(), FinalTestActivityInterface {
    private var unitId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityFinalTestBinding.inflate(layoutInflater)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
        }
        setup()
        replaceFragment(FragmentFinalTest.newInstance(unitId, 0), R.id.placeholder, false)
    }

    override fun goToNext(index: Int) {
        replaceFragment(FragmentFinalTest.newInstance(unitId, index), R.id.placeholder, true)
    }

    override fun goToFinish() {
        startActivity(Intent(this, ActivityIntro::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            putExtra(ActivityIntro.EXTRA_CATEGORY_ID, Category.FINAL_TEST.value)
            putExtra(ActivityIntro.EXTRA_IS_FINISH, true)
        })
    }

    @Suppress("DEPRECATION")
    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(Category.FINAL_TEST.title)
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this) {
            it?.let {
                supportActionBar?.setBackgroundDrawable(
                    ResourceProvider.getColor(this, it.name).toDrawable()
                )
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM)
                    window.statusBarColor = ResourceProvider.getColor(this, "${it.name}_dark")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}