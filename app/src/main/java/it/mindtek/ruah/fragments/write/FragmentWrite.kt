package it.mindtek.ruah.fragments.write


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityIntro
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.SelectableLettersAdapter
import it.mindtek.ruah.adapters.SelectedLettersAdapter
import it.mindtek.ruah.adapters.dividers.GridSpaceItemDecoration
import it.mindtek.ruah.config.GlideApp
import it.mindtek.ruah.db.models.ModelWrite
import it.mindtek.ruah.enums.Category
import it.mindtek.ruah.interfaces.WriteActivityInterface
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.disable
import kotlinx.android.synthetic.main.fragment_write.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.support.v4.dip


/**
 * A simple [Fragment] subclass.
 */
class FragmentWrite : Fragment() {
    var unitId: Int = -1
    var category: Category? = null
    var stepIndex: Int = -1
    var write: MutableList<ModelWrite> = mutableListOf()
    var communicator: WriteActivityInterface? = null
    var selectedAdapter: SelectedLettersAdapter? = null
    var selectableAdapter: SelectableLettersAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_write, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
            activity.finish()
        initCommunicators()
        write = db.writeDao().getWriteByUnitId(unitId)
        setup()
    }

    private fun initCommunicators() {
        if (activity is WriteActivityInterface)
            communicator = activity as WriteActivityInterface
    }

    @SuppressLint("RestrictedApi")
    private fun setup() {
        setupPicture()
        setupButtons()
        setupSteps()
        setupRecyclers()
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            val color = ContextCompat.getColor(activity, it.color)
            stepLayout.backgroundColor = color
        }
    }

    private fun setupRecyclers() {
        val stepWrite = write[stepIndex]
        val selectableCol = calculateSelectableColumns()
        val selectedCol = calculateColumns()
        compile.layoutManager = GridLayoutManager(activity, if (stepWrite.letters.size >= selectedCol) selectedCol else stepWrite.letters.size)
        available.layoutManager = GridLayoutManager(activity, if (stepWrite.letters.size >= selectableCol) selectableCol else stepWrite.letters.size)
        selectedAdapter = SelectedLettersAdapter(stepWrite.word, stepWrite.letters, {})
        selectableAdapter = SelectableLettersAdapter(stepWrite.letters, { letters ->
            selectedAdapter?.select(letters)
        })
        compile.adapter = selectedAdapter
        available.adapter = selectableAdapter
        compile.addItemDecoration(GridSpaceItemDecoration(dip(4), dip(4)))
        available.addItemDecoration(GridSpaceItemDecoration(dip(8), dip(8)))
    }

    private fun calculateColumns(): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val columns = ((dpWidth - 32) / 32) - 1
        println(columns)
        return columns.toInt()
    }

    private fun calculateSelectableColumns(): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val columns = ((dpWidth - 32) / 40) - 1
        println(columns)
        return columns.toInt()
    }

    private fun setupPicture() {
        GlideApp.with(this).load("https://ichef-1.bbci.co.uk/news/976/media/images/83351000/jpg/_83351965_explorer273lincolnshirewoldssouthpicturebynicholassilkstone.jpg").placeholder(R.color.grey).into(picture)
    }

    private fun setupButtons() {
        next.setOnClickListener {
            dispatch()
        }
        next.disable()
    }

    private fun setupSteps() {
        step.text = "${stepIndex + 1}/${write.size}"
    }

    private fun dispatch() {
        if (stepIndex + 1 < write.size) {
            goToNext()
        } else {
            finish()
        }
    }

    private fun finish() {
        communicator?.goToFinish()
    }

    private fun goToNext() {
        communicator?.goToNext(stepIndex + 1)
    }

    companion object {
        val EXTRA_STEP = "extra step int position"

        fun newInstance(): FragmentWrite = FragmentWrite()

        fun newInstance(unit_id: Int, category: Category, stepIndex: Int): FragmentWrite {
            val frag = FragmentWrite()
            val bundle = Bundle()
            bundle.putInt(ActivityUnit.EXTRA_UNIT_ID, unit_id)
            bundle.putInt(ActivityIntro.EXTRA_CATEGORY_ID, category.value)
            bundle.putInt(EXTRA_STEP, stepIndex)
            frag.arguments = bundle
            return frag
        }
    }
}
