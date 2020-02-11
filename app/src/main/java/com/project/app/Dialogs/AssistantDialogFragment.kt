package com.project.app.Dialogs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.AssistantFeatureAdapter
import com.project.app.Adapters.WelcomeAdapter
import com.project.app.Helpers.GridItemDecoration
import com.project.app.R


class AssistantDialogFragment : DialogFragment() {


    lateinit var parent: HomeActivity
    lateinit var next: FloatingActionButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            parent = context as HomeActivity


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleSearch)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_assistant, container, false)
        val recyclerView =  v.findViewById<RecyclerView>(R.id.assistant_recycler)
        recyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL)
        recyclerView.addItemDecoration(GridItemDecoration())

        v.findViewById<View>(R.id.back).setOnClickListener { dismiss() }
        recyclerView.adapter= AssistantFeatureAdapter(this)


        return v
    }


}