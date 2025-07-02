package it.mindtek.ruah.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import it.mindtek.ruah.databinding.ActivityMainBinding
import it.mindtek.ruah.kotlin.extensions.setTopPadding

class ActivityMain : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setTopPadding()
        val navController: NavController =
            binding.activityMainNavHostFragment.getFragment<NavHostFragment>().navController
        binding.activityMainBottomNavigation.setupWithNavController(navController)
    }
}