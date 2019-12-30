package com.project.app.Fragments

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.AutoCompleteSearchAdapter
import com.project.app.Dialogs.QuestionControllerDialogFragment
import com.project.app.Helpers.Constants
import com.project.app.Helpers.MasterViewModel
import com.project.app.Interfaces.PanelSearchInterface
import com.project.app.Interfaces.TopicHelper
import com.project.app.Objects.AutoCompleteItem
import com.project.app.Objects.Topic
import com.project.app.Helpers.ContentLoader
import com.project.app.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TopicDetailFragment : QuestionControllerDialogFragment() {
    lateinit var recyclerAll: RecyclerView
    lateinit var tagStr: String
    lateinit var contentLoader: ContentLoader
    lateinit var edit: AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_topic_detail, null)
        setUpSearchPanel(view, object : PanelSearchInterface {
            override fun onBack() {
                dismiss()
            }

            override fun onTextChange(s: String) {


            }

            override fun afterTextChange(s: String) {
                searchQuestionsInTags(s)
            }
        })

        recyclerAll = view.findViewById(R.id.cd_content)

        recyclerAll.setHasFixedSize(true)
        recyclerAll.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        contentLoader =
            ContentLoader(
                this,
                activity,
                view
            )
        contentLoader.enqueueContent(Constants.CONTENT_TAGDETAIL, tagStr)
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        val tag = viewModel.topicBase.tags.value?.first?.find { tagStr == it._id }
        tag?.let {
            edit.hint = "Search for ${it.name}"
            setUpTopicHeader(view,it)

        }

        return view
    }

    private fun setUpTopicHeader(v:View, topic:Topic) {
        val header= v.findViewById<TextView>(R.id.topic_header_1)
        val subheader= v.findViewById<TextView>(R.id.topic_header_2)
        val qstCount=v.findViewById<TextView>(R.id.topic_header_qst)
        val voteCount=v.findViewById<TextView>(R.id.topic_header_votes)
        val back=v.findViewById<View>(R.id.topic_header_back)

        header.text=topic.name
        back.backgroundTintList= ColorStateList.valueOf(Color.parseColor(topic.color))
        getTopicDetails(object: TopicHelper{
            override fun onError(errorCode: Int) {
            }

            override fun onVoteCount(topicID: String, count: Long) {
                voteCount.text="$count"
            }

            override fun onTopicCount(topicID: String, count: Long) {
                qstCount.text="$count"

            }
        })
    }

    private fun getTopicDetails(topicHelper: TopicHelper) {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        viewModel.topicBase.getTopicDetails(tagStr,topicHelper)
    }


    override fun onDestroy() {
        clearDataBase()

        super.onDestroy()
    }

    private fun clearDataBase() {
        GlobalScope.launch {
            val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
            val int = viewModel.database.contentAccessor().deleteByContentName(Constants.CONTENT_TAGDETAIL)
            Log.d("DetailTopic", "ON DESTROY: $int")
        }
    }

    fun searchQuestionsInTags(s: String) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyleSearch)
        val b = arguments
        if (b?.get("tagID") != null) {
            tagStr = b.get("tagID") as String
        } else {
            dismiss()
        }


    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            activity = context as HomeActivity
        }
    }

    private fun setUpSearchPanel(view: View?, panelSearchInterface: PanelSearchInterface) {
        if (view != null) {
            val clear = view.findViewById<ImageView>(R.id.panel_search_clear)
            edit = view.findViewById<AutoCompleteTextView>(R.id.panel_search_edit)
            edit.threshold = 2
            val back=view.findViewById<View>(R.id.panel_search_back)
            back.setOnClickListener { panelSearchInterface.onBack() }
            val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)

            edit.setOnItemClickListener { adapterView, view, i, l ->
                val item: AutoCompleteItem = edit.adapter.getItem(i) as AutoCompleteItem
                edit.setText(item.text)
                // searchQuestions(item.text)

            }

            edit.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        // searchQuestions(p0?.text.toString())

                        return true
                    }
                    return false; }
            })




            context?.let {
                val arr = ArrayList<AutoCompleteItem>()
                for (s in viewModel.localBase.getAutoCompleteHistory()) {
                    arr.add(AutoCompleteItem(s))
                }
                val adapter = AutoCompleteSearchAdapter(it, R.layout.adapter_autocomplete, arr)
                edit.setAdapter(adapter)
            }
            clear.setOnClickListener { edit.setText("") }
            edit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    //  panelSearchInterface.afterTextChange(s.toString())


                    // (edit.adapter as AutoCompleteSearchAdapter).update()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    panelSearchInterface.onTextChange(s.toString())


                }
            })


        }
    }
}