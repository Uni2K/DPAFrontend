package com.project.app.APIs

import com.project.app.Helpers.TopicRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface TopicAPI {
    @POST("/topics/details")
    suspend fun getTopicDetails(@Body request: TopicRequest): ResponseBody

    @POST("/topics/all")
    suspend fun getTopicAll(): ResponseBody

}