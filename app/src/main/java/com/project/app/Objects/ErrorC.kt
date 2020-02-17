package com.project.app.Objects

import android.content.Context
import com.project.app.Helpers.Constants
import com.project.app.R


data class ErrorC (val code: Int, val text: String?, val hint: String?, val importance: Int, val retryPossible: Boolean=false) {




    companion object {

         fun analyzeError(code: Int): Int {
            when (code) {
                301 -> {
                    //Wrong PW
                    return Constants.ERROR_WRONGPW
                }
                300 -> {
                    //Wrong EMAIL
                    return Constants.ERROR_NOUSERFOUND
                }
                400 -> {
                    //Wrong EMAIL
                    return Constants.ERROR_UNDEFINED
                }
                303 -> {
                    //Wrong EMAIL
                    return Constants.ERROR_NOUSERFOUND
                }
                304 -> {
                    //Wrong EMAIL
                    return Constants.ERROR_DUPLICATEEMAIL
                }
                305 -> {
                    //Wrong EMAIL
                    return Constants.ERROR_DUPLICATENAME
                }
                306 -> {
                    //Wrong EMAIL
                    return Constants.ERROR_NOQUESTION
                }
                307 -> {
                    //Wrong EMAIL
                    return Constants.ERROR_ANSWERINDEX
                }
                308 -> {
                    //Wrong EMAIL
                    return Constants.ERROR_UNDEFINED_VOTING
                }


            }
            return Constants.ERROR_UNDEFINED
        }

        fun createError(code: Int, c: Context): ErrorC {
            when (code) {
                Constants.ERROR_NOCONNECTION -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_NOCONNECTION),
                        str(c, R.string.ERROR_NOCONNECTION_HINT),
                        Constants.IMPORTANCE_LVL3
                    )
                }
                Constants.ERROR_UNDEFINED -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_UNDEFINED),
                        str(c, R.string.ERROR_UNDEFINED_HINT),
                        Constants.IMPORTANCE_LVL1
                    )
                }
                Constants.ERROR_TOKENWRONG -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_TOKENWRONG),
                        str(c, R.string.ERROR_TOKENWRONG_HINT),
                        Constants.IMPORTANCE_LVL1
                    )
                }
                Constants.ERROR_TIMEOUT -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_SOCKET_TIMEOUT),
                        str(c, R.string.ERROR_SOCKET_TIMEOUT_HINT),
                        Constants.IMPORTANCE_LVL2
                    )
                }
                Constants.ERROR_NOUSERFOUND -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_NOUSERFOUND),
                        str(c, R.string.ERROR_NOUSERFOUND_HINT),
                        Constants.IMPORTANCE_LVL1
                    )
                }
                Constants.ERROR_DUPLICATENAME -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_DUPLICATENAME),
                        str(c, R.string.ERROR_DUPLICATENAME_HINT),
                        Constants.IMPORTANCE_LVL1
                    )
                }
                Constants.ERROR_DUPLICATEEMAIL -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_DUPLICATEMAIL),
                        str(c, R.string.ERROR_DUPLICATEMAIL_HINT),
                        Constants.IMPORTANCE_LVL1
                    )
                }
                Constants.ERROR_NOCONTENT -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_NOCONTENT),
                        str(c, R.string.ERROR_NOCONTENT_HINT),
                        Constants.IMPORTANCE_LVL1
                    )
                }
                Constants.ERROR_NOQUESTION -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_NOQUESTION),
                        str(c, R.string.ERROR_NOQUESTION_HINT),
                        Constants.IMPORTANCE_LVL1
                    )
                }
                Constants.ERROR_ANSWERINDEX -> {
                    return ErrorC(
                        code,
                        str(c, R.string.ERROR_ANSWERINDEX),
                        str(c, R.string.ERROR_ANSWERINDEX_HINT),
                        Constants.IMPORTANCE_LVL1
                    )
                }

            }
            return ErrorC(
                code,
                str(c, R.string.ERROR_UNDEFINED),
                str(c, R.string.ERROR_UNDEFINED_HINT),
                Constants.IMPORTANCE_LVL1
            )

        }

         fun str(context: Context, id: Int): String {
            return context.getString(id)
        }
    }


}