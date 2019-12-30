package com.project.app.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Activities.HomeActivity
import com.project.app.Helpers.Constants
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.ContentLoader
import com.project.app.R


class MainContentFragment : QuestionControllerFragment() {
    override fun userClicked(userid: String) {
        if(::parent.isInitialized){
            parent.openAccount(userid)
        }
    }
    lateinit var recycler_switch: RecyclerView
    lateinit var parent: HomeActivity
    lateinit var qmodel: MasterViewModel
    lateinit var contentLoader: ContentLoader

    var contentName: String = Constants.CONTENT_NOTHING



    fun setContent(contentName: String){
        contentLoader.enqueueContent(contentName)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it["initialContent"] != null) {
                contentName = it.getString("initialContent", Constants.CONTENT_FEED)
           if(::contentLoader.isInitialized)     contentLoader.enqueueContent(contentName)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            parent = context as HomeActivity
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_stream, null)

        qmodel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        recycler_switch = view.findViewById(R.id.cd_content)
        recycler_switch.layoutManager = LinearLayoutManager(context)
        contentLoader =
            ContentLoader(
                this,
                activity,
                view
            )
        if(contentName!=Constants.CONTENT_NOTHING)contentLoader.enqueueContent(contentName)
        return view
    }




}