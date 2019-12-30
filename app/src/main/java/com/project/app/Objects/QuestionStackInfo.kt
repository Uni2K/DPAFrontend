package com.project.app.Objects

import android.graphics.Color
import com.project.app.Paging.Displayable

class QuestionStackInfo(arrayList:List<Displayable>) {

        val tagLimit=3 //How many tags stored
        var tagColors= ArrayList<Int>()
        var tagNames= ArrayList<String>()

    class QuestionStackInfo(arrayList: ArrayList<Question>){

    }
        init {
            for(q in arrayList){
                if(tagColors.size<=tagLimit){
                    if(q as? Question !=null)
                    tagColors.add(Color.parseColor(q.tags?.color))
                }
                if(tagNames.size<=tagLimit){
                    if(q as? Question !=null)
                    q.tags?.name?.let { tagNames.add(it) }
                }else{
                    break
                }
            }



        }






}