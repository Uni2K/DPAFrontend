package com.project.app.CustomViews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.DisplayablePagingAdapter
import com.project.app.Objects.ContentModel
import com.project.app.Helpers.ContentLoader
import com.project.app.R
import kotlinx.coroutines.*

class ContentDisplay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private lateinit var swipeLayout: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView
    lateinit var contentLoader: ContentLoader


    init {
        val v = View.inflate(context, R.layout.contentdisplay, this)
        swipeLayout = v.findViewById(R.id.cd_swipelayout)
        recyclerView = v.findViewById(R.id.cd_content)
        swipeLayout.setColorSchemeColors(
            resources.getColor(android.R.color.holo_blue_bright, null),
            resources.getColor(android.R.color.holo_green_light, null),
            resources.getColor(android.R.color.holo_orange_light, null),
            resources.getColor(android.R.color.holo_red_light, null)
        )
        // addView(v)

    }

    public fun setUp(contentLoader: ContentLoader) {
        this.contentLoader = contentLoader
    }

    @MainThread
    fun initSwipeToRefresh(parentActivity: HomeActivity, lifeCycleOwner: LifecycleOwner) {


        swipeLayout.setOnRefreshListener {

            showLoading()
            contentLoader.fetchMore()
        }
        var scrollHandler: Job? = null

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                parentActivity.onScrollHandler(dy)

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {

                        scrollHandler = GlobalScope.launch {
                            delay(1500)
                            launch(Dispatchers.Main) { parentActivity.onScrollHandler(-2000) }
                        }
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        scrollHandler?.cancel()

                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        scrollHandler?.cancel()

                    }
                }
            }
        })
    }

    fun hideLoading() {
        swipeLayout.isRefreshing = false
    }

    fun showLoading() {

        swipeLayout.isRefreshing = true
    }

    fun setAdapter(displayablePagingAdapter: DisplayablePagingAdapter) {
        recyclerView.adapter = displayablePagingAdapter

    }

    fun submit(it: PagedList<ContentModel>?) {
        (recyclerView.adapter as? DisplayablePagingAdapter)?.submitList(it)

    }

    fun running() {
        if ((recyclerView.adapter as? DisplayablePagingAdapter)?.itemCount == 0) {
            //No Answer from server but no content->
          //  showLoading()  //Do not display with plain running
        }
    }


    fun success() {

        if ((recyclerView.adapter as DisplayablePagingAdapter).itemCount == 0) {
            //Answer from server but no content->
            GlobalScope.launch(Dispatchers.Main) {
                delay(1000) //To make sure there is no content
                Log.d("stateNOCONTENT","PRE")
                if ((recyclerView.adapter as? DisplayablePagingAdapter)?.itemCount == 0) {
                    stateNoContent()
                }
            }


        }
        hideLoading()

    }

    private fun stateNoContent() {
        Log.d("stateNOCONTENT","ERROR")

        //errorHandler?.showErrorMessage(Constants.ERROR_NOCONTENT)
        hideLoading()

    }

    fun getList(): PagedList<ContentModel>? {
        return (recyclerView.adapter as? DisplayablePagingAdapter)?.currentList
    }

    fun notificationClicked() {
        recyclerView.smoothScrollToPosition(0)
    }

    fun stateReady() {
        Log.d("stateREADY","ERROR")
        hideLoading()
    }

    fun stateError() {
        Log.d("stateERROR","ERROR")
        if(getList()?.isNullOrEmpty() != false){
           // errorContainer.visibility=View.VISIBLE
        }


        hideLoading()
    }

    fun getParentView(): View {
        return swipeLayout
    }


}