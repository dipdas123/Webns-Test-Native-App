package com.dip_int.test_webns.screens.multi_selection.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dip_int.test_webns.R
import com.dip_int.test_webns.screens.multi_selection.adapter.DateAdapter

/*** Created By Dipe K Das on: 13th Aug 2024*/

class MultiDateSelectedListActivity : AppCompatActivity() {

    companion object {

    }

    lateinit var listViewDates: ListView
    lateinit var back: ImageView

    override fun onResume() {
        super.onResume()
        println("onResume")

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_multi_date_selected_list)
        listViewDates = findViewById(R.id.listViewDates)
        back = findViewById(R.id.back)

        val formattedDates = intent.getStringArrayListExtra("formattedDates")
        val listView: ListView = findViewById(R.id.listViewDates)
        val adapter = formattedDates?.let { DateAdapter(this, it) }
        listView.adapter = adapter

        clicks()
    }

    private fun clicks() {
        back.setOnClickListener{
            finish()
        }
    }


}