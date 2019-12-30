package com.project.app.Objects

import android.os.Bundle
import com.project.app.Helpers.Constants
import com.project.app.Paging.Displayable

data class Question(
    val _id:String,
    var text: String? = null,
    val userid: User? = null,
    val timestamp: String? = null,
    var answers:Array<String> = emptyArray(),
    var tags: Topic? = null,
    val votes:Array<Int> = emptyArray(),
    var context: String? = null,
    val updated: String? = null,
    var expirationDate: String? = null,
    val scoreOverall: Long = 0L,
    val rankCategory: Long = 0,
    val rankOverall: Long = 0,
    val flag: Int? = null


) : Displayable(type=Displayable.QUESTION,data=this) {




    /**
     * Wenn Text und Antworten sich ändern, wird das nur als Textänderung erkannt
     */
     fun compareTo(other: Question?): Int {
        if(other==null)return 10
        if (this.id != other.id) return 0
        if (this.text != other.text) return 1
        if (!(this.answers contentEquals other.answers)) return 4
        if (!(this.votes contentEquals other.votes)) return 5
        if (this.userid != other.userid) return 6
        if (this.timestamp != other.timestamp) return 7
        if (this.context != other.context) return 8
        if (this.tags?._id   != other.tags?._id) return 9
        if (this.expirationDate != other.expirationDate) return 12
        if (this.rankCategory != other.rankCategory) return 13
        if (this.rankOverall != other.rankOverall) return 14
        if (this.scoreOverall != other.scoreOverall) return 15
        if (this.flag != other.flag) return 16

        //Everything the same
        return 10

    }


   


    constructor(
        q: ContentModel
    ):this( _id=q.id,answers = q.q_answers,context = q.q_context,text=q.q_text,userid=q.q_userid,
        timestamp=q.q_timestamp,updated=q.q_updated,expirationDate=q.q_expirationDate, tags=q.q_tags ,votes=q.q_votes,
        scoreOverall=q.q_scoreOverall, rankOverall=q.q_rankOverall, rankCategory=q.q_rankCategory, flag=q.q_flag){
        index=q.index
        contentname=q.contentname
        id=q.id
    }





    fun getChangePayload(questionOld: Question, questionNew: Question): Bundle {
        val diff = Bundle()

        if (questionNew.text != questionOld.text) {
            diff.putString("text", questionNew.text)
        }
        if (questionNew.context != questionOld.context) {
            diff.putString("context", questionNew.context)
        }
        if (!(questionNew.answers contentEquals questionOld.answers)) {
            diff.putStringArray("answers", questionNew.answers)
        }

        if (!(questionNew.votes contentEquals questionOld.votes)) {
            diff.putIntArray("votes", questionNew.votes.toIntArray())

        }
        if (questionNew.expirationDate != questionOld.expirationDate) {
            diff.putString("expirationdate", questionNew.expirationDate)

        }
        if (questionNew.userid != questionOld.userid) {
            diff.putString("userid", "ignored")
        }

        if (questionNew.tags != questionOld.tags) {
            diff.putString("tags","ignored")
        }

        if (questionNew.timestamp != questionOld.timestamp) {
            diff.putString("timestamp", questionNew.timestamp)
        }
        if (diff.size() == 0) {
            //ALL EQUAL
            diff.putBoolean("allEqual", true)
        }
        return diff

    }







     companion object {
         //fun createSampleQuestion(): Question {
         //    return Question()
          /*   var arraySize = Random().nextInt(2)
             if (arraySize < 0 || arraySize > 4) arraySize = 2
             val randomTags=ArrayList<Topic>()
             randomTags.add(Topic("y10y3"))
             randomTags.add(Topic("y13y1"))
             randomTags.add(Topic("y14y1"))
             randomTags.add(Topic("y7y1"))
             randomTags.add(Topic("y6y1"))
             randomTags.add(Topic("y12y1"))
             randomTags.shuffle()
             return Question(
                 "sdsdsss",
                 "Wie geht es den Herrschaften denn heute?",
                 "5d8f51688021fb20ac4842b6",
                 Date().time.toString(),
                 true,
                 Array<String>(2 + arraySize) { "Gut Gut" },
                 Array<Int>(2 + arraySize) { 50 },
                 randomTags[0]
                 ,
                 "Germany",
                 "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
                 ,

                 Date().time.toString(),
                 (Date().time.toString().toLong() + 50000).toString()
             )*/
      //   }

         fun getQuestionType(qst: Question): Int {
             val answerSize = qst.answers.size
             if (answerSize == 2) return Constants.QUESTION_TYPE_TXT2
             if (answerSize == 3) return Constants.QUESTION_TYPE_TXT3
             if (answerSize == 4) return Constants.QUESTION_TYPE_TXT4_LONG

             return Constants.QUESTION_TYPE_TXT2
         }


     }
}