package it.mindtek.ruah.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import it.mindtek.ruah.R
import it.mindtek.ruah.adapters.AnswersAdapter
import it.mindtek.ruah.config.LayoutUtils
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.FragmentUnderstandQuestionsBinding
import it.mindtek.ruah.db.models.ModelMedia
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import it.mindtek.ruah.pojos.PojoQuestion
import java.io.File
import androidx.core.view.isGone
import it.mindtek.ruah.adapters.ModelAnswerItem


class FragmentUnderstandQuestions : Fragment() {
    private lateinit var binding: FragmentUnderstandQuestionsBinding
    private lateinit var communicator: UnderstandActivityInterface
    private var unitId: Int = -1
    private var questionIndex: Int = -1
    private var understandIndex: Int = -1
    private var currentAudioIndex: Int = -1
    private var questions: MutableList<PojoQuestion> = mutableListOf()
    private var understandSize: Int = -1
    private var answersPlayer: MediaPlayer? = null
    private var questionPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUnderstandQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(EXTRA_UNIT_ID)) unitId = it.getInt(EXTRA_UNIT_ID, -1)
            if (it.containsKey(EXTRA_QUESTION_NUMBER)) questionIndex = it.getInt(
                EXTRA_QUESTION_NUMBER, -1
            )
            if (it.containsKey(EXTRA_STEP)) understandIndex = it.getInt(EXTRA_STEP, -1)
        }
        setup()
    }

    private fun setup() {
        communicator = requireActivity() as UnderstandActivityInterface
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ResourceProvider.getColor(requireActivity(), it.name)
            binding.stepLayout.setBackgroundColor(color)
            binding.questionAudio.backgroundTintList = ColorStateList.valueOf(color)
        }
        understandSize = db.understandDao().countByUnitId(unitId)
        val understand = db.understandDao().getUnderstandByUnitId(unitId)
        questions = understand[understandIndex].questions
        setupQuestion()
        setupAnswers()
        setupSection()
    }

    private fun setupQuestion() {
        binding.title.text = getString(R.string.question)
        questions[questionIndex].question?.let {
            binding.description.text = it.body
            setupPicture(it.picture)
            setupQuestionAudio(it.audio)
        }
    }

    private fun setupAnswers() {
        val answers = questions[questionIndex].answers.map {
            ModelAnswerItem(it.id, it.body, it.audio, it.correct)
        }
        answers.forEach {
            val audioFile = File(fileFolder.absolutePath, it.audio.value)
            val player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
            player.setOnCompletionListener {
                if (canAccessActivity) player.pause()
            }
        }
        val adapter = AnswersAdapter(object : AnswersAdapter.OnClickListener {
            override fun onAnswerSelected(answer: ModelAnswerItem) {
                if (answer.correct) binding.next.enable()
            }

            override fun onAnswerAudioClicked(answer: ModelAnswerItem) {
                playAnswerAudio(answers.indexOf(answer), answer.audio.value)
            }
        })
        binding.answersRecycler.adapter = adapter
        adapter.submitList(answers)
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        binding.step.text = "${questionIndex + 1}/${questions.size}"
        binding.reset.setOnClickListener {
            communicator.goToVideo(understandIndex, true)
        }
        binding.next.disable()
        binding.next.setOnClickListener {
            destroyPlayers()
            questionPlayer = null
            answersPlayer = null
            if (questionIndex + 1 < questions.size) communicator.goToNextQuestion(questionIndex + 1)
            else {
                if (understandIndex + 1 < understandSize)
                    communicator.goToVideo(understandIndex + 1, false)
                else communicator.goToFinish()
            }
        }
    }

    private fun setupPicture(picture: ModelMedia?) {
        picture?.let {
            if (picture.value.isNotBlank()) {
                binding.stepImage.setVisible()
                val pictureFile = File(fileFolder.absolutePath, picture.value)
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

    private fun setupQuestionAudio(audio: ModelMedia) {
        binding.questionAudio.setOnClickListener {
            playQuestionAudio(audio.value)
        }
        if (!audio.credits.isNullOrBlank()) {
            binding.questionAudioCredits.setVisible()
            binding.questionAudioCredits.text = audio.credits
        }
    }

    private fun playQuestionAudio(audio: String) {
        answersPlayer?.pause()
        when {
            questionPlayer == null -> {
                questionPlayer = initPlayer(audio)
                questionPlayer?.start()
            }

            questionPlayer?.isPlaying == true -> questionPlayer?.pause()
            else -> questionPlayer?.start()
        }
    }

    private fun playAnswerAudio(index: Int, audio: String) {
        questionPlayer?.pause()
        when {
            answersPlayer == null -> {
                currentAudioIndex = index
                answersPlayer = initPlayer(audio)
                answersPlayer?.start()
            }

            answersPlayer?.isPlaying == true -> {
                if (currentAudioIndex == index) answersPlayer?.pause()
                else resetAnswerPlayer(index, audio)
            }

            else -> {
                if (currentAudioIndex == index) answersPlayer?.start()
                else resetAnswerPlayer(index, audio)
            }
        }
    }

    private fun resetAnswerPlayer(index: Int, audio: String) {
        answersPlayer?.reset()
        currentAudioIndex = index
        val audioFile = File(fileFolder.absolutePath, audio)
        answersPlayer?.setDataSource(requireActivity(), Uri.fromFile(audioFile))
        answersPlayer?.prepare()
        answersPlayer?.start()
    }

    private fun initPlayer(audio: String): MediaPlayer {
        val audioFile = File(fileFolder.absolutePath, audio)
        val player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
        player.setOnCompletionListener {
            if (canAccessActivity) player.pause()
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
        private const val EXTRA_QUESTION_NUMBER = "question_number_extra"
        private const val EXTRA_UNIT_ID = "unit_id_extra"
        private const val EXTRA_STEP = "extra step int position"

        fun newInstance(
            questionIndex: Int,
            unitId: Int,
            stepIndex: Int
        ): FragmentUnderstandQuestions = FragmentUnderstandQuestions().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_QUESTION_NUMBER, questionIndex)
                putInt(EXTRA_UNIT_ID, unitId)
                putInt(EXTRA_STEP, stepIndex)
            }
        }
    }
}