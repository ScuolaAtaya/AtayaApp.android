package it.mindtek.ruah.adapters.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_option.view.*

class OptionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val number: AppCompatEditText = itemView.editText
    val text: TextView = itemView.optionText
    val audio: ImageView = itemView.optionAudio
    val credits: TextView = itemView.optionCredits
}