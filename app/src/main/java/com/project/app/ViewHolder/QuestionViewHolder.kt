package com.project.app.ViewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.project.app.Bases.LocalBase
import com.project.app.Bases.TextBase
import com.project.app.Bases.TextBase.Companion.updateExpirationDate
import com.project.app.Interfaces.QuestionController
import com.project.app.Objects.Question
import com.project.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


open class QuestionViewHolder(
    val v: View, val localBase: LocalBase, val questionController: QuestionController
) : RecyclerView.ViewHolder(v) {

    var lifeCycleOwner: LifecycleOwner? = null
    var expired: MutableLiveData<Boolean> = MutableLiveData(false)
    var text: TextView = v.findViewById(R.id.adap_question)
    var timestamp: TextView = v.findViewById(R.id.adap_timestamp)
    var qcontext: TextView = v.findViewById(R.id.adap_context)
    var tagicon: ImageView = v.findViewById(R.id.adap_tagicon)

    var stat_1c: ImageView = v.findViewById(R.id.adap_stat_1c)
    var stat_2c: ImageView = v.findViewById(R.id.adap_stat_2c)
    var stat_3c: ImageView = v.findViewById(R.id.adap_stat_3c)
    var stat_1t: TextView = v.findViewById(R.id.adap_stat_1t)
    var stat_2t: TextView = v.findViewById(R.id.adap_stat_2t)
    var stat_3t: TextView = v.findViewById(R.id.adap_stat_3t)
    var stat_4t: TextView = v.findViewById(R.id.adap_stat_4t)
    val expander: ImageView = v.findViewById<ImageView>(R.id.expander)
    var allVotes = 0f
    var percentages = Array(4) { 0 }
    private var updateExpiration: Boolean = true
    lateinit var question: Question

    fun bind(qst: Question?, expanderEnabled: Boolean) {
        if (qst != null) {
            question = qst
        }
        allVotes = 0f
        percentages = Array(4) { 0 }
        if (qst != null) {
            for (f in qst.votes) {
                allVotes += f
            }
            for (l in qst.votes.indices) {
                percentages[l] = (((qst.votes[l]) / allVotes) * 100).roundToInt()

            }
        }

        if (qst != null) {
            populateItemRows(qst)
        }

        val expander = v.findViewById<ImageView>(R.id.expander)
        if (!expanderEnabled) {
            expander.visibility = View.GONE
        } else {
            expander.visibility = View.VISIBLE

        }

    }

    init {
        runExpirationLoop()
    }


    fun init(lifecycleOwner: LifecycleOwner?): QuestionViewHolder {
        this.lifeCycleOwner = lifecycleOwner
        expander.setOnClickListener {
            toggleAnswers(expander)
        }
        expander.setImageResource(R.drawable.baseline_expand_more_24)
        return this
    }

    open fun toggleAnswers(expander: ImageView) {

    }


    private fun populateItemRows(qst: Question) {
        //    setContext(qst.context)
        updateUser(qst)
        itemView.setOnClickListener {
            questionController.showFullScreen(qst)
        }
        tagicon.setOnClickListener {
            qst.userid?._id?.let { questionController.userClicked(it) }
        }
        val delay = updateExpirationDate(qst, timestamp)

        if (delay == -1L) setExpired()
        text.text = qst.id
        stat_3t.text = allVotes.roundToInt().toString()
        stat_2t.text = qst.timestamp


    }

    fun updateExpirationDate(qst: Question?){
        val delay = updateExpirationDate(qst, timestamp)
        if (delay == -1L) setExpired()
    }

    private fun setExpired() {

        this@QuestionViewHolder.expired.value = true
        this@QuestionViewHolder.expired.value = true
           updateExpiration = false

    }

    private fun setContext(context: String) {
        if (context == "-1" || context.isBlank()) {
            qcontext.text = v.context.resources.getString(R.string.default_questioncontext)
        } else {
            qcontext.text = context

        }

    }


    open fun updateVotes(qst: Question?) {
        allVotes = 0f
        percentages = Array(4) { 0 }
        if (qst != null) {
            for (f in qst.votes) {
                allVotes += f
            }
            for (l in qst.votes.indices) {
                percentages[l] = (((qst.votes[l]) / allVotes) * 100).roundToInt()

            }
        }
        stat_3t.text = allVotes.roundToInt().toString()

    }

    fun updateText(item: Question?) {
        item?.let { this.text.text = it.text }
    }

    fun updateContext(item: Question?) {
        item?.let { this.qcontext.text = it.context }
    }

    fun flashAnimation(vararg v: View) {
        for (view in v) {
            YoYo.with(Techniques.Flash).playOn(view)
        }
    }

    fun allEqual() {
        flashAnimation(stat_1c, stat_2c, stat_3c)
    }


    /**
     * Has to be as lightweight as possible
     * //TODO Runs in Background, no Lifecycle
     */
    private fun runExpirationLoop() {
        GlobalScope.launch {
            var delay = 5000L
            while (updateExpiration) {
                if (::question.isInitialized) {

                    val delayNew = updateExpirationDate(question, timestamp)

                    if (delayNew == -1L) {
                       launch(Dispatchers.Main) {  setExpired()}
                        return@launch
                    }
                    delay = delayNew
                }
                delay(delay)
            }
        }
    }


    fun updateUser(let: Question?) {
        let?.let { qst ->
            qst.userid?.let {
                TextBase.formatUserAvatar(it.avatar,tagicon)

                stat_4t.text = qst.userid.name ?: "Unknown"


            }
        }
    }


}
