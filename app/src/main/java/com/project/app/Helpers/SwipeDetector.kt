package com.project.app.Helpers

import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

open class SwipeDetector(val swipeHelper: SwipeHelper) : View.OnTouchListener {

    var lastEventX: Float = 0f
    var lastEventY: Float = 0f
    var distance: Float = 0f
    var minThreshold = 100f //Snaps back to idle
    var maxThreshold = 100000f //Snaps to dismiss

    interface SwipeHelper {
        fun onMove(distance: Float)
        fun onStop( isMax: Boolean, largerThanMin:Boolean, negativeDistance:Boolean)
        fun onClick()
        fun onDown()
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        Log.e("onTouch","DATA "+p1?.action)
        when (p1?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastEventX = p1.rawX
                lastEventY = p1.rawY
                    swipeHelper.onDown()
                return true
            }
            MotionEvent.ACTION_UP -> {
            if(abs(lastEventX-p1.rawX) <10 && abs(lastEventY-p1.rawY) <10){
                swipeHelper.onClick()
                return true
            }

            swipeHelper.onStop(Math.abs(distance)==maxThreshold, Math.abs(distance)>minThreshold, distance<0)
                return true

            }
            MotionEvent.ACTION_CANCEL -> {
                swipeHelper.onStop(Math.abs(distance)==maxThreshold, Math.abs(distance)>minThreshold, distance<0)
                return true

            }



            MotionEvent.ACTION_MOVE -> {

                if ((Math.abs(lastEventX - p1.rawX)) <maxThreshold) {
                    distance = -(lastEventX - p1.rawX)
                }
                swipeHelper.onMove(distance)
                return true

            }


        }



        return false
    }


}