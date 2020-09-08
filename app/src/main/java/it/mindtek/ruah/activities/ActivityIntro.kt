package it.mindtek.ruah.activities

import android.annotation.TargetApi
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import it.mindtek.ruah.R
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.*
import kotlinx.android.synthetic.main.activity_intro.*
import org.jetbrains.anko.dip

class ActivityIntro : AppCompatActivity() {
    private var unitId: Int = -1
    private var finish: Boolean = false
    private var category: Category? = null
    private lateinit var unitObject: ModelUnit
    private lateinit var player: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            category = Category.from(it.getIntExtra(EXTRA_CATEGORY_ID, -1))
            finish = it.getBooleanExtra(EXTRA_IS_FINISH, false)
        }
        setup()
    }

    private fun setup() {
        if (unitId == -1 || category == null) {
            finish()
        }
        player = MediaPlayer.create(this, category!!.audio)
        player.setOnCompletionListener {
            player.release()
        }
        if (finish) {
            buttonNext.setGone()
            done.setVisible()
            sectionDescription.text = getString(R.string.congrats)
            fabBack.setOnClickListener {
                completeCategory(category!!)
                goToCategory()
            }
        } else {
            done.setGone()
            fabBack.setOnClickListener {
                onBackPressed()
            }
            buttonNext.setVisible()
            buttonNext.setOnClickListener {
                dispatch()
            }
            sectionDescription.text = getString(category!!.description)
            player.start()
        }
        GlideApp.with(this).load(category!!.icon).override(dip(24), dip(24)).into(sectionIcon)
        sectionName.text = getString(category!!.title)
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this, Observer {
            it?.let {
                unitObject = it
                val color = ContextCompat.getColor(this, it.color)
                val colorDark = ContextCompat.getColor(this, it.colorDark)
                compat21(@TargetApi(21) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = colorDark
                }, {})
                coordinator.setBackgroundColor(color)
                fabBack.setTintPreLollipop(color, R.drawable.home)
                unitIcon.setImageResource(it.icon)
                val play = ContextCompat.getDrawable(this, R.drawable.play)
                buttonNext.setCompoundDrawables(null, null, play, null)
                buttonNext.setColor(color)
            }
        })
    }

    private fun completeCategory(category: Category) {
        unitObject.completed.add(category.value)
        db.unitDao().updateUnit(unitObject)
    }

    private fun dispatch() {
        player.release()
        when (category?.value) {
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
        val intent = Intent(this, ActivitySpeak::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        startActivity(intent)
    }

    private fun goToUnderstand() {
        val intent = Intent(this, ActivityUnderstand::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        intent.putExtra(ActivityUnderstand.STEP_INDEX, 0)
        startActivity(intent)
    }

    private fun goToRead() {
        val intent = Intent(this, ActivityRead::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        startActivity(intent)
    }

    private fun goToWrite() {
        val intent = Intent(this, ActivityWrite::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        startActivity(intent)
    }

    private fun goToFinalTest() {
        val intent = Intent(this, ActivityFinalTest::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToCategory()
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