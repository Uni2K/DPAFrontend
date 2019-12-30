package com.project.app.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Adapters.TopicChipAdapter
import com.project.app.Adapters.TopicGridAdapter
import com.project.app.Helpers.MasterViewModel
import com.project.app.Interfaces.TopicControllerInterface
import com.project.app.Objects.Topic
import com.project.app.R
import kotlinx.android.synthetic.main.adapter_topics_default_trending.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TopicPickFragment : TopicProviderFragment() {

    var topicControllerInterface: TopicControllerInterface? = null
    override fun isTopicSelected(id: Topic): Boolean {
        return isTopicSelected(id._id)
    }

    override fun onClickTopicOverview(topic: Topic) {
        unselectTopic(topic)
    }

    override fun onClickTopicAll(topic: Topic) {
        toggleTopicSubscription(topic)
    }


    fun onControlBack() {
        resetTagCloud()
    }

    fun onControlafterTextChange(s: String) {

        searchTags(s)
    }

    fun onControlonTextChange(s: String) {

    }

    fun onControlHideSearch() {
        appBarLayout.setExpanded(true, true)
    }

    fun onControlShowSearch() {
        appBarLayout.setExpanded(false, true)
    }




    private fun updateSubscriptions() {
        (recyclerOverview.adapter as TopicChipAdapter).submitList(selectedTopics)
        (recyclerAll.adapter as TopicGridAdapter).update(selectedTopics, newList = false)
        (subTopicRecyclerView.adapter as TopicGridAdapter).update(selectedTopics, newList = false)


    }

    fun setSelection(list: List<Topic>) {
        selectedTopics=ArrayList(list)
    }


    override fun fillSelectionRecycler(
        unsortedTopics: List<Topic>,
        mmap: Map<String, List<Topic>>
    ) {

        recyclerOverview.adapter =
            TopicChipAdapter( this@TopicPickFragment)
        (recyclerOverview.adapter as TopicChipAdapter).submitList(selectedTopics)

    }


    private fun unselectTopic(subTopic: Topic) {
        super.unselect(subTopic)
        updateSubscriptions()

        topicControllerInterface?.onSubTopicNotChosen(subTopic)
    }

    private fun selectTopic(subTopic: Topic) {
        super.select(subTopic)
        updateSubscriptions()
        topicControllerInterface?.onSubTopicChosen(subTopic)


    }


    private fun toggleTopicSubscription(subTopic: Topic) {
        if (isTopicSelected(subTopic._id)) {
            unselectTopic(subTopic)
        } else {
            selectTopic(subTopic)
        }
    }

    fun setCallBack(topicControllerInterface: TopicControllerInterface) {
        this.topicControllerInterface = topicControllerInterface
    }

}