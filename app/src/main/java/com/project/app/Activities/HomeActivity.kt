package com.project.app.Activities

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cl.jesualex.stooltip.Position
import cl.jesualex.stooltip.Tooltip
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.app.Bases.Assistant
import com.project.app.Bases.DesignBase
import com.project.app.Bases.SocketBase
import com.project.app.Bases.TextBase
import com.project.app.Dialogs.*
import com.project.app.Fragments.*
import com.project.app.Helpers.Constants
import com.project.app.Helpers.Constants.Companion.CONTENT_FEED
import com.project.app.Helpers.Constants.Companion.CONTENT_NEW
import com.project.app.Helpers.Constants.Companion.CONTENT_TOPICS
import com.project.app.Helpers.Constants.Companion.CONTENT_TRENDING
import com.project.app.Helpers.CustomBottomSheetBehavior
import com.project.app.Helpers.MasterViewModel
import com.project.app.Objects.User
import com.project.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.showKeyboard

class HomeActivity : AppCompatActivity() {

    //Views
    lateinit var FAB: FloatingActionButton
    lateinit var bottomSheet: ConstraintLayout
    lateinit var bottomNavigationView: BottomNavigationView
    //Misc
    var bottomSheetHidden = false

    override fun onCreate(savedInstanceState: Bundle?) {
        DesignBase.setNavigationBarColor(this, window, R.color.hintBackgroundColor)
        DesignBase.setStatusBarColor(this, window, R.color.white)

        super.onCreate(savedInstanceState)




        setContentView(R.layout.activity_home)
        loadToolbar()
        initFeed()
        setUpTriggers()


        registerActivity()
        setUpBNV()



        findViewById<LottieAnimationView>(R.id.botStart).setOnClickListener {
            AssistantDialogFragment().show(supportFragmentManager,"assistant")
        }



        bottomSheet = findViewById(R.id.cons)
        FAB = findViewById(R.id.fabs)
        FAB.setOnClickListener {
                QuestionCreator().show(supportFragmentManager, "CreatorIntro")

        }

      /*  findViewById<ImageView>(R.id.search_sheet).setOnClickListener {
            SearchFragment().show(supportFragmentManager, "search")
        }*/

        val frag = MainContentFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_list, frag, "content").commit()






    }

    private fun registerActivity() {
        //val viewModel = ViewModelProvider(this).get(MasterViewModel::class.java)
       // viewModel.contentSyncer.registerLifecycleOwner(this)
      //  viewModel.contentSyncer.connectSyncIndicator(findViewById(R.id.indicator_sync_update),findViewById(R.id.indicator_sync_content))
    }


    private fun initFeed() {

        val viewModel = ViewModelProvider(this).get(MasterViewModel::class.java)

        GlobalScope.launch (Dispatchers.IO) {

              val contentExists=viewModel.userBase.doesStreamContentExist()

                    launch(Dispatchers.Main) {

                        if(contentExists || viewModel.userBase.isLoggedIn()) {

                            bottomNavigationView.selectedItemId=R.id.main_feed

                            initBottomSheet()
                            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                            bottomNavigationView.menu.findItem(R.id.main_feed).isVisible = true
                        }else{

                            bottomNavigationView.selectedItemId=R.id.main_discover





                            viewModel.userBase.loggedInUserChanged.observe(this@HomeActivity,object: Observer<User?>{
                                override fun onChanged(t: User?) {
                                    if(t!=null) {

                                        viewModel.userBase.loggedInUserChanged.removeObserver(this)
                                        initFeed()
                                    }
                                }

                            })

                        }








            }


        }



    }


    private fun setUpTriggers() {
        val model = ViewModelProvider(this).get(MasterViewModel::class.java)
        model.questionBase.newQuestionsAvaiableTrigger.observe(this, Observer {
            if (it == 0) return@Observer
            if (it < Constants.FETCH_OVERLAD) {
                //Update Immediately
                model.fetchMore(Constants.CONTENT_STREAM)
            } else {
                oversizeUpdateReady(it)
            }
        })
    }


    private fun oversizeUpdateReady(it: Int?) {
        if (it != null) {
            //     notifierOverSize = it
            val text = "$it Questions Ready! Fetch?"
            //notifier.requestUserConfirmation(text, it)
            FAB.setImageResource(R.drawable.baseline_cloud_download_24)
            //pinNotifierToTop(200, 1)
        }
    }


    /**
     * Handles FAB and Bottomsheet hiding
     * TODO rework
     */
    fun onScrollHandler(dy: Int) {
     //   notifier.onScroll()
        if (dy > 0) {

            if (!bottomSheetHidden) {
                hideBottomSheet()
                bottomSheetHidden = true

            }

        } else if (dy < 0) {
            //Show

            if (bottomSheetHidden) {
                showBottomSheet()
                //    moveFabCenter()
                bottomSheetHidden = false
            }
        }


    }


    private fun loadToolbar() {
        val img = findViewById<ImageView>(R.id.main_account)


        img.setOnClickListener {
            openAccount("-1")
        }
        val viewModel = ViewModelProvider(this).get(MasterViewModel::class.java)
        val user = viewModel.localBase.getStoredLogin()

        user?.let { im ->
            TextBase.formatUserAvatar(im.avatar, img)
        }

        if(viewModel.localBase.getStoredLogin()==null){
            Tooltip.on(img)
                .text("You are not logged in!\nClick here to create an account or sign in!")
                .icon(R.drawable.baseline_info_24)
                .iconSize(60, 60)
                .textColor(Color.BLACK)
                .shadow(10f)
                .color(Color.WHITE)
                .clickToHide(true)
                .corner(20)
                .position(Position.BOTTOM)
                .show(20000)
        }









    }


    fun openAccount(userID: String?) {



       when (userID) {
            null -> {
                Toast.makeText(this, "User not found!", Toast.LENGTH_LONG).show()
            }
            "-1" -> {
                val sp=getSharedPreferences("Settings",0)
                if(!sp.getBoolean("user_TOS",false)){
                    UserIntroDialogFragment().show(supportFragmentManager,"user intro")
                }else {
                    val frag = LoggedInUserDialogFragment()
                    frag.show(supportFragmentManager, "account")
                }
            }
            else -> {
                val arg = Bundle()
                arg.putString("userid", userID)
                val frag = GeneralUserDialogFragment()
                frag.arguments = arg
                frag.show(supportFragmentManager, "account")
            }
        }
    }


    private fun loadContent(content: String) {
        val frag = MainContentFragment()
        val bundle = Bundle()
        bundle.putString("initialContent", content)
        frag.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_list, frag, "content").commit()


    }




    override fun onStop() {
        super.onStop()
        getSocketBase().onStop()
    }

    override fun onStart() {
        super.onStart()
        getSocketBase().onStart()
        val sharedPreferences = getSharedPreferences("settings",0)
        if(sharedPreferences.getBoolean("welcome",true)){
            sharedPreferences.edit().putBoolean("welcome",false).apply()
            WelcomeDialogFragment().show(supportFragmentManager, "welcome")

        }

    }


    private fun initBottomSheet() {

        val viewModel = ViewModelProvider(this).get(MasterViewModel::class.java)
        val doesQuickSelectIsAttached:Boolean =  supportFragmentManager.findFragmentByTag("quickTopics")!=null

        if(doesQuickSelectIsAttached) return
        if(viewModel.topicBase.tags.value==null){
            viewModel.topicBase.tags.observe(this, Observer {
                if(it!=null){
                    viewModel.topicBase.tags.removeObservers(this)
                    initBottomSheet()

                }

            })
            return
        }



        if (supportFragmentManager.findFragmentByTag("bottomsheet") == null) {
            val bundle = Bundle()
            bundle.putBoolean("bottomSheet", true)
            bundle.putInt("preset",TopicProviderFragment.PRESET_QUICKSELECT)
            val tagFragment = TopicQuickSelectFragment()
            tagFragment.arguments = bundle
            tagFragment.setBottomSheet(findViewById(R.id.cons))
            supportFragmentManager.beginTransaction().replace(R.id.fr, tagFragment,"quickTopics")
                .commitAllowingStateLoss()

        }

        val bottomSheetBehavior: CustomBottomSheetBehavior<ConstraintLayout> =
            CustomBottomSheetBehavior.from(findViewById(R.id.cons))
        bottomSheetBehavior.setLocked(false)
        val mainViewFlippter=findViewById<ViewFlipper>(R.id.main_viewflipper)

        bottomSheetBehavior.setBottomSheetCallback(object: CustomBottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState==BottomSheetBehavior.STATE_COLLAPSED){
                    if(mainViewFlippter.displayedChild!=0){
                        mainViewFlippter.displayedChild=0
                        resetQuickHeader()
                    }

                }else if(newState==BottomSheetBehavior.STATE_EXPANDED){
                    if(mainViewFlippter.displayedChild!=1)
                        mainViewFlippter.displayedChild=1

                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {


            }
        })

        //   LoginDialogFragment().show(supportFragmentManager,"")


    }

    private fun setUpBNV() {


       bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_feed -> {
                    setContent(Constants.CONTENT_FEED)
                    true
                }
                R.id.main_search -> {
                   SearchDialogFragment().show(supportFragmentManager,"search")

                }

              /*  R.id.main_hot -> {
                    setContent(Constants.CONTENT_TRENDING)


                }*/
                /*  R.id.main_new -> {
                    setContent(Constants.CONTENT_NEW)

                }
                R.id.main_topics -> {
                    setContent(Constants.CONTENT_TOPICS)
                }*/
            }
           false
        }

        val mainViewFlippter=findViewById<ViewFlipper>(R.id.main_viewflipper)
        mainViewFlippter.setInAnimation(this, android.R.anim.slide_in_left)
        mainViewFlippter.setOutAnimation(this,android.R.anim.slide_out_right)


        //SEARCH
        val flipperQuick:ViewFlipper=findViewById(R.id.viewflipper_quick)
        val searchLayout: ViewGroup = findViewById(R.id.tn_searchbar)
        val searchClear: ImageView = findViewById(R.id.panel_search_clear)
        val searchBack: ImageView = findViewById(R.id.panel_search_back)
        val searchEdit: EditText = findViewById(R.id.panel_search_edit)
        searchClear.setOnClickListener { searchEdit.setText("") }
        searchBack.setOnClickListener {
            flipperQuick.displayedChild=0
            FAB.show()
            searchLayout.visibility = View.GONE
            hideKeyboard(this)
            searchEdit.clearFocus()
            searchEdit.text.clear()
            (supportFragmentManager.findFragmentByTag("quickTopics") as? TopicQuickSelectFragment)?.onControlBack()
            (supportFragmentManager.findFragmentByTag("quickTopics") as? TopicQuickSelectFragment)?.onControlHideSearch()

        }
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                (supportFragmentManager.findFragmentByTag("quickTopics") as? TopicQuickSelectFragment)?.onControlafterTextChange(s.toString())

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                (supportFragmentManager.findFragmentByTag("quickTopics") as? TopicQuickSelectFragment)?.onControlonTextChange(s.toString())

            }
        })

         findViewById<ImageView>(R.id.tn_quick_search).setOnClickListener {

                if (flipperQuick.displayedChild==1) {
                    flipperQuick.displayedChild=0
                    FAB.show()
                    searchLayout.visibility = View.GONE
                    hideKeyboard(this)
                    searchEdit.clearFocus()
                    searchEdit.text.clear()
                    (supportFragmentManager.findFragmentByTag("quickTopics") as? TopicQuickSelectFragment)?.onControlBack()
                    (supportFragmentManager.findFragmentByTag("quickTopics") as? TopicQuickSelectFragment)?.onControlHideSearch()

                } else {
                    flipperQuick.displayedChild=1
                    FAB.hide()
                    searchEdit.requestFocus()
                    showKeyboard(this,searchEdit)
                    (supportFragmentManager.findFragmentByTag("quickTopics") as TopicQuickSelectFragment).onControlShowSearch()

                }
            }

    }

    private fun setContent(type: String) {
        Log.d("switchFragments","DATA: "+type)
        when (type) {
           CONTENT_FEED, CONTENT_TRENDING, CONTENT_NEW -> {
                val frag =
                    (supportFragmentManager.findFragmentByTag("content"))
                if (frag == null) {
                    loadContent(type)
                } else
                    (frag as MainContentFragment).setContent(type)
            }

            CONTENT_TOPICS -> {
                var frag =
                    (supportFragmentManager.findFragmentByTag("topics"))
                if (frag == null) frag = TopicOverviewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container_list, frag, "topics").commit()
            }
        }
    }


    fun getSocketBase(): SocketBase {
        val viewModel = ViewModelProvider(this).get(MasterViewModel::class.java)
        return viewModel.socketBase

    }

    private fun hideBottomSheet() {
        val bottomSheetBehavior: CustomBottomSheetBehavior<ConstraintLayout> =
            CustomBottomSheetBehavior.from(findViewById(R.id.cons))
        bottomSheetBehavior.state = CustomBottomSheetBehavior.STATE_HIDDEN

    }

    private fun showBottomSheet() {
        val bottomSheetBehavior: CustomBottomSheetBehavior<ConstraintLayout> =
            CustomBottomSheetBehavior.from(findViewById(R.id.cons))
        bottomSheetBehavior.state = CustomBottomSheetBehavior.STATE_COLLAPSED

    }

    private fun resetQuickHeader(){
        val searchLayout: ViewGroup = findViewById(R.id.tn_searchbar)
        val searchEdit: EditText = findViewById(R.id.panel_search_edit)
        val flipperQuick:ViewFlipper=findViewById(R.id.viewflipper_quick)
        flipperQuick.displayedChild=0
        FAB.show()
        searchLayout.visibility = View.GONE
        hideKeyboard(this)
        searchEdit.clearFocus()
        searchEdit.text.clear()

    }

}
