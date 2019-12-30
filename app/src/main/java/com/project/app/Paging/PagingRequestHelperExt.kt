package com.project.app.Paging

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning() -> {

                if(Looper.myLooper()== Looper.getMainLooper()){
                    liveData.setValue(NetworkState.LOADING)
                }else{
                    liveData.postValue(NetworkState.LOADING)
                }



            }
            report.hasError() -> {
                if(Looper.myLooper()== Looper.getMainLooper()){
                    liveData.setValue(  NetworkState.error(getErrorMessage(report)))
                }else{
                    liveData.postValue(
                        NetworkState.error(getErrorMessage(report)))                }


            }
            else ->{
                if(Looper.myLooper()== Looper.getMainLooper()){
                    liveData.setValue(NetworkState.LOADED)
                }else{
                    liveData.postValue(NetworkState.LOADED)
                }
            }
        }
    }
    return liveData
}