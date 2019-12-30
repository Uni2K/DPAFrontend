package com.project.app.Paging

import android.util.Log
import androidx.annotation.MainThread
import androidx.paging.PagedList
import com.project.app.Bases.PagingBase
import com.project.app.Objects.ContentModel
import com.project.app.Paging.Displayable.Companion.MIXED
import com.project.app.Paging.Displayable.Companion.TIMED

import java.util.concurrent.Executor
import kotlin.reflect.KFunction1

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class BoundaryCallback(
    private val contentName: String,
    private val webservice: PagingBase,
    private val ioExecutor: Executor,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<ContentModel>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        Log.e("onZeroItemsLoaded", "CONTENT: $contentName  ")
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webservice.getInitial(
                contentName = contentName,
                limit = networkPageSize, callBack = createWebserviceCallback(it)

            )
        }
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback): PagingBase.PagingCallBack {
    return webservice.masterViewModel.createWebserviceCallback(it)
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd_: ContentModel) {
        val itemAtEnd=ContentModel.toDisplayable(itemAtEnd_)
        var before:String?=null
        when(itemAtEnd?.indextype){
            TIMED-> {
                before = itemAtEnd.getQuestion()?.timestamp ?:itemAtEnd.getUser()?.timestamp
            }
            MIXED->{
                before = itemAtEnd.getQuestion()?.index ?:itemAtEnd.getUser()?.index
                //before = itemAtEnd.index

            }
        }
        Log.e("onZeroItemsAtEnd", "CONTENT: $contentName ${networkState.value}   $before        ${itemAtEnd?.getQuestion()}        ${itemAtEnd?.getUser()}    ${itemAtEnd?.indextype}")

        if (before != null) {
            helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
                webservice.getAfter(
                    contentName = contentName,
                    after = before,
                    limit = networkPageSize,
                    callBack = createWebserviceCallback(it)
                )
            }
        }

    }





    override fun onItemAtFrontLoaded(itemAtFront_: ContentModel) {
        val itemAtFront=ContentModel.toDisplayable(itemAtFront_)

        var before:String?=null
        when(itemAtFront?.indextype){
            TIMED-> {
                before = itemAtFront.getQuestion()?.timestamp ?:itemAtFront.getUser()?.timestamp
            }
            MIXED->{
                before = itemAtFront.getQuestion()?.index ?:itemAtFront.getUser()?.index
            }
        }
        if (before != null) {
            helper.runIfNotRunning(PagingRequestHelper.RequestType.BEFORE) {
                webservice.getBefore(
                    contentName = contentName,
                    before = before,
                    limit = networkPageSize,
                    callBack = createWebserviceCallback(it)
                )
            }
        }


    }



}