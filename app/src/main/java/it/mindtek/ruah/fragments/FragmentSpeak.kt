package it.mindtek.ruah.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.config.LayoutUtils
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.FragmentSpeakBinding
import it.mindtek.ruah.db.models.ModelSpeak
import it.mindtek.ruah.interfaces.SpeakActivityInterface
import it.mindtek.ruah.kotlin.extensions.canAccessActivity
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.disable
import it.mindtek.ruah.kotlin.extensions.enable
import it.mindtek.ruah.kotlin.extensions.fileFolder
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible
import java.io.File

/**
 * Created by alessandrogaboardi on 15/12/2017.
 */
class FragmentSpeak : Fragment() {
    private lateinit var binding: FragmentSpeakBinding
    private lateinit var recorder: MediaRecorder
    private lateinit var communicator: SpeakActivityInterface
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var recording: Boolean = false
    private var isLocked: Boolean = false
    private var speak: MutableList<ModelSpeak> = mutableListOf()
    private var recodedPlayer: MediaPlayer? = null
    private var player: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeakBinding.inflate(inflater, container, false)
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
        communicator = requireActivity() as SpeakActivityInterface
        speak = db.speakDao().getSpeakByUnitId(unitId)
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            @ColorInt val color: Int = ResourceProvider.getColor(requireActivity(), it.name)
            binding.stepBackground.setBackgroundColor(color)
            binding.listenButton.backgroundTintList = ColorStateList.valueOf(color)
        }
        setupPicture()
        setupAudio()
        setupSection()
    }

    private fun setupPicture() {
        val picture = speak[stepIndex].picture
        val pictureImage = File(fileFolder.absolutePath, picture.value)
        Glide.with(this).load(pictureImage).placeholder(R.color.grey).into(binding.stepImage)
        if (!picture.credits.isNullOrBlank()) {
            binding.stepImageCredits.setVisible()
            binding.stepImageCredits.text = picture.credits
        }
    }

    private fun setupAudio() {
        val audio = speak[stepIndex].audio
        binding.listenButton.setOnClickListener {
            if (!recording) playAudio(audio.value)
        }
        if (!audio.credits.isNullOrBlank()) {
            binding.audioCredits.setVisible()
            binding.audioCredits.text = audio.credits
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        binding.step.text = "${stepIndex + 1}/${speak.size}"
        binding.record.setOnClickListener {
            if (isLocked) return@setOnClickListener
            if (recording) endRecording() else startRecording()
        }
        binding.next.disable()
        binding.next.setOnClickListener {
            if (recording) endRecording()
            destroyPlayers()
            destroyFile()
            if (stepIndex + 1 < speak.size) communicator.goToNext(stepIndex + 1)
            else communicator.goToFinish()
        }
        binding.listenAgain.disable()
        binding.listenAgain.setOnClickListener {
            playRecordedAudio()
        }
    }

    private fun playAudio(audio: String) {
        recodedPlayer?.pause()
        when {
            player == null -> {
                val audioFile = File(fileFolder.absolutePath, audio)
                player = initPlayer(audioFile)
                player?.start()
            }

            player?.isPlaying == true -> player?.pause()
            else -> player?.start()
        }
    }

    private fun startRecording() {
        player?.pause()
        recodedPlayer?.pause()
        binding.listenAgain.disable()
        binding.next.disable()
        Dexter.withContext(requireActivity()).withPermission(Manifest.permission.RECORD_AUDIO)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    setupRecorder()
                    recording = true
                    binding.record.setImageResource(R.drawable.stop)
                    binding.pulsator.start()
                    binding.record.compatElevation =
                        LayoutUtils.dpToPx(requireActivity(), 16).toFloat()
                    recorder.start()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    binding.record.isEnabled = false
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).check()
    }

    private fun endRecording() {
        isLocked = true
        binding.loading.setVisible()
        binding.pulsator.stop()
        binding.record.compatElevation = LayoutUtils.dpToPx(requireActivity(), 8).toFloat()
        binding.record.setImageResource(R.drawable.mic)
        Handler(Looper.getMainLooper()).postDelayed({
            recording = false
            isLocked = false
            recorder.stop()
            binding.loading.setGone()
            binding.listenAgain.enable()
            binding.next.enable()
        }, 1000)
    }

    @Suppress("DEPRECATION")
    private fun setupRecorder() {
        recorder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(requireActivity()) else MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
        val file = File(requireActivity().filesDir, "recording")
        recorder.setOutputFile(file.absolutePath)
        recorder.prepare()
    }

    private fun playRecordedAudio() {
        player?.pause()
        when {
            recodedPlayer == null -> {
                val audioFile = File(requireActivity().filesDir, "recording")
                recodedPlayer = initPlayer(audioFile)
                recodedPlayer?.start()
            }

            recodedPlayer?.isPlaying == true -> recodedPlayer?.pause()
            else -> recodedPlayer?.start()
        }
    }

    private fun initPlayer(audioFile: File): MediaPlayer {
        val player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
        player.setOnCompletionListener {
            if (canAccessActivity) player.pause()
        }
        return player
    }

    private fun destroyPlayers() {
        player?.release()
        recodedPlayer?.release()
    }

    private fun destroyFile() {
        val file = File(requireActivity().filesDir, "recording")
        if (file.exists()) file.delete()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayers()
        destroyFile()
    }

    companion object {
        private const val EXTRA_STEP = "extra step int position"

        fun newInstance(unitId: Int, stepIndex: Int): FragmentSpeak = FragmentSpeak().apply {
            arguments = Bundle().apply {
                putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
                putInt(EXTRA_STEP, stepIndex)
            }
        }
    }
}