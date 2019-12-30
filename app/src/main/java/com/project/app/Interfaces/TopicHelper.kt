package com.project.app.Interfaces

interface TopicHelper {
    fun onTopicCount(topicID: String,count:Long)
    fun onVoteCount(topicID: String,count:Long)
    fun onError(errorCode:Int)
}