package it.mindtek.ruah.fragments.understand

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.AnswersAdapter
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.fileFolder
import it.mindtek.ruah.kotlin.extensions.setVisible
import it.mindtek.ruah.pojos.PojoQuestion
import kotlinx.android.synthetic.main.fragment_understand_questions.*
import kotlinx.android.synthetic.main.fragment_understand_questions.next
import kotlinx.android.synthetic.main.fragment_understand_questions.step
import org.jetbrains.anko.backgroundColor
import java.io.File

class FragmentUnderstandQuestions : Fragment() {
    private var unitId: Int = -1
    private var questionIndex: Int = -1
    private var stepIndex: Int = -1
    private var questions: MutableList<PojoQuestion> = mutableListOf()
    private var communicator: UnderstandActivityInterface? = null
    private var player: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_understand_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(EXTRA_UNIT_ID)) {
                unitId = it.getInt(EXTRA_UNIT_ID, -1)
            }
            if (it.containsKey(EXTRA_QUESTION_NUMBER)) {
                questionIndex = it.getInt(EXTRA_QUESTION_NUMBER, -1)
            }
            if (it.containsKey(EXTRA_STEP)) {
                stepIndex = it.getInt(EXTRA_STEP, -1)
            }
        }
        setup()
    }

    private fun setup() {
        if (unitId == -1 || stepIndex == -1) {
            requireActivity().finish()
        }
        if (requireActivity() is UnderstandActivityInterface) {
            communicator = requireActivity() as UnderstandActivityInterface
        }
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(requireActivity(), it.color)
            stepLayout.backgroundColor = color
        }
        val understand = db.understandDao().getUnderstandByUnitId(unitId)
        questions = understand[stepIndex].questions
        next.isEnabled = false
        setupBack()
        setupSection()
        setupQuestion()
        setupAnswers()
    }

    private fun setupBack() {
        reset.setOnClickListener {
            communicator?.goToVideo(stepIndex)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        step.text = "${questionIndex + 1}/${questions.size}"
        next.setOnClickListener {
            destroyPlayer()
            if (questionIndex + 1 < questions.size) {
                communicator?.openQuestion(questionIndex + 1, stepIndex)
            } else {
                communicator?.goToVideo(stepIndex + 1)
            }
        }
    }

    private fun playAudio(audio: String) {
        if (player != null) {
            destroyPlayer()
        }
        val audioFile = File(fileFolder.absolutePath, audio)
        player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
        player?.setOnCompletionListener {
            destroyPlayer()
        }
        player?.start()
    }

    private fun setupQuestion() {
        if (questions.size >= questionIndex) {
            val question = questions[questionIndex]
            title.text = getString(R.string.question)
            question.question?.let { q ->
                description.text = q.body
                setupPicture(q.picture?.value)
                questionAudio.setOnClickListener {
                    playAudio(q.audio.value)
                }
            }
        }
    }

    private fun setupAnswers() {
        if (questions.size >= questionIndex) {
            val question = questions[questionIndex]
            val answers = question.answers
            val adapter = AnswersAdapter(answers, { answer ->
                if (answer.correct) {
                    next.isEnabled = true
                }
            }, { answer ->
                playAudio(answer.audio.value)
            })
            answersRecycler.layoutManager = LinearLayoutManager(requireActivity())
            answersRecycler.adapter = adapter
        }
    }

    private fun setupPicture(picture: String?) {
        if (!picture.isNullOrEmpty()) {
            stepImage.setVisible()
            val pictureFile = File(fileFolder.absolutePath, picture)
            GlideApp.with(this).load(pictureFile).placeholder(R.color.grey).into(stepImage)
        }
    }

    private fun destroyPlayer() {
        player?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    companion object {
        const val EXTRA_QUESTION_NUMBER = "question_number_extra"
        const val EXTRA_UNIT_ID = "unit_id_extra"
        const val EXTRA_STEP = "extra step int position"

        fun newInstance(questionIndex: Int, unitId: Int, stepIndex: Int): FragmentUnderstandQuestions {
            val fragment = FragmentUnderstandQuestions()
            val bundle = Bundle()
            bundle.putInt(EXTRA_QUESTION_NUMBER, questionIndex)
            bundle.putInt(EXTRA_UNIT_ID, unitId)
            bundle.putInt(EXTRA_STEP, stepIndex)
            fragment.arguments = bundle
            return fragment
        }
    }
}