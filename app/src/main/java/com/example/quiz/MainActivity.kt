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
    "Final",
    listOf(
        Question(
            "OSI Layer มีทั้งหมดกี่ Layer",
            listOf(EChoice.C),
            Choice("5 Layer"),
            Choice("6 Layer"),
            Choice("7 Layer"),
            Choice("8 Layer")
        ),
        Question(
            "จงเรียงลำดับขนาดของเครือข่าย",
            listOf(EChoice.A, EChoice.C),
            Choice("LAN MAN WAN"),
            Choice("LAN WAN MAN"),
            Choice("WAN MAN LAN"),
            Choice("WAN LAN MAN")
        ),
        Question(
            "Address ของ IPv4 กับ IPv6 แตกต่างกันเท่าไหร่",
            listOf(EChoice.D),
            Choice("1 เท่า"),
            Choice("2 เท่า"),
            Choice("3 เท่า"),
            Choice("4 เท่า")
        ),
        Question(
            "1 + 2 x 3 - 4 ÷ 4 = ?",
            listOf(EChoice.C),
            Choice("4"),
            Choice("5"),
            Choice("6"),
            Choice("7")
        ),
        Question(
            "IP Address 192.168.1.21/24 จัดเป็น IP คลาสใด",
            listOf(EChoice.C),
            Choice("Class A"),
            Choice("Class B"),
            Choice("Class C"),
            Choice("Class D")
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
    val correct: List<EChoice>,
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
