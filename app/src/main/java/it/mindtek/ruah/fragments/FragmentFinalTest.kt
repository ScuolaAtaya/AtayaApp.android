package it.mindtek.ruah.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.config.LayoutUtils
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.FragmentFinalTestBinding
import it.mindtek.ruah.db.models.ModelFinalTestQuestion
import it.mindtek.ruah.interfaces.FinalTestActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import java.io.File
import androidx.core.view.isGone

class FragmentFinalTest : Fragment() {
    private lateinit var binding: FragmentFinalTestBinding
    private lateinit var communicator: FinalTestActivityInterface
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var questions: MutableList<ModelFinalTestQuestion> = mutableListOf()
    private var player: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinalTestBinding.inflate(inflater, container, false)
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

    private fun setup() {
        communicator = requireActivity() as FinalTestActivityInterface
        val finalTest = db.finalTestDao().getFinalTestByUnitId(unitId)
        finalTest.forEach {
            questions.addAll(it.questions)
        }
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            @ColorInt val color: Int = ResourceProvider.getColor(requireActivity(), it.name)
            binding.stepLayout.setBackgroundColor(color)
            binding.questionAudio.backgroundTintList = ColorStateList.valueOf(color)
        }
        setupAudio()
        setupPicture()
        setupAnswers()
        setupSection()
    }

    private fun setupAudio() {
        val audio = questions[stepIndex].audio
        val audioFile = File(fileFolder.absolutePath, audio.value)
        player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
        player?.setOnCompletionListener {
            if (canAccessActivity) player?.pause()
        }
        binding.questionAudio.setOnClickListener {
            if (player?.isPlaying == true) player?.pause() else player?.start()
        }
        if (!audio.credits.isNullOrBlank()) {
            binding.questionAudioCredits.setVisible()
            binding.questionAudioCredits.text = audio.credits
        }
    }


    private fun setupPicture() {
        val picture = questions[stepIndex].picture
        picture?.let {
            if (it.value.isNotBlank()) {
                binding.stepImage.setVisible()
                val pictureFile = File(fileFolder.absolutePath, it.value)
                Glide.with(this).load(pictureFile).placeholder(R.color.grey).into(binding.stepImage)
            }
            if (!picture.credits.isNullOrBlank()) {
                binding.stepImageCredits.setVisible()
                binding.stepImageCredits.text = picture.credits
            }
        }
        if (binding.stepImage.isGone) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.container)
            constraintSet.connect(
                R.id.questionAudio,
                ConstraintSet.END,
                R.id.stepLayout,
                ConstraintSet.START,
                LayoutUtils.dpToPx(requireActivity(), 16)
            )
            constraintSet.applyTo(binding.container)
        }
    }

    private fun setupAnswers() {
        binding.yes.text.text = getString(R.string.yes)
        binding.no.text.text = getString(R.string.no)
        binding.yes.radioSelect.setOnClickListener {
            binding.yes.radioSelect.setGone()
            if (questions[stepIndex].answers) {
                binding.yes.correct.setVisible()
                binding.next.enable()
            } else binding.yes.wrong.setVisible()
        }
        binding.no.radioSelect.setOnClickListener {
            binding.no.radioSelect.setGone()
            if (questions[stepIndex].answers) binding.no.wrong.setVisible() else {
                binding.no.correct.setVisible()
                binding.next.enable()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        binding.step.text = "${stepIndex + 1}/${questions.size}"
        binding.description.text = questions[stepIndex].body
        binding.next.disable()
        binding.next.setOnClickListener {
            if (stepIndex + 1 < questions.size) {
                player?.release()
                binding.yes.radioSelect.isChecked = false
                binding.no.radioSelect.isChecked = false
                communicator.goToNext(stepIndex + 1)
            } else communicator.goToFinish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    companion object {
        private const val EXTRA_STEP = "extra_step_int_position"

        fun newInstance(unitId: Int, stepIndex: Int): FragmentFinalTest =
            FragmentFinalTest().apply {
                arguments = Bundle().apply {
                    putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
                    putInt(EXTRA_STEP, stepIndex)
                }
            }
    }
}