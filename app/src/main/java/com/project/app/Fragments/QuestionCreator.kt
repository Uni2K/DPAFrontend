package com.project.app.Fragments

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.CreateQuestionViewPagerAdapter
import com.project.app.CustomViews.DurationPicker
import com.project.app.Dialogs.LoginDialogFragment
import com.project.app.Helpers.DialogFactory
import com.project.app.Helpers.MasterViewModel
import com.project.app.Objects.Question
import com.project.app.Objects.Topic
import com.project.app.R
import com.project.app.Slides.CreateSlideAnswers
import com.project.app.Slides.CreateSlideContext
import com.project.app.Slides.CreateSlideText
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class QuestionCreator : DialogFragment() {


    //General
    lateinit var parent: HomeActivity
    lateinit var motionLayout: MotionLayout
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout
    private lateinit var halfGroup: Group
    private lateinit var send: ImageView
    lateinit var creatorNavigator: FloatingActionButton
    lateinit var introLayout:ViewGroup

    //Preview
    private lateinit var mediumText: TextView
    private lateinit var mediumAnswerleft: TextView
    private lateinit var mediumAnswerright: TextView
    private lateinit var mediumContext: TextView
    private lateinit var mediumTagicon: ImageView
    private lateinit var statusText:TextView


    //Misc
    var questionLiveData: MutableLiveData<Question> = MutableLiveData()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            parent = context as HomeActivity
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        return dialog
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleCreate)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_creator, container, false)

        viewPager = v.findViewById(R.id.c_viewpager)
        viewPager.adapter = CreateQuestionViewPagerAdapter(childFragmentManager, this)
        motionLayout = v.findViewById(R.id.motionLayout)
        tabLayout = v.findViewById(R.id.c_tabs)
        halfGroup = v.findViewById(R.id.half_group)
        creatorNavigator = v.findViewById(R.id.creatorNavigator)
        introLayout = v.findViewById(R.id.create_intro)
        val switch = v.findViewById<SwitchCompat>(R.id.c_intro_switch)
        val start = v.findViewById<View>(R.id.c_intro_start)
        val tos=v.findViewById<TextView>(R.id.c_intro_tos)
        tos.setOnClickListener {
            DialogFactory.createSystemDialogPositive(parent,"This is just an example of the TOS. Needs to be replaced!"," Terms of Usage","dismiss",object: DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
                }
            })
        }

        start.setOnClickListener {
            if (switch.isChecked) {
                startCreator(v)
            } else {
                Toast.makeText(context, "Please accept the Terms of Usage!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        v.findViewById<View>(R.id.c_intro_back).setOnClickListener { dismiss() }

        return v
    }


    private fun setNavigatorAwareness() {
        creatorNavigator.visibility = View.VISIBLE
        val tabCount = tabLayout.tabCount
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == tabCount - 1) {
                    creatorNavigator.setImageResource(R.drawable.baseline_send_24)
                } else {
                    creatorNavigator.setImageResource(R.drawable.baseline_navigate_next_24)

                }
            }
        })
        creatorNavigator.setOnClickListener {
           next()
        }


    }

    private fun startCreator(v: View) {
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)

        if(!viewModel.userBase.isLoggedIn()){
            DialogFactory.createSystemDialog(parent,"In order to create a poll you have to be logged in!", "Not logged in!","Login",object: DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    LoginDialogFragment().show(parent.supportFragmentManager, "login")
                p0?.dismiss()
                }
            }, "Dismiss",
                DialogInterface.OnClickListener { p0, p1 ->
                    p0.dismiss()
                })
            return
        }



        motionLayout.visibility = View.VISIBLE
        introLayout.visibility = View.GONE
        setNavigatorAwareness()

        tabLayout.addTab(tabLayout.newTab().setText("Topic"))
        tabLayout.addTab(tabLayout.newTab().setText("Text"))
        tabLayout.addTab(tabLayout.newTab().setText("Answers"))
        tabLayout.addTab(tabLayout.newTab().setText("Context"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { viewPager.setCurrentItem(it, true) }
            }
        })

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
            }

        })



        v.findViewById<ImageView>(R.id.c_back).setOnClickListener {
            dismiss()
        }
        v.findViewById<ImageView>(R.id.c_back).setOnLongClickListener {

            //   for (i in 1..30 step 1)
            //    createQuestion(Question.createSampleQuestion())
            true
        }
        send = v.findViewById(R.id.c_send)
        send.setOnClickListener {
            checkSend()
        }
        v.findViewById<TextView>(R.id.c_send_full).setOnClickListener {
            createQuestion()
        }

        viewPager.offscreenPageLimit = 4

        statusText = v.findViewById(R.id.c_status_text)


        mediumText = v.findViewById(R.id.medium_question)
        mediumAnswerleft = v.findViewById(R.id.medium_left)
        mediumAnswerright = v.findViewById(R.id.medium_right)
        mediumContext = v.findViewById(R.id.medium_context)
        mediumTagicon = v.findViewById(R.id.medium_tagicon)

        questionLiveData.observe(viewLifecycleOwner, Observer {
            refreshPreview()
        })

        setUpDurationPicker(v)

    }

    private fun setUpDurationPicker(v:View) {
        val mCircularSeekBar = v.findViewById<DurationPicker>(R.id.durationPicker)
        val text = v.findViewById<TextView>(R.id.dp_text)
        val subtext = v.findViewById<TextView>(R.id.dp_subtext)

        mCircularSeekBar.dotMarkers = false
        mCircularSeekBar.setDrawMarkings(false)
        mCircularSeekBar.arcColor = context?.getColor(R.color.secondaryDarkColor)!!
        mCircularSeekBar.setRoundedEdges(true)
        mCircularSeekBar.setIsGradient(false)
        mCircularSeekBar.sweepAngle = 225
        mCircularSeekBar.arcRotation = 249
        mCircularSeekBar.arcThickness = 45
        mCircularSeekBar.min = 0
        mCircularSeekBar.max = 100
        mCircularSeekBar.valueStep = 10
        mCircularSeekBar.setOnCircularSeekBarChangeListener(object :
            DurationPicker.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                CircularSeekBar: DurationPicker?,
                progress: Float,
                fromUser: Boolean
            ) {

                val pair = formatDuration(progress)

                if (pair.first != getString(R.string.automatic))
                    animateChangeTextCount(text, pair.first.toFloat())
                else
                    text.text = getString(R.string.automatic)
                subtext.text = formatTime(pair.second)


                val quest = questionLiveData.value
                val now = Date().time
                var expire = "-1"
                if (pair.first != getString(R.string.automatic))
                    expire = (now + pair.first.toInt() * getTimeRange(pair.second)).toString()

                quest?.expirationDate= expire
                questionLiveData.postValue(quest)


            }

            override fun onStartTrackingTouch(CircularSeekBar: DurationPicker?) {
            }

            override fun onStopTrackingTouch(CircularSeekBar: DurationPicker?) {
            }
        })
    }

    private fun checkSend() {
        context?.let { it1 -> hideKeyboard(it1) }
        if (motionLayout.currentState == R.id.collapsed) {

            if (everythingComplete()) {
                showFull()
            } else {
                showHalf()
            }
        } else {
            showIdle()
        }
    }

    private fun refreshPreview(){
        val statusQuestion = "You need to insert a valid question!"
        val statusAnswers = "There are not enough answers for your question!"
        val statusTag = "You need to insert a category!"
        val statusContext = "Please add some details about your question!"


        val defaultAnswer1 = getString(R.string.default_questionanswer1)
        val defaultAnswer2 = getString(R.string.default_questionanswer2)
        val defaultQuestion = getString(R.string.default_questiontext)
        val defaultContext = getString(R.string.default_questioncontext)

        val qTextContent: String? = questionLiveData.value?.text
        val qAnswerContent: Array<String>? = questionLiveData.value?.answers
        val qTopicContent: Topic? = questionLiveData.value?.tags
        val qContextContent = questionLiveData.value?.context

        qAnswerContent?.let {
            if (it[1].isNotEmpty()) mediumAnswerright.text =
                it[1] else mediumAnswerright.text =
                defaultAnswer2
            if (it[0].isNotEmpty()) mediumAnswerleft.text = it[0] else mediumAnswerleft.text =
                defaultAnswer1
        }
        qContextContent?.let {
            if (it != "-1") mediumContext.text =
                cleanContextText(it) else mediumContext.text =
                defaultContext
        }
        qTextContent?.let {
            if (it != "-1") mediumText.text = it else mediumText.text = defaultQuestion
        }

        qTopicContent?.let {
            mediumTagicon.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(it.color))
        }



        if (qTopicContent == null) {
            statusText.text = statusTag
        } else if (!CreateSlideText.isValid(qTextContent)) {
            statusText.text = statusQuestion
        } else if (!CreateSlideAnswers.isValidText(qAnswerContent)) {
            statusText.text = statusAnswers
        } else if (!CreateSlideContext.isValid(qContextContent)) {
            statusText.text = statusContext

        }


        if (everythingComplete()) {
            statusText.text = "How long should your question stay?"
            if (motionLayout.currentState != R.id.collapsed) {
                showFull()
            }
        }

    }

    private fun createQuestion(question: Question) {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        /*  question.let {
              parent.getSocketBase().sendQuestionToServer(it, object : SocketBase.QuestionInterface {
                  override fun onSent() {
                      questionSent()
                  }

                  override fun onFailed() {
                  }
              })
          }*/
    }

    private fun hideKeyboard(activity: Context) {
        val view = view?.rootView?.windowToken
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
            imm?.let {
                it.hideSoftInputFromWindow(view,0)
            }
        }
    }

    fun animateChangeTextCount(textView: TextView, newVal: Float) {

        if (textView.text == newVal.toString()) return
        textView.text = newVal.toInt().toString()
        return
        val oldVal = textView.text.toString().toFloat()
        val valAnim: ValueAnimator = ValueAnimator.ofInt(oldVal.toInt(), newVal.toInt())
        valAnim.duration = 200
        valAnim.addUpdateListener { p0 -> textView.text = p0?.animatedValue.toString() }
        valAnim.start()
    }

    fun formatTime(string: String): String {
        return when (string) {
            "h" -> getString(R.string.hour)
            "d" -> getString(R.string.day)
            "w" -> getString(R.string.week)
            "m" -> getString(R.string.month)
            "a" -> ""
            else -> ""
        }
    }


    fun formatDuration(progress: Float): Pair<String, String> {
        return when (progress) {
            in 0f..10f -> Pair("1", "h")
            in 10f..20f -> Pair("2", "h")
            in 20f..30f -> Pair("4", "h")
            in 30f..40f -> Pair("12", "h")
            in 40f..50f -> Pair("24", "h")
            in 50f..60f -> Pair("3", "d")
            in 60f..70f -> Pair("7", "d")
            in 70f..80f -> Pair("14", "d")
            in 80f..90f -> Pair("1", "m")
            in 90f..100f -> Pair(getString(R.string.automatic), "a")
            else -> Pair(getString(R.string.automatic), "a")

        }
    }


    fun getTimeRange(s: String): Int {

        var seconds = 0
        when (s) {
            "h" -> seconds = 60 * 60
            "d" -> seconds = 60 * 60 * 24
            "m" -> seconds = 60 * 60 * 24 * 30
            "a" -> seconds = -1
        }

        return seconds * 1000


    }


    private fun showHalf() {
        halfGroup.visibility = View.GONE
        motionLayout.transitionToState(R.id.half_collapsed)
        send.setImageResource(R.drawable.baseline_expand_less_24)

    }

    private fun showFull() {
        halfGroup.visibility = View.VISIBLE
        motionLayout.transitionToState(R.id.expanded)
        send.setImageResource(R.drawable.baseline_expand_less_24)
    }

    private fun showIdle() {
        halfGroup.visibility = View.GONE
        motionLayout.transitionToState(R.id.collapsed)
        send.setImageResource(R.drawable.baseline_send_24)

    }

    private fun everythingComplete(): Boolean {

        val tx = questionLiveData.value?.text
        val tx2: Array<String>? = questionLiveData.value?.answers
        val tx3: Topic? = questionLiveData.value?.tags
        val tx4 = questionLiveData.value?.context

        if ( !CreateSlideText.isValid(tx)) return false
        if (!CreateSlideAnswers.isValidText(tx2)) return false
        if (tx3==null) return false
        if (! CreateSlideContext.isValid(tx4)) return false

        return true

    }


    fun questionSent() {
        GlobalScope.launch(Dispatchers.Main) {
            if (context != null) Toast.makeText(
                context,
                "Successfully sent question!",
                Toast.LENGTH_LONG
            ).show()
            dismiss()
        }

    }

    private fun createQuestion() {

        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        /* question.value?.let {
             parent.getSocketBase().sendQuestionToServer(it, object : SocketBase.QuestionInterface {
                 override fun onSent() {
                     questionSent()
                 }

                 override fun onFailed() {
                 }
             })
         }*/


    }
    private fun cleanContextText(s: String): String {
        //MARKDOWN TO NOT MARKDOWN, only to display in small preview
        var cleanstring = s.replace("<br>", "")
        cleanstring = cleanstring.replace("#", "")
        cleanstring = cleanstring.replace(">", "")
        cleanstring = cleanstring.replace("\n", " ")
        cleanstring = cleanstring.replace("\\\n", " ")
        cleanstring = cleanstring.replace("\\n", " ")
        cleanstring.trim()

        return cleanstring
    }

    fun next() {
        val tabCount = tabLayout.tabCount
        if (tabLayout.selectedTabPosition == tabCount - 1) {
            checkSend()
        } else {
            tabLayout.getTabAt(tabLayout.selectedTabPosition + 1)?.select()
        }
    }

    fun onChangeText(questionText: String) {
        var question=questionLiveData.value
        if (question != null) {
            question.text=questionText
            questionLiveData.setValue(question)
        }

    }

    fun onChangeContext(context: String) {
        val markwon: Markwon? = parent.let {
            Markwon.builder(it)
                .usePlugin(HtmlPlugin.create())
                .build()
        }
        val markdown: Spanned? = markwon?.toMarkdown(context)
        if (markdown != null) {
            var question=questionLiveData.value
            if (question != null) {
                question.context=cleanContextText(markdown.toString())
                questionLiveData.setValue(question)
            }

        }

    }

    fun onChangeQuestionAnswer(answerArray: Array<String>) {
        var question=questionLiveData.value
        if (question != null) {
            question.answers=answerArray
            questionLiveData.setValue(question)
        }
    }

    fun onChangeTopic(nothing: Topic?) {
        var question=questionLiveData.value
        if (question != null) {
            question.tags=nothing
            questionLiveData.setValue(question)
        }
    }
}