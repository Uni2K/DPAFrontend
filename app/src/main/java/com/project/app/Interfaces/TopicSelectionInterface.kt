package com.project.app.Interfaces

import com.project.app.Objects.Topic

interface TopicSelectionInterface {
    fun onClickTopicOverview(topic: Topic)
    fun isTopicSelected(id:Topic):Boolean
    fun onClickTopicAll(topic:Topic)

}