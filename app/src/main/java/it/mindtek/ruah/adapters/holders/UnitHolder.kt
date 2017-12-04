package it.mindtek.ruah.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelUnit
import kotlinx.android.synthetic.main.recycler_item_unit.view.*

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class UnitHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val number = itemView.unitNumber
    val background = itemView.unitBackground
    val icon = itemView.unitIcon
    val text = itemView.unitText

    fun bind(unit: ModelUnit, onClick: ((unit: ModelUnit) -> Unit)?) {
        number.text = unit.position.toString()
        background.setBackgroundColor(ContextCompat.getColor(itemView.context, unit.color))
        GlideApp.with(itemView.context).load(unit.icon).into(icon)
        text.text = itemView.context.getString(unit.name)
        itemView.setOnClickListener {
            onClick?.invoke(unit)
        }
    }
}