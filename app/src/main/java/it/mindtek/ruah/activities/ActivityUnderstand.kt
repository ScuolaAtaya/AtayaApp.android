package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityUnderstandBinding
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.FragmentUnderstandVideo
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityUnderstand : AppCompatActivity() {
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var isVideoWatched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUnderstandBinding = ActivityUnderstandBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activityUnderstandToolbar.setTopPadding()
        setSupportActionBar(binding.activityUnderstandToolbar)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            stepIndex = it.getIntExtra(STEP_INDEX, -1)
            isVideoWatched = it.getBooleanExtra(VIDEO_WATCHED, false)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(
                    if (stepIndex == 0)
                        Intent(this@ActivityUnderstand, ActivityIntro::class.java).apply {
                            putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
                            putExtra(ActivityIntro.EXTRA_CATEGORY_ID, Category.UNDERSTAND.value)
                        }
                    else Intent(this@ActivityUnderstand, ActivityUnderstand::class.java).apply {
                        putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
                        putExtra(STEP_INDEX, stepIndex - 1)
                        putExtra(VIDEO_WATCHED, true)
                    })
                finish()
            }
        })
        setup()
        replaceFragment(
            FragmentUnderstandVideo.newInstance(unitId, stepIndex, isVideoWatched),
            R.id.activity_understand_placeholder,
            false
        )
    }

    @Suppress("DEPRECATION")
    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(Category.UNDERSTAND.title)
        db.unitDao().getUnitByIdAsync(unitId).observe(this) {
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

    companion object {
        const val STEP_INDEX: String = "step_index"
        const val VIDEO_WATCHED: String = "video_watched"
    }
}