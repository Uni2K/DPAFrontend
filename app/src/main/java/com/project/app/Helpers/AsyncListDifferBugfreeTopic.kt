package com.project.app.Helpers

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.project.app.Adapters.TopicChipAdapter
import com.project.app.Objects.Topic

class AsyncListDifferBugfreeTopic(adapter: TopicChipAdapter, callback: DiffUtil.ItemCallback<Topic>): AsyncListDiffer<Topic>(adapter,callback) {

    /**
     * https://stackoverflow.com/questions/49726385/listadapter-not-updating-item-in-recyclerview
     */
    override fun submitList(list: List<Topic?>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

}