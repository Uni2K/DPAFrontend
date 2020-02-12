package com.project.app.Bases

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat.getSystemService
import com.airbnb.lottie.LottieAnimationView
import com.project.app.R
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip


class AssistantBase {
    var lottieAnimationView:LottieAnimationView?=null
    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()




    public fun setUpWithView(view: LottieAnimationView){
        this.lottieAnimationView=view
    }


    public fun createBigText(context: Context, parentview: View ){
        parentview.post {

            // inflate the layout of the popup window
            // inflate the layout of the popup window
            val popupView: View =
                LayoutInflater.from(context).inflate(R.layout.snack_assistant_big, null)

            // create the popup window
            // create the popup window
            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // lets taps outside the popup also dismiss it

            val popupWindow = PopupWindow(popupView, width, height, focusable)
            popupView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));




            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken


            val heightOfPopup= popupView.measuredHeight

            popupWindow.showAtLocation(parentview, Gravity.BOTTOM ,0 ,heightOfPopup-(56+20).dpToPx(popupView.resources.displayMetrics))
           // popupWindow.showAsDropDown(parentview)

            // dismiss the popup window when touched
            // dismiss the popup window when touched
            popupView.setOnTouchListener { v, event ->
                popupWindow.dismiss()
                true
            }
        }
    }



   public fun createInfoText(parentview: View) {
       parentview.post {
           lottieAnimationView?.let {

               val tooltip = Tooltip.Builder(it.context)
                   .anchor(it, 0, 0)
                   .text("There are 10 new Questions")
                   .styleId(R.style.ToolTipAssistant)

                   .arrow(true)
                   .floatingAnimation(Tooltip.Animation.SLOW)
                   .closePolicy(ClosePolicy.TOUCH_INSIDE_CONSUME)
                   .showDuration(100000)
                   .fadeDuration(300)
                   .overlay(false)
                   .create()

               tooltip
                   .doOnHidden { }
                   .doOnFailure { }
                   .doOnShown { }
                   .show(parentview, Tooltip.Gravity.BOTTOM, true)
           }
       }


   }

}

