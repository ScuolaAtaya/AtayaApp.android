package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivitySpeakBinding
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.speak.FragmentSpeak
import it.mindtek.ruah.interfaces.SpeakActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment

class ActivitySpeak : AppCompatActivity(), SpeakActivityInterface {
    private var unitId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivitySpeakBinding.inflate(layoutInflater)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
        }
        setup()
        replaceFragment(FragmentSpeak.newInstance(unitId, 0), R.id.placeholder, false)
    }

    override fun goToNext(index: Int) {
        replaceFragment(FragmentSpeak.newInstance(unitId, index), R.id.placeholder, true)
    }

    override fun goToFinish() {
        startActivity(Intent(this, ActivityIntro::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            putExtra(ActivityIntro.EXTRA_CATEGORY_ID, Category.TALK.value)
            putExtra(ActivityIntro.EXTRA_IS_FINISH, true)
        })
    }

    @Suppress("DEPRECATION")
    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(Category.TALK.title)
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