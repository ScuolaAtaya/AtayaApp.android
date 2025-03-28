package it.mindtek.ruah.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible
import kotlinx.android.synthetic.main.recycler_item_unit.view.*

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
@SuppressLint("NotifyDataSetChanged")
class UnitsAdapter(owner: LifecycleOwner, private val onClick: ((unit: ModelUnit) -> Unit)?) :
    RecyclerView.Adapter<UnitHolder>() {
    private var units: MutableList<ModelUnit> = mutableListOf()

    init {
        try {
            val data = db.unitDao().getUnitsAsync()
            data.observe(owner) {
                it?.let { unitList: MutableList<ModelUnit> ->
                    units = unitList
                    notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitHolder = UnitHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_unit, parent, false)
    )

    override fun getItemCount(): Int = units.size

    override fun onBindViewHolder(holder: UnitHolder, position: Int) {
        val unit = units[position]
        holder.number.text = unit.position.toString()
        holder.background.setBackgroundColor(
            ResourceProvider.getColor(holder.itemView.context, unit.name)
        )
        if (unit.completed.size >= 5) holder.check.setVisible() else holder.check.setGone()
        @DrawableRes val icon: Int = ResourceProvider.getIcon(holder.itemView.context, unit.name)
        Glide.with(holder.itemView.context).load(icon).into(holder.icon)
        @StringRes val name: Int = ResourceProvider.getString(holder.itemView.context, unit.name)
        holder.text.text = holder.itemView.context.getString(name)
        holder.itemView.setOnClickListener {
            onClick?.invoke(unit)
        }
    }
}

class UnitHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val number: TextView = itemView.unitNumber
    val background: RelativeLayout = itemView.unitBackground
    val icon: ImageView = itemView.unitIcon
    val text: TextView = itemView.unitText
    val check: ImageView = itemView.check
}