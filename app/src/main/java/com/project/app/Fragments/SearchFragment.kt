package com.project.app.Fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.AutoCompleteSearchAdapter
import com.project.app.Dialogs.QuestionControllerDialogFragment
import com.project.app.Helpers.Constants
import com.project.app.Helpers.MasterViewModel
import com.project.app.Interfaces.PanelSearchInterface
import com.project.app.Objects.AutoCompleteItem
import com.project.app.Helpers.ContentLoader
import com.project.app.Preferences.SearchPreferenceFragment
import com.project.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchFragment : QuestionControllerDialogFragment() {

    lateinit var background: ImageView
    lateinit var contentLoader: ContentLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleSearch)
    }

    private fun setUpSort() {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.registerOnSharedPreferenceChangeListener { preference, key ->

            when (key) {
                context?.getString(R.string.pref_sort) -> {
                   /* (mRecyclerView.adapter as QuestionPageAdapter).changeSort(
                        preference.getString(
                            key,
                            "sort1"
                        )
                    )*/
                }
            }

        }
    }


    private fun searchQuestions(s: String) {
       // contentLoader.setContent(Constants.CONTENT_NOTHING)
        var str = s
        if (s.isEmpty()) {
            str = "-1"
            return
        }
        addTextToHistory(s)
        contentLoader.enqueueContent(Constants.CONTENT_SEARCH,s)


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_search, null)

        Handler(Looper.getMainLooper()).postDelayed({
            childFragmentManager.beginTransaction()
                .replace(R.id.background_content,
                    SearchPreferenceFragment(), "prefs_search").commit()
            setUpSort()


        },2000)


        setUpSearchPanel(view, object : PanelSearchInterface {
            override fun onBack() {
                val view = view?.rootView?.windowToken
                if (view != null) {
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm!!.hideSoftInputFromWindow(view, 0)
                }
                dismiss()
            }

            override fun onTextChange(s: String) {


            }

            override fun afterTextChange(s: String) {
                //   searchQuestions(s)
            }
        })

         contentLoader =
             ContentLoader(
                 this,
                 activity,
                 view
             )
        contentLoader.enqueueContent(Constants.CONTENT_SEARCH)



        return view
    }



    private fun addTextToHistory(s: String) {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        viewModel.localBase.addTextToAutoCompleteHistory(s)
    }

    private fun setUpSearchPanel(view: View?, panelSearchInterface: PanelSearchInterface) {
        if (view != null) {
            val clear = view.findViewById<ImageView>(R.id.panel_search_clear)
            val back = view.findViewById<ImageView>(R.id.panel_search_back)
            val edit = view.findViewById<AutoCompleteTextView>(R.id.panel_search_edit)
            val icon = view.findViewById<ImageView>(R.id.panel_search_icon)
            edit.threshold = 2

            val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)

            edit.setOnItemClickListener { adapterView, view, i, l ->
                val item: AutoCompleteItem = edit.adapter.getItem(i) as AutoCompleteItem
                edit.setText(item.text)
                searchQuestions(item.text)

            }


            edit.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        searchQuestions(p0?.text.toString())

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
            back.setOnClickListener { panelSearchInterface.onBack() }
            edit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    //  panelSearchInterface.afterTextChange(s.toString())

                    GlobalScope.launch {
                        val arr = ArrayList<AutoCompleteItem>()
                        for (si in viewModel.localBase.getAutoCompleteHistory()) {
                            arr.add(AutoCompleteItem(si))
                        }
                        launch(Dispatchers.Main) {
                            (edit.adapter as AutoCompleteSearchAdapter).update(arr)
                        }
                    }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            activity = context as HomeActivity
        }
    }

}