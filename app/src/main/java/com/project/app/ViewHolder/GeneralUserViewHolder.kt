package com.project.app.ViewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Bases.TextBase
import com.project.app.CustomViews.SubscribeButton
import com.project.app.Objects.User
import com.project.app.R
import kotlin.system.measureTimeMillis

class GeneralUserViewHolder(
    val v: View
) : RecyclerView.ViewHolder(v) {

    var user: User? = null
    var name: TextView = v.findViewById<TextView>(R.id.adap_name)
    var location: TextView = v.findViewById(R.id.adap_stat_1t)
    var timestamp: TextView = v.findViewById(R.id.adap_stat_2t)
    var reputation: TextView = v.findViewById(R.id.adap_stat_3t)
    var desc: TextView = v.findViewById(R.id.adap_context)
    var subscribe: SubscribeButton = v.findViewById(R.id.adap_tb)
    var avatar: ImageView = v.findViewById(R.id.adap_avatar)
    var info:TextView=v.findViewById(R.id.adap_timestamp)

    fun bind(user_: User?) {
        val l = measureTimeMillis {
            if (user_ != null) {
                user = user_
            } else {
                showUserError()
                return
            }
            populateUser(user_)
        }
    }


    private fun showUserError() {

    }


    private fun populateUser(qst: User) {

        name.text = qst.name
        location.text = TextBase.formatUserLocation(qst.location)
        timestamp.text = qst.timestamp?.substring(0, 4)
        reputation.text = qst.reputation.toString()
        desc.text = qst.desc
        subscribe.setOnClickListener {

        }
        info.text=TextBase.formatUserFollowers(qst.subscriptions?.size?:0)
        updateAvatar(qst)


    }

    fun updateAvatar(qst: User?) {
      TextBase.formatUserAvatar(qst?.avatar,avatar)


    }

    fun updateName(qst: User?) {
        if(qst==null)return
        name.text = qst.name

    }

    fun updateDesc(qst: User?) {
        if(qst==null)return
        desc.text=qst.desc
    }

    fun updateUrl(qst: User?) {
        if(qst==null)return
    }

    fun updateReputation(qst: User?) {
        if(qst==null)return
        reputation.text=qst.reputation.toString()

    }

    fun updateLocation(qst: User?) {
        if(qst==null)return
        location.text=qst.location

    }

    fun updateFollowercount(qst: User?) {
        if(qst==null)return
        info.text=TextBase.formatUserFollowers(qst.subscriptions?.size?:0)

    }


}