package com.dip_int.test_webns.screens.multi_selection

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dip_int.test_webns.R
import com.dip_int.test_webns.screens.multi_selection.adapter.DateAdapter
import com.prolificinteractive.materialcalendarview.CalendarDay

class MultiDateSelectedListActivity : AppCompatActivity() {

    companion object {

    }

    private val selectedDates = mutableListOf<CalendarDay>()
    lateinit var listViewDates: ListView

    override fun onResume() {
        super.onResume()
        println("onResume")

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_multi_date_selected_list)
        listViewDates = findViewById(R.id.listViewDates)

        val formattedDates = intent.getStringArrayListExtra("formattedDates")
        val listView: ListView = findViewById(R.id.listViewDates)
        val adapter = formattedDates?.let { DateAdapter(this, it) }
        listView.adapter = adapter

        clicks()
    }

    private fun clicks() {

    }


}