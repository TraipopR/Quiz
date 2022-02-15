package com.example.quiz

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.databinding.FragmentSegmentBinding
import com.example.quiz.databinding.FragmentSegmentBinding.inflate
import com.example.quiz.databinding.ResultActivityBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ResultActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ResultActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.title = "Your score is ${quizData.question.map { if (it.answer == it.correct) 1 else 0  }.sum()}/${quizData.question.count()}"
        binding.toolbar.setTitleTextColor(Color.WHITE);

        val resultActivity = this
        binding.recycleViewQuestion.apply {
            layoutManager = LinearLayoutManager(resultActivity)
            adapter = QuestionAdapter(quizData.question)
        }

    }
}

class QuestionAdapter(
    private val questions: List<Question>,
): RecyclerView.Adapter<QuestionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = inflate(from, parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bindQuestion(questions[position])
    }

    override fun getItemCount(): Int = questions.size
}

class QuestionViewHolder(
    private val segmentBinding: FragmentSegmentBinding,
): RecyclerView.ViewHolder(segmentBinding.root) {
    fun bindQuestion(segment: Question) {
        with(segmentBinding) {
            titleContainer.visibility = View.GONE
            btnContainer.visibility = View.GONE
            question.text = segment.question
            chipA.text = segment.choiceA.label
            chipB.text = segment.choiceB.label
            chipC.text = segment.choiceC.label
            chipD.text = segment.choiceD.label

            var color = "#fb6976"
            if (segment.answer == segment.correct) {
                color = "#81e6d9"
            }

            listOf(chipA, chipB, chipC, chipD).forEachIndexed { index, it ->
                it.chipBackgroundColor = setChipBackgroundColor(
                    checkedColor = Color.parseColor(color),
                    unCheckedColor = Color.parseColor("#FFFFFF")
                )
            }

            val correct = when(segment.answer) {
                EChoice.A -> chipA
                EChoice.B -> chipB
                EChoice.C -> chipC
                EChoice.D -> chipD
                else -> null
            }
            correct?.let {
                it.isChecked = true
                it.setBackgroundResource(R.color.teal_200)
            }

            chipGroup.setOnCheckedChangeListener { group, _ ->
                correct?.id?.let { group.check(it) }
            }
        }
    }
}