package com.project.app.Dialogs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.project.app.Activities.HomeActivity
import com.project.app.Bases.TextBase
import com.project.app.CustomViews.ActionButton
import com.project.app.Helpers.Constants
import com.project.app.Helpers.MasterViewModel
import com.project.app.Helpers.RetrofitHelper
import com.project.app.Objects.StoredUser
import com.project.app.Objects.User
import com.project.app.R

class LoginDialogFragment : DialogFragment() {
    private lateinit var parent: HomeActivity

    interface signUpCallback {
        fun onResponse(s: User)
        fun onError(s: Int)

    }
    interface loginCallback {
        fun onResponse(s: User?)
        fun onError(s: Int)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            parent = context as HomeActivity


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleLogin)
    }

    lateinit var flipper: ViewFlipper
    lateinit var tabLayout: TabLayout
    lateinit var flipWrapper: SwipeRefreshLayout
    lateinit var parentView: ConstraintLayout
    var storedUser: StoredUser?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_login, null)

        tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        flipper = view.findViewById<ViewFlipper>(R.id.login_flipper)
        flipWrapper = view.findViewById(R.id.flip_wrapper)
        flipWrapper.isEnabled=false
        parentView=view.findViewById(R.id.parentView)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { flipper.displayedChild = it.position }
            }
        })

        val qmodel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        val user: StoredUser? = qmodel.localBase.getStoredLogin()

        if (user != null) {
         setUserFoundMode(user,view)
        }else{
            setDefaultLogin(view)

        }
        tabLayout.addTab(tabLayout.newTab().setText("Login"))
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"))
        setUpLogin(view)
        setUpSignIn(view)
        return view
    }

    private fun setUserFoundMode(user:StoredUser,view:View) {
        Log.e("userFound","LOGIN")
        val userImage = view.findViewById<ImageView>(R.id.login_icon)

        TextBase.formatUserAvatar(user.avatar,userImage)

        val userGroup=view.findViewById<Group>(R.id.userGroup)
        val loginNameGroup=view.findViewById<Group>(R.id.loginNameGroup)
        userGroup.visibility=View.VISIBLE
        loginNameGroup.visibility=View.GONE
        val userName=view.findViewById<TextView>(R.id.login_user)
        userName.text=user.name
        storedUser=user
    }

    private fun setDefaultLogin(view:View) {
        val userGroup=view.findViewById<Group>(R.id.userGroup)
        val loginNameGroup=view.findViewById<Group>(R.id.loginNameGroup)
        userGroup.visibility=View.GONE
        loginNameGroup.visibility=View.VISIBLE
        storedUser=null
    }

    private fun setUpSignIn(view: View) {
        val signupGO = view.findViewById<ActionButton>(R.id.signup_go)
        val clearEmail = view.findViewById<ImageView>(R.id.signup_email_clear)
        val clearPW = view.findViewById<ImageView>(R.id.signup_pw_clear)
        val password = view.findViewById<EditText>(R.id.signup_pw)
        val email = view.findViewById<EditText>(R.id.signup_email)
        val name = view.findViewById<EditText>(R.id.name)
        val clearName = view.findViewById<ImageView>(R.id.name_clear)
        setUpEditText(password, clearPW, object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        setUpEditText(email, clearEmail, object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        setUpEditText(name, clearName, object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        signupGO.setOnClickListener {
            if(!isLoading()){
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            if (name.text.length > 2) {
                if (email.text.trim().matches(Regex(emailPattern))) {
                    if (password.length() > 3) {
                        signUp(
                            email.text.toString(),
                            password.text.toString(),
                            name.text.toString()
                        )
                    } else {
                        errorMessage("Please enter a valid password (4 letters at least)")

                    }
                } else {
                    errorMessage("Please enter a valid email!")
                }
            } else {
                errorMessage("Please enter a valid username! (3 letters at least)")

            }
        }}
    }

    private fun errorMessage(s: String) {
        Toast.makeText(parent, s, Toast.LENGTH_LONG).show()
    }

    private fun signUp(email: String, pw: String, name: String) {
        showLoader()
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        viewModel.userBase.signUp(email, name, pw, object : RetrofitHelper.Companion.SensitiveUserDataCallback {
            override fun onResponse(user: User, token: String, email: String) {
                hideLoader()

                viewModel.localBase.addStoredLogin(user,token,email)
                viewModel.userBase.updateUser(user)
                openAccount()
            }


            override fun onError(s: Int) {
               hideLoader()
                showError(s)
            }

        })

    }

    private fun openAccount(){
        parent.openAccount("-1")
        dismiss()

    }

    private fun showLoader() {
        flipWrapper.isRefreshing=true

    }
    private fun hideLoader() {
        flipWrapper.isRefreshing=false
    }

    private fun setUpLogin(view: View) {

        val loginGO = view.findViewById<ActionButton>(R.id.login_go)
        val forgotPWEdit = view.findViewById<TextView>(R.id.login_forgotpw)
        val changeUserEdit = view.findViewById<TextView>(R.id.login_changeuser)
        val clearEmail = view.findViewById<ImageView>(R.id.login_email_clear)
        val clearPW = view.findViewById<ImageView>(R.id.login_pw_clear)
        val password = view.findViewById<EditText>(R.id.login_pw)
        val email = view.findViewById<EditText>(R.id.login_email)
        setUpEditText(password, clearPW, object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        setUpEditText(email, clearEmail, object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        
        
        

        loginGO.setOnClickListener {
            if(!isLoading()){

            if (storedUser != null) {
                //USER Stored
                if (password.length() > 3) {
                    //Login with stored email and entered pw
                    login(
                        storedUser!!.email,
                        password.text.toString()
                    )
                } else {
                    errorMessage("Please enter a valid password (4 letters at least)")
                }
            } else {
                //No User stored

                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                if (email.text.trim().matches(Regex(emailPattern))) {
                    if (password.length() > 3) {
                        login(
                            email.text.toString(),
                            password.text.toString()
                        )
                    } else {
                        errorMessage("Please enter a valid password (4 letters at least)")

                    }
                } else {
                    errorMessage("Please enter a valid email!")
                }
            }
        }}
        

        changeUserEdit.setOnClickListener {
            changeUser(view)
        }
        forgotPWEdit.setOnClickListener {
            // forgotPW()
        }
    }

    private fun login(email: String, pw: String) {
        showLoader()
        val viewModel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        viewModel.userBase.login(email,pw, object : RetrofitHelper.Companion.SensitiveUserDataCallback{
            override fun onResponse(user: User, token: String, email: String) {

                hideLoader()
                viewModel.localBase.addStoredLogin(user,token,email)
                viewModel.userBase.updateUser(user)
                openAccount()


            }



            override fun onError(s: Int) {
                Log.d("onError", "s: $s")
                showError(s)
                hideLoader()
            }
        })
    }


    private fun showError(errorCode: Int){
        var errorString=""
        when(errorCode){
            Constants.ERROR_WRONGPW->{
                errorString="You entered a wrong password!"
            }
            Constants.ERROR_NOUSERFOUND->{
                errorString="The entered email could not be found!"
            }
            Constants.ERROR_TIMEOUT->{
                errorString="Connection Timed out, please check your internet!"
            }
            Constants.ERROR_DUPLICATEEMAIL->{
                errorString="Email already exists!"
            }
            Constants.ERROR_DUPLICATENAME->{
                errorString="Name already exists!"
            }
        }
        Log.e("error","DATA: $errorCode")


        //Snackbar(view)
        val snackbar = Snackbar.make(parentView, errorString,
            Snackbar.LENGTH_LONG)
        snackbar.setAction("OKAY") {snackbar.dismiss()}
        snackbar.duration=3000
        snackbar.show()
    }

    private fun isLoading(): Boolean {
    return flipWrapper.isRefreshing
    }

    private fun changeUser(view: View) {
        forgetLastLogin()
        setDefaultLogin(view)
    }

    private fun forgetLastLogin() {
        val qmodel = ViewModelProvider(parent).get(MasterViewModel::class.java)
        qmodel.localBase.removeStoredLogin()
    }

    private fun setUpEditText(loginPW: EditText, clear: ImageView, callback: TextWatcher) {
        loginPW.addTextChangedListener(callback)
        clear.setOnClickListener { loginPW.setText("") }
    }


}