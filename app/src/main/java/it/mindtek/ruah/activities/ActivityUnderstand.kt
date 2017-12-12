package it.mindtek.ruah.activities

import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.WindowManager
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.understand.FragmentUnderstandQuestions
import it.mindtek.ruah.fragments.understand.FragmentUnderstandVideo
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.*

class ActivityUnderstand : AppCompatActivity(), UnderstandActivityInterface {
    var unit_id: Int = -1
    var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_understand)

        unit_id = intent.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
        category = Category.from(intent.getIntExtra(ActivityIntro.EXTRA_CATEGORY_ID, -1))

        val fragment = FragmentUnderstandVideo.newInstance(unit_id)
        replaceFragment(fragment, R.id.placeholder, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        category?.let {
            supportActionBar?.title = getString(it.title).capitalize()
        }

        val unitObservable = db.unitDao().getUnitByIdAsync(unit_id)
        unitObservable.observe(this, Observer<ModelUnit> { unit ->
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

    override fun openQuestion(question: Int) {
        replaceFragment(FragmentUnderstandQuestions.newInstance(question, unit_id), R.id.placeholder)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return false
    }
}
