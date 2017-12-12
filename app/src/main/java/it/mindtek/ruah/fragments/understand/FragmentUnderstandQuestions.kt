package it.mindtek.ruah.fragments.understand


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelQuestion
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.pojos.PojoQuestion
import it.mindtek.ruah.pojos.UnderstandPojo
import kotlinx.android.synthetic.main.fragment_understand_questions.*


/**
 * A simple [Fragment] subclass.
 */
class FragmentUnderstandQuestions : Fragment() {
    var unit_id: Int = -1
    var question: Int = -1
    var questions: MutableList<PojoQuestion> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_understand_questions, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (it.containsKey(EXTRA_UNIT_ID))
                unit_id = it.getInt(EXTRA_UNIT_ID, -1)
            if (it.containsKey(EXTRA_QUESTION_NUMBER))
                question = it.getInt(EXTRA_QUESTION_NUMBER, -1)
        }

        if(unit_id == -1)
            activity.finish()
        else {
            val categoryObservable = db.understandDao().getUnderstandByUnitId(unit_id)
            categoryObservable.observe(activity, Observer<UnderstandPojo>{ category ->
                category?.let {
                    questions = it.questions
                    setupQuestion()
                    setupAnswers()
                }
            })
        }
    }

    private fun setupQuestion(){
        val question = questions[question]
        title
    }

    companion object {
        val EXTRA_QUESTION_NUMBER = "question_number_extra"
        val EXTRA_UNIT_ID = "unit_id_extra"

        fun newInstance(): FragmentUnderstandQuestions = FragmentUnderstandQuestions()

        fun newInstance(question: Int, unit_id: Int): FragmentUnderstandQuestions {
            val fragment = FragmentUnderstandQuestions()
            val bundle = Bundle()
            bundle.putInt(EXTRA_QUESTION_NUMBER, question)
            bundle.putInt(EXTRA_UNIT_ID, unit_id)
            fragment.arguments = bundle
            return fragment
        }
    }
}
