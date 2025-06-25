package it.mindtek.ruah.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.ModelOptionItem
import it.mindtek.ruah.adapters.OptionsAdapter
import it.mindtek.ruah.config.ImageWithMarkersGenerator
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.FragmentReadBinding
import it.mindtek.ruah.db.models.ModelMarker
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.interfaces.ReadActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import it.mindtek.ruah.pojos.PojoRead
import java.io.File

class FragmentRead : Fragment() {
    private lateinit var binding: FragmentReadBinding
    private lateinit var communicator: ReadActivityInterface
    private lateinit var adapter: OptionsAdapter
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var currentAudioIndex: Int = -1
    private var optionsPlayers: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID))
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            if (it.containsKey(EXTRA_STEP)) stepIndex = it.getInt(EXTRA_STEP)
        }
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        optionsPlayers?.release()
    }

    private fun setup() {
        communicator = requireActivity() as ReadActivityInterface
        db.unitDao().getUnitById(unitId)?.let {
            binding.stepBackground.setBackgroundColor(
                ResourceProvider.getColor(requireActivity(), it.name)
            )
        }
        val read: MutableList<PojoRead> = db.readDao().getReadByUnitId(unitId)
        setupPicture(read[stepIndex])
        setupOptions(read[stepIndex])
        setupSection(read)
    }

    private fun setupPicture(read: PojoRead) {
        read.read?.let {
            val pictureFile = File(fileFolder.absolutePath, it.picture.value)
            val bitmap: Bitmap? =
                ImageWithMarkersGenerator.createImageWithMarkers(it.markers, pictureFile)
            binding.stepImage.setImageBitmap(bitmap)
            if (!it.picture.credits.isNullOrBlank()) {
                binding.stepImageCredits.setVisible()
                binding.stepImageCredits.text = it.picture.credits
            }
        }
    }

    private fun setupOptions(read: PojoRead) {
        val markerList: MutableList<ModelMarker> = read.read?.markers ?: mutableListOf()
        val answerList: MutableList<String> = markerList.map {
            it.id
        }.toMutableList()
        adapter =
            OptionsAdapter(requireActivity(), answerList, object : OptionsAdapter.OnClickListener {
                override fun onNumberChanged(answersNumber: Int) {
                    binding.next.isEnabled = answersNumber == markerList.size
                }

                override fun onPlayOptionClicked(option: ModelReadOption) {
                    playOptionAudio(read.options.indexOf(option), option.audio.value)
                }
            })
        binding.options.adapter = adapter
        adapter.submitList(read.options.map {
            ModelOptionItem(it, null, null)
        }.toMutableList().apply {
            shuffle()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection(read: MutableList<PojoRead>) {
        binding.step.text = "${stepIndex + 1}/${read.size}"
        binding.next.disable()
        binding.next.setOnClickListener {
            if (adapter.completed()) {
                if (stepIndex + 1 < read.size) {
                    optionsPlayers?.release()
                    optionsPlayers = null
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
                optionsPlayers =
                    MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile)).apply {
                        setOnCompletionListener {
                            if (canAccessActivity) pause()
                        }
                        start()
                    }
            }

            optionsPlayers?.isPlaying == true -> {
                if (currentAudioIndex == index) optionsPlayers?.pause()
                else resetOptionPlayer(index, audio)
            }

            else -> {
                if (currentAudioIndex == index) optionsPlayers?.start()
                else resetOptionPlayer(index, audio)
            }
        }
    }

    private fun resetOptionPlayer(index: Int, audio: String) {
        optionsPlayers?.apply {
            reset()
            currentAudioIndex = index
            val audioFile = File(fileFolder.absolutePath, audio)
            setDataSource(requireActivity(), Uri.fromFile(audioFile))
            prepare()
            start()
        }
    }

    companion object {
        private const val EXTRA_STEP: String = "extra_current_step_number"

        fun newInstance(unitId: Int, stepIndex: Int): FragmentRead = FragmentRead().apply {
            arguments = Bundle().apply {
                putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
                putInt(EXTRA_STEP, stepIndex)
            }
        }
    }
}