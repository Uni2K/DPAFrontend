package com.project.app.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Fragments.TopicPickFragment
import com.project.app.Fragments.TopicProviderFragment
import com.project.app.R


class WelcomeAdapter(val parent: DialogFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var topicPickFragment: TopicPickFragment= TopicPickFragment()
    private val numberOfElements = 4
    lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView_: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView_)
        recyclerView = recyclerView_
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var vh: RecyclerView.ViewHolder


        when (p1) {

            1 -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.adapter_welcome_start, p0, false)
                vh = StartViewHolder(v)
                return vh
            }
            2 -> {
                val v =
                    LayoutInflater.from(p0.context).inflate(R.layout.adapter_welcome_end, p0, false)
                vh = EndViewHolder(v, parent, topicPickFragment)
                return vh
            }
            else -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.adapter_welcome_default, p0, false)
                vh = DefaultViewHolder(v)
                return vh
            }
        }


    }

    override fun getItemCount(): Int {
        return numberOfElements
    }

    override fun getItemViewType(position: Int): Int {

        if (position == 0) {
            return 1
        }
        if (position == numberOfElements - 1) {
            return 2
        }
        return 0
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (viewHolder.itemViewType) {
            0 -> {
               // val vh = (viewHolder as DefaultViewHolder)

            }
            1 -> {
                val vh = (viewHolder as StartViewHolder)
                vh.button.setOnClickListener {
                    recyclerView.smoothScrollToPosition(1)
                }
            }
            2 -> {
                val vh = (viewHolder as EndViewHolder)
                vh.button.setOnClickListener {
                    parent.dismiss()
                }

            }
        }


    }


    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder.itemViewType == 2) {
            val holder_ = holder as EndViewHolder
            holder_.removeFragment()
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.itemViewType == 2) {
            val holder_ = holder as EndViewHolder
            holder_.addFragment()
        }
    }

    class DefaultViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.welcome_logo)
    }

    class EndViewHolder(
        v: View,
        val parent: DialogFragment,
        private val topicPickFragment: TopicPickFragment
    ) : RecyclerView.ViewHolder(v) {
        fun addFragment() {
            val bundle:Bundle= Bundle()
            bundle.putInt("preset",TopicProviderFragment.PRESET_WELCOME)
            topicPickFragment.arguments=bundle

            parent.childFragmentManager.beginTransaction()
                .replace(tagContainer.id, topicPickFragment, "tags").commit()
        }

        fun removeFragment() {
            parent.childFragmentManager.beginTransaction().remove(topicPickFragment)

        }

        private val tagContainer: FrameLayout = v.findViewById(R.id.tagContainer)
        val button: View = v.findViewById(R.id.welcome_finish)
    }

    class StartViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.welcome_text)
        val button: ImageView = v.findViewById(R.id.welcome_start)
    }

}