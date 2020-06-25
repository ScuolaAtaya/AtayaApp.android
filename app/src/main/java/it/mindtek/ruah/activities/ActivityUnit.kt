package it.mindtek.ruah.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import it.mindtek.ruah.R
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setVisible
import kotlinx.android.synthetic.main.activity_unit.*

class ActivityUnit : AppCompatActivity() {
    var unitId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit)
        intent?.let {
            unitId = it.getIntExtra(EXTRA_UNIT_ID, -1)
        }
        setup()
    }

    private fun setup() {
        if (unitId == -1) {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        capire.setOnClickListener {
            openIntro(Category.UNDERSTAND.value)
        }
        parlare.setOnClickListener {
            openIntro(Category.TALK.value)
        }
        leggere.setOnClickListener {
            openIntro(Category.READ.value)
        }
        scrivere.setOnClickListener {
            openIntro(Category.WRITE.value)
        }
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this, Observer { unit ->
            unit?.let { modelUnit ->
                if (modelUnit.completed.any { it == Category.UNDERSTAND.value }) {
                    capiamoDone.setVisible()
                }
                if (modelUnit.completed.any { it == Category.TALK.value }) {
                    parliamoDone.setVisible()
                }
                if (modelUnit.completed.any { it == Category.READ.value }) {
                    leggiamoDone.setVisible()
                }
                if (modelUnit.completed.any { it == Category.WRITE.value }) {
                    scriviamoDone.setVisible()
                }
                supportActionBar?.title = getString(unit.name)
                val color = ContextCompat.getColor(this, modelUnit.color)
                val colorDark = ContextCompat.getColor(this, modelUnit.colorDark)
                supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
                constraint.setBackgroundColor(color)
                if (Build.VERSION.SDK_INT >= 21) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = colorDark
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return false
    }

    private fun openIntro(category_id: Int) {
        val intent = Intent(this, ActivityIntro::class.java)
        intent.putExtra(EXTRA_UNIT_ID, unitId)
        intent.putExtra(ActivityIntro.EXTRA_CATEGORY_ID, category_id)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_UNIT_ID = "unit_id_extra"
    }
}