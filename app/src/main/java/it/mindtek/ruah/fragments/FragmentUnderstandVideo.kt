package it.mindtek.ruah.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnderstand
import it.mindtek.ruah.activities.ActivityUnderstandQuestion
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.FragmentUnderstandVideoBinding
import it.mindtek.ruah.db.models.ModelMedia
import it.mindtek.ruah.kotlin.extensions.*
import it.mindtek.ruah.pojos.PojoUnderstand
import java.io.File

class FragmentUnderstandVideo : Fragment() {
    private lateinit var binding: FragmentUnderstandVideoBinding
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var isVideoWatched: Boolean = false
    private var understand: MutableList<PojoUnderstand> = mutableListOf()
    private var audioPlayer: MediaPlayer? = null
    private var videoPlayer: YouTubePlayer? = null
    private var videoPlayerView: YouTubePlayerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUnderstandVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

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
        videoPlayerView = view.findViewById(R.id.videoPlayerView)
        videoPlayerView?.let {
            lifecycle.addObserver(it)
        }
        setup()
    }

    private fun setup() {
        understand = db.understandDao().getUnderstandByUnitId(unitId)
        db.unitDao().getUnitById(unitId)?.let {
            binding.stepLayout.setBackgroundColor(
                ResourceProvider.getColor(requireActivity(), it.name)
            )
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
        binding.step.text = "${stepIndex + 1}/${understand.size}"
        binding.next.isEnabled = isVideoWatched
        binding.next.setOnClickListener {
            audioPlayer?.release()
            audioPlayer = null
            startActivity(Intent(requireActivity(), ActivityUnderstandQuestion::class.java).apply {
                putExtra(ActivityUnderstand.STEP_INDEX, stepIndex)
                putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            })
        }
    }

    private fun setupVideo(video: ModelMedia) {
        videoPlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                videoPlayer = youTubePlayer
                videoPlayer?.loadVideo(video.value, 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                when (state) {
                    PlayerConstants.PlayerState.PLAYING -> audioPlayer?.pause()
                    PlayerConstants.PlayerState.ENDED -> {
                        if (canAccessActivity) {
                            isVideoWatched = true
                            binding.next.enable()
                        }
                    }

                    else -> {}
                }
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                Log.e("YouTubePlayerView", "Error: $error")
            }
        })
        if (!video.credits.isNullOrBlank()) {
            binding.videoCredits.setVisible()
            binding.videoCredits.text = video.credits
        }
    }

    private fun setupAudio(audio: ModelMedia) {
        binding.listen.setOnClickListener {
            playAudio(audio.value)
        }
        if (!audio.credits.isNullOrBlank()) {
            binding.audioCredits.setVisible()
            binding.audioCredits.text = audio.credits
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

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer?.release()
    }

    companion object {
        fun newInstance(
            unitId: Int,
            stepIndex: Int,
            isVideoWatched: Boolean
        ): FragmentUnderstandVideo = FragmentUnderstandVideo().apply {
            arguments = Bundle().apply {
                putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
                putInt(ActivityUnderstand.STEP_INDEX, stepIndex)
                putBoolean(ActivityUnderstand.VIDEO_WATCHED, isVideoWatched)
            }
        }
    }
}