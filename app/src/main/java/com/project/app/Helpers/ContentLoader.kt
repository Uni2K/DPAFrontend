package com.project.app.Helpers

import android.util.Log
import android.view.View
import androidx.annotation.MainThread
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.DisplayablePagingAdapter
import com.project.app.CustomViews.ContentDisplay
import com.project.app.Interfaces.QuestionController
import com.project.app.Objects.ContentModel
import com.project.app.Objects.QuestionStackInfo
import com.project.app.Objects.Topic
import com.project.app.Objects.User
import com.project.app.Paging.*
import com.project.app.R
import kotlinx.coroutines.*

class ContentLoader(
    val lifeCycleOwner: Fragment,
    var parentActivity: HomeActivity,
    parentView: View

) {

    //Misc
    var model: MasterViewModel =
        ViewModelProvider(parentActivity).get(MasterViewModel::class.java)
    var contentName: String = Constants.CONTENT_NOTHING
    var isFirstContent = MutableLiveData(false)
    var contentChanged = false
    var isFullyReady=MutableLiveData<Boolean>(false)

    //Handler
    var errorHandler: ErrorHandler? = null
    var notificationHandler: NotificationHandler? = null


    //Views & Adapter
    lateinit var contentDisplay: ContentDisplay



    init {
        loadViews(parentView)
        GlobalScope.launch(Dispatchers.Main) {
            contentDisplay.showLoading()
            delay(1000)
            contentDisplay.setAdapter(
                DisplayablePagingAdapter(
                    model.localBase,
                    lifeCycleOwner as QuestionController, lifecycleOwner = lifeCycleOwner
                )
            )
            contentDisplay.initSwipeToRefresh(parentActivity, lifeCycleOwner)
            //setContent(initialContentName_, *payLoad)
            isFullyReady.setValue(true)
        }
        scheduleImmediateUpdates()
        setUpHandler()

        model.registerContentLoader(this)
        lifeCycleOwner.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                model.unregisterContentLoader(this@ContentLoader)
            }
        })
    }


    var contentObserver: Observer<PagedList<ContentModel>> =
        Observer {
            Log.e("contentObserver", "DATA: ${it.size}   $contentName")

            contentDisplay.submit(it)
            if (it.size > 0) {
                if (contentChanged) {
                    isFirstContent.value = true
                }
                contentDisplay.stateReady()
            }
        }


    var networkObserver: Observer<NetworkState> = Observer {
        Log.d("ContentLoader","NETWORK OBSERVER $it  $contentName")
        when (it.status) {
            Status.FAILED -> {
                contentDisplay.stateError()
            }
            Status.SUCCESS -> {

                contentDisplay.success()

            }
            Status.RUNNING -> {
                contentDisplay.running()
            }
        }


    }

    var stackObserver: Observer<ArrayList<Pair<String, String>>> = Observer {
        if (it.size == 0) return@Observer
        val latestEntry = it[it.size - 1]
        if (latestEntry.first == PagingRequestHelper.RequestType.BEFORE.toString() || latestEntry.first == PagingRequestHelper.RequestType.INITIAL.toString()) {
            if (latestEntry.second == PagingRequestHelper.Status.RUNNING.toString()) {
              //  contentDisplay.showLoading()
            } else {
                contentDisplay.hideLoading()
            }
        }
    }

    @MainThread
    private fun loadViews(view: View) {
        if(view.findViewById<View>(R.id.cd_parent)==null){
            throw NotImplementedError("CONTENTDISPLAY NOT IMPLEMENTED IN XML -> DO IT!")
        }
        contentDisplay = view.findViewById(R.id.cd_parent)
        contentDisplay.setUp(this)
    }


    private fun setUpHandler() {

        errorHandler = ErrorHandler(parentActivity, lifeCycleOwner, contentDisplay)

        val inflater = AsyncLayoutInflater(parentActivity)

        GlobalScope.launch(Dispatchers.Main) {
            delay(5000)
            inflater.inflate(
                R.layout.update_notifier, contentDisplay
            ) { view, resid, parent ->
               contentDisplay.addView(view)

                notificationHandler =
                  NotificationHandler(this@ContentLoader, parentActivity, lifeCycleOwner,contentDisplay)
            }

        }
    }


    @MainThread
    private fun scheduleImmediateUpdates() {

        isFirstContent.observe(lifeCycleOwner, Observer {

            if (contentChanged && it) {
                //SUBMIT LIST FIRST CALLED FOR THIS CONTENT -> ALSO CONTENT CHANGED
                val currentList = contentDisplay.getList()

                GlobalScope.launch {
                    contentChanged = false

                    val ids = ArrayList<String>()
                    if (currentList != null) {
                        for (q in currentList.snapshot()) {
                            if (q != null) ids.add(q.id)
                        }
                    }


                    ids.let { iti ->
                        currentList?.let { list ->
                            model.DISK_IO.execute {
                                model.contentSyncer.syncCollectionCM(list.snapshot())
                            }
                        }


                        isFirstContent.postValue(false)


                    }

                }

            }
        })
    }


    @MainThread
    private fun setContent(newContentName: String, vararg payLoad: String) {
        Log.d("SETCONTENT", "SETCONTENT $newContentName")

        val oldContent = contentName
        if (newContentName == Constants.CONTENT_NOTHING) {
            if (newContentName != contentName) {

                model.contentStorage.value.let {
                    it?.get(contentName)?.pagedList?.removeObservers(lifeCycleOwner)
                    it?.get(contentName)?.pagedList?.removeObserver(contentObserver)
                }
            }

            contentDisplay.submit(null)
            return
        }

        addCustomStoreManager(newContentName)
        var newContent = newContentName
        for (s in payLoad) {
            newContent = newContent.plus(s)
        }

        model.contentStorage.observe(
            lifeCycleOwner,
            object : Observer<HashMap<String, Listing<ContentModel>>> {
                override fun onChanged(it: HashMap<String, Listing<ContentModel>>?) {

                    if (it?.get(newContent) != null) {
                        Log.d("ContentLoader","onChanged ContentStorage $contentName  $newContent")
                        contentChanged = true

                        //   if (newContent != contentName) {
                        it[newContent]?.pagedList?.observe(
                            lifeCycleOwner,
                            contentObserver
                        )


                        it[newContent]?.networkState?.observe(
                            lifeCycleOwner,
                            networkObserver
                        )

                        model.contentStorage.removeObserver(this)
                        if (newContent != contentName) {
                            it[contentName]?.pagedList?.removeObservers(lifeCycleOwner)
                            it[contentName]?.pagedList?.removeObserver(contentObserver)
                        }
                        contentName = newContent


                    }
                }
            })


        model.contentStackMap.observe(
            lifeCycleOwner,
            object : Observer<HashMap<String, MutableLiveData<ArrayList<Pair<String, String>>>>> {
                override fun onChanged(it: HashMap<String, MutableLiveData<ArrayList<Pair<String, String>>>>?) {
                    if (it?.get(newContent) != null) {

                        it[newContent]?.observe(lifeCycleOwner, stackObserver)
                        model.contentStackMap.removeObserver(this)

                        if (oldContent != newContent) {
                            model.contentStackMap.value?.get(oldContent)
                                ?.removeObservers(lifeCycleOwner)
                            model.contentStackMap.value?.get(oldContent)
                                ?.removeObserver(stackObserver)
                        }
                    }
                }
            })


        val repContent = model.pagingBase.getContent(newContent, 5)
        model.updateContentHashmap(newContentName, repContent as Listing<ContentModel>)

        model.notifyOnNewContentInserted(
            contentName,
            lifeCycleOwner,
            Observer<Pair<Int, QuestionStackInfo>> {
                if (it.first != 0) {
                    notificationHandler?.setNewContentNotifier(it)

                }

            })


    }

    /**
     * Watches the changes made e.g. in subscriptions and readjusts the database
     */
    @MainThread
    private fun addCustomStoreManager(contentName: String) {
        if (contentName == Constants.CONTENT_FEED) {
            model.userBase.loggedInUserChanged.observe(lifeCycleOwner, object : Observer<User?> {

                override fun onChanged(t: User?) {
                    model.userBase.loggedInUserChanged.removeObserver(this)
                    t?.let {
                        GlobalScope.launch(Dispatchers.IO) {
                            //remove the subscriptions from the local database that the user has not subscribed to
                            val userIDs: List<String> = model.userBase.getSubscribedUsers()
                            val topicIDs: List<Topic> =
                                model.topicBase.getTopicsByStrings(ArrayList(model.userBase.getSubscribedUserTopics()))
                            topicIDs.forEach { topic ->
                                topic.data = "-1"
                                topic.id = "-1"
                            }
                            model.DISK_IO.execute {
                                val deleted = model.database.contentAccessor()
                                    .deleteSubscribedUserContent(
                                        userIDs,
                                        topicIDs,
                                        Displayable.QUESTION
                                    )
                                Log.d("CustomStoreManager", "DELETED: $deleted")

                            }


                        }

                    }


                }

            })


        }
    }

    fun notificationClicked() {
        contentDisplay.notificationClicked()
    }

    fun fetchMore() {
        model.fetchMore(contentName)

    }

    fun enqueueContent(contentName: String, vararg payLoad: String) {
        var newContent=contentName
        for (s in payLoad) {
            newContent = newContent.plus(s)
        }
        isFullyReady.value?.let {
        if(it){
           setContent(newContent)
        }else{
            isFullyReady.observe(lifeCycleOwner, object: Observer<Boolean> {
                override fun onChanged(t: Boolean?) {
                    t?.let {
                        if(t){
                            isFullyReady.removeObserver(this)
                            setContent(newContent)

                        }
                    }

                }

            })
        }}
    }


}