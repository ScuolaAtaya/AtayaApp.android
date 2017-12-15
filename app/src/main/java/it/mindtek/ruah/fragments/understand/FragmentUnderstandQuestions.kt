package it.mindtek.ruah.fragments.understand


import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.AnswersAdapter
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.compat21
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.pojos.PojoQuestion
import kotlinx.android.synthetic.main.fragment_understand_questions.*


/**
 * A simple [Fragment] subclass.
 */
class FragmentUnderstandQuestions : Fragment() {
    var unit_id: Int = -1
    var question: Int = -1
    var questions: MutableList<PojoQuestion> = mutableListOf()
    var communicator: UnderstandActivityInterface? = null

    var player: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_understand_questions, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCommunicators()

        arguments?.let {
            if (it.containsKey(EXTRA_UNIT_ID))
                unit_id = it.getInt(EXTRA_UNIT_ID, -1)
            if (it.containsKey(EXTRA_QUESTION_NUMBER))
                question = it.getInt(EXTRA_QUESTION_NUMBER, -1)
        }

        if (unit_id == -1)
            activity.finish()
        else {
            val category = db.understandDao().getUnderstandByUnitId(unit_id)
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

    private fun playAudio(audio: Int) {
        if (player != null)
            destroyPlayer()
        player = MediaPlayer.create(activity, audio)
        player?.setOnCompletionListener {
            destroyPlayer()
        }
        player?.start()
    }

    private fun finish() {
        communicator?.finishSection()
    }

    private fun setupQuestion() {
        val question = questions[question]
        title.text = question.question?.title
        description.text = question.question?.body
        questionAudio.setOnClickListener {
            //todo: replace fake audio with right one
            playAudio(R.raw.voice)
        }
    }

    private fun setupAnswers() {
        val question = questions[question]
        val answers = question.answers
        val adapter = AnswersAdapter(answers, { answer ->
            handleAnswerSelected(answer)
        }, { answer ->
            //todo: Replace file with correct one
            playAudio(R.raw.voice)
        })
        answersRecycler.layoutManager = LinearLayoutManager(activity)
        answersRecycler.adapter = adapter
    }

    private fun handleAnswerSelected(answer: ModelAnswer) {
        if (answer.correct) {
            enableNext()
        }
    }

    private fun disableNext() {
        next.isEnabled = false
        compat21(@TargetApi(21) {
            next.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disabled))
        }, null)
    }

    private fun enableNext() {
        next.isEnabled = true
        compat21(@TargetApi(21) {
            next.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.casa))
        }, null)
    }

    private fun destroyPlayer() {
        player?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    companion object {
        val EXTRA_QUESTION_NUMBER = "question_number_extra"
        val EXTRA_UNIT_ID = "unit_id_extra"

        fun newInstance(): FragmentUnderstandQuestions = FragmentUnderstandQuestions()

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
