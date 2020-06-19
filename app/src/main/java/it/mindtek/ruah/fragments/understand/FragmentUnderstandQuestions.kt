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
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.AnswersAdapter
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.fileFolder
import it.mindtek.ruah.pojos.PojoQuestion
import kotlinx.android.synthetic.main.fragment_understand_questions.*
import java.io.File

class FragmentUnderstandQuestions : Fragment() {
    private var unitId: Int = -1
    private var question: Int = -1
    private var questions: MutableList<PojoQuestion> = mutableListOf()
    private var communicator: UnderstandActivityInterface? = null
    private var player: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_understand_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCommunicators()
        arguments?.let {
            if (it.containsKey(EXTRA_UNIT_ID))
                unitId = it.getInt(EXTRA_UNIT_ID, -1)
            if (it.containsKey(EXTRA_QUESTION_NUMBER))
                question = it.getInt(EXTRA_QUESTION_NUMBER, -1)
        }
        if (unitId == -1)
            activity?.finish()
        else {
            val category = db.understandDao().getUnderstandByUnitId(unitId)
            category?.let {
                questions = it.questions
                disableNext()
                setupBack()
                setupSection()
                setupQuestion()
                setupAnswers()
            }
        }
    }

    private fun getCommunicators() {
        if (activity is UnderstandActivityInterface)
            communicator = activity as UnderstandActivityInterface
    }

    private fun setupBack() {
        reset.setOnClickListener {
            communicator?.goToStart()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        step.text = "${question + 1}/${questions.size}"
        next.setOnClickListener {
            destroyPlayer()
            if (question + 1 < questions.size)
                communicator?.openQuestion(question + 1)
            else
                finish()
        }
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

    private fun finish() {
        communicator?.finishSection()
    }

    private fun setupQuestion() {
        if (questions.size >= question) {
            val question = questions[question]
            title.text = getString(R.string.question)
            question.question?.let { q ->
                description.text = q.body
                questionAudio.setOnClickListener {
                    playAudio(q.audio.value)
                }
            }
        }
    }

    private fun setupAnswers() {
        if (questions.size >= question) {
            val question = questions[question]
            val answers = question.answers
            val adapter = AnswersAdapter(answers, { answer ->
                handleAnswerSelected(answer)
            }, { answer ->
                playAudio(answer.audio.value)
            })
            answersRecycler.layoutManager = LinearLayoutManager(activity)
            answersRecycler.adapter = adapter
        }
    }

    private fun handleAnswerSelected(answer: ModelAnswer) {
        if (answer.correct) {
            enableNext()
        }
    }

    private fun disableNext() {
        next.isEnabled = false
    }

    private fun enableNext() {
        next.isEnabled = true
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

        fun newInstance(question: Int, unit_id: Int): FragmentUnderstandQuestions {
            val fragment = FragmentUnderstandQuestions()
            val bundle = Bundle()
            bundle.putInt(EXTRA_QUESTION_NUMBER, question)
            bundle.putInt(EXTRA_UNIT_ID, unit_id)
            fragment.arguments = bundle
            return fragment
        }
    }
}