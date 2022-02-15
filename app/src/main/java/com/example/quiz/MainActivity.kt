package com.example.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quiz.databinding.MainActivityBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val adapter = ViewPagerQuizAdapter(this)
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.getQuiz(binding).observe(this, {
            it.forEachIndexed { i, _ ->
                adapter.addFragment(SegmentFragment(), i)
            }
            binding.viewPager.adapter = adapter
            val tabLayout = binding.tabs
            TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
                tab.text = (position + 1).toString()
            }.attach()
        })
    }
}

val quizData = Quiz(
    "อะไรเอ๋ย!!!",
    listOf(
        Question(
            "ไก่กับไข่อันไหนเกิดก่อนกัน",
            EChoice.C,
            Choice("ไก่เกิดก่อน"),
            Choice("ไข่เกิดก่อน"),
            Choice("อันไหนจะเกิดก่อนก็กินได้เหมือนกัน"),
            Choice("เกิดพร้อมกัน")
        ),
        Question(
            "ไก่กับไข่อันไหนเกิดทีหลัง",
            EChoice.C,
            Choice("ไก่เกิดก่อน"),
            Choice("ไข่เกิดก่อน"),
            Choice("อันไหนจะเกิดก่อนก็กินได้เหมือนกัน"),
            Choice("เกิดพร้อมกัน")
        ),
    )
)

class MainViewModel : ViewModel() {
    private val quiz: MutableLiveData<List<Question>> by lazy {
        MutableLiveData<List<Question>>().run {
            loadQuiz()
        }
    }

    private var binding: MainActivityBinding? = null
    fun getQuiz(activity: MainActivityBinding): LiveData<List<Question>> {
        binding = activity
        return quiz
    }

    fun getQuiz(): LiveData<List<Question>> {
        return quiz
    }

    fun setSegment(segment: Int) {
        binding?.let {
            it.viewPager.currentItem = segment
        }
    }

    fun setAnswer(answer: Int, segment: Int) {
        if (answer in 0..3) {
            quizData.question[segment].answer = when(answer) {
                0 -> EChoice.A
                1 -> EChoice.B
                2 -> EChoice.C
                else -> EChoice.D
            }
        }
    }

    private fun loadQuiz(): MutableLiveData<List<Question>> {
        val temp: MutableLiveData<List<Question>> = MutableLiveData()
        temp.value = quizData.question
        return temp
    }
}

data class Quiz(
    val title: String,
    val question: List<Question>
)

data class Question(
    val question: String,
    val correct: EChoice,
    val choiceA: Choice,
    val choiceB: Choice,
    val choiceC: Choice,
    val choiceD: Choice,
    var answer: EChoice? = null
)

data class Choice(
    val label: String,
    val choice: EChoice? = null
)

enum class EChoice {
    A, B, C, D
}
