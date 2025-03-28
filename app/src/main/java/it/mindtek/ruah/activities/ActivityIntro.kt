package it.mindtek.ruah.activities

import android.annotation.TargetApi
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import it.mindtek.ruah.R
import it.mindtek.ruah.config.LayoutUtils
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.*
import kotlinx.android.synthetic.main.activity_intro.*

class ActivityIntro : AppCompatActivity() {
    private var unitId: Int = -1
    private var finish: Boolean = false
    private lateinit var category: Category
    private lateinit var unitObject: ModelUnit
    private lateinit var player: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            category = Category.from(it.getIntExtra(EXTRA_CATEGORY_ID, -1))!!
            finish = it.getBooleanExtra(EXTRA_IS_FINISH, false)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToCategory()
            }
        })
        setup()
    }

    private fun setup() {
        player = MediaPlayer.create(this, category.audio)
        player.setOnCompletionListener {
            player.release()
        }
        if (finish) {
            buttonNext.setGone()
            done.setVisible()
            sectionDescription.text = getString(R.string.congrats)
            fabBack.setOnClickListener {
                completeCategory(category)
                goToCategory()
            }
        } else {
            done.setGone()
            fabBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            buttonNext.setVisible()
            buttonNext.setOnClickListener {
                dispatch()
            }
            sectionDescription.text = getString(category.description)
            player.start()
        }
        Glide.with(this).load(category.icon)
            .override(LayoutUtils.dpToPx(this, 24), LayoutUtils.dpToPx(this, 24)).into(sectionIcon)
        sectionName.text = getString(category.title)
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this) {
            it?.let {
                unitObject = it
                @ColorInt val color: Int = ResourceProvider.getColor(this, it.name)
                compat21(@TargetApi(21) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = ResourceProvider.getColor(this, "${it.name}_dark")
                }, {})
                coordinator.setBackgroundColor(color)
                fabBack.setTintPreLollipop(color, R.drawable.home)
                unitIcon.setImageResource(ResourceProvider.getIcon(this, it.name))
                val play = ContextCompat.getDrawable(this, R.drawable.play)
                buttonNext.setCompoundDrawables(null, null, play, null)
                buttonNext.setColor(color)
            }
        }
    }

    private fun completeCategory(category: Category) {
        unitObject.completed.add(category.value)
        db.unitDao().updateUnit(unitObject)
    }

    private fun dispatch() {
        when (category.value) {
            Category.UNDERSTAND.value -> goToUnderstand()
            Category.TALK.value -> goToSpeak()
            Category.READ.value -> goToRead()
            Category.WRITE.value -> goToWrite()
            Category.FINAL_TEST.value -> goToFinalTest()
        }
    }

    private fun goToCategory() {
        val intent = Intent(this, ActivityUnit::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        startActivity(intent)
        finish()
    }

    private fun goToSpeak() {
        if (check(db.speakDao().countByUnitId(unitId))) {
            val intent = Intent(this, ActivitySpeak::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            startActivity(intent)
        }
    }

    private fun goToUnderstand() {
        if (check(db.understandDao().countByUnitId(unitId))) {
            val intent = Intent(this, ActivityUnderstand::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            intent.putExtra(ActivityUnderstand.STEP_INDEX, 0)
            startActivity(intent)
        }
    }

    private fun goToRead() {
        if (check(db.readDao().countByUnitId(unitId))) {
            val intent = Intent(this, ActivityRead::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            startActivity(intent)
        }
    }

    private fun goToWrite() {
        if (check(db.writeDao().countByUnitId(unitId))) {
            val intent = Intent(this, ActivityWrite::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            startActivity(intent)
        }
    }

    private fun goToFinalTest() {
        if (check(db.finalTestDao().countByUnitId(unitId))) {
            val intent = Intent(this, ActivityFinalTest::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            startActivity(intent)
        }
    }

    private fun check(count: Int): Boolean {
        if (count == 0) {
            Snackbar.make(coordinator, R.string.category_empty_error, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.red)).show()
            return false
        }
        player.release()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    companion object {
        const val EXTRA_CATEGORY_ID = "category_id"
        const val EXTRA_IS_FINISH = "extra_is_finish_section"
    }
}