package com.project.app.Objects

import android.os.Bundle
import com.project.app.Paging.Displayable

data class User(

    var name: String? = null,
    var desc: String? = null,
    var followercount: Long? = null,
    var location: String? = null,
    var timestamp: String? = null,
    var subscriptions: Array<Subscription>? = emptyArray(),
    var url: String? = null,
    var avatar: String? = null,
    var reputation: Long? = null,
    var _id: String

    ) : Displayable(type= Displayable.USER,data=this) {

    constructor(
        q: ContentModel
    ) : this(
        _id = q.id,
        avatar = q.u_avatar,
        desc = q.u_desc,
        followercount = q.u_followercount,
        subscriptions = ContentModel.convertSubscriptionsBack(q.u_subscriptions),
        location = q.u_location,
        name = q.u_name,
        reputation = q.u_reputation,
        timestamp = q.q_timestamp,
        url = q.u_url
    ){
        index = q.index
        contentname = q.contentname
        id=q.id

    }


    fun getChangePayload(UserOld: User, UserNew: User): Bundle {

        val diff = Bundle()

        if (UserNew.avatar != UserOld.avatar) {
            diff.putString("avatar", UserNew.avatar)
        }
        if (UserNew.desc != UserOld.desc) {
            diff.putString("desc", UserNew.desc)
        }
        if (UserNew.location != UserOld.location) {
            diff.putString("location", UserNew.location)

        }
        if (UserNew.followercount != UserOld.followercount) {
            diff.putLong("followercount", UserNew.followercount ?: 0L)
        }
        if (UserNew.url != UserOld.url) {
            diff.putString("location", UserNew.location)
        }
        if (UserNew.reputation != UserOld.reputation) {
            diff.putLong("reputation", UserNew.reputation ?: 0L)
        }
        if (UserNew.name != UserOld.name) {
            diff.putString("name", UserNew.name)
        }

        if (diff.size() == 0) {
            diff.putBoolean("allEqual", true)
        }
        return diff

    }

    fun compareTo(newItem: User?): Int {
        if(newItem==null)return 10
        if (this._id != newItem._id) return 0
        if (this.name != newItem.name) return 1
        if (this.reputation != newItem.reputation) return 3
        if (this.avatar != newItem.avatar) return 4
        if (this.url != newItem.url) return 5
        if (this.location != newItem.location) return 6
        if (this.followercount != newItem.followercount) return 7
        if (this.desc != newItem.desc) return 8
        return 10

    }
}


    
