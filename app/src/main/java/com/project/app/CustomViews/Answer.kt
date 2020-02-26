package com.project.app.CustomViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.project.app.R
import kotlinx.android.synthetic.main.preset_answer_image.view.*

open class Answer@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr){

    fun submitAnswer(){

    }


    fun setIdleMode(){

    }

    fun setSelectedMode(){
        findViewById<ImageView>(R.id.check).visibility= View.VISIBLE
    }

    fun setExpiredMode(){

    }

}