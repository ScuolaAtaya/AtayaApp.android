package it.mindtek.ruah.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelMarker
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.kotlin.extensions.setVisible
import it.mindtek.ruah.pojos.PojoRead
import kotlinx.android.synthetic.main.item_option.view.*

class OptionsAdapter(
        val color: Int,
        val read: PojoRead,
        private val textChangedCallback: ((option: ModelReadOption, correct: Boolean) -> Unit)?,
        private val playOptionCallback: ((option: ModelReadOption) -> Unit)?
) : RecyclerView.Adapter<OptionHolder>() {
    private var markers: MutableList<ModelMarker> = read.read!!.markers
    private lateinit var options: MutableList<OptionRenderViewModel>

    init {
        read.options.forEach {
            options.add(OptionRenderViewModel(it, null, null))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return OptionHolder(view)
    }

    override fun getItemCount(): Int = options.size

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: OptionHolder, position: Int) {
        val option = options[position]
        holder.text.text = option.option.body
        holder.number.supportBackgroundTintList = ColorStateList.valueOf(color)
        holder.number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                holder.number.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                val index = markers.indexOfFirst {
                    it.id == s.toString()
                }
                if (s.toString() == option.option.markerId && index > -1) {
                    //holder.number.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(holder.itemView.context, R.drawable.done), null)
                    textChangedCallback?.invoke(option.option, true)
                } else {
                    textChangedCallback?.invoke(option.option, false)
                    if (s.toString().isNotEmpty()) {
                        //holder.number.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(holder.itemView.context, R.drawable.close), null)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        holder.audio.setOnClickListener {
            playOptionCallback?.invoke(option.option)
        }
        if (option.option.audio.credits.isNotBlank()) {
            holder.credits.setVisible()
            holder.credits.text = option.option.audio.credits
        }
    }

    fun completed(correctOptions: Int): Boolean {
        return correctOptions == markers.size
    }
}

data class OptionRenderViewModel(
        var option: ModelReadOption,
        var answer: String?,
        var correct: Boolean?
)

class OptionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val number: AppCompatEditText = itemView.editText
    val text: TextView = itemView.optionText
    val audio: ImageView = itemView.optionAudio
    val credits: TextView = itemView.optionCredits
}