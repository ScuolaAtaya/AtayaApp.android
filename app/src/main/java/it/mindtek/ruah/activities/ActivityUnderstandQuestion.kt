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
import it.mindtek.ruah.databinding.ActivityUnderstandQuestionBinding
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.FragmentUnderstandQuestions
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityUnderstandQuestion : AppCompatActivity(), UnderstandActivityInterface {
    private var unitId: Int = -1
    private var understandIndex: Int = -1
    private var questionIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUnderstandQuestionBinding =
            ActivityUnderstandQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activityUnderstandQuestionToolbar.setTopPadding()
        setSupportActionBar(binding.activityUnderstandQuestionToolbar)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            understandIndex = it.getIntExtra(ActivityUnderstand.STEP_INDEX, -1)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (questionIndex == 0) goToVideo(understandIndex, true) else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        setup()
        replaceFragment(
            FragmentUnderstandQuestions.newInstance(questionIndex, unitId, understandIndex),
            R.id.activity_understand_question_placeholder,
            false
        )
    }

    override fun goToNextQuestion(index: Int) {
        questionIndex = index
        replaceFragment(
            FragmentUnderstandQuestions.newInstance(questionIndex, unitId, understandIndex),
            R.id.activity_understand_question_placeholder,
            true
        )
    }

    override fun goToFinish() {
        startActivity(Intent(this, ActivityIntro::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            putExtra(ActivityIntro.EXTRA_CATEGORY_ID, Category.UNDERSTAND.value)
            putExtra(ActivityIntro.EXTRA_IS_FINISH, true)
        })
    }

    override fun goToVideo(index: Int, isVideoWatched: Boolean) {
        startActivity(Intent(this, ActivityUnderstand::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            putExtra(ActivityUnderstand.STEP_INDEX, index)
            putExtra(ActivityUnderstand.VIDEO_WATCHED, isVideoWatched)
        })
        finish()
    }

    @Suppress("DEPRECATION")
    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(Category.UNDERSTAND.title)
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