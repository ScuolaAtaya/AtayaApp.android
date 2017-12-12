package it.mindtek.ruah.activities

import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import it.mindtek.ruah.R
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.compat21
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setColor
import it.mindtek.ruah.kotlin.extensions.setTintPreLollipop
import kotlinx.android.synthetic.main.activity_intro.*
import org.jetbrains.anko.dip

class ActivityIntro : AppCompatActivity() {
    var unit_id: Int = -1
    var category: Category? = null
    var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        intent?.let { intentNN ->
            unit_id = intentNN.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            category = Category.from(intentNN.getIntExtra(EXTRA_CATEGORY_ID, -1))
        }

        setup()
    }

    private fun setup() {
        if (unit_id == -1 || category == null) {
            finish()
        }
        fabBack.setOnClickListener { onBackPressed() }
        buttonBack.setOnClickListener { goToUnderstand() }
        playAudio()
        GlideApp.with(this).load(category!!.icon).override(dip(24), dip(24)).into(sectionIcon)
        sectionName.text = getString(category!!.title)
        sectionDescription.text = getString(category!!.description)
        val unitObservable = db.unitDao().getUnitByIdAsync(unit_id)
        unitObservable.observe(this, Observer<ModelUnit> { unit ->
            unit?.let {
                val color = ContextCompat.getColor(this, unit.color)
                val colorDark = ContextCompat.getColor(this, unit.colorDark)
                compat21(@TargetApi(21) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = colorDark
                }, {})
                coordinator.setBackgroundColor(color)
                fabBack.setTintPreLollipop(color, R.drawable.home)
                unitIcon.setImageResource(unit.icon)
                val play = ContextCompat.getDrawable(this, R.drawable.play)
                buttonBack.setCompoundDrawables(null, null, play, null)
                buttonBack.setColor(color)
            }
        })
    }

    private fun playAudio() {
        player = MediaPlayer.create(this, category!!.audio)
        player?.setOnCompletionListener {
            destroyPlayer()
        }
        player?.start()
    }

    private fun goToUnderstand() {
        val intent = Intent(this, ActivityUnderstand::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unit_id)
        intent.putExtra(EXTRA_CATEGORY_ID, category?.value ?: -1)
        startActivity(intent)
    }

    private fun destroyPlayer() {
        player?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    companion object {
        val EXTRA_CATEGORY_ID = "category_id"
    }
}
