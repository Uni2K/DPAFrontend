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

class StatBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var image1:ImageView
    private var image2:ImageView

    private var txt1:TextView
    private var txt2:TextView

    init {
        val v = View.inflate(context, R.layout.customview_statbar_full, this)
        image1=v.findViewById(R.id.statbar_ic1)
        image2=v.findViewById(R.id.statbar_ic2)

        txt1=v.findViewById(R.id.statbar_txt1)
        txt2=v.findViewById(R.id.statbar_txt2)

    }







}

