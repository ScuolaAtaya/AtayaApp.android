package it.mindtek.ruah.activities

import android.annotation.TargetApi
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import it.mindtek.ruah.R
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.final_test.FragmentFinalTest
import it.mindtek.ruah.kotlin.extensions.compat21
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment

class ActivityFinalTest : AppCompatActivity() {
    var unitId: Int = -1
    var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_test)
        unitId = intent.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
        category = Category.from(intent.getIntExtra(ActivityIntro.EXTRA_CATEGORY_ID, -1))
        setup()
        val fragment = FragmentFinalTest.newInstance(unitId, category!!, 0)
        replaceFragment(fragment, R.id.placeholder, false)
    }

    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(category!!.title)
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this, Observer { unit ->
            unit?.let {
                val color = ContextCompat.getColor(this, unit.color)
                val colorDark = ContextCompat.getColor(this, unit.colorDark)
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
}