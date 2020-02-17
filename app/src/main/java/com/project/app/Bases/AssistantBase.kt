package com.project.app.Bases

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.airbnb.lottie.LottieAnimationView
import com.project.app.Activities.HomeActivity
import com.project.app.CustomViews.ContentDisplay
import com.project.app.CustomViews.ExpandableSnackBar
import com.project.app.Helpers.ErrorHandler
import com.project.app.Objects.ErrorC
import com.project.app.Objects.QuestionStackInfo
import com.project.app.R
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import java.lang.Exception


/**
 * Parent of ErrorHandler and Notificationhandler
 */
class AssistantBase(
   val parentActivity: HomeActivity,
   val lifeCycleOwner: LifecycleOwner,
    val parentView: View
) {


    //Manages Errors in the app and used methods from this assistant
    var errorHandler: ErrorHandler? = ErrorHandler(parentActivity,lifeCycleOwner,this)

    //Views
    var lottieAnimationView: LottieAnimationView? = null
    var snackBar: FrameLayout? = null
    var bnvExpander:View?=null
    val DIMEN_TRANSY_HIDDEN: Float = 112.dpToPx(parentActivity.resources.displayMetrics).toFloat()


    //Animators
    private lateinit var snackbarShowAnimator: Animator
    private lateinit var snackbarHideAnimator: Animator

    //config
    var hasBNV=true //If not-> Other margin
    var hasButton=true //If has a opener button, if not no info texts

    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()


    fun setUpWithView(view: LottieAnimationView) {
        this.lottieAnimationView = view
    }


    fun setUpAssistantSnackbar(parent: FrameLayout) {
        this.snackBar = parent
        this.bnvExpander=parent.findViewById(R.id.bnvExpander)
        snackBar?.translationY=DIMEN_TRANSY_HIDDEN
        snackBar?.visibility=View.VISIBLE

    }


    private fun initAnimators() {
        snackBar?.let { snackBar ->

            snackbarHideAnimator = ValueAnimator.ofFloat(
                snackBar.translationY,
                DIMEN_TRANSY_HIDDEN
            )
            (snackbarHideAnimator as ValueAnimator?)?.addUpdateListener { it ->
                snackBar.translationY = it.animatedValue.toString().toFloat()

            }
            (snackbarHideAnimator as ValueAnimator?)?.duration = 500
            (snackbarHideAnimator as ValueAnimator?)?.startDelay = 0
            (snackbarHideAnimator as ValueAnimator?)?.addListener(object :
                Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                 //   snackBar.visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
            snackbarShowAnimator = ValueAnimator.ofFloat(
                snackBar.translationY,
                0f
            )
            (snackbarShowAnimator as ValueAnimator?)?.addUpdateListener { it ->
                snackBar.translationY = it.animatedValue.toString().toFloat()

            }
            (snackbarShowAnimator as ValueAnimator?)?.duration = 500
            (snackbarShowAnimator as ValueAnimator?)?.startDelay = 0
            (snackbarShowAnimator as ValueAnimator?)?.addListener(object :
                Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                   // snackBar.visibility = View.VISIBLE
                }
            })


        }?: let { throw Exception("SNACKBAR NOT SET") }

    }







    private fun createInfoText() {
        parentView.post {
            lottieAnimationView?.let {

                val tooltip = Tooltip.Builder(it.context)
                    .anchor(it, 0, 0)
                    .text("There are 10 new Questions")
                    .styleId(R.style.ToolTipAssistant)

                    .arrow(true)
                    .floatingAnimation(Tooltip.Animation.SLOW)
                    .customView(R.layout.update_notifier,R.id.not_text)
                    .closePolicy(ClosePolicy.TOUCH_INSIDE_CONSUME)
                    .showDuration(100000)
                    .fadeDuration(300)
                    .overlay(false)
                    .create()

                tooltip
                    .doOnHidden { }
                    .doOnFailure { }
                    .doOnShown { }
                    .show( parentView, Tooltip.Gravity.BOTTOM, false)
            }
        }


    }

    fun snackbarVisible(): Boolean {
        return snackBar?.translationY == 0f
    }

    fun showNewContentNotifier(it: Pair<Int, QuestionStackInfo>?) {
       if(hasButton)
        createInfoText()
    }

    fun hideErrorMessage() {
        hideBottomSnackbar()

    }

    private fun hideBottomSnackbar() {
        if (snackbarShowAnimator != null) {
            snackbarShowAnimator.cancel()
            initAnimators()
        }
        if (!snackbarHideAnimator.isRunning) {
            snackbarHideAnimator.start()
        }
    }



    fun showErrorMessage(err: ErrorC) {
        configSnackbars(err)
        showBottomSnackbar()

    }

    private fun showBottomSnackbar() {
        if ( ::snackbarHideAnimator.isInitialized && snackbarHideAnimator.isRunning) {
            snackbarHideAnimator.cancel()

        }
        initAnimators()

        if (!snackbarShowAnimator.isRunning) {
            snackbarShowAnimator.start()
        }
    }



    /**
     * Set Error Content to Snackbars
     */
    private fun configSnackbars(err: ErrorC) {

    if(hasBNV){
        bnvExpander?.visibility=View.VISIBLE
    }else  bnvExpander?.visibility=View.GONE



    }
}

