package com.project.app.Helpers

import com.google.gson.annotations.SerializedName

class QuestionSingleRequestConfig {
    @SerializedName("searchQuery")
    var searchQuery:String?=null

    @SerializedName("key")
    var key:String?=null

    @SerializedName("older")
    var older=false

    @SerializedName("loadSize")
    var loadSize=10

    @SerializedName("filterTags")
    var filterTags:String?=null

    @SerializedName("type")
    var type:String?=null

    @SerializedName("userid")
    var userid:String?=null



}