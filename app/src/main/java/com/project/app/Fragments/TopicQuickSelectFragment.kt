package com.project.app.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.app.Adapters.TopicChipAdapter
import com.project.app.Adapters.TopicGridAdapter
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Objects.Topic
import com.project.app.Paging.Displayable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TopicQuickSelectFragment : TopicProviderFragment() {



    override fun isTopicSelected(id: Topic): Boolean {
        return isTopicSelected(id._id)
    }

    override fun onClickTopicOverview(topic: Topic) {
        unsubscribeTopic(topic)
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




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        val retur= super.onCreateView(inflater, container, savedInstanceState)
        viewModel.userBase.loggedInUserChanged.observe(viewLifecycleOwner, Observer {
            it?.let { updateSubscriptions() }
        })
        return retur
    }


    private fun updateSubscriptions() {
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        GlobalScope.launch {

            //Check if Overview and All have the correct selection

            val populatedList: ArrayList<Topic> =
                ArrayList(viewModel.topicBase.getTopicsByStrings(ArrayList(viewModel.userBase.getSubscribedUserTopics())))
            selectedTopics.addAll(populatedList)
            launch(Dispatchers.Main) {
                (recyclerOverview.adapter as? TopicChipAdapter)?.submitList(populatedList)
                (recyclerAll.adapter as? TopicGridAdapter)?.update(populatedList,newList = false)
                (subTopicRecyclerView.adapter as? TopicGridAdapter)?.update(populatedList,newList = false)
            }
        }

    }



    override fun fillSelectionRecycler(
        unsortedTopics: List<Topic>,
        mmap: Map<String, List<Topic>>
    ) {
        val selectedTagList = ArrayList<Topic>()
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        var selectedTag = viewModel
            .userBase.getSubscribedUserTopics()
        for (string in selectedTag) {
            var topic: Topic? = unsortedTopics.find { it._id == string }
            if (topic != null) {
                selectedTagList.add(topic)
            }

        }

        GlobalScope.launch {
            val populatedList: ArrayList<Topic> =
                ArrayList(viewModel.topicBase.getTopics(selectedTagList))
            launch(Dispatchers.Main) {
                recyclerOverview.adapter =
                    TopicChipAdapter( this@TopicQuickSelectFragment)
                (recyclerOverview.adapter as TopicChipAdapter).submitList(populatedList)

            }

        }

    }











    private fun unsubscribeTopic(subTopic: Topic) {
        super.unselect(subTopic)
        showLoading()
        ViewModelProvider(parent).get(MasterViewModel::class.java).userBase.unsubscribeTopic(
            subTopic._id,
            object : RetrofitHelper.Companion.DisplayableListCallback {
                override fun onError(errorCode: Int) {
                    hideLoading()
                }

                override fun onResponse(content: List<Displayable>) {
                    hideLoading()
                    //updateRecyclersRemoved(subTopic)
                    ViewModelProvider(parent).get(MasterViewModel::class.java)
                        .userBase.updateUser(content[0])

                }
            })
    }

    private fun subscribeTopic(subTopic: Topic) {
        super.select(subTopic)
        showLoading()
        ViewModelProvider(parent).get(MasterViewModel::class.java).userBase.subscribeTopic(
            subTopic._id,
            object : RetrofitHelper.Companion.DisplayableListCallback {
                override fun onError(errorCode: Int) {
                    hideLoading()
                }

                override fun onResponse(content: List<Displayable>) {

                    Log.d("subscribe", "RETURN : ${content[0]}")

                    hideLoading()
                    ViewModelProvider(parent).get(MasterViewModel::class.java)
                        .userBase.updateUser(content[0])
                    // updateRecyclersInserted(subTopic)

                }
            })
    }


    private fun toggleTopicSubscription(subTopic: Topic) {

        if (isTopicSelected(subTopic._id)) {
            unsubscribeTopic(subTopic)
        } else {
            subscribeTopic(subTopic)
        }
    }

}