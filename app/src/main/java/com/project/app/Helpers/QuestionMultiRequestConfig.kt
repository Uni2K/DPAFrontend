package com.project.app.Helpers

import com.google.gson.annotations.SerializedName

data class QuestionMultiRequestConfig(

    @SerializedName("ids")
    var ids:List<String>?=null
)



