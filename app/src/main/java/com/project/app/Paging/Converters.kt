package com.project.app.Paging

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.app.Objects.Subscription
import com.project.app.Objects.Topic
import com.project.app.Objects.User

class Converters {

    @TypeConverter
    fun stringArrayToJson(value: Array<String>?): String {
        return value?.joinToString()?:""
    }
    @TypeConverter
    fun stringListToJson(value: List<String>?): String {
        return Gson().toJson(value)
    }
    @TypeConverter
    fun jsonToStringArray(value: String): Array<String>? {
        return value.split(",").toTypedArray()
    }
    @TypeConverter
    fun jsonToStringList(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {
        }.type
        return Gson().fromJson(value,listType) as List<String>?
    }

    @TypeConverter
    fun intArrayToJson(value: Array<Int>?): String {
        return value?.joinToString()?:""
    }

    @TypeConverter
    fun jsonToIntArray(value: String): Array<Int>? {

        return value.split(",").map { it.trim().toInt() }.toTypedArray()
    }
    @TypeConverter
    fun tagToJson(value: Topic?): String {
        return Gson().toJson(value)
    }
    @TypeConverter
    fun userToJson(value: User?): String {
        return Gson().toJson(value)
    }
    @TypeConverter
    fun subToJson(value: Subscription?): String {
        return Gson().toJson(value)
    }


    @TypeConverter
    fun jsonToTag(value: String): Topic? {
        return Gson().fromJson(value, Topic::class.java)
    }
    @TypeConverter
    fun jsonToUser(value: String): User? {
            val user=Gson().fromJson(value, User::class.java)

        return user?:null
    }




    @TypeConverter
    fun jsonToSubArray(value: String): Array<Subscription>? {
        val listType = object : TypeToken<Array<Subscription>?>() {
        }.type
        return Gson().fromJson(value,listType) as Array<Subscription>?
    }

}