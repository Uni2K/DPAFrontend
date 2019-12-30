package com.project.app.Helpers

import android.app.Application
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.google.gson.Gson
import com.project.app.Bases.*
import com.project.app.Objects.*
import com.project.app.Paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set
import kotlin.collections.sortBy


class MasterViewModel(application: Application) : AndroidViewModel(application) {

    //Misc
    private var retrofit: Retrofit? = null
    val gson = Gson()
    val DISK_IO = Executors.newSingleThreadExecutor()



    //Database
    private val useInMemoryDb = false
    val database = OfflineDatabase.create(application, useInMemoryDb)

    //Bases
    val socketBase: SocketBase = SocketBase(this)
    val localBase: LocalBase = LocalBase(this)
    val userBase: UserBase = UserBase(this)
    val questionBase: QuestionBase = QuestionBase(this)
    val pagingBase: PagingBase = PagingBase(this)
    val topicBase: TopicBase = TopicBase(this)

    //content Management
    val contentStorage = MutableLiveData(HashMap<String, Listing<ContentModel>>())
    val contentStackMap =
        MutableLiveData(HashMap<String, MutableLiveData<ArrayList<Pair<String, String>>>>())
    val contentSyncer: ContentSyncer = ContentSyncer(this)
    private val newContentStore: HashMap<String, MutableLiveData<Pair<Int, QuestionStackInfo>>> = HashMap()
    private val activeContentLoaders:ArrayList<ContentLoader> = ArrayList()


    override fun onCleared() {
        contentSyncer.pause()
        super.onCleared()
    }

    fun getRetrofitClient(): Retrofit? {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            val client = OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .readTimeout(10, TimeUnit.SECONDS).build()

            try {
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.SERVER_BASEURL + ":3000")
                    .client(client)

                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
        return retrofit as Retrofit
    }


    fun insertResultIntoDb(body: ListingData,  it: PagingRequestHelper.Request.Callback?=null) {
       GlobalScope.launch {


           val converted = ContentModel.convertDisplayableList(body.children)
           database.contentAccessor().insert(converted)
           it?.recordSuccess()
           if (body.children.isNotEmpty()) {
               newContentStore[body.children[0].contentname]?.postValue(
                   Pair(
                       body.children.size,
                       QuestionStackInfo(body.children)
                   )
               )
           }
       }
    }


























    @MainThread
    fun updateContentHashmap(contentName_: String, data: Listing<ContentModel>) {
        if (Looper.myLooper() != Looper.getMainLooper()) throw IllegalThreadStateException()
        var hm = contentStorage.value
        if (hm == null) hm = HashMap()
        if (hm[contentName_] == null) {
            hm[contentName_] = data
            contentStorage.value = hm
        }
    }


    @MainThread
    fun updateContentStackMap(contentName_: String, values: Pair<String, String>) {
        if (Looper.myLooper() != Looper.getMainLooper()) throw IllegalThreadStateException()
        var hm = contentStackMap.value
        if (hm == null) hm = HashMap<String, MutableLiveData<ArrayList<Pair<String, String>>>>()
        var stackLiveData =
            MutableLiveData<ArrayList<Pair<String, String>>>(ArrayList<Pair<String, String>>())

        if (hm[contentName_] != null) stackLiveData = hm[contentName_]!!
        val newArray = stackLiveData.value
        newArray?.add(values)
        stackLiveData.value = newArray
        hm[contentName_] = stackLiveData
        contentStackMap.value = hm
        //Log.d("updateContentStackMap","NAME: $contentName_  INSERT: ${values.first} / ${values.second}")
    }


    @MainThread
    fun refresh(contentName_: String, userTrigger: Boolean) {
        this.userBase.userUpdateTrigger.value = userTrigger
        contentStorage.value?.get(contentName_)?.refresh?.invoke()
    }

    @MainThread
    fun retry(contentName_: String) {
        contentStorage.value?.get(contentName_)?.retry?.invoke()
    }

