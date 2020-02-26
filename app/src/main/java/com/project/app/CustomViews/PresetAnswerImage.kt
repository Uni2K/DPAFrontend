package com.project.app.CustomViews

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.project.app.R
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask


class PresetAnswerImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Answer(context, attrs, defStyleAttr) {

    private var stillTouched: Boolean=false
    var animatorUp:ValueAnimator?=null
    var timer=Timer()
    init {
        inflate(context, R.layout.preset_answer_image, this)


        findViewById<View>(R.id.toucher).setOnTouchListener { v, event ->

            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    animationDown()
                }
                MotionEvent.ACTION_UP->{
                    animationUp()

                }
                MotionEvent.ACTION_MOVE->{
                    stillTouched=true

                }

            }

            false
        }

        findViewById<View>(R.id.toucher).setOnClickListener { 
            submitAnswer()
            setSelectedMode()

        }
        findViewById<ImageView>(R.id.check).visibility=View.GONE
        
        
    }

    /**
     * Move back after 1s of not touching
     */
    private fun animationDown() {
        scaleX=0.9f
        scaleY=0.9f

        timer.cancel()
        timer.purge()
        timer=Timer()
        timer.schedule(300){
            if (stillTouched) {
                stillTouched = false
                Handler(Looper.getMainLooper()).post{
              if(scaleX!=1f){
                  animationDown()
              }
                }
            } else {

             Handler(Looper.getMainLooper()).post{
                 if(animatorUp?.isRunning != true) animationUp()
             }



            }
        }






    }

    private fun animationUp() {
        if(animatorUp==null){
           animatorUp = ValueAnimator.ofFloat(scaleX, 1f)
            animatorUp?.duration=300
            animatorUp?.addUpdateListener {
                scaleX=it.animatedValue.toString().toFloat()
                scaleY=it.animatedValue.toString().toFloat()
            }

        }
        animatorUp!!.start()


    }
}





