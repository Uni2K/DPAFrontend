package com.project.app.Bases

import android.util.Log
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import com.project.app.APIs.UserAPI
import com.project.app.Helpers.*
import com.project.app.Objects.ParsingConfig
import com.project.app.Objects.Update
import com.project.app.Objects.User
import com.project.app.Paging.Displayable
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.reflect.Type


class UserBase(val masterViewModel: MasterViewModel) {
    var service: UserAPI? = null

    //Triggers
    var userVoteNotifier: MutableLiveData<Update> =
        MutableLiveData() //Called when the user Voted and the server response is here
    var userUpdateTrigger = MutableLiveData<Boolean>(false)
    val refreshAccountScreenTrigger = MutableLiveData<User>()
    var loggedInUserChanged: MutableLiveData<User?> =
        MutableLiveData(null) //User when logged in, full details except token, pw -> triggered everytime something changes, e.g. subscriptions

    init {
        loginUser()
        userVoteNotifier.observeForever {
            masterViewModel.localBase.addVote(it)
        }
     // fetchAllUserVotes()
    }


    private fun getUserService(): UserAPI? {
        if (service == null) {
            service = masterViewModel.getRetrofitClient()?.create(UserAPI::class.java)
        }
        return service
    }

    @MainThread
    private fun analyseAllUserVotes(args: Array<Any>?) {
        //TODO rework
        if (args == null) return
        GlobalScope.launch {
            val moshi = Moshi.Builder().build()

            var type: Type = Types.newParameterizedType(List::class.java, Update::class.java)
            var adapter: JsonAdapter<ArrayList<Update>> = moshi.adapter(type)
            var questionData: ArrayList<Update> = adapter.fromJson(args[0].toString())!!

         //   viewModel.localBase.updateVoteStore(questionData)

        }

    }
    /**
     * Something changed on the server, notified by client or socket
     * Call appwide for an update -> Quick Select, Feed, Account
     */
    fun updateUser(displayable: Displayable?) {
        loggedInUserChanged.value=displayable?.getUser()
    }

    fun isLoggedIn(): Boolean {
        return loggedInUserChanged.value!=null
    }

    /**
     * Try to get the latest User infos about the logged in user -> Use them for FastTopics and login screen -> replace storedUser Informations
     */
    @MainThread
    private fun loginUser(refreshAccount: Boolean = false) {
      try {


          val token = masterViewModel.localBase.getStoredLogin()?.token
          token?.let {
              RetrofitHelper.launchControlled(masterViewModel,
                  null, object : RetrofitHelper.Companion.DisplayableListCallback {
                      override fun onResponse(content: List<Displayable>) {
                          if (content.isNotEmpty()) loggedInUserChanged.value = content[0] as? User
                          if (refreshAccount) {
                              refreshAccountScreenTrigger.value = content[0] as? User
                          }
                      }

                      override fun onError(errorCode: Int) {

                      }
                  }, codeToExecute = {
                      getUserService()?.getAccount(token)
                  })
          }
      }catch (e:Exception){
          e.printStackTrace()
      }
    }

    fun getSubscribedUserTopics(): List<String> {
        return loggedInUserChanged.value?.subscriptions?.filter { it.type == "T" }?.map { it.content }
            ?: ArrayList<String>()
    }

    fun getSubscribedUsers(): List<String> {
        return loggedInUserChanged.value?.subscriptions?.filter { it.type == "U" }?.map { it.content }
            ?: ArrayList<String>()
    }
    /**
     * Pre Validation on client side, to speed up and avoid useless calls with empty token
     */
    @AnyThread
    fun validateToken(token: String?): Boolean {
        if (token == null) return false
        if (token == "null") return false
        if (token.length < 10) return false
        return true
    }

    @MainThread

    fun getAccount(token: String, callback: RetrofitHelper.Companion.DisplayableListCallback) {
        RetrofitHelper.launchControlled(masterViewModel,
            null, callback, codeToExecute = {
                getUserService()?.getAccount(token)
            })

    }

    @MainThread
    fun getFeed(questionSingleConfig: QuestionSingleRequestConfig?,
                parsingConfig: ParsingConfig,
                callback: RetrofitHelper.Companion.DisplayableListCallback
    ) {
        var userid = masterViewModel.localBase.getStoredLogin()?.id
        userid?.let {
            RetrofitHelper.launchControlled(
                masterViewModel,
                parsingConfig,
                callback,
                codeToExecute = {
                    getUserService()?.getFeed(UserRequest(loadSize = questionSingleConfig?.loadSize?:0, key = questionSingleConfig?.key, older = questionSingleConfig?.older?:true,id = userid))
                })
        }


    }

