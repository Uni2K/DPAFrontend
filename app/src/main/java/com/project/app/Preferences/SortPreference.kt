package com.project.app.Preferences

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.project.app.R

class SortPreference : Preference {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        layoutResource = R.layout.preference_sort
    }

    lateinit var sort1: ImageView
    lateinit var sort2: ImageView
    lateinit var sort3: ImageView
    lateinit var sort4: ImageView


    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        // Set our custom views inside the layout
        /*   final TextView myTextView = (TextView) view.findViewById(R.id.mypreference_widget);
           if (myTextView != null) {
               myTextView.setText(String.valueOf(mClickCounter));
           }*/


         sort1 = holder?.findViewById(R.id.sort1) as ImageView
         sort2 = holder.findViewById(R.id.sort2) as ImageView
        sort3 = holder.findViewById(R.id.sort3) as ImageView
        sort4 = holder.findViewById(R.id.sort4) as ImageView

        sort1.setOnClickListener {
            resetAlpha()
            persistString("sort1")
            sort1.alpha = 1f
            notifyChanged()


        }

        sort2.setOnClickListener {
            resetAlpha()
            sort2.alpha = 1f
            persistString("sort2")
            notifyChanged()
        }
        sort3.setOnClickListener {
            resetAlpha()
            sort3.alpha = 1f
            persistString("sort3")
            notifyChanged()
        }
        sort4.setOnClickListener {
            resetAlpha()
            sort4.alpha = 1f
            persistString("sort4")
            notifyChanged()
        }

        val activated = ColorStateList.valueOf(context.getColor(R.color.activated))
        val idle = ColorStateList.valueOf(context.getColor(R.color.primaryOppositeColor))
        sort1.imageTintList = idle
        sort2.imageTintList = idle
        sort3.imageTintList = idle
        sort4.imageTintList = idle

        when (getPersistedString("sort1")) {
            "sort1" -> {
                sort1.imageTintList = activated
            }
            "sort2" -> {
                sort2.imageTintList = activated
            }
            "sort3" -> {
                sort3.imageTintList = activated
            }
            "sort4" -> {
                sort4.imageTintList = activated
            }
        }

    }

    private fun resetAlpha() {
        sort1.alpha = 0.5f
        sort2.alpha = 0.5f
        sort3.alpha = 0.5f
        sort4.alpha = 0.5f

    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            // Restore state
        } else {
            // Set state
            persistString(defaultValue.toString())
        }
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return "sort1"
    }


}