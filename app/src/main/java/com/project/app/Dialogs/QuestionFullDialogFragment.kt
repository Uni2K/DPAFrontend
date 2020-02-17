package com.project.app.Dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.Window.FEATURE_NO_TITLE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.gson.Gson
import com.project.app.Bases.StatisticsBase
import com.project.app.Bases.TextBase
import com.project.app.CustomViews.CustomMarkerView
import com.project.app.CustomViews.PresetAnswer
import com.project.app.Helpers.Constants
import com.project.app.Helpers.ErrorHandler
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Interfaces.SnapshotHelper
import com.project.app.Objects.ContentModel
import com.project.app.Objects.Question
import com.project.app.Objects.QuestionSnapshot
import com.project.app.Paging.Displayable
import com.project.app.R
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis


class QuestionFullDialogFragment : QuestionControllerDialogFragment() {


    lateinit var parentLayout: ConstraintLayout
    lateinit var question: TextView
    lateinit var context: TextView
    lateinit var timestamp: TextView
    lateinit var expiration: TextView

    lateinit var avatar: ImageView
    lateinit var username: TextView

    lateinit var stat1: TextView
    lateinit var stat2: TextView
    lateinit var voted: TextView

    lateinit var mainChart: LineChart
    lateinit var loader: View
    lateinit var toolbar:Toolbar
    private lateinit var answer1: PresetAnswer
    private lateinit var answer2: PresetAnswer
    private lateinit var answer3: PresetAnswer
    private lateinit var answer4: PresetAnswer
   // lateinit var answerStub: ViewStub
    lateinit var answerContainer: ViewGroup
    lateinit var temp_question: Question
    var allVotes = 0f
    var percentages = Array(4) { 0 }

    var expired: MutableLiveData<Boolean> = MutableLiveData(false)

    lateinit var  viewModel:MasterViewModel
    private var updateExpiration: Boolean = true
    private lateinit var errorHandler:ErrorHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val start = System.currentTimeMillis()

        val v = inflater.inflate(R.layout.fragment_fullview_new, container, false)
        parentLayout = v.findViewById(R.id.motion)
        question = v.findViewById(R.id.full_question)
        context = v.findViewById(R.id.full_context)
        expiration = v.findViewById(R.id.full_expiration)
        mainChart = v.findViewById(R.id.full_chart)
        stat1 = v.findViewById(R.id.full_stat1_t)
        stat2 = v.findViewById(R.id.full_stat2_t)
        timestamp = v.findViewById(R.id.full_time)
      //  answerStub = v.findViewById(R.id.answerStub)
        answerContainer = v.findViewById(R.id.answerContainer)
        loader = v.findViewById(R.id.full_progressbar)
        avatar = v.findViewById(R.id.full_avatar)
        username = v.findViewById(R.id.full_username)
        voted=v.findViewById(R.id.full_votes)
        viewModel=ViewModelProvider(activity).get(MasterViewModel::class.java)

