package com.project.app.Dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.app.Activities.HomeActivity
import com.project.app.Objects.Question
import com.project.app.R

class TestDialogFragment : DialogFragment() {


    lateinit var parent: HomeActivity
    lateinit var viewPager: ViewPager
    lateinit var next: FloatingActionButton
    lateinit var prev: FloatingActionButton
    lateinit var bottomBar: Group
    var question: MutableLiveData<Question> = MutableLiveData<Question>()


    fun setActivity(HomeActivity: HomeActivity) {
        this.parent = HomeActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleCreate)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.test, container, false)

        return v
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }
}