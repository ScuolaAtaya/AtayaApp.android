package it.mindtek.ruah.fragments.read


import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityIntro
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.AnswersAdapter
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.interfaces.ReadActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.fileFolder
import it.mindtek.ruah.pojos.PojoRead
import kotlinx.android.synthetic.main.fragment_read.*
import org.jetbrains.anko.backgroundColor
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class FragmentRead : Fragment() {
    var unitId: Int = -1
    var category: Category? = null
    var stepIndex: Int = -1
    var adapter: AnswersAdapter? = null
    var correctCount = 0
    var player: MediaPlayer? = null
    var communicator: ReadActivityInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_read, container, false)
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

        setup()
    }

    private fun initCommunicators() {
        if (activity is ReadActivityInterface)
            communicator = activity as ReadActivityInterface
    }

    private fun setup() {
        next.isEnabled = false

        val read = db.readDao().getReadByUnitId(unitId)
        setupSteps(read)
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(activity, it.color)
            stepBackground.backgroundColor = color
        }
        if (read.size > 0) {
            next.setOnClickListener {
                if (stepIndex + 1 < read.size) {
                    if (player != null)
                        destroyPlayer()
                    communicator?.goToNext(stepIndex + 1)
                } else {
                    communicator?.goToFinish()
                }
            }
            val currentRead = read[stepIndex]
            currentRead.read?.let {
                val pictureFile = File(fileFolder.absolutePath, it.picture)
                GlideApp.with(this).load(pictureFile).placeholder(R.color.grey).into(picture)
            }
            setupAnswers(currentRead)
        } else {
            activity.finish()
        }
    }

    private fun setupSteps(read: MutableList<PojoRead>) {
        step.text = "${stepIndex + 1}/${read.size}"
    }

    private fun setupAnswers(read: PojoRead) {
        answers.layoutManager = LinearLayoutManager(activity)
        adapter = AnswersAdapter(read.answersConverted, { answer ->
            val correctNum = read.answersConverted.map { it.correct }.count { it }
            if (answer.correct)
                correctCount += 1
            if (correctCount >= correctNum) {
                next.isEnabled = true
            }
        }, { answer ->
            playAudio(answer.audio)
//            playAudio()
        })
        answers.adapter = adapter
    }

    private fun playAudio(audio: String) {
        if (player != null)
            destroyPlayer()
        val audioFile = File(fileFolder.absolutePath, audio)
        player = MediaPlayer.create(activity, Uri.fromFile(audioFile))
        player?.setOnCompletionListener {
            destroyPlayer()
        }
        player?.start()
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

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    private fun destroyPlayer() {
        player?.release()
    }

    companion object {
        val EXTRA_STEP = "extra_current_step_number"

        fun newInstance(): FragmentRead = FragmentRead()

        fun newInstance(unit_id: Int, category: Category, stepIndex: Int): FragmentRead {
            val frag = FragmentRead()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unit_id)
            bundle.putInt(ActivityIntro.EXTRA_CATEGORY_ID, category.value)
            bundle.putInt(EXTRA_STEP, stepIndex)
            frag.arguments = bundle
            return frag
        }
    }
}
