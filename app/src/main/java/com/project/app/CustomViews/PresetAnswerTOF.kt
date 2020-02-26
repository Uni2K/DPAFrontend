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
import kotlinx.android.synthetic.main.snack_assistant_big.view.*
import kotlin.math.roundToInt


class PresetAnswerTOF  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr){
    var type:String="positive"
    init {
        inflate(context, R.layout.preset_answer_tof, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.customTOFButton,
            0, 0).apply {

            try {
                type = getString(R.styleable.customTOFButton_tofButton)?:"positive"

            } finally {
                recycle()
            }
        }

        setUpStyle()
    }

    private fun setUpStyle() {

        if(type=="positive"){
            findViewById<View>(R.id.tof_back).setBackgroundColor(resources.getColor(R.color.tof_pos,null))
            findViewById<ImageView>(R.id.tof_thumb).setImageResource(R.drawable.baseline_thumb_up_24)

        }else{
            findViewById<View>(R.id.tof_back).setBackgroundColor(resources.getColor(R.color.tof_neg,null))
            findViewById<ImageView>(R.id.tof_thumb).setImageResource(R.drawable.baseline_thumb_down_24)
        }
    }


}





