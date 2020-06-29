package it.mindtek.ruah.fragments.speak

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelSpeak
import it.mindtek.ruah.interfaces.SpeakActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import kotlinx.android.synthetic.main.fragment_speak.*
import kotlinx.android.synthetic.main.fragment_speak.next
import kotlinx.android.synthetic.main.fragment_speak.step
import kotlinx.android.synthetic.main.fragment_speak.stepImage
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import java.io.File

/**
 * Created by alessandrogaboardi on 15/12/2017.
 */
class FragmentSpeak : Fragment() {
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var player: MediaPlayer? = null
    private var recodedPlayer: MediaPlayer? = null
    private var recorder: MediaRecorder? = null
    private var speak: MutableList<ModelSpeak> = mutableListOf()
    private var communicator: SpeakActivityInterface? = null
    private var recording = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_speak, container, false)
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
        if (requireActivity() is SpeakActivityInterface) {
            communicator = requireActivity() as SpeakActivityInterface
        }
        speak = db.speakDao().getSpeakByUnitId(unitId)
        if (speak.size == 0 || speak.size <= stepIndex) {
            requireActivity().finish()
        }
        setupPicture()
        setupButtons()
        setupSteps()
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(requireActivity(), it.color)
            stepBackground.backgroundColor = color
            listenButton.supportBackgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun setupPicture() {
        val picture = File(fileFolder.absolutePath, speak[stepIndex].picture.value)
        GlideApp.with(this).load(picture).placeholder(R.color.grey).into(stepImage)
    }

    private fun setupButtons() {
        record.setOnClickListener {
            if (recording) {
                endRecording()
            } else {
                startRecording()
            }
        }
        next.setOnClickListener {
            if (recording) {
                endRecording()
            }
            destroyPlayers()
            destroyFile()
            dispatch()
        }
        listenButton.setOnClickListener {
            playAudio(speak[stepIndex].audio.value)
        }
        listenAgain.setOnClickListener {
            playRecordedAudio()
        }
        listenAgain.disable()
        next.disable()
    }

    @SuppressLint("SetTextI18n")
    private fun setupSteps() {
        step.text = "${stepIndex + 1}/${speak.size}"
    }

    private fun startRecording() {
        player?.pause()
        recodedPlayer?.pause()
        if (initRecorder()) {
            recording = true
            record.setImageResource(R.drawable.stop)
            pulsator.start()
            record.compatElevation = requireActivity().dip(16f).toFloat()
            recorder?.start()
        }
    }

    private fun initRecorder(): Boolean {
        return if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setupRecorder()
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_AUDIO)
            false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_AUDIO) {
            permissions.forEachIndexed { index, s ->
                if (s == Manifest.permission.RECORD_AUDIO) {
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        setupRecorder()
                    } else {
                        record.isEnabled = false
                    }
                }
            }
        }
    }

    private fun setupRecorder() {
        if (recorder != null) {
            recorder?.release()
        }
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val file = File(requireActivity().filesDir, "recording")
        recorder!!.setOutputFile(file.absolutePath)
        recorder?.prepare()
    }

    private fun endRecording() {
        recording = false
        pulsator.stop()
        record.compatElevation = requireActivity().dip(8f).toFloat()
        recorder?.stop()
        listenAgain.enable()
        next.enable()
        record.setImageResource(R.drawable.mic)
    }

    private fun playAudio(audio: String) {
        recodedPlayer?.pause()
        when {
            player == null -> {
                val audioFile = File(fileFolder.absolutePath, audio)
                player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
                player!!.setOnCompletionListener {
                    if (canAccessActivity) {
                        next.isEnabled = true
                        player!!.pause()
                    }
                }
                player!!.start()
            }
            player!!.isPlaying -> player!!.pause()
            else -> player!!.start()
        }
    }

    private fun dispatch() {
        if (stepIndex + 1 < speak.size) {
            communicator?.goToNext(stepIndex + 1)
        } else {
            communicator?.goToFinish()
        }
    }

    private fun playRecordedAudio() {
        player?.pause()
        when {
            recodedPlayer == null -> {
                val audioFile = File(fileFolder.absolutePath, "recording")
                recodedPlayer = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
                recodedPlayer!!.setOnCompletionListener {
                    if (canAccessActivity) {
                        recodedPlayer!!.pause()
                    }
                }
                recodedPlayer!!.start()
            }
            recodedPlayer!!.isPlaying -> recodedPlayer!!.pause()
            else -> recodedPlayer!!.start()
        }
    }

    private fun destroyPlayers() {
        player?.release()
        recodedPlayer?.release()
    }

    private fun destroyFile() {
        val file = File(requireActivity().filesDir, "recording")
        if (file.exists()) {
            file.delete()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayers()
        destroyFile()
    }

    companion object {
        const val EXTRA_STEP = "extra step int position"
        const val REQUEST_PERMISSION_AUDIO = 20183

        fun newInstance(unitId: Int, stepIndex: Int): FragmentSpeak {
            val frag = FragmentSpeak()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
            bundle.putInt(EXTRA_STEP, stepIndex)
            frag.arguments = bundle
            return frag
        }
    }
}