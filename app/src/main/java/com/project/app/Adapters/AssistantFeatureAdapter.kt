package com.project.app.Adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Dialogs.AssistantDialogFragment
import com.project.app.Fragments.TopicPickFragment
import com.project.app.Fragments.TopicProviderFragment
import com.project.app.R


class AssistantFeatureAdapter(val parent: AssistantDialogFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val numberOfElements = 11
    lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView_: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView_)
        recyclerView = recyclerView_
    }


    override fun getItemCount(): Int {
        return numberOfElements
    }



    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as DefaultViewHolder
        viewHolder.icon.imageTintList= ColorStateList.valueOf(Color.WHITE)
        viewHolder.text.setTextColor(Color.WHITE)

        when(position){
            0->{
                viewHolder.text.text = "Contact Support"
                viewHolder.icon.setImageResource(R.drawable.baseline_live_help_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#21CAD9"))
            }
            1->{
                viewHolder.text.text = "Settings"
                viewHolder.icon.setImageResource(R.drawable.baseline_build_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#465B65"))
            }
            2->{
                viewHolder.text.text = "Report"
                viewHolder.icon.setImageResource(R.drawable.baseline_flag_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#C20808"))
            }
            3->{
                viewHolder.text.text = "Rate your experience"
                viewHolder.icon.setImageResource(R.drawable.baseline_rate_review_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#FF4600"))
            }
            4->{
                viewHolder.text.text = "Play a game"
                viewHolder.icon.setImageResource(R.drawable.baseline_play_arrow_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#ffffff"))
                viewHolder.icon.imageTintList= ColorStateList.valueOf(Color.BLACK)
                viewHolder.text.setTextColor(Color.BLACK)
            }
            5->{
                viewHolder.text.text = "Survey"
                viewHolder.icon.setImageResource(R.drawable.baseline_favorite_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#622FED"))
            }
            6->{
                viewHolder.text.text = "View FAQ"
                viewHolder.icon.setImageResource(R.drawable.baseline_forum_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#4C386E"))
            }
            7->{
                viewHolder.text.text = "View Disclaimer"
                viewHolder.icon.setImageResource(R.drawable.baseline_import_contacts_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#92206A"))
            }
            8->{
                viewHolder.text.text = "Jobs"
                viewHolder.icon.setImageResource(R.drawable.baseline_location_city_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#868F29"))
            }
            9->{
                viewHolder.text.text = "How to gain Reputation"
                viewHolder.icon.setImageResource(R.drawable.baseline_show_chart_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#EC407A"))
            }
            10->{
                viewHolder.text.text = "Scanner & QR"
                viewHolder.icon.setImageResource(R.drawable.baseline_scanner_24)
                viewHolder.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#0072BE"))
            }

        }

        viewHolder.back.setOnClickListener {
            Toast.makeText(parent.context, "Clicked: "+viewHolder.text.text,Toast.LENGTH_SHORT).show()
        }
    }

    class DefaultViewHolder(v: View) : RecyclerView.ViewHolder(v) {
       val text: TextView = v.findViewById(R.id.adap_as_text)
       val icon: ImageView = v.findViewById(R.id.adap_as_icon)
        val back: LinearLayout= v.findViewById(R.id.adap_as_back)

    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.adapter_assitant_features, p0, false)
        return DefaultViewHolder(v)
    }

}