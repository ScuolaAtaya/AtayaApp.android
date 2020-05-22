package it.mindtek.ruah.fragments.write


import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityIntro
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.SelectableLettersAdapter
import it.mindtek.ruah.adapters.SelectedLettersAdapter
import it.mindtek.ruah.adapters.dividers.GridSpaceItemDecoration
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelWrite
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.interfaces.WriteActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import kotlinx.android.synthetic.main.fragment_write.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.support.v4.dip
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class FragmentWrite : Fragment() {
    var unitId: Int = -1
    var category: Category? = null
    var stepIndex: Int = -1
    var player: MediaPlayer? = null
    var write: MutableList<ModelWrite> = mutableListOf()
    var communicator: WriteActivityInterface? = null
    var selectedAdapter: SelectedLettersAdapter? = null
    var selectableAdapter: SelectableLettersAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID))
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            if (it.containsKey(ActivityIntro.EXTRA_CATEGORY_ID))
                category = Category.from(it.getInt(ActivityIntro.EXTRA_CATEGORY_ID))
            if (it.containsKey(EXTRA_STEP))
                stepIndex = it.getInt(EXTRA_STEP)
        }
        if (unitId == -1 || category == null || stepIndex == -1)
            activity?.finish()
        write = db.writeDao().getWriteByUnitId(unitId)
        if (write.size == 0 || write.size <= stepIndex) {
            activity?.finish()
        } else {
            initCommunicators()
            setup()
        }
    }

    private fun initCommunicators() {
        if (activity is WriteActivityInterface)
            communicator = activity as WriteActivityInterface
    }

    @SuppressLint("RestrictedApi")
    private fun setup() {
        setupPicture()
        setupButtons()
        setupSteps()
        if (write[stepIndex].type == "basic") {
            setupBasic()
            setupRecyclers()
            setupAudio()
        } else {
            setupAdvanced()
            setupAudio()
        }
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            activity?.let { activity ->
                val color = ContextCompat.getColor(activity, it.color)
                stepLayout.backgroundColor = color
                editText.supportBackgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

    private fun setupAudio() {
        audioButton.setOnClickListener {
            if (write.size >= stepIndex) {
                val currentWrite = write[stepIndex]
                playAudio(currentWrite.audio)
            }
        }
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
                if (s.toString().toLowerCase() == write[stepIndex].word.toLowerCase()) {
                    showRight()
                    complete()
                } else {
                    reset()
                }
                if (s.toString().isNotEmpty() && s.toString().toLowerCase() != write[stepIndex].word.substring(0, s.toString().length).toLowerCase()) {
                    showError()
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
        activity?.let { activity ->
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity, R.drawable.close), null)
        }
    }

    fun showRight() {
        activity?.let { activity ->
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity, R.drawable.done), null)
        }
    }

    private fun setupRecyclers() {
        val stepWrite = write[stepIndex]
        val selectableCol = calculateSelectableColumns()
        val selectedCol = calculateColumns()
        compile.layoutManager = GridLayoutManager(activity, if (stepWrite.letters.size >= selectedCol) selectedCol else stepWrite.letters.size)
        available.layoutManager = GridLayoutManager(activity, if (stepWrite.letters.size >= selectableCol) selectableCol else stepWrite.letters.size)
        selectedAdapter = SelectedLettersAdapter(stepWrite.word, stepWrite.letters, { letter ->
            selectableAdapter?.unlockLetter(letter)
            if (selectedAdapter?.completed() == true) {
                complete()
            } else {
                reset()
            }
        })
        stepWrite.letters.shuffle()
        selectableAdapter = SelectableLettersAdapter(stepWrite.letters, { letters ->
            selectedAdapter?.select(letters)
            if (selectedAdapter?.completed() == true) {
                complete()
            } else {
                reset()
            }
        })
        compile.adapter = selectedAdapter
        available.adapter = selectableAdapter
        compile.addItemDecoration(GridSpaceItemDecoration(dip(4), dip(4)))
        available.addItemDecoration(GridSpaceItemDecoration(dip(8), dip(8)))
    }

    private fun complete() {
        next.isEnabled = true
    }

    private fun reset() {
        next.isEnabled = false
    }

    private fun calculateColumns(): Int {
        context?.let { context ->
            val displayMetrics = context.resources.displayMetrics
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
            val columns = ((dpWidth - 32) / 32) - 1
            println(columns)
            return columns.toInt()
        } ?: return 0
    }

    private fun calculateSelectableColumns(): Int {
        context?.let { context ->
            val displayMetrics = context.resources.displayMetrics
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
            val columns = ((dpWidth - 32) / 40) - 1
            println(columns)
            return columns.toInt()
        } ?: return 0
    }

    private fun setupPicture() {
        val pictureFile = File(fileFolder.absolutePath, write[stepIndex].picture)
        GlideApp.with(this).load(pictureFile).placeholder(R.color.grey).into(picture)
//        GlideApp.with(this).load(R.drawable.placeholder).placeholder(R.color.grey).into(picture)
    }

    private fun playAudio(audio: String) {
        if (player != null)
            destroyPlayer()
        val audioFile = File(fileFolder.absolutePath, audio)
        player = MediaPlayer.create(activity, Uri.fromFile(audioFile))
        player?.setOnCompletionListener {
            destroyPlayer()
        }
        player?.start()
    }

    private fun destroyPlayer() {
        player?.release()
    }

    private fun setupButtons() {
        next.setOnClickListener {
            destroyPlayer()
            dispatch()
        }
        next.disable()
    }

    private fun setupSteps() {
        step.text = "${stepIndex + 1}/${write.size}"
    }

    private fun dispatch() {
        if (stepIndex + 1 < write.size) {
            goToNext()
        } else {
            finish()
        }
    }

    private fun finish() {
        communicator?.goToFinish()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    private fun goToNext() {
        communicator?.goToNext(stepIndex + 1)
    }

    companion object {
        val EXTRA_STEP = "extra step int position"

        fun newInstance(): FragmentWrite = FragmentWrite()

        fun newInstance(unit_id: Int, category: Category, stepIndex: Int): FragmentWrite {
            val frag = FragmentWrite()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unit_id)
            bundle.putInt(ActivityIntro.EXTRA_CATEGORY_ID, category.value)
            bundle.putInt(EXTRA_STEP, stepIndex)
            frag.arguments = bundle
            return frag
        }
    }
}
