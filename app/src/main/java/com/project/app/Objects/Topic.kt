package com.project.app.Objects

import android.os.Bundle
import com.project.app.Paging.Displayable

data class Topic(
    var _id: String = "-1",
    var name: String = "-1",
    var enabled: Boolean = true,
    var parent: String = "-2",
    var color: String = "#000000"
) : Displayable(type= Displayable.TOPIC,data=this) {

    init {
        id=_id
    }
    fun compareTo(newItem: Topic?): Int {
        if(newItem==null)return 10
        if (this._id != newItem._id) return 0
        if (this.name != newItem.name) return 1
        if (this.enabled != newItem.enabled) return 3
        if (this.parent != newItem.parent) return 4
        if (this.color != newItem.color) return 5
        return 10

    }
     fun getChangePayload(topicOld: Topic, topicNew: Topic): Bundle {

        val diff = Bundle()

         
         if (topicOld.name != topicNew.name) {
             diff.putString("name", topicNew.name)
         }
         if (topicOld.color != topicNew.color) {
             diff.putString("color", topicNew.color)
         }
         if (topicOld.parent != topicNew.parent) {
             diff.putString("parent", topicNew.parent)
         }
        if (diff.size() == 0) {
            diff.putBoolean("allEqual", true)
        }
        return diff

    }
}


