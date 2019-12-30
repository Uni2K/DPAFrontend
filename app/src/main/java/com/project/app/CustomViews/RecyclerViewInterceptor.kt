package com.project.app.CustomViews

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewInterceptor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var lastClickedCoordinates: Point?=null


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                lastClickedCoordinates=Point(event.x.toInt(), event.y.toInt())

            }


        }
       return super.onInterceptTouchEvent(event)

    }


}