        loader.visibility = View.VISIBLE
        Log.e("timeInflate", "TIME: ${System.currentTimeMillis() - start}")
        stat1.setOnClickListener { }
        stat2.setOnClickListener { }
        toolbar = v.findViewById(R.id.full_toolbar)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)

        mainChart.setNoDataText("")
        answerContainer.visibility = View.GONE
        avatar.setOnClickListener {openUser()  }
        username.setOnClickListener { openUser() }
        answer1 = v.findViewById(R.id.answer1)
        answer2 = v.findViewById(R.id.answer2)
        answer3 = v.findViewById(R.id.answer3)
        answer4 = v.findViewById(R.id.answer4)
        //errorHandler = ErrorHandler(activity, this, v, this)

        setQuestion()

        return v
    }

    private fun openUser() {
        val userID:String?=temp_question.userid?._id
        activity.openAccount(userID)


    }


    private fun initQuestion(){
        val questionString: String? = arguments?.get("question").toString()
        if (questionString != null) {
            Log.e("que", "STRING: $questionString")
            val moshi = Moshi.Builder().build()
            val adap: JsonAdapter<Question> = moshi.adapter<Question>(Question::class.java)
            val question = adap.fromJson(questionString)
            if (question != null) {
                temp_question = question
            } else {
                dismiss()
                Toast.makeText(
                    activity,
                    "This Question is not avaiable anymore!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                dismiss()
            }
            R.id.report -> {
                Toast.makeText(activity, "Reporrrrteeed", Toast.LENGTH_LONG).show()

            }
        }
        return true
    }






    fun loadCharts() {

        val lineData = LineData()
        mainChart.axisLeft.isEnabled = true
        var colors: Array<Int> = Array(4) { Color.BLACK }

        getContext()?.let {
            colors[0] = it.getColor(R.color.indicatorColor4)
            colors[1] = it.getColor(R.color.indicatorColor1)
            colors[2] = it.getColor(R.color.indicatorColor2)
            colors[3] = it.getColor(R.color.indicatorColor3)


        }


        GlobalScope.launch(Dispatchers.Main) {
            // StatisticsBase().setUpDefaultLineChart(mainChart,Color.WHITE)
            StatisticsBase().setUpDefaultLineChart(mainChart, activity.getColor(R.color.gradientFullview))
            loadChartData(colors, lineData)

            /* val pointList_=ArrayList<Entry>()
             pointList_.add(Entry(0f, 1f, "1"))
             pointList_.add(Entry(1f, 12f , "12"))
             pointList_.add(Entry(2f, 14f , "13"))
             pointList_.add(Entry(3f, 15f , "14"))
             pointList_.add(Entry(4f, 16f , "1"))

                 val lineDataSet = LineDataSet(pointList_, null)
                 lineData.addDataSet(lineDataSet)
                 StatisticsBase().setUpDefaultLineData(
                     lineDataSet,
                     pointList_,
                     Color.WHITE,
                     Color.WHITE,
                     Color.BLACK
                 )*/


            //val exclude = ArrayList<Int>()
            //exclude.add(0)
            //  exclude.add(5)
            //    mainChart.highlightAllValues(exclude) //Labels
        }


    }



    fun setUpAnswers() {
        answerContainer.visibility = View.GONE

        val answerType = Question.getQuestionType(temp_question)
        var inflateID = R.layout.fullview_answer_txt2
        when (answerType) {
            Constants.QUESTION_TYPE_TXT3 -> {
                inflateID = R.layout.fullview_answer_txt3
            }
            Constants.QUESTION_TYPE_TXT4_LONG -> {
                inflateID = R.layout.fullview_answer_txt4_long
            }
            Constants.QUESTION_TYPE_TXT4_SHRT -> {
                inflateID = R.layout.fullview_answer_txt4_short
            }

        }

        when (answerType) {
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




    /*    answerStub.layoutResource = inflateID
        val v: View = answerStub.inflate()
        when (answerType) {
            Constants.QUESTION_TYPE_TXT2 -> {
                answer1 = v.findViewById(R.id.answer1)
                answer2 = v.findViewById(R.id.answer2)
            }
            Constants.QUESTION_TYPE_TXT3 -> {
                answer1 = v.findViewById(R.id.answer1)
                answer2 = v.findViewById(R.id.answer2)
                answer3 = v.findViewById(R.id.answer3)
            }
            Constants.QUESTION_TYPE_TXT4_LONG -> {
                answer1 = v.findViewById(R.id.answer1)
                answer2 = v.findViewById(R.id.answer2)
                answer3 = v.findViewById(R.id.answer3)
                answer4 = v.findViewById(R.id.answer4)

            }
            Constants.QUESTION_TYPE_TXT4_SHRT -> {
                answer1 = v.findViewById(R.id.answer1)
                answer2 = v.findViewById(R.id.answer2)
                answer3 = v.findViewById(R.id.answer3)
                answer4 = v.findViewById(R.id.answer4)

            }


        }*/

        setAnswers(temp_question)
        answerContainer.visibility = View.VISIBLE

    }


    private fun setAnswers(qst: Question) {
        allVotes = 0f
        percentages = Array(4) { 0 }
        for (f in qst.votes) {
            allVotes += f
        }
        for (l in qst.votes.indices) {
            percentages[l] = (((qst.votes[l]) / allVotes) * 100).roundToInt()

        }
        val arr: Array<PresetAnswer?> = Array(4) { answer1 }
        arr[0] = answer1
        arr[1] = answer2
        arr[2] = answer3
        arr[3] = answer4
        
        getContext()?.getColor(R.color.indicatorColor1)?.let { arr[0]?.setIndicatorColor(it) }
        getContext()?.getColor(R.color.indicatorColor2)?.let { arr[1]?.setIndicatorColor(it) }
        getContext()?.getColor(R.color.indicatorColor3)?.let { arr[2]?.setIndicatorColor(it) }
        getContext()?.getColor(R.color.indicatorColor4)?.let { arr[3]?.setIndicatorColor(it) }

        expired.observe(this@QuestionFullDialogFragment, Observer {
          if(it) {
              expiration.text = TextBase.formatExpirationTimeStamp().first
              displayDetailMode(arr, -1)
          }

        })

        
        for (i in qst.answers.indices step 1) {
            if (arr[i] == null) continue
            arr[i]?.setText(qst.answers[i])
            arr[i]?.setOnClickListener {
                displayDetailMode(arr, arr.indexOf(it), true)
                if(true)return@setOnClickListener


                if (expired.value!!) return@setOnClickListener
                (it as PresetAnswer).showLoading()

                if (viewModel.localBase.getVote( qst.id) == null) {


                    questionAnswered(arr.indexOf(it), qst, object :
                        RetrofitHelper.Companion.DisplayableListCallback{
                        override fun onResponse(content: List<Displayable>) {
                           it.hideLoading()
                            displayDetailMode(arr, arr.indexOf(it), true)



                        }



                        override fun onError(s: Int) {
                            it.hideLoading()
                            //TODO VOTING ERROR
                            Toast.makeText(activity,"Voting Error",Toast.LENGTH_LONG).show()
                        }
                    })
                  //  displayDetailMode(arr, arr.indexOf(it), true)
                } else {
                    Toast.makeText(
                        activity,
                        "ALREADY VOTED FOR DATABASE: " + qst.id,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
      
            if (expired.value!!) {
                displayDetailMode(arr, -1)

            } else {
                val update = viewModel.localBase.getVote(qst.id)
                if (update != null) {
                    Log.d("FullView", "UpdateFound: ${update.voteon}")
                    displayDetailMode(arr, update.voteon)

                } else {
                    for (answer in arr) {
                        answer?.setIdleMode()
                    }


                }
            
        }
        loader.visibility = View.GONE
        voted.text = allVotes.roundToInt().toString()

    }

    private fun setExpired() {
        this.expired.postValue(true)
        updateExpiration = false

    }

    private fun setChartHighlight(i: Int) {
        var colors: Array<Int> = Array(4) { Color.BLACK }


        getContext()?.let {

            colors[0] = it.getColor(R.color.indicatorColor1)
            colors[1] = it.getColor(R.color.indicatorColor2)
            colors[2] = it.getColor(R.color.indicatorColor3)
            colors[3] = it.getColor(R.color.indicatorColor4)


        }

        (mainChart.marker as CustomMarkerView).setTextColor(colors[i])

        val include = ArrayList<Int>()
        include.add(1)
        include.add(2)
        include.add(3)
        include.add(4)

        // mainChart.highlightValues(include, i)


    }






    private fun displayDetailMode(
        array: Array<PresetAnswer?>,
        voteon: Int,
        clicked: Boolean = false
    ) {
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


    private fun runExpirationLoop() {
        GlobalScope.launch {
            var delay = 5000L
            while (updateExpiration) {
                if (::question.isInitialized) {
                    val pair=TextBase.formatExpirationTimeStamp(temp_question.expirationDate?.toLong())
                    val delayNew = pair.second
                    launch(Dispatchers.Main) {  expiration.text=pair.first}
                    if (delayNew == -1L) {
                        launch(Dispatchers.Main) {   setExpired()}
                        return@launch
                    }
                    delay = delayNew
                }
                kotlinx.coroutines.delay(delay)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fullscreen, menu)

        super.onCreateOptionsMenu(menu, inflater)

    }

    private fun setQuestion() {

        val l = measureTimeMillis {
            question.text = temp_question.text
            setQuestionContext(temp_question.context)
            runExpirationLoop()
            TextBase.formatUserAvatar(temp_question.userid?.avatar,avatar)
            username.text=TextBase.formatUserName(temp_question.userid?.name)

            stat1.text = temp_question.scoreOverall.toString()
            stat2.text = temp_question.tags?.name
            timestamp.text=TextBase.formatTimeStampDefault(temp_question.timestamp?.toLong())
            val model = ViewModelProvider(activity).get(MasterViewModel::class.java)
            temp_question.id.let {
                model.database.contentAccessor().getQuestionLiveData(it,Displayable.QUESTION)
                    .observe(this, Observer {
                        updateQuestion(ContentModel.toDisplayable(it))
                    })
            }

        }
        Log.e("timeToFull", "D: $l")

        Handler().postDelayed(object : Runnable {
            override fun run() {
                setUpAnswers()
                loadCharts()

            }
        }, 200)

    }

    private fun updateQuestion(it_: Displayable?) {
        Log.e("updateQuestion", "Fullview ")
        val it:Question?= it_?.getQuestion()
        it?.let {
            if (temp_question.text != it.text) {
                it.text?.let { it1 -> setQuestionText(it1) }
            }
            if (temp_question.context != it.context) {
                setQuestionContext(it.context)
            }
            if (!temp_question.votes.contentEquals(it.votes)) {
                setQuestionVotes(it.votes)
            }
            if (!temp_question.answers.contentEquals(it.answers)) {
                setQuestionAnswers(it.answers)
            }
        }
    }

    private fun setQuestionAnswers(answers: Array<String>) {
        val arr: Array<PresetAnswer?> = Array(4) { answer1 }
        arr[0] = answer1
        arr[1] = answer2
        arr[2] = answer3
        arr[3] = answer4
        for (answer in arr) {
            answer?.setText(answers[arr.indexOf(answer)])
        }
    }

    private fun setQuestionVotes(votes: Array<Int>) {
        GlobalScope.launch { }
        allVotes = 0f
        percentages = Array(4) { 0 }
        for (f in votes) {
            allVotes += f
        }
        for (l in votes.indices) {
            percentages[l] = (((votes[l]) / allVotes) * 100).roundToInt()

        }


        val arr: Array<PresetAnswer?> = Array(4) { answer1 }
        arr[0] = answer1
        arr[1] = answer2
        arr[2] = answer3
        arr[3] = answer4
        GlobalScope.launch(Dispatchers.Main) {
            for (answer in arr) {
                answer?.setProgress(percentages[arr.indexOf(answer)].toFloat())
            }
            voted.text = allVotes.roundToInt().toString()
            //loadCharts //TODO loadCharts
        }

    }

    private fun setQuestionContext(context_: String?) {
        context.text =
            temp_question.context + "Für den Fall, dass die italienischen Behörden Carola Rackete, die Kapitänin der SeaWatch 3, strafrechtlich verfolgen, werden wir, wie im letzten Jahr, Geld für die anfallenden Rechtskosten und Ausgaben der Lebensretter sammeln " + temp_question.context + "Für den Fall, dass die italienischen Behörden Carola Rackete, die Kapitänin der SeaWatch 3, strafrechtlich verfolgen, werden wir, wie im letzten Jahr, Geld für die anfallenden Rechtskosten und Ausgaben der Lebensretter sammeln "

    }

    private fun setQuestionText(text: String) {
        question.text = text
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        setHasOptionsMenu(true)
        initQuestion()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(FEATURE_NO_TITLE)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }


    private fun getTimeDomain(difference: Long): String {
        var domain = "0 days"

        var diff = TimeUnit.MILLISECONDS.toSeconds(difference)

        var str = TimeUnit.MILLISECONDS.toSeconds(difference).toString() + "s ago"
        if (diff > 60) {
            str = TimeUnit.MILLISECONDS.toMinutes(difference).toString() + "min ago"
        }
        if (diff > 60 * 60) {
            str = TimeUnit.MILLISECONDS.toHours(difference).toString() + "h ago"
        }
        if (diff > 24 * 60 * 60) {
            str = TimeUnit.MILLISECONDS.toDays(difference).toString() + "d ago"
        } else {
            domain = "m"
        }




        return domain
    }


    /** This is how the data should look
     *   pointList.add(Entry(-1f, 0f, "1")) //OUTFADING
    pointList.add(Entry(0f, 1f, "1"))
    pointList.add(Entry(1f, 12f + i, "12"))  ->
    pointList.add(Entry(2f, 14f + i, "13"))
    pointList.add(Entry(3f, 15f + i, "14"))
    pointList.add(Entry(4f, 16f + i, "1"))  //OUTFADING
     *
     * 1. Wenn es 10000 Updates gibt, dann würden die alle hierher geschickt und es würde schonmal ewig dauern die zu parsen -> nicht gut
     * 2. LÖsung: Die Update Tabelle bleibt, aber es werden Snapshots gemacht alle x Minuten.
     *  - Jeder Snapshot, beinhaltet Timestamp, Votes und wird in einer Separaten Tabelle gespeichert: UpdateSnapshots
     *  - Snapshots werden von allen Fragen die nochN nicht expired sind erstellt
     *
     *
     *
     *
     * Scope: If all timestamps are recent, it would be pointless to show the default x-Axis
     */
    private fun loadChartData(
        colors: Array<Int>,
        lineData: LineData
    ) {


        temp_question.id.let {
            activity.getSocketBase().getSnapshots(it, object : SnapshotHelper {
                override fun onSnapshotsReady(result: List<QuestionSnapshot>) {
                    GlobalScope.launch(Dispatchers.Main) {

                        val now = System.currentTimeMillis()
                        var missingDataMode = false
                        val pointList = HashMap<Int, ArrayList<Entry>>()

                        val maxSnap = result.maxBy { it.timestamp.toLong() }
                        val minSnap = result.minBy { it.timestamp.toLong() }
                        val maxTime: String? = maxSnap?.timestamp
                        val minTime: String? = minSnap?.timestamp
                        if (maxTime == null || minTime == null) missingDataMode = true
                        Log.d("chartDebug", "CONTENT: ${Gson().toJson(result)}")
                        val maxTimeLong: Long = maxTime!!.toLong()
                        val minTimeLong: Long = minTime!!.toLong()

                        if (!missingDataMode) {
                            val difference = maxTimeLong - minTimeLong


                            //4 Bins
                            val binWidth: Long = difference / 4

                            Log.d(
                                "chartDebug",
                                "DATA: maxTime: $maxTime minTime: $minTime Distance: $difference  BinWidth= $binWidth"
                            )

                            for (j in 0..4) {
                                var store = pointList[j]
                                if (store == null) store = ArrayList()
                                store.add(Entry(-1f, 0f, "1")) //INFADING
                                pointList[j] = store
                            }


                            for (i in 0..3) {
                                //POINTS
                                var addStart: Long = binWidth.times(i)
                                val binStart: Long = minTimeLong + addStart
                                val binEnd: Long = ((i + 1) * binWidth + minTimeLong)

                                val entriesInThatBin =
                                    result.filter { it.timestamp.toLong() < binEnd && it.timestamp.toLong() > binStart }


                                Log.d(
                                    "chartDebug",
                                    "POINT Number: $i   binStart: $binStart   binEnd: $binEnd  numberOfEntries: ${entriesInThatBin.size} $addStart   MinTime:$minTime  DIFFERENCE: ${binStart - minTime.toLong()}"
                                )



                                for (j in 0..3) {
                                    var store: ArrayList<Entry>? = pointList[j]
                                    if (store == null) store = ArrayList()
                                    //ANSWERS
                                    entriesInThatBin.sortedBy { it.votes[j] }
                                    if (entriesInThatBin.isEmpty()) {
                                        //TAKE PREVIOUS
                                        val time =
                                            " ${getTimeDomain(difference * (j / 4))}"

                                        var storeSize = store.size
                                        if (storeSize > 0) {
                                            var previousEntry = store.get(storeSize - 1)
                                            store.add(
                                                Entry(
                                                    i.toFloat(),
                                                    previousEntry.y, time

                                                )
                                            )
                                        } else {

                                            store.add(
                                                Entry(
                                                    i.toFloat(),
                                                    0f, time

                                                )
                                            )
                                        }
                                    } else {
                                        val maxEntryInThatBin =
                                            entriesInThatBin[entriesInThatBin.size - 1]
                                        val time =
                                            " ${getTimeDomain(now - maxEntryInThatBin.timestamp.toLong())}"

                                        val voteSum = maxEntryInThatBin.votes.sum()
                                        var percentage = 0.0f
                                        if (voteSum != 0) percentage =
                                            (maxEntryInThatBin.votes[j] / voteSum.toFloat()) * 100f
                                        Log.d(
                                            "chartDebug",
                                            "Content ADD: ${time}    $i  $j $percentage  $voteSum        VOTES: ${maxEntryInThatBin.votes[j]}"
                                        )

                                        store.add(
                                            Entry(
                                                i.toFloat(),
                                                percentage,
                                                time
                                            )
                                        )
                                    }
                                    pointList[j] = store


                                }


                            }
                            for (j in 0..3) {
                                var store = pointList[j]
                                if (store == null) store = ArrayList()
                                var lastValue: Float? = 1f
                                if (pointList[j] != null) {
                                    if (pointList[j]!!.size > 0) {
                                        val lastIndex = pointList[j]!!.size - 1
                                        lastValue = pointList[j]?.get(lastIndex)?.y
                                    }
                                }
                                if (lastValue == null) lastValue = 1f

                                store.add(Entry(4f, lastValue, "1")) //OUTFADING
                                pointList[j] = store
                                Log.d(
                                    "chartDebug",
                                    "FINAL: ${store.size}    ${Gson().toJson(store)}     ${Looper.myLooper() ==
                                            Looper.getMainLooper()}   $lastValue"
                                )
                                Collections.sort(store, EntryXComparator())


                                val lineDataSet = LineDataSet(store, null)

                                StatisticsBase().setUpDefaultLineData(
                                    lineDataSet,
                                    store,
                                    activity.getColor(R.color.gradientFullview),
                                    activity.getColor(R.color.gradientFullview),
                                    colors[j]
                                )

                                lineData.addDataSet(lineDataSet)
                            }

                            mainChart.data = lineData
                            mainChart.visibility = View.VISIBLE
                            mainChart.invalidate()
                        }
                    }


                }
            })
        }

    }


}


