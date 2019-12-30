package com.project.app.Adapters


import android.content.res.ColorStateList
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Objects.Topic
import com.project.app.R


class TopicsTrendingAdapter(var content: ArrayList<Topic>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mRecyclerView: RecyclerView? = null
    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()


    fun update(conti: ArrayList<Topic>) {
        content = conti
        this.notifyDataSetChanged()
        //mRecyclerView?.smoothScrollToPosition(0) //BUG


    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(p0.context).inflate(R.layout.adapter_topics_default_trending, p0, false)
        vh = DefaultViewHolder(v)
        return vh


    }

    override fun getItemCount(): Int {
        return content.size
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {


                val holder = viewHolder as DefaultViewHolder
                holder.text.text = content[position].name
               // holder.icon.imageTintList =
              //      ColorStateList.valueOf(Color.parseColor(content[position].color))

                holder.parent.backgroundTintList=ColorStateList.valueOf(Color.parseColor(content[position].color))



    val id = holder.icon.context.resources.getIdentifier(
        "com.project.app:drawable/topicc${position + 1}",
        null,
        null
    )
    // Picasso.get().load(id).fit().into(viewHolder.topicIcon)
    holder.icon.setImageResource(id)


    }


    class DefaultViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.trending_text)
        val icon: ImageView = v.findViewById(R.id.trending_icon)
        val parent: ConstraintLayout= v.findViewById(R.id.trending_parent)
    }


}