    @MainThread
    fun getUser(userid: String, callback: RetrofitHelper.Companion.DisplayableListCallback) {
        RetrofitHelper.launchControlled(masterViewModel, null, callback, codeToExecute = {
            getUserService()?.getUser(UserRequest(id=userid))
        })

    }

    @MainThread
    fun getFollowing(
        userid: QuestionSingleRequestConfig,
        callback: RetrofitHelper.Companion.DisplayableListCallback
    ) {
        RetrofitHelper.launchControlled(masterViewModel, null, callback, codeToExecute = {
            getUserService()?.getFollowing(userid)
        })

    }

    @MainThread
    fun subscribe(
        token: String,
        id: String,
        callback: RetrofitHelper.Companion.DisplayableListCallback, type:String="U"
    ) {
        RetrofitHelper.launchControlled(masterViewModel, null, callback, codeToExecute = {
            getUserService()?.subscribe(token, UserRequest(id=id,type=type))
        })

    }
    @MainThread
    fun unsubscribe(
        token: String,
        id: String,
        callback: RetrofitHelper.Companion.DisplayableListCallback, type:String="U"
    ) {
        RetrofitHelper.launchControlled(masterViewModel, null, callback, codeToExecute = {
            getUserService()?.unsubscribe(token, UserRequest(id=id,type=type))
        })

    }
    @MainThread
    fun signUp(
        email: String,
        name: String,
        pw: String,
        callback: RetrofitHelper.Companion.SensitiveUserDataCallback
    ) {
        RetrofitHelper.launchControlled(
            masterViewModel,
            null,
            null,
            responseCallback = object : RetrofitHelper.Companion.ResponseCallback {
                override fun onError(errorCode: Int) {
                    callback.onError(errorCode)
                }

                override fun onResponse(content: String?) {
                    if (content == null) {
                        callback.onError(Constants.ERROR_UNDEFINED)
                        return
                    }
                    //Send as { user, token }
                    val obj = JSONObject(content)

                    val user: User? = masterViewModel.parseSingle(
                        null,
                        JSONObject(obj.get("user").toString())
                    ) as User?
                    if (user == null) {
                        callback.onError(Constants.ERROR_UNDEFINED)
                        return
                    }
                    //Extract EMAIL and TOKEN
                    val email: String? =
                        JSONObject(obj.get("user").toString()).get("email").toString()
                    val token: String? = obj.get("token").toString()

                    if (token != null) {
                        if (email != null) {
                            callback.onResponse(user, token, email)
                        } else callback.onError(Constants.ERROR_UNDEFINED)
                    } else callback.onError(Constants.ERROR_UNDEFINED)


                }
            },
            codeToExecute = {
                getUserService()?.singUp(SignUpRequest(email, pw, name))
            })

    }

    @MainThread
    fun login(
        email: String,
        pw: String,
        callback: RetrofitHelper.Companion.SensitiveUserDataCallback
    ) {
        RetrofitHelper.launchControlled(
            masterViewModel,
            null,
            null,
            responseCallback = object : RetrofitHelper.Companion.ResponseCallback {
                override fun onError(errorCode: Int) {
                    callback.onError(errorCode)
                }

                override fun onResponse(content: String?) {
                    Log.d("onResponse", "LOGIN EMAIL: CONTENT:  $content")

                    if (content == null) {
                        callback.onError(Constants.ERROR_UNDEFINED)
                        return
                    }
                    //Send as { user, token }
                    val obj = JSONObject(content)

                    val user: User? = masterViewModel.parseSingle(
                        null,
                        JSONObject(obj.get("user").toString())
                    ) as User?
                    if (user == null) {
                        callback.onError(Constants.ERROR_UNDEFINED)
                        return
                    }
                    //Extract EMAIL and TOKEN
                    val email: String? =
                        JSONObject(obj.get("user").toString()).get("email").toString()
                    val token: String? = obj.get("token").toString()

                    Log.d("onResponse", "SIGNUP EMAIL: $email  TOKEN $token CONTENT:  $content")
                    if (token != null) {
                        if (email != null) {
                            callback.onResponse(user, token, email)
                        } else callback.onError(Constants.ERROR_UNDEFINED)
                    } else callback.onError(Constants.ERROR_UNDEFINED)


                }
            },
            codeToExecute = {
                getUserService()?.login(LoginRequest(email.toLowerCase(), pw))
            })

    }

    @MainThread
    fun editUser(
        token: String,
        changes: ArrayList<Pair<String, String>>,
        callback: RetrofitHelper.Companion.DisplayableListCallback
    ) {
        RetrofitHelper.launchControlled(
            masterViewModel,
            null,
            callback,
            codeToExecute = { getUserService()?.editAccount(token, EditRequest(changes)) })

    }

    @MainThread
    fun logoutAll(token: String, callback: RetrofitHelper.Companion.DisplayableListCallback) {
        RetrofitHelper.launchControlled(
            masterViewModel,
            null,
            callback,
            responseCallback = object : RetrofitHelper.Companion.ResponseCallback {
                override fun onResponse(content: String?) {
                    callback.onResponse(ArrayList())

                }

                override fun onError(errorCode: Int) {
                    callback.onError(errorCode)
                }
            },
            codeToExecute = {
                getUserService()?.logoutAll(token)
            },
            parse = false
        )

    }
    @MainThread
    fun logout(token: String, callback: RetrofitHelper.Companion.DisplayableListCallback) {
        RetrofitHelper.launchControlled(
            masterViewModel,
            null,
            callback,
            responseCallback = object : RetrofitHelper.Companion.ResponseCallback {
                override fun onResponse(content: String?) {
                    callback.onResponse(ArrayList())

                }

                override fun onError(errorCode: Int) {
                    callback.onError(errorCode)
                }
            },
            codeToExecute = {
                getUserService()?.logout(token)
            },
            parse = false
        )

    }

    @MainThread
    fun voteQuestion(
        questionID: String?,
        indexOfAnswer: Int,
        token: String? = null,
        callback: RetrofitHelper.Companion.DisplayableListCallback
    ) {

        if (questionID == null) {
            callback.onError(Constants.ERROR_UNDEFINED)
            return
        }
        if (token == null) {
            //TODO do!

            /*    RetrofitHelper.launchControlled(masterViewModel,null,callback, codeToExecute = {
                getUserService()?.voteOpen(VoteRequest(questionID, indexOfAnswer))
            },parse = true,codeToExecuteAfterParse = {
                val user=it[0]
                val cm=ContentModel.convertDisplayable(user)
                if (cm != null) {
                    masterViewModel.database.contentAccessor().update(cm)
                }
                masterViewModel.userVoteNotifier.postValue(Update(questionID,voteon= indexOfAnswer))


            })

        } else {
            RetrofitHelper.launchControlled(masterViewModel,null,callback, codeToExecute = {
                getUserService()?.voteClosed(token,VoteRequest(questionID, indexOfAnswer))
            },parse = true,codeToExecuteAfterParse = {
                val user=it[0]
                val cm=ContentModel.convertDisplayable(user)
                if (cm != null) {
                    masterViewModel.database.contentAccessor().update(cm)
                }
                masterViewModel.userVoteNotifier.postValue(Update(questionID,voteon= indexOfAnswer))


            })
        }*/
        }
    }

    @MainThread
    fun unsubscribeTopic(_id: String, displayableListCallback: RetrofitHelper.Companion.DisplayableListCallback) {
        val token= masterViewModel.localBase.getStoredLogin()?.token
        if (token != null) {
            unsubscribe(token,_id,displayableListCallback,"T")
        }
    }

    @MainThread
    fun subscribeTopic(_id: String, displayableListCallback: RetrofitHelper.Companion.DisplayableListCallback) {
        val token= masterViewModel.localBase.getStoredLogin()?.token
        if (token != null) {
            subscribe(token,_id,displayableListCallback,"T")
        }
    }


    fun isSubscribed(isSubscribedTo: String, loggedInUser: User): Boolean {
        return loggedInUser.subscriptions?.find { it.content==isSubscribedTo }!=null
    }

    fun doesStreamContentExist(): Boolean {
        val token= masterViewModel.localBase.getStoredLogin()?.token ?: return false
       return masterViewModel.doesContentExist(Constants.CONTENT_FEED)

    }
}


