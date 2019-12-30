package com.project.app.Paging

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.project.app.Objects.ContentModel

class ContentModelDiffCallback: DiffUtil.ItemCallback<ContentModel>() {
    override fun areItemsTheSame(oldItem: ContentModel, newItem: ContentModel): Boolean {
       val result=(ContentModel.toDisplayable(newItem)?.let {
            ContentModel.toDisplayable(oldItem)?.isSame(
                it
            )
        })?:false
      //  Log.i("areItemsTheSame","DATA: $result ${newItem.id}   ${oldItem.id}")
        return result

    }

    override fun areContentsTheSame(oldItem: ContentModel, newItem: ContentModel): Boolean {
        val result= ContentModel.toDisplayable(newItem)?.let {
            ContentModel.toDisplayable(oldItem)?.isContentTheSame(
                it
            )
        }

      //  Log.i("areContentTheSame","DATA: $result ${newItem.id}   ${!(result!=10 && result!=0)}")
        return !(result!=10 && result!=0)
    }

    override fun getChangePayload(oldItem: ContentModel, newItem: ContentModel): Any? {

        val result=ContentModel.toDisplayable(newItem)?.let {
            ContentModel.toDisplayable(oldItem)?.getChangePayload(
                it
            )
        }
        Log.i("getChangePayload","DATA: $result ${newItem.id} ")
        return result
    }
}