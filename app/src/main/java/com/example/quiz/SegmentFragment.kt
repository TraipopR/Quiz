package com.example.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.quiz.databinding.FragmentSegmentBinding
import com.google.android.material.chip.Chip

private const val ARG_SEGMENT = "segment"
const val RESULT_EXTRA = "resultExtra"

class SegmentFragment : Fragment() {
    private var segment: Int? = null
    private lateinit var binding: FragmentSegmentBinding
    private val model: MainViewModel by activityViewModels()
    private var segmentCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            segment = it.getInt(ARG_SEGMENT)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSegmentBinding.inflate(layoutInflater)

        segment?.let { segment ->
            model.getQuiz().observe(viewLifecycleOwner,  {
                segmentCount = it.count()
                binding.allSegment.text = segmentCount.toString()
                binding.segment.text = (segment + 1).toString()
                val segmentData = it[segment]
                with(segmentData) {
                    binding.question.text = question
                    binding.chipA.text = choiceA.label
                    binding.chipB.text = choiceB.label
                    binding.chipC.text = choiceC.label
                    binding.chipD.text = choiceD.label
                }
                if ((segment + 1) == segmentCount)
                    binding.btnNext.text = "Submit"

                val correct = when(segmentData.answer) {
                    EChoice.A -> binding.chipA
                    EChoice.B -> binding.chipB
                    EChoice.C -> binding.chipC
                    EChoice.D -> binding.chipD
                    else -> null
                }
                correct?.isChecked = true
            })

            binding.btnNext.setOnClickListener {
                if (segment + 1 >= segmentCount){
                    val intent = Intent(activity, ResultActivity::class.java)
                    intent.putExtra(RESULT_EXTRA, segmentCount)
                    startActivity(intent)
                } else model.setSegment(segment + 1)
            }

            if (segment != 0) {
                binding.btnPrev.setOnClickListener {
                    if (segment - 1 < 0)
                        return@setOnClickListener
                    model.setSegment(segment - 1)
                }
            } else {
                binding.btnPrev.visibility = View.GONE
            }
        }

        binding.chipGroup.children.forEachIndexed { index, it ->
            val chip = it as Chip
            chip.chipBackgroundColor = setChipBackgroundColor(
                checkedColor = Color.parseColor("#81e6d9"),
                unCheckedColor = Color.parseColor("#FFFFFF")
            )
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    model.setAnswer(index, segment!!)
                if (segment!! + 1 >= segmentCount && quizData.question.all { it.answer != null })
                    Toast.makeText(activity, "ตอบคำถามครบทุกข้อแล้ว", Toast.LENGTH_SHORT).show()
            }
        }

        var lastCheckedId = View.NO_ID
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == View.NO_ID) {
                // User tried to uncheck, make sure to keep the chip checked
                group.check(lastCheckedId)
                return@setOnCheckedChangeListener
            }
            lastCheckedId = checkedId
        }

        return binding.root
    }

    fun newInstance(segment: Int) =
        SegmentFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_SEGMENT, segment)
            }
        }
}

fun setChipBackgroundColor(
    checkedColor: Int = Color.GREEN,
    unCheckedColor: Int = Color.RED
): ColorStateList {
    val states = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked)
    )

    val colors = intArrayOf(
        // chip checked background color
        checkedColor,
        // chip unchecked background color
        unCheckedColor
    )

    return ColorStateList(states, colors)
}

class ViewPagerQuizAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {

    // declare arrayList to contain fragments and its title
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentQuizList = ArrayList<Int>()

    override fun getItemCount(): Int = mFragmentList.size
    override fun createFragment(position: Int): Fragment =
        (mFragmentList[position] as SegmentFragment).newInstance(mFragmentQuizList[position])

    fun addFragment(fragment: Fragment, segment: Int) {
        // add each fragment and its title to the array list
        mFragmentList.add(fragment)
        mFragmentQuizList.add(segment)
    }
}