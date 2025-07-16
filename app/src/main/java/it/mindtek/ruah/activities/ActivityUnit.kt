package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityUnitBinding
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Exercise
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setTopPadding
import it.mindtek.ruah.kotlin.extensions.setVisible

class ActivityUnit : AppCompatActivity() {
    private lateinit var binding: ActivityUnitBinding
    private var unitId: Int = -1
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activityUnitToolbar.setTopPadding()
        setSupportActionBar(binding.activityUnitToolbar)
        intent?.let {
            unitId = it.getIntExtra(EXTRA_UNIT_ID, -1)
        }
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    @Suppress("DEPRECATION")
    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.capire.setOnClickListener {
            openIntro(Exercise.UNDERSTAND.value)
        }
        binding.parlare.setOnClickListener {
            openIntro(Exercise.TALK.value)
        }
        binding.leggere.setOnClickListener {
            openIntro(Exercise.READ.value)
        }
        binding.scrivere.setOnClickListener {
            openIntro(Exercise.WRITE.value)
        }
        db.unitDao().getUnitByIdAsync(unitId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                val isUnderstandCompleted: Boolean =
                    isExerciseCompleted(it, Exercise.UNDERSTAND.value)
                val isSpeakingCompleted: Boolean = isExerciseCompleted(it, Exercise.TALK.value)
                val isReadingCompleted: Boolean = isExerciseCompleted(it, Exercise.READ.value)
                val isWritingCompleted: Boolean = isExerciseCompleted(it, Exercise.WRITE.value)
                if (isUnderstandCompleted) binding.capiamoDone.setVisible()
                if (isSpeakingCompleted) binding.parliamoDone.setVisible()
                if (isReadingCompleted) binding.leggiamoDone.setVisible()
                if (isWritingCompleted) binding.scriviamoDone.setVisible()
                if (isExerciseCompleted(it, Exercise.FINAL_TEST.value))
                    binding.verificaFinaleDone.setVisible()
                if (isUnderstandCompleted && isSpeakingCompleted && isReadingCompleted && isWritingCompleted) {
                    binding.iconVerificaFinale.setImageResource(R.drawable.verifica_finale)
                    binding.verificaFinaleText.setTextColor(
                        ContextCompat.getColor(this, R.color.white)
                    )
                    binding.verificaFinale.setOnClickListener {
                        openIntro(Exercise.FINAL_TEST.value)
                    }
                } else {
                    binding.iconVerificaFinale.setImageResource(R.drawable.verifica_finale_disattivato)
                    binding.verificaFinaleText.setTextColor(
                        ContextCompat.getColor(this, R.color.whiteAlpha50)
                    )
                }
                supportActionBar?.title = getString(ResourceProvider.getString(this, it.name))
                @ColorInt val color: Int = ResourceProvider.getColor(this, it.name)
                supportActionBar?.setBackgroundDrawable(color.toDrawable())
                binding.constraint.setBackgroundColor(color)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM)
                    window.statusBarColor = ResourceProvider.getColor(this, "${it.name}_dark")
            }, {
                Log.e("ActivityUnit", "Error loading unit data", it)
            }).let {
                disposable.add(it)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun openIntro(exerciseId: Int) {
        startActivity(Intent(this, ActivityIntro::class.java).apply {
            putExtra(EXTRA_UNIT_ID, unitId)
            putExtra(ActivityIntro.EXTRA_EXERCISE_ID, exerciseId)
        })
    }

    private fun isExerciseCompleted(modelUnit: ModelUnit, exerciseId: Int): Boolean =
        modelUnit.completed.any {
            it == exerciseId
        }

    companion object {
        const val EXTRA_UNIT_ID: String = "unit_id_extra"
    }
}