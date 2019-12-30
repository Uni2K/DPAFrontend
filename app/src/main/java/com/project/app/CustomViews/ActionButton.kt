package com.project.app.CustomViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.project.app.R

class ActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    var icon:ImageView
    var text:TextView
    init {
        val v = View.inflate(context, R.layout.customview_actionbutton, this)
        setCardBackgroundColor(Color.WHITE)
        icon=v.findViewById(R.id.ab_icon)
        text=v.findViewById(R.id.ab_text)
        radius=2000f
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.customActionButton,
            0, 0).apply {

            try {
                icon.setImageResource(getResourceId(R.styleable.customToggleButton_cicon,0))
                text.text=getString(R.styleable.customToggleButton_ctext)

            } finally {
                recycle()
            }

        }

    }











}

