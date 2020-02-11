package com.project.app.Helpers


import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/***
 * Made by Lokesh Desai (Android4Dev)
 */
class GridItemDecoration() : RecyclerView.ItemDecoration() {
   val mSpace=35

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        val spanIndex = lp.spanIndex
        if (spanIndex == 1) {
            outRect.left = mSpace / 2
        } else {
            outRect.right = mSpace / 2
        }

        outRect.bottom=mSpace/2

    }
}