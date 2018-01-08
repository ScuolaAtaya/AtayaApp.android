package it.mindtek.ruah.fragments.speak

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityIntro
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelSpeak
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.interfaces.SpeakActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.disable
import it.mindtek.ruah.kotlin.extensions.enable
import kotlinx.android.synthetic.main.fragment_speak.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.support.v4.dip
import java.io.File

/**
 * Created by alessandrogaboardi on 15/12/2017.
 */
class FragmentSpeak : Fragment() {
    var unitId: Int = -1
    var category: Category? = null
    var stepIndex: Int = -1
    var player: MediaPlayer? = null
    var recorder: MediaRecorder? = null
    var speak: MutableList<ModelSpeak> = mutableListOf()
    var communicator: SpeakActivityInterface? = null
    var recording = false
    val REQUEST_PERMISSION_AUDIO = 20183

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_speak, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID))
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            if (it.containsKey(ActivityIntro.EXTRA_CATEGORY_ID))
                category = Category.from(it.getInt(ActivityIntro.EXTRA_CATEGORY_ID))
            if (it.containsKey(EXTRA_STEP))
                stepIndex = it.getInt(EXTRA_STEP)
        }
        if (unitId == -1 || category == null || stepIndex == -1)
            activity.finish()
        initCommunicators()
        speak = db.speakDao().getSpeakByUnitId(unitId)
        setup()
    }

    private fun initCommunicators() {
        if (activity is SpeakActivityInterface)
            communicator = activity as SpeakActivityInterface
    }

    @SuppressLint("RestrictedApi")
    private fun setup() {
        setupPicture()
        setupButtons()
        setupSteps()
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(activity, it.color)
            stepBackground.backgroundColor = color
            listenButton.supportBackgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun setupPicture() {
        GlideApp.with(this).load("https://ichef-1.bbci.co.uk/news/976/media/images/83351000/jpg/_83351965_explorer273lincolnshirewoldssouthpicturebynicholassilkstone.jpg").placeholder(R.color.grey).into(stepImage)
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
            if (recording)
                endRecording()
            destroyFile()
            dispatch()
        }
        listenButton.setOnClickListener {
            playAudio()
        }
        listenAgain.setOnClickListener {
            playRecordedAudio()
        }
        listenAgain.disable()
        next.disable()
    }

    private fun setupSteps() {
        step.text = "${stepIndex + 1}/${speak.size}"
    }

    private fun startRecording() {
        destroyPlayer()
        if (initRecorder()) {
            recording = true
            record.setImageResource(R.drawable.stop)
            pulsator.start()
            record.compatElevation = dip(16f).toFloat()
            recorder?.start()
        }
    }

    private fun initRecorder(): Boolean {
        return if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
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
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED)
                        setupRecorder()
                    else
                        disableRecorder()
                }
            }
        }
    }

    private fun disableRecorder() {
        record.isEnabled = false
    }

    private fun setupRecorder() {
        if (recorder != null)
            destroyRecorder()

        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val file = File(activity.filesDir, "recording")
        recorder!!.setOutputFile(file.absolutePath)
        recorder?.prepare()
    }

    private fun endRecording() {
        recording = false
        pulsator.stop()
        record.compatElevation = dip(8f).toFloat()
        recorder?.stop()
        listenAgain.enable()
        next.enable()
        record.setImageResource(R.drawable.mic)
    }

    private fun playAudio() {
        if (player != null)
            destroyPlayer()
        player = MediaPlayer.create(activity, R.raw.voice)
        player?.setOnCompletionListener {
            destroyPlayer()
        }
        player?.start()
    }

    private fun dispatch() {
        if (stepIndex + 1 < speak.size) {
            goToNext()
        } else {
            finish()
        }
    }

    private fun finish() {
        communicator?.goToFinish()
    }

    private fun goToNext() {
        communicator?.goToSpeak(stepIndex + 1)
    }

    private fun playRecordedAudio() {
        if (player != null)
            destroyPlayer()
        val record = File(activity.filesDir, "recording")
        val uri = Uri.parse(record.absolutePath)
        player = MediaPlayer.create(activity, uri)
        player?.setOnCompletionListener {
            destroyPlayer()
        }
        player?.start()
    }

    private fun destroyPlayer() {
        player?.release()
    }

    private fun destroyRecorder() {
        recorder?.release()
    }

    private fun destroyFile() {
        val file = File(activity.filesDir, "recording")
        if (file.exists()) {
            file.delete()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
        destroyFile()
    }

    companion object {
        val EXTRA_STEP = "extra step int position"

        fun newInstance(): FragmentSpeak = FragmentSpeak()

        fun newInstance(unit_id: Int, category: Category, stepIndex: Int): FragmentSpeak {
            val frag = FragmentSpeak()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unit_id)
            bundle.putInt(ActivityIntro.EXTRA_CATEGORY_ID, category.value)
            bundle.putInt(EXTRA_STEP, stepIndex)
            frag.arguments = bundle
            return frag
        }
    }
}