package com.project.app.Interfaces

import com.project.app.Objects.Topic

interface TopicFragmentInterface {
    fun expandTopic(parent: Topic)
    fun collapseTopic()
    fun getParentTagName(id:String):String


}