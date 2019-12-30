package com.project.app.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.AutoCompleteSearchAdapter
import com.project.app.Adapters.TopicsAdapter
import com.project.app.Helpers.MasterViewModel
import com.project.app.Interfaces.PanelSearchInterface
import com.project.app.Objects.AutoCompleteItem
import com.project.app.Objects.Topic
import com.project.app.R
import kotlinx.coroutines.*

class TopicOverviewFragment : Fragment() {
    fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

    lateinit var recyclerAll: RecyclerView
    lateinit var activity: HomeActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_topic, null)
        setUpSearchPanel(view, object : PanelSearchInterface {
            override fun onBack() {

            }

            override fun onTextChange(s: String) {
                filterTags(s)


            }

            override fun afterTextChange(s: String) {
            }
        })

        recyclerAll = view.findViewById(R.id.topics)

        setUpRecyclers()

        return view
    }

    private fun filterTags(s: String) {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)

        val all = viewModel.topicBase.tags.value?.first?.sortedBy { it.name }
        all?.let {
            val results = all.filter { it.name.contains(s) }
            (recyclerAll.adapter as TopicsAdapter).update(ArrayList(results))
        }
    }

    private fun setUpRecyclers() {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        var scrollHandler: Job? = null

        recyclerAll.setHasFixedSize(true)
        recyclerAll.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        //val tags= viewModel.topicBase.tags.value?.first
        viewModel.topicBase.tags.observe(viewLifecycleOwner, Observer {


            recyclerAll.adapter =
                TopicsAdapter(ArrayList(it.first.sortedBy { iti -> iti.name }.filter { it.name!="-1" }), this)

        })
        recyclerAll.addItemDecoration(SpacingItemDecoration(0, 12.dpToPx(resources.displayMetrics)))
        recyclerAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                activity.onScrollHandler(dy)

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {

                        scrollHandler = GlobalScope.launch {
                            delay(1500)
                            launch(Dispatchers.Main) { activity.onScrollHandler(-2000) }
                        }
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        scrollHandler?.cancel()

                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        scrollHandler?.cancel()

                    }
                }
            }
        })
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
            val edit = view.findViewById<AutoCompleteTextView>(R.id.panel_search_edit)
            val icon = view.findViewById<ImageView>(R.id.panel_search_back)
            icon.setImageResource(R.drawable.baseline_search_24)
            edit.threshold = 2

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

    fun showDetailsForTag(topic: Topic) {
        val frag = TopicDetailFragment()
        val bundle = Bundle()
        bundle.putString("tagID", topic._id)
        frag.arguments = bundle
        frag.show(childFragmentManager,"detailTopic")
    }
}