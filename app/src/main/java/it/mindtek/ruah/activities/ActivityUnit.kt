package it.mindtek.ruah.activities

import android.arch.lifecycle.Observer
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.kotlin.extensions.db
import android.view.WindowManager
import android.os.Build
import kotlinx.android.synthetic.main.activity_unit.*


class ActivityUnit : AppCompatActivity() {
    var unit_id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit)

        unit_id = intent.getIntExtra(EXTRA_UNIT_ID, -1)
        setup()
    }

    private fun setup() {
        if (unit_id == -1) {
            finish()
        }

        val unitObservable = db.unitDao().getUnitByIdAsync(unit_id)
        unitObservable.observe(this, Observer<ModelUnit> { unit ->
            unit?.let {
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
        })
    }

    companion object {
        val EXTRA_UNIT_ID = "unit_id_extra"
    }
}
