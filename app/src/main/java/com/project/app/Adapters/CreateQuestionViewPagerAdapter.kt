package com.project.app.Adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.project.app.Fragments.QuestionCreator
import com.project.app.Slides.CreateSlideAnswers
import com.project.app.Slides.CreateSlideContext
import com.project.app.Slides.CreateSlideTopic
import com.project.app.Slides.CreateSlideText


@SuppressLint("WrongConstant")
class CreateQuestionViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val questionCreator: QuestionCreator
) : FragmentStatePagerAdapter(fragmentManager,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return createFragment(position)
    }

    override fun getCount(): Int {
        return getItemCount()
    }

    fun getItemCount(): Int {
        return 4
    }

     fun createFragment(position: Int): Fragment {
        val slidex = CreateSlideText()
        slidex.setController(questionCreator)
        when (position) {
            1 -> {
                val slide0 = CreateSlideText()
                slide0.setController(questionCreator)
                return slide0
            }
            2 -> {
                val slide1 = CreateSlideAnswers()
                slide1.setController(questionCreator)
                return slide1
            }
            0 -> {
                val slide2 = CreateSlideTopic()
                slide2.setController(questionCreator)
                return slide2
            }
            3 -> {
                val slide3 = CreateSlideContext()
                slide3.setController(questionCreator)
                return slide3
            }

        }
        return slidex
    }


}