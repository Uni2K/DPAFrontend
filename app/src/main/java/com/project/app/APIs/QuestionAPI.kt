package com.project.app.APIs

import com.project.app.Helpers.QuestionMultiRequestConfig
import com.project.app.Helpers.QuestionSingleRequestConfig
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface QuestionAPI {
    @POST("/questions/simple")
    suspend fun getQuestion(@Body singleRequest: QuestionSingleRequestConfig): ResponseBody

    @POST("/questions/byIDs")
    suspend fun getQuestionsByIDs(@Body multiRequest: QuestionMultiRequestConfig): ResponseBody


}