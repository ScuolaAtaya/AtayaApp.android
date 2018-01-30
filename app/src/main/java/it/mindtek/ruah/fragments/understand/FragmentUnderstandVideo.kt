package it.mindtek.ruah.fragments.understand


import android.arch.lifecycle.Observer
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.canAccessActivity
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.fileFolder
import it.mindtek.ruah.pojos.UnderstandPojo
import kotlinx.android.synthetic.main.fragment_understand_video.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class FragmentUnderstandVideo : Fragment() {
    private var unit_id: Int = -1
    var audioPlayer: MediaPlayer? = null
    var videoPlayer: YouTubePlayer? = null
    var videoUrl: String = ""
    var communicator: UnderstandActivityInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_understand_video, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCommunicators()

        arguments?.let {
            if (arguments.containsKey(ActivityUnit.EXTRA_UNIT_ID)) {
                unit_id = arguments.getInt(ActivityUnit.EXTRA_UNIT_ID)
            }
        }
    }

    val videoListener = object : YouTubePlayer.OnInitializedListener {
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
            p1.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener{
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
//            p1.loadVideo("v_nF8LrPBSg")
        }

        override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
            println("YOUTUBE ERROR")
        }
    }

    private fun showVideo(video: String?) {
        video?.let {
            videoUrl = video
            val playerFragment = childFragmentManager.findFragmentById(R.id.videoPlayer) as YouTubePlayerSupportFragment
            playerFragment.initialize(getString(R.string.youtube_api_key), videoListener)
        }
    }

    private fun getCommunicators() {
        if (activity is UnderstandActivityInterface) {
            communicator = activity as UnderstandActivityInterface
        }
    }

    override fun onResume() {
        super.onResume()
        if (unit_id == -1)
            activity.finish()
        else {
            setupNext()
            val categoriesObservable = db.understandDao().getUnderstandAsync()
            categoriesObservable.observe(this, Observer<MutableList<UnderstandPojo>> { categories ->
                val cat = categories
                println(categories)
            })
            val category = db.understandDao().getUnderstandByUnitId(unit_id)
            category?.let {
                showVideo(it.category?.video_url)
                setupListen(it.category?.audio)
            }
        }
    }

    private fun setupListen(audio: String?) {
        listen.setOnClickListener {
            audio?.let {
                playAudio(it)
//                playAudio()
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
        if(audioPlayer != null)
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

    private fun playAudio() {
        videoPlayer?.pause()
        if (audioPlayer != null)
            destroyPlayer()
        audioPlayer = MediaPlayer.create(activity, R.raw.voice)
        audioPlayer?.setOnCompletionListener {
            enableNext()
            destroyPlayer()
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
        fun newInstance(): FragmentUnderstandVideo = FragmentUnderstandVideo()

        fun newInstance(unit_id: Int): FragmentUnderstandVideo {
            val fragment = FragmentUnderstandVideo()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unit_id)
            fragment.arguments = bundle
            return fragment
        }
    }
}
