package com.project.app.Helpers

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.DisplayMetrics
import android.view.View

class TouchAnimationHelper {

    val ANIMATION_PEEK_RIGHT = 11
    var animator: ValueAnimator? = null
    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

    fun addTouchAnimation(view: View, animationtype: Int, callback: View.OnClickListener) {

        view.setOnTouchListener(object : SwipeDetector(object : SwipeHelper {
            override fun onMove(distance: Float) {

            }

            override fun onStop(isMax: Boolean, largerThanMin: Boolean, negativeDistance: Boolean) {

            }

            override fun onClick() {
                callback.onClick(view)
            }

            override fun onDown() {
                startAnimation(view,animationtype)
            }
        }) {})

    }

   private fun startAnimation(view: View, animationtype: Int) {

        when (animationtype) {
            ANIMATION_PEEK_RIGHT -> {
                if (animator != null) return
                val startTranslation=   -20.dpToPx(view.context.resources.displayMetrics).toFloat()
                animator = ValueAnimator.ofFloat(
                    startTranslation,
                    0f
                )
                animator?.addUpdateListener { it ->
                    view.translationX = it.animatedValue.toString().toFloat()
                }
                animator?.duration = 500
                animator?.startDelay = 0
                animator?.addListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        animator = ValueAnimator.ofFloat(
                            view.translationX,
                            startTranslation
                        )
                        animator?.addUpdateListener { it ->
                            view.translationX= it.animatedValue.toString().toFloat()
                        }
                        animator?.duration = 500
                        animator?.startDelay = 500
                        animator?.addListener(object :
                            Animator.AnimatorListener {
                            override fun onAnimationRepeat(p0: Animator?) {
                            }

                            override fun onAnimationEnd(p0: Animator?) {
                                animator = null

                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationStart(p0: Animator?) {
                            }
                        })
                        animator?.start()
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }
                })
            }
        }
        animator?.start()
    }
}