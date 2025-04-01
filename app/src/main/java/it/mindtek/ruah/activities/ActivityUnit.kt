package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityUnitBinding
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.setTopPadding
import it.mindtek.ruah.kotlin.extensions.setVisible

class ActivityUnit : AppCompatActivity() {
    private lateinit var binding: ActivityUnitBinding
    private var unitId: Int = -1

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

    @Suppress("DEPRECATION")
    private fun setup() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.capire.setOnClickListener {
            openIntro(Category.UNDERSTAND.value)
        }
        binding.parlare.setOnClickListener {
            openIntro(Category.TALK.value)
        }
        binding.leggere.setOnClickListener {
            openIntro(Category.READ.value)
        }
        binding.scrivere.setOnClickListener {
            openIntro(Category.WRITE.value)
        }
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this) {
            it?.let {
                val isUnderstandCompleted = isCategoryCompleted(it, Category.UNDERSTAND.value)
                val isSpeakingCompleted = isCategoryCompleted(it, Category.TALK.value)
                val isReadingCompleted = isCategoryCompleted(it, Category.READ.value)
                val isWritingCompleted = isCategoryCompleted(it, Category.WRITE.value)
                val isFinalTestCompleted = isCategoryCompleted(it, Category.FINAL_TEST.value)
                if (isUnderstandCompleted) binding.capiamoDone.setVisible()
                if (isSpeakingCompleted) binding.parliamoDone.setVisible()
                if (isReadingCompleted) binding.leggiamoDone.setVisible()
                if (isWritingCompleted) binding.scriviamoDone.setVisible()
                if (isFinalTestCompleted) binding.verificaFinaleDone.setVisible()
                if (isUnderstandCompleted && isSpeakingCompleted && isReadingCompleted && isWritingCompleted) {
                    binding.iconVerificaFinale.setImageResource(R.drawable.verifica_finale)
                    binding.verificaFinaleText.setTextColor(
                        ContextCompat.getColor(this, R.color.white)
                    )
                    binding.verificaFinale.setOnClickListener {
                        openIntro(Category.FINAL_TEST.value)
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
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun openIntro(categoryId: Int) {
        startActivity(Intent(this, ActivityIntro::class.java).apply {
            putExtra(EXTRA_UNIT_ID, unitId)
            putExtra(ActivityIntro.EXTRA_CATEGORY_ID, categoryId)
        })
    }

    private fun isCategoryCompleted(modelUnit: ModelUnit, categoryId: Int): Boolean =
        modelUnit.completed.any {
            it == categoryId
        }

    companion object {
        const val EXTRA_UNIT_ID = "unit_id_extra"
    }
}