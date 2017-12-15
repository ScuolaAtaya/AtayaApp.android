package it.mindtek.ruah.fragments.speak

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mindtek.ruah.R

/**
 * Created by alessandrogaboardi on 15/12/2017.
 */
class FragmentSpeak : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_speak, container, false)
    }


}