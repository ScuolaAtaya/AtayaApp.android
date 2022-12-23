package it.mindtek.ruah.fragments.understand

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnderstand
import it.mindtek.ruah.activities.ActivityUnderstandQuestion
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.db.models.ModelMedia
import it.mindtek.ruah.kotlin.extensions.*
import it.mindtek.ruah.pojos.PojoUnderstand
import kotlinx.android.synthetic.main.fragment_understand_video.*
import org.jetbrains.anko.backgroundColor
import java.io.File

class FragmentUnderstandVideo : Fragment() {
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var isVideoWatched: Boolean = false
    private var understand: MutableList<PojoUnderstand> = mutableListOf()
    private var audioPlayer: MediaPlayer? = null
    private var videoPlayer: YouTubePlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_understand_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID))
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            if (it.containsKey(ActivityUnderstand.STEP_INDEX))
                stepIndex = it.getInt(ActivityUnderstand.STEP_INDEX)
            if (it.containsKey(ActivityUnderstand.VIDEO_WATCHED))
                isVideoWatched = it.getBoolean(ActivityUnderstand.VIDEO_WATCHED)
        }
        setup()
    }

    private fun setup() {
        understand = db.understandDao().getUnderstandByUnitId(unitId)
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            stepLayout.backgroundColor = ResourceProvider.getColor(requireActivity(), it.name)
        }
        setupVideoAndAudio(understand[stepIndex])
        setupSection()
    }

    private fun setupVideoAndAudio(understand: PojoUnderstand) {
        understand.understand?.let {
            setupVideo(it.video_url)
            setupAudio(it.audio)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        step.text = "${stepIndex + 1}/${understand.size}"
        next.isEnabled = isVideoWatched
        next.setOnClickListener {
            destroyPlayers()
            val intent = Intent(requireActivity(), ActivityUnderstandQuestion::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            intent.putExtra(ActivityUnderstand.STEP_INDEX, stepIndex)
            startActivity(intent)
        }
    }

    // CAST_NEVER_SUCCEEDS can be ignored - happens because Youtube SDK's fragment is not androidx.Fragment, but Jetifier will take care of that and cast will succeed
    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun setupVideo(video: ModelMedia) {
        val playerFragment =
            childFragmentManager.findFragmentById(R.id.videoPlayer) as YouTubePlayerSupportFragment
        playerFragment.initialize(
            getString(R.string.youtube_api_key),
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubePlayer,
                    p2: Boolean
                ) {
                    videoPlayer = p1
                    p1.setPlayerStateChangeListener(object :
                        YouTubePlayer.PlayerStateChangeListener {
                        override fun onAdStarted() {}
                        override fun onLoading() {}
                        override fun onVideoStarted() {}
                        override fun onLoaded(p0: String?) {}
                        override fun onVideoEnded() {
                            if (canAccessActivity) next.enable()
                        }

                        override fun onError(p0: YouTubePlayer.ErrorReason?) {}
                    })
                    p1.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
                        override fun onSeekTo(p0: Int) {}
                        override fun onBuffering(p0: Boolean) {}
                        override fun onPlaying() {
                            audioPlayer?.pause()
                        }

                        override fun onStopped() {}
                        override fun onPaused() {}
                    })
                    p1.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                    p1.loadVideo(video.value)
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                }
            })
        if (!video.credits.isNullOrBlank()) {
            videoCredits.setVisible()
            videoCredits.text = video.credits
        }
    }

    private fun setupAudio(audio: ModelMedia) {
        listen.setOnClickListener {
            playAudio(audio.value)
        }
        if (!audio.credits.isNullOrBlank()) {
            audioCredits.setVisible()
            audioCredits.text = audio.credits
        }
    }

    private fun playAudio(audio: String) {
        videoPlayer?.pause()
        when {
            audioPlayer == null -> {
                val audioFile = File(fileFolder.absolutePath, audio)
                audioPlayer = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
                audioPlayer?.setOnCompletionListener {
                    if (canAccessActivity) audioPlayer?.pause()
                }
                audioPlayer?.start()
            }
            audioPlayer?.isPlaying == true -> audioPlayer?.pause()
            else -> audioPlayer?.start()
        }
    }

    private fun destroyPlayers() {
        audioPlayer?.release()
        videoPlayer?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayers()
    }

    companion object {
        fun newInstance(
            unit_id: Int,
            stepIndex: Int,
            isVideoWatched: Boolean
        ): FragmentUnderstandVideo {
            val fragment = FragmentUnderstandVideo()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unit_id)
            bundle.putInt(ActivityUnderstand.STEP_INDEX, stepIndex)
            bundle.putBoolean(ActivityUnderstand.VIDEO_WATCHED, isVideoWatched)
            fragment.arguments = bundle
            return fragment
        }
    }
}