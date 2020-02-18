package com.project.app.CustomViews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.project.app.Objects.ErrorC
import com.project.app.R

class ExpandableSnackBar(
    parent: ViewGroup,
    val content: ExpandableSnackBarView
) : BaseTransientBottomBar<ExpandableSnackBar>(parent, content, content) {
    fun setError(previousErrorC: ErrorC) {
        this.content.setError(previousErrorC)

    }

    fun action1() {

    }

    fun action2() {
        dismiss()
    }


    companion object {

        fun make(view: View):ExpandableSnackBar {
            // First we find a suitable parent for our custom view
         //   val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
           //     "No suitable parent found from the given view. Please provide a valid view."
            //)
                val parent:ViewGroup= view as ViewGroup
            // We inflate our custom view
            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.snackbar_expandable,
                parent,
                false
            ) as ExpandableSnackBarView
            val b=ExpandableSnackBar(
                parent,
                customView)
            customView.setParent(b)

            // We create and return our Snackbar
            return b
        }
         private fun View?.findSuitableParent(): ViewGroup? {
            var view = this
            var fallback: ViewGroup? = null
            do {
                if (view is CoordinatorLayout) {
                    // We've found a CoordinatorLayout, use it
                    return view
                } else if (view is FrameLayout) {
                    if (view.id == android.R.id.content) {
                        // If we've hit the decor content view, then we didn't find a CoL in the
                        // hierarchy, so use it.
                        return view
                    } else {
                        // It's not the content view but we'll use it as our fallback
                        fallback = view
                    }
                }

                if (view != null) {
                    // Else, we will loop and crawl up the view hierarchy and try to find a parent
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)

            // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
            return fallback
        }
    }

}