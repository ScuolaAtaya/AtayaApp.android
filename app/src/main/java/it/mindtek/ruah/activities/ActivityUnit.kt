package it.mindtek.ruah.activities

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.db
import kotlinx.android.synthetic.main.activity_unit.*
import org.jetbrains.anko.dip


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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        capire.setOnClickListener {
//            elevate(it)
            openIntro(Category.UNDERSTAND.value)
        }
        parlare.setOnClickListener {
//            elevate(it)
            openIntro(Category.TALK.value)
        }
        leggere.setOnClickListener {
//            elevate(it)
            openIntro(Category.READ.value)
        }
        scrivere.setOnClickListener {
//            elevate(it)
            openIntro(Category.WRITE.value)
        }

        val unitObservable = db.unitDao().getUnitByIdAsync(unit_id)
        unitObservable.observe(this, Observer<ModelUnit> { unit ->
            unit?.let {
                supportActionBar?.title = getString(unit.name).capitalize()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return false
    }

    fun elevate(view: View) {
        val layout = view as CardView
        layout.cardElevation = dip(8).toFloat()
    }

    private fun openIntro(category_id: Int) {
        val intent = Intent(this, ActivityIntro::class.java)
        intent.putExtra(EXTRA_UNIT_ID, unit_id)
        intent.putExtra(ActivityIntro.EXTRA_CATEGORY_ID, category_id)
        startActivity(intent)
    }

    companion object {
        val EXTRA_UNIT_ID = "unit_id_extra"
    }
}
