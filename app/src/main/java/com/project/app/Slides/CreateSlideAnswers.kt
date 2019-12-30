package com.project.app.Slides

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.app.Fragments.QuestionCreator
import com.project.app.Objects.Question
import com.project.app.R

class CreateSlideAnswers : Fragment() {


    lateinit var question: MutableLiveData<Question>
    private lateinit var creatorParent: QuestionCreator
    lateinit var scrollview: NestedScrollView
    lateinit var answerTextContainer: LinearLayout
    lateinit var answerPictureContainer: LinearLayout

    private lateinit var infoText1: TextView
    private lateinit var infoBack: View

    lateinit var addTextAnswerButton: ImageView
    lateinit var viewFlipper: ViewFlipper
    private var answerMode_: Int = 0

    lateinit var next: ImageView
    private lateinit var gallery: ImageView
    private lateinit var camera: ImageView
    lateinit var text: ImageView

    companion object {
        private const val maxLength = 50
        const val maxTextAnswers = 4
        private const val minLength = 1


        fun isValidFullText(s: Array<String>): Int {
            var allEmpty=true
            for(string in s){
                if(string.isNotBlank())allEmpty=false
            }
            if(allEmpty)return 999
            for (i in s.indices step 1) {

                if (i<2 && !isValidText(s[i])) {
                    return i + 100
                }else if(i>2 && s[i]!="" && !isValidText(s[i])){
                    return i + 100

                }
            }
            return 0

        }

        fun isValidText(s: Array<String>?): Boolean {
            if(s==null)return false
            return ((isValidText(s[0]))&&(isValidText(s[1])))
        }

        fun isValidText(strin: String): Boolean {
            return strin.length in minLength..maxLength


        }
    }

    fun setController(questionCreator: QuestionCreator){
        this.creatorParent=questionCreator
    }


    private fun changeInfoStyleDone() {
        val drawable =
            context?.let { ContextCompat.getDrawable(it, R.drawable.baseline_done_24) }
        infoText1.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        context?.getColor(R.color.conditionDone)?.let {
            infoText1.setBackgroundColor(
                it
            )
            infoBack.setBackgroundColor(
                it
            )
        }
    }

    private fun changeInfoStyleIdle() {
        val drawable =
            context?.let { ContextCompat.getDrawable(it, R.drawable.baseline_info_24) }
        infoText1.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        context?.getColor(R.color.hintBackgroundColor)?.let {
            infoText1.setBackgroundColor(
                it
            )
            infoBack.setBackgroundColor(
                it
            )
        }
    }

    private fun changeInfoStyleNotDone() {
        val drawable =
            context?.let { ContextCompat.getDrawable(it, R.drawable.baseline_clear_24) }
        infoText1.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        context?.getColor(R.color.conditionNotDone)?.let {
            infoText1.setBackgroundColor(
                it
            )
            infoBack.setBackgroundColor(
                it
            )
        }
    }


    private fun setUpInfoStatus(question: Question?) {
        val answerArray = question?.answers ?: return
        var infoText = context?.getString(R.string.default_info_answer_both)
        when (val x = isValidFullText(answerArray)) {

            999->{
                changeInfoStyleIdle()
            }


            in 102..998 -> {
                infoText = "Answer " + (x - 99) + " is not valid!"

                changeInfoStyleIdle()

            }
            in 100..101 -> {
                infoText = context?.getString(R.string.default_info_answer_single).toString()

                changeInfoStyleNotDone()

            }
            0 -> {
                infoText = context?.getText(R.string.default_info_done).toString()
                changeInfoStyleDone()
            }
        }

        infoText1.text = infoText


    }

