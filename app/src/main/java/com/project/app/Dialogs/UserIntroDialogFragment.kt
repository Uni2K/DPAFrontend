package com.project.app.Dialogs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
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


class UserIntroDialogFragment : DialogFragment() {


    lateinit var parent: HomeActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleLogin)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            parent = context as HomeActivity


        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_intro_user, container, false)
        v.findViewById<View>(R.id.back).setOnClickListener { dismiss() }

        v.findViewById<View>(R.id.start).setOnClickListener {
            if(v.findViewById<Switch>(R.id.switch1).isChecked){
                val sp=parent.getSharedPreferences("Settings",0)
                sp.edit().putBoolean("user_TOS",true).apply()

                val frag = LoggedInUserDialogFragment()
                frag.show(parent.supportFragmentManager, "account")
                dismiss()

            }else{
                Toast.makeText(context,"Enable the terms of usage, please!",Toast.LENGTH_LONG).show()
            }
        }

        return v
    }


}