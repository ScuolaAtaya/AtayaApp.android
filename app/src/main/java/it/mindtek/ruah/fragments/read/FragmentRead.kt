package it.mindtek.ruah.fragments.read

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.OptionRenderViewModel
import it.mindtek.ruah.adapters.OptionsAdapter
import it.mindtek.ruah.config.ImageWithMarkersGenerator
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.interfaces.ReadActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import it.mindtek.ruah.pojos.PojoRead
import kotlinx.android.synthetic.main.fragment_read.*
import java.io.File


class FragmentRead : Fragment() {
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var currentAudioIndex: Int = -1
    private lateinit var adapter: OptionsAdapter
    private var optionsPlayers: MediaPlayer? = null
    private lateinit var communicator: ReadActivityInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_read, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID))
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            if (it.containsKey(EXTRA_STEP)) stepIndex = it.getInt(EXTRA_STEP)
        }
        setup()
    }

    private fun setup() {
        communicator = requireActivity() as ReadActivityInterface
        val read = db.readDao().getReadByUnitId(unitId)
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            stepBackground.backgroundColor = ResourceProvider.getColor(requireActivity(), it.name)
        }
        setupPicture(read[stepIndex])
        setupOptions(read[stepIndex])
        setupSection(read)
    }

    private fun setupPicture(read: PojoRead) {
        read.read?.let {
            val pictureFile = File(fileFolder.absolutePath, it.picture.value)
            val bitmap: Bitmap? =
                ImageWithMarkersGenerator.createImageWithMarkers(it.markers, pictureFile)
            stepImage.setImageBitmap(bitmap)
            if (!it.picture.credits.isNullOrBlank()) {
                stepImageCredits.setVisible()
                stepImageCredits.text = it.picture.credits
            }
        }
    }

    private fun setupOptions(read: PojoRead) {
        val markerList = read.read!!.markers
        val answerList = markerList.map {
            it.id
        }.toMutableList()
        val optionList = read.options.map {
            return@map OptionRenderViewModel(it, null, null)
        }.toMutableList()
        optionList.shuffle()
        adapter = OptionsAdapter(requireActivity(), optionList, answerList, {
            next?.isEnabled = it == markerList.size
        }, {
            playOptionAudio(read.options.indexOf(it), it.audio.value)
        })
        options.layoutManager = LinearLayoutManager(requireActivity())
        options.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection(read: MutableList<PojoRead>) {
        step.text = "${stepIndex + 1}/${read.size}"
        next.disable()
        next.setOnClickListener {
            if (adapter.completed()) {
                if (stepIndex + 1 < read.size) {
                    optionsPlayers?.release()
                    communicator.goToNext(stepIndex + 1)
                } else communicator.goToFinish()
            }
        }
    }

    private fun playOptionAudio(index: Int, audio: String) {
        when {
            optionsPlayers == null -> {
                currentAudioIndex = index
                val audioFile = File(fileFolder.absolutePath, audio)
                optionsPlayers = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
                optionsPlayers!!.setOnCompletionListener {
                    if (canAccessActivity) optionsPlayers!!.pause()
                }
                optionsPlayers!!.start()
            }
            optionsPlayers!!.isPlaying -> {
                if (currentAudioIndex == index) optionsPlayers!!.pause()
                else resetOptionPlayer(index, audio)
            }
            else -> {
                if (currentAudioIndex == index) optionsPlayers!!.start()
                else resetOptionPlayer(index, audio)
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