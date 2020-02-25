package com.project.app.ViewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.app.R

/**
 * Combine a header with a body inside a skeleton
 */
open class MiscPollViewHolder(
    val v: View,
    val viewType: Int
) : RecyclerView.ViewHolder(v) {




    init {
        val parent: ViewGroup = v.findViewById(R.id.skeletonContainer)
        parent.post {
            val header = LayoutInflater.from(v.context)
                .inflate(R.layout.adapter_polls_stub_header_default, parent, true)
            if (viewType == 0) {
                val body = LayoutInflater.from(v.context)
                    .inflate(R.layout.adapter_polls_body_grid_4, parent, true)
            } else if (viewType == 1){
                val body = LayoutInflater.from(v.context)
                    .inflate(R.layout.adapter_polls_body_list_expandable, parent, true)
            }
            else if (viewType == 2){
                val body = LayoutInflater.from(v.context)
                    .inflate(R.layout.adapter_polls_body_grid_2, parent, true)
            }
            else if (viewType == 3){
                val body = LayoutInflater.from(v.context)
                    .inflate(R.layout.adapter_polls_body_grid_3, parent, true)
            }
            // parent.addView(header)
        }


    }


}
