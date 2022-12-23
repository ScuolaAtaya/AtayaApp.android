package it.mindtek.ruah.activities

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.fragments.write.FragmentWrite
import it.mindtek.ruah.interfaces.WriteActivityInterface
import it.mindtek.ruah.kotlin.extensions.compat21
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment

class ActivityWrite : AppCompatActivity(), WriteActivityInterface {
    var unitId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
        }
        setup()
        replaceFragment(FragmentWrite.newInstance(unitId, 0), R.id.placeholder, false)
    }

    override fun goToNext(index: Int) {
        replaceFragment(FragmentWrite.newInstance(unitId, index), R.id.placeholder, true)
    }

    override fun goToFinish() {
        val intent = Intent(this, ActivityIntro::class.java)
        intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        intent.putExtra(ActivityIntro.EXTRA_CATEGORY_ID, Category.WRITE.value)
        intent.putExtra(ActivityIntro.EXTRA_IS_FINISH, true)
        startActivity(intent)
    }

    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(Category.WRITE.title)
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this) {
            it?.let {
                supportActionBar?.setBackgroundDrawable(
                    ColorDrawable(ResourceProvider.getColor(this, it.name))
                )
                compat21(@TargetApi(21) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = ResourceProvider.getColor(this, "${it.name}_dark")
                }, {})
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return false
    }
}