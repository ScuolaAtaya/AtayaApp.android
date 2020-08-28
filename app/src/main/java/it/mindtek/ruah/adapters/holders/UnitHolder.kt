package it.mindtek.ruah.adapters.holders

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible
import kotlinx.android.synthetic.main.recycler_item_unit.view.*

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class UnitHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val number: TextView = itemView.unitNumber
    val background: RelativeLayout = itemView.unitBackground
    val icon: ImageView = itemView.unitIcon
    val text: TextView = itemView.unitText
    val check: ImageView = itemView.check

    fun bind(unit: ModelUnit, onClick: ((unit: ModelUnit) -> Unit)?) {
        number.text = unit.position.toString()
        background.setBackgroundColor(ContextCompat.getColor(itemView.context, unit.color))
        if (unit.completed.size >= 5) {
            check.setVisible()
        } else {
            check.setGone()
        }
        GlideApp.with(itemView.context).load(unit.icon).into(icon)
        text.text = itemView.context.getString(unit.name)
        itemView.setOnClickListener {
            onClick?.invoke(unit)
        }
    }
}