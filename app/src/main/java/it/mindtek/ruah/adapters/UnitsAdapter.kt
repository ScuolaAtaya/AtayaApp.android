package it.mindtek.ruah.adapters

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.holders.UnitHolder
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.kotlin.extensions.db

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class UnitsAdapter(owner: LifecycleOwner, private val onClick: ((unit: ModelUnit) -> Unit)?) : RecyclerView.Adapter<UnitHolder>() {
    private var units: MutableList<ModelUnit> = mutableListOf()

    init {
        try {
            val data = db.unitDao().getUnitsAsync()
            data.observe(owner, Observer {
                it?.let {
                    units = it
                    notifyDataSetChanged()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_unit, parent, false)
        return UnitHolder(view)
    }

    override fun onBindViewHolder(holder: UnitHolder, position: Int) {
        holder.bind(units[position], onClick)
    }

    override fun getItemCount(): Int = units.size
}