package it.mindtek.ruah.activities

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.UnitsAdapter
import kotlinx.android.synthetic.main.activity_units.*

class ActivityUnits : AppCompatActivity() {
    private lateinit var adapter: UnitsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units)
        unitsRecycler.layoutManager = GridLayoutManager(this, 2)
        adapter = UnitsAdapter(this) {
            val intent = Intent(this@ActivityUnits, ActivityUnit::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, it.id)
            startActivity(intent)
        }
        unitsRecycler.adapter = adapter
        privacyPolicy.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        privacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.privacy_policy_url))
            })
        }
    }

    companion object {
        const val TIMESTAMP = "timestamp"
    }
}