package com.project.app.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.project.app.Objects.AutoCompleteItem
import com.project.app.R


class AutoCompleteSearchAdapter(
    val ctxt: Context,
    val layt: Int,
    var allContent: ArrayList<AutoCompleteItem>
) :
    ArrayAdapter<AutoCompleteItem>(ctxt, layt) {

    var content: ArrayList<AutoCompleteItem> = ArrayList()



    override fun getItem(position: Int): AutoCompleteItem {
        return content[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) v = LayoutInflater.from(ctxt).inflate(layt, parent, false)
        //Log.d("getView", "DATA: " + position + "  " + content.size)
        val item: AutoCompleteItem = getItem(position)
        val textView = v?.findViewById<TextView>(R.id.ai_text)
        textView?.text = item.text




        return v!!


    }

    override fun getCount(): Int {
        return content.size
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    fun update(arr: ArrayList<AutoCompleteItem>) {
        this.allContent=arr
       // notifyDataSetChanged()

    }


    inner class SearchFilter : Filter() {
        private val lock = Object()

        override fun performFiltering(prefix: CharSequence?): FilterResults {
            //Log.d("performFiltering", "DATA: " + prefix)
            val results = Filter.FilterResults()


            if (prefix == null || prefix.isEmpty()) {
                synchronized(lock) {
                    results.values = allContent
                    results.count = allContent.size
                }

            } else {
                val searchStrLowerCase = prefix.toString().toLowerCase()

                val matchValues = ArrayList<AutoCompleteItem>()

                for (dataItem in allContent) {
                    if (dataItem.text.toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem)
                    }
                }

                results.values = matchValues
                results.count = matchValues.size


            }

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {
            //Log.d("publishValues", "DATA: $p0  |  ${results?.count}")
            if (results?.values != null) {
                content = results.values as ArrayList<AutoCompleteItem>
            } else {
                content.clear()
            }
            if (content.size > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }

        }

    }
}

