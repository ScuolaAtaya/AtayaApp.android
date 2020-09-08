package it.mindtek.ruah.fragments.write

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.SelectableLettersAdapter
import it.mindtek.ruah.adapters.SelectedLettersAdapter
import it.mindtek.ruah.adapters.dividers.GridSpaceItemDecoration
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelWrite
import it.mindtek.ruah.interfaces.WriteActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import kotlinx.android.synthetic.main.fragment_write.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import java.io.File
import java.util.*

class FragmentWrite : Fragment() {
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var write: MutableList<ModelWrite> = mutableListOf()
    private lateinit var player: MediaPlayer
    private lateinit var selectedAdapter: SelectedLettersAdapter
    private lateinit var selectableAdapter: SelectableLettersAdapter
    private lateinit var communicator: WriteActivityInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID)) {
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            }
            if (it.containsKey(EXTRA_STEP)) {
                stepIndex = it.getInt(EXTRA_STEP)
            }
        }
        setup()
    }

    @SuppressLint("RestrictedApi")
    private fun setup() {
        if (unitId == -1 || stepIndex == -1) {
            requireActivity().finish()
        }
        if (requireActivity() is WriteActivityInterface) {
            communicator = requireActivity() as WriteActivityInterface
        }
        write = db.writeDao().getWriteByUnitId(unitId)
        if (write.size == 0 || write.size <= stepIndex) {
            requireActivity().finish()
        }
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(requireActivity(), it.color)
            stepLayout.backgroundColor = color
            editText.supportBackgroundTintList = ColorStateList.valueOf(color)
            audioButton.supportBackgroundTintList = ColorStateList.valueOf(color)
        }
        setupAudio()
        setupPicture()
        setupButtons()
        setupSteps()
        if (write[stepIndex].type == BASIC) {
            setupBasic()
            setupRecyclers()
        } else {
            setupAdvanced()
        }
    }

    private fun setupAudio() {
        val audio = write[stepIndex].audio
        val audioFile = File(fileFolder.absolutePath, audio.value)
        player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
        player.setOnCompletionListener {
            if (canAccessActivity) {
                player.pause()
            }
        }
        audioButton.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.start()
            }
        }
        if (audio.credits.isNotBlank()) {
            audioCredits.setVisible()
            audioCredits.text = audio.credits
        }
    }

    private fun setupPicture() {
        val picture = write[stepIndex].picture
        val pictureFile = File(fileFolder.absolutePath, picture.value)
        GlideApp.with(this).load(pictureFile).placeholder(R.color.grey).into(stepImage)
        if (picture.credits.isNotBlank()) {
            stepImageCredits.setVisible()
            stepImageCredits.text = picture.credits
        }
    }

    private fun setupButtons() {
        next.setOnClickListener {
            player.release()
            if (stepIndex + 1 < write.size) {
                communicator.goToNext(stepIndex + 1)
            } else {
                communicator.goToFinish()
            }
        }
        next.disable()
    }

    @SuppressLint("SetTextI18n")
    private fun setupSteps() {
        step.text = "${stepIndex + 1}/${write.size}"
    }

    private fun setupBasic() {
        editText.setGone()
        compile.setVisible()
        available.setVisible()
    }

    private fun setupAdvanced() {
        compile.setGone()
        available.setGone()
        editText.setVisible()
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearDrawable()
                if (setLowerCase(s.toString()) == setLowerCase(write[stepIndex].word)) {
                    showRight()
                    next.enable()
                } else {
                    if (s.toString().isNotEmpty()) {
                        showError()
                    }
                    next.disable()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun clearDrawable() {
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    fun showError() {
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireActivity(), R.drawable.close), null)
    }

    fun showRight() {
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireActivity(), R.drawable.done), null)
    }

    private fun setupRecyclers() {
        val stepWrite = write[stepIndex]
        val selectableCol = calculateSelectableColumns()
        val selectedCol = calculateColumns()
        val selectedSpanCount = if (stepWrite.letters.size >= selectedCol) {
            selectedCol
        } else {
            stepWrite.letters.size
        }
        val selectableSpanCount = if (stepWrite.letters.size >= selectableCol) {
            selectableCol
        } else {
            stepWrite.letters.size
        }
        compile.layoutManager = GridLayoutManager(requireActivity(), selectedSpanCount)
        available.layoutManager = GridLayoutManager(requireActivity(), selectableSpanCount)
        selectedAdapter = SelectedLettersAdapter(stepWrite.letters) {
            selectableAdapter.unlockLetter(it)
            next.isEnabled = selectedAdapter.completed()
        }
        stepWrite.letters.shuffle()
        selectableAdapter = SelectableLettersAdapter(stepWrite.letters) {
            selectedAdapter.select(it)
            next.isEnabled = selectedAdapter.completed()
        }
        compile.adapter = selectedAdapter
        available.adapter = selectableAdapter
        compile.addItemDecoration(GridSpaceItemDecoration(requireActivity().dip(4), requireActivity().dip(4)))
        available.addItemDecoration(GridSpaceItemDecoration(requireActivity().dip(8), requireActivity().dip(8)))
    }

    private fun calculateColumns(): Int {
        val displayMetrics = requireContext().resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val columns = ((dpWidth - 32) / 40) - 1
        return columns.toInt()
    }

    private fun calculateSelectableColumns(): Int {
        val displayMetrics = requireContext().resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val columns = ((dpWidth - 32) / 40) - 1
        return columns.toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun setLowerCase(text: String) = text.toLowerCase(Locale.ITALIAN)

    companion object {
        const val EXTRA_STEP = "extra step int position"
        const val BASIC = "basic"

        fun newInstance(unitId: Int, stepIndex: Int): FragmentWrite {
            val frag = FragmentWrite()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
            bundle.putInt(EXTRA_STEP, stepIndex)
            frag.arguments = bundle
            return frag
        }
    }
}