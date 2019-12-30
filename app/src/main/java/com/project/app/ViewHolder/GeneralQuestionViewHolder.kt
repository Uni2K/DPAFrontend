package com.project.app.ViewHolder

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.project.app.Bases.LocalBase
import com.project.app.CustomViews.PresetAnswer
import com.project.app.Helpers.Constants
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Interfaces.QuestionController
import com.project.app.Objects.Question
import com.project.app.Paging.Displayable
import com.project.app.R
import kotlin.math.roundToInt

class GeneralQuestionViewHolder(
    v: View,
    localBase_: LocalBase,
    questionController_: QuestionController
) : QuestionViewHolder(v, localBase_, questionController_) {

    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()
    private var answer1: PresetAnswer = v.findViewById(R.id.answer1)
    private var answer2: PresetAnswer = v.findViewById(R.id.answer2)
    private var answer3: PresetAnswer = v.findViewById(R.id.answer3)
    private var answer4: PresetAnswer = v.findViewById(R.id.answer4)
    private val answer_background: View = v.findViewById(R.id.adap_background_answer)
    private var type: Int = Constants.QUESTION_TYPE_TXT_GENERAL
    private val answerArray: Array<PresetAnswer?> = Array(4) { answer1 }

    override fun updateVotes(qst: Question?) {
        super.updateVotes(qst)
        for (answer in answerArray) {
            answer?.updateVote(percentages[answerArray.indexOf(answer)])
        }
    }


    fun bind(question_: Question?) {
      //  Log.d("onBindViewHolder","DATA: $question_   $")

        if (question_ != null) {
            question = question_
        } else {
            showQuestionError()
            return
        }
        type = question.let { Question.getQuestionType(it) }
        super.bind(question, true)
        populateAnswers(v, question)


    }

    private fun showQuestionError() {

    }

    private fun hideAnswers() {
        answer1.visibility = View.GONE
        answer2.visibility = View.GONE
        answer3.visibility = View.GONE
        answer4.visibility = View.GONE

    }

    /**
     * RelativeLayout doesnt provide the feature to wrap a view, so the background will stay fixed -> manual solve this
     */
    private fun calculateBackgroundHeight(numberOfAnswers: Int) {
        var between = 12.dpToPx(answer1.context.resources.displayMetrics) * (numberOfAnswers - 1)
        var bottom = 6.dpToPx(answer1.context.resources.displayMetrics)
        var top = 6.dpToPx(answer1.context.resources.displayMetrics)
        var answersHeight =
            answer1.context.resources.getDimension(R.dimen.height_answer) * numberOfAnswers
        var height = between + top + bottom + answersHeight
        answer_background.layoutParams.height = height.roundToInt()

    }


    private fun showAnswers() {

        hideAnswers()

        when (type) {
            Constants.QUESTION_TYPE_TXT2 -> {
                answer1.visibility = View.VISIBLE
                answer2.visibility = View.VISIBLE
                answer3.visibility = View.GONE
                answer4.visibility = View.GONE

            }
            Constants.QUESTION_TYPE_TXT3 -> {
                answer1.visibility = View.VISIBLE
                answer2.visibility = View.VISIBLE
                answer3.visibility = View.VISIBLE
                answer4.visibility = View.GONE


            }
            Constants.QUESTION_TYPE_TXT4_LONG -> {
                answer1.visibility = View.VISIBLE
                answer2.visibility = View.VISIBLE
                answer3.visibility = View.VISIBLE
                answer4.visibility = View.VISIBLE

            }
            Constants.QUESTION_TYPE_TXT4_SHRT -> {
                answer1.visibility = View.VISIBLE
                answer2.visibility = View.VISIBLE
                answer3.visibility = View.VISIBLE
                answer4.visibility = View.VISIBLE


            }
        }

        // for (answer in question.answers) {
        //   answerArray[question.answers.indexOf(answer)]?.visibility = View.VISIBLE
        //}

    }

    override fun toggleAnswers(expander: ImageView) {


        if (answer_background.visibility == View.VISIBLE) {
            answer_background.visibility = View.GONE
            hideAnswers()
            expander.setImageResource(R.drawable.baseline_expand_more_24)

        } else {
            answer_background.visibility = View.VISIBLE
            showAnswers()
            expander.setImageResource(R.drawable.baseline_expand_less_24)

        }


    }

    fun newInstance(lifecycleOwner: LifecycleOwner?): GeneralQuestionViewHolder {
        super.init(lifecycleOwner)
        return this
    }

    init {
        answerArray[0] = answer1
        answerArray[1] = answer2
        answerArray[2] = answer3
        answerArray[3] = answer4
    }


    private fun populateAnswers(v: View, qst: Question) {
        lifeCycleOwner?.let { expired.removeObservers(it) }

        for (answer in answerArray) {
            answer?.showStatLabel = true
        }
        calculateBackgroundHeight(qst.answers.size)
        val context: Context = v.context
        var alwaysVisible = true
        when (type) {
            Constants.QUESTION_TYPE_TXT2 -> alwaysVisible = true
            Constants.QUESTION_TYPE_TXT3 -> alwaysVisible = false
            Constants.QUESTION_TYPE_TXT4_SHRT -> {
                alwaysVisible = true
                for (answer in answerArray) {
                    answer?.showStatLabel = false
                }
            }
            Constants.QUESTION_TYPE_TXT4_LONG -> alwaysVisible = false

        }


        if (alwaysVisible) {
            answer_background.visibility = View.GONE
            showAnswers()
            expander.visibility = View.GONE
        } else {
            answer_background.visibility = View.GONE
            hideAnswers()
            expander.visibility = View.VISIBLE

        }

        for (i in qst.answers.indices step 1) {
            answerArray[i]?.setText(qst.answers[i])
            answerArray[i]?.setOnClickListener {
                if (expired.value!!) return@setOnClickListener
                (it as PresetAnswer).showLoading()

                if (localBase.getVote(qst.id) == null) {
                    questionController.questionAnswered(
                        answerArray.indexOf(it),
                        qst,
                        object : RetrofitHelper.Companion.DisplayableListCallback {
                            override fun onResponse(content: List<Displayable>) {
                                it.hideLoading()
                                displayDetailMode(answerArray, answerArray.indexOf(it), true)
                            }


                            override fun onError(s: Int) {
                                it.hideLoading()
                                //TODO VOTING ERROR
                                Toast.makeText(context, "Voting Error", Toast.LENGTH_LONG).show()
                            }
                        })
                    //  displayDetailMode(answerArray, answerArray.indexOf(it), true)
                } else {
                    Toast.makeText(
                        itemView.context,
                        "ALREADY VOTED FOR DATABASE: " + qst.id,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        val update = localBase.getVote(qst.id)

        if (expired.value!!) {

            displayDetailMode(answerArray, -1, false)
        } else {
            if (update != null) {
                displayDetailMode(answerArray, update.voteon, false)
            } else {

                answer1.setIdleMode()
                answer2.setIdleMode()
                answer3.setIdleMode()
                answer4.setIdleMode()

            }
        }

        if (expired.value == false) {
            if (lifeCycleOwner == null) {
                expired.observeForever(Observer {

                    if (it)
                        displayDetailMode(answerArray, -1, true)
                })
            } else {
                lifeCycleOwner?.let {
                    expired.observe(it, Observer { b ->
                        if (b)
                            displayDetailMode(answerArray, -1, true)
                    })
                }
            }
        }


    }


    private fun displayDetailMode(
        array: Array<PresetAnswer?>,
        voteon: Int,
        clicked: Boolean = false
    ) {
        //  Log.i("setStatsEnabled","percentages: ${Arrays.toString(percentages)}    ${question._id}    ${Arrays.toString(question.votes)}")

        if (voteon == -1) {
            //Expired
            for (answer in array) {
                answer?.setStatsEnabled(percentages[array.indexOf(answer)], clicked)
            }
        } else {
            for (answer in array) {
                if (array.indexOf(answer) == voteon) {
                    answer?.setSelectedMode(percentages[array.indexOf(answer)], clicked)
                } else {
                    answer?.setStatsEnabled(percentages[array.indexOf(answer)], clicked)

                }
            }
        }
    }

    fun updateAnswers(item: Question?) {
        bind(item)
    }


}