package com.project.app.CustomViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.project.app.R

class ImageTimer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var image:ImageView
    private var timer:ProgressBar
    init {
        val v = View.inflate(context, R.layout.customview_imagetimer, this)
        image=v.findViewById(R.id.imagetimer_image)
        timer=v.findViewById(R.id.imagetimer_progressbar)
    }



    fun setImage(resID:Int){
        image.setImageResource(resID)
    }

    fun setProgress(value: Int){
        timer.progress = value
    }






}

