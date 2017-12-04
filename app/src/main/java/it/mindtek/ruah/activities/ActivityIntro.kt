package it.mindtek.ruah.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import java.util.*

class ActivityIntro : AppCompatActivity() {
    var unit_id: Int = -1
    var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        intent?.let { intentNN ->
            unit_id = intentNN.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            category = Category.from(intentNN.getIntExtra(EXTRA_CATEGORY_ID, -1))
        }

        setup()
    }

    private fun setup(){

    }

    companion object {
        val EXTRA_CATEGORY_ID = "category_id"
    }
}
