package com.project.app.Adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.app.Helpers.Constants.Companion.FIELD_DESC
import com.project.app.Helpers.Constants.Companion.FIELD_LOCATION
import com.project.app.Helpers.Constants.Companion.FIELD_URL
import com.project.app.Objects.User
import com.project.app.R

class AccountChangeAdapter :RecyclerView.Adapter<AccountChangeAdapter.ViewHolder>() {
    var mRecyclerView: RecyclerView? = null
    var accountChanges = ArrayList<Pair<String,String>>()
    var userAccount:User?=null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        lateinit var vh: ViewHolder
        val v = LayoutInflater.from(p0.context).inflate(R.layout.adapter_account_change, p0, false)
        vh = ViewHolder(v)

        return vh


    }

    override fun getItemCount(): Int {
        return 3
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
       viewHolder.clear.setOnClickListener { viewHolder.edit.text.clear() }

        when(position){
            0->{
            viewHolder.icon.setImageResource(R.drawable.baseline_location_city_24)
            viewHolder.name.text = "Location"
                viewHolder.edit.setText(userAccount?.location)
                viewHolder.edit.hint="Enter your location"

            }
            1->{
                viewHolder.icon.setImageResource(R.drawable.baseline_description_24)
                viewHolder.name.text = "User Infos"

                viewHolder.edit.setText(userAccount?.desc)
                viewHolder.edit.hint="Enter informations about you"


            }
            2->{
                viewHolder.icon.setImageResource(R.drawable.baseline_link_24)
                viewHolder.name.text = "Link"
                viewHolder.edit.setText(userAccount?.url)
                viewHolder.edit.hint="Enter your website"


            }
        }
        viewHolder.edit.tag=position
        viewHolder.edit.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

                reportChange(p0.toString(),viewHolder.adapterPosition )
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })



    }

    fun reportChange(change:String, fi:Int){
        var field=FIELD_LOCATION
        when(fi){
            1->{
                field=FIELD_DESC

            }
            2->{
                field=FIELD_URL
            }
        }

        val pair= Pair<String,String>(field,change)
        accountChanges.find { pair.first==it.first }?.let {
            accountChanges.remove(it)
        }

        accountChanges.add(pair)
    }

    fun getChanges(): ArrayList<Pair<String, String>> {
        return accountChanges
    }

    fun setUser(im: User) {
        userAccount=im
        Log.e("setUser","AccountChangeAdapter: ${im.location}")
        notifyDataSetChanged()
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView=v.findViewById(R.id.panel_search_back)
        val clear: ImageView=v.findViewById(R.id.panel_search_clear)
        val edit: EditText =v.findViewById(R.id.panel_search_edit)
        val name: TextView=v.findViewById(R.id.panel_name)

    }
}