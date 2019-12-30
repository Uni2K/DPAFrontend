package com.project.app.Helpers

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.project.app.Activities.HomeActivity
import com.project.app.Objects.QuestionStackInfo
import com.project.app.R


/**
 * Helper Class to manage the updater in HomeActivity
 * No Update code in here-> provided via callback
 */
class NotificationHandler(
    val contentLoader: ContentLoader,
    val parent: HomeActivity,
    private val lifecycleOwner: LifecycleOwner,
    val parentView:ViewGroup

    ) {


    private lateinit var notifierShowAnimator: Animator
    private lateinit var notifierHideAnimator: Animator
    private lateinit var notifierPinAnimator: Animator
    private lateinit var notifierHideSideAnimator: Animator
    lateinit var notifierItems: ConstraintLayout
    private lateinit var notifierText: TextView
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView

    private val DIMEN_TRANSY_HIDDEN:Float=-112.dpToPx(parent.resources.displayMetrics).toFloat()
    private val DIMEN_TRANSY_SHOW:Float=10.dpToPx(parent.resources.displayMetrics).toFloat()


    var viewModel: MasterViewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)

    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

    init {
        setUpNotifier()
       initializeShowAnimators()
       initializePinAnimators()
        initializeHideAnimators()
        initializeMoveAnimator(0f, false, 0)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setUpNotifier() {
        notifierItems = parentView.findViewById(R.id.not_parent)
        notifierText = parentView.findViewById(R.id.not_text)
        image1 = parentView.findViewById(R.id.not_ic1)
        image2 = parentView.findViewById(R.id.not_ic2)
        notifierItems.translationY = DIMEN_TRANSY_HIDDEN
        image1.visibility = View.GONE
        image2.visibility = View.GONE
       // notifierItems.visibility = View.GONE

        notifierItems.setOnTouchListener(object : SwipeDetector(object : SwipeHelper {
            override fun onClick() {
                contentLoader.notificationClicked()
                hideNotifier(0)
            }

            override fun onDown() {
            }

            override fun onMove(distance: Float) {
                notifierItems.translationX = distance
            }

            override fun onStop(isMax: Boolean, largerThanMin: Boolean, negative: Boolean) {

                if (!largerThanMin) {
                    //Snap to IDLE
                    moveNotifierSide(200, 0f, false)
                } else {

                    if (negative) moveNotifierSide(
                        200,
                        (-((parent.resources.displayMetrics.widthPixels))).toFloat(),
                        true
                    )
                    else moveNotifierSide(
                        200,
                        (((parent.resources.displayMetrics.widthPixels))).toFloat(),
                        true
                    )

                }


            }


        }) {})






    }


    private fun animationRunning(): Boolean {
        return notifierHideSideAnimator.isRunning || notifierShowAnimator.isRunning || notifierPinAnimator.isRunning || notifierHideSideAnimator.isRunning
    }

    private fun cancelAllAnimations() {
        if (::notifierShowAnimator.isInitialized) notifierShowAnimator.cancel()
        if (::notifierPinAnimator.isInitialized) notifierPinAnimator.cancel()
        if (::notifierHideAnimator.isInitialized) notifierHideAnimator.cancel()
        if (::notifierHideSideAnimator.isInitialized) notifierHideSideAnimator.cancel()

    }

    private fun initializeHideAnimators() {

        notifierHideAnimator = ValueAnimator.ofFloat(
            notifierItems.translationY,
            DIMEN_TRANSY_HIDDEN
        )
        (notifierHideAnimator as ValueAnimator?)?.addUpdateListener { it ->
            notifierItems.translationY = it.animatedValue.toString().toFloat()

        }
        (notifierHideAnimator as ValueAnimator?)?.duration = 500
        (notifierHideAnimator as ValueAnimator?)?.startDelay = 0
        (notifierHideAnimator as ValueAnimator?)?.addListener(object :
            Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {


            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })

    }

    private fun initializePinAnimators() {

        notifierPinAnimator = ValueAnimator.ofFloat(
            notifierItems.translationY,DIMEN_TRANSY_SHOW
        )
        (notifierPinAnimator as ValueAnimator?)?.addUpdateListener { it ->

            notifierItems.translationY = it.animatedValue.toString().toFloat()
        }
        (notifierPinAnimator as ValueAnimator?)?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {

            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
        (notifierPinAnimator as ValueAnimator?)?.duration = 500
        (notifierPinAnimator as ValueAnimator?)?.startDelay = 0

    }

    private fun initializeMoveAnimator(distance: Float, hide: Boolean, delay: Int) {
        notifierHideSideAnimator = ValueAnimator.ofFloat(
            notifierItems.translationX,
            distance
        )
        (notifierHideSideAnimator as ValueAnimator?)?.addUpdateListener { it ->
            notifierItems.translationX = it.animatedValue.toString().toFloat()
        }
        (notifierHideSideAnimator as ValueAnimator?)?.duration = 500
        (notifierHideSideAnimator as ValueAnimator?)?.startDelay = delay.toLong()
        (notifierHideSideAnimator as ValueAnimator?)?.addListener(object :
            Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                if (hide) notifierItems.translationY = DIMEN_TRANSY_HIDDEN
                if (hide) notifierItems.translationX = 0f

            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {

            }
        })

    }


    private fun initializeShowAnimators() {
        notifierShowAnimator = ValueAnimator.ofFloat(
            notifierItems.translationY,
            DIMEN_TRANSY_SHOW
        )
        (notifierShowAnimator as ValueAnimator?)?.addUpdateListener { it ->
            notifierItems.translationY = it.animatedValue.toString().toFloat()
        }
        (notifierShowAnimator as ValueAnimator?)?.duration = 500
        (notifierShowAnimator as ValueAnimator?)?.addListener(object :
            Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {

            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {

            }
        })


    }


    fun hideNotifier(delay: Int = 0) {

        notifierItems.alpha = 1f

        if (::notifierShowAnimator.isInitialized) notifierShowAnimator.cancel()
        if (::notifierPinAnimator.isInitialized) notifierPinAnimator.cancel()

        if (!notifierHideAnimator.isRunning) {
            initializeHideAnimators()
            (notifierHideAnimator as ValueAnimator?)?.startDelay = delay.toLong()
            notifierHideAnimator.start()
        }
    }

    fun pinNotifierToTop(delay: Int = 0, animationListener: Animator.AnimatorListener? = null) {
        notifierItems.alpha = 1f

        if (::notifierHideAnimator.isInitialized) notifierHideAnimator.cancel()
        if (::notifierShowAnimator.isInitialized) notifierShowAnimator.cancel()


        if (!notifierPinAnimator.isRunning) {
            initializePinAnimators()
            (notifierPinAnimator as ValueAnimator?)?.startDelay = delay.toLong()
            if (animationListener != null) (notifierPinAnimator as ValueAnimator?)?.addListener(
                animationListener
            )
            notifierPinAnimator.start()

        }


    }

    /**
     * Passive Notifier, when new questions got inserted
     */
    fun setNewContentNotifier(it: Pair<Int, QuestionStackInfo?>) {

        it.second?.let { it1 -> setUpNotifierInfos(it1) }
        setTextMode("${it.first} new Questions")

        showNotifier(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                hideNotifier(20000)
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {

            }
        })

    }

    private fun setUpNotifierInfos(info: QuestionStackInfo?) {

        if (info == null) {
            image1.visibility = View.GONE
            image2.visibility = View.GONE

        } else {


            if (info.tagColors.size == 0) {
                image1.visibility = View.GONE
                image2.visibility = View.GONE
            } else if (info.tagColors.size == 1) {
                image1.visibility = View.VISIBLE
                image2.visibility = View.GONE
                image1.imageTintList = ColorStateList.valueOf(info.tagColors[0])
                image2.imageTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                image1.visibility = View.VISIBLE
                image2.visibility = View.VISIBLE
                image1.imageTintList = ColorStateList.valueOf(info.tagColors[0])
                image2.imageTintList = ColorStateList.valueOf(info.tagColors[1])
            }
        }
    }


    private fun setTextMode(text: String) {
        notifierText.text = text
    }

    private fun showNotifier(animationListener: Animator.AnimatorListener? = null) {

        notifierItems.alpha = 1f

        if (::notifierHideAnimator.isInitialized) notifierHideAnimator.cancel()
        if (::notifierPinAnimator.isInitialized) notifierPinAnimator.cancel()

        if (!notifierShowAnimator.isRunning) {

            initializeShowAnimators()
            if (animationListener != null) (notifierShowAnimator as ValueAnimator?)?.addListener(
                animationListener
            )
            (notifierShowAnimator as ValueAnimator?)?.start()

        }
    }


    fun moveNotifierSide(delay: Int = 0, distance: Float, hide: Boolean) {
        notifierItems.alpha = 1f
        if (::notifierShowAnimator.isInitialized) notifierShowAnimator.cancel()
        if (::notifierPinAnimator.isInitialized) notifierPinAnimator.cancel()
        if (::notifierHideAnimator.isInitialized) notifierHideAnimator.cancel()



        if (!notifierHideSideAnimator.isRunning) {
            initializeMoveAnimator(distance, hide, delay)
            (notifierHideSideAnimator as ValueAnimator?)?.startDelay = delay.toLong()
            notifierHideSideAnimator.start()
        }
    }


}