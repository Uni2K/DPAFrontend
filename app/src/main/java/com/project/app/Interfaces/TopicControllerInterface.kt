package com.project.app.Interfaces

import com.project.app.Objects.Topic

interface TopicControllerInterface {
    fun onSubTopicChosen(subtag: Topic)
    fun onSubTopicNotChosen(subtag: Topic)
    fun onClearAll()
    fun onRestore()
    fun onTopicsChanged()


}