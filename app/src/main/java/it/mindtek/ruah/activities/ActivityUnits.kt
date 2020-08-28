package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.UnitsAdapter
import kotlinx.android.synthetic.main.activity_units.*

class ActivityUnits : AppCompatActivity() {
    private var adapter: UnitsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units)
        setupRecycler()
    }

    private fun setupRecycler() {
        unitsRecycler.layoutManager = GridLayoutManager(this, 2)
        adapter = UnitsAdapter(this, {
            val intent = Intent(this@ActivityUnits, ActivityUnit::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, it.id)
            startActivity(intent)
        })
        unitsRecycler.adapter = adapter
    }

    companion object {
        const val TIMESTAMP = "timestamp"
    }
}