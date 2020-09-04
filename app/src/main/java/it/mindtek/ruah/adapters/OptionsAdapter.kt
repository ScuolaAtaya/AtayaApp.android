package it.mindtek.ruah.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.holders.OptionHolder
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.kotlin.extensions.setVisible
import kotlinx.android.synthetic.main.fragment_write.*

class OptionsAdapter(
        val color: Int,
        val options: MutableList<ModelReadOption>,
        private val textChangedCallback: ((option: ModelReadOption) -> Unit)?,
        private val playOptionCallback: ((option: ModelReadOption) -> Unit)?
) : RecyclerView.Adapter<OptionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return OptionHolder(view)
    }

    override fun getItemCount(): Int = options.size

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: OptionHolder, position: Int) {
        val option = options[position]
        holder.text.text = option.body
        holder.number.supportBackgroundTintList = ColorStateList.valueOf(color)
        holder.number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                holder.number.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                if (s.toString() == option.markerId) {
                    holder.number.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(holder.itemView.context, R.drawable.done), null)
                } else {
                    if (s.toString().isNotEmpty()) {
                        holder.number.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(holder.itemView.context, R.drawable.close), null)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        holder.audio.setOnClickListener {
            playOptionCallback?.invoke(option)
        }
        if (option.audio.credits.isNotBlank()) {
            holder.credits.setVisible()
            holder.credits.text = option.audio.credits
        }
    }
}