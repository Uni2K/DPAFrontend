package com.project.app.Dialogs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.project.app.Activities.HomeActivity
import com.project.app.Adapters.AccountChangeAdapter
import com.project.app.Bases.TextBase
import com.project.app.CustomViews.ToggleButton
import com.project.app.Helpers.Constants
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Objects.StoredUser
import com.project.app.Objects.User
import com.project.app.Paging.Displayable
import com.project.app.R

class LoggedInSettingsDialogFragment : DialogFragment() {
    private var userCredentials: StoredUser? = null
    private var userInformations: User? = null

    private lateinit var parent: HomeActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            parent = context as HomeActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleLogin)
        setHasOptionsMenu(true)

    }

    lateinit var flipWrapper: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView
    lateinit var userName: TextView
    lateinit var userDesc: TextView
    lateinit var userLocation: TextView
    lateinit var userTimeStamp: TextView
    lateinit var userFollower: TextView
    lateinit var userAvatar: ImageView
    lateinit var subGroup: ToggleButton
    lateinit var bnv: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_settings_account, null)

        recyclerView = view.findViewById(R.id.cd_content)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(parent, RecyclerView.VERTICAL, false)
        recyclerView.adapter = AccountChangeAdapter()


        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
       userCredentials = viewModel.localBase.getStoredLogin()
            //Check user on server
            userCredentials?.token?.let {

                viewModel.userBase.getAccount(
                    it, object : RetrofitHelper.Companion.DisplayableListCallback {
                        override fun onResponse(content: List<Displayable>) {
                            (content[0] as? User?)?.let { it1 -> setUser(it1, view) }
                        }


                        override fun onError(error: Int) {
                            dismiss()

                        }


                    })
            }




        flipWrapper = view.findViewById(R.id.flip_wrapper)
                val back=   view.findViewById<ImageView>(R.id.account_back)
      back.setOnClickListener {
            dismiss()
        }
        view.findViewById<ImageView>(R.id.account_save).setOnClickListener { saveSettings(view) }
        view.findViewById<TextView>(R.id.account_logout).setOnClickListener { logout() }
        view.findViewById<TextView>(R.id.account_logoutall).setOnClickListener { logoutAll() }

        return view
    }

    private fun logout() {
        showLoader()
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        val token = userCredentials?.token
        Log.d("logout","USERTOKEN: $token")
        if (!viewModel.userBase.validateToken(token)) {
            showError(Constants.ERROR_TOKENWRONG)
        } else {
            token?.let {

                showLoader()
                viewModel.userBase.logout(
                    it,
                    object : RetrofitHelper.Companion.DisplayableListCallback {
                        override fun onResponse(s: List<Displayable>) {
                            hideLoader()
                            viewModel.localBase.removeTokenFromStoredLogin()
                            viewModel.userBase.refreshAccountScreenTrigger.value = User(_id = "null")

                            dismiss()
                        }

                        override fun onError(s: Int) {
                            hideLoader()
                        }
                    })
            } ?: kotlin.run {
                hideLoader()
                showError(Constants.ERROR_TOKENWRONG)

            }

        }
    }

    private fun logoutAll() {
        showLoader()
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        val token = userCredentials?.token
        if (!viewModel.userBase.validateToken(token)) {
            showError(Constants.ERROR_TOKENWRONG)
        } else {
            token?.let {

                showLoader()


                viewModel.userBase.logoutAll(
                    it,
                    object : RetrofitHelper.Companion.DisplayableListCallback {
                        override fun onResponse(s: List<Displayable>) {
                            hideLoader()
                            viewModel.localBase.removeTokenFromStoredLogin()
                            viewModel.userBase.refreshAccountScreenTrigger.value = User(_id = "null")
                            dismiss()
                        }

                        override fun onError(s: Int) {
                            hideLoader()
                        }
                    })
            } ?: kotlin.run {
                hideLoader()
                showError(Constants.ERROR_TOKENWRONG)

            }

        }
    }


    private fun setUser(im: User, view: View) {
        userInformations=im
        userName = view.findViewById(R.id.account_username)
        userFollower = view.findViewById(R.id.account_followercount)
        userLocation = view.findViewById(R.id.account_country)
        userTimeStamp = view.findViewById(R.id.account_time)
        userAvatar = view.findViewById(R.id.account_avatar)
        subGroup = view.findViewById(R.id.subscribe)
        bnv = view.findViewById(R.id.account_bnv)
        userDesc = view.findViewById(R.id.account_bio)
        userName.background = null
        userFollower.background = null
        userTimeStamp.background = null
        userLocation.background = null
        (recyclerView.adapter as AccountChangeAdapter).setUser(im)
        userName.text = im.name


        userDesc.visibility = View.GONE
        userLocation.visibility = View.GONE
        subGroup.visibility = View.GONE
        bnv.visibility = View.GONE
        view.findViewById<View>(R.id.imageView3).visibility = View.GONE
        view.findViewById<View>(R.id.divider).visibility = View.GONE



        userTimeStamp.text = TextBase.formatUserTimestamp(im.timestamp)
        userLocation.text = TextBase.formatUserLocation(im.location)
        userFollower.text = TextBase.formatUserFollowers(im.subscriptions?.size?:0)
        TextBase.formatUserAvatar(im.avatar, userAvatar)



    }


    private fun saveSettings(view: View) {
        showLoader()
        val changes: ArrayList<Pair<String, String>> =
            (recyclerView.adapter as AccountChangeAdapter).getChanges()
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        val token = userCredentials?.token
        Log.e("save Settings", "DATA: ${token?.get(0)}")
        if (!viewModel.userBase.validateToken(token)) {
            showError(Constants.ERROR_TOKENWRONG)
        } else {
            token?.let {

                showLoader()

                viewModel.userBase.editUser(
                    it,
                    changes,
                    object : RetrofitHelper.Companion.DisplayableListCallback {
                        override fun onResponse(content: List<Displayable>) {
                            hideLoader()
                            (content[0] as? User?)?.let {

                                    it1 -> setUser(it1, view)
                                viewModel.userBase.refreshAccountScreenTrigger.value = it1

                            }


                            dismiss()

                        }

                        override fun onError(s: Int) {
                            hideLoader()
                        }
                    })
            } ?: kotlin.run { hideLoader() }

        }

    }


    private fun showLoader() {
        flipWrapper.isRefreshing = true

    }

    private fun hideLoader() {
        flipWrapper.isRefreshing = false
    }


    private fun showError(errorCode: Int) {
        var errorString = ""
        when (errorCode) {
            Constants.ERROR_WRONGPW -> {
                errorString = "You entered a wrong password!"
            }
            Constants.ERROR_NOUSERFOUND -> {
                errorString = "The entered email could not be found!"
            }
            Constants.ERROR_TIMEOUT -> {
                errorString = "Connection Timed out, please check your internet!"
            }
            Constants.ERROR_TOKENWRONG -> {
                errorString = "Server issue"
            }
            Constants.ERROR_DUPLICATEEMAIL -> {
                errorString = "Email already exists!"
            }
            Constants.ERROR_DUPLICATENAME -> {
                errorString = "Name already exists!"
            }
        }

        hideLoader()

        //Snackbar(view)
        val snackbar = Snackbar.make(
            flipWrapper, errorString,
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("OKAY") { snackbar.dismiss() }
        snackbar.duration = 3000
        snackbar.show()
    }

}