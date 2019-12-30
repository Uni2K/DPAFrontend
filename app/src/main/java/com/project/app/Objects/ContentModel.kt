package com.project.app.Objects

import androidx.annotation.NonNull
import androidx.room.Entity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.project.app.Paging.Displayable


/**
 * Storing the different content in different Tables leads to serious problems.
 * ->In the individual DAOs DataSourceFactories are returned with the specific type
 * -> Even if they have to same superclass its not possible to merge these DataSourceFactories without much work
 * -> Since all the Boundary Callback stuff is also directly connected into the DSF one would need to rework this entire system
 * -> Easy fix-> 1 Model for ALL classes
 */

@Entity(
    primaryKeys = ["id","contentname","type"],
    tableName = "content"
)
data class ContentModel(
    /**
     * GENERAL
     */
    @SerializedName("id")
    @NonNull
    val id: String,
    @SerializedName("contentname")
    val contentname: String,
    @SerializedName("type") //Prevent ID duplicates
    val type: String,
    @SerializedName("index")
    val index: String,



    /**
     * TOPIC
     */
    @SerializedName("t_name")
    val t_name: String = "-1",
    @SerializedName("t_parent")
    val t_parent: String = "-2",
    @SerializedName("t_color")
    val t_color: String = "#000000",


    /**
     * USER
     */

    @SerializedName("u_name")
    val u_name: String? = null,
    @SerializedName("u_desc")
    val u_desc: String? = null,
    @SerializedName("u_followercount")
    val u_followercount: Long? = null,
    @SerializedName("u_location")
    val u_location: String? = null,
    @SerializedName("u_timestamp")
    val u_timestamp: String? = null,
    @SerializedName("u_following")
    val u_subscriptions: Array<String> = emptyArray(), //Has to be a primitiv type
    @SerializedName("u_url")
    val u_url: String? = null,
    @SerializedName("u_avatar")
    val u_avatar: String? = null,
    @SerializedName("u_reputation")
    val u_reputation: Long? = null,


    /**
     * QUESTION
     */
    @SerializedName("q_text")
    val q_text: String?=null,
    @SerializedName("q_userid")
    val q_userid: User?=null,
    @SerializedName("q_timestamp")
    val q_timestamp: String?=null,
    @SerializedName("q_answers")
    val q_answers: Array<String> = emptyArray(),
    @SerializedName("q_tags")
    val q_tags: Topic?=null,
    @SerializedName("q_votes")
    val q_votes: Array<Int> = emptyArray(),
    @SerializedName("q_context")
    val q_context: String?=null,
    @SerializedName("q_updated")
    val q_updated: String?=null,
    @SerializedName("q_expirationdate")
    val q_expirationDate: String?=null,
    @SerializedName("q_scoreOverall")
    val q_scoreOverall: Long=0L,
    @SerializedName("q_rankOverall")
    val q_rankOverall: Long=0L,
    @SerializedName("q_rankCategory")
    val q_rankCategory: Long=0L,
    @SerializedName("q_flag")
    val q_flag: Int?=null
){

    fun compareTo(other: ContentModel): Int {

        if (this.type != other.type) return 0
        if (this.contentname != other.contentname) return 1
        if (this.id != other.id) return 2

        if (this.u_name != other.u_name) return 3
        if (this.u_desc != other.u_desc) return 4
        if (this.u_avatar != other.u_avatar) return 5
        if (this.u_location != other.u_location) return 6
        if (this.u_url != other.u_url) return 7
        if (this.u_timestamp != other.u_timestamp) return 8
        if (this.u_reputation != other.u_reputation) return 9
        if (this.u_followercount != other.u_followercount) return 10
        if (!this.u_subscriptions.contentEquals(other.u_subscriptions)) return 11

        if (this.q_text != other.q_text) return 12
        if (!(this.q_answers contentEquals  other.q_answers)) return 13
        if (!(this.q_votes contentEquals  other.q_votes)) return 14
        if (this.q_flag != other.q_flag) return 15
        if (this.q_context != other.q_context) return 16
        if (this.q_expirationDate != other.q_expirationDate) return 17
        if (this.q_timestamp != other.q_timestamp) return 18
        if (this.q_rankOverall != other.q_rankOverall) return 19
        if (this.q_scoreOverall != other.q_scoreOverall) return 20
        if (this.q_tags != other.q_tags) return 21
        if (this.q_userid != other.q_userid) return 22
        if (this.q_updated != other.q_updated) return 23


        if (this.t_name != other.t_name) return 24
        if (this.t_color != other.t_color) return 25
        if (this.t_parent != other.t_parent) return 24







        return 10

    }

    companion object{
        fun convertDisplayableList(content:List<Displayable>):List<ContentModel>{
            val list=ArrayList<ContentModel>()
            for (displayable in content) {

                convertDisplayable(displayable)?.let {
                    list.add(it)
                }
            }
            return list
        }
        
        private fun convertDisplayable(displayable: Displayable):ContentModel?{

            when(displayable.type){

                Displayable.QUESTION ->{
                    val q:Question?=displayable.getQuestion()

                    q?.let {
                        q.tags?.data="-1" //To compare with correct data, see ContentLoader CustomStoreManager
                        return ContentModel(id=displayable.id,contentname = displayable.contentname,index = displayable.index,type = displayable.type,q_answers =q.answers,q_expirationDate = q.expirationDate,q_context = q.context,q_flag = q.flag,q_rankCategory = q.rankCategory,q_rankOverall = q.rankOverall,q_scoreOverall = q.scoreOverall,q_tags = q.tags,q_text = q.text,q_timestamp = q.timestamp,q_updated = q.updated,q_userid = q.userid,q_votes = q.votes )
                    }

                }
                Displayable.USER ->{
                    val q:User?=displayable.getUser()
                    q?.let {
                        return ContentModel(id=displayable.id,contentname = displayable.contentname,index = displayable.index,type = displayable.type,u_avatar = q.avatar,u_desc = q.desc,u_followercount = q.followercount,u_subscriptions = convertSubscriptions(q.subscriptions),u_location = q.location,u_name = q.name,u_reputation = q.reputation,u_timestamp = q.timestamp,u_url = q.url)
                    }
                }
                Displayable.TOPIC ->{
                    val q:Topic?=displayable.getTopic()
                    q?.let {
                        return ContentModel(id=displayable.id,contentname = displayable.contentname,index = displayable.index,type = displayable.type,t_color = q.color,t_name = q.name,t_parent = q.parent)
                    }

                }
            }
           return null
        }
        fun convertToDisplayable(list: ArrayList<ContentModel>): List<Displayable> {
            return list.map { ContentModel.toDisplayable(it) }.filterNotNull()
        }

        /**
         * Only because room does not allow objects in query, problem on UpdateSingle Query and subscription
         */
        fun convertSubscriptions(input: Array<Subscription>?):Array<String>{
            val gson=Gson() //TODO PErformance?
           return input?.mapNotNull { gson.toJson(it) }?.toTypedArray()?: emptyArray()
        }
        fun convertSubscriptionsBack(input: Array<String>?):Array<Subscription>{
            val gson=Gson() //TODO PErformance?
            val listType = object : TypeToken<Subscription?>() {
            }.type

            return input?.mapNotNull {gson.fromJson(it,listType) as  Subscription? }?.toTypedArray()?: emptyArray()
        }

        fun toDisplayable(qm:ContentModel?):Displayable?{
            if(qm==null)return null
           var data:Any?=null
            when(qm.type) {
                Displayable.QUESTION -> { data=Question(qm)
                }
                Displayable.USER -> { data=User(qm)
                }
                Displayable.TOPIC -> { data=Topic()
                }
            }



            return Displayable(id=qm.id,index = qm.index,contentname = qm.contentname,type = qm.type,data=data,indextype = Displayable.getIndexType(qm.contentname))

        }


        
    }




}

