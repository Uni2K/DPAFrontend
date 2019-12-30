package com.project.app.Bases

import android.util.Log
import com.project.app.Helpers.Constants
import com.project.app.Helpers.MasterViewModel
import com.project.app.Interfaces.SnapshotHelper
import com.project.app.Objects.QuestionSnapshot
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Only used to inform of: tagChange, or special events occuring
 */
class SocketBase(val viewModel: MasterViewModel) {

    var socket: Socket = IO.socket(Constants.SERVER_BASEURL + ":3000")


    init {

        socket.on("onTagsChanged") {
            GlobalScope.launch {
                viewModel.topicBase.getAllTopics()
            }
        }


        try {
            socket.connect()
        } catch (exception: SocketTimeoutException) {
            Log.d("socket", "timedout")

        }
    }


    fun onStart() {
        socket.connect()
    }

    fun onStop() {
        socket.disconnect()
    }


    fun getSnapshots(_id: String, snapshotHelper: SnapshotHelper) {
        GlobalScope.launch {
            val array = ArrayList<QuestionSnapshot>()
            val now = System.currentTimeMillis()
            val random = Math.min(0, Random().nextInt(1000))
            Log.d("getSnapshots", "RANDOM: $random")
            array.add(
                QuestionSnapshot(
                    (now - 30000).toString(),
                    _id,
                    intArrayOf(22, 23, 32, 44).toTypedArray()
                )
            )
            array.add(
                QuestionSnapshot(
                    (now - 40000).toString(),
                    _id,
                    intArrayOf(22, 338, 232, 434).toTypedArray()
                )
            )
            array.add(
                QuestionSnapshot(
                    (now - 20000).toString(),
                    _id,
                    intArrayOf(22, 32223, 22332, 44).toTypedArray()
                )
            )
            array.add(
                QuestionSnapshot(
                    (now - 10000).toString(),
                    _id,
                    intArrayOf(22, 33, 232, 44).toTypedArray()
                )
            )
            snapshotHelper.onSnapshotsReady(array)
        }
    }


}