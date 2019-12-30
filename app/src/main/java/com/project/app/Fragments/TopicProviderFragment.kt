package com.project.app.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.google.android.material.appbar.AppBarLayout
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.TopicGridAdapter
import com.project.app.CustomViews.RecyclerViewInterceptor
import com.project.app.Helpers.CustomBottomSheetBehavior
import com.project.app.Helpers.MasterViewModel
import com.project.app.Interfaces.TopicFragmentInterface
import com.project.app.Interfaces.TopicSelectionInterface
import com.project.app.Objects.Topic
import com.project.app.R
import kotlin.math.hypot

open class TopicProviderFragment : Fragment(),
    TopicFragmentInterface, TopicSelectionInterface {

    var selectedTopics = ArrayList<Topic>()
    lateinit var recyclerOverview: RecyclerView
    lateinit var recyclerAll: RecyclerViewInterceptor
    lateinit var parent: HomeActivity

    private lateinit var bottomSheet: ViewGroup
    private lateinit var master: CoordinatorLayout
    lateinit var appBarLayout: AppBarLayout
    private lateinit var headerGroup: Group
    lateinit var subTopicRecyclerView: RecyclerViewInterceptor
    private lateinit var subTopicLayout: View
    private lateinit var loader:ProgressBar
    private lateinit var layoutParent: ConstraintLayout

    private lateinit var heading1:TextView
    private lateinit var heading2:TextView

    private lateinit var currentTopTopic: Topic
    var inBottomSheet: Boolean = false
    var topicLimit=10000

    var preset=PRESET_QUICKSELECT


    companion object{
        const val PRESET_QUICKSELECT=0
        const val PRESET_WELCOME=1
        const val PRESET_FILTER=2
        const val PRESET_CREATE=3

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view = inflater.inflate(R.layout.fragment_tag_new, null)
        recyclerAll = view.findViewById(R.id.tn_recycler_all)
        recyclerOverview = view.findViewById(R.id.tn_recycler_overview)

        headerGroup = view.findViewById(R.id.tn_header_group)
        subTopicLayout = view.findViewById(R.id.tn_layout_expanded)
        subTopicRecyclerView = view.findViewById(R.id.tn_recycler_expanded)
        layoutParent = view.findViewById(R.id.tn_layout_parent)
        loader=view.findViewById(R.id.tn_loading)

        recyclerAll.addItemDecoration(SpacingItemDecoration(0, 0))
        recyclerAll.layoutManager = GridLayoutManager(parent, 3)
        subTopicRecyclerView.addItemDecoration(SpacingItemDecoration(0, 0))
        subTopicRecyclerView.layoutManager = GridLayoutManager(parent, 3)

        while (subTopicRecyclerView.itemDecorationCount > 0) {
            subTopicRecyclerView.removeItemDecorationAt(0)
        }

        appBarLayout = view.findViewById(R.id.appbarlayout)
        master = view.findViewById(R.id.master)

        if (inBottomSheet) {
            var oldY = 0f
            val onTouchListener = object : RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                }

                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    var pullDown = false
                    if (oldY - e.rawY < 0) {
                        pullDown = true
                    }

                    oldY = e.rawY

                    if (e.action == MotionEvent.ACTION_MOVE && pullDown && isAppBarExpanded(
                            appBarLayout
                        )
                    ) {

                        appBarLayout.isLiftOnScroll
                        if ((rv.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition() == 0) {

                            //   Log.d("onT","Intercept EVENT: ${e.action}   ${oldY}  $pullDown")
                            if (::bottomSheet.isInitialized) setScrollable(
                                bottomSheet,
                                recyclerOverview
                            )  //Sets the reference to another view

                            return false
                        }
                    }

                    if (::bottomSheet.isInitialized) setScrollable(bottomSheet, rv)
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                }

            }


            val onTouchListener2 = object : RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                }

                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    // Log.d("onT","Intercept EVENT: ${e.action}  ${getScrollableChild(bottomSheet,rv)}    $rv")

                    if (::bottomSheet.isInitialized) setScrollable(bottomSheet, rv)
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                }

            }




            recyclerOverview.addOnItemTouchListener(onTouchListener2)
            subTopicRecyclerView.addOnItemTouchListener(onTouchListener)
            recyclerAll.addOnItemTouchListener(onTouchListener)


        }
        var spanCount=3
        if(topicLimit<10){
            spanCount=1
        }
        val layoutM = GridLayoutManager(parent, spanCount, RecyclerView.HORIZONTAL, false)
        recyclerOverview.layoutManager = layoutM

        recyclerOverview.setHasFixedSize(true)
        recyclerAll.setHasFixedSize(true)
        subTopicRecyclerView.setHasFixedSize(true)


        val transition = LayoutTransition()
        transition.setAnimateParentHierarchy(false)

        view.findViewById<ViewGroup>(R.id.tn_panel_master).layoutTransition = transition
        view.findViewById<ViewGroup>(R.id.master).layoutTransition = transition



         heading1 = view.findViewById<TextView>(R.id.tn_info1_tx)
         heading2 = view.findViewById<TextView>(R.id.tn_info2_tx)

        applyPreset()



        start()



        return view
    }

    private fun applyPreset() {
       when(preset){
           PRESET_QUICKSELECT->{
               heading1.text = "Here you can see the currently selected topics!"
               heading2.text = "What you choose will be displayed in your Feed"
           }
           PRESET_FILTER->{
               heading1.text = "Here you can see the currently selected topic!"
               heading2.text = "Choose some topics to filter the search results"
           }
           PRESET_CREATE->{
               heading1.text = "Here you can see the currently selected topic!"
               heading2.text = "Choose the single topic, that fits the question the most"
           }
           PRESET_WELCOME->{
               heading1.text = "Here you can see the currently selected topic!"
               heading2.text = "Choose some topics you are interested in"
           }
       }

    }

    override fun getParentTagName(id: String): String {
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        return viewModel.topicBase.tags.value?.second?.get("-1")?.find { it.id == id }?.name
            ?: "Other"
    }

    override fun collapseTopic() {
        closeSubMenu()
    }


    override fun expandTopic(parent: Topic) {
        expandParentTopic(parent)
    }


    fun isAppBarExpanded(abl: AppBarLayout): Boolean {
        val behavior: AppBarLayout.Behavior? =
            (abl.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as AppBarLayout.Behavior?
        return ((behavior)?.topAndBottomOffset == 0)
    }

    fun hideLoading() {
    loader.visibility=View.GONE
    }

    fun showLoading() {
    loader.visibility=View.VISIBLE
    }
    fun isTopicSelected(topic: String): Boolean {
        val sele=selectedTopics.find { it._id == topic }!=null
        return sele
    }

    private fun start() {
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        val observer: Observer<Pair<List<Topic>, Map<String, List<Topic>>>> =
            Observer { t ->
                if (t != null) {
                    Log.d("TopicQuickSelectFragmen", "Topic Observer Triggered $t")
                    loadInitial(t.first, t.second)
                    hideLoading()
                }
            }

        viewModel.topicBase.tags.observe(parent, observer)

        if (viewModel.topicBase.requestAllTagSub()) {
            observer.onChanged(viewModel.topicBase.tags.value)
        } else {
            showLoading()
        }

    }


    fun searchTags(txt: String) {


        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        if (viewModel.topicBase.requestAllTagSub()) {
            val unsorted_tags =
                ViewModelProvider(parent).get(MasterViewModel::class.java)
                    .topicBase.tags.value?.first
            var filtered = unsorted_tags?.filter { tag -> tag.name.contains(txt) }
            Log.d("search", "DD: $txt ${filtered?.size}")
            if (filtered != null) {
                createTagCloud(filtered, recyclerAll)
            }
        } else {
            //TAGS NOT READY
            //Can not happen, cause the loadingscreen is blocking everything till tags are here on the beginning
        }


    }

    fun resetTagCloud() {
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        if (viewModel.topicBase.requestAllTagSub()) {
            val mmap =
                ViewModelProvider(parent).get(MasterViewModel::class.java)
                    .topicBase.tags.value?.second
            mmap?.get("-1")?.let {
                createTagCloud(it, recyclerAll, false)
            }
        }


    }


    private fun createTagCloud(
        arrayList: List<Topic>,
        recyclerView: RecyclerView,
        isSubmenu: Boolean = false
    ) {
        var ar = ArrayList(arrayList)
        ar.sortBy { it.name }
        val specialTopics: List<Topic> = ar.filter { it._id.startsWith("x") }
        specialTopics.sortedBy { it._id }
        ar.removeAll { it._id.startsWith("x") }
        ar.addAll(0, specialTopics)

        if (recyclerView.adapter == null) {
            recyclerView.adapter = TopicGridAdapter(ar, this, isSubmenu)

        } else (recyclerView.adapter as TopicGridAdapter).update(ar, newList = true)
    }


    open fun fillSelectionRecycler(
        unsortedTopics: List<Topic>,
        mmap: Map<String, List<Topic>>
    ) {

    }

    private fun loadInitial(unsorted_topics: List<Topic>, mmap: Map<String, List<Topic>>) {
        mmap["-1"]?.let {
            createTagCloud(it, recyclerAll)
        }
       fillSelectionRecycler(unsorted_topics,mmap)

    }


    fun setScrollable(bottomSheet: View, recyclerView: RecyclerView) {
        val params = bottomSheet.layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            val coordinatorLayoutParams: CoordinatorLayout.LayoutParams = params
            val behavior = coordinatorLayoutParams.behavior as CustomBottomSheetBehavior
            behavior.setNestedScrollingChildRef(recyclerView)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var inBottomSheet_: Boolean? = it.getBoolean("bottomSheet", false)
            if (inBottomSheet_ != null) {
                inBottomSheet = inBottomSheet_
            }
            topicLimit=it.getInt("topicLimit",10000)
            preset=it.getInt("preset",PRESET_QUICKSELECT)
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            parent = context as HomeActivity
        }
    }

    fun setBottomSheet(bottomSheet: ViewGroup) {
        this.bottomSheet = bottomSheet
    }


    private fun expandParentTopic(topic: Topic) {


        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        if (viewModel.topicBase.requestAllTagSub()) {
            val tags =
                ViewModelProvider(parent).get(MasterViewModel::class.java)
                    .topicBase.tags.value?.second
            if (tags?.get(topic._id) != null) {

                currentTopTopic = topic
                tags.get(topic._id)?.let {

                    openSubMenu(topic, it)
                }


            } else {

                Toast.makeText(context, "NO TAGS AVAILABLE", Toast.LENGTH_SHORT).show()


            }
        }


    }


    private fun showCircularReveal(x: Int, y: Int, view: View) {

        val startRadius = 0f
        val endRadius = hypot(
            view.width.toDouble(), view.height.toDouble()
        )

        val anim = ViewAnimationUtils.createCircularReveal(
            view, x, y, startRadius,
            endRadius.toFloat()
        )

        view.visibility = View.VISIBLE

        anim.duration = 500
        anim.start()
    }

    private fun hideCircularReveal(
        x: Int,
        y: Int,
        view: View
    ) {

        val endRadius = 0f
        val startRadius = hypot(
            view.width.toDouble(), view.height.toDouble()
        )

        val anim = ViewAnimationUtils.createCircularReveal(
            view, x, y, startRadius.toFloat(),
            endRadius
        )
        anim.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.INVISIBLE

            }
        })


        anim.duration = 500
        anim.start()
    }


    private fun openSubMenu(
        topic: Topic,
        it: List<Topic>
    ) {
        recyclerAll.visibility = View.INVISIBLE
        createTagCloud(it, subTopicRecyclerView, true)
        val x: Int = recyclerAll.lastClickedCoordinates?.x ?: 0
        val y: Int = recyclerAll.lastClickedCoordinates?.y ?: 0

        showCircularReveal(x, y, subTopicLayout)


    }

    private fun closeSubMenu() {
        recyclerAll.visibility = View.VISIBLE

        val x: Int = subTopicRecyclerView.lastClickedCoordinates?.x ?: 0
        val y: Int = subTopicRecyclerView.lastClickedCoordinates?.y ?: 0

        hideCircularReveal(x, y, subTopicLayout)


    }

    override fun onClickTopicOverview(topic: Topic) {

    }

    override fun isTopicSelected(id: Topic): Boolean {
        return selectedTopics.find { it._id==id._id }!=null
    }

    override fun onClickTopicAll(topic: Topic) {

    }

    fun unselect(subTopic: Topic) {
        selectedTopics.removeAll { it._id==subTopic._id }

    }
    fun select(subTopic: Topic) {
        if(selectedTopics.size==topicLimit){
            Toast.makeText(parent,"Limit reached!",Toast.LENGTH_LONG).show()
            return
        }
        selectedTopics.add(subTopic)

    }
}