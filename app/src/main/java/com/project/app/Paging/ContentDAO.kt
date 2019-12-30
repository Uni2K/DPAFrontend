package com.project.app.Paging


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.google.gson.Gson
import com.project.app.Objects.ContentModel
import com.project.app.Objects.Subscription
import com.project.app.Objects.Topic
import com.project.app.Objects.User

@Dao
interface ContentDAO {


    @Delete
    fun deleteContent(person: List<ContentModel>):Int

    @Query("delete from content where id in (:questionIDs) and type is :type")
     fun deleteContentByIDs(questionIDs: List<String>,type:String):Int

    @Query("delete from content where q_userid in (:userIDs) and type is :type")
     fun deleteContentByUserIDs(userIDs: List<String>,type:String):Int

    @Query("delete from content where q_userid not in (:userIDs) and q_tags not in (:topicIDs) and type is :type")
     fun deleteSubscribedUserContent(userIDs: List<String>,topicIDs:List<Topic>,type:String):Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(post : ContentModel):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(post : List<ContentModel>):List<Long>


    @Query("SELECT * FROM content WHERE contentName LIKE '%' || :contentName || '%'  ORDER BY `index` DESC " )
     fun contentByContentName(contentName : String) : DataSource.Factory<Int, ContentModel>


    @Query("SELECT id FROM content WHERE contentName LIKE  '%' || :contentName || '%' and type=:type")
      fun idsByContentName(contentName : String,type:String) :List<String>

    @Query("SELECT DISTINCT id FROM content")
      fun getAvailableIDs() :List<String>



    @Query("SELECT * FROM content  WHERE id = :id ")
     fun questionByID(id : String) : ContentModel


    @Query("DELETE FROM content WHERE contentname LIKE '%' || :contentName || '%'" )
      fun deleteByContentName(contentName: String):Int

    /**
     * Checks by primary Key -> HAS TO BE ID ->>>>>>>>>>>PROBLEM -> loop through all -> probably slow af
     */
    //@Query("UPDATE questions SET votes  WHERE contentName LIKE '%' || :contentName || '%'" )
    @Transaction
     fun updateData(body: List<ContentModel>){

        for(qm in body){
            updateSingle(
                qm.id,
                qm.type,
                qm.u_name,
                qm.u_desc,
                qm.u_followercount,
                qm.u_location,
                qm.u_timestamp,
                qm.u_subscriptions.joinToString(),
                qm.u_url,
                qm.u_avatar,
                qm.u_reputation,
                qm.q_text,
                qm.q_userid,
                qm.q_timestamp,
                qm.q_answers.joinToString(),
                qm.q_tags,
                qm.q_votes.joinToString(),
                qm.q_context,
                qm.q_updated,
                qm.q_expirationDate,
                qm.q_scoreOverall,
                qm.q_rankOverall,
                qm.q_rankCategory,
                qm.q_flag
            )

        }
    }






    @Query("UPDATE content SET q_rankOverall=:q_rankOverall,q_rankCategory=:q_rankCategory,q_scoreOverall=:q_scoreOverall,q_rankCategory=:q_rankCategory,q_flag=:q_flag,q_expirationDate=:q_expirationDate,q_text=:q_text,q_userid=:q_userid,q_timestamp=:q_timestamp,q_answers=:q_answers,q_votes=:q_votes,q_tags=:q_tags,q_context=:q_context,q_timestamp=:q_timestamp,q_updated=:q_updated,q_userid=:q_userid,u_name=:u_name,u_desc=:u_desc,u_followercount=:u_followercount,u_location=:u_location,u_timestamp=:u_timestamp,u_subscriptions=:u_subscription, u_followercount=:u_followercount, u_reputation=:u_reputation, u_avatar=:u_avatar,u_url=:u_url WHERE id=:id and type=:type" )
      fun updateSingle(
        id: String? = null,
        type: String? = null,

        /**
         * USER
         */

         u_name: String? = null,
        u_desc: String? = null,
        u_followercount: Long? = null,
        u_location: String? = null,
        u_timestamp: String? = null,
        u_subscription: String?=null,
        u_url: String? = null,
        u_avatar: String? = null,
        u_reputation: Long? = null,


        /**
         * QUESTION
         */
         q_text: String?=null,
        q_userid: User?=null,
        q_timestamp: String?=null,
        q_answers: String?=null,
        q_tags: Topic?=null,
        q_votes: String?=null,
        q_context: String?=null,
        q_updated: String?=null,
        q_expirationDate: String?=null,
        q_scoreOverall: Long?=null,
        q_rankOverall: Long?=null,
        q_rankCategory: Long?=null,
        q_flag: Int?=null):Int

    @Update
      fun update(body: List<ContentModel>):Int

    @Update
      fun update(body: ContentModel):Int


    @Query("SELECT MAX(q_timestamp) FROM content WHERE contentName LIKE '%' || :contentName || '%'")
      fun getLatest(contentName: String): String

    @Query("SELECT * FROM content WHERE id = :id and type=:type")
      fun getQuestionLiveData(id: String,type: String): LiveData<ContentModel>

    @Query("SELECT id FROM content WHERE contentname = :content")
    fun doesContentExist(content: String): String?




    /**
     * Either update or insert will be longer than default
     * -> QuestionID Primary Key: No duplicates -> problem: info about content has to be updated -> slow Inserting when existing
     * -> QuestionID not primary key: Duplicates -> problem: Update difficult since different ids/same questionID have to be updated -> not that slow -> USE THAT
     */
   /* @Transaction //NOT USED
     fun insertData(posts : List<ContentModel>, contentName: String) {
        val start=System.currentTimeMillis()
        for( qm in posts){
            val id=insert(qm)

            if (id == -1L) {
                val model= questionByID(qm.questionid)
                if(!model.contentname.contains(contentName,true)){
                    //ADD CONTENTNAME TO FIELD STRING
                    model.contentname=model.contentname+contentName
                    update(model)
                    Log.e("not inserted not cont","ID: ${qm.questionid}         $contentName")

                }else
                Log.e("not inserted contains","ID: ${qm.questionid}         $contentName")

            }else{
                Log.e("inserted","ID $id $contentName")

            }
        }
        Log.e("insertData","TIME: ${System.currentTimeMillis()-start}")



    }*/



}