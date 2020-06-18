package it.mindtek.ruah.fragments.final_test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityIntro
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.kotlin.extensions.db

class FragmentFinalTest : Fragment() {
    private var unitId: Int = -1
    private var category: Category? = null
    private var stepIndex: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_final_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID))
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            if (it.containsKey(ActivityIntro.EXTRA_CATEGORY_ID))
                category = Category.from(it.getInt(ActivityIntro.EXTRA_CATEGORY_ID))
            if (it.containsKey(EXTRA_STEP))
                stepIndex = it.getInt(EXTRA_STEP)
        }
        if (unitId == -1 || category == null || stepIndex == -1)
            requireActivity().finish()
        setup()
    }

    private fun setup() {
        val finalTest = db.finalTestDao().getFinalTestByUnitId(unitId)
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(requireActivity(), it.color)
        }
    }

    companion object {
        const val EXTRA_STEP = "extra_step_int_position"

        fun newInstance(unitId: Int, category: Category, stepIndex: Int): FragmentFinalTest {
            val fragment = FragmentFinalTest()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
            bundle.putInt(ActivityIntro.EXTRA_CATEGORY_ID, category.value)
            bundle.putInt(EXTRA_STEP, stepIndex)
            fragment.arguments = bundle
            return fragment
        }
    }
}