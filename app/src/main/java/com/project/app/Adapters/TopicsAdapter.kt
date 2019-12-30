package com.project.app.Adapters


import android.content.res.ColorStateList
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.project.app.Fragments.TopicOverviewFragment
import com.project.app.Objects.Topic
import com.project.app.R
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller


class TopicsAdapter(var content: ArrayList<Topic>, val parent: TopicOverviewFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerViewFastScroller.OnPopupTextUpdate {
    override fun onChange(position: Int): CharSequence {
        return content[position].name.first().toString()
    }

    var mRecyclerView: RecyclerView? = null
    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()


    fun update(conti: ArrayList<Topic>) {
        content = conti
        this.notifyDataSetChanged()


    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 1 else 0
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var vh: RecyclerView.ViewHolder

        if (p1 == 1) {
            val v = LayoutInflater.from(p0.context)
                .inflate(R.layout.adapter_topics_trending_overview, p0, false)
            vh = TrendingViewHolder(v)
            return vh
        }
        val v = LayoutInflater.from(p0.context).inflate(R.layout.adapter_topics_default, p0, false)
        vh = DefaultViewHolder(v)
        return vh


    }

    override fun getItemCount(): Int {
        return content.size + 1
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (viewHolder.itemViewType) {
            0 -> {
                val position = viewHolder.adapterPosition - 1
                val holder = viewHolder as DefaultViewHolder
                holder.text.text = content[position].name
                holder.itemView.setOnClickListener {
                    parent.showDetailsForTag(content[position])
                }
                val id = holder.icon.context.resources.getIdentifier(
                    "com.project.app:drawable/topicc${position + 1}",
                    null,
                    null
                )
                // Picasso.get().load(id).fit().into(viewHolder.topicIcon)
                holder.icon.setImageResource(id)
            }
            1 -> {
                val holder = viewHolder as TrendingViewHolder
                holder.recycler.setHasFixedSize(true)
                holder.recycler.layoutManager=LinearLayoutManager(viewHolder.recycler.context,RecyclerView.HORIZONTAL, false)
                holder.recycler.adapter=TopicsTrendingAdapter(content)
                holder.recycler.addItemDecoration(SpacingItemDecoration(6.dpToPx(viewHolder.recycler.context.resources.displayMetrics), 0))

            }
        }


    }



    class DefaultViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.adap_topics_text)
        val icon: ImageView = v.findViewById(R.id.adap_topics_icon)
    }

    class TrendingViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val recycler: RecyclerView = v.findViewById(R.id.topics_trending)
    }
}