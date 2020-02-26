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

            when (viewType) {
                0 -> {
                    val header = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_stub_header_default, parent, true)
                    val body = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_body_grid_4, parent, true)
                }
                1 -> {
                    val header = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_stub_header_default, parent, true)
                    val body = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_body_list_expandable, parent, true)
                }
                2 -> {
                    val header = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_stub_header_default, parent, true)
                    val body = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_body_grid_2, parent, true)
                }
                3 -> {
                    val header = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_stub_header_default, parent, true)
                    val body = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_body_grid_3, parent, true)
                }
                4 -> {
                    val header = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_stub_header_small, parent, true)
                    val body = LayoutInflater.from(v.context)
                        .inflate(R.layout.adapter_polls_body_tof_image, parent, true)
                }
                // parent.addView(header)
            }
            // parent.addView(header)
        }


    }


}
