package it.mindtek.ruah.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import it.mindtek.ruah.R
import it.mindtek.ruah.fragments.speak.FragmentSpeak
import it.mindtek.ruah.kotlin.extensions.replaceFragment

class ActivitySpeak : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speak)

        replaceFragment(FragmentSpeak(), R.id.placeholder)
    }
}
