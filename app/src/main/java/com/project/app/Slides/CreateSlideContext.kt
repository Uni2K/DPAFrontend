package com.project.app.Slides

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.app.Fragments.QuestionCreator
import com.project.app.R
import com.project.app.Slides.CreateSlideText.Companion.errorText
import xute.markdeditor.EditorControlBar
import xute.markdeditor.MarkDEditor
import xute.markdeditor.Styles.TextComponentStyle
import xute.markdeditor.utilities.TextListener

class CreateSlideContext: Fragment() {

    lateinit var creatorParent: QuestionCreator

    companion object{
        const val maxLength=300
        private const val minLength=10

        fun isValid(s:String?):Boolean{
            if(s==null)return false
            if(s.replace("\\n","").length in minLength..maxLength){
                return true
            }
            return false
        }
    }

    fun setController(questionCreator: QuestionCreator){
        this.creatorParent=questionCreator
    }


    private fun setUpInfoStatus(infoTextView:TextView,infoBack:View, markDEditor: MarkDEditor){
        markDEditor.setTextListener(object: TextListener {
            override fun onTextChange() {
                val text= markDEditor.markdownContent
                val removed=text.replace("\\n","")


                creatorParent.onChangeContext(text.toString())
                if(isValid(removed)){
                    val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.baseline_done_24) }
                   infoTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    context?.getColor(R.color.conditionDone)?.let {
                        infoTextView.setBackgroundColor(
                            it
                        )
                        infoBack.setBackgroundColor(
                            it
                        )
                    }
                    infoTextView.text=context?.getText(R.string.default_info_done)

                }else{
                    val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.baseline_info_24) }
                    infoTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    context?.getColor(R.color.hintBackgroundColor)?.let {
                        infoTextView.setBackgroundColor(
                            it
                        )
                        infoBack.setBackgroundColor(
                            it
                        )
                    }
                    infoTextView.text=context?.getText(R.string.default_info_context)

                }



            }

            override fun afterTextChanged() {
            }
        })

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_create_slide3, container, false)
        val questionInput=v.findViewById<TextInputEditText>(R.id.context)
        val questionInputLayout=v.findViewById<TextInputLayout>(R.id.contextlayout)
        val editor=v.findViewById<MarkDEditor>(R.id.mdEditor)
        val controls=v.findViewById<EditorControlBar>(R.id.controlBar)

        questionInputLayout.error = errorText
        questionInputLayout.isErrorEnabled = true
        questionInputLayout.counterMaxLength= maxLength
        editor.configureEditor(
            "",//server url for image upload
            "",              //serverToken
            false,           // isDraft: set true when you are loading draft
            "Enter some context...", //default hint of input box
            TextComponentStyle.NORMAL
        )
        controls.setEditor(editor)

        val info=v.findViewById<TextView>(R.id.info_text)
        val infoBack=v.findViewById<View>(R.id.info_background)
        setUpInfoStatus(info,infoBack, editor)


        questionInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                creatorParent.onChangeContext(s.toString())
                questionInputLayout.error = errorText
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