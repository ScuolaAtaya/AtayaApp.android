package it.mindtek.ruah.fragments.understand


import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.interfaces.UnderstandActivityInterface
import it.mindtek.ruah.kotlin.extensions.canAccessActivity
import it.mindtek.ruah.kotlin.extensions.compat21
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.pojos.UnderstandPojo
import kotlinx.android.synthetic.main.fragment_understand_video.*
import org.jetbrains.anko.support.v4.find


/**
 * A simple [Fragment] subclass.
 */
class FragmentUnderstandVideo : Fragment(), Player.EventListener {
    private var unit_id: Int = -1
//    var player: SimpleExoPlayer? = null
    var audioPlayer: MediaPlayer? = null
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

    val videoListener = object : YouTubePlayer.OnInitializedListener{
        override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer, p2: Boolean) {
            p1.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
            p1.loadVideo("blBV9No0xGo")
        }

        override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
            println("YOUTUBE ERROR")
        }
    }

    private fun showVideo(video: String?) {
        video?.let {
            /*player = ExoPlayerFactory.newSimpleInstance(
                    DefaultRenderersFactory(activity),
                    DefaultTrackSelector(),
                    DefaultLoadControl()
            )
            player?.addListener(this)

            exoPlayer.player = player
            player?.playWhenReady = false

            val uri = Uri.parse(video)
            val mediaSource = buildMediaSource(uri)
            player?.prepare(mediaSource, true, false)*/
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
            }
        }
    }

    private fun setupNext() {
        next.setOnClickListener {
            goToQuestions()
        }
        disableNext()
    }

    private fun goToQuestions() {
        communicator?.openQuestion(0)
    }

    private fun playAudio(audio: String) {
        audioPlayer = MediaPlayer.create(activity, R.raw.voice)
        audioPlayer?.setOnCompletionListener {
            if (canAccessActivity) {
                enableNext()
                destroyPlayer()
            }
        }
        audioPlayer?.start()
    }

    private fun destroyPlayer() {
//        player?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
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

    private fun buildMediaSource(uri: Uri): MediaSource =
            ExtractorMediaSource(
                    uri,
                    DefaultHttpDataSourceFactory("ua"),
                    DefaultExtractorsFactory(),
                    null,
                    null)

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

    override fun onSeekProcessed() {}

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

    override fun onPlayerError(error: ExoPlaybackException?) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPositionDiscontinuity(reason: Int) {}

    override fun onRepeatModeChanged(repeatMode: Int) {}

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_ENDED) {
            if (canAccessActivity)
                enableNext()
        }
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
