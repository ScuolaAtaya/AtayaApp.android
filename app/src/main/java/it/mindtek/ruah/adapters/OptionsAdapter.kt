package it.mindtek.ruah.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible
import kotlinx.android.synthetic.main.item_option.view.*

class OptionsAdapter(
        val context: Context,
        val options: MutableList<OptionRenderViewModel>,
        private val answers: MutableList<String>,
        private val numberChangedCallback: ((answersNumber: Int) -> Unit)?,
        private val playOptionCallback: ((option: ModelReadOption) -> Unit)?
) : RecyclerView.Adapter<OptionHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionHolder = OptionHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
    )

    override fun getItemCount(): Int = options.size

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: OptionHolder, position: Int) {
        val option = options[position]
        val readOption = option.option
        holder.number.text = option.answer
        holder.text.text = readOption.body
        holder.right.setGone()
        holder.wrong.setGone()
        holder.spinner.setGone()
        when (option.correct) {
            true -> holder.right.setVisible()
            false -> holder.wrong.setVisible()
            else -> holder.spinner.setVisible()
        }
        holder.audio.setOnClickListener {
            playOptionCallback?.invoke(readOption)
        }
        if (!readOption.audio.credits.isNullOrBlank()) {
            holder.credits.setVisible()
            holder.credits.text = readOption.audio.credits
        }
        holder.numberView.setOnClickListener {
            val listPopupWindow = ListPopupWindow(context)
            listPopupWindow.setAdapter(ArrayAdapter(context, R.layout.item_number, R.id.numberText, answers))
            listPopupWindow.anchorView = holder.numberView
            listPopupWindow.setOnItemClickListener { _, _, position, _ ->
                option.correct = null
                option.answer = answers[position]
                val answersNumber = options.count {
                    !it.answer.isNullOrBlank()
                }
                numberChangedCallback?.invoke(answersNumber)
                listPopupWindow.dismiss()
                notifyDataSetChanged()
            }
            listPopupWindow.show()
        }
    }

    fun completed(): Boolean {
        options.forEach {
            it.correct = it.answer == it.option.markerId
        }
        notifyDataSetChanged()
        return options.all {
            it.correct == true
        }
    }
}

data class OptionRenderViewModel(
        var option: ModelReadOption,
        var answer: String?,
        var correct: Boolean?
)

class OptionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val wrong: ImageView = itemView.wrong
    val right: ImageView = itemView.correct
    val spinner: AppCompatSpinner = itemView.spinner
    val number: TextView = itemView.number
    val numberView: View = itemView.numberClickableView
    val text: TextView = itemView.optionText
    val audio: ImageView = itemView.optionAudio
    val credits: TextView = itemView.optionCredits
}