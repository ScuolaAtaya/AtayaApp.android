package it.mindtek.ruah.fragments.understand

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.canAccessActivity
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.fileFolder
import kotlinx.android.synthetic.main.fragment_understand_video.*
import java.io.File


class FragmentUnderstandVideo : Fragment() {
    private var unitId: Int = -1
    private var audioPlayer: MediaPlayer? = null
    private var videoPlayer: YouTubePlayer? = null
    private var videoUrl: String = ""
    private var communicator: UnderstandActivityInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_understand_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCommunicators()
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID)) {
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            }
        }
    }

    // CAST_NEVER_SUCCEEDS can be ignored - happens because Youtube SDK's fragment is not androidx.Fragment, but Jetifier will take care of that and cast will succeed
    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun showVideo(video: String?) {
        video?.let {
            videoUrl = it
            val playerFragment = childFragmentManager.findFragmentById(R.id.videoPlayer) as YouTubePlayerSupportFragment
            playerFragment.initialize(getString(R.string.youtube_api_key), object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer, p2: Boolean) {
                    videoPlayer = p1
                    p1.setPlayerStateChangeListener(object : YouTubePlayer.PlayerStateChangeListener {
                        override fun onAdStarted() {}
                        override fun onLoading() {}
                        override fun onVideoStarted() {}
                        override fun onLoaded(p0: String?) {}
                        override fun onVideoEnded() {
                            onVideoEnd()
                        }

                        override fun onError(p0: YouTubePlayer.ErrorReason?) {}
                    })
                    p1.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
                        override fun onSeekTo(p0: Int) {}
                        override fun onBuffering(p0: Boolean) {}
                        override fun onPlaying() {
                            destroyPlayer()
                        }

                        override fun onStopped() {}
                        override fun onPaused() {}
                    })
                    p1.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                    p1.loadVideo(videoUrl)
                }

                override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                    println("YOUTUBE ERROR")
                }
            })
        }
    }

    private fun getCommunicators() {
        if (activity is UnderstandActivityInterface) {
            communicator = activity as UnderstandActivityInterface
        }
    }

    override fun onResume() {
        super.onResume()
        if (unitId == -1)
            activity?.finish()
        else {
            setupNext()
            val categoriesObservable = db.understandDao().getUnderstandAsync()
            categoriesObservable.observe(this, Observer { println(it) })
            val category = db.understandDao().getUnderstandByUnitId(unitId)
            category?.let {
                showVideo(it.category?.video_url!!.value)
                setupListen(it.category?.audio!!.value)
            }
        }
    }

    private fun setupListen(audio: String?) {
        listen.setOnClickListener {
            audio?.let {
                playAudio(it)
            }
        }
    }

    private fun setupNext() {
        next.setOnClickListener {
            destroyPlayer()
            goToQuestions()
        }
        disableNext()
    }

    private fun goToQuestions() {
        communicator?.openQuestion(0)
    }

    private fun playAudio(audio: String) {
        videoPlayer?.pause()
        if (audioPlayer != null)
            destroyPlayer()
        val audioFile = File(fileFolder.absolutePath, audio)
        audioPlayer = MediaPlayer.create(activity, Uri.fromFile(audioFile))
        audioPlayer?.setOnCompletionListener {
            if (canAccessActivity) {
                enableNext()
                destroyPlayer()
            }
        }
        audioPlayer?.start()
    }

    private fun destroyPlayer() {
        audioPlayer?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    private fun disableNext() {
        next.isEnabled = false
    }

    private fun enableNext() {
        next.isEnabled = true
    }

    private fun onVideoEnd() {
        if (canAccessActivity)
            enableNext()
    }

    companion object {
        fun newInstance(unit_id: Int): FragmentUnderstandVideo {
            val fragment = FragmentUnderstandVideo()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unit_id)
            fragment.arguments = bundle
            return fragment
        }
    }
}