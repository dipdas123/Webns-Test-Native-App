package com.dip_int.test_webns.screens.multi_selection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.dip_int.test_webns.R

class DateAdapter(context: Context, dates: List<String>) : ArrayAdapter<String>(context, 0, dates) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.selected_dates_list_item, parent, false)
        val dateTextView = view.findViewById<TextView>(R.id.textItemsListDate)
        dateTextView.text = getItem(position)
        return view
    }
}
