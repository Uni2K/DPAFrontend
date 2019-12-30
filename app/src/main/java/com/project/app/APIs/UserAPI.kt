package com.project.app.APIs

import com.project.app.Helpers.*
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * No implementation of the methods
 */
interface UserAPI {
    @GET("/users/me")
    suspend fun getAccount(@Header("Authorization") token: String): ResponseBody

    @POST("/users/login")
    suspend fun login(@Body request: LoginRequest): ResponseBody

    @POST("/users/signup")
    suspend fun singUp(@Body request: SignUpRequest): ResponseBody


    @POST("/users/me/logout")
    suspend fun logout(@Header("Authorization") token: String): ResponseBody

    @POST("/users/me/logoutall")
    suspend fun logoutAll(@Header("Authorization") token: String): ResponseBody

    @POST("/users/me/edit")
    suspend fun editAccount(@Header("Authorization") token: String, @Body request: EditRequest): ResponseBody

    @POST("/users/user")
    suspend fun getUser(@Body userid: UserRequest): ResponseBody

    @POST("/users/me/subscribe")
    suspend fun subscribe(@Header("Authorization") token: String, @Body userid: UserRequest): ResponseBody

    @POST("/users/me/unsubscribe")
    suspend fun unsubscribe(@Header("Authorization") token: String, @Body userid: UserRequest): ResponseBody

    @POST("/questions/voteopen")
    suspend fun voteOpen(@Body questionID: VoteRequest): ResponseBody

    @POST("/questions/voteclosed")
    suspend fun voteClosed(@Header("Authorization") token: String, @Body questionID: VoteRequest): ResponseBody

    @POST("/users/following")
    suspend fun getFollowing( @Body questionSingleID: QuestionSingleRequestConfig): ResponseBody

    @POST("/users/feed")
    suspend fun getFeed(@Body userid: UserRequest): ResponseBody


}
