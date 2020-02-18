package com.project.app.Bases

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.appbar.AppBarLayout
import com.project.app.Activities.HomeActivity
import com.project.app.Helpers.ErrorHandler
import com.project.app.Objects.ErrorC
import com.project.app.Objects.QuestionStackInfo
import com.project.app.R
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import java.lang.Exception
import kotlin.math.max


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
    var anchor: LottieAnimationView? = null
    var bottomBar: FrameLayout? = null
    var topBar:FrameLayout?=null
    var topAppBarLayout:AppBarLayout?=null

    var bnvExpander:View?=null
    val DIMEN_TRANSY_HIDDEN_BOTTOM: Float = 112.dpToPx(parentActivity.resources.displayMetrics).toFloat()
    val DIMEN_TRANSY_HIDDEN_TOP: Float = -112.dpToPx(parentActivity.resources.displayMetrics).toFloat()


    //Animators
    private lateinit var snackbarShowAnimator: Animator
    private lateinit var snackbarHideAnimator: Animator

    //config
    var hasBNV=true //If not-> Other margin
    var hasAnchor=true //If has a opener button, if not no info texts
    var hasMovableTopAppBar=true


    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()
    fun Int.pxToDp(displayMetrics: DisplayMetrics): Int = (this / displayMetrics.density).toInt()


    fun setUpWithView(view: LottieAnimationView) {
        this.anchor = view
    }


    fun registerBottombar(parent: FrameLayout) {
        this.bottomBar = parent
        this.bnvExpander=parent.findViewById(R.id.bnvExpander)
        bottomBar?.translationY=DIMEN_TRANSY_HIDDEN_BOTTOM

    }
    fun registerAppBarLayout(appBarLayout: AppBarLayout){
        this.topAppBarLayout=appBarLayout
        initTopAppBarBehaviour()
    }

    private fun initTopAppBarBehaviour() {

        topAppBarLayout?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if(hasMovableTopAppBar){
                val distanceToMove= max(verticalOffset, (-32).dpToPx(parentActivity.resources.displayMetrics))
                val minTranslationY:Float= (-56).dpToPx(parentActivity.resources.displayMetrics).toFloat()
                val percentage=1.0-(verticalOffset/minTranslationY)

                topBar?.translationY= distanceToMove.toFloat()
                val triangle=topBar?.findViewById<View>(R.id.triangle)
               triangle?.scaleY= percentage.toFloat()
               triangle?.pivotY= triangle?.height?.toFloat()?:0f
            }
        })
    }

    fun registerTopbar(parent: FrameLayout) {
        this.topBar = parent
        initTopbarBehaviour()
    }

    private fun initTopbarBehaviour() {

        topBar?.setOnClickListener {

        Log.d("onClicked","DDD")
        }

    }


    private fun initAnimators() {
       bottomBar?.let { snackBar ->

            snackbarHideAnimator = ValueAnimator.ofFloat(
                snackBar.translationY,
                DIMEN_TRANSY_HIDDEN_BOTTOM
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
                   snackBar.visibility = View.INVISIBLE
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
                    snackBar.visibility = View.VISIBLE
                }
            })


        }?: let { throw Exception("SNACKBAR NOT SET") }

    }







    private fun createInfoText() {
       anchor?.post {
            anchor?.let {

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

    fun bottomBarVisible(): Boolean {
        return bottomBar?.visibility == View.VISIBLE
    }

    fun showNewContentNotifier(it: Pair<Int, QuestionStackInfo>?) {
        configTopBar(it)
        showTopBar()
    }

    private fun configTopBar(it: Pair<Int, QuestionStackInfo>?) {
        topBar?.let {topBar->
            val text = topBar.findViewById<TextView>(R.id.not_text)
            text.text = "${it?.first} new Polls added!"
        }
    }

    fun hideErrorMessage() {
        hideBottomBar()

    }

    private fun hideBottomBar() {
        if ( ::snackbarShowAnimator.isInitialized&& snackbarShowAnimator != null) {
            snackbarShowAnimator.cancel()
            initAnimators()
        }
        if (!snackbarHideAnimator.isRunning) {
            snackbarHideAnimator.start()
        }
    }



    fun showErrorMessage(err: ErrorC) {
        configBottomBar(err)
        showBottomBar()

    }

     fun showTopBar() {
        topBar?.visibility = View.INVISIBLE
        val triangle=topBar?.findViewById<View>(R.id.triangle)

        val cx:Double = topBar?.height?.toDouble() ?:0.0
        val cy:Double =topBar?.width?.toDouble() ?:0.0

        val finalRadius = Math.hypot(cx, cy).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(topBar,
            triangle?.x?.toInt()?:0, triangle?.x?.toInt()?:0, 0f, finalRadius)
         anim.duration=900
        // make the view visible and start the animation
        topBar?.visibility = View.VISIBLE
        anim.start()


    }
     fun hideTopBar() {
        val triangle=topBar?.findViewById<View>(R.id.triangle)

         val cx:Double = topBar?.height?.toDouble() ?:0.0
         val cy:Double =topBar?.width?.toDouble() ?:0.0

        val finalRadius = Math.hypot(cx, cy).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(topBar, triangle?.x?.toInt()?:0, triangle?.x?.toInt()?:0,  finalRadius,0f)
        anim.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                topBar?.visibility = View.INVISIBLE
            }
        })
         anim.duration=900

        anim.start()


    }

     fun topBarVisible() :Boolean{
        return topBar?.visibility==View.VISIBLE
    }



    private fun showBottomBar() {
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
    private fun configBottomBar(err: ErrorC) {

    if(hasBNV){
        bnvExpander?.visibility=View.VISIBLE
    }else  bnvExpander?.visibility=View.GONE

        bottomBar?.let {bottomBar->
            val textView: TextView = bottomBar.findViewById(R.id.text)
            val close: ImageView =bottomBar.findViewById(R.id.close)
            val buttonLeft: TextView = bottomBar.findViewById(R.id.buttonLeft)

            textView.setText(err.text)
            close.setOnClickListener { hideErrorMessage() }
            err.hint?.let {
                buttonLeft.visibility=View.VISIBLE
                buttonLeft.setText(it)
            }?:let { buttonLeft.visibility=View.GONE }

            buttonLeft.setOnClickListener { err.solution.invoke() }

        }

    }
}

