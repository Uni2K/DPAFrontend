package com.project.app.Slides

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.app.Fragments.QuestionControllerFragment
import com.project.app.Fragments.QuestionCreator
import com.project.app.R

//TEXT SLIDE
class CreateSlideText: QuestionControllerFragment() {


   lateinit var creatorParent: QuestionCreator

    companion object{
        private const val maxLength=50
        private const val minLength=10
        const val errorText="Insert more characters"

        fun isValid(s:String?):Boolean{
            if(s==null)return false
            if(s.toString().length in minLength..maxLength){
                return true
            }
            return false
        }
    }

   fun setController(questionCreator: QuestionCreator){
       this.creatorParent=questionCreator
   }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_create_slide0, container, false)

        val questionInput=v.findViewById<TextInputEditText>(R.id.c_question)
        val questionInputLayout=v.findViewById<TextInputLayout>(R.id.c_question_layout)
        questionInputLayout.error = errorText
        questionInputLayout.isErrorEnabled = true


        val related: RecyclerView= v.findViewById(R.id.c_recycler_related)
        related.setHasFixedSize(true)
        related.layoutManager=LinearLayoutManager(context,RecyclerView.VERTICAL,false)

        questionInputLayout.setEndIconOnClickListener {
           creatorParent.next()

        }

        questionInput.setOnKeyListener(object: View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if ((event?.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    creatorParent.next()
                    return true
                }
                return false;            }
        })
        questionInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
               creatorParent.onChangeText(s.toString())
                questionInputLayout.isErrorEnabled = !isValid(s.toString())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        return v
    }
}