package com.project.app.Interfaces

import com.project.app.Helpers.RetrofitHelper
import com.project.app.Objects.Question

interface QuestionController {
    fun showFullScreen(q: Question)
    fun questionAnswered(NumberOfAnswer:Int, question: Question, callback: RetrofitHelper.Companion.DisplayableListCallback)
    fun userClicked(userid: String)

}