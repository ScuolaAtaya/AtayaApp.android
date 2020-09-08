package it.mindtek.ruah.fragments.understand

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
import androidx.recyclerview.widget.LinearLayoutManager
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.AnswersAdapter
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelMedia
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import it.mindtek.ruah.pojos.PojoQuestion
import kotlinx.android.synthetic.main.fragment_understand_questions.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import java.io.File


class FragmentUnderstandQuestions : Fragment() {
    private var unitId: Int = -1
    private var questionIndex: Int = -1
    private var understandIndex: Int = -1
    private var currentAudioIndex: Int = -1
    private var questions: MutableList<PojoQuestion> = mutableListOf()
    private var understandSize: Int = -1
    private var answersPlayer: MediaPlayer? = null
    private var questionPlayer: MediaPlayer? = null
    private lateinit var communicator: UnderstandActivityInterface

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
                understandIndex = it.getInt(EXTRA_STEP, -1)
            }
        }
        setup()
    }

    @SuppressLint("RestrictedApi")
    private fun setup() {
        communicator = requireActivity() as UnderstandActivityInterface
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(requireActivity(), it.color)
            stepLayout.backgroundColor = color
            questionAudio.supportBackgroundTintList = ColorStateList.valueOf(color)
        }
        understandSize = db.understandDao().count()
        val understand = db.understandDao().getUnderstandByUnitId(unitId)
        questions = understand[understandIndex].questions
        next.disable()
        setupBack()
        setupSection()
        setupQuestion()
        setupAnswers()
    }

    private fun setupBack() {
        reset.setOnClickListener {
            communicator.goToVideo(understandIndex, true)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        step.text = "${questionIndex + 1}/${questions.size}"
        next.setOnClickListener {
            destroyPlayers()
            if (questionIndex + 1 < questions.size) {
                communicator.goToNextQuestion(questionIndex + 1)
            } else {
                if (understandIndex + 1 < understandSize) {
                    communicator.goToVideo(understandIndex + 1, false)
                } else {
                    communicator.goToFinish()
                }
            }
        }
    }

    private fun setupQuestion() {
        title.text = getString(R.string.question)
        questions[questionIndex].question?.let {
            description.text = it.body
            setupPicture(it.picture)
            setupQuestionAudio(it.audio)

        }
    }

    private fun setupQuestionAudio(audio: ModelMedia) {
        questionAudio.setOnClickListener {
            playQuestionAudio(audio.value)
        }
        if (audio.credits.isNotBlank()) {
            questionAudioCredits.setVisible()
            questionAudioCredits.text = audio.credits
        }
    }

    private fun playQuestionAudio(audio: String) {
        answersPlayer?.pause()
        when {
            questionPlayer == null -> {
                questionPlayer = initPlayer(audio)
                questionPlayer!!.start()
            }
            questionPlayer!!.isPlaying -> questionPlayer!!.pause()
            else -> questionPlayer!!.start()
        }
    }

    private fun setupAnswers() {
        val answers = questions[questionIndex].answers
        answers.forEach {
            val audioFile = File(fileFolder.absolutePath, it.audio.value)
            val player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
            player.setOnCompletionListener {
                if (canAccessActivity) {
                    player.pause()
                }
            }
        }
        val adapter = AnswersAdapter(answers, {
            if (it.correct) {
                next.enable()
            }
        }, {
            playAnswerAudio(answers.indexOf(it), it.audio.value)
        })
        answersRecycler.layoutManager = LinearLayoutManager(requireActivity())
        answersRecycler.adapter = adapter
    }

    private fun playAnswerAudio(index: Int, audio: String) {
        questionPlayer?.pause()
        when {
            answersPlayer == null -> {
                currentAudioIndex = index
                answersPlayer = initPlayer(audio)
                answersPlayer!!.start()
            }
            answersPlayer!!.isPlaying -> {
                if (currentAudioIndex == index) {
                    answersPlayer!!.pause()
                } else {
                    resetAnswerPlayer(index, audio)
                }
            }
            else -> {
                if (currentAudioIndex == index) {
                    answersPlayer!!.start()
                } else {
                    resetAnswerPlayer(index, audio)
                }
            }
        }
    }

    private fun resetAnswerPlayer(index: Int, audio: String) {
        answersPlayer!!.reset()
        currentAudioIndex = index
        val audioFile = File(fileFolder.absolutePath, audio)
        answersPlayer!!.setDataSource(requireActivity(), Uri.fromFile(audioFile))
        answersPlayer!!.prepare()
        answersPlayer!!.start()
    }

    private fun setupPicture(picture: ModelMedia?) {
        picture?.let {
            if (picture.value.isNotBlank()) {
                stepImage.setVisible()
                val pictureFile = File(fileFolder.absolutePath, picture.value)
                GlideApp.with(this).load(pictureFile).placeholder(R.color.grey).into(stepImage)
            }
            if (picture.credits.isNotBlank()) {
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

    private fun initPlayer(audio: String): MediaPlayer {
        val audioFile = File(fileFolder.absolutePath, audio)
        val player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
        player.setOnCompletionListener {
            if (canAccessActivity) {
                player.pause()
            }
        }
        return player
    }

    private fun destroyPlayers() {
        questionPlayer?.release()
        answersPlayer?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayers()
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