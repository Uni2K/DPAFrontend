package com.project.app.Bases

import android.annotation.SuppressLint
import android.util.Log
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.gson.reflect.TypeToken
import com.project.app.Helpers.Constants
import com.project.app.Helpers.MasterViewModel
import com.project.app.Objects.StoredUser
import com.project.app.Objects.Topic
import com.project.app.Objects.Update
import com.project.app.Objects.User


class LocalBase(var viewModel: MasterViewModel) {


    var storedVotes: HashMap<String, Update> = HashMap()
    private val voteKEY = "voteMap"
    private val autoCompleteKEY = "autocomplete"
    private val loginKEY = "login"





    fun addVote( update: Update) {
        Log.e("addVOte","YES: ${update.questionid}")
        var x = getHashMap( voteKEY)
        if (x == null) {
            saveHashMap( voteKEY, HashMap<String, Update>(), true)
        } else {
            storedVotes = x
        }
        if (getVote(update.questionid) == null)
            storedVotes[update.questionid] = update
        saveHashMap( voteKEY, storedVotes)
    }

    fun getVote(questionid: String?): Update? {
        if(questionid==null)return null
        var x = getHashMap( voteKEY)
        if (x == null) {
            saveHashMap( voteKEY, HashMap<String, Update>(), true)
        } else {
            storedVotes = x
        }
        return storedVotes[questionid]

    }

    private fun saveHashMap( key: String, obj: Any, now: Boolean = false) {
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        val editor = prefs.edit()
        val json = viewModel.gson.toJson(obj)
        editor.putString(key, json)
        if (now) editor.commit()
        else editor.apply()
    }


    fun getHashMap( key: String): HashMap<String, Update>? {
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        val json = prefs.getString(key, "")
        val type = object : TypeToken<HashMap<String, Update>>() {

        }.type
        return viewModel.gson.fromJson<Any>(json, type) as HashMap<String, Update>?
    }



    fun getHistoryTags(): ArrayList<String> {
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        var json = prefs.getString(Constants.SAVE_HISTORY_TAG, "")
        val type = object : TypeToken<ArrayList<String>>() {
        }.type

        return viewModel.gson.fromJson<Any>(json, type) as ArrayList<String>? ?: ArrayList()
    }




    fun updateVoteStore(array: ArrayList<Update>) {
        val content: HashMap<String, Update> = HashMap(array.associateBy({ it.questionid }, { it }))
        saveHashMap(voteKEY, content)
        storedVotes = content

    }

    fun addTextToAutoCompleteHistory(s: String) {
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        var set = prefs.getStringSet(autoCompleteKEY, HashSet<String>())
        set?.let {
            set.add(s)
            if (set.size > 10) {
                set.drop(0)
            }
            prefs.edit().putStringSet(autoCompleteKEY, set).apply()
        }
    }

    fun getAutoCompleteHistory(): MutableSet<String> {
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        var set = prefs.getStringSet(autoCompleteKEY, HashSet<String>())
        return set ?: HashSet<String>()

    }

    fun addTagToHistory(subTopic: Topic) {
        val arr = getHistoryTags()
        if (!arr.contains(subTopic._id)) {
            arr.add(subTopic._id)
        }
        saveHashMap(
            Constants.SAVE_HISTORY_TAG, arr, false
        )
    }







    /**
     * To be stored only:
     * 1. Token
     * 2. Avatar -> for fast display
     * 3. UserID
     * 4. Name -> fast Display
     * Theoretically fat display could be achieved with only the userid and an additional server call
     */
    @SuppressLint("ApplySharedPref")
    fun addStoredLogin(user: User, token:String, email:String) {
        val storedUser= StoredUser(user._id,token,user.name,user.avatar, email)
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        prefs.edit().putString(loginKEY, viewModel.gson.toJson(storedUser)).commit()
    }
    @SuppressLint("ApplySharedPref")
    private fun addStoredLogin(storedUser: StoredUser) {
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        prefs.edit().putString(loginKEY, viewModel.gson.toJson(storedUser)).commit()
    }
    fun isUserLoggedIn(): Boolean {
        return getStoredLogin() != null
    }
    /**
     * Token got checked wrong on the server -> remove it and keep the rest for fast display
     */
    fun removeTokenFromStoredLogin() {
       val storedUser:StoredUser?=getStoredLogin()
        if (storedUser != null) {
            storedUser.token=null
            addStoredLogin(storedUser)
        }
    }
    fun removeStoredLogin() {
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        prefs.edit().putString(loginKEY, "").apply()
    }
    fun getStoredLogin(): StoredUser? {
        val prefs = getDefaultSharedPreferences(viewModel.getApplication())
        val json = prefs.getString(loginKEY, null)

        if (json.isNullOrEmpty()) return null
        val type = object : TypeToken<StoredUser>() {
        }.type
        return try {
            val user = viewModel.gson.fromJson<Any>(json, type) as StoredUser?
            user
        } catch (e: Exception) {
            removeStoredLogin()
            null
        }

    }
}