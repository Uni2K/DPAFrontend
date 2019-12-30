package com.project.app.Dialogs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.project.app.Activities.HomeActivity
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Interfaces.QuestionController
import com.project.app.Objects.Question
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

open class QuestionControllerDialogFragment: DialogFragment(),QuestionController {
    lateinit var activity: HomeActivity
    lateinit var fullView: QuestionFullDialogFragment

    override fun userClicked(userid: String) {

        if(::activity.isInitialized){
            activity.openAccount(userid)
        }

    }


    override fun showFullScreen(q: Question) {
        if(!::fullView.isInitialized){
            fullView= QuestionFullDialogFragment()
        }
        val bundle= Bundle()
        val moshi= Moshi.Builder().build()
        val adap: JsonAdapter<Question> = moshi.adapter<Question>(Question::class.java)
        val question= adap.toJson(q)
        bundle.putString("question",question)
        fullView.arguments=bundle
        fullView.show(childFragmentManager,"full")    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            activity = context as HomeActivity
        }
    }

    override fun questionAnswered(NumberOfAnswer:Int, question: Question, callback: RetrofitHelper.Companion.DisplayableListCallback) {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        viewModel.userBase.voteQuestion(questionID = question.id,  indexOfAnswer = NumberOfAnswer, callback = callback)
    }


}