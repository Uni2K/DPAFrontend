package com.project.app.Dialogs

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Objects.StoredUser
import com.project.app.Objects.User
import com.project.app.Paging.Displayable
import com.project.app.R

class LoggedInUserDialogFragment : AccountProviderDialogFragment() {


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.account, menu)
        menu.findItem(R.id.settings).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val re = super.onCreateView(inflater, container, savedInstanceState)

        subscribe.visibility = View.GONE
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)
        viewModel.userBase.refreshAccountScreenTrigger.observe(this, Observer {
            //Refresh After Settings
            //FIX with "null" -> when using null for logout, called everytime when creating fragment -> immediate openLogin
            if (it?._id !="null" && it!=null) {
                setUser(it)
                viewModel.userBase.refreshAccountScreenTrigger.value=null
            } else if(it?._id =="null") {
                //LOGOUT
                openLogin(1)
                dismiss()
                viewModel.userBase.refreshAccountScreenTrigger.value=null
            }

        })
        loadUser()
        return re
    }







    fun setUser(im: User) {
        super.setUserInformations(im)
    }


    private fun loadUser() {
        val viewModel = ViewModelProvider(activity).get(MasterViewModel::class.java)

            val storedUser: StoredUser? = viewModel.localBase.getStoredLogin()
            if (storedUser == null) {
                //LOGIN AGAIN
                openLogin(3)
                return
            }

            if (viewModel.userBase.validateToken(storedUser.token)) {
                //GOOD TOKEN PROBABLY
                storedUser.token?.let {
                    viewModel.userBase.getAccount(
                        it,
                        object : RetrofitHelper.Companion.DisplayableListCallback {
                            override fun onResponse(content: List<Displayable>) {
                                val user: User? = content[0] as? User
                                user?.let {
                                    setUser(it)
                                } ?: run {
                                    openLogin(40)
                                }

                            }

                            override fun onError(error: Int) {
                                openLogin(4)
                            }
                        })
                }


            } else {
                //BAD TOKEN -> keep account for username preview on login Page
                Log.e("loadUser", "BAD TOKEN ")
                viewModel.localBase.removeTokenFromStoredLogin()
                openLogin(5)
            }





    }



}
