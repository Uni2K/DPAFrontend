package com.project.app.Preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.project.app.R

class MinimumvotesPreference : Preference {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        layoutResource = R.layout.preference_minimumvotes
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)


        val text: TextView = holder?.findViewById(R.id.settings_mv_text) as TextView
        val seekbar: SeekBar = holder.findViewById(R.id.settings_mv_seekbar) as SeekBar


        seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.progress?.let { persistInt(it)
                    text.text=getPersistedInt(it).toString()
                }
                notifyChanged()

            }
        })

        //Log.e("STIRNG","D: "+getPersistedInt(0))

        seekbar.progress=getPersistedInt(0)
        text.text=getPersistedInt(0).toString()



    }



    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            // Restore state
        } else {
            // Set state
            persistInt(defaultValue.toString().toInt())
        }
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return 0
    }


}