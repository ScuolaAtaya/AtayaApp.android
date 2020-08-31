package it.mindtek.ruah.fragments.final_test

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelFinalTestQuestion
import it.mindtek.ruah.interfaces.FinalTestActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import kotlinx.android.synthetic.main.fragment_final_test.*
import kotlinx.android.synthetic.main.item_final_test.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import java.io.File

class FragmentFinalTest : Fragment() {
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var questions: MutableList<ModelFinalTestQuestion> = mutableListOf()
    private var player: MediaPlayer? = null
    private var communicator: FinalTestActivityInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_final_test, container, false)
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
        if (requireActivity() is FinalTestActivityInterface) {
            communicator = requireActivity() as FinalTestActivityInterface
        }
        val finalTest = db.finalTestDao().getFinalTestByUnitId(unitId)
        finalTest.forEach {
            questions.addAll(it.questions)
        }
        if (questions.size == 0 || questions.size <= stepIndex) {
            requireActivity().finish()
        }
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(requireActivity(), it.color)
            stepLayout.backgroundColor = color
            questionAudio.supportBackgroundTintList = ColorStateList.valueOf(color)
        }
        next.isEnabled = false
        setupSteps()
        setupNext()
        setupQuestion()
        setupAudio()
        setupPicture()
        setupAnswers()
    }

    @SuppressLint("SetTextI18n")
    private fun setupSteps() {
        step.text = "${stepIndex + 1}/${questions.size}"
    }

    private fun setupNext() {
        next.setOnClickListener {
            if (stepIndex + 1 < questions.size) {
                player?.release()
                communicator?.goToNext(stepIndex + 1)
            } else {
                communicator?.goToFinish()
            }
        }
    }

    private fun setupQuestion() {
        if (questions.size >= stepIndex) {
            title.text = getString(R.string.question)
            description.text = questions[stepIndex].body
        }
    }

    private fun setupAudio() {
        questionAudio.setOnClickListener {
            if (questions.size >= stepIndex) {
                val audio = questions[stepIndex].audio
                playAudio(audio.value)
            }
        }
    }

    private fun playAudio(audio: String) {
        when {
            player == null -> {
                val audioFile = File(fileFolder.absolutePath, audio)
                player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
                player!!.setOnCompletionListener {
                    if (canAccessActivity) {
                        player!!.pause()
                    }
                }
                player!!.start()
            }
            player!!.isPlaying -> player!!.pause()
            else -> player!!.start()
        }
    }

    private fun setupPicture() {
        val picture = questions[stepIndex].picture?.value
        if (!picture.isNullOrEmpty()) {
            stepImage.setVisible()
            val pictureFile = File(fileFolder.absolutePath, picture)
            GlideApp.with(this).load(pictureFile).placeholder(R.color.grey).into(stepImage)
        }
        if (stepImage.visibility == View.GONE) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(root)
            constraintSet.connect(R.id.questionAudio, ConstraintSet.END, R.id.stepLayout, ConstraintSet.START, requireActivity().dip(16))
            constraintSet.applyTo(root)
        }
    }

    private fun setupAnswers() {
        yes.text.text = getString(R.string.yes)
        no.text.text = getString(R.string.no)
        yes.radioSelect.setOnClickListener {
            yes.radioSelect.setGone()
            if (questions[stepIndex].correct) {
                yes.correct.setVisible()
                next.isEnabled = true
            } else {
                yes.wrong.setVisible()
            }
        }
        no.radioSelect.setOnClickListener {
            no.radioSelect.setGone()
            if (questions[stepIndex].correct) {
                no.wrong.setVisible()
            } else {
                no.correct.setVisible()
                next.isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    companion object {
        const val EXTRA_STEP = "extra_step_int_position"

        fun newInstance(unitId: Int, stepIndex: Int): FragmentFinalTest {
            val fragment = FragmentFinalTest()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
            bundle.putInt(EXTRA_STEP, stepIndex)
            fragment.arguments = bundle
            return fragment
        }
    }
}