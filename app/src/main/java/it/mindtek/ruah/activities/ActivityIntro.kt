package it.mindtek.ruah.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.mindtek.ruah.R
import it.mindtek.ruah.config.LayoutUtils
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityIntroBinding
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Exercise
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setColor
import it.mindtek.ruah.kotlin.extensions.setGone
import it.mindtek.ruah.kotlin.extensions.setVisible

class ActivityIntro : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private lateinit var exercise: Exercise
    private lateinit var unitObject: ModelUnit
    private lateinit var player: MediaPlayer
    private var unitId: Int = -1
    private var finish: Boolean = false
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            exercise = Exercise.from(it.getIntExtra(EXTRA_EXERCISE_ID, -1))
            finish = it.getBooleanExtra(EXTRA_IS_FINISH, false)
        }
        player = MediaPlayer.create(this, exercise.audio)
        player.setOnCompletionListener {
            player.release()
        }
        if (finish) {
            binding.buttonNext.setGone()
            binding.done.setVisible()
            binding.sectionDescription.text = getString(R.string.congrats)
            binding.fabBack.setOnClickListener {
                completeExercise(exercise)
                goToExercise()
            }
        } else {
            binding.done.setGone()
            binding.fabBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            binding.buttonNext.setVisible()
            binding.buttonNext.setOnClickListener {
                dispatch()
            }
            binding.sectionDescription.text = getString(exercise.description)
            player.start()
        }
        Glide.with(this).load(exercise.icon)
            .override(LayoutUtils.dpToPx(this, 24), LayoutUtils.dpToPx(this, 24))
            .into(binding.sectionIcon)
        binding.sectionName.text = getString(exercise.title)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToExercise()
            }
        })
        db.unitDao().getUnitByIdAsync(unitId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                unitObject = it
                @ColorInt val color: Int = ResourceProvider.getColor(this, it.name)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM)
                    window.statusBarColor = ResourceProvider.getColor(this, "${it.name}_dark")
                binding.coordinator.setBackgroundColor(color)
                binding.fabBack.imageTintList = ColorStateList.valueOf(color)
                binding.unitIcon.setImageResource(ResourceProvider.getIcon(this, it.name))
                val play: Drawable? = ContextCompat.getDrawable(this, R.drawable.play)
                binding.buttonNext.setCompoundDrawables(null, null, play, null)
                binding.buttonNext.setColor(color)
            }, { error ->
                Log.e("ActivityIntro", "Error loading unit", error)
            }).let {
                disposable.add(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        disposable.clear()
    }

    private fun completeExercise(exercise: Exercise) {
        unitObject.completed.add(exercise.value)
        db.unitDao().updateUnit(unitObject)
    }

    private fun dispatch() {
        when (exercise.value) {
            Exercise.UNDERSTAND.value -> goToUnderstand()
            Exercise.TALK.value -> goToSpeak()
            Exercise.READ.value -> goToRead()
            Exercise.WRITE.value -> goToWrite()
            Exercise.FINAL_TEST.value -> goToFinalTest()
        }
    }

    private fun goToExercise() {
        startActivity(Intent(this, ActivityUnit::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
        })
        finish()
    }

    private fun goToSpeak() {
        if (check(db.speakDao().countByUnitId(unitId))) startActivity(
            Intent(this, ActivitySpeak::class.java).apply {
                putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            }
        )
    }

    private fun goToUnderstand() {
        if (check(db.understandDao().countByUnitId(unitId))) startActivity(
            Intent(this, ActivityUnderstand::class.java).apply {
                putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
                putExtra(ActivityUnderstand.STEP_INDEX, 0)
            })
    }

    private fun goToRead() {
        if (check(db.readDao().countByUnitId(unitId))) startActivity(
            Intent(this, ActivityRead::class.java).apply {
                putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            })
    }

    private fun goToWrite() {
        if (check(db.writeDao().countByUnitId(unitId))) startActivity(
            Intent(this, ActivityWrite::class.java).apply {
                putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            })
    }

    private fun goToFinalTest() {
        if (check(db.finalTestDao().countByUnitId(unitId))) {
            val intent = Intent(this, ActivityFinalTest::class.java)
            intent.putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            startActivity(intent)
        }
    }

    private fun check(count: Int): Boolean {
        if (count == 0) {
            Snackbar.make(binding.root, R.string.exercise_empty_error, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.red)).show()
            return false
        }
        player.release()
        return true
    }

    companion object {
        const val EXTRA_EXERCISE_ID: String = "exercise_id"
        const val EXTRA_IS_FINISH: String = "extra_is_finish_section"
    }
}