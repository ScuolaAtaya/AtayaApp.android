package it.mindtek.ruah.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.UnitsAdapter
import kotlinx.android.synthetic.main.activity_units.*

class ActivityUnits : AppCompatActivity() {
    var adapter: UnitsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units)

        setup()
    }

    private fun setup(){
        setupRecycler()
    }

    private fun setupRecycler(){
        unitsRecycler.layoutManager = GridLayoutManager(this, 2)
        adapter = UnitsAdapter(this)
        unitsRecycler.adapter = adapter
    }
}
