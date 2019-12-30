package com.project.app.Helpers

import android.util.Log
import com.project.app.Objects.ErrorC
import com.project.app.Objects.ParsingConfig
import com.project.app.Objects.Topic
import com.project.app.Objects.User
import com.project.app.Paging.Displayable
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.HttpException

class RetrofitHelper {
    companion object {

        interface RetrofitCallback {
            fun onError(errorCode: Int)
            fun onResponse(s: String)

        }

        interface DisplayableListCallback {
            fun onError(errorCode: Int)
            fun onResponse(content: List<Displayable>)
        }

        interface SensitiveUserDataCallback {
            fun onError(errorCode: Int)
            fun onResponse(user: User, token: String, email: String)
        }

        interface TopicListCallback {
            fun onError(errorCode: Int)
            fun onResponse(content: List<Topic>)
        }


        interface ResponseCallback {
            fun onResponse(content: String?)
            fun onError(errorCode: Int)

        }


        /**
         * Handles Timeout Errors and HTTP Errors
         */
        fun launchControlled(
            masterViewModel: MasterViewModel, parsingConfig: ParsingConfig?,
            callback: DisplayableListCallback?,
            codeToExecute: suspend () -> ResponseBody?, responseCallback: ResponseCallback? = null,
            parse: Boolean = true
        ): Job {


            return GlobalScope.launch {
                try {
                    withTimeout(6000) {
                        try {

                            val response: ResponseBody? = codeToExecute()
                            val string = response?.string()
                            launch(Dispatchers.Main) { responseCallback?.onResponse(string) }
                            Log.d("launchControlled", "DATA: $string")
                            string?.let {
                                if (parse) {
                                    callback?.let { it1 ->
                                        masterViewModel.parseContent(
                                            parsingConfig,
                                            it,
                                            it1
                                        )

                                    }

                                }
                            } ?: kotlin.run {
                                launch(Dispatchers.Main) { callback?.onError(Constants.ERROR_UNDEFINED) }
                            }


                        } catch (exception: HttpException) {
                            Log.d("DEBUG", "launchControlled: HTTP ERROR ${exception.code()}")

                            launch(Dispatchers.Main) {
                                responseCallback?.onError(
                                    ErrorC.analyzeError(
                                        exception.code()
                                    )
                                )
                                callback?.onError(
                                    ErrorC.analyzeError(
                                        exception.code()
                                    )
                                )
                            }
                        }
                    }


                } catch (ex: Exception) {

                    launch(Dispatchers.Main) {
                        callback?.onError(Constants.ERROR_TIMEOUT)
                        responseCallback?.onError(Constants.ERROR_TIMEOUT)
                    }
                }


            }



        }

    }
}