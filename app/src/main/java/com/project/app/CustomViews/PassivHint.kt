package com.project.app.CustomViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.project.app.R

class PassivHint @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var icon:ImageView
    var text:TextView
    init {
        val v = View.inflate(context, R.layout.customview_passivhint, this)
        icon=v.findViewById(R.id.ab_icon)
        text=v.findViewById(R.id.ab_text)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.customActionButton,
            0, 0).apply {

            try {
                //icon.setImageResource(getResourceId(R.styleable.customActionButton_abicon,0))
                text.text=getString(R.styleable.customActionButton_abtext)


            } finally {
                recycle()
            }

        }

    }











}

