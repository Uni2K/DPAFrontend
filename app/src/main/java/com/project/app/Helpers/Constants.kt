package com.project.app.Helpers

class Constants {
    companion object {


       const val SERVER_BASEURL = "http://192.168.178.21"
      //  const val SERVER_BASEURL = "http://192.168.178.58"
        const val FRAGMENT_STREAM: Int = 0
        const val FRAGMENT_HOT: Int = 1
        const val FRAGMENT_TOPICS: Int = 2

        const val LOADSIZE_INITIAL=5
        const val LOADSIZE_BEFORE=5
        const val LOADSIZE_AFTER=5


        const val SAVE_SELECTED_TAG = "SelectedTags"
        const val SAVE_HISTORY_TAG = "HistoryTags"


        const val QUESTION_TYPE_TXT2 = 0
        const val QUESTION_TYPE_TXT3 = 1
        const val QUESTION_TYPE_TXT4_LONG = 2
        const val QUESTION_TYPE_TXT4_SHRT = 3
        const val QUESTION_TYPE_TXT_GENERAL = 22
        const val QUESTION_TYPE_IMG2 = 4
        const val USER_TYPE_TXT_GENERAL= 100


        const val CONTENT_FEED = "FEED"
        const val CONTENT_STREAM = "STREAM"
        const val CONTENT_TRENDING = "HOT"
        const val CONTENT_NEW = "NEW"
      const val CONTENT_SEARCH = "SEARCH"
        const val CONTENT_NOTHING: String = "-1"
        const val CONTENT_TAGDETAIL = "TAGDETAIL"
      const val CONTENT_TOPICS = "TOPICS"

        const val CONTENT_ENDING_SOON = "ENDING_SOON"
        const val CONTENT_USER_VOTED = "USER_VOTED"
        const val CONTENT_USER_ASKED = "USER_ASKED"
        const val CONTENT_USER_FOLLOWING = "USER_FOLLOWING"

        const val FETCH_OVERLAD = 1

        const val ERROR_UNDEFINED = 9
        const val ERROR_WRONGPW = 10
        const val ERROR_NOUSERFOUND = 11
        const val ERROR_DUPLICATEEMAIL = 13
        const val ERROR_DUPLICATENAME = 14
        const val ERROR_TIMEOUT = 12
        const val ERROR_TOKENWRONG = 15
        const val ERROR_NOCONTENT = 16
        const val ERROR_NOQUESTION = 17
        const val ERROR_ANSWERINDEX = 18
        const val ERROR_UNDEFINED_VOTING = 19




        const val FIELD_URL = "FIELD_URL"
        const val FIELD_DESC = "FIELD_DESC"
        const val FIELD_LOCATION = "FIELD_LOCATION"

        const val SOCKET_LOADING: Int=0
        const val SOCKET_TIMEOUT: Int=1
        const val SOCKET_ERROR: Int=2
        const val SOCKET_CONNECTED: Int=3
        const val SOCKET_DISCONNECTED: Int=4
        const val ERROR_NOCONNECTION: Int=5


        const val IMPORTANCE_LVL1=1
        const val IMPORTANCE_LVL2=2
        const val IMPORTANCE_LVL3=3





    }
}