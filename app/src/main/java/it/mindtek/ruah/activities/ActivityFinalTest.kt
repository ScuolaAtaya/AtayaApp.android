package it.mindtek.ruah.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.mindtek.ruah.R
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.ActivityFinalTestBinding
import it.mindtek.ruah.enums.Exercise
import it.mindtek.ruah.fragments.FragmentFinalTest
import it.mindtek.ruah.interfaces.FinalTestActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityFinalTest : AppCompatActivity(), FinalTestActivityInterface {
    private var unitId: Int = -1
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityFinalTestBinding = ActivityFinalTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
        }
        binding.activityFinalTestToolbar.setTopPadding()
        setSupportActionBar(binding.activityFinalTestToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(Exercise.FINAL_TEST.title)
        replaceFragment(
            FragmentFinalTest.newInstance(unitId, 0),
            R.id.activity_final_test_placeholder,
            false
        )
        db.unitDao().getUnitByIdAsync(unitId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                supportActionBar?.setBackgroundDrawable(
                    ResourceProvider.getColor(this, it.name).toDrawable()
                )
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM)
                    window.statusBarColor = ResourceProvider.getColor(this, "${it.name}_dark")
            }, { error ->
                Log.e("ActivityFinalTest", "Error loading unit", error)
            }).let {
                disposable.add(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun goToNext(index: Int) {
        replaceFragment(
            FragmentFinalTest.newInstance(unitId, index),
            R.id.activity_final_test_placeholder,
            true
        )
    }

    override fun goToFinish() {
        startActivity(Intent(this, ActivityIntro::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            putExtra(ActivityIntro.EXTRA_EXERCISE_ID, Exercise.FINAL_TEST.value)
            putExtra(ActivityIntro.EXTRA_IS_FINISH, true)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}