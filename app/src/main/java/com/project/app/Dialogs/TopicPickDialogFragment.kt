package com.project.app.Dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.TopicChipAdapter
import com.project.app.Fragments.TopicPickFragment
import com.project.app.Interfaces.TopicControllerInterface
import com.project.app.Objects.Topic
import com.project.app.R

class TopicPickDialogFragment : DialogFragment() {
    private var topicControllerInterface: TopicControllerInterface?=null
    lateinit var parent: HomeActivity
    val tagFragment = TopicPickFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleTransparent)
    }

    fun setCallback(topicControllerInterface: TopicControllerInterface){
        this.topicControllerInterface=topicControllerInterface

    }
    public fun getFragment():TopicPickFragment?{
        return childFragmentManager.findFragmentByTag("topic") as? TopicPickFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_tagdialog, container, false)

        v.setOnClickListener {
            dismiss()
        }

        val bundle = Bundle()
        bundle.putInt("topicLimit",10000)

        topicControllerInterface?.let { tagFragment.setCallBack(it) }
        if (arguments != null) {
            tagFragment.arguments = arguments

        }else{
            tagFragment.arguments = bundle

        }

        childFragmentManager.beginTransaction().replace(R.id.container, tagFragment,"topic").commit()





        return v
    }

    fun setSelection(list: ArrayList<Topic>) {
        tagFragment.setSelection(list)
    }


}