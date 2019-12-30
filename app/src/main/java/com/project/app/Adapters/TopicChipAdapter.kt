package com.project.app.Adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.project.app.Helpers.AsyncListDifferBugfreeTopic
import com.project.app.Interfaces.TopicSelectionInterface
import com.project.app.Objects.Topic
import com.project.app.R


class TopicChipAdapter( val parent: TopicSelectionInterface) :
    RecyclerView.Adapter<TopicChipAdapter.ViewHolder>() {
    private val diffCallback = object: DiffUtil.ItemCallback<Topic>(){
        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
       return oldItem._id==newItem._id
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            val result=oldItem.compareTo(newItem)

            return !(result!=10 && result!=0)
        }

    }
    private val mDiffer = AsyncListDifferBugfreeTopic(this, diffCallback)
    private var mRecyclerView: RecyclerView? = null




    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val inflatedView =
            LayoutInflater.from(p0.context).inflate(R.layout.adapter_tagoverview, p0, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }
    fun submitList(list: List<Topic>) {
       mDiffer.submitList(null)
        mDiffer.submitList(list)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        viewHolder.chip.chipStrokeWidth = 0f
        var color = Color.BLACK
        val content=mDiffer.currentList
        try {
            color = Color.parseColor(content[viewHolder.adapterPosition].color)
        } catch (exp: IllegalArgumentException) {
            exp.printStackTrace()
            color = content[viewHolder.adapterPosition].color.toInt()

        }
        val id = viewHolder.chip.context.resources.getIdentifier(
            "com.project.app:drawable/topicc${position + 1}",
            null,
            null
        )
        // Picasso.get().load(id).fit().into(viewHolder.topicIcon)
       viewHolder.chip.setChipIconResource(id)
        viewHolder.chip.backgroundTintList=ColorStateList.valueOf(color)

        viewHolder.chip.text = content[viewHolder.adapterPosition].name

        viewHolder.chip.setOnClickListener {
          parent.onClickTopicOverview(content[viewHolder.adapterPosition])

        }

    }

    fun getList(): ArrayList<Topic> {
        return ArrayList(mDiffer.currentList)
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var chip: Chip = v.findViewById(R.id.chip)


    }
}