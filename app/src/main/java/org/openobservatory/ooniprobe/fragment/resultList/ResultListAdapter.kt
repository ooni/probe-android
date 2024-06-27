package org.openobservatory.ooniprobe.fragment.resultList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView

enum class ResultItemType { DEFAULT, RUN_V2_ITEM }

data class ResultListSpinnerItem(@JvmField val id: String, @JvmField val label: String, @JvmField val type: ResultItemType = ResultItemType.DEFAULT)

class ResultListAdapter(private val items: List<ResultListSpinnerItem>) : BaseAdapter(), SpinnerAdapter {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: LayoutInflater.from(parent.context).inflate(android.R.layout.simple_spinner_item, parent, false)
        view.findViewById<TextView>(android.R.id.text1).text = items[position].label
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: LayoutInflater.from(parent.context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        view.findViewById<TextView>(android.R.id.text1).text = items[position].label
        return view
    }
}
