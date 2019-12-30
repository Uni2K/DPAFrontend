package com.project.app.Helpers

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.WorkerThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import cl.jesualex.stooltip.Position
import cl.jesualex.stooltip.Tooltip
import com.project.app.Bases.PagingBase
import com.project.app.Objects.ContentModel
import com.project.app.Paging.Displayable
import com.project.app.Paging.ListingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class ContentSyncer(val masterViewModel: MasterViewModel) {

    //Misc
    private var lifeCycleOwner: LifecycleOwner? = null
    private var syncIndicatorUpdate: ProgressBar? = null
    private var syncIndicatorContent: ProgressBar? = null

    //Constants
    private val UPDATE_INITIALDELAY = 10000L
    private val UPDATE_INTERVAL = 5000L
    private val NEWCONTENT_INITIALDELAY = 10000L
    private val CHECK_FOR_NEW_INTERVAL = 10000L

    //Timer
    var timerUpdate: Timer = Timer()
    var timerNewContent: Timer = Timer()

    //Progress Trackers
    var synchingIDs = ArrayList<Pair<String, String>>() //Type, ID
    var synchingProgress = ArrayList<String>()
    var contentCheckerOf = ArrayList<String>()
    var synchingStatus = 0 //0- IDLE, 1-SYNCHING, 2-ERROR


    //Tasks
    private val timerTaskUpdate: TimerTask = object : TimerTask() {
        override fun run() {
            sync()
        }

    }
    private val timerTaskNewContent: TimerTask = object : TimerTask() {
        override fun run() {
          GlobalScope.launch(Dispatchers.Main) {
              checkForNewContent()
          }
        }

    }


    init {
        masterViewModel.DISK_IO.execute {
            syncCollection()
        }
        contentCheckerOf.add(Constants.CONTENT_FEED)
    }


    fun connectSyncIndicator(update: ProgressBar,content: ProgressBar) {

        syncIndicatorUpdate = update
        syncIndicatorContent=content
        hideSyncIndicatorUpdate()
    }


    /**
     * e.g. called by submitList
     * TODO all ids add type
     */
    @WorkerThread
    fun syncCollection(content: List<Displayable>? = null) {
        var idsToSync: List<Displayable> = content ?: ArrayList()
        if (content == null) {
            val emulatedPair =
                ArrayList(masterViewModel.database.contentAccessor().getAvailableIDs()).map {
                    Pair(
                        Displayable.QUESTION,
                        it
                    )
                }
            synchingIDs.addAll(emulatedPair)

        } else {
            for (displayable in idsToSync) {
                synchingIDs.add(Pair(displayable.type, displayable.id))
            }
        }
        synchingIDs = ArrayList(synchingIDs.distinct())


        sync()
    }


    fun syncCollectionCM(content: List<ContentModel>) {
        syncCollection(ContentModel.convertToDisplayable(ArrayList(content)))
    }


    private fun checkForNewContent() {
        indicateContentSync(0)
        val toCheck=masterViewModel.getActiveContentByNames()
        val contentNumber=toCheck.size
        var successfullChecked=0
        var checkedAtAll=0
        Log.i("checkForNewContent","${System.currentTimeMillis()} ToCheck: $toCheck   $contentNumber")

        for (content in toCheck){
            if(content=="-1"){
                continue
            }
            masterViewModel.pagingBase.fetchBeforeManual(content, object: PagingBase.PagingCallBack{
                override fun onFailure() {
                    checkedAtAll++
                    if(checkedAtAll==contentNumber){
                        if(successfullChecked>=contentNumber){
                            indicateContentSync(1)
                        }else  indicateContentSync(-1)

                    }

                }

                override fun onResponse(result: ListingData) {
                    successfullChecked++
                    checkedAtAll++

                    if(checkedAtAll==contentNumber){
                        if(successfullChecked>=contentNumber){
                            indicateContentSync(1)
                        }else  indicateContentSync(-1)

                    }


                }
            })
        }
    }


    private fun create() {
        Log.d("sync", "SYNC CREATE")

        pause()
        timerUpdate = timer(
            "contentSyncerUpdate",
            daemon = false,
            initialDelay = UPDATE_INITIALDELAY,
            period = UPDATE_INTERVAL
        ) {
            timerTaskUpdate.run()
        }
        timerNewContent = timer(
            "contentSyncerNewContent",
            daemon = false,
            initialDelay = NEWCONTENT_INITIALDELAY,
            period = CHECK_FOR_NEW_INTERVAL
        ) {
            timerTaskNewContent.run()
        }

    }

    fun pause() {
        Log.d("sync", "PAUSE")
        timerUpdate.cancel()
        timerNewContent.cancel()

    }

    /**
     * Main Synching method -> fetches all queued questions from server and inserts them
     */

    private fun sync() {
        if (synchingStatus == 1) return //Skip when it is still running
        indicateSync(0)

        val syncSize = synchingIDs.size
        synchingStatus = 1
        val hashmap = synchingIDs.groupBy { it.first }

        val callback = object : RetrofitHelper.Companion.DisplayableListCallback {
            override fun onError(errorCode: Int) {
                synchingStatus = 2
                indicateSync(-1)

            }

            override fun onResponse(onServer: List<Displayable>) {
                GlobalScope.launch(Dispatchers.IO) {
                    indicateSync(1)

                    val converted = ContentModel.convertDisplayableList(onServer)
                    synchingProgress.addAll(converted.map { it.id })
                    if (synchingProgress.size == syncSize) {
                        synchingStatus = 0
                    } else {
                        synchingStatus = 2
                    }

                    val testArray = ArrayList<String>()
                    for (i in onServer) {
                        testArray.add(i.id)
                    }
                    val notOnServer =
                        synchingIDs.filter { onServer.find { itus -> itus.id == it.second } == null }



                    masterViewModel.DISK_IO.execute {
                        val number = masterViewModel.database.contentAccessor()
                            .deleteContentByIDs(notOnServer.map { it.second }, Displayable.QUESTION)
                        Log.d("sync", "DELETE $number")

                        masterViewModel.database.contentAccessor().updateData(converted)
                    }


                }
            }


        }

        hashmap[Displayable.QUESTION]?.map { it.second }?.let {
            masterViewModel.questionBase.getQuestionByIDs(
                it, callback
            )
        }
        /* hashmap[Displayable.USER]?.map { it.second }?.let {
             masterViewModel.userBase.getUsersByIDs(
                 it, callback)
         }*/
        /* hashmap[Displayable.TOPIC]?.map { it.second }?.let {
             masterViewModel.topicBase.getTopicsByIDs(
                 it, callback)
         }*/


    }

    private fun indicateSync(status: Int) {
        showSyncIndicatorUpdate()
        var color = Color.BLUE
        when (status) {
            -1 -> {
                color = Color.RED
                hideSyncIndicatorUpdate()
            }
            0 -> {

            }
            1 -> {
                color = Color.GREEN
                hideSyncIndicatorUpdate()
            }

        }
        syncIndicatorUpdate?.indeterminateDrawable?.colorFilter =
            PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }
    private fun indicateContentSync(status: Int) {
        showSyncIndicatorContent()
        var color = Color.MAGENTA
        when (status) {
            -1 -> {
                color = Color.RED
                hideSyncIndicatorContent()
            }
            0 -> {

            }
            1 -> {
                color = Color.GRAY
                hideSyncIndicatorContent()
            }

        }
        syncIndicatorContent?.indeterminateDrawable?.colorFilter =
            PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }
    private fun hideSyncIndicatorUpdate() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(2000)
            syncIndicatorUpdate?.visibility = View.INVISIBLE
        }
    }
    private fun hideSyncIndicatorContent() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(2000)
            syncIndicatorContent?.visibility = View.INVISIBLE
        }
    }
    private fun showSyncIndicatorContent() {
        syncIndicatorContent?.visibility = View.VISIBLE
    }
    private fun showSyncIndicatorUpdate() {
        syncIndicatorUpdate?.visibility = View.VISIBLE
    }

    fun registerLifecycleOwner(homeActivity: LifecycleOwner) {
        this.lifeCycleOwner = homeActivity
        lifeCycleOwner?.lifecycle?.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                pause()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                create()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                //   timerNewContent.purge()
                // timerUpdate.purge()
            }


        })
    }


}