package com.project.app.CustomViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.project.app.R

class ToggleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    fun toggle(s: String, outline:Boolean) {
        text.text=s
    if(!outline){
        back.setBackgroundResource(R.drawable.background_ctb)
        text.setTextColor(Color.WHITE)
        icon.imageTintList= ColorStateList.valueOf(Color.WHITE)
    }else{
        back.setBackgroundResource(R.drawable.background_ctb_outline)
        text.setTextColor(Color.BLACK)
        icon.imageTintList= ColorStateList.valueOf(Color.BLACK)

    }


    }

    var icon:ImageView
    var text:TextView
    var back:RelativeLayout
    init {
        val v = View.inflate(context, R.layout.customview_togglebutton, this)
        icon=v.findViewById(R.id.tb_icon)
        text=v.findViewById(R.id.tb_text)
        back=v.findViewById(R.id.tb_back)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.customToggleButton,
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

