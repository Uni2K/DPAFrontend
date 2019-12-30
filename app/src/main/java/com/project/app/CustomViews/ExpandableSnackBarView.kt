package com.project.app.CustomViews

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.ContentViewCallback
import com.project.app.Objects.ErrorC
import com.project.app.R


class ExpandableSnackBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {
    override fun animateContentOut(delay: Int, duration: Int) {
    }

    override fun animateContentIn(delay: Int, duration: Int) {
    }

    private val header: TextView
    private val hint: TextView
    private val action0: TextView
    private val action1: TextView
    private val action2: TextView
    private val action3: TextView

    private var errorC: ErrorC? = null
    private var parent: ExpandableSnackBar? = null
    fun setError(previousErrorC: ErrorC) {
        Log.e("setERROR", "DATA: ${previousErrorC.hint} +   ${previousErrorC.text}")
        header.text = previousErrorC.text
        hint.text = previousErrorC.hint
        errorC = previousErrorC

        if (!previousErrorC.retryPossible) {
            action1.visibility = View.GONE
        } else action2.visibility = View.VISIBLE
    }

    fun setParent(b: ExpandableSnackBar) {
        parent = b
    }

    init {
        val v = View.inflate(context, R.layout.snackbar_expandable_view, this)
        this.header = v.findViewById(R.id.header)
        this.hint = v.findViewById(R.id.subheader)
        this.action1 = v.findViewById(R.id.action1)
        this.action2 = v.findViewById(R.id.action2)
        this.action0 = v.findViewById(R.id.action)
        this.action3 = v.findViewById(R.id.action3)

        val lt = LayoutTransition()
        lt.enableTransitionType(LayoutTransition.CHANGING)
        layoutTransition = lt
        action0.setOnClickListener {
            //TOGGLE
            action0.visibility = View.GONE
            action2.visibility = View.VISIBLE
            action3.visibility = View.VISIBLE
            hint.visibility = View.VISIBLE
            header.setTypeface(null, Typeface.BOLD)
            if (errorC?.retryPossible == true) action1.visibility = View.VISIBLE
        }

        action3.setOnClickListener {
            action0.visibility = View.VISIBLE
            action2.visibility = View.GONE
            action3.visibility = View.GONE
            hint.visibility = View.GONE
            header.setTypeface(null, Typeface.NORMAL)
            action1.visibility = View.GONE
        }



        action0.visibility = View.VISIBLE
        action2.visibility = View.GONE
        action3.visibility = View.GONE
        hint.visibility = View.GONE
        header.setTypeface(null, Typeface.NORMAL)
        action1.visibility = View.GONE

        action1.setOnClickListener {
            parent?.action1()

        }
        action2.setOnClickListener {
            parent?.action2()
        }

    }
}