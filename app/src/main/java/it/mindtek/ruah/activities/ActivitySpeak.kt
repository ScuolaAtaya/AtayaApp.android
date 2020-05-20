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
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.speak.FragmentSpeak
import it.mindtek.ruah.interfaces.SpeakActivityInterface
import it.mindtek.ruah.kotlin.extensions.compat21
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment

class ActivitySpeak : AppCompatActivity(), SpeakActivityInterface {
    var unit_id: Int = -1
    var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speak)

        intent?.let { intentNN ->
            unit_id = intentNN.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            category = Category.from(intentNN.getIntExtra(ActivityIntro.EXTRA_CATEGORY_ID, -1))
        }

        if (unit_id == -1 || category == null)
            finish()

        setup()
        val fragment = FragmentSpeak.newInstance(unit_id, category!!, 0)
        replaceFragment(fragment, R.id.placeholder, false)
    }

    override fun goToSpeak(index: Int) {
        val fragment = FragmentSpeak.newInstance(unit_id, category!!, index)
        replaceFragment(fragment, R.id.placeholder)
    }

    override fun goToFinish() {
        val intent = Intent(this, ActivityIntro::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unit_id)
        intent.putExtra(ActivityIntro.EXTRA_CATEGORY_ID, category?.value ?: -1)
        intent.putExtra(ActivityIntro.EXTRA_IS_FINISH, true)
        startActivity(intent)
    }

    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(category!!.title).capitalize()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return false
    }
}
