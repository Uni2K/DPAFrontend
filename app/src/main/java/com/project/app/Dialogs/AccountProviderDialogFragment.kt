package com.project.app.Dialogs

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.project.app.Bases.TextBase
import com.project.app.CustomViews.SubscribeButton
import com.project.app.Helpers.Constants
import com.project.app.Objects.User
import com.project.app.Helpers.ContentLoader
import com.project.app.R


open class AccountProviderDialogFragment : QuestionControllerDialogFragment() {


    lateinit var recycler: RecyclerView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var swipeLayout: SwipeRefreshLayout
    lateinit var contentLoader: ContentLoader
    lateinit var parentView: View

    lateinit var userName: TextView
    lateinit var userDesc: TextView
    lateinit var userLocation: TextView
    lateinit var userTimeStamp: TextView
    lateinit var userFollower: TextView
    lateinit var userAvatar: ImageView
    lateinit var subscribe: SubscribeButton
    lateinit var menu: Menu
    lateinit var tabLayout: TabLayout
    var userID: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        setHasOptionsMenu(true)
        userID = arguments?.getString("userid", null).toString()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_account, null)
      //  view.findViewById<View>(R.id.account_back).setOnClickListener { dismiss() }
        parentView = view
        tabLayout = view.findViewById<TabLayout>(R.id.account_bnv)

        
        recycler = view.findViewById(R.id.cd_content)
        swipeLayout = view.findViewById(R.id.cd_swipelayout)
        toolbar = view.findViewById(R.id.toolbar)
        subscribe = view.findViewById(R.id.toolbarSubscripe)
        userName = view.findViewById(R.id.account_username)
        userDesc = view.findViewById(R.id.account_bio)
        userFollower = view.findViewById(R.id.account_followercount)
        userLocation = view.findViewById(R.id.account_country)
        userTimeStamp = view.findViewById(R.id.account_time)
        userAvatar = view.findViewById(R.id.account_avatar)

        recycler.layoutManager = LinearLayoutManager(context)
        swipeLayout.isEnabled = false
        recycler.setHasFixedSize(true)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)

        tabLayout.addTab(tabLayout.newTab().setText("News").setIcon(R.drawable.baseline_rss_feed_24))
        tabLayout.addTab(tabLayout.newTab().setText("Voted").setIcon(R.drawable.baseline_how_to_vote_24))
        tabLayout.addTab(tabLayout.newTab().setText("Subscribed").setIcon(R.drawable.baseline_favorite_24))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                
            }
        })
        
        
        start()
        return view
    }


    private fun start() {
      /*  tabLayout.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.following -> {
                    userID?.let { it1 ->
                        contentLoader.enqueueContent(
                            Constants.CONTENT_USER_FOLLOWING,
                            it1
                        )
                    }
                }

                R.id.voted -> {
                    userID?.let { it1 ->
                        contentLoader.enqueueContent(
                            Constants.CONTENT_USER_VOTED,
                            it1
                        )
                    }
                }
                R.id.news -> {
                    userID?.let { it1 ->
                        contentLoader.enqueueContent(
                            Constants.CONTENT_USER_ASKED,
                            it1
                        )
                    }
                }
            }
            true
        }
*/

    }


    fun setUserInformations(im: User) {
        this.userID = im._id
        userName.background = null
        userDesc.background = null
        userFollower.background = null
        userTimeStamp.background = null
        userLocation.background = null
        userAvatar.background = null
        userName.text = im.name
        userDesc.text = im.desc
        userTimeStamp.text = TextBase.formatUserTimestamp(im.timestamp)
        userLocation.text = TextBase.formatUserLocation(im.location)
        userFollower.text = TextBase.formatUserFollowers(im.subscriptions?.size?:0)
        TextBase.formatUserAvatar(im.avatar, userAvatar)


        if (!::contentLoader.isInitialized) {
            im._id.let { il ->
                contentLoader =
                    ContentLoader(
                        this@AccountProviderDialogFragment,
                        activity,
                        parentView
                    )

                contentLoader.enqueueContent(  Constants.CONTENT_USER_ASKED,il)
            }

        } else {
            im._id.let {
                contentLoader.enqueueContent(Constants.CONTENT_USER_ASKED, it)
            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                dismiss()
            }
            R.id.settings -> {
                showSettings()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showSettings() {
        LoggedInSettingsDialogFragment().show(childFragmentManager, "settingsAccount")
    }


    fun openLogin(debug: Int) {
        LoginDialogFragment().show(activity.supportFragmentManager, "login")
        dismiss()
    }


}