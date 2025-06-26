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
import it.mindtek.ruah.databinding.ActivityReadBinding
import it.mindtek.ruah.enums.Exercise
import it.mindtek.ruah.fragments.FragmentRead
import it.mindtek.ruah.interfaces.ReadActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.replaceFragment
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityRead : AppCompatActivity(), ReadActivityInterface {
    private var unitId: Int = -1
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityReadBinding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            unitId = it.getIntExtra(ActivityUnit.EXTRA_UNIT_ID, -1)
        }
        binding.activityReadToolbar.setTopPadding()
        setSupportActionBar(binding.activityReadToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(Exercise.READ.title)
        replaceFragment(FragmentRead.newInstance(unitId, 0), R.id.activity_read_placeholder, false)
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
                Log.e("ActivityRead", "Error loading unit", error)
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
            FragmentRead.newInstance(unitId, index),
            R.id.activity_read_placeholder,
            true
        )
    }

    override fun goToFinish() {
        startActivity(Intent(this, ActivityIntro::class.java).apply {
            putExtra(ActivityUnit.EXTRA_UNIT_ID, unitId)
            putExtra(ActivityIntro.EXTRA_EXERCISE_ID, Exercise.READ.value)
            putExtra(ActivityIntro.EXTRA_IS_FINISH, true)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}