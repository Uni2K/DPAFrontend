package com.project.app.Paging

import android.os.Bundle
import android.os.Looper
import com.project.app.Helpers.Constants
import com.project.app.Objects.Question
import com.project.app.Objects.Topic
import com.project.app.Objects.User


/**
 * Basic Class of all pagedLists -> can contain either Questions, Users or Topics
 * id: Either question, user or topic id-> no conflicts cause type will prevent this
 *
 *
 *
 *
 * Different TYPES of lists ->
 * 1.strict Timeordered -> e.g: New Questions/Users/Topics -> only 1 DB, notified by socket: Index: Timestamp -> allows before
 * 2.strict Paged -> e.g.: similar Questions -> 1 DB -> Index: Page Key -> No before
 * 3.mixed -> e.g. Feed -> Multiple DB-> Index: Specific iterative index-> starting at 0 with the initial and couting up with new ones
 *
 *  2 User
 *  1 Question
 *  0 Question
 *  -1 Topic
 *  -2 Question
 * -> The order and content comes from multiple thisbases, therefor one has to keep track of the list
 * -> Answer: FEED Table ->
 */
 open class Displayable(var id:String="-1", var type: String,  var index:String="-1", var contentname:String="-1",var data:Any?, val indextype:Int= TIMED) {
    companion object{
        const val QUESTION="Q"
        const val USER="U"
        const val TOPIC="T"

        const val MIXED=0
        const val TIMED=1

        fun getIndexType(contentname: String):Int{
            return if(contentname== Constants.CONTENT_FEED) MIXED
            else TIMED
        }
        /**
         * Splits the list by types, needed for storing in thisbase, since each type got a separate db
         */
        suspend fun splitListByType(list:List<Displayable>):Triple<List<Question>,List<User>,List<Topic>>{
            if(Looper.getMainLooper()== Looper.myLooper())throw IllegalThreadStateException()
            var content= Triple(ArrayList<Question>(), ArrayList<User>(),ArrayList<Topic>())
            for (displayable in list) {
                when(displayable.type){
                    QUESTION->{
                        displayable.getQuestion()?.let{content.first.add(it)}
                    }
                    USER->{
                        displayable.getUser()?.let{content.second.add(it)}
                    }
                    TOPIC->{
                        displayable.getTopic()?.let{content.third.add(it)}
                    }
                }
            }


            return content
        }

    }

    fun getQuestion(): Question? {
        return data as? Question
    }

    fun getUser(): User? {
        return data as? User
    }

    fun getTopic(): Topic? {
        return data as? Topic
    }



    fun isSame(dis: Displayable): Boolean {
        if (type != dis.type) return false
        if(id!=dis.id)return false
        return true
    }

    fun isContentTheSame(dis:Displayable):Int?{
        if (type != dis.type) return -1

        when(type){
            QUESTION->{

               return (data as? Question)?.compareTo(dis.data as? Question)
            }
            USER->{

                return (data as? User)?.compareTo(dis.data as? User)
            }
            TOPIC->{
                return (data as? Topic)?.compareTo(dis.data as? Topic)
            }
        }


        return 10
    }

    fun getChangePayload(newitem: Displayable): Bundle {
        when(type){
            QUESTION->{
                this as Question
                newitem as Question
                return this.getChangePayload(this,newitem)
            }
            USER->{
                this as User
                newitem as User
                return this.getChangePayload(this,newitem)
            }
            TOPIC->{
                this as Topic
                newitem as Topic
                return this.getChangePayload(this,newitem)
            }
        }
        return Bundle()
    }





}
