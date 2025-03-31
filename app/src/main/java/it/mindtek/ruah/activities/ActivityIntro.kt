package it.mindtek.ruah.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import it.mindtek.ruah.R
import it.mindtek.ruah.config.LayoutUtils
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityIntroBinding
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.*

class ActivityIntro : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private lateinit var category: Category
    private lateinit var unitObject: ModelUnit
    private lateinit var player: MediaPlayer
    private var unitId: Int = -1
    private var finish: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
            category = Category.from(it.getIntExtra(EXTRA_CATEGORY_ID, -1))!!
            finish = it.getBooleanExtra(EXTRA_IS_FINISH, false)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToCategory()
            }
        })
        setup()
    }

    @Suppress("DEPRECATION")
    private fun setup() {
        player = MediaPlayer.create(this, category.audio)
        player.setOnCompletionListener {
            player.release()
        }
        if (finish) {
            binding.buttonNext.setGone()
            binding.done.setVisible()
            binding.sectionDescription.text = getString(R.string.congrats)
            binding.fabBack.setOnClickListener {
                completeCategory(category)
                goToCategory()
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
            binding.sectionDescription.text = getString(category.description)
            player.start()
        }
        Glide.with(this).load(category.icon)
            .override(LayoutUtils.dpToPx(this, 24), LayoutUtils.dpToPx(this, 24))
            .into(binding.sectionIcon)
        binding.sectionName.text = getString(category.title)
        val unitObservable = db.unitDao().getUnitByIdAsync(unitId)
        unitObservable.observe(this) {
            it?.let {
                unitObject = it
                @ColorInt val color: Int = ResourceProvider.getColor(this, it.name)
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ResourceProvider.getColor(this, "${it.name}_dark")
                binding.coordinator.setBackgroundColor(color)
                binding.fabBack.imageTintList = ColorStateList.valueOf(color)
                binding.unitIcon.setImageResource(ResourceProvider.getIcon(this, it.name))
                val play = ContextCompat.getDrawable(this, R.drawable.play)
                binding.buttonNext.setCompoundDrawables(null, null, play, null)
                binding.buttonNext.setColor(color)
            }
        }
    }

    private fun completeCategory(category: Category) {
        unitObject.completed.add(category.value)
        db.unitDao().updateUnit(unitObject)
    }

    private fun dispatch() {
        when (category.value) {
            Category.UNDERSTAND.value -> goToUnderstand()
            Category.TALK.value -> goToSpeak()
            Category.READ.value -> goToRead()
            Category.WRITE.value -> goToWrite()
            Category.FINAL_TEST.value -> goToFinalTest()
        }
    }

    private fun goToCategory() {
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
            Snackbar.make(binding.root, R.string.category_empty_error, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.red)).show()
            return false
        }
        player.release()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    companion object {
        const val EXTRA_CATEGORY_ID = "category_id"
        const val EXTRA_IS_FINISH = "extra_is_finish_section"
    }
}