package com.project.app.Helpers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LoginRequest(email: String, password: String) {
    @SerializedName("email")
    @Expose
    var email: String? = email
    @SerializedName("password")
    @Expose
    var password: String? = password

}


data class UserRequest(
    @Expose
    @SerializedName("id")
    var id: String, @Expose @SerializedName("type") var type: String? = null, @Expose @SerializedName(
        "key"
    ) var key: String? = null, @Expose @SerializedName("older") var older: Boolean = false,
    @Expose
    @SerializedName("loadSize")
    var loadSize: Int = 10

    )


class TopicRequest(topicid: String) {

    @SerializedName("topic")
    @Expose
    var topicid: String? = topicid

}

class EditRequest(arrayList: ArrayList<Pair<String, String>>) {
    @SerializedName("desc")
    @Expose
    var desc: String? = arrayList.find { it.first == Constants.FIELD_DESC }?.second
    @SerializedName("location")
    @Expose
    var location: String? = arrayList.find { it.first == Constants.FIELD_LOCATION }?.second
    @SerializedName("url")
    @Expose
    var url: String? = arrayList.find { it.first == Constants.FIELD_URL }?.second
}


class SignUpRequest(email: String, password: String, name: String) {
    @SerializedName("email")
    @Expose
    var email: String? = email
    @SerializedName("password")
    @Expose
    var password: String? = password
    @SerializedName("name")
    @Expose
    var name: String? = name

}

class VoteRequest(questionID: String, indexOfAnswer: Int) {
    @SerializedName("questionid")
    @Expose
    var questionid: String? = questionID
    @SerializedName("indexofanswer")
    @Expose
    var indexofAnswer: Int = indexOfAnswer


}