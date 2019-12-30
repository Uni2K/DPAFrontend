package com.project.app.Bases

import android.os.Build
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.project.app.Objects.Question
import com.project.app.Objects.Subscription
import com.project.app.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Containing all the formatting stuff in a static fashion
 */
class TextBase {


    companion object {


        fun formatExpirationTimeStamp(qstTime: Long?=0L): Pair<String, Long> {
            if(qstTime==null) return Pair("expired", -1L)
            val difference: Long = qstTime - System.currentTimeMillis()
            var delay = 5000L

            if (difference < 0) {
                return Pair("expired", -1L)
            }
            var diff = TimeUnit.MILLISECONDS.toSeconds(difference)

            var str = TimeUnit.MILLISECONDS.toSeconds(difference).toString() + "s valid"
            delay = 1000L
            if (diff > 60) {
                str = TimeUnit.MILLISECONDS.toMinutes(difference).toString() + "min valid"
                delay = 30000L

            }
            if (diff > 60 * 60) {
                str = TimeUnit.MILLISECONDS.toHours(difference).toString() + "h valid"
                delay = 60000L

            }
            if (diff > 24 * 60 * 60) {
                str = TimeUnit.MILLISECONDS.toDays(difference).toString() + "d valid"
            }

            return Pair(str, delay)
        }


        fun formatTimeStampDefault(qstTime: Long?):String{
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(qstTime)
        }


        fun formatTimeStamp(qstTime: Long?): String {
            if(qstTime==null)return ""
            val difference: Long = System.currentTimeMillis() - qstTime
            var diff = TimeUnit.MILLISECONDS.toSeconds(difference)

            var str = TimeUnit.MILLISECONDS.toSeconds(difference).toString() + "s ago"
            if (diff > 60) {
                str = TimeUnit.MILLISECONDS.toMinutes(difference).toString() + "min ago"
            }
            if (diff > 60 * 60) {
                str = TimeUnit.MILLISECONDS.toHours(difference).toString() + "h ago"
            }
            if (diff > 24 * 60 * 60) {
                str = TimeUnit.MILLISECONDS.toDays(difference).toString() + "d ago"
            }

            return str
        }

        /**
         * return: delay
         */
        fun updateExpirationDate(qst: Question?, textView: TextView): Long {

            qst?.let {
                val timeStamp = formatTimeStamp(qst.timestamp?.toLong())
                val expirationDate = formatExpirationTimeStamp(qst.expirationDate?.toLong())



                qst.let {
                    val s = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(
                            timeStamp + " · <i> " + expirationDate.first + " </i> · ${qst.tags?.name}",
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    } else {
                        timeStamp + " ·  " + expirationDate.first + "· ${qst.tags?.name}"

                    }

                    GlobalScope.launch(Dispatchers.Main) {   textView.text = s}

                }

                return expirationDate.second
            }
            return -1L

        }

        fun formatUserTimestamp(timestamp: String?): CharSequence? {
            return if(timestamp.isNullOrEmpty()){
                "Member since the beginning"
            }else "Member since ${timestamp.substring(0, 4)}"
        }
        fun formatUserLocation(timestamp: String?): CharSequence? {
            return if(timestamp.isNullOrEmpty()){
                "Unknown Location"
            }else "In $timestamp"
        }
        fun formatUserName(timestamp: String?): CharSequence? {
            return if(timestamp.isNullOrEmpty()){
                "Unknown"
            }else "$timestamp"
        }
        fun formatUserFollowers(size:Int): CharSequence? {
            return if(size==0){
                "Following Nothing"
            }else "Following $size Topics/Users"
        }







        fun formatUserAvatar(avatarLink: String?, image: ImageView){
          if(avatarLink.isNullOrBlank()){
              image.setImageResource(R.drawable.baseline_account_circle_24)
              return
          }
            try {
                Picasso.get().load(avatarLink).fit().transform(CropCircleTransformation()).error(R.drawable.baseline_account_circle_24).placeholder(R.drawable.baseline_account_circle_24).into(image)
            } catch (e: Exception) {
                image.setImageResource(R.drawable.baseline_account_circle_24)

            }
        }


    }


}