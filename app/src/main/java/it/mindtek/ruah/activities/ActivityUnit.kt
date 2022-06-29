package it.mindtek.ruah.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelUnit
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
        unitObservable.observe(this) {
            it?.let {
                val isUnderstandCompleted = isCategoryCompleted(it, Category.UNDERSTAND.value)
                val isSpeakingCompleted = isCategoryCompleted(it, Category.TALK.value)
                val isReadingCompleted = isCategoryCompleted(it, Category.READ.value)
                val isWritingCompleted = isCategoryCompleted(it, Category.WRITE.value)
                val isFinalTestCompleted = isCategoryCompleted(it, Category.FINAL_TEST.value)
                if (isUnderstandCompleted) capiamoDone.setVisible()
                if (isSpeakingCompleted) parliamoDone.setVisible()
                if (isReadingCompleted) leggiamoDone.setVisible()
                if (isWritingCompleted) scriviamoDone.setVisible()
                if (isFinalTestCompleted) verificaFinaleDone.setVisible()
                if (isUnderstandCompleted && isSpeakingCompleted && isReadingCompleted && isWritingCompleted) {
                    iconVerificaFinale.setImageResource(R.drawable.verifica_finale)
                    verificaFinaleText.setTextColor(ContextCompat.getColor(this, R.color.white))
                    verificaFinale.setOnClickListener {
                        openIntro(Category.FINAL_TEST.value)
                    }
                } else {
                    iconVerificaFinale.setImageResource(R.drawable.verifica_finale_disattivato)
                    verificaFinaleText.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.whiteAlpha50
                        )
                    )
                }
                supportActionBar?.title = getString(it.name)
                val color = ContextCompat.getColor(this, it.color)
                val colorDark = ContextCompat.getColor(this, it.colorDark)
                supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
                constraint.setBackgroundColor(color)
                if (Build.VERSION.SDK_INT >= 21) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = colorDark
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return false
    }

    private fun openIntro(categoryId: Int) {
        val intent = Intent(this, ActivityIntro::class.java)
        intent.putExtra(EXTRA_UNIT_ID, unitId)
        intent.putExtra(ActivityIntro.EXTRA_CATEGORY_ID, categoryId)
        startActivity(intent)
    }

    private fun isCategoryCompleted(modelUnit: ModelUnit, categoryId: Int): Boolean = modelUnit.completed.any {
        it == categoryId
    }

    companion object {
        const val EXTRA_UNIT_ID = "unit_id_extra"
    }
}