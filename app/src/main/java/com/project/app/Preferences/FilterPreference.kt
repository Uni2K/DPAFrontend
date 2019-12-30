package com.project.app.Preferences

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.TopicChipAdapter
import com.project.app.Dialogs.TopicPickDialogFragment
import com.project.app.Fragments.TopicProviderFragment
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.TouchAnimationHelper
import com.project.app.Interfaces.TopicControllerInterface
import com.project.app.Interfaces.TopicSelectionInterface
import com.project.app.Objects.Topic
import com.project.app.R

class FilterPreference : Preference, TopicSelectionInterface {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private lateinit var scrollGroup: Group
    private lateinit var idleGroup: Group

    init {
        layoutResource = R.layout.preference_filter
    }

    lateinit var recyclerView: RecyclerView

    private fun updateRecycler() {
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun openTagFragment() {
        val frag = TopicPickDialogFragment()
        frag.setSelection((recyclerView.adapter as TopicChipAdapter).getList())
        frag.setCallback(object: TopicControllerInterface{
            override fun onSubTopicChosen(subtag: Topic) {
                addFilterTopic(subtag)
            }

            override fun onSubTopicNotChosen(subtag: Topic) {
                removeFilterTopic(subtag)
            }

            override fun onClearAll() {
                removeAllFilterTopic()
            }

            override fun onRestore() {
            }

            override fun onTopicsChanged() {

            }
        })
        val bundle = Bundle()
        bundle.putInt("tagLimit", 10000)
        bundle.putInt("preset", TopicProviderFragment.PRESET_FILTER)
        frag.arguments = bundle
        frag.show((context as HomeActivity).supportFragmentManager, "")
    }


    private fun updateScroll(value: Int?=null) {
        Log.d("updateScroll","DATA: ${recyclerView.adapter?.itemCount}   $value")

        if(value!=null && value>0){
            idleGroup.visibility = View.GONE
            scrollGroup.visibility = View.VISIBLE
        }else if(value!=null) {
            idleGroup.visibility = View.VISIBLE
            scrollGroup.visibility = View.GONE
        }else{
        if (recyclerView.adapter?.itemCount == 0 || recyclerView.adapter == null ) {
            idleGroup.visibility = View.VISIBLE
            scrollGroup.visibility = View.GONE
        } else {
            idleGroup.visibility = View.GONE
            scrollGroup.visibility = View.VISIBLE
        }}
    }

    private fun updateStorage(){
        val topicList= getViewModel().topicBase.topicsToStrings((recyclerView.adapter as TopicChipAdapter).getList())
        persistStringSet(HashSet(topicList))

    }

    private fun getViewModel(): MasterViewModel {
        return ViewModelProvider((context as HomeActivity)).get(MasterViewModel::class.java)
    }


    private fun addFilterTopic(subtopic:Topic){
        if (recyclerView.adapter == null) {
            recyclerView.adapter= TopicChipAdapter(this)
        }
        val arrayList: ArrayList<Topic> = ( recyclerView.adapter as TopicChipAdapter).getList()
        arrayList.add(subtopic)
        ( recyclerView.adapter as TopicChipAdapter).submitList(arrayList)
        updateRecycler()
        updateScroll()
        updateStorage()

    }

    private fun removeFilterTopic(subtopic:Topic){
        if (recyclerView.adapter == null) {
            recyclerView.adapter= TopicChipAdapter(this)
        }
        val arrayList: ArrayList<Topic> = ( recyclerView.adapter as TopicChipAdapter).getList()
        arrayList.removeAll { it._id==subtopic._id }
        ( recyclerView.adapter as TopicChipAdapter).submitList(arrayList)
        updateRecycler()
        updateScroll(arrayList.size)
        updateStorage()

    }
    private fun removeAllFilterTopic(){
        if (recyclerView.adapter == null) {
            recyclerView.adapter= TopicChipAdapter(this)
        }
        val arrayList: ArrayList<Topic> = ( recyclerView.adapter as TopicChipAdapter).getList()
        arrayList.clear()
        ( recyclerView.adapter as TopicChipAdapter).submitList(arrayList)
        updateRecycler()
        updateScroll()
        updateStorage()
    }
    private fun setSelectedTags() {

        val arr =
            getViewModel().topicBase.getTopicsByStrings(ArrayList(getViewModel().userBase.getSubscribedUserTopics()))
        if (arr.isEmpty()) {
            Toast.makeText(context, "No Selected Tags available", Toast.LENGTH_LONG).show()
            return
        }
        if (recyclerView.adapter == null) {
            recyclerView.adapter= TopicChipAdapter(this)
        }

        (recyclerView.adapter as TopicChipAdapter).submitList(arr)

        updateScroll(arr.size)

    }


    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (defaultValue == null) return
        if (restorePersistedValue) {
            // Restore state
        } else {
            // Set state
            setStoredTopics(defaultValue as MutableSet<String>?)
        }
    }

    private fun setStoredTopics(mutableSet: MutableSet<String>?) {
        if (mutableSet != null) {
            if(mutableSet.size>0){

                if (recyclerView.adapter == null) {
                    recyclerView.adapter= TopicChipAdapter(this)
                }
                (recyclerView.adapter as TopicChipAdapter).submitList(getViewModel().topicBase.getTopicsByStrings(ArrayList(mutableSet)))


                updateScroll()
                updateRecycler()
                updateStorage()
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Set<String> {
        return HashSet<String>()
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)


        val addSelected: TextView = holder?.findViewById(R.id.settings_filter_selected) as TextView
        val addNew: TextView = holder?.findViewById(R.id.settings_filter_add) as TextView
        scrollGroup = holder.findViewById(R.id.scrollGroup) as Group
        idleGroup = holder.findViewById(R.id.idleGroup) as Group
        recyclerView = holder.findViewById(R.id.settings_filter_recycler) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager= LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)

        addSelected.setOnClickListener {
            setSelectedTags()
        }
        addNew.setOnClickListener {
            openTagFragment()
        }

        val edit= holder.findViewById(R.id.settings_filter_edit)
        val touchAnimationHelper= TouchAnimationHelper()
        touchAnimationHelper.addTouchAnimation(edit,touchAnimationHelper.ANIMATION_PEEK_RIGHT, object :View.OnClickListener{
            override fun onClick(p0: View?) {
                openTagFragment()
            }

        })



    }

    //Ignored
    override fun onClickTopicOverview(topic: Topic) {
    removeFilterTopic(topic)
    }
    //Ignored
    override fun isTopicSelected(id: Topic): Boolean {
        return false
    }

    //Ignored
    override fun onClickTopicAll(topic: Topic) {
    }

}