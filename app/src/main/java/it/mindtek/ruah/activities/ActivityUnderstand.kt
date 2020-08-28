package it.mindtek.ruah.activities

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import it.mindtek.ruah.R
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.understand.FragmentUnderstandVideo
import it.mindtek.ruah.kotlin.extensions.compat21
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment

class ActivityUnderstand : AppCompatActivity() {
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var isVideoWatched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_understand)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            stepIndex = it.getIntExtra(STEP_INDEX, -1)
            isVideoWatched = it.getBooleanExtra(VIDEO_WATCHED, false)
        }
        setup()
        val fragment = FragmentUnderstandVideo.newInstance(unitId, stepIndex, isVideoWatched)
        replaceFragment(fragment, R.id.placeholder, false)
    }

    private fun setup() {
        if (unitId == -1) {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(Category.UNDERSTAND.title)
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this, Observer {
            it?.let {
                val color = ContextCompat.getColor(this, it.color)
                val colorDark = ContextCompat.getColor(this, it.colorDark)
                supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
                compat21(@TargetApi(21) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = colorDark
                }, {})
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent: Intent
        if (stepIndex == 0) {
            intent = Intent(this, ActivityIntro::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            intent.putExtra(ActivityIntro.EXTRA_CATEGORY_ID, Category.UNDERSTAND.value)
        } else {
            intent = Intent(this, ActivityUnderstand::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            intent.putExtra(STEP_INDEX, stepIndex - 1)
            intent.putExtra(VIDEO_WATCHED, true)
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val STEP_INDEX = "step_index"
        const val VIDEO_WATCHED = "video_watched"
    }
}