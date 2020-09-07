package it.mindtek.ruah.fragments.read

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.OptionsAdapter
import it.mindtek.ruah.config.ImageWithMarkersGenerator
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.interfaces.ReadActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import it.mindtek.ruah.pojos.PojoRead
import kotlinx.android.synthetic.main.fragment_read.*
import org.jetbrains.anko.backgroundColor
import java.io.File


class FragmentRead : Fragment() {
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var color: Int = -1
    private var currentAudioIndex: Int = -1
    private lateinit var adapter: OptionsAdapter
    private var optionsPlayers: MediaPlayer? = null
    private lateinit var communicator: ReadActivityInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_read, container, false)
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

    private fun setup() {
        if (unitId == -1 || stepIndex == -1) {
            requireActivity().finish()
        }
        if (requireActivity() is ReadActivityInterface) {
            communicator = requireActivity() as ReadActivityInterface
        }
        val read = db.readDao().getReadByUnitId(unitId)
        if (read.size == 0 || read.size <= stepIndex) {
            requireActivity().finish()
        }
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            color = ContextCompat.getColor(requireActivity(), it.color)
            stepBackground.backgroundColor = color
        }
        next.disable()
        setupSteps(read)
        setupNext(read)
        setupPicture(read[stepIndex])
        setupOptions(read[stepIndex])
    }

    @SuppressLint("SetTextI18n")
    private fun setupSteps(read: MutableList<PojoRead>) {
        step.text = "${stepIndex + 1}/${read.size}"
    }

    private fun setupNext(read: MutableList<PojoRead>) {
        next.setOnClickListener {
            if (stepIndex + 1 < read.size) {
                optionsPlayers?.release()
                communicator.goToNext(stepIndex + 1)
            } else {
                communicator.goToFinish()
            }
        }
    }

    private fun setupPicture(read: PojoRead) {
        read.read?.let {
            val pictureFile = File(fileFolder.absolutePath, it.picture.value)
            val bitmap = ImageWithMarkersGenerator.createImageWithMarkers(it.markers, pictureFile)
            stepImage.setImageBitmap(bitmap)
            if (it.picture.credits.isNotBlank()) {
                stepImageCredits.setVisible()
                stepImageCredits.text = it.picture.credits
            }
        }
    }

    private fun setupOptions(read: PojoRead) {
        read.options.shuffle()
        val correctOptions: MutableList<ModelReadOption> = mutableListOf()
        adapter = OptionsAdapter(color, read, { it: ModelReadOption, correct: Boolean ->
            if (correct) {
                correctOptions.add(it)
            } else {
                correctOptions.remove(it)
            }
            next.isEnabled = adapter.completed(correctOptions.size)
        }, {
            playOptionAudio(read.options.indexOf(it), it.audio.value)
        })
        options.layoutManager = LinearLayoutManager(requireActivity())
        options.adapter = adapter
    }

    private fun playOptionAudio(index: Int, audio: String) {
        when {
            optionsPlayers == null -> {
                currentAudioIndex = index
                val audioFile = File(fileFolder.absolutePath, audio)
                optionsPlayers = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
                optionsPlayers!!.setOnCompletionListener {
                    if (canAccessActivity) {
                        optionsPlayers!!.pause()
                    }
                }
                optionsPlayers!!.start()
            }
            optionsPlayers!!.isPlaying -> {
                if (currentAudioIndex == index) {
                    optionsPlayers!!.pause()
                } else {
                    resetOptionPlayer(index, audio)
                }
            }
            else -> {
                if (currentAudioIndex == index) {
                    optionsPlayers!!.start()
                } else {
                    resetOptionPlayer(index, audio)
                }
            }
        }
    }

    private fun resetOptionPlayer(index: Int, audio: String) {
        optionsPlayers!!.reset()
        currentAudioIndex = index
        val audioFile = File(fileFolder.absolutePath, audio)
        optionsPlayers!!.setDataSource(requireActivity(), Uri.fromFile(audioFile))
        optionsPlayers!!.prepare()
        optionsPlayers!!.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        optionsPlayers?.release()
    }

    companion object {
        const val EXTRA_STEP = "extra_current_step_number"

        fun newInstance(unitId: Int, stepIndex: Int): FragmentRead {
            val frag = FragmentRead()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
            bundle.putInt(EXTRA_STEP, stepIndex)
            frag.arguments = bundle
            return frag
        }
    }
}