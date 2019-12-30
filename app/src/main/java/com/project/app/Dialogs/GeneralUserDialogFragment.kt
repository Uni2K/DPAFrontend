package com.project.app.Dialogs

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Objects.StoredUser
import com.project.app.Objects.User
import com.project.app.Paging.Displayable
import com.project.app.R

class GeneralUserDialogFragment : AccountProviderDialogFragment() {
    private var isFollowing: Boolean = false

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.account, menu)
        menu.findItem(R.id.settings).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val re=super.onCreateView(inflater, container, savedInstanceState)
        userID?.let { loadUser(it) } ?: kotlin.run {
            showError()
        }
        return re



    }


    private fun showError() {
        try {
            dismiss()
        }catch (e:Exception){
        }
        Toast.makeText(activity, "User not available!", Toast.LENGTH_LONG).show()

    }


    private fun loadUser(userID: String) {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        viewModel.userBase.getUser(userID,
            object : RetrofitHelper.Companion.DisplayableListCallback {
                override fun onResponse(content: List<Displayable>) {
                    val user = content[0] as User
                    setUser(user)
                }

                override fun onError(error: Int) {
                    Toast.makeText(activity, "User not available!", Toast.LENGTH_LONG).show()
                    dismiss()
                }
            })
    }


    private fun setIsFollowing() {
        isFollowing = true
        subscribe.toggle("Subscribed", true)


    }

    private fun setIsNotFollowing() {

        isFollowing = false
        subscribe.toggle("Subscribe", false)

    }


    fun setUser(im: User) {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        super.setUserInformations(im)
        checkFollowing()
        setUpSubscribe(im)
        //Set Subscribe Button
        viewModel.localBase.getStoredLogin()?.let {
            subscribe.visibility = View.VISIBLE
        }
        viewModel.userBase.loggedInUserChanged.observe(this, Observer {user->
            user?.let {
               val isSub:Boolean= viewModel.userBase.isSubscribed(im._id,it)
                if(isSub){
                    setIsFollowing()
                }else{
                    setIsNotFollowing()
                }
            }?: kotlin.run { setIsNotFollowing() }


        })

    }


    /**
     * Check if the logged in user is following the user of the account page
     */
    private fun checkFollowing() {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        val storedUser: StoredUser? = viewModel.localBase.getStoredLogin()
        if (viewModel.userBase.validateToken(storedUser?.token)) {
            //GOOD TOKEN PROBABLY
            storedUser?.token?.let {
                viewModel.userBase.getAccount(
                    it,
                    object : RetrofitHelper.Companion.DisplayableListCallback {
                        override fun onResponse(content: List<Displayable>) {
                            viewModel.userBase.updateUser(content[0])

                        }

                        override fun onError(error: Int) {
                            setIsNotFollowing()

                        }
                    })
            }


        }
    }


    private fun setUpSubscribe(im: User) {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        subscribe.setOnClickListener {
            val token = viewModel.localBase.getStoredLogin()?.token
            if (token != null) {

                if (isFollowing) {
                    im._id.let {
                        viewModel.userBase.unsubscribe(
                            token,
                            it,
                            object : RetrofitHelper.Companion.DisplayableListCallback {
                                override fun onResponse(s: List<Displayable>) {
                                    viewModel.userBase.updateUser(s[0])
                                }

                                override fun onError(error: Int) {
                                    Toast.makeText(activity, "NOT UnSubscribed", Toast.LENGTH_LONG)
                                        .show()

                                }


                            })
                    }
                } else {
                    im._id.let {
                        viewModel.userBase.subscribe(
                            token,
                            it,
                            object : RetrofitHelper.Companion.DisplayableListCallback {
                                override fun onResponse(s: List<Displayable>) {
                                    viewModel.userBase.updateUser(s[0])

                                }

                                override fun onError(error: Int) {
                                    Toast.makeText(activity, "dfSubscribed", Toast.LENGTH_LONG)
                                        .show()

                                }


                            })
                    }
                }


            } else {
                openLogin(2)

            }
        }


    }
}
