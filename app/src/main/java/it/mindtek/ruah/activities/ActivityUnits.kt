package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.UnitsAdapter
import it.mindtek.ruah.ws.interfaces.NeedsUpdateInterface
import kotlinx.android.synthetic.main.activity_units.*
import okhttp3.ResponseBody
import org.jetbrains.anko.defaultSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ActivityUnits : AppCompatActivity(), Callback<ResponseBody> {
    private val TIMESTAMP = "timestamp"

    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
        setup()
    }

    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            response.body()?.let {
                val result = String(it.bytes())
                println(result)
                if (result == "true") {
                    download()
                } else {
                    setup()
                }
            } ?: setup()
        } else {
            setup()
        }
    }

    var adapter: UnitsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units)

        checkUpdates()
    }

    private fun checkUpdates() {
        val base_url = getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder()
                .baseUrl(base_url)
                .build()

        val retrofitInterface = retrofit.create(NeedsUpdateInterface::class.java)

        val timestamp = defaultSharedPreferences.getLong(TIMESTAMP, 0)
        println(timestamp)
        val request = retrofitInterface.needsUpdate(timestamp)
        request.enqueue(this)
    }

    private fun download() {
        val intent = Intent(this, ActivityDownload::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
    }

    private fun setup() {
        setupRecycler()
    }

    private fun setupRecycler() {
        unitsRecycler.layoutManager = GridLayoutManager(this, 2)
        adapter = UnitsAdapter(this, { unit ->
            val intent = Intent(this@ActivityUnits, ActivityUnit::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unit.id)
            startActivity(intent)
        })
        unitsRecycler.adapter = adapter
    }
}
