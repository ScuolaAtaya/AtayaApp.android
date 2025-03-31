package it.mindtek.ruah.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import it.mindtek.ruah.R
import it.mindtek.ruah.activities.ActivityUnit
import it.mindtek.ruah.adapters.ModelSyllableItem
import it.mindtek.ruah.adapters.SelectableLettersAdapter
import it.mindtek.ruah.adapters.SelectedLettersAdapter
import it.mindtek.ruah.adapters.dividers.GridSpaceItemDecoration
import it.mindtek.ruah.config.LayoutUtils
import it.mindtek.ruah.config.ResourceProvider
import it.mindtek.ruah.databinding.FragmentWriteBinding
import it.mindtek.ruah.db.models.ModelWrite
import it.mindtek.ruah.interfaces.WriteActivityInterface
import it.mindtek.ruah.kotlin.extensions.*
import java.io.File
import java.util.*

class FragmentWrite : Fragment() {
    private lateinit var binding: FragmentWriteBinding
    private lateinit var selectedAdapter: SelectedLettersAdapter
    private lateinit var selectableAdapter: SelectableLettersAdapter
    private lateinit var communicator: WriteActivityInterface
    private var unitId: Int = -1
    private var stepIndex: Int = -1
    private var write: MutableList<ModelWrite> = mutableListOf()
    private var player: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(ActivityUnit.EXTRA_UNIT_ID))
                unitId = it.getInt(ActivityUnit.EXTRA_UNIT_ID)
            if (it.containsKey(EXTRA_STEP)) stepIndex = it.getInt(EXTRA_STEP)
        }
        setup()
    }

    private fun setup() {
        communicator = requireActivity() as WriteActivityInterface
        write = db.writeDao().getWriteByUnitId(unitId)
        val unit = db.unitDao().getUnitById(unitId)
        unit?.let {
            @ColorInt val color: Int = ResourceProvider.getColor(requireActivity(), it.name)
            binding.stepLayout.setBackgroundColor(color)
            binding.editText.backgroundTintList = ColorStateList.valueOf(color)
            binding.audioButton.backgroundTintList = ColorStateList.valueOf(color)
        }
        setupAudio()
        setupPicture()
        setupSection()
        if (write[stepIndex].type == BASIC) {
            setupBasic()
            setupRecyclers()
        } else setupAdvanced()
    }

    private fun setupAudio() {
        val audio = write[stepIndex].audio
        val audioFile = File(fileFolder.absolutePath, audio.value)
        player = MediaPlayer.create(requireActivity(), Uri.fromFile(audioFile))
        player?.setOnCompletionListener {
            if (canAccessActivity) player?.pause()
        }
        binding.audioButton.setOnClickListener {
            if (player?.isPlaying == true) player?.pause() else player?.start()
        }
        if (!audio.credits.isNullOrBlank()) {
            binding.audioCredits.setVisible()
            binding.audioCredits.text = audio.credits
        }
    }

    private fun setupPicture() {
        val picture = write[stepIndex].picture
        val pictureFile = File(fileFolder.absolutePath, picture.value)
        Glide.with(this).load(pictureFile).placeholder(R.color.grey).into(binding.stepImage)
        if (!picture.credits.isNullOrBlank()) {
            binding.stepImageCredits.setVisible()
            binding.stepImageCredits.text = picture.credits
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSection() {
        binding.step.text = "${stepIndex + 1}/${write.size}"
        binding.next.disable()
        binding.next.setOnClickListener {
            player?.release()
            if (stepIndex + 1 < write.size) communicator.goToNext(stepIndex + 1)
            else communicator.goToFinish()
        }
    }

    private fun setupBasic() {
        binding.editText.setGone()
        binding.compile.setVisible()
        binding.available.setVisible()
    }

    private fun setupAdvanced() {
        binding.compile.setGone()
        binding.available.setGone()
        binding.editText.setVisible()
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearDrawable()
                if (setLowerCase(s.toString()) == setLowerCase(write[stepIndex].word)) {
                    showRight()
                    binding.next.enable()
                } else {
                    if (s.toString().isNotEmpty()) showError()
                    binding.next.disable()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun clearDrawable() {
        binding.editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    fun showError() {
        binding.editText.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            ContextCompat.getDrawable(requireActivity(), R.drawable.close),
            null
        )
    }

    fun showRight() {
        binding.editText.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            ContextCompat.getDrawable(requireActivity(), R.drawable.done),
            null
        )
    }

    private fun setupRecyclers() {
        val letters = write[stepIndex].letters.map {
            ModelSyllableItem(it.id, it.text, it.occurences, it.enabled)
        }.toMutableList()
        val selectableCol = calculateSelectableColumns()
        val selectedCol = calculateColumns()
        val selectedSpanCount =
            if (letters.size >= selectedCol) selectedCol else letters.size
        val selectableSpanCount =
            if (letters.size >= selectableCol) selectableCol else letters.size
        binding.compile.layoutManager = GridLayoutManager(requireActivity(), selectedSpanCount)
        binding.available.layoutManager = GridLayoutManager(requireActivity(), selectableSpanCount)
        selectedAdapter = SelectedLettersAdapter(
            letters,
            object : SelectedLettersAdapter.OnClickListener {
                override fun onLetterTapped(item: ModelSyllableItem) {
                    selectableAdapter.unlockLetter(item)
                    binding.next.isEnabled = selectedAdapter.completed()
                }
            })
        letters.shuffle()
        selectableAdapter =
            SelectableLettersAdapter(object : SelectableLettersAdapter.OnClickListener {
                override fun onLetterTapped(item: ModelSyllableItem) {
                    selectedAdapter.select(item)
                    binding.next.isEnabled = selectedAdapter.completed()
                }
            })
        binding.compile.adapter = selectedAdapter
        binding.available.adapter = selectableAdapter
        binding.compile.addItemDecoration(
            GridSpaceItemDecoration(
                LayoutUtils.dpToPx(requireActivity(), 4),
                LayoutUtils.dpToPx(requireActivity(), 4)
            )
        )
        binding.available.addItemDecoration(
            GridSpaceItemDecoration(
                LayoutUtils.dpToPx(requireActivity(), 8),
                LayoutUtils.dpToPx(requireActivity(), 8)
            )
        )
    }

    private fun calculateColumns(): Int {
        val displayMetrics = requireContext().resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val columns = ((dpWidth - 32) / 40) - 1
        return columns.toInt()
    }

    private fun calculateSelectableColumns(): Int {
        val displayMetrics = requireContext().resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val columns = ((dpWidth - 32) / 40) - 1
        return columns.toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    private fun setLowerCase(text: String) = text.lowercase(Locale.ITALIAN)

    companion object {
        private const val EXTRA_STEP = "extra step int position"
        private const val BASIC = "basic"

        fun newInstance(unitId: Int, stepIndex: Int): FragmentWrite = FragmentWrite().apply {
            arguments = Bundle().apply {
                putInt(ActivityUnit.EXTRA_UNIT_ID, unitId)
                putInt(EXTRA_STEP, stepIndex)
            }
        }
    }
}