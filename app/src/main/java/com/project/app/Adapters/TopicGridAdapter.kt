package com.project.app.Adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Fragments.TopicProviderFragment
import com.project.app.Interfaces.TopicFragmentInterface
import com.project.app.Objects.Topic
import com.project.app.R


class TopicGridAdapter(
    contentInitial: ArrayList<Topic>,
    val parent: TopicProviderFragment,
    private val isSubmenu: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mRecyclerView: RecyclerView? = null


    private val diffCallback = object : DiffUtil.ItemCallback<Topic>() {
        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            val result = oldItem.compareTo(newItem)
            return !(result != 10 && result != 0)
        }

    }
    private val mDiffer = AsyncListDiffer(this, diffCallback)


    init {
        mDiffer.submitList(contentInitial)
    }


    /**
     * NEEDS FIXED SIZED LISTS
     * Need to filter the list in order to not push wrong items into it
     * The preserved list is the first one that got pushed, everything coming from update should contain the SAME ITEMS, but with different content
     * Problem: The information which position was tiped is lost in the process-> refresh everything with notifydatasetchanged
     */
    fun update(conti: ArrayList<Topic>, newList: Boolean = true) {

        if (newList) {

            mDiffer.submitList(conti) {
                notifyDataSetChanged()
                //CALL this otherwise issues with not updating all items-> 4 changed, only 3 got updated -> Problem.

            }


        }else{
            notifyDataSetChanged()
        }

       /* GlobalScope.launch {
            //Get the list with the correct topics .> subtopic e.g.
            val current = ArrayList(mDiffer.currentList)
            //for every item get the new item inside of the updated list, and replace it inside current
            for (c in current) {
                val sameItem = conti.find { it.id == c.id }
                sameItem?.let {
                    val indexToReplace = current.indexOf(c)
                    current.set(indexToReplace, sameItem)
                }
            }
            Log.d("LIST", "DATA: $current             ||  $conti")
            launch(Dispatchers.Main) {
                mDiffer.submitList(current)
            }
        }
*/
        // mRecyclerView?.smoothScrollToPosition(0) //BUG
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var vh: RecyclerView.ViewHolder

        vh = when (p1) {
            0 -> {
                val v =
                    LayoutInflater.from(p0.context).inflate(R.layout.adapter_topic_grid, p0, false)
                DefaultViewHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.adapter_topic_grid_full, p0, false)
                FullViewHolder(v)

            }
        }

        return vh


    }

    override fun getItemCount(): Int {
        if (isSubmenu) return mDiffer.currentList.size + 1
        return mDiffer.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && isSubmenu) return 1
        return 0
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val content = mDiffer.currentList

        when (viewHolder.itemViewType) {
            0 -> {
                viewHolder as DefaultViewHolder
                var pos = viewHolder.adapterPosition
                if (isSubmenu) pos -= 1
                if (content[pos].parent != "-1" || content[pos]._id.startsWith("x")) {


                    if (parent.isTopicSelected(content[pos])) {
                        val id = viewHolder.topicIcon.context.resources.getIdentifier(
                            "com.project.app:drawable/topicc${position + 1}",
                            null,
                            null
                        )
                        // Picasso.get().load(id).fit().into(viewHolder.topicIcon)
                        viewHolder.topicIcon.setImageResource(id)


                    } else {

                        val id = viewHolder.topicIcon.context.resources.getIdentifier(
                            "com.project.app:drawable/topicf${position + 1}",
                            null,
                            null
                        )
                        // Picasso.get().load(id).fit().into(viewHolder.topicIcon)
                        viewHolder.topicIcon.setImageResource(id)


                    }
                } else {

                    val id = viewHolder.topicIcon.context.resources.getIdentifier(
                        "com.project.app:drawable/topicc${position + 1}",
                        null,
                        null
                    )
                    //  Picasso.get().load(id).fit().into(viewHolder.topicIcon)
                    viewHolder.topicIcon.setImageResource(id)
                }
                viewHolder.itemView.setOnClickListener {
                    if (content[pos].parent == "-1" && !content[pos]._id.startsWith(
                            "x"
                        )
                    ) {
                        parent.expandTopic(content[pos])
                    } else {
                        parent.onClickTopicAll(
                            content[pos]
                        )


                    }
                }
                viewHolder.topicTitle.text = content[pos].name

            }
            1 -> {
                viewHolder as FullViewHolder


                viewHolder.itemView.setOnClickListener { parent.collapseTopic() }

                viewHolder.topicTitle.text = parent.getParentTagName(content[0].parent)
                viewHolder.topicTitleSub.text = "${content.size} Topics"

                val id = viewHolder.topicIcon.context.resources.getIdentifier(
                    "com.project.app:drawable/topicc${position + 1}",
                    null,
                    null
                )
                // Picasso.get().load(id).fit().centerCrop().into(viewHolder.topicIcon)
             //   viewHolder.topicIcon.setImageResource(id)


            }
        }


    }


    class DefaultViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val topicTitle: TextView = v.findViewById(R.id.topic_title)
        val topicIcon: ImageView = v.findViewById(R.id.topic_icon)
    }

    class FullViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val topicTitle: TextView = v.findViewById(R.id.topic_title)
        val topicIcon: ImageView = v.findViewById(R.id.topic_icon)
        val topicBack: ImageView = v.findViewById(R.id.topic_full_back)
        val topicTitleSub: TextView = v.findViewById(R.id.topic_title_sub)

    }
}