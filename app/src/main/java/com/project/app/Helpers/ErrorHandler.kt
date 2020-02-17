package com.project.app.Helpers

import android.view.View
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.project.app.Activities.HomeActivity
import com.project.app.Bases.AssistantBase
import com.project.app.CustomViews.ExpandableSnackBar
import com.project.app.Objects.ErrorC


/**
 *  What if the socket loses connection? What if it cant even connect once? -> show SnackBar
 *  Where to show this snackbar? Everywhere content is wanted! -> ContentLoader
 *
 *
 *  No Content-> Managed by ContentLoader, no app uniform motive -> Callback to Fragment
 *  Error in Content requests , Request Timeout etc. -> Callback to Fragment
 *  Loading: In Content Loader -> fully managed
 *  User/HTTP Errors -> handled in Fragments itself
 *
 */
class ErrorHandler(
    val parent: HomeActivity,
    lifecycleOwner: LifecycleOwner,
    private val assistantBase: AssistantBase
) :
    ConnectionStateMonitor.OnNetworkAvailableCallbacks {
    override fun onPositive() {
        hideErrorMessage(Constants.ERROR_NOCONNECTION)
    }

    override fun onNegative() {
        showErrorMessage(Constants.ERROR_NOCONNECTION)
    }

    private var previousErrorC: ErrorC ?= null
    private var connectionStateMonitor: ConnectionStateMonitor? = null

    init {
        if (connectionStateMonitor == null)
            connectionStateMonitor = ConnectionStateMonitor(parent, this)


        /**
         * Actually works
         */
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                pause()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                resume()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
            }


        })
    }

    fun pause() {
        connectionStateMonitor?.disable()
    }




    fun resume() {
        connectionStateMonitor?.enable()
        if (connectionStateMonitor?.hasNetworkConnection() == false) onNegative()
        else onPositive()
    }

    /**
     * code: Hide only this one
     */
  fun hideErrorMessage(code: Int?=null) {
      if(code!=null){
          if(previousErrorC?.code!= code){
              return
          }
      }

        previousErrorC = null
        assistantBase.hideErrorMessage()

    }




    /**
     * Some errors are more important than other ones -> ERROR Classes
     */
     fun showErrorMessage(errorCode: Int, duration: Int=Snackbar.LENGTH_INDEFINITE) {
        val error=ErrorC.createError(errorCode,parent)
        var showNew=true
        if (error.code == previousErrorC?.code) {
            return
        }

        previousErrorC?.let {
            if(it.importance>error.importance){
                showNew=false
            }
        }
        if(!showNew)return
        assistantBase.hideErrorMessage()
        previousErrorC = error



          previousErrorC?.let { im->
              assistantBase.showErrorMessage(im)




       }


    }







}