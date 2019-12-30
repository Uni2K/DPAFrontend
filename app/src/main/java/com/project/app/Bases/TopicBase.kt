package com.project.app.Bases

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.project.app.APIs.TopicAPI
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Interfaces.TopicHelper
import com.project.app.Objects.Topic
import com.project.app.Paging.Displayable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class TopicBase(val masterViewModel: MasterViewModel) {

    var service: TopicAPI? = null
    var tags: MutableLiveData<Pair<List<Topic>, Map<String, List<Topic>>>> = MutableLiveData()
    private var tagUpdatingJob: Job? = null



    init {
        requestAllTagSub()
    }




    private fun getTopicService():TopicAPI? {
        if (service == null) {
            service = masterViewModel.getRetrofitClient()?.create(TopicAPI::class.java)
        }
        return service
    }





    /**
     * Return true= Liste da
     * Return false= Liste nicht da -> fetching triggert.
     */
    fun requestAllTagSub(): Boolean {
        if (tags.value == null) {
            //FETCH

            if (tagUpdatingJob != null) {
                //Already once did an update
                tagUpdatingJob?.let {
                    if (!it.isActive) {
                        //Already finished -> start a new one
                    } else {
                        //Running-> e.g. timeouting (no server), working (serve)
                        //Abort -> start a new one
                        it.cancel()
                    }
                    tagUpdatingJob = getAllTopics()
                }
            } else {
                tagUpdatingJob = getAllTopics()

            }
            return false
        } else {
            return true
        }
    }



    fun getTopicDetails(topicID:String,callback:TopicHelper) {

    /*
        RetrofitHelper.launchControlled(masterViewModel,callback = object :
            RetrofitHelper.Companion.RetrofitCallback {


            override fun onError(errorCode: Int) {
                callback.onError(errorCode)
                Log.d("DEBUG","getQuestions: ERROR $errorCode")

            }

            override fun onResponse(s: String) {
                //parse
                GlobalScope.launch(Dispatchers.Main) {
                    callback.onTopicCount(topicID,s.toLong())
                    callback.onVoteCount(topicID,s.toLong())

                }
            }
        }, codeToExecute = {
            getTopicService()?.getTopicDetails(TopicRequest((topicID)))
        })
*/
    }



    /**
    Return true: Response here
     * Return false: No response before timeout
     */

    fun getAllTopics(callback:RetrofitHelper.Companion.TopicListCallback?=null): Job {

       return RetrofitHelper.launchControlled(masterViewModel,parsingConfig = null,callback = object :
            RetrofitHelper.Companion.DisplayableListCallback {
            override fun onResponse(content: List<Displayable>) {
               GlobalScope.launch {
                   content as List<Topic>   //Be sure the list really only contains Topics-> easier grouping
                   var mmap = content.groupBy { it.parent }.filterNot { it.value.isEmpty() }

                   Log.d("fetchTags", "socketBase ${content.size}")
                   val tags_ = content.filter { tag ->
                       ((tag.parent != "-1") || tag._id.startsWith(
                           "x"
                       ))
                   }
                  tags.postValue(
                       Pair(
                           tags_, mmap
                       )
                   )

                   GlobalScope.launch(Dispatchers.Main) {
                       callback?.onResponse(content)
                   }
               }
            }
            override fun onError(errorCode: Int) {
                callback?.onError(errorCode)
                Log.d("DEBUG","getQuestions: ERROR $errorCode")
            }
        }, codeToExecute = {

            getTopicService()?.getTopicAll()
        })
    }

    fun getTopics(selectedTagList: ArrayList<Topic>): List<Topic> {
        return tags.value?.first?.filter { selectedTagList.find { it2->it2._id==it._id }!=null }?:ArrayList()
    }
    fun getTopicsByStrings(selectedTagList: ArrayList<String>): List<Topic> {
        return tags.value?.first?.filter { selectedTagList.find { it2->it2==it._id }!=null }?:ArrayList()
    }

    fun topicsToStrings(topicChipAdapter: ArrayList<Topic>): List<String> {
    return topicChipAdapter.map { it._id }
    }

}