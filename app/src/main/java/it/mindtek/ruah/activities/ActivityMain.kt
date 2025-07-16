package it.mindtek.ruah.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.mindtek.ruah.databinding.ActivityMainBinding
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setTopPadding()
    }
}