package com.project.app.Bases

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import com.project.app.APIs.QuestionAPI
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.QuestionMultiRequestConfig
import com.project.app.Helpers.QuestionSingleRequestConfig
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Objects.ParsingConfig
import com.project.app.Objects.QuestionStackInfo


/**
 * Replaces SocketAPI/SocketBase -> all content fetching from server belongs in here
 */
class QuestionBase(val masterViewModel: MasterViewModel) {

    var service: QuestionAPI? = null
    val newQuestionsAvaiableTrigger: MutableLiveData<Int> =
        MutableLiveData(0) //To signal when getBefore is allowed to return content



    private fun getQuestionService(): QuestionAPI? {
        if (service == null) {
            service = masterViewModel.getRetrofitClient()?.create(QuestionAPI::class.java)
        }
        return service
    }

    @MainThread
   fun getQuestionByIDs(ids:List<String>, callback:RetrofitHelper.Companion.DisplayableListCallback){
        val parsingConfig=ParsingConfig("-1")
        RetrofitHelper.launchControlled(masterViewModel,parsingConfig,callback , codeToExecute = {
            getQuestionService()?.getQuestionsByIDs(QuestionMultiRequestConfig(ids))
        })
    }


    @MainThread
    fun getQuestions(configSingle: QuestionSingleRequestConfig, parsingConfig: ParsingConfig, callback:RetrofitHelper.Companion.DisplayableListCallback) {
        RetrofitHelper.launchControlled(masterViewModel,parsingConfig,callback , codeToExecute = {
            getQuestionService()?.getQuestion(configSingle)
        })
    }
}