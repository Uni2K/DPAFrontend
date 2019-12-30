package com.project.app.CustomViews

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.project.app.R
import kotlin.math.roundToInt


class PresetAnswer : ConstraintLayout {
    private var idle: Boolean = true
    var progressView: View
    var selectedView: ImageView
    var textView: TextView
    var percenttextView: TextView
    var loading: ProgressBar
    var indicator: View
    var cover: View
    var blue: View
    private val ANIM_DURATION = 400L
    var showStatLabel = true
    var fullWidth = false


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        inflate(context, R.layout.preset_answer, this)
        blue = findViewById(R.id.pr_blue)
        progressView = findViewById(R.id.pr_foreground)
        selectedView = findViewById(R.id.pr_selected)
        textView = findViewById(R.id.pr_text)
        percenttextView = findViewById(R.id.pr_percentext)
        indicator = findViewById(R.id.pr_indicator)
        cover = findViewById(R.id.pr_cover)
        loading = findViewById(R.id.pr_progress)
        setIdleMode()

    }


    fun setIdleMode() {
        selectedView.setImageResource(0)
        percenttextView.setTextColor(context.getColor(R.color.primaryOppositeColor))
        selectedView.visibility = View.INVISIBLE
        blue.visibility = View.INVISIBLE
        if (showStatLabel) {

            if (!fullWidth) percenttextView.visibility = View.INVISIBLE
            else percenttextView.visibility = View.GONE
        }
        setProgress(100f, null)
        val typeface = ResourcesCompat.getFont(context, R.font.segui)
        textView.typeface = typeface
        this.idle = true

    }

    fun setSelectedMode(progress: Int, clicked: Boolean = false) {
        selectedView.visibility = View.VISIBLE
        blue.visibility = View.VISIBLE
        percenttextView.setTextColor(Color.WHITE)
        selectedView.setImageResource(R.drawable.baseline_done_24)
        val typeface = ResourcesCompat.getFont(context, R.font.seguisb)
        textView.typeface = typeface
        setStatsEnabled(progress, clicked)
    }

    fun updateVote(progress: Int, clicked: Boolean = false) {
        if (idle) return  //When user not clicked on it yet
        setProgress(progress.toFloat(), object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {

            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        }, clicked)
    }

    fun setStatsEnabled(progress: Int, clicked: Boolean = false) {
        cover.visibility = View.GONE
        this.idle = false
        setProgress(progress.toFloat(), object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                if (showStatLabel) percenttextView.visibility = View.VISIBLE

            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        }, clicked)


    }


    fun setProgress(
        prgs: Float,
        endlistener: Animator.AnimatorListener? = null,
        clicked: Boolean = false
    ) {
        if (prgs > 100) return
        if (!clicked) {
            progressView.scaleX = prgs / 100f
            endlistener?.onAnimationEnd(null)

        } else {
            Log.e("setProgress","CLICKED: $clicked")
            val valAnim: ValueAnimator = ValueAnimator.ofFloat(progressView.scaleX, prgs / 100f)
            valAnim.duration = ANIM_DURATION
            valAnim.addUpdateListener { p0 ->
                progressView.scaleX = p0?.animatedValue.toString().toFloat()

            }
            if (endlistener != null) valAnim.addListener(endlistener)
            valAnim.start()
        }
        if (showStatLabel) percenttextView.text = "${prgs.roundToInt()}%"


    }

    fun setText(str: String) {
        textView.text = str

    }

    fun setIndicatorColor(blue: Int) {
        indicator.visibility = View.VISIBLE
        indicator.setBackgroundColor(blue)
        cover.setBackgroundResource(R.drawable.background_answer_indicated)
        progressView.setBackgroundResource(R.drawable.background_answer_indicated)
    }


    fun showLoading() {
        loading.visibility = View.VISIBLE
    }
    fun hideLoading() {
        loading.visibility = View.GONE
    }
}





