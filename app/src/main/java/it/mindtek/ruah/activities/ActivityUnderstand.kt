package it.mindtek.ruah.activities

import android.annotation.TargetApi
import androidx.lifecycle.Observer
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.WindowManager
import it.mindtek.ruah.R
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.understand.FragmentUnderstandQuestions
import it.mindtek.ruah.fragments.understand.FragmentUnderstandVideo
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.compat21
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment

class ActivityUnderstand : AppCompatActivity(), UnderstandActivityInterface {
    var unitId: Int = -1
    var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_understand)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            category = Category.from(it.getIntExtra(ActivityIntro.EXTRA_CATEGORY_ID, -1))
        }
        setup()
        val fragment = FragmentUnderstandVideo.newInstance(unitId)
        replaceFragment(fragment, R.id.placeholder, false)
    }

    override fun openQuestion(question: Int) {
        replaceFragment(FragmentUnderstandQuestions.newInstance(question, unitId), R.id.placeholder)
    }

    override fun goToStart() {
        val manager = supportFragmentManager
        for (i in 0..manager.backStackEntryCount) {
            manager.popBackStack()
        }
        replaceFragment(FragmentUnderstandVideo.newInstance(unitId), R.id.placeholder, false)
    }

    override fun goToFinish() {
        val intent = Intent(this, ActivityIntro::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        intent.putExtra(ActivityIntro.EXTRA_CATEGORY_ID, category?.value ?: -1)
        intent.putExtra(ActivityIntro.EXTRA_IS_FINISH, true)
        startActivity(intent)
    }

    private fun setup() {
        if (unitId == -1 || category == null) {
            finish()
        }
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