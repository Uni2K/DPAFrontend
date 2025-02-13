package com.project.app.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Bases.LocalBase
import com.project.app.Interfaces.QuestionController
import com.project.app.Objects.ContentModel
import com.project.app.Paging.ContentModelDiffCallback
import com.project.app.Paging.Displayable
import com.project.app.R
import com.project.app.ViewHolder.*

class DisplayablePagingAdapter(val localBase: LocalBase, val questionProvider: QuestionController, val lifecycleOwner: LifecycleOwner?=null) :
    PagedListAdapter<ContentModel, RecyclerView.ViewHolder>(diffCallback) {

    companion object{
        const val VIEWTYPE_QUESTION=0
        const val VIEWTYPE_USER=1
        const val VIEWTYPE_TOPIC=2

        val diffCallback=ContentModelDiffCallback()

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

      /*       when(viewType){
                 VIEWTYPE_QUESTION->{
                     val v = LayoutInflater.from(parent.context)
                         .inflate(R.layout.adapter_question_general, parent, false)
                     return GeneralQuestionViewHolder(v, localBase, questionProvider).newInstance(lifecycleOwner)

                 }
                 VIEWTYPE_USER->{
                     val v = LayoutInflater.from(parent.context)
                         .inflate(R.layout.adapter_user, parent, false)
                     return GeneralUserViewHolder(v)
                 }
                 VIEWTYPE_TOPIC->{

                 }


             }

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_question_general, parent, false)
        return GeneralQuestionViewHolder(v, localBase, questionProvider).newInstance(lifecycleOwner)
*/


         val v = LayoutInflater.from(parent.context)
             .inflate(R.layout.adapter_polls_skeleton, parent, false)
         return MiscPollViewHolder(v,viewType)



    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       // (holder as? GeneralQuestionViewHolder)?.bind(ContentModel.toDisplayable(getItem(position))?.getQuestion())
     //   (holder as? GeneralUserViewHolder)?.bind(ContentModel.toDisplayable(getItem(position))?.getUser())
    }

    override fun getItemViewType(position: Int): Int {
        if(position%6==0)return 6
        if(position%5==0)return 5
        if(position%4==0)return 4
        if(position%3==0)return 3
        if(position%2==0)return 2
        if(position%1==0)return 1
        return 0

        val displayable:Displayable?=ContentModel.toDisplayable(currentList?.get(position))

        when(displayable?.type){
            Displayable.QUESTION->{
                return VIEWTYPE_QUESTION
            }
            Displayable.USER->{
                return VIEWTYPE_USER
            }
            Displayable.TOPIC->{
                return VIEWTYPE_TOPIC
            }
        }

        return VIEWTYPE_QUESTION


    }


    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(viewHolder, position, payloads)
        } else {
            val o: Bundle = payloads[0] as Bundle
            for (key: String in o.keySet()) {
                when (key) {
                    "answers" -> {
                        (viewHolder as GeneralQuestionViewHolder).updateAnswers(ContentModel.toDisplayable(getItem(position))?.let {it.getQuestion()
                        })
                    }
                    "text" -> {
                        (viewHolder as QuestionViewHolder).updateText(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getQuestion()
                        })
                    }
                    "context" -> {
                        (viewHolder as QuestionViewHolder).updateContext(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getQuestion()
                        })
                    }
                    "votes" -> {
                        (viewHolder as GeneralQuestionViewHolder).updateVotes(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getQuestion()
                        })
                    }
                    "expirationdate" -> {
                        (viewHolder as GeneralQuestionViewHolder).updateExpirationDate(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getQuestion()
                        })
                    }
                    "userid" -> {
                        (viewHolder as GeneralQuestionViewHolder).updateUser(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getQuestion()

                        })
                    }
                    "avatar" -> {
                        (viewHolder as GeneralUserViewHolder).updateAvatar(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getUser()
                        })
                    }
                    "name" -> {
                        (viewHolder as GeneralUserViewHolder).updateName(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getUser()
                        })
                    }
                    "desc" -> {
                        (viewHolder as GeneralUserViewHolder).updateDesc(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getUser()
                        })
                    }
                    "url" -> {
                        (viewHolder as GeneralUserViewHolder).updateUrl(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getUser()
                        })
                    }
                    "reputation" -> {
                        (viewHolder as GeneralUserViewHolder).updateReputation(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getUser()
                        })
                    }
                    "location" -> {
                        (viewHolder as GeneralUserViewHolder).updateLocation(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getUser()
                        })
                    }
                    "followercount" -> {
                        (viewHolder as GeneralUserViewHolder).updateFollowercount(ContentModel.toDisplayable(getItem(position))?.let {
                            it.getUser()
                        })
                    }

                    "allEqual" -> {
                        (viewHolder as QuestionViewHolder).allEqual()
                    }
                }

            }
        }


    }


}