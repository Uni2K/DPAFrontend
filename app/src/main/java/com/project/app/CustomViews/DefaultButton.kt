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

class DefaultButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    var text:TextView
    init {
        val v = View.inflate(context, R.layout.customview_defaultbutton, this)
        text=v.findViewById(R.id.ab_text)

        radius=resources.getDimension(R.dimen.radius_action)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.customDefaultButton,
            0, 0).apply {

            try {
                text.text=getString(R.styleable.customDefaultButton_dbtext)
                val tintListElement=resources.getColorStateList(getResourceId(R.styleable.customDefaultButton_dbelementTint,0),null)
                val tintListBackground=resources.getColorStateList(getResourceId(R.styleable.customDefaultButton_dbbackgroundTint,0),null)

                text.setTextColor(tintListElement)

                backgroundTintList=tintListBackground


            } finally {
                recycle()
            }

        }

    }











}