    @MainThread
    fun fetchMore(contentName_: String) {
        this.userBase.userUpdateTrigger.value = (true)
        contentStorage.value?.get(contentName_)?.fetchMore?.invoke()
    }


    @WorkerThread
    fun parseSingle(parsingConfig: ParsingConfig?, it: JSONObject): Displayable? {
        when {
            it.has("user") -> {
                //Nested User
                return parseSingle(parsingConfig, it.getJSONObject("user"))
            }

            it.has("color") -> {
                val topic = gson.fromJson(it.toString(), Topic::class.java)
                topic.type = Displayable.TOPIC
                topic.contentname = parsingConfig?.contentname ?: "-1"
                topic.id = topic._id
                topic.data = topic
                return topic
            }
            it.has("text") -> {
                val question = gson.fromJson(it.toString(), Question::class.java)
                question.type = Displayable.QUESTION
                question.contentname = parsingConfig?.contentname ?: "-1"
                question.id = question._id
                question.data = question
                question.index = question.timestamp ?: "-1"
                return question
            }
            it.has("subscriptions") -> {
                val user = gson.fromJson(it.toString(), User::class.java)
                user.type = Displayable.USER
                user.contentname = parsingConfig?.contentname ?: "-1"
                user.id = user._id
                user.data = user
                return user

            }
            else -> {
                return null
            }

        }
    }

    @WorkerThread
    fun parseContent(
        parsingConfig: ParsingConfig?,
        s: String,
        callback: RetrofitHelper.Companion.DisplayableListCallback
    ) {
        GlobalScope.launch {
            //TODO:REWORK THIS SOME DAY
            val arrayList = ArrayList<Displayable>()
            try {

                val tokener = JSONTokener(s).nextValue()
                if (tokener is JSONObject)
                    parseSingle(parsingConfig, tokener)?.let { arrayList.add(it) }
                else if (tokener is JSONArray) {
                    for (i in 0 until tokener.length()) {
                        val obj: JSONObject? = tokener.getJSONObject(i)
                        obj?.let {
                            parseSingle(parsingConfig, it)?.let { arrayList.add(it) }
                        }
                    }

                }
                arrayList.sortBy { it.index }
                arrayList.let {
                    GlobalScope.launch(Dispatchers.Main) {
                        callback.onResponse(it)
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
                callback.onError(Constants.ERROR_UNDEFINED)


            }
        }
    }

    fun notifyOnNewContentInserted(
        contentName_: String,
        lifecycleOwner: LifecycleOwner,
        observer: Observer<Pair<Int, QuestionStackInfo>>
    ) {
        val mutableLiveData = MutableLiveData<Pair<Int, QuestionStackInfo>>()
        mutableLiveData.observe(lifecycleOwner, observer)
        newContentStore[contentName_] = mutableLiveData
    }

    fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : PagingBase.PagingCallBack {
        return object : PagingBase.PagingCallBack {
            override fun onResponse(result_: ListingData) {
                if (result_.after == null && result_.before == null) it.recordSuccess()  //GET BEFORE CORRECT HANDLING (FÜR TESTZWECKE)
                else
                    insertResultIntoDb(result_, it)
            }
            override fun onFailure() {
                it.recordFailure(Throwable("OHHHHHOHHOHH"))
            }


        }
    }

    fun registerContentLoader(contentLoader: ContentLoader) {
        if(!activeContentLoaders.contains(contentLoader))activeContentLoaders.add(contentLoader)
    }

    fun unregisterContentLoader(contentLoader: ContentLoader) {
      activeContentLoaders.remove(contentLoader)
    }

    fun getActiveContentByNames(): List<String> {
        return activeContentLoaders.map { it.contentName }.filter { it != "-1" }
    }

    fun doesContentExist(content: String): Boolean {
        return !database.contentAccessor().doesContentExist(content).isNullOrBlank()
    }

}