    private fun createTextAnswer(index: Int): ConstraintLayout {
        val layoutParent: ConstraintLayout =
            LayoutInflater.from(context).inflate(
                R.layout.add_answertext,
                null,
                false
            ) as ConstraintLayout

        val layout = layoutParent.findViewById<TextInputLayout>(R.id.textInputLayout)
        val edit: TextInputEditText = layout.findViewById(R.id.edit) as TextInputEditText
        val remove = layoutParent.findViewById<ImageView>(R.id.remove)
        layout.isErrorEnabled = true
        layout.hint = "Answer " + (index + 1)
        edit.hint = "Answer " + (index + 1)
        edit.tag = index
        layout.tag = index
        layout.error = CreateSlideText.errorText
        if (index > 1) remove.visibility = View.VISIBLE
        else remove.visibility = View.INVISIBLE

        edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onTextChangedFull(layout, p0, p1, p2, p3)
                layout.error = CreateSlideText.errorText
                layout.isErrorEnabled = !isValidText(p0.toString())

            }
        })
        if (index > 1) {
            remove.setOnClickListener {
                (layoutParent.parent as ViewGroup).removeView(layoutParent)
                answerTextCountChanged()
            }
        }
        return layoutParent
    }

    private fun createPictureAnswer(index: Int): ConstraintLayout {
        val layoutParent: ConstraintLayout =
            LayoutInflater.from(context).inflate(
                R.layout.add_answerpicture, null, false
            ) as ConstraintLayout

        val content = layoutParent.findViewById<ImageView>(R.id.c_answer_picture_content)
        content.setOnClickListener {
            addImage(content)
        }
        content.tag = index
        layoutParent.tag = index
        return layoutParent
    }

    private fun addImage(content: ImageView?) {

    }


    private fun answerTextCountChanged() {
        scrollview.post(Runnable {
            if (answerTextContainer.childCount > 2)
                scrollview.fullScroll(ScrollView.FOCUS_DOWN)
            else
                scrollview.fullScroll(ScrollView.FOCUS_UP)

        })
        updateTextAddButton()
    }

    private fun updateTextAddButton() {

        if (answerTextContainer.childCount < maxTextAnswers && answerMode_ == 0) {
            addTextAnswerButton.visibility = View.VISIBLE
        } else {
            addTextAnswerButton.visibility = View.GONE

        }
    }

    fun onTextChangedFull(layout: TextInputLayout, p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        var answerArray = question.value?.answers
        if (answerArray == null)
            answerArray = arrayOf()

        answerArray[layout.tag.toString().toInt()] = p0.toString()
        creatorParent.onChangeQuestionAnswer(answerArray)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_create_slide1, container, false)


        answerTextContainer = v.findViewById<LinearLayout>(R.id.container_textanswers)
        answerPictureContainer = v.findViewById<LinearLayout>(R.id.container_pictureanswers)

        answerTextContainer.addView(createTextAnswer(0))
        answerTextContainer.addView(createTextAnswer(1))

        answerPictureContainer.addView(createPictureAnswer(0))
        answerPictureContainer.addView(createPictureAnswer(1))

        infoText1 = v.findViewById(R.id.info_text)
        infoBack = v.findViewById(R.id.info_background)

        scrollview = v.findViewById(R.id.c_scrollview)
        addTextAnswerButton = v.findViewById(R.id.c_add_answer)


        addTextAnswerButton.setOnClickListener {

            val size = answerTextContainer.childCount
            if (size == maxTextAnswers) return@setOnClickListener
            answerTextContainer.addView(createTextAnswer(size))
            answerTextCountChanged()


        }
        viewFlipper = v.findViewById(R.id.c_viewflipper_answers)
        setUpButtonBar(v)
        changeInfoStyleIdle()

        return v
    }



    private fun setUpButtonBar(view: View) {
        next = view.findViewById<ImageView>(R.id.c_answers_next)
        gallery = view.findViewById<ImageView>(R.id.c_answers_gallery)
        camera = view.findViewById<ImageView>(R.id.c_answers_photo)
        text = view.findViewById<ImageView>(R.id.c_answers_text)
        next.visibility=View.GONE


        text.setOnClickListener {
            setAnswerMode(0)
        }
        gallery.setOnClickListener {
            setAnswerMode(1)
        }
        camera.setOnClickListener {
            setAnswerMode(2)
        }
        next.setOnClickListener {
            creatorParent.next()
        }


    }

    private fun setAnswerMode(mode: Int) {

        val cActivated =
            context?.getColor(R.color.secondaryColor)?.let { ColorStateList.valueOf(it) }
        val cDeactivated =
            context?.getColor(R.color.primaryOppositeColor)?.let { ColorStateList.valueOf(it) }


        this.answerMode_ = mode
        when (mode) {
            0 -> {
                //TEXTMODE
                camera.imageTintList = cDeactivated
                gallery.imageTintList = cDeactivated
                text.imageTintList = cActivated

                viewFlipper.displayedChild = 0
            }
            1 -> {
                camera.imageTintList = cDeactivated
                gallery.imageTintList = cActivated
                text.imageTintList = cDeactivated
                viewFlipper.displayedChild = 1

            }
            2 -> {
                camera.imageTintList = cActivated
                gallery.imageTintList = cDeactivated
                text.imageTintList = cDeactivated
                viewFlipper.displayedChild = 1

            }
        }

        updateTextAddButton()

    }


}