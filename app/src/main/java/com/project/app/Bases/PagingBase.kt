package com.project.app.Bases

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.project.app.Helpers.Constants
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.QuestionSingleRequestConfig
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Objects.ParsingConfig
import com.project.app.Paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * Implements the main Paging Methods
 */
class PagingBase(
    val masterViewModel: MasterViewModel
) {


    interface PagingCallBack {
        fun onFailure()
        fun onResponse(result: ListingData)
    }

    private fun triggerUpdateRoutine(): Boolean {
        val triggerNewQuestionsOnServer = masterViewModel.questionBase.newQuestionsAvaiableTrigger.value ?: 0
        val triggerUserWantsToUpdate = masterViewModel.userBase.userUpdateTrigger.value
        //Log.d("triggerUpdateRoutine","PARAMETERS: onServer?: $triggerNewQuestionsOnServer userWants?: $triggerUserWantsToUpdate")
        if (triggerUserWantsToUpdate!!) return true
        if (triggerNewQuestionsOnServer != 0 && triggerNewQuestionsOnServer < Constants.FETCH_OVERLAD) return true

        return false
    }

    fun resetTriggers() {
        masterViewModel.questionBase.newQuestionsAvaiableTrigger.postValue(0)
        masterViewModel.userBase.userUpdateTrigger.postValue(false)

    }


    private fun createListing(content: List<Displayable>, requestType: PagingRequestHelper.RequestType):ListingData{
        var afterResponse = content.minBy { it.index }?.index
        var beforeResponse= content.maxBy { it.index }?.index

        when(requestType){
            PagingRequestHelper.RequestType.AFTER->{
                beforeResponse=null
            }
            PagingRequestHelper.RequestType.BEFORE->{
                afterResponse=null
            }
            PagingRequestHelper.RequestType.INITIAL->{
                beforeResponse=null


            }
        }

        var list= ListingData(
            content,
            afterResponse,
            beforeResponse
        )
        return list
    }

    @MainThread
    private fun getData(contentName: String, callBack: PagingCallBack?, parsingConfig:ParsingConfig, requestType: PagingRequestHelper.RequestType, questionSingleRequestConfig: QuestionSingleRequestConfig?=null ){
        when(contentName) {
            Constants.CONTENT_FEED -> {

                masterViewModel.userBase.getFeed(questionSingleRequestConfig,parsingConfig,
                    object : RetrofitHelper.Companion.DisplayableListCallback {
                        override fun onError(errorCode: Int) {
                            callBack?.onFailure()
                            GlobalScope.launch(Dispatchers.Main) {
                                masterViewModel.updateContentStackMap(
                                    contentName,
                                    Pair(
                                       requestType.toString(),
                                        PagingRequestHelper.Status.FAILED.toString()
                                    )
                                )
                            }
                        }

                        override fun onResponse(content: List<Displayable>) {
                            GlobalScope.launch {
                                val listing = createListing(content,requestType)


                                launch(Dispatchers.Main) {
                                    callBack?.onResponse(
                                        listing
                                    )


                                    masterViewModel.updateContentStackMap(
                                        contentName,
                                        Pair(
                                            requestType.toString(),
                                            PagingRequestHelper.Status.SUCCESS.toString()
                                        )
                                    )
                                }

                            }


                        }
                    })

            }
            else -> {
                if (questionSingleRequestConfig != null) {
                    masterViewModel.questionBase.getQuestions(questionSingleRequestConfig, parsingConfig,
                        object : RetrofitHelper.Companion.DisplayableListCallback {
                            override fun onError(errorCode: Int) {

                              if(requestType==PagingRequestHelper.RequestType.BEFORE){
                                  resetTriggers()
                              }
                                callBack?.onFailure()
                                masterViewModel.updateContentStackMap(
                                    contentName,
                                    Pair(
                                       requestType.toString(),
                                        PagingRequestHelper.Status.FAILED.toString()
                                    )
                                )
                            }

                            override fun onResponse(content: List<Displayable>) {
                                if(requestType==PagingRequestHelper.RequestType.BEFORE) {
                                    resetTriggers()
                                    //var questionInfo: QuestionStackInfo = QuestionStackInfo(ArrayList(content))
                                    //      masterViewModel.newQuestionsInsertedTrigger.value = Pair(content.size, questionInfo)

                                    //}
                                }
                                GlobalScope.launch {
                                    val listing = createListing(content,requestType)
                                    launch(Dispatchers.Main) {
                                        callBack?.onResponse(
                                            listing
                                        )


                                        masterViewModel.updateContentStackMap(
                                            contentName,
                                            Pair(
                                                requestType.toString(),
                                                PagingRequestHelper.Status.SUCCESS.toString()
                                            )
                                        )
                                    }

                                }


                            }
                        })
                }


            }
        }




    }



    @MainThread
    fun getInitial(contentName: String, limit: Int, callBack: PagingCallBack?) {
        val parsingConfig=ParsingConfig(contentname=contentName)
        masterViewModel.updateContentStackMap(
            contentName,
            Pair(
                PagingRequestHelper.RequestType.INITIAL.toString(),
                PagingRequestHelper.Status.RUNNING.toString()
            )
        )
        val config = QuestionSingleRequestConfig()
        config.loadSize = limit
        config.key = System.currentTimeMillis().toString()
        config.older = true

        when (contentName) {
            Constants.CONTENT_STREAM -> {
            //    config.filterTags = masterViewModel.getStreamFilterTags().joinToString()
                config.type = Constants.CONTENT_STREAM
            }
            Constants.CONTENT_TRENDING -> {
                config.type = Constants.CONTENT_TRENDING
            }
            Constants.CONTENT_FEED -> {
                config.type = Constants.CONTENT_FEED
            }


        }
        with(contentName) {
            when {
                startsWith(Constants.CONTENT_SEARCH) -> {
                    config.type = Constants.CONTENT_SEARCH
                    config.searchQuery = contentName.removePrefix(Constants.CONTENT_SEARCH)

                }

                startsWith(Constants.CONTENT_USER_ASKED) -> {
                    config.type = Constants.CONTENT_USER_ASKED
                    config.userid = contentName.removePrefix(Constants.CONTENT_USER_ASKED)
                }


                startsWith(Constants.CONTENT_TAGDETAIL) -> {
                    config.type = Constants.CONTENT_TAGDETAIL
                    config.filterTags = contentName.removePrefix(Constants.CONTENT_TAGDETAIL)

                }
            }
        }

        getData(contentName,callBack,parsingConfig,PagingRequestHelper.RequestType.INITIAL,config)





    }





    @MainThread
    fun getAfter(contentName: String, after: String, limit: Int, callBack: PagingCallBack) {
        val parsingConfig=ParsingConfig(contentname=contentName)

        masterViewModel.updateContentStackMap(
            contentName,
            Pair(
                PagingRequestHelper.RequestType.AFTER.toString(),
                PagingRequestHelper.Status.RUNNING.toString()
            )
        )
        val config = QuestionSingleRequestConfig()
        config.loadSize = limit
        config.key = after
        config.older = true

        when (contentName) {
            Constants.CONTENT_STREAM -> {
            //    config.filterTags = masterViewModel.getStreamFilterTags().joinToString()
                config.type = Constants.CONTENT_STREAM
            }
            Constants.CONTENT_TRENDING -> {
                config.type = Constants.CONTENT_TRENDING
            }

        }
        with(contentName) {
            when {
                startsWith(Constants.CONTENT_SEARCH) -> {
                    config.type = Constants.CONTENT_SEARCH
                    config.searchQuery = contentName.removePrefix(Constants.CONTENT_SEARCH)

                }

                startsWith(Constants.CONTENT_USER_ASKED) -> {
                    config.type = Constants.CONTENT_USER_ASKED
                    config.userid = contentName.removePrefix(Constants.CONTENT_USER_ASKED)
                }
                startsWith(Constants.CONTENT_TAGDETAIL) -> {
                    config.type = Constants.CONTENT_TAGDETAIL
                    config.filterTags = contentName.removePrefix(Constants.CONTENT_TAGDETAIL)

                }
            }
        }

        getData(contentName,callBack,parsingConfig,PagingRequestHelper.RequestType.AFTER,config)



    }


    @MainThread
    fun getBefore(
        contentName: String,
        before: String,
        limit: Int,
        callBack: PagingCallBack, force:Boolean=false
    ) {
        val parsingConfig=ParsingConfig(contentname=contentName)

        if (triggerUpdateRoutine()|| force) {

            masterViewModel.updateContentStackMap(
                contentName,
                Pair(
                    PagingRequestHelper.RequestType.BEFORE.toString(),
                    PagingRequestHelper.Status.RUNNING.toString()
                )
            )
            val config = QuestionSingleRequestConfig()
            config.loadSize = limit
            config.key = before
            config.older = false

            when (contentName) {
                Constants.CONTENT_STREAM -> {
                    //    config.filterTags = masterViewModel.getStreamFilterTags().joinToString()
                    config.type = Constants.CONTENT_STREAM
                }
                Constants.CONTENT_TRENDING -> {
                    config.type = Constants.CONTENT_TRENDING
                }

            }
            with(contentName) {
                when {
                    startsWith(Constants.CONTENT_SEARCH) -> {
                        config.type = Constants.CONTENT_SEARCH
                        config.searchQuery = contentName.removePrefix(Constants.CONTENT_SEARCH)

                    }

                    startsWith(Constants.CONTENT_USER_ASKED) -> {
                        config.type = Constants.CONTENT_USER_ASKED
                        config.userid = contentName.removePrefix(Constants.CONTENT_USER_ASKED)
                    }
                    startsWith(Constants.CONTENT_TAGDETAIL) -> {
                        config.type = Constants.CONTENT_TAGDETAIL
                        config.filterTags = contentName.removePrefix(Constants.CONTENT_TAGDETAIL)

                    }
                }
            }

            getData(contentName,callBack,parsingConfig,PagingRequestHelper.RequestType.BEFORE,config)
        } else {
            callBack.onResponse(
               ListingData(
                    ArrayList(),
                    null,
                    null
                )
            )
        }

    }




    /**
     * Only called by user -> creates State and is able to call refresh
     * DOES DOWNLOAD ALL NEW QUESTIONS!!!
     * TODO DO!
     */
    @MainThread
    fun fetchBeforeManual(contentName: String, callback:PagingCallBack?=null): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        if(contentName=="-1"){
            networkState.value= NetworkState.LOADED
            return networkState
        }



        GlobalScope.launch {
            val before = masterViewModel.database.contentAccessor().getLatest(contentName)



            launch (Dispatchers.Main) {
             if (before == null) {
                 Log.e("fetchBefore", "REPOSITORY $contentName  $before")

                 getInitial(contentName,Constants.LOADSIZE_INITIAL,callback)

                return@launch
            }


                getBefore(contentName, before, 50, object : PagingCallBack {
                    override fun onFailure() {
                        callback?.onFailure()
                    }

                    override fun onResponse(result_: ListingData) {
                        masterViewModel.insertResultIntoDb(result_)
                        callback?.onResponse(result_)
                    }
                },true)
            }


            /* GlobalScope.launch(Dispatchers.Main) {
                 socketapi.getBefore(
                     contentName,
                     before,
                     networkPageSize,
                     object : PagingBase.callBack {
                         override fun onResponse(result: Any, type: Int) {
                             var isQuestion = result as? PagingBase.ListingDataUser == null
                             if (isQuestion) {

                                 ioExecutor.execute {
                                     db.runInTransaction {
                                         result as PagingBase.ListingData
                                         insertQuestionsIntoDb(contentName, result)
                                         networkState.postValue(NetworkState.LOADED)

                                     }
                                 }
                             } else {
                                 ioExecutor.execute {
                                     db.runInTransaction {
                                         result as PagingBase.ListingDataUser
                                         //   db.questions().deleteByContentName(contentName)
                                         insertUsersIntoDb(contentName, result)
                                         networkState.postValue(NetworkState.LOADED)

                                     }
                                 }

                             }

                         }


                         override fun onFailure() {
                             networkState.postValue(NetworkState.error(""))

                         }


                     })
             }

             */


        }

        return networkState

    }



    @MainThread
    fun getContent(contentName: String, pageSize: Int): Listing<Any> {

        val boundaryCallback = BoundaryCallback(
            webservice = masterViewModel.pagingBase,
            contentName = contentName,
            ioExecutor = masterViewModel.DISK_IO,
            networkPageSize = 10
        )




            val livePagedList =
                ( masterViewModel.database.contentAccessor().contentByContentName(contentName)).toLiveData(
                    pageSize = pageSize,
                    boundaryCallback = boundaryCallback
                )
            return Listing(

                pagedList = livePagedList,
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.helper.retryAllFailed()

                },
                refresh = {
                    Log.e("Listing", "REFRESH")
                   getInitial(contentName,pageSize,null)

                },
                fetchMore = {
                    fetchBeforeManual(contentName)

                }

            ) as Listing<Any>





    }




}
    
    
    
    
    
    
    
    
    
    
    
    
    