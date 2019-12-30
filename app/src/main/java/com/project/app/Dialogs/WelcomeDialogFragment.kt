package com.project.app.Dialogs

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
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.WelcomeAdapter
import com.project.app.R


class WelcomeDialogFragment : DialogFragment() {


    lateinit var parent: HomeActivity
    lateinit var next: FloatingActionButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleSearch)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_welcome, container, false)
        val recyclerView =  v.findViewById<RecyclerView>(R.id.welcome_recycler)
        recyclerView.layoutManager=LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        recyclerView.adapter=WelcomeAdapter(this)
        val snapHelper = LinearSnapHelper()
      //  snapHelper.attachToRecyclerView(recyclerView)
        return v
    }


}