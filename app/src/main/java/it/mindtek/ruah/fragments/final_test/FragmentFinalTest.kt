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
    private lateinit var player: MediaPlayer
    private lateinit var communicator: FinalTestActivityInterface

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
        communicator = requireActivity() as FinalTestActivityInterface
        val finalTest = db.finalTestDao().getFinalTestByUnitId(unitId)
        finalTest.forEach {
            questions.addAll(it.questions)
        }
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(requireActivity(), it.color)
            stepLayout.backgroundColor = color
            questionAudio.supportBackgroundTintList = ColorStateList.valueOf(color)
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
        player.setOnCompletionListener {
            if (canAccessActivity) {
                player.pause()
            }
        }
        questionAudio.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.start()
            }
        }
        if (!audio.credits.isNullOrBlank()) {
            questionAudioCredits.setVisible()
            questionAudioCredits.text = audio.credits
        }
    }


    private fun setupPicture() {
        val picture = questions[stepIndex].picture
        picture?.let {
            if (it.value.isNotBlank()) {
                stepImage.setVisible()
                val pictureFile = File(fileFolder.absolutePath, it.value)
                GlideApp.with(this).load(pictureFile).placeholder(R.color.grey).into(stepImage)
            }
            if (!picture.credits.isNullOrBlank()) {
                stepImageCredits.setVisible()
                stepImageCredits.text = picture.credits
            }
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
            if (questions[stepIndex].answers) {
                yes.correct.setVisible()
                next.enable()
            } else {
                yes.wrong.setVisible()
            }
        }
        no.radioSelect.setOnClickListener {
            no.radioSelect.setGone()
            if (questions[stepIndex].answers) {
                no.wrong.setVisible()
            } else {
                no.correct.setVisible()
                next.enable()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        step.text = "${stepIndex + 1}/${questions.size}"
        title.text = getString(R.string.question)
        description.text = questions[stepIndex].body
        next.disable()
        next.setOnClickListener {
            if (stepIndex + 1 < questions.size) {
                player.release()
                yes.radioSelect.isChecked = false
                no.radioSelect.isChecked = false
                communicator.goToNext(stepIndex + 1)
            } else {
                communicator.goToFinish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